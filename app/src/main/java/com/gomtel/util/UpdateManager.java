package com.gomtel.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mtk.btnotification.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UpdateManager {
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private static final int DOWNLOAD_XML_FINISH = 3;
    private static final String TAG = "UpdateManager";
    private final String lon;
    private final String lat;

    private int progress;


    private Context mContext;
    /* 鏇存柊杩涘害鏉� */
    private ProgressBar mProgress;
    private TextView update_text;
    private Dialog mDownloadDialog;
    private static final int BUFFER_SIZE = 1024;
    public final static String SHARE_LAST_UPDATE = "share_last_update";

    public final long interval_hours = 24;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD:
                    mProgress.setProgress(progress);
                    update_text.setText(progress + "%");
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;

                case DOWNLOAD_XML_FINISH:
//				checkUpdate();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private BasicHttpParams httpParams;
    private DefaultHttpClient httpClient;
    private String mUserAgent;
    private long length;
    private boolean cancelUpdate;

    public UpdateManager(Context context, String lon, String lat) {
        this.mContext = context;
        this.lon = lon;
        this.lat = lat;
    }

    public void showNoticeDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });

        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
        noticeDialog.setCanceledOnTouchOutside(false);
    }

    private void showDownloadDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        update_text = (TextView) v.findViewById(R.id.update_num);
        builder.setView(v);
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//				cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        mDownloadDialog.setCanceledOnTouchOutside(false);
        Log.e(TAG, "downloadApk ");
        downloadApk();
    }

    private void downloadApk() {
        getHttpClient(mContext);
        new downloadApkThread().start();
    }

    private HttpClient getHttpClient(Context c) {
        this.httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        HttpClientParams.setRedirecting(httpParams, true);
        String userAgent = getUserAgent(c);// "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        HttpProtocolParams.setUserAgent(httpParams, userAgent);
        httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }

    private String getUserAgent(Context c) {
        if (mUserAgent == null) {
            WebView webview = new WebView(c);
            WebSettings settings = webview.getSettings();
            mUserAgent = settings.getUserAgentString();
        }
        return mUserAgent;
    }

    private class downloadApkThread extends Thread {
        @Override
        public void run() {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("bg_proj", "3070");
                jsonObject.put("lon", lon);
                jsonObject.put("lat", lat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpPost httpRequest = new HttpPost(Global.URL_DOWNLOAD_SOFTWARE);
            InputStream inputStream = null;
            try {
                httpRequest.setEntity(new StringEntity(jsonObject.toString(), "utf-8"));
                HttpResponse httpResponse = httpClient.execute(httpRequest);
                LogUtil.e(TAG,"httpResponse= "+httpResponse.getHeaders("Content-Disposition").toString());
                Log.d("", "lixiang getStatusCode() = "
                        + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
//                file_name = httpResponse.getHeaders("filename");
                    inputStream = entity.getContent();
                    length = entity.getContentLength();

                    LogUtil.e(TAG,"entity.getContentType()= "+entity.getContentType());
                    new File(Global.DOWNLOAD_PATH).mkdirs();
                    inputstreamToFile(inputStream, (new StringBuilder(String.valueOf(Global.DOWNLOAD_PATH))).append("WATCH.apk").toString());
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mDownloadDialog.dismiss();
        }
    }

    ;

    private boolean inputstreamToFile(InputStream inStream, String fileName) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        FileOutputStream fileOutStream = null;
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            long count = 0;
            while (true) {
//				int numread = inStream.read(buffer);
                len = inStream.read(buffer);
                count += len;
                progress = (int) (((float) count / length) * 100);
                if (progress > 100) {
                    progress = 100;
                }
                // 鏇存柊杩涘害
                mHandler.sendEmptyMessage(DOWNLOAD);
                if (len <= 0) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                    break;
                }
                outStream.write(buffer, 0, len);
            }

            byte[] data = outStream.toByteArray();
            File imageFile = new File(fileName);
            fileOutStream = new FileOutputStream(imageFile);
            fileOutStream.write(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.e(TAG, "inputstreamToImage");
            e.printStackTrace();
        } finally {

            try {
                if (inStream != null) {
                    inStream.close();
                    inStream = null;
                }
                if (outStream != null) {
                    outStream.close();
                    outStream = null;
                }
                if (fileOutStream != null) {
                    fileOutStream.close();
                    fileOutStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return true;
        }
    }


    private void installApk() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        sp.edit()
                .putString(SHARE_LAST_UPDATE,
                        getCurrentTimeString()).commit();
        File apkfile = new File(Global.DOWNLOAD_PATH, "WATCH.apk");
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    private static int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }

    public String getCurrentTimeString() {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        time = sdf.format(new Date());
        return time;
    }
}
