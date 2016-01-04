
package com.mtk.app.appstore;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.mediatek.wearable.VxpControllerChangeListener;
import com.mediatek.wearable.VxpInstallController;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.applist.AppInfo;
import com.mtk.app.applist.AppManager;
import com.mtk.btnotification.R;

public class InstallManager implements VxpControllerChangeListener {

    private static InstallManager sInstance;

    private AppStoreManager mAppStoreManager;

    private VxpInstallController mController = VxpInstallController.getInstance();

    private static final String TAG = "AppManager/InstallManager";

    // private int mCurrentLessCount = 0;
    HashMap<Integer, String> mErrorMap = new HashMap<Integer, String>();

    public static InstallManager getInstance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            sInstance = new InstallManager();
        }
        return sInstance;
    }

    private InstallManager() {
        mAppStoreManager = AppStoreManager.getInstance();
        initErrorString();
    }

    @Override
    public void notifyProgressChanged(float percent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyVxpInstallResult(String vxpName, boolean isSucceed, int errorCode) {
        Log.i(TAG, "notifyVxpInstallStatus, vxpName = " + vxpName + ", isSucceed = " + isSucceed
                + ", errorCode = " + errorCode);

        RemoteAppInfo currApp = AppStoreManager.getInstance().getAppInfoByVxp(vxpName);
        if (currApp == null) {
            return;
        }

        // int installedVxpNum = currApp.getInstalledVxpNum();
        if (currApp.mInstalledVxpNum < currApp.getVxpNum()) {
            currApp.mInstalledVxpNum++;
            // currApp.setInstalledVxpNum(installedVxpNum);
        }

        if (!isSucceed) {
            currApp.mInstallError = true;
        }

        if (currApp.mInstalledVxpNum == currApp.getVxpNum()) {
            if (currApp.mInstallError == true) {
                for (int i = (currApp.getVxpNum() - 1); i >= 0; i--) {
                    mController.sendVxpDelete(currApp.getVxpName(i));
                }
                String toastStr = AppStoreActivity.instance.getString(R.string.install_fail_code,
                        currApp.getAppName(), mErrorMap.get(errorCode) == null ? AppStoreActivity.instance.getString(R.string.install_code_error)
                                : mErrorMap.get(errorCode));
                Toast.makeText(AppStoreActivity.instance, toastStr, Toast.LENGTH_SHORT).show();
            } else {
                if (currApp.mInstalledVxpNum == currApp.getVxpNum()) {
                    currApp.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL);
                }
            }
        }
    }

    @Override
    public void notifyVxpUninstallResult(String vxpName, boolean isSucceed) {
        Log.i(TAG, "notifyVxpUninstallStatus, vxpName = " + vxpName + ", isSucceed = " + isSucceed);

        RemoteAppInfo currApp = AppStoreManager.getInstance().getAppInfoByVxp(vxpName);
        if (currApp == null) {
            return;
        }
        if (isSucceed) {
            if (currApp.isDownload()) {
                currApp.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
            } else {
                currApp.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
            }
        } else {
            if (currApp.isDownload()) {
                currApp.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL);
            } else {
                currApp.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
            }
        }
    }

    @Override
    public void notifyVxpListStatus(String[] vxpList, Integer[] statusList) {
        if (!AppStoreActivity.isActivityResumed && !AppDetailActivity.isActivityResumed) {
            Log.i(TAG, "notifyVxpListStatus, this activity is running backgroud, return.");
            return;
        }
        Log.i(TAG, "notifyVxpListStatus, vxpList = " + Arrays.toString(vxpList) + ", statusList = "
                + Arrays.toString(statusList));

        AppStoreManager appStoreManager = AppStoreManager.getInstance();
        for (int i = 0; i < appStoreManager.getApplength(); i++) {
            RemoteAppInfo appInfo = appStoreManager.getAppInfo(i);

            if (statusList[i] == 0) {
                appInfo.setInstalled(false);
            } else {
                appInfo.setInstalled(true);
            }

            if (appInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.DOWNLOADING) {
                if (appInfo.isNeedToUpdate()) {
                    appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_UPDATE);
                } else {
                    if (statusList[i] == 0) {
                        if (appInfo.isDownload()) {
                            appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
                        } else {
                            appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
                        }
                    } else {
                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALL_SUCCESSFUL);
                    }
                }
            }
        }
    }

    @Override
    public void notifyAllVxpUninstallResult(boolean success) {
        Log.i(TAG, "notifyAllVxpUninstallResult, success = " + success);
        if (success) {
            AppStoreManager appStoreManager = AppStoreManager.getInstance();
            for (int i = 0; i < appStoreManager.getApplength(); i++) {
                RemoteAppInfo appInfo = appStoreManager.getAppInfo(i);
                appInfo.setInstalled(false);
                if (appInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.DOWNLOADING
                        && appInfo.getAppStatus() != RemoteAppInfo.RemoteAppStatus.INSTALLING) {
                    if (appInfo.isDownload()) {
                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_INSTALL);
                    } else {
                        appInfo.setAppStatus(RemoteAppInfo.RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL);
                    }
                }
            }
        } else {
            sendRequestInstallData();
        }
    }

    @Override
    public void notifyDeleteResult(String vxpName, boolean success) {
        Log.i(TAG, "notifyDeleteResult, vxpName = " + vxpName + ", success = " + success);
        RemoteAppInfo currApp = AppStoreManager.getInstance().getAppInfoByVxp(vxpName);
        if (currApp.mInstalledVxpNum > 0) {
            currApp.mInstalledVxpNum--;
        }
        if (currApp.mInstalledVxpNum == 0) {
            currApp.setAppStatus(RemoteAppInfo.RemoteAppStatus.INSTALL_FAILED);
        }
    }

    @Override
    public void notifyAllVxpList(String[] vxpList) {
        Log.i(TAG, "notifyAllVxpList, vxpList = " + Arrays.toString(vxpList));
    }

    @Override
    public void notifyVxpPermissionStatus(String vxpName, boolean success, int permSet,
            int permValue) {
        Log.i(TAG, "notifyVxpPermissionStatus, vxpName = " + vxpName + ", permSet = " + String.format("%08x", permSet) + ", permValue = " + String.format("%08x", permValue));
        RemoteAppInfo currApp = AppStoreManager.getInstance().getAppInfoByVxp(vxpName);
        if ((0x80000000 & permSet) == 0x80000000) {
            sendUnInstallData(currApp);
            sendInstallData(currApp);
            mController.sendSetVxpPermission(vxpName, 0x80000000);
        } else {
            sendUnInstallData(currApp);
            sendInstallData(currApp);
        }
        return;
    }

    @Override
    public void notifyVxpPermissionSettingResult(String vxpName, boolean success) {
        return;
    }

    @Override
    public void notifyConnectionChanged(int state) {

    }

    public void sendInstallData(RemoteAppInfo appInfo) {
        appInfo.mInstalledVxpNum = 0;
        appInfo.mInstallError = false;
        // mCurrentLessCount = appInfo.getVxpNum();
        for (int i = (appInfo.getVxpNum() - 1); i >= 0; i--) {
            InstallManager.getInstance().sendInstallData(appInfo, i);
        }
    }

    private void sendInstallData(RemoteAppInfo appInfo, int index) {

        try {
            File file = new File(appInfo.getVxpPath(index));
            byte[] vxpBuffer = null;
            @SuppressWarnings("resource")
            FileInputStream fis = new FileInputStream(file);
            vxpBuffer = new byte[fis.available()];
            fis.read(vxpBuffer);
            fis.close();
            // appInfo.setVxpSize(index, vxpBuffer.length);
            mController.sendVxpInstall(appInfo.getVxpName(index), vxpBuffer,
                    appInfo.getVxpType(index));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendUnInstallData(RemoteAppInfo appInfo) {

        try {
            mController.sendVxpUnInstall(appInfo.getVxpName(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendRequestInstallData() {
        if (mAppStoreManager.getApplength() <= 0) {
            return;
        }
        try {
            String vxpList = new String();
            for (int i = 0; i < mAppStoreManager.getApplength(); i++) {
                if (i == 0) {
                    vxpList += mAppStoreManager.getAppInfo(i).getVxpName(0);
                } else {
                    vxpList += " ";
                    vxpList += mAppStoreManager.getAppInfo(i).getVxpName(0);
                }
            }
            // mGetStautsState = STATE_WAITING_FOR_RSP;
            mController.sendGetVxpStatus(vxpList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendGetPermission(RemoteAppInfo appInfo) {
        String vxpName = appInfo.getVxpName(0);
        mController.sendGetVxpPermission(vxpName);
    }

    private void initErrorString() {
        mErrorMap.put(1, AppStoreActivity.instance.getString(R.string.install_code_success));
        mErrorMap.put(-1000, AppStoreActivity.instance.getString(R.string.install_code_error));
        mErrorMap.put(-1001, AppStoreActivity.instance.getString(R.string.install_code_error1));
        mErrorMap.put(-1002, AppStoreActivity.instance.getString(R.string.install_code_error2));
        mErrorMap.put(-1003, AppStoreActivity.instance.getString(R.string.install_code_error3));
        mErrorMap.put(-1004, AppStoreActivity.instance.getString(R.string.install_code_error4));
        mErrorMap.put(-1005, AppStoreActivity.instance.getString(R.string.install_code_error5));
        mErrorMap.put(-1006, AppStoreActivity.instance.getString(R.string.install_code_error6));
        mErrorMap.put(-1007, AppStoreActivity.instance.getString(R.string.install_code_error7));
        mErrorMap.put(-1008, AppStoreActivity.instance.getString(R.string.install_code_error8));
        mErrorMap.put(-1009, AppStoreActivity.instance.getString(R.string.install_code_error9));
        mErrorMap.put(-1010, AppStoreActivity.instance.getString(R.string.install_code_error10));
        mErrorMap.put(-1011, AppStoreActivity.instance.getString(R.string.install_code_error11));
    }
}
