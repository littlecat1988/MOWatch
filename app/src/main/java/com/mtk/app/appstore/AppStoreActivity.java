
package com.mtk.app.appstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import com.mediatek.wearable.VxpInstallController;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.applist.AppInfo;
import com.mtk.app.applist.AppManager;
import com.mtk.app.appstore.AppStoreManager;
import com.mtk.app.appstore.RemoteAppInfo;
import com.mtk.app.appstore.RemoteAppInfo.AppInfoListener;
import com.mtk.app.appstore.RemoteAppInfo.RemoteAppStatus;
import com.mtk.main.BTNotificationApplication;
import com.mtk.main.MainService;
import com.mtk.btnotification.R;
import android.util.Log;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.ClipDrawable;

public class AppStoreActivity extends ListActivity implements RemoteAppInfo.AppInfoListener {

    private ListAddLayoutAdapter mAdapter;

    private LayoutInflater mInflater;

    private AppStoreManager mAppStoreManager;

    private Toast mToast = null;

    private int mCurrId;

    public static RemoteAppInfo mCurrApp = null;

    public static AppStoreActivity instance = null;

    private ProgressDialog mProgressDialog;

    private ProgressDialog mLoadingDialog;

    private Handler mHandler = new Handler();

    private static final String TAG = "AppManager/AppStore";

    public static boolean isActivityResumed = false;

    // Track which TextView's show which Contact objects so that we can update
    // appropriately when the Contact gets fully loaded.
    private HashMap<RemoteAppInfo, ViewHolder> mAppViewMap = new HashMap<RemoteAppInfo, ViewHolder>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate, this = " + this + ", instance = " + instance);
        if (instance != null) {
            instance.finish();
        }
        instance = this;
        mAppStoreManager = AppStoreManager.getInstance();
        mAdapter = new ListAddLayoutAdapter(this);
        this.setListAdapter(mAdapter);

        mToast = Toast.makeText(AppStoreActivity.this, R.string.no_connect, 1);

        RemoteAppInfo.addListener(this);

        VxpInstallController.addListener(InstallManager.getInstance());

        mLoadingDialog = new ProgressDialog(AppStoreActivity.this);
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setMessage(getText(R.string.progress_dialog_title));
        AppStoreActivity.this.getListView().setVisibility(View.GONE);
        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
                // activate spinner after half a second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.show();
                        }
                    }
                }, 500);
            }

            protected Void doInBackground(Void... none) {
                // / [method] refresh AppManager
                for (int i = 0; i < mAppStoreManager.getApplength(); i++) {
                    RemoteAppInfo appInfo = mAppStoreManager.getAppInfo(i);
                    if (appInfo.getAppStatus() == RemoteAppInfo.RemoteAppStatus.INSTALLING
                            || appInfo.getAppStatus() == RemoteAppInfo.RemoteAppStatus.DOWNLOADING) {
                        return null;
                    }
                }
                mAppStoreManager.refreshAppInfo(getApplicationContext());
                return null;
            }

            protected void onPostExecute(Void result) {
                // / [method] bind view and get install status.
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                mLoadingDialog = null;
                if (WearableManager.getInstance().isAvailable()) {
                    InstallManager.getInstance().sendRequestInstallData();
                } else {
                    for (int i = 0; i < mAppStoreManager.getApplength(); i++) {
                        RemoteAppInfo appInfo = mAppStoreManager.getAppInfo(i);
                        if (appInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.DOWNLOADING
                                && appInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.INSTALLING) {
                            if (appInfo.isNeedToUpdate()) {
                                appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_UPDATE);
                            } else {
                                if (appInfo.isDownload()) {
                                    if (appInfo.isInstalled()) {
                                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL);
                                    } else {
                                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
                                    }
                                } else {
                                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
                                }
                            }
                        }
                    }
                }
                AppStoreActivity.this.getListView().setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
        // / add end

        this.getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!WearableManager.getInstance().isAvailable()) {
                    mToast.show();
                    return;
                }
                Intent intent = new Intent(AppStoreActivity.this, AppDetailActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                RemoteAppInfo appInfo = AppStoreManager.getInstance().getAppInfo(position);
                if (!WearableManager.getInstance().isAvailable()) {
                    mToast.show();
                    return true;
                }

                if (appInfo.getAppStatus() == RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL) {
                    showUninstallPrompt();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        isActivityResumed = true;
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        isActivityResumed = false;
        super.onPause();
    }

    protected void onDestroy() {
        Log.i(TAG, "onDestory");
        super.onDestroy();
        instance = null;
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        VxpInstallController.removeListener(InstallManager.getInstance());
        RemoteAppInfo.removeListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void showUninstallPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.uninstall);
        builder.setMessage(R.string.uninstall_content);

        // Cancel, do nothing
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoteAppInfo appInfo = mAppStoreManager.getAppInfo(mCurrId);
                if (appInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL) {
                    return;
                }
                InstallManager.getInstance().sendUnInstallData(appInfo);
            }
        });
        builder.create().show();
    }

    public class ViewHolder {
        public TextView tvAppName;

        public TextView tvSubText;

        public ImageView ivIcon;

        public Button installButton;
    }

    public class ListAddLayoutAdapter extends BaseAdapter {

        private Context context;

        private AppStoreActivity activity;

        public ListAddLayoutAdapter(Context context) {
            this.context = context;
            this.activity = (AppStoreActivity) context;
            mInflater = activity.getLayoutInflater();
        }

        public int getCount() {
            return mAppStoreManager.getApplength();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("NewApi")
        public View getView(final int position, View view, ViewGroup arg2) {

            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.appstore_listview, null);
                view.setPadding(20, 20, 20, 30);

                viewHolder.tvAppName = (TextView) view.findViewById(R.id.app_name);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.app_icon);
                viewHolder.installButton = (Button) view.findViewById(R.id.install);
                viewHolder.installButton.setFocusable(false);
            } else {
                viewHolder = (ViewHolder) view.getTag();

                if (viewHolder == null) {
                    viewHolder = new ViewHolder();

                    viewHolder.tvAppName = (TextView) view.findViewById(R.id.app_name);
                    viewHolder.ivIcon = (ImageView) view.findViewById(R.id.app_icon);
                    viewHolder.installButton = (Button) view.findViewById(R.id.install);
                    viewHolder.installButton.setFocusable(false);
                }
            }

            RemoteAppInfo appInfo = mAppStoreManager.getAppInfo(position);

            mAppViewMap.put(appInfo, viewHolder);

            String iconPath = appInfo.getIconPath();
            byte[] imageBuffer = null;
            if (iconPath != null) {
                File file = new File(iconPath);
                try {
                    @SuppressWarnings("resource")
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
                viewHolder.ivIcon.setImageBitmap(bm);
            } else {
                viewHolder.ivIcon.setBackgroundResource(R.drawable.setting_icon1);
            }

            viewHolder.tvAppName.setText(appInfo.getAppName());
            viewHolder.tvAppName.setTextSize(20f);
            // add for refresh download/install view.
            refreshAppStatus(viewHolder, appInfo);
            if (!WearableManager.getInstance().isAvailable()) {
                viewHolder.installButton.setClickable(false);
            }
            viewHolder.installButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrId = position;
                    RemoteAppInfo appInfo = mAppStoreManager.getAppInfo(position);
                    if (!WearableManager.getInstance().isAvailable()) {
                        mToast.show();
                        return;
                    }
                    if (isFastDoubleClick()) {
                        return;
                    }
                    int status = appInfo.getAppStatus();
                    switch (status) {
                        case RemoteAppInfo.RemoteAppStatus.NEED_UPDATE:
                            if (mAppStoreManager.getDownloadingCount() >= 5) {
                                Toast.makeText(AppStoreActivity.this,
                                        R.string.reachmax, 1).show();
                            } else {
                                mAppStoreManager.updateApp(appInfo);
                            }
                            break;
                        case RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL:
                        case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_FAILED:
                            if (mAppStoreManager.getDownloadingCount() >= 5) {
                                Toast.makeText(AppStoreActivity.this,
                                        R.string.reachmax, 1).show();
                            } else {
                                mAppStoreManager.downloadApp(appInfo);
                            }
                            break;
                        case RemoteAppInfo.RemoteAppStatus.INSTALL_FAILED:
                        case RemoteAppInfo.RemoteAppStatus.NEED_INSTALL:
                            if (!WearableManager.getInstance().isAvailable()) {
                                mToast.show();
                                return;
                            }
                            appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALLING);
                            InstallManager.getInstance().sendInstallData(appInfo);
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

            view.setTag(viewHolder);
            return view;
        }
    }

    @Override
    public void onAppInfoChanged(final RemoteAppInfo appInfo) {
        Log.i(TAG,
                "AppStoreActivity#onAppInfoChanged, appInfo.getRecieverID() = "
                        + appInfo.getReceiverID());
        final ViewHolder viewHolder = mAppViewMap.get(appInfo);
        mHandler.post(new Runnable() {
            public void run() {
                if (viewHolder == null) {
                    Log.i(TAG, "AppStoreActivity#onAppInfoChanged, viewHolder == null, update all");
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                String iconPath = appInfo.getIconPath();
                byte[] imageBuffer = null;
                if (iconPath != null) {
                    File file = new File(iconPath);
                    try {
                        @SuppressWarnings("resource")
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
                    if (bm != null) {
                        viewHolder.ivIcon.setImageBitmap(bm);
                    } else {
                        viewHolder.ivIcon.setBackgroundResource(R.drawable.setting_icon1);
                    }
                } else {
                    viewHolder.ivIcon.setBackgroundResource(R.drawable.setting_icon1);
                }
            }
        });
    }

    public void onAppStatusChanged(final RemoteAppInfo appInfo) {
        Log.i(TAG,
                "AppStoreActivity#onAppStatusChanged, appInfo.getRecieverID() = "
                        + appInfo.getReceiverID());
        final ViewHolder viewHolder = mAppViewMap.get(appInfo);
        mHandler.post(new Runnable() {
            public void run() {
                if (viewHolder == null) {
                    Log.i(TAG, "AppStoreActivity#onAppInfoChanged, viewHolder == null, update all");
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                refreshAppStatus(viewHolder, appInfo);
            }
        });
    }

    // add for refresh download/install view.
    private void refreshAppStatus(ViewHolder viewHolder, RemoteAppInfo appInfo) {
        int status = appInfo.getAppStatus();
        Log.i(TAG,
                "AppStoreActivity#refreshAppStatus, appInfo.getRecieverID() = "
                        + appInfo.getReceiverID() + ", status = " + status);
        switch (status) {
            case RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL:
            case RemoteAppInfo.RemoteAppStatus.NEED_INSTALL:
                viewHolder.installButton.setBackgroundResource(R.drawable.bt_store_install);
                viewHolder.installButton.setText(R.string.install);
                viewHolder.installButton.setEnabled(true);
                break;
            case RemoteAppInfo.RemoteAppStatus.DOWNLOADING:
                viewHolder.installButton.setBackgroundResource(R.drawable.bt_store_downloading);
                viewHolder.installButton.setText(R.string.downloading_hint);
                viewHolder.installButton.setEnabled(false);
                break;
            case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_SUCCESSFUL:
                if (!WearableManager.getInstance().isAvailable()) {
                    mToast.show();
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
                    return;
                }
                if (appInfo.isInstalled()) {
                    if (appInfo.isNeedToUpdate()) {
                        int deviceVersion = WearableManager.getInstance().getRemoteDeviceVersion();
                        if (VxpInstallController.getInstance().isCTASupported()) {
                          //if need to update pre-installed vxp, restore permission for it.
                            InstallManager.getInstance().sendGetPermission(appInfo);
                        } else {
                            InstallManager.getInstance().sendUnInstallData(appInfo);
                            InstallManager.getInstance().sendInstallData(appInfo);
                        }
                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALLING);
                    } else {
                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL);
                    }
                } else {
                    InstallManager.getInstance().sendInstallData(appInfo);
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALLING);
                }
                break;
            case RemoteAppInfo.RemoteAppStatus.DOWNLOAD_FAILED:
                Toast.makeText(AppStoreActivity.this, R.string.download_fail, 1).show();
                if (appInfo.isNeedToUpdate()) {
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_UPDATE);
                } else {
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
                }
                break;
            case RemoteAppInfo.RemoteAppStatus.INSTALLING:
                viewHolder.installButton.setBackgroundResource(R.drawable.bt_store_downloading);
                viewHolder.installButton.setText(R.string.installing_hint);
                viewHolder.installButton.setEnabled(false);
                break;
            case RemoteAppInfo.RemoteAppStatus.INSTALL_FAILED:
                if (appInfo.isNeedToUpdate()) {
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_UPDATE);
                } else {
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
                }
                break;
            case RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL:
                appInfo.setNeedToUpdate(false);
                appInfo.setInstalled(true);
                viewHolder.installButton.setBackgroundResource(R.drawable.bt_store_uninstall);
                viewHolder.installButton.setText(R.string.uninstall);
                viewHolder.installButton.setEnabled(true);
                break;
            case RemoteAppInfo.RemoteAppStatus.NEED_UPDATE:
                viewHolder.installButton.setBackgroundResource(R.drawable.bt_store_update);
                viewHolder.installButton.setText(R.string.update);
                viewHolder.installButton.setEnabled(true);
            default:
                break;
        }
    }

    public void onDownloadError(final String error) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(AppStoreActivity.this, error, 1).show();
            }
        });
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
