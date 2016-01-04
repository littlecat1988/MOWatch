
package com.mtk.app.appstore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.text.TextUtils;
import android.util.Log;

public class RemoteAppInfo {

    private static final String TAG = "AppManager/RemoteAppInfo";

    // RemoteAppInfo CATEGORY
    public static final int SMARTWATCH_APP_CATEGORY_LOCAL = 1;

    public static final int SMARTWATCH_APP_CATEGORY_THIRDPARTY = 2;

    // RemoteAppInfo VXP_TYPE
    public static final int SMARTWATCH_VXP_TYPE_NORMAL = 1;

    public static final int SMARTWATCH_VXP_TYPE_TINY = 2;

    // RemoteAppInfo status
    public static class RemoteAppStatus {
        // default status
        public static final int NEED_DOWNLOAD_AND_INSTALL = 0;

        // set when begin to download
        public static final int DOWNLOADING = 1;

        // set if download successful
        public static final int DOWNLOAD_SUCCESSFUL = 2;

        // set if download failed
        public static final int DOWNLOAD_FAILED = 3;

        // set when begin to install
        public static final int INSTALLING = 4;

        public static final int NEED_INSTALL = 5;

        // set if install successful
        public static final int INSTALL_FAILED = 6;

        // set if install failed
        public static final int INSTALL_SUCCESSFUL = 7;

        public static final int NEED_UPDATE = 8;
    }

    public class VxpInfo {

        private int mVxpType;

        private String mVxpName;

        private String mVxpPath;

        private String mVxpURL;

        public VxpInfo(int type, String name, String vxpURL) {
            mVxpType = type;
            mVxpName = name;
            mVxpURL = vxpURL;
        }

        public int getType() {
            return mVxpType;
        }

        public String getVxpName() {
            return mVxpName;
        }

        public String getVxpPath() {
            return mVxpPath;
        }

        public void setVxpPath(String path) {
            mVxpPath = path;
        }

        public String getVxpURL() {
            return mVxpURL;
        }
    };

    public interface AppInfoListener {
        void onAppInfoChanged(RemoteAppInfo appInfo);

        // void onAppInfoUpdated(RemoteAppInfo appInfo);

        void onAppStatusChanged(RemoteAppInfo appInfo);

        void onDownloadError(String error);
    }

    private static HashSet<AppInfoListener> mListeners = new HashSet<AppInfoListener>();

    private int mAppCategory;

    private int mAppStatus = RemoteAppStatus.NEED_DOWNLOAD_AND_INSTALL;

    private String mAppPath;

    private String mReceiverID;

    private String mAppName;

    private int mVxpNum;

    // RemoteAppInfo vxp list
    private ArrayList<VxpInfo> mVxpInfoList;

    // RemoteAppInfo icon
    private String mIconName;

    private String mIconURL;

    private String mIconPath;

    // RemoteAppInfo sample image
    private String mSampleName;

    private String mSampleURL;

    private String mSamplePath;

    // RemoteAppInfo apk
    private String mApkPackageName;

    private String mApkURL;

    private String mApkPath;

    // RemoteAppInfo app data
    private String mProvider;

    private String mVersion;

    private String mReleaseDate;

    private String mAppSize;

    private String mIntroduction;

    private boolean mNeedToUpdate;

    private boolean mIsInstalled;

    public int mInstalledVxpNum = 0;

    public boolean mInstallError = false;

    public RemoteAppInfo() {
        mVxpInfoList = new ArrayList<VxpInfo>();
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String provider) {
        mProvider = provider;
    }

    public String getVersion() {
        return mVersion;
    }

    // / vxp update
    public int getVersionNumber() {
        Log.d(TAG, "[getVersionNumber] Version = " + mVersion);
        int version = 0;
        try {
            version = Integer.valueOf(mVersion);
        } catch (NumberFormatException e) {
            Log.d(TAG, "[getVersionNumber] NumberFormatException");
        }
        return version;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getAppSize() {
        return mAppSize;
    }

    public void setAppSize(String appSize) {
        mAppSize = appSize;
    }

    public String getIntroduction() {
        return mIntroduction;
    }

    public void setIntroduction(String introduction) {
        mIntroduction = introduction;
    }

    public String getSamplePath() {
        return mSamplePath;
    }

    public void setSamplePath(String samplePath) {
        mSamplePath = samplePath;
    }

    public String getSampleURL() {
        return mSampleURL;
    }

    public void setSampleURL(String sampleURL) {
        mSampleURL = sampleURL;
    }

    public String getSampleName() {
        return mSampleName;
    }

    public void setSampleName(String sampleName) {
        mSampleName = sampleName;
    }

    public int getAppCategory() {
        return mAppCategory;
    }

    public void setAppCategory(int appCategory) {
        mAppCategory = appCategory;
    }

    public String getAppPath() {
        return mAppPath;
    }

    public void setAppPath(String appPath) {
        mAppPath = appPath;
    }

    public String getReceiverID() {
        return mReceiverID;
    }

    public void setReceiverID(String receiverID) {
        mReceiverID = receiverID;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public int getVxpNum() {
        return mVxpNum;
    }

    public void setVxpNum(int vxpNum) {
        mVxpNum = vxpNum;
    }

    public String getIconURL() {
        return mIconURL;
    }

    public void setIconURL(String iconURL) {
        mIconURL = iconURL;
    }

    public String getIconPath() {
        return mIconPath;
    }

    public void setIconPath(String iconPath) {
        mIconPath = iconPath;
    }

    public String getIconName() {
        return mIconName;
    }

    public void setIconName(String iconName) {
        mIconName = iconName;
    }

    public String getApkPackageName() {
        return mApkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        mApkPackageName = apkPackageName;
    }

    public String getApkURL() {
        return mApkURL;
    }

    public void setApkURL(String apkURL) {
        mApkURL = apkURL;
    }

    // get Apk Name from ApkURL
    public String getApkName() {
        String name = getAppName() + ".apk";
        if (mApkURL != null) {
            name = mApkURL.substring(mApkURL.lastIndexOf('/') + 1);
        }
        return name;
    }

    public String getApkPath() {
        return mApkPath;
    }

    public void setApkPath(String apkPath) {
        mApkPath = apkPath;
    }

    // manage RemoteAppInfo vxp data
    public String getVxpName(int index) {
        return mVxpInfoList.get(index).getVxpName();
    }

    public int getVxpType(int index) {
        return mVxpInfoList.get(index).getType();
    }

    public String getVxpURL(int index) {
        return mVxpInfoList.get(index).getVxpURL();
    }

    public String getVxpPath(int index) {
        return mVxpInfoList.get(index).getVxpPath();
    }

    public void setVxpPath(String vxpPath, int index) {
        mVxpInfoList.get(index).setVxpPath(vxpPath);
    }

    public void addVxpInfo(VxpInfo vxpInfo) {
        mVxpInfoList.add(vxpInfo);
    }

    // RemoteAppInfo manage AppStatus
    public int getAppStatus() {
        return mAppStatus;
    }

    public void setAppStatus(int status) {
        Log.d(TAG, "setAppStatus, app receiverId = " + this.getReceiverID() + ", status = "
                + status + ", mAppStatus = " + mAppStatus);
        if (mAppStatus != status) {
            mAppStatus = status;
            for (AppInfoListener l : mListeners) {
                l.onAppStatusChanged(this);
            }
        }
    }

    public boolean isNeedToUpdate() {
        return mNeedToUpdate;
    }

    public void setNeedToUpdate(boolean isNeed) {
        mNeedToUpdate = isNeed;
    }

    public boolean isInstalled() {
        return mIsInstalled;
    }

    public void setInstalled(boolean installed) {
        mIsInstalled = installed;
    }

    public int getInstalledVxpNum() {
        return mInstalledVxpNum;
    }

    public void setInstalledVxpNum(int vxpNum) {
        mInstalledVxpNum = vxpNum;
    }

    // refresh RemoteAppInfo resource path
    public void refreshAppRes() {
        String iconPath = FileUtils.getRootPath() + getReceiverID() + "/" + getIconName();
        if (FileUtils.isFileExist(iconPath)) {
            Log.d(TAG, "[refreshAppRes] icon isFileExist = " + iconPath);
            mIconPath = iconPath;
        }

        String apkPath = FileUtils.getRootPath() + getReceiverID() + "/" + getApkName();
        if (FileUtils.isFileExist(apkPath)) {
            Log.d(TAG, "[refreshAppRes] apk isFileExist = " + apkPath);
            mApkPath = apkPath;
        }

        String samplePath = FileUtils.getRootPath() + getReceiverID() + "/" + getSampleName();
        if (FileUtils.isFileExist(samplePath)) {
            Log.d(TAG, "[refreshAppRes] sample isFileExist = " + samplePath);
            mSamplePath = samplePath;
        }

        for (int i = 0; i < mVxpInfoList.size(); i++) {
            String vxpPath = FileUtils.getRootPath() + getReceiverID() + "/" + getVxpName(i);
            if (FileUtils.isFileExist(vxpPath)) {
                Log.d(TAG, "[refreshAppRes] vxp isFileExist = " + vxpPath);
                setVxpPath(vxpPath, i);
            }
        }
    }

    // whether RemoteAppInfo is valid
    public boolean isAvailable() {
        return mVxpNum > 0 && !TextUtils.isEmpty(getVxpURL(0));
    }

    // whether RemoteAppInfo app is downloaded
    public boolean isDownload() {
        boolean isDownload = FileUtils.isFileExist(mApkPath);
        if (isDownload) {
            for (int i = 0; i < mVxpInfoList.size(); i++) {
                String vxpPath = getVxpPath(i);
                isDownload = isDownload && FileUtils.isFileExist(vxpPath);
                if (!isDownload) {
                    Log.d(TAG, "[isDownload] isDownload set false: " + vxpPath);
                    break;
                }
            }
        }
        Log.d(TAG, "[isDownload] return " + isDownload);
        return isDownload;
    }

    // manage RemoteAppInfo Listener for AppStoreActivity and AppDetailActivity
    public static HashSet<AppInfoListener> getAppInfoListener() {
        return mListeners;
    }

    public static void addListener(AppInfoListener listener) {
        mListeners.add(listener);
    }

    public static void removeListener(AppInfoListener listener) {
        mListeners.remove(listener);
    }

    // print RemoteAppInfo
    public void printInfo() {
        Log.d(TAG, "[printInfo] " + " mAppCategory: " + mAppCategory + " mAppPath: " + mAppPath
                + "\n mRecieverID: " + mReceiverID + " mAppName: " + mAppName + "\n mVxpNum: "
                + mVxpNum + " mIconName: " + mIconName + " mIconURL: " + mIconURL
                + "\n mApkPackageName: " + mApkPackageName + " mApkURL: " + mApkURL
                + "\n mSampleURL: " + mSampleURL + " mAppSize: " + mAppSize + "\n mVersion: "
                + mVersion + " mProvider: " + mProvider);
        for (int i = 0; i < mVxpInfoList.size(); i++) {
            Log.d(TAG, "[printInfo] " + " VxpName: " + getVxpName(i) + " VxpPath: " + getVxpPath(i)
                    + " VxpType: " + getVxpType(i) + " VxpURL: " + getVxpURL(i));
        }
    }

    public ArrayList<String> getNormalVxpList() {
        Log.d(TAG, "[getNormalVxpList] begin");
        ArrayList<String> vxpSet = new ArrayList<String>();
        for (int i = 0; i < getVxpNum(); i++) {
            if (mVxpInfoList.get(i).getType() == SMARTWATCH_VXP_TYPE_NORMAL) {
                String name = mVxpInfoList.get(i).getVxpName();
                Log.d(TAG, "[getNormalVxpList] name = " + name);
                if (TextUtils.isEmpty(name)) {
                    Log.d(TAG, "[getNormalVxpList] continue");
                    continue;
                }
                if (name.lastIndexOf('.') == -1) {
                    vxpSet.add(name);
                    Log.d(TAG, "[getNormalVxpList] name not '.'");
                    continue;
                }
                int index = name.lastIndexOf('.');
                String new_name = name.substring(0, index);
                Log.d(TAG, "[getNormalVxpList] new_name = " + new_name);
                vxpSet.add(new_name);
            }
        }
        return vxpSet;
    }
}
