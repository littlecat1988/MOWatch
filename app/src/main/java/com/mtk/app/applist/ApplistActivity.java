
package com.mtk.app.applist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.mediatek.ctrl.yahooweather.YahooWeatherController;
import com.mediatek.wearable.VxpControllerChangeListener;
import com.mediatek.wearable.VxpInstallController;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.appstore.AppStoreActivity;
import com.mtk.app.yahooweather.YWSettingActivity;
import com.mtk.btnotification.R;
import com.mtk.main.BTNotificationApplication;
import com.mtk.main.MainActivity;

import android.util.Log;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.binarydata.processing.MREProperties;

public class ApplistActivity extends ListActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, VxpControllerChangeListener {

    private ListAddLayoutAdapter mAdapter;
    private LayoutInflater mInflater;

    private AppManager mAppManager = AppManager.getInstance();

    private Toast mToast = null;

    private Toast mCommonToast = null;

    private SharedPreferences installPrefs = null;

    private int mCurrId;

    private int count = 0;

    private Context mContext;

    public static AppInfo mCurrApp = null;

    public static ApplistActivity instance = null;

    private ProgressDialog mProgressDialog;

    LoadInstallStatusTask loadPackageTask = null;

    private static final String TAG = "AppManager/Applist";

    private static final int STATE_NONE = 0;

    private static final int STATE_INSTALLING = 1;

    private static final int STATE_UNINSTALLING = 2;

    private static final int STATE_ALLVXP_UNINSTALLING = 3;

    private static final int STATE_GETTING_ALLVXP = 4;
    


    // private static final int STATE_WAITING_FOR_RSP = 4;
    private static final int STATE_DELETE_VXP = 5;

    public static int mGetStautsState = STATE_NONE;

    // private static int mCurrentState = STATE_NONE;
    // private static int mCurrentWriteLength = 0;
    private int mCurrentLessCount = 0;

    private boolean mInstallError;

    private static final int MENU_ENTER_APPSTORE = 1001;

    private static final int MENU_UNINSTALL_ALLVXP = 1002;

    // private static final int MENU_UNINSTALL_THIRDVXP = 1003;
    private VxpInstallController mController = VxpInstallController.getInstance();

    private boolean isActivityResumed = false;

    HashMap<Integer, String> mErrorMap = new HashMap<Integer, String>();

    private int mDeviceVersion = 0;

    private static final int SET_PERMISSION_BEFORE_INSTALL = 8001;
    private static int NO_PERMISSION                       = 0x00000000;
    private static int CANT_MODIFY_PERMISSION              = 0x80000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (instance != null) {
            VxpInstallController.removeListener(instance);
            instance.finish();
        }
        mDeviceVersion = WearableManager.getInstance().getRemoteDeviceVersion();
        instance = this;
        initErrorString();
        mGetStautsState = STATE_NONE;
        mAdapter = new ListAddLayoutAdapter(this);
        this.setListAdapter(mAdapter);
        mContext = BTNotificationApplication.getInstance().getBaseContext();

        mAppManager = AppManager.getInstance();

        mToast = Toast.makeText(ApplistActivity.this, R.string.no_connect, 1);
        mCommonToast = Toast.makeText(ApplistActivity.this, R.string.try_later, Toast.LENGTH_SHORT);

        VxpInstallController.addListener(instance);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        installPrefs = getSharedPreferences("installprefs", MODE_PRIVATE);

        this.getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                mCurrId = position;
                AppInfo appInfo = AppManager.getInstance().getAppInfo(position);
                if (!WearableManager.getInstance().isAvailable()) {
                    mToast.show();
                    return;
                }
                if (!appInfo.isInLocal()) {
                    return;
                }
                if (installPrefs.getBoolean(appInfo.getReceiverId(), true)) {
                    if (appInfo.getReceiverId().equals("yahooweather")) {
                        Intent intent = new Intent(ApplistActivity.this, YWSettingActivity.class);
                        startActivityForResult(intent, 100);
                    } else if (appInfo.getReceiverId().equals("facebook")) {
                    } else if (appInfo.getReceiverId().equals("pedometer")) {
                        LaunchApplication(mCurrId);
                    } else {
                        LaunchApplication(mCurrId);
                    }
                } else {
                    if (mGetStautsState != STATE_NONE) {
                        Log.i(TAG, "installerror, currState:" + mGetStautsState);
                        mCommonToast.show();
                        return;
                    }
                    for (int x = 0; x < appInfo.getVxpNum(); x++) {
                        if (appInfo.getVxpPath(x) == null) {
                            Toast.makeText(ApplistActivity.this, R.string.file_lost,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
//                    mCurrApp = appInfo;
                    checkIfNeedPermission(appInfo);
                    return;
                }
            }
        });

        this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                mCurrId = position;
                AppInfo appInfo = AppManager.getInstance().getAppInfo(position);
//                mCurrApp = appInfo;
                if (!WearableManager.getInstance().isAvailable()) {
                    mToast.show();
                    return true;
                }

                if (appInfo.isPreInstalled()) {
                    return true;
                }

                if (installPrefs.getBoolean(appInfo.getReceiverId(), true)) {
                    showUninstallPrompt(appInfo);
                }
                return true;
            }
        });

        loadPackageTask = new LoadInstallStatusTask(this);
        try {
            loadPackageTask.execute("");
        } catch (Exception e) {
            Toast toast = Toast.makeText(ApplistActivity.this, R.string.launchfail, 1);
            toast.show();
        }
    }

    private void showUninstallPrompt(final AppInfo appInfo) {
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

        // Go to accessibility settings
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                AppInfo appInfo = AppManager.getInstance().getAppInfo(mCurrId);
                
                if (mGetStautsState != STATE_NONE) {
                    Log.i(TAG, "uninstallerror, currState:" + mGetStautsState);
                    mCommonToast.show();
                    return;
                }
                boolean isInstalled = installPrefs.getBoolean(appInfo.getReceiverId(), false);
                if (!isInstalled) {
                    return;
                }
                mCurrApp = appInfo;
                mGetStautsState = STATE_UNINSTALLING;
                sendUnInstallData(mCurrApp);
            }
        });
        builder.create().show();
    }

    private void sendUnInstallData(AppInfo appInfo) {

        try {
            // RunRequestTimer();
            mController.sendVxpUnInstall(appInfo.getNormalVxpName());
            // MreInstaller installer = MreInstaller.getInstance();
            // installer.sendVxpUnInstall(RECEIVER_APP_MANAGER,
            // appInfo.getVxpName(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkIfNeedPermission(AppInfo appInfo) {
        if (mController.isCTASupported()) {
            String normalVxpPath = appInfo.getVxpPath(0);
            Log.i(TAG, "checkIfSetPermission, normalVxpPath = " + normalVxpPath);
            int permSet = MREProperties.getPermission(normalVxpPath);
            if (permSet != NO_PERMISSION && permSet != CANT_MODIFY_PERMISSION) {
                mCurrApp = appInfo;
                Intent intent = new Intent(ApplistActivity.this, AppAuthActivity.class);
                intent.putExtra("permset", permSet);
                startActivityForResult(intent, SET_PERMISSION_BEFORE_INSTALL);
                return;
            }
        }
        sendInstallData(appInfo, NO_PERMISSION);
    }

    private void sendInstallData(AppInfo appInfo, int permValue) {

        String installString = getResources().getString(R.string.installing) + " "
                + appInfo.getAppName() + " "
                + getResources().getString(R.string.install_into) + ", "
                + getResources().getString(R.string.take_mins);
        mCurrentLessCount = appInfo.getVxpNum();
        mProgressDialog = new ProgressDialog(ApplistActivity.this);
        mProgressDialog.setTitle(R.string.install_application);
        mProgressDialog.setMessage(installString);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mCurrApp = appInfo;
        mGetStautsState = STATE_INSTALLING;
        mInstallError = false;
        for (int index = (appInfo.getVxpNum() - 1); index >= 0; index--) {
            try {
                File file = new File(appInfo.getVxpPath(index));
                byte[] vxpBuffer = null;
                @SuppressWarnings("resource")
                FileInputStream fis = new FileInputStream(file);
                vxpBuffer = new byte[fis.available()];
                fis.read(vxpBuffer);
                fis.close();
                appInfo.setVxpSize(index, vxpBuffer.length);

                if (mController.isCTASupported()) {
                    mController.sendVxpInstall(appInfo.getVxpName(index), vxpBuffer,
                            appInfo.getVxpType(index), permValue);
                } else {
                    mController.sendVxpInstall(appInfo.getVxpName(index), vxpBuffer,
                            appInfo.getVxpType(index));
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_PERMISSION_BEFORE_INSTALL) {
            if (resultCode == RESULT_OK) {
                int currentPermission = data.getExtras().getInt("permission", 0x00000000);
                sendInstallData(mCurrApp, currentPermission);
            }
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        mAppManager.refreshAppInfo();
        isActivityResumed = true;
        if (mGetStautsState == STATE_NONE) {
            sendRequestInstallData();
        } else {
            if (loadPackageTask != null) {
                loadPackageTask.closeDialog();
                loadPackageTask = null;
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        isActivityResumed = false;
    }

    public void onDestory() {
        Log.i(TAG, "onDestory");
        super.onDestroy();
        VxpInstallController.removeListener(instance);
        dialogDismiss();
    }

    private void sendRequestInstallData() {
        mGetStautsState = STATE_GETTING_ALLVXP;

        if (mDeviceVersion < WearableManager.VERSION_35) {
            try {
                String vxpList = new String();
                for (int i = 0; i < mAppManager.getApplength(); i++) {
                    if (i == 0) {
                        vxpList += mAppManager.getAppInfo(i).getVxpName(0);
                    } else {
                        vxpList += " ";
                        vxpList += mAppManager.getAppInfo(i).getVxpName(0);
                    }
                }
                mController.sendGetVxpStatus(vxpList);
            } catch (Exception e) {
                mGetStautsState = STATE_NONE;
                e.printStackTrace();
            }
        } else {
            mController.sendGetAllVxpInfo();
        }
    }

    public class ViewHolder {
        public TextView tvAppName;

        public TextView tvSubText;

        public ImageView ivIcon;

        public ImageButton installButton;
        // public Switch installSwitch;
    }

    public class ListAddLayoutAdapter extends BaseAdapter implements
            SharedPreferences.OnSharedPreferenceChangeListener {



		private Context context;

        private ApplistActivity activity;

        private SharedPreferences installPrefs = null;

        public ListAddLayoutAdapter(Context context) {
            this.context = context;
            this.activity = (ApplistActivity) context;
            mInflater = activity.getLayoutInflater();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.registerOnSharedPreferenceChangeListener(this);

            installPrefs = getSharedPreferences("installprefs", MODE_PRIVATE);
            installPrefs.registerOnSharedPreferenceChangeListener(this);
        }

        public int getCount() {
            return mAppManager.getApplength();
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
                view = mInflater.inflate(R.layout.applistview, null);

                viewHolder.tvAppName = (TextView) view.findViewById(R.id.app_name);
                viewHolder.tvSubText = (TextView) view.findViewById(R.id.app_subtext);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.app_icon);
                viewHolder.installButton = (ImageButton) view.findViewById(R.id.install_button);
                viewHolder.installButton.setFocusable(false);
            } else {
                viewHolder = (ViewHolder) view.getTag();

                if (viewHolder == null) {
                    viewHolder = new ViewHolder();

                    viewHolder.tvAppName = (TextView) view.findViewById(R.id.app_name);
                    viewHolder.tvSubText = (TextView) view.findViewById(R.id.app_subtext);
                    viewHolder.ivIcon = (ImageView) view.findViewById(R.id.app_icon);
                    viewHolder.installButton = (ImageButton) view.findViewById(R.id.install_button);
                    viewHolder.installButton.setFocusable(false);
                }
            }

            AppInfo appInfo = mAppManager.getAppInfo(position);
//
			
            byte[] imageBuffer = null;
            try {
                File file = new File(appInfo.getIconPath());
                @SuppressWarnings("resource")
                FileInputStream fis = new FileInputStream(file);
                imageBuffer = new byte[fis.available()];
                fis.read(imageBuffer);
                fis.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (imageBuffer != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length);
                viewHolder.ivIcon.setImageBitmap(bm);
            } else {
                viewHolder.ivIcon.setImageResource(R.drawable.setting_icon1);
            }

            viewHolder.tvAppName.setText(appInfo.getAppName());
            if (appInfo.isPreInstalled()) {
                viewHolder.tvSubText.setText(R.string.pre_install);
                viewHolder.tvSubText.setTextColor(Color.RED);
                viewHolder.installButton.setVisibility(View.GONE);
            } else {
                if (!appInfo.isInLocal()) {
                    viewHolder.tvSubText.setText(R.string.uninstall_hint);
                    viewHolder.tvSubText.setTextColor(Color.RED);
                    viewHolder.installButton.setImageResource(R.drawable.bt_uninstall);
                } else {
                    viewHolder.tvSubText.setTextColor(Color.GRAY);
                    if (installPrefs.getBoolean(appInfo.getReceiverId(), true)) {
                        if (appInfo.getPackageName().equals("null")) {
                            viewHolder.tvSubText.setText(R.string.install_done);
                        } else {
                            viewHolder.tvSubText.setText(R.string.launch_app);
                        }
                        viewHolder.installButton.setImageResource(R.drawable.bt_uninstall);
                    } else {
                        viewHolder.tvSubText.setText(R.string.install_hint);
                        viewHolder.installButton.setImageResource(R.drawable.bt_install);
                    }
                }
            }

            if (!WearableManager.getInstance().isAvailable()) {
                viewHolder.installButton.setClickable(false);
            }
            viewHolder.installButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrId = position;
                    AppInfo appInfo = AppManager.getInstance().getAppInfo(position);
                    ImageButton currButton = (ImageButton) v;
                    if (!WearableManager.getInstance().isAvailable()) {
                        mToast.show();
                        if (installPrefs.getBoolean(appInfo.getReceiverId(), true)) {
                            currButton.setImageResource(R.drawable.bt_uninstall);
                        } else {
                            currButton.setImageResource(R.drawable.bt_install);
                        }
                        return;
                    }
                    if (installPrefs.getBoolean(appInfo.getReceiverId(), true)) {
//                        mCurrApp = appInfo;
                        currButton.setImageResource(R.drawable.bt_uninstall);
                        showUninstallPrompt(appInfo);
                        return;
                    } else {
                        if (mGetStautsState != STATE_NONE) {
                            Log.i(TAG, "installerror, currState:" + mGetStautsState);
                            mCommonToast.show();
                            return;
                        }
                        for (int x = 0; x < appInfo.getVxpNum(); x++) {
                            if (appInfo.getVxpPath(x) == null) {
                                Toast.makeText(ApplistActivity.this, R.string.file_lost,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
//                        mCurrApp = appInfo;
                        checkIfNeedPermission(appInfo);
                        return;
                    }
                }
            });

            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            dialogDismiss();
            notifyDataSetChanged();
        }
    }

    private void LaunchApplication(int index) {

        AppInfo appInfo = mAppManager.getAppInfo(index);
        PackageInfo packageInfo = null;
        if (appInfo.getPackageName().equals("null")) {
            return;
        }
        try {
            packageInfo = getPackageManager().getPackageInfo(appInfo.getPackageName(), 0);
            if (packageInfo == null) {
                System.out.println("packageInfo==null");
            } else {
                System.out.println("packageInfo!=null");
            }
        } catch (PackageManager.NameNotFoundException e) {
            String downloadUrl = appInfo.getDownloadUrl();
            if (!downloadUrl.equals("null")) {
                InstallApplication(appInfo.getDownloadUrl());
            } else {
                Toast toast = Toast.makeText(mContext, R.string.warning_not_install,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageInfo.packageName);
        System.out.println("packageInfo.packageName=" + packageInfo.packageName);

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(
                resolveIntent, 0);

        System.out.println("resolveInfoList.size()=" + resolveInfoList.size());

        ResolveInfo resolveInfo = resolveInfoList.iterator().next();
        if (resolveInfo != null) {
            String activityPackageName = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName(activityPackageName, className);

            intent.setComponent(componentName);
            startActivity(intent);
        }

    }

    private void InstallApplication(String url) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(it);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private class LoadInstallStatusTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog mLoadingDialog;

        public LoadInstallStatusTask(Context context) {
            Log.i(TAG, "LoadPackageTask(), Create LoadPackageTask!");

            mContext = context;
            createProgressDialog();
        }

        private void createProgressDialog() {
            mLoadingDialog = new ProgressDialog(mContext);
            mLoadingDialog.setTitle(R.string.progress_dialog_title);
            mLoadingDialog.setMessage(mContext.getString(R.string.progress_dialog_message));
            mLoadingDialog.show();

            Log.i(TAG, "createProgressDialog(), ProgressDialog shows");
        }

        public void closeDialog() {
            if (Build.VERSION.SDK_INT >= 17) {
                if (ApplistActivity.this.isDestroyed() || ApplistActivity.this.isFinishing()) {
                    return;
                }
            } else {
                if (ApplistActivity.this.isFinishing()) {
                    return;
                }
            }
            if (mLoadingDialog != null) {
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Log.i(TAG, "closeDialog(), ProgressDialog dismiss");
                mLoadingDialog = null;
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    @Override
    public void notifyVxpInstallResult(String vxpName, boolean success, int errorCode) {
        Log.i(TAG, "notifyVxpInstallStatus, mCurrentLessCount = " + mCurrentLessCount
                + ", mGetStautsState = " + mGetStautsState + ", vxpName = " + vxpName
                + ", success = " + success + ", errorCode = " + errorCode);

        if (mGetStautsState != STATE_INSTALLING) {
            return;
        }

        if (mCurrentLessCount > 0) {
            mCurrentLessCount--;
        }

        if (!success) {
            mInstallError = true;
        }

        if (mCurrentLessCount == 0) {
            if (mInstallError == true) {
                AppInfo failedAppInfo = ApplistActivity.mCurrApp;
                mGetStautsState = STATE_DELETE_VXP;
                mCurrentLessCount = failedAppInfo.getVxpNum();
                for (int i = (failedAppInfo.getVxpNum() - 1); i >= 0; i--) {
                    mController.sendVxpDelete(failedAppInfo.getVxpName(i));
                }
                if (mDeviceVersion < WearableManager.VERSION_35) {
                    Toast.makeText(mContext, R.string.install_fail, Toast.LENGTH_SHORT).show();
                } else {
                    String toastStr = getString(
                            R.string.install_fail_code,
                            failedAppInfo.getAppName(),
                            mErrorMap.get(errorCode) == null ? getString(R.string.install_code_error) : mErrorMap
                                    .get(errorCode));
                    Toast.makeText(mContext, toastStr, Toast.LENGTH_SHORT).show();
                }
            } else {
                mGetStautsState = STATE_NONE;
                dialogDismiss();
            }
            SharedPreferences.Editor editor = installPrefs.edit();
            AppInfo appInfo = ApplistActivity.mCurrApp;
            editor.putBoolean(appInfo.getReceiverId(), !mInstallError);
            editor.commit();

        }
    }

    @Override
    public void notifyVxpUninstallResult(String vxpName, boolean success) {
        Log.i(TAG, "notifyVxpUninstallStatus, mCurrentLessCount = " + mCurrentLessCount
                + ", mGetStautsState = " + mGetStautsState + ", vxpName = " + vxpName
                + ", success = " + success);

        if (mGetStautsState != STATE_UNINSTALLING) {
            return;
        }
        mGetStautsState = STATE_NONE;

        SharedPreferences.Editor editor = installPrefs.edit();
        AppInfo appInfo = ApplistActivity.mCurrApp;
        if (success) {
            if (!appInfo.isInLocal()) {
                AppManager.getInstance().removeAppInfo(appInfo);
            }
        }
        editor.putBoolean(appInfo.getReceiverId(), !success);
        editor.commit();

        // if (mProgressDialog != null && mProgressDialog.isShowing()) {
        // mProgressDialog.cancel();
        // mProgressDialog = null;
        // }
    }

    @Override
    public void notifyVxpListStatus(String[] vxpList, Integer[] statusList) {
        if (!isActivityResumed) {
            Log.i(TAG, "notifyVxpListStatus, this activity is running backgroud, return.");
            return;
        }
        Log.i(TAG,
                "notifyVxpListStatus, mCurrentLessCount = " + mCurrentLessCount
                        + ", mGetStautsState = " + mGetStautsState + ", vxpList = "
                        + Arrays.toString(vxpList) + ", statusList = "
                        + Arrays.toString(statusList));

        if (mGetStautsState != STATE_GETTING_ALLVXP) {
            return;
        }
        mGetStautsState = STATE_NONE;
        SharedPreferences.Editor editor = installPrefs.edit();
        AppManager appManager = AppManager.getInstance();
        for (int i = 0; i < appManager.getApplength(); i++) {
            if (statusList[i] == 1) {
                editor.putBoolean(appManager.getAppInfo(i).getReceiverId(), true);
            } else {
                editor.putBoolean(appManager.getAppInfo(i).getReceiverId(), false);
            }
        }
        editor.commit();
        if (loadPackageTask != null) {
            loadPackageTask.closeDialog();
            loadPackageTask = null;
        }
    }

    @Override
    public void notifyAllVxpUninstallResult(boolean success) {
        Log.i(TAG, "notifyAllVxpUninstallResult, success = " + success);
        // if (mGetStautsState != STATE_ALLVXP_UNINSTALLING) {
        // return;
        // }
        mGetStautsState = STATE_NONE;
        if (success) {
            SharedPreferences.Editor editor = installPrefs.edit();
            AppManager appManager = AppManager.getInstance();
            int length = appManager.getApplength();
            for (int i = length - 1; i >= 0; i--) {
                AppInfo appInfo = appManager.getAppInfo(i);
                if (!appInfo.isInLocal() && !appInfo.isPreInstalled()) {
                    AppManager.getInstance().removeAppInfo(appInfo);
                }
                if (!appInfo.isPreInstalled()) {
                    editor.putBoolean(appInfo.getReceiverId(), false);
                }
            }
            editor.commit();
        } else {
//            mGetStautsState = STATE_GETTING_ALLVXP;
//            mController.sendGetAllVxpInfo();
             sendRequestInstallData();
        }

        dialogDismiss();
    }

    @Override
    public void notifyAllVxpList(String[] vxpList) {
        if (!isActivityResumed) {
            Log.i(TAG, "notifyVxpListStatus, this activity is running backgroud, return.");
            return;
        }
        Log.i(TAG, "notifyAllVxpList, vxpList = " + Arrays.toString(vxpList));
        if (mGetStautsState != STATE_GETTING_ALLVXP) {
            return;
        }
        mGetStautsState = STATE_NONE;
//        String provider = "";
        String appName = "";
        String version = "";
//        ArrayList<String[]> vxpInfoList = new ArrayList<String[]>();
        HashMap<String, String[]> vxpInfoMap = new HashMap<String, String[]>();
        ArrayList<String> vxpNames = new ArrayList<String>();
        for (String vxpItem : vxpList) {
            vxpItem = vxpItem.substring(1, vxpItem.length() - 1);
            String[] vxpItemInfo = vxpItem.split(",");
            if (vxpItemInfo.length == 3) {
//                provider = vxpItemInfo[0];
                appName = vxpItemInfo[0];
                version = vxpItemInfo[1];
//                isPreInstalled = Integer.parseInt(vxpItemInfo[2]);
//                vxpInfoList.add(vxpItemInfo);
                //add by lixiang for hide some apps 20150603
//                Log.e(TAG,"AppName= "+appName);
//    				if(appName.equals(HIDE_APP_DIGITAL_CLOCK) || appName.equals(HIDE_APP_CODOON)){
//    					continue;
//    				}
                vxpInfoMap.put(appName, vxpItemInfo);
                vxpNames.add(appName);
            }
        }
        SharedPreferences.Editor editor = installPrefs.edit();
        AppManager appManager = AppManager.getInstance();
        for (int i = 0; i < appManager.getApplength(); i++) {
            AppInfo appInfo = appManager.getAppInfo(i);
            String vxpName = appInfo.getNormalVxpName();
            if (vxpNames.contains(vxpName)) {
                editor.putBoolean(appInfo.getReceiverId(), true);
                vxpNames.remove(vxpName);
                vxpInfoMap.remove(vxpName);
            } else {
                editor.putBoolean(appInfo.getReceiverId(), false);
            }
        }

        for (String vxp : vxpNames) {
            int index = vxp.lastIndexOf('.');
            String vxpNonLocal = vxp;
            if (index != -1) {
                vxpNonLocal = vxp.substring(0, index);
            }
            AppInfo appInfo = new AppInfo(vxpNonLocal, vxp, false);
            if (vxpInfoMap.get(vxp)[2].equals("1")) {
                appInfo.setPreInstalled(true);
            } else {
                appInfo.setPreInstalled(false);
            }
            appManager.addAppInfo(appInfo);
            editor.putBoolean(appInfo.getReceiverId(), true);
        }
        editor.commit();

        if (loadPackageTask != null) {
            loadPackageTask.closeDialog();
            loadPackageTask = null;
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDeleteResult(String vxpName, boolean success) {
        Log.i(TAG, "notifyDeleteResult, mCurrentLessCount = " + mCurrentLessCount
                + ", mGetStautsState = " + mGetStautsState + ", vxpName = " + vxpName
                + ", success = " + success);
        if (mGetStautsState != STATE_DELETE_VXP) {
            return;
        }

        if (mCurrentLessCount > 0) {
            mCurrentLessCount--;
        }

        if (mCurrentLessCount == 0) {
            mGetStautsState = STATE_NONE;
            dialogDismiss();
        }
    }

    @Override
    public void notifyVxpPermissionStatus(String vxpName, boolean success, int permSet,
            int permValue) {
        return;
    }

    @Override
    public void notifyVxpPermissionSettingResult(String vxpName, boolean success) {
        return;
    }

    @Override
    public void notifyConnectionChanged(int state) {
        if (state == WearableManager.STATE_CONNECT_LOST) {
            mGetStautsState = STATE_NONE;
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        if (WearableManager.getInstance().isAvailable()) {
            mDeviceVersion = WearableManager.getInstance().getRemoteDeviceVersion();
        }
    }

    @Override
    public void notifyProgressChanged(float percent) {
        // mProgressDialog.setProgress((int)(mProgressDialog.getMax() *
        // percent));
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mDeviceVersion < WearableManager.VERSION_35) {
            return true;
        }
        menu.clear();
//        menu.add(0, MENU_ENTER_APPSTORE, 0, R.string.app_store).setIcon(R.drawable.appstore)
//                .setTitle(R.string.app_store).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_UNINSTALL_ALLVXP, 0, R.string.uninstall_allvxp).setTitle(
                R.string.uninstall_allvxp);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ENTER_APPSTORE:
                Intent intent = new Intent(this, AppStoreActivity.class);
                startActivity(intent);
                break;
            case MENU_UNINSTALL_ALLVXP:
                if (mGetStautsState != STATE_NONE) {
                    Log.i(TAG, "all vxp uninstallerror, currState:" + mGetStautsState);
                    mCommonToast.show();
                    break;
                }
                mProgressDialog = new ProgressDialog(ApplistActivity.this);
                mProgressDialog.setTitle(R.string.uninstall_allvxp);
                mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_message));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                mGetStautsState = STATE_ALLVXP_UNINSTALLING;

                mController.sendAllVxpUninstall();
                break;
        }
        return true;
    }

    private void initErrorString() {
        mErrorMap.put(1, getString(R.string.install_code_success));
        mErrorMap.put(-1000, getString(R.string.install_code_error));
        mErrorMap.put(-1001, getString(R.string.install_code_error1));
        mErrorMap.put(-1002, getString(R.string.install_code_error2));
        mErrorMap.put(-1003, getString(R.string.install_code_error3));
        mErrorMap.put(-1004, getString(R.string.install_code_error4));
        mErrorMap.put(-1005, getString(R.string.install_code_error5));
        mErrorMap.put(-1006, getString(R.string.install_code_error6));
        mErrorMap.put(-1007, getString(R.string.install_code_error7));
        mErrorMap.put(-1008, getString(R.string.install_code_error8));
        mErrorMap.put(-1009, getString(R.string.install_code_error9));
        mErrorMap.put(-1010, getString(R.string.install_code_error10));
        mErrorMap.put(-1011, getString(R.string.install_code_error11));
    }

    private void dialogDismiss() {
        if (Build.VERSION.SDK_INT >= 17) {
            if (this.isDestroyed() || this.isFinishing()) {
                return;
            }
        } else{
            if (this.isFinishing()) {
                return;
            }
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }
}
