package com.mtk.app.fota;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

//import com.mtk.app.fota.gmobi.GmFotaService;
//import com.mtk.app.fota.gmobi.GmFotaService.IGmFotaCallBack;

import com.gmobi.fota.GmFotaService;
import com.gmobi.fota.GmFotaService.IGmFotaCallBack;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NewVersionChecker {

    private static final String TAG = "[FOTA_UPDATE][NewVersionChecker]";

    public static final int NEW_VERSION_CHECKER_GMOBI = 100;
    public static final int NEW_VERSION_CHECKER_REDBEND = 101;
    public static final int NEW_VERSION_CHECKER_MTK_SERVER_UBIN = 102;
    public static final int NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN = 103;

    private static final String URL_PLATFORM_STRING = "platform=";
    private static final String URL_MODULE_STRING = "model=";
    private static final String URL_TYPE_STRING = "type=";
    private static final String URL_VERSION_STRING = "version=";
    private static final String URL_DOWNLOAD_KEY_STRING = "key=";
    private static final String URL_QUERY_ADD_STRING = "&";
    private static final String URL_QUERY_QUES_STRING = "?";

    private static final String USB_FULL_BIN_URL_QUERY_STRING = "USB";
    private static final String UBIN_URL_QUERY_STRING = "UBIN";

    private static final int CONNECT_TIMEOUT_MESSAGE = 100;
    private static final int CONNECT_TIMEOUT = 60 * 1000 * 2;

//    private static final String MTK_SERVER_HEADER = "https://iotlab.mediatek.com/fota/general/1.0/download";

    private static final String HTTP_HEADER = "http://";

    private static String sMTKDownloadUrl = null;

    private static boolean sErrorHandled = false;

    public interface INewVersionCheckerCallback {
        void onSuccessed(String newVersion, String newReleaseNote);

        void onProgress();

        void onNetworkError();

        void onSystemError();

        void onNewVersionExisted();
    }

    public static void startCheckNewVersion(Context context, int which,
            String platform, String moduleString, String currentVersion,
            String brand, String domain, String pincode,
            String devId, String downloadKey, INewVersionCheckerCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException(
                    "[startCheckNewVersion] CALLBACK IS NULL !!");
        }
        if (context == null) {
            Log.e(TAG, "[startCheckNewVersion] context is null");
            callback.onSystemError();
            return;
        }
        if (which < 100) {
            Log.e(TAG, "[startCheckNewVersion] which is below 100, wrong");
            callback.onSystemError();
            return;
        }
        
        if (domain == null || domain.length() == 0) {
            Log.e(TAG, "[startCheckNewVersion] domain is wrong");
            callback.onSystemError();
            return;
        }
        
        switch (which) {
        case NEW_VERSION_CHECKER_GMOBI:
            Log.d(TAG, "[startCheckNewVersion] NEW_VERSION_CHECKER_GMOBI SWITCH");
            // startCheckNewVersionFromGmobi(moduleString, currentVersion,
            // callback);
            break;

        case NEW_VERSION_CHECKER_REDBEND:
            Log.d(TAG, "[startCheckNewVersion] NEW_VERSION_CHECKER_REDBEND SWITCH");
            startCheckNewVersionFromRedbend(context, devId, moduleString,
                    currentVersion, brand, domain, pincode, callback);
            break;

        case NEW_VERSION_CHECKER_MTK_SERVER_UBIN:
            
            if (downloadKey == null || downloadKey.trim().length() == 0) {
                Log.d(TAG, "[startCheckNewVersion] downloadKey is null or EMPTY");
                callback.onSystemError();
                return;
            }
            
            Log.d(TAG,
                    "[startCheckNewVersion] NEW_VERSION_CHECKER_MTK_SERVER_UBIN SWITCH");
            String strUrl = buildUrlQueryString(domain, platform, moduleString,
                    NEW_VERSION_CHECKER_MTK_SERVER_UBIN, currentVersion, downloadKey);
            startCheckNewVersionFromMtkServer(strUrl, callback);
            break;

        case NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN:
            
            if (downloadKey == null || downloadKey.trim().length() == 0) {
                Log.d(TAG, "[startCheckNewVersion] downloadKey is null or EMPTY");
                callback.onSystemError();
                return;
            }
            
            Log.d(TAG,
                    "[startCheckNewVersion] NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN SWITCH");
            String strUrl1 = buildUrlQueryString(domain, platform, moduleString,
                    NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN, currentVersion, downloadKey);
            startCheckNewVersionFromMtkServer(strUrl1, callback);
            break;

        default:
            Log.e(TAG, "[startCheckNewVersion] unrecognized which");
            callback.onSystemError();
            break;
        }
    }

    public static void downloadNewVersion(Context context, int which,
            File file, INewVersionCheckerCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("[downloadNewVersion] CALLBACK IS NULL !!");
        }
        if (context == null) {
            Log.e(TAG, "[downloadNewVersion] context is null");
            callback.onSystemError();
            return;
        }
        if (which < 100) {
            Log.e(TAG, "[downloadNewVersion] which is below 100, wrong");
            callback.onSystemError();
            return;
        }
        switch (which) {
        case NEW_VERSION_CHECKER_GMOBI:
            Log.d(TAG, "[downloadNewVersion] SWITCH NEW_VERSION_CHECKER_GMOBI");
            // downloadCheckNewVersionFromGmobi(file, callback);
            break;

        case NEW_VERSION_CHECKER_REDBEND:
            Log.d(TAG, "[downloadNewVersion] SWITCH NEW_VERSION_CHECKER_REDBEND");
            downloadCheckNewVersionFromRedbend(context, file, callback);
            break;

        case NEW_VERSION_CHECKER_MTK_SERVER_UBIN:
            Log.d(TAG, "[downloadNewVersion] SWITCH NEW_VERSION_CHECKER_MTK_SERVER_UBIN");
            downloadCheckNewVersionFromMtkServer(file, callback);
            break;

        case NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN:
            Log.d(TAG, "[downloadNewVersion] SWITCH NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN");
            downloadCheckNewVersionFromMtkServer(file, callback);
            break;

        default:
            Log.d(TAG, "[downloadNewVersion] SWITCH unknown id");
            callback.onSystemError();
            break;
        }
    }

    private static void startCheckNewVersionFromRedbend(final Context context,
            final String devId, final String module, final String curVersion,
            final String brand, final String domain, final String pincode,
            final INewVersionCheckerCallback callback) {
        if (module == null || module.length() == 0) {
            Log.e(TAG, "[startCheckNewVersionFromGmobi] module str is null or empty");
            callback.onSystemError();
            return;
        }
        if (curVersion == null || curVersion.length() == 0) {
            Log.e(TAG, "[startCheckNewVersionFromGmobi] curVersion str is null or empty");
            callback.onSystemError();
            return;
        }
        if (devId == null || devId.length() == 0) {
            Log.e(TAG, "[startCheckNewVersionFromGmobi] devId str is null or empty");
            callback.onSystemError();
            return;
        }
        if ((domain == null || domain.length() == 0)
                || (pincode == null || pincode.length() == 0)) {
            Log.e(TAG, "[startCheckNewVersionFromGmobi] domain or pincode is null or empty");
            callback.onSystemError();
            return;
        }
        
        if (brand == null || brand.length() == 0) {
            Log.e(TAG, "[startCheckNewVersionFromGmobi] brand is null or empty");
            callback.onSystemError();
            return;
        }
        
        GmFotaService.useTestServer = false;
        Log.d(TAG, "[startCheckNewVersionFromRedbend] devId : " + devId);
        Log.d(TAG, "[startCheckNewVersionFromRedbend] module : " + module);
        Log.d(TAG, "[startCheckNewVersionFromRedbend] curVersion : "
                + curVersion);
        GmFotaService.initGmFota(context, devId, brand, module, curVersion);
        GmFotaService.regiesterDomain(context, domain, pincode);

        GmFotaService.startCheckFw(new IGmFotaCallBack() {

            @Override
            public void onDebug(String arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onDebug arg0 : "
                        + arg0);
            }

            @Override
            public void onDownloading(int arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onDownloading arg0 : "
                                + arg0);
            }

            @Override
            public void onNetError() {
                // TODO Auto-generated method stub
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onNetError enter");
                callback.onNetworkError();
            }

            @Override
            public void onSuccess(GmFotaService arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onSuccess enter");
                if (arg0 == null) {
                    Log.d(TAG, "[startCheckNewVersionFromRedbend] onSuccess arg0 is null, new version exsited");
                    callback.onNewVersionExisted();
                    return;
                }
                String version = arg0.dlVer;
                String rela = arg0.dlRnUri;
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onSuccess newVersion : "
                                + version + ", rela : " + rela);
                callback.onSuccessed(version, rela);
                return;
            }

            @Override
            public void onSysError() {
                // TODO Auto-generated method stub
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onSysError enter");
                callback.onSystemError();
            }

            @Override
            public void onProgress() {
                // TODO Auto-generated method stub
                Log.d(TAG, "[startCheckNewVersionFromRedbend] onProgress enter");
                callback.onProgress();
            }

        });
    }

    private static void startCheckNewVersionFromMtkServer(
            final String stringUrl, final INewVersionCheckerCallback callback) {
        if (stringUrl == null || stringUrl.length() == 0) {
            callback.onSystemError();
            return;
        }

        sMTKDownloadUrl = null;
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "[startCheckNewVersionFromMtkServer] runnable begin to run");
                InputStream isUrl = HttpHelper.getInputStreamFromURL(stringUrl,
                        new HttpHelper.IExceptionHandler() {

                            @Override
                            public void onNetworkError() {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "[startCheckNewVersionFromMtkServer] [onNetworkError] enter");
                                sErrorHandled = true;
                                callback.onNetworkError();
                            }

                            @Override
                            public void onDataError() {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "[startCheckNewVersionFromMtkServer] [onDataError] enter");
                                sErrorHandled = true;
                                callback.onSystemError();
                            }
                        }, mHandler);

                if (isUrl == null) {
                    if (!sErrorHandled) {
                        Log.e(TAG, "[startCheckNewVersionFromMtkServer] isUrl is null");
                        callback.onNewVersionExisted();
                    }
                    return;
                }
                sErrorHandled = false;
                HashMap<String, String> jsonMap = HttpHelper
                        .getJSonFromInputStream(isUrl);

                if (jsonMap == null) {
                    Log.d(TAG, "[startCheckNewVersionFromMtkServer] jasonMap is null");
                    callback.onSystemError();
                    return;
                }
                
                String strUrl = jsonMap.get(HttpHelper.JSON_URL_STRING);
                String version = jsonMap.get(HttpHelper.JSON_VERSION_STRING);
                String releaseNote = jsonMap.get(HttpHelper.JSON_RELEASE_DATE_STRING);

                Log.d(TAG, "[startCheckNewVersionFromMtkServer] strUrl is : "
                        + strUrl);
                Log.d(TAG, "[startCheckNewVersionFromMtkServer] version is : "
                        + version);
                Log.d(TAG, "[startCheckNewVersionFromMtkServer] releaseNote is : "
                                + releaseNote);
                if (strUrl == null || strUrl.length() == 0) {
                    Log.e(TAG, "[startCheckNewVersionFromMtkServer] strUrl length is 0");
                    callback.onNewVersionExisted();
                    return;
                }
                sMTKDownloadUrl = strUrl;
                callback.onSuccessed(version, releaseNote);
            }
        };
        new Thread(r).start();
    }


    private static void downloadCheckNewVersionFromRedbend(
            final Context context, final File file,
            final INewVersionCheckerCallback callback) {
        GmFotaService.upgradePkgDownload(new IGmFotaCallBack() {
            @Override
            public void onDebug(String arg0) {
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] onDebug arg0 : "
                                + arg0);
            }

            @Override
            public void onDownloading(int arg0) {
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] onDownloading arg0 : "
                                + arg0);
            }

            @Override
            public void onNetError() {
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] onNetError enter");
                callback.onNetworkError();
            }

            @Override
            public void onSuccess(GmFotaService arg0) {
                if (arg0 == null) {
                    Log.e(TAG, "[downloadCheckNewVersionFromRedbend] onSuccess return service is null");
                    callback.onNewVersionExisted();
                    return;
                }
                String newVersion = arg0.dlVer;
                String newReleaseNote = arg0.dlRnUri;
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] onSuccess newVersion : "
                                + newVersion);
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] onSuccess newReleaseNote : "
                                + newReleaseNote);

                File versionFile = arg0.dlDpFile;
                if (!versionFile.isFile()) {
                    Log.d(TAG, "[downloadCheckNewVersionFromRedbend] versionFile is not a file");
                    callback.onSystemError();
                    // GmFotaService.updateFinished(false);
                    // FotaUtils
                    return;
                }
                if (file.isFile()) {
                    File f = new File(file.getAbsolutePath());
                    InputStream in = null;
                    FileOutputStream out = null;
                    try {
                        in = new FileInputStream(versionFile);
                        out = new FileOutputStream(f);
                        byte[] bytes = new byte[5 * 1024];
                        int c = 0;
                        while ((c = in.read(bytes)) > -1) {
                            out.write(bytes, 0, c);
                            out.flush();
                        }
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "[downloadCheckNewVersionFromRedbend] FileNotFoundException happened");
                        callback.onSystemError();
                        // GmFotaService.updateFinished(false);
                        return;
                    } catch (IOException e) {
                        Log.d(TAG, "[downloadCheckNewVersionFromRedbend] IOException 11 happened");
                        callback.onSystemError();
                        // GmFotaService.updateFinished(false);
                        return;
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] IOException 22 happened");
                                callback.onSystemError();
                                return;
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] IOException 33 happened");
                                callback.onSystemError();
                                return;
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "[downloadCheckNewVersionFromRedbend] file is not a file");
                    callback.onSystemError();
                    return;
                }
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] download & generate file succeed");
                FotaUtils.updateFinished(context, true);
                callback.onSuccessed(newVersion, newReleaseNote);
            }

            @Override
            public void onSysError() {
                // TODO Auto-generated method stub
                Log.e(TAG, "[downloadCheckNewVersionFromRedbend] onSysError enter");
                callback.onSystemError();
                // GmFotaService.updateFinished(false);
                // FotaUtils.updateFinishedCalled(context, false);
            }

            @Override
            public void onProgress() {
                // TODO Auto-generated method stub
                Log.d(TAG, "[downloadCheckNewVersionFromRedbend] onProgress enter");
                callback.onProgress();
            }

        });
    }

    private static void downloadCheckNewVersionFromMtkServer(final File file,
            final INewVersionCheckerCallback callback) {
        if (file == null) {
            throw new IllegalArgumentException("[downloadCheckNewVersionFromMtkServer] file is null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("[downloadCheckNewVersionFromMtkServer] callback is null");
        }
        if (sMTKDownloadUrl == null || sMTKDownloadUrl.length() == 0) {
            Log.e(TAG, "[downloadCheckNewVersionFromMtkServer] stringUrl is null or empty");
            callback.onSystemError();
            return;
        }
        Runnable r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String url = HTTP_HEADER + sMTKDownloadUrl;
                Log.d(TAG, "[downloadCheckNewVersionFromMtkServer] strUrl is : "
                                + url);
                InputStream fileStream = HttpHelper.getInputStreamFromURL(url,
                        new HttpHelper.IExceptionHandler() {

                            @Override
                            public void onNetworkError() {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "[downloadCheckNewVersionFromMtkServer] onNetworkError enter");
                                sErrorHandled = true;
                                callback.onNetworkError();
                            }

                            @Override
                            public void onDataError() {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "[downloadCheckNewVersionFromMtkServer] onDataError enter");
                                sErrorHandled = true;
                                callback.onSystemError();
                            }
                        }, mHandler);
                if (fileStream == null) {
                    if (!sErrorHandled) {
                        Log.e(TAG, "[downloadCheckNewVersionFromMtkServer] fileStream is null");
                        callback.onSystemError();
                    }
                    return;
                }
                sErrorHandled = false;
                try {
                    OutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[5 * 1024];
                    int length = 0;
                    while ((length = fileStream.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                    }
                    output.close();
                    callback.onSuccessed("newVersion", "newReleaseNote");
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG, "[downloadCheckNewVersionFromMtkServer] FileNotFoundException happened!");
                    e.printStackTrace();
                    callback.onSystemError();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG, "[downloadCheckNewVersionFromMtkServer] IOException happened!");
                    e.printStackTrace();
                    callback.onSystemError();
                } finally {
                    try {
                        if (fileStream != null) {
                            fileStream.close();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sMTKDownloadUrl = null;
                }
            }

        };
        new Thread(r).start();
    }

    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case HttpHelper.URL_CONNECT_TIMEOUT:
                Log.d(TAG, "[handleMessage] HttpHelper.URL_CONNECT_TIMEOUT");
                HttpURLConnection conn = (HttpURLConnection) msg.obj;
                if (conn != null) {
                    Log.d(TAG, "[handleMessage] handle disconnect action");
                    conn.disconnect();
                }
                break;

            case CONNECT_TIMEOUT_MESSAGE:
                Log.d(TAG, "[handleMessage] CONNECT_TIMEOUT");
                INewVersionCheckerCallback callback = (INewVersionCheckerCallback) msg.obj;
                callback.onNetworkError();
                break;

            default:
                break;
            }
        }
    };

    private static String buildUrlQueryString(String domain, String platform, String module,
            int type, String version, String downloadKey) {
        if (version == null) {
            return null;
        }
        Log.d(TAG, "[buildUrlQueryString] domain : " + domain);
        Log.d(TAG, "[buildUrlQueryString] platform : " + platform);
        Log.d(TAG, "[buildUrlQueryString] module : " + module);
        Log.d(TAG, "[buildUrlQueryString] type : " + type);
        Log.d(TAG, "[buildUrlQueryString] version : " + version);

        String typeStr = null;
        String modelStr = null;
        StringBuilder sb = new StringBuilder();
        sb.append(domain + URL_QUERY_QUES_STRING);
        sb.append(URL_PLATFORM_STRING + platform + URL_QUERY_ADD_STRING);
        if (module == null || module.length() == 0) {
            modelStr = "TEST";
        } else {
            modelStr = module;
        }
        sb.append(URL_MODULE_STRING + modelStr + URL_QUERY_ADD_STRING);
        if (type == NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN) {
            typeStr = USB_FULL_BIN_URL_QUERY_STRING;
        } else if (type == NEW_VERSION_CHECKER_MTK_SERVER_UBIN) {
            typeStr = UBIN_URL_QUERY_STRING;
        }
        Log.d(TAG, "[buildUrlQueryString] typeStr : " + typeStr);
        sb.append(URL_TYPE_STRING + typeStr + URL_QUERY_ADD_STRING);
        sb.append(URL_VERSION_STRING + version + URL_QUERY_ADD_STRING);
        sb.append(URL_DOWNLOAD_KEY_STRING + downloadKey);
        Log.d(TAG, "[buildUrlQueryString] URL QUERY STRING : " + sb.toString());
        return sb.toString();
    }
}
