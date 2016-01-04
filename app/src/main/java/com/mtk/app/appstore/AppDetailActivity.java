
package com.mtk.app.appstore;

import com.mediatek.wearable.VxpInstallController;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.appstore.AppStoreActivity.ViewHolder;
import com.mtk.btnotification.R;
import android.util.Log;
import com.mtk.main.MainService;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Handler;
import android.graphics.drawable.ClipDrawable;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class AppDetailActivity extends Activity implements RemoteAppInfo.AppInfoListener {

    private ImageView mAppIcon;

    private TextView mAppName;

    private Button mInstallButton;

    private TextView mAppSummary;

    private TextView mAppSummaryBody;

    private TextView mIntro;

    private TextView mAppIntro;

    private ImageView mAppSmapleImage;

    public static AppDetailActivity instance = null;

    private int count = 0;

    private RemoteAppInfo mAppInfo;

    private Toast mToast = null;

    private Handler mHandler = new Handler();

    private static final String TAG = "AppManager/AppDetail";

    public static boolean isActivityResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appstore_item_detail);
        Intent intent = getIntent();
        if (intent == null) {
            this.finish();
            return;
        }
        int position = intent.getIntExtra("position", 0);
        mAppInfo = AppStoreManager.getInstance().getAppInfo(position);
        if (mAppInfo == null) {
            this.finish();
            return;
        }
        if (instance != null) {
            instance.finish();
        }
        instance = this;
        RemoteAppInfo.addListener(this);
        mToast = Toast.makeText(AppDetailActivity.this, R.string.no_connect, 1);
        AppStoreManager.getInstance().refreshAppDetail(mAppInfo);
        initResource();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityResumed = false;
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
        RemoteAppInfo.removeListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initResource() {
        mAppIcon = (ImageView) findViewById(R.id.app_icon);
        mAppName = (TextView) findViewById(R.id.app_name);
        mInstallButton = (Button) findViewById(R.id.install);
        mAppSummary = (TextView) findViewById(R.id.app_summary_title);
        StringBuilder summary = new StringBuilder();
        summary.append("Provider   \n");
        summary.append("Version   \n");
        summary.append("Release date   \n");
        summary.append("App size   ");
        mAppSummary.setText(summary.toString());

        mAppSummaryBody = (TextView) findViewById(R.id.app_summary_body);
        mIntro = (TextView) findViewById(R.id.intro);
        mIntro.setText("Introduction");
        mAppIntro = (TextView) findViewById(R.id.app_intro);
        mAppSmapleImage = (ImageView) findViewById(R.id.app_sample_image);
    }

    private void refreshView() {
        String iconPath = mAppInfo.getIconPath();
        byte[] imageBuffer = null;
        if (iconPath != null) {
            File file = new File(iconPath);
            try {
                FileInputStream fis = new FileInputStream(file);
                imageBuffer = new byte[fis.available()];
                fis.read(imageBuffer);
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (imageBuffer != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length);
            mAppIcon.setImageBitmap(bm);
        } else {
            mAppIcon.setImageResource(R.drawable.setting_icon1);
        }
        mAppName.setText(mAppInfo.getAppName());

        refreshAppStatus(mAppInfo);

        mInstallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WearableManager.getInstance().isAvailable()) {
                    mToast.show();
                    return;
                }
                if (isFastDoubleClick()) {
                    return;
                }
                int status = mAppInfo.getAppStatus();
                switch (status) {
                    case RemoteAppInfo.RemoteAppStatus.NEED_UPDATE:
                        if (AppStoreManager.getInstance().getDownloadingCount() >= 5) {
                            Toast.makeText(AppDetailActivity.this,
                                    R.string.reachmax, 1).show();
                        } else {
                            AppStoreManager.getInstance().updateApp(mAppInfo);
                        }
                        break;
                    case RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL:
                    case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_FAILED:
                        if (AppStoreManager.getInstance().getDownloadingCount() >= 5) {
                            Toast.makeText(AppDetailActivity.this,
                                    R.string.reachmax, 1).show();
                        } else {
                            AppStoreManager.getInstance().downloadApp(mAppInfo);
                        }
                        break;
                    case RemoteAppInfo.RemoteAppStatus.INSTALL_FAILED:
                    case RemoteAppInfo.RemoteAppStatus.NEED_INSTALL:
                        mAppInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALLING);
                        InstallManager.getInstance().sendInstallData(mAppInfo);
                        break;
                    case RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL:
                        showUninstallPrompt();
                        break;
                    case RemoteAppInfo.RemoteAppStatus.DOWNLOADING:
                    case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_SUCCESSFUL:
                    case RemoteAppInfo.RemoteAppStatus.INSTALLING:
                    default:
                        break;
                }
            }
        });
        StringBuilder summary = new StringBuilder();
        summary.append(mAppInfo.getProvider() + "\n");
        summary.append(mAppInfo.getVersion() + "\n");
        summary.append(mAppInfo.getReleaseDate() + "\n");
        summary.append(mAppInfo.getAppSize());
        mAppSummaryBody.setText(summary.toString());

        mAppIntro.setText(mAppInfo.getIntroduction());

        byte[] sampleImageBuffer = null;
        if (mAppInfo.getSamplePath() != null) {
            File file = new File(mAppInfo.getSamplePath());
            try {
                FileInputStream fis = new FileInputStream(file);
                sampleImageBuffer = new byte[fis.available()];
                fis.read(sampleImageBuffer);
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sampleImageBuffer != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(sampleImageBuffer, 0,
                    sampleImageBuffer.length);
            mAppSmapleImage.setImageBitmap(bm);
        } else {
            mAppSmapleImage.setImageResource(R.drawable.default_sample);
        }
    }

    @Override
    public void onAppInfoChanged(final RemoteAppInfo appInfo) {
        Log.i(TAG,
                "AppDetailActivity#onAppInfoChanged, appInfo.getRecieverID() = "
                        + appInfo.getReceiverID());
        if (appInfo.getReceiverID().equals(mAppInfo.getReceiverID())) {
            mAppInfo = appInfo;
            mHandler.post(new Runnable() {
                public void run() {
                    String iconPath = appInfo.getIconPath();
                    byte[] imageBuffer = null;
                    if (iconPath != null) {
                        File file = new File(iconPath);
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            imageBuffer = new byte[fis.available()];
                            fis.read(imageBuffer);
                            fis.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (imageBuffer != null) {
                        Bitmap bm = BitmapFactory.decodeByteArray(imageBuffer, 0,
                                imageBuffer.length);
                        mAppIcon.setImageBitmap(bm);
                    } else {
                        mAppIcon.setImageResource(R.drawable.setting_icon1);
                    }

                    byte[] sampleImageBuffer = null;
                    if (appInfo.getSamplePath() != null) {
                        File file = new File(appInfo.getSamplePath());
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            sampleImageBuffer = new byte[fis.available()];
                            fis.read(sampleImageBuffer);
                            fis.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sampleImageBuffer != null) {
                        Bitmap bm = BitmapFactory.decodeByteArray(sampleImageBuffer, 0,
                                sampleImageBuffer.length);
                        mAppSmapleImage.setImageBitmap(bm);
                    } else {
                        mAppSmapleImage.setImageResource(R.drawable.default_sample);
                    }
                }
            });
        }
    }

    public void onAppInfoUpdated(RemoteAppInfo appInfo) {
    }

    public void onAppStatusChanged(final RemoteAppInfo appInfo) {
        Log.i(TAG,
                "AppDetailActivity#onAppStatusChanged, appInfo.getRecieverID() = "
                        + appInfo.getReceiverID());
        if (appInfo.getReceiverID().equals(mAppInfo.getReceiverID())) {
            mAppInfo = appInfo;
            mHandler.post(new Runnable() {
                public void run() {
                    refreshAppStatus(appInfo);
                }
            });
        }
    }

    // add for refresh download/install view.
    private void refreshAppStatus(RemoteAppInfo appInfo) {
        int status = appInfo.getAppStatus();
        Log.i(TAG, "AppDetailActivity#refreshAppStatus, status = " + status);
        switch (status) {
            case RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL:
            case RemoteAppInfo.RemoteAppStatus.NEED_INSTALL:
                mInstallButton.setBackgroundResource(R.drawable.bt_store_install);
                mInstallButton.setText(R.string.install);
                mInstallButton.setEnabled(true);
                break;
            case RemoteAppInfo.RemoteAppStatus.DOWNLOADING:
                mInstallButton.setBackgroundResource(R.drawable.bt_store_downloading);
                mInstallButton.setText(R.string.downloading_hint);
                mInstallButton.setEnabled(false);
                break;
            case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_SUCCESSFUL:
                appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALLING);
                InstallManager.getInstance().sendInstallData(appInfo);
                break;
            case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_FAILED:
                Toast.makeText(AppDetailActivity.this, R.string.download_fail, 1).show();
                appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
                break;
            case RemoteAppInfo.RemoteAppStatus.INSTALLING:
                mInstallButton.setBackgroundResource(R.drawable.bt_store_downloading);
                mInstallButton.setText(R.string.installing_hint);
                mInstallButton.setEnabled(false);
                break;
            case RemoteAppInfo.RemoteAppStatus.INSTALL_FAILED:
                appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
                break;
            case RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL:
                mInstallButton.setBackgroundResource(R.drawable.bt_store_uninstall);
                mInstallButton.setText(R.string.uninstall);
                mInstallButton.setEnabled(true);
                break;
            case RemoteAppInfo.RemoteAppStatus.NEED_UPDATE:
                mInstallButton.setBackgroundResource(R.drawable.bt_store_update);
                mInstallButton.setText(R.string.update);
                mInstallButton.setEnabled(true);
            default:
                break;
        }
    }

    public void onDownloadError(final String error) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(AppDetailActivity.this, error, 1).show();
            }
        });
    }

    private void showUninstallPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.uninstall);
        builder.setMessage(R.string.uninstall_content);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mAppInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL) {
                    return;
                }
                InstallManager.getInstance().sendUnInstallData(mAppInfo);
            }
        });
        builder.create().show();
    }

    private long mLastClickTime = System.currentTimeMillis();;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        Log.i(TAG, "isFastDoubleClick, clicked time = " + time + ", mLastClickTime = "
                + mLastClickTime);
        long slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;
    }

}
