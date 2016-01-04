
package com.mtk.app.appstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mtk.app.applist.AppInfo;
import com.mtk.app.applist.AppManager;
import com.mtk.app.appstore.AppResourceManager.DownloadReq;
import com.mtk.app.appstore.RemoteAppInfo.AppInfoListener;
import com.mtk.app.appstore.RemoteAppInfo.RemoteAppStatus;

public class AppStoreManager {

    private static final String TAG = "AppManager/AppStoreManager";

    private static final String PREF_DOWNLOADING_APP = "pref_key_downloading";

    private ArrayList<RemoteAppInfo> mAppList;

    private static AppStoreManager sInstance;

    private String mAbsPath;

    private HashMap<String, RemoteAppInfo> mVxpAppMap = new HashMap<String, RemoteAppInfo>();

    private AppStoreManager() {
        mAppList = new ArrayList<RemoteAppInfo>();
    }

    public static synchronized AppStoreManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppStoreManager();
        }
        return sInstance;
    }

    public int getApplength() {
        return mAppList.size();
    }

    public RemoteAppInfo getAppInfo(int index) {
        return mAppList.get(index);
    }

    public void addAppInfo(RemoteAppInfo appInfo) {
        mAppList.add(appInfo);
    }

    public String getAbsPath() {
        return mAbsPath;
    }

    public void setAbsPath(String absPath) {
        mAbsPath = absPath;
    }

    public RemoteAppInfo getAppInfoByVxp(String vxpName) {
        return mVxpAppMap.get(vxpName);
    }

    public void initVxpMap() {
        mVxpAppMap.clear();
        for (int i = 0; i < getApplength(); i++) {
            RemoteAppInfo appInfo = getAppInfo(i);
            for (int j = 0; j < appInfo.getVxpNum(); j++) {
                mVxpAppMap.put(appInfo.getVxpName(j), appInfo);
            }
        }
    }

    // AppStoreActivity AsyncTask do background Runnable
    public void refreshAppInfo(Context context) {

        Log.d(TAG, "[refreshAppInfo] begin");
        // download and update RemoteAppInfo List
        mAppList.clear();
        InputStream input = DownloadManager.getInstance().getAppListData();
        if (input == null) {
            Log.d(TAG, "[refreshAppInfo] return, input ==null");
            return;
        }
        XMLParser.parseAppList(input);

        // refresh RemoteAppInfo resource path
        for (int i = 0; i < mAppList.size(); i++) {
            mAppList.get(i).refreshAppRes();
        }
        initVxpMap();
        // restore RemoteAppInfo downloading status
        restoreDownloading(context);

        // refresh normal vxp update
        refreshNormalVxp();

        // begin download RemoteAppInfo icon
        refreshIcon();
    }

    public void refreshAppDetail(RemoteAppInfo appInfo) {
        String iconPath = appInfo.getIconPath();
        Log.d(TAG, "[refreshAppDetail] iconPath = " + iconPath);
        if (FileUtils.isFileExist(iconPath)) {
            Log.d(TAG, "[refreshAppDetail] iconPath exist");
        } else {
            AppResourceManager.getInstance().addDownloadFront(DownloadReq.DOWNLOAD_ICON, appInfo);
        }

        String samplePath = appInfo.getSamplePath();
        Log.d(TAG, "[refreshAppDetail] samplePath = " + samplePath);
        if (FileUtils.isFileExist(samplePath)) {
            Log.d(TAG, "[refreshAppDetail] samplePath exist");
        } else {
            AppResourceManager.getInstance().addDownloadFront(DownloadReq.DOWNLOAD_IMAGE, appInfo);
        }
    }

    // AppStoreActivity download APP files
    public void downloadApp(RemoteAppInfo appInfo) {
        if (appInfo != null && appInfo.isDownload()) {
            Log.d(TAG, "[downloadApp] DOWNLOAD_SUCCESSFUL, return");
            return;
        }
        appInfo.setAppStatus(RemoteAppStatus.DOWNLOADING);
        AppResourceManager.getInstance().addDownloadReq(DownloadReq.DOWNLOAD_APP, appInfo);
    }

    public int getDownloadingCount() {
        return AppResourceManager.getInstance().getDownloadingCount();
    }

    // save download status before shutdown
    public void saveDownloadingStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String appSet = new String();
        for (int i = 0; i < mAppList.size(); i++) {
            RemoteAppInfo appInfo = mAppList.get(i);
            if (appInfo.getAppStatus() == RemoteAppStatus.DOWNLOADING) {
                appSet = appSet + appInfo.getReceiverID() + ";";
            }
        }
        Log.d(TAG, "[saveDownloadingStatus] appSet = " + appSet);
        editor.remove(PREF_DOWNLOADING_APP);
        editor.putString(PREF_DOWNLOADING_APP, appSet);
        editor.commit();
    }

    // restore download status and restart
    public void restoreDownloading(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String appSet = sp.getString(PREF_DOWNLOADING_APP, "");
        Log.d(TAG, "[restoreDownloading] appSet = " + appSet);
        String[] strings = appSet.split(";");
        if (strings != null && strings.length > 0) {
            for (String s : strings) {
                for (int i = 0; i < mAppList.size(); i++) {
                    if (s.equals(mAppList.get(i).getReceiverID())) {
                        // set downloading and restart download App
                        Log.d(TAG, "[restoreDownloading] downloadApp : " + s);
                        downloadApp(mAppList.get(i));
                        break;
                    }
                }
            }
        }
    }

    // AppStoreActivity refresh icon
    private void refreshIcon() {
        for (int i = 0; i < mAppList.size(); i++) {
            RemoteAppInfo appInfo = mAppList.get(i);
            if (FileUtils.isFileExist(appInfo.getIconPath())) {
                Log.d(TAG, "[refreshIcon] icon exist: " + appInfo.getIconPath());
                continue;
            }
            AppResourceManager.getInstance().addDownloadReq(DownloadReq.DOWNLOAD_ICON, appInfo);
        }
    }

    // AppStore update feature
    private void refreshNormalVxp() {
        Log.d(TAG, "[refreshNormalVxp] begin");
        for (int i = 0; i < mAppList.size(); i++) {
            for (int j = 0; j < AppManager.getInstance().getApplength(); j++) {
                RemoteAppInfo remoteAppInfo = mAppList.get(i);
                AppInfo appInfo = AppManager.getInstance().getAppInfo(j);
                if (!appInfo.getReceiverId().equals(remoteAppInfo.getReceiverID())) {
                    continue;
                } else {
                    int appVersion = appInfo.getVersionNumber();
                    int remoteAppVersion = remoteAppInfo.getVersionNumber();
                    Log.d(TAG, "[refreshNormalVxp] local version = " + appVersion + " remote = "
                            + remoteAppVersion);
                    if (appVersion > 0 && appVersion < remoteAppVersion) {
                        remoteAppInfo.setNeedToUpdate(true);
                    }
                }
            }
        }
    }

    public void updateApp(RemoteAppInfo remoteAppInfo) {
        Log.d(TAG, "[updateApp] begin");
        if (remoteAppInfo == null) {
            Log.d(TAG, "[updateApp] return");
            return;
        }

        String folderPath = FileUtils.getRootPath() + remoteAppInfo.getAppPath();
        File folder = new File(folderPath);
        Log.d(TAG, "[updateApp] folderPath = " + folderPath);

        if (folder != null && folder.isDirectory() && folder.exists()) {
            FileUtils.deleteAppFile(folderPath);
        } else {
            Log.d(TAG, "[updateApp] needn't delete app");
        }

        downloadApp(remoteAppInfo);
        // whether install
        // if local version >= watch version (watch installed), install
        // else nothing
    }

    public AppInfo getAppInfoByName(String name) {
        return null;
    }

    public RemoteAppInfo getRemoteAppInfofoByName(String name) {
        return null;
    }
}
