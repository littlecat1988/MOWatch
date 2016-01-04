
package com.mtk.app.appstore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;

import com.mtk.app.appstore.RemoteAppInfo.AppInfoListener;
import com.mtk.app.appstore.RemoteAppInfo.RemoteAppStatus;

import android.util.Log;

public class AppResourceManager {

    private static final String TAG = "AppManager/AppResourceManager";

    private static AppResourceManager sInstance;

    private Vector<DownloadReq> mDownloadImageReqs = new Vector<DownloadReq>();

    private Vector<DownloadReq> mDownloadAppReqs = new Vector<DownloadReq>();

    private DownloadThread mDownloadImageThread;

    private DownloadThread mDownloadAppThread;

    private AppResourceManager() {
    }

    public static final int MAX_DOWNLOAD_LIMIT = 10;

    public static synchronized AppResourceManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppResourceManager();
        }
        return sInstance;
    }

    public int getDownloadingCount() {
        return mDownloadAppReqs.size();
    }

    public void addDownloadReq(int type, RemoteAppInfo appInfo) {
        Log.d(TAG, "[addDownloadReq] type = " + type + " AppInfo = " + appInfo.getReceiverID());
        DownloadReq req = new DownloadReq(type, appInfo);
        if (type == DownloadReq.DOWNLOAD_ICON) {
            mDownloadImageReqs.add(req);
        } else if (type == DownloadReq.DOWNLOAD_APP) {
            mDownloadAppReqs.add(req);
        }
        execute();
    }

    public void addDownloadFront(int type, RemoteAppInfo appInfo) {
        Log.d(TAG, "[addDownloadFront] type = " + type + " AppInfo = " + appInfo.getReceiverID());
        DownloadReq req = new DownloadReq(type, appInfo);
        mDownloadImageReqs.add(0, req);
        execute();
    }

    public static class DownloadReq {

        public static final int DOWNLOAD_ICON = 0;

        public static final int DOWNLOAD_APP = 1;

        public static final int DOWNLOAD_IMAGE = 2;

        private int mType;

        private RemoteAppInfo mAppInfo;

        public DownloadReq(int type, RemoteAppInfo appInfo) {
            mType = type;
            mAppInfo = appInfo;
        }

        public void executeDownload() {
            Log.d(TAG, "executeDownload begin");
            HashSet<AppInfoListener> listeners = RemoteAppInfo.getAppInfoListener();
            if (mType == DOWNLOAD_ICON) {
                String URL = mAppInfo.getIconURL();
                String iconPath = AppResourceManager.getInstance().downloadFile(URL,
                        mAppInfo.getAppPath(), mAppInfo.getIconName());
                if (FileUtils.isFileExist(iconPath)) {
                    mAppInfo.setIconPath(iconPath);
                    AppResourceManager.getInstance().syncIconToAppManager(mAppInfo);
                    for (AppInfoListener listener : listeners) {
                        listener.onAppInfoChanged(mAppInfo);
                    }
                }
            } else if (mType == DOWNLOAD_APP) {
                // Download VXP files
                for (int i = 0; i < mAppInfo.getVxpNum(); i++) {
                    String URL = mAppInfo.getVxpURL(i);
                    String vxpPath = AppResourceManager.getInstance().downloadFile(URL,
                            mAppInfo.getAppPath(), mAppInfo.getVxpName(i));
                    if (FileUtils.isFileExist(vxpPath)) {
                        mAppInfo.setVxpPath(vxpPath, i);
                    }
                }
                // Download APK file
                String URL = mAppInfo.getApkURL();
                String apkPath = AppResourceManager.getInstance().downloadFile(URL,
                        mAppInfo.getAppPath(), mAppInfo.getApkName());
                if (FileUtils.isFileExist(apkPath)) {
                    mAppInfo.setApkPath(apkPath);
                }
                // update download status, onAppInfoChanged
                if (mAppInfo.isDownload()) {
                    mAppInfo.setAppStatus(RemoteAppStatus.DOWNLOAD_SUCCESSFUL);
                    AppResourceManager.getInstance().syncToAppManager(mAppInfo);
                } else {
                    Log.d(TAG, "[DownloadThread] DOWNLOAD_FAILED");
                    mAppInfo.setAppStatus(RemoteAppStatus.DOWNLOAD_FAILED);
                }
            } else if (mType == DOWNLOAD_IMAGE) {
                String URL = mAppInfo.getSampleURL();
                String samplePath = AppResourceManager.getInstance().downloadFile(URL,
                        mAppInfo.getAppPath(), mAppInfo.getSampleName());
                if (FileUtils.isFileExist(samplePath)) {
                    mAppInfo.setSamplePath(samplePath);
                    for (AppInfoListener listener : listeners) {
                        listener.onAppInfoChanged(mAppInfo);
                    }
                }
            }
        }
    };

    private class DownloadThread extends Thread {

        private Vector<DownloadReq> mDownloadReqs;

        private Boolean mIsDownloadImage;

        public DownloadThread(Vector<DownloadReq> downloadReqs, boolean isIcon) {
            mDownloadReqs = downloadReqs;
            mIsDownloadImage = isIcon;
            this.setName("DownloadThread.execute");
        }

        public void run() {
            boolean tobeStopped = false;
            while (!tobeStopped) {
                DownloadReq req = mDownloadReqs.get(0);
                Log.d(TAG, "[DownloadThread] get size = " + mDownloadReqs.size());
                if (req == null) {
                    break;
                }

                req.executeDownload();

                mDownloadReqs.remove(req);
                Log.d(TAG, "[DownloadThread] remove size = " + mDownloadReqs.size());
                if (mDownloadReqs.isEmpty()) {
                    if (mIsDownloadImage) {
                        mDownloadImageThread = null;
                    } else {
                        mDownloadAppThread = null;
                    }
                    tobeStopped = true;
                    Log.d(TAG, "[DownloadThread] end, tobeStopped = true");
                }
            }
        }
    }

    private void execute() {
        if (mDownloadImageThread == null && mDownloadImageReqs.size() == 1) {
            mDownloadImageThread = new DownloadThread(mDownloadImageReqs, true);
            mDownloadImageThread.start();
            Log.d(TAG, "[execute] mDownloadImageThread execute");
        }
        if (mDownloadAppThread == null && mDownloadAppReqs.size() == 1) {
            mDownloadAppThread = new DownloadThread(mDownloadAppReqs, false);
            mDownloadAppThread.start();
            Log.d(TAG, "[execute] mDownloadAppThread execute");
        }
    }

    private String downloadFile(String url, String appPath, String fileName) {
        Log.d(TAG, "[downloadFile] " + fileName + " appPath = " + appPath + " url = " + url);
        InputStream inputStream = null;
        File resultFile = null;
        try {
            String filePath = FileUtils.getRootPath() + appPath + fileName;
            if (FileUtils.isFileExist(filePath)) {
                Log.d(TAG, "[downloadFile] File exist = " + filePath);
                return filePath;
            } else {
                inputStream = HttpHelper.getInputStreamFromURL(url);
                if (inputStream == null) {
                    Log.d(TAG, "[downloadFile] http error, inputStream == null");
                    return null;
                }
                resultFile = FileUtils.writeFileFromNet(appPath, fileName, inputStream);
                if (resultFile == null || !resultFile.exists()) {
                    Log.d(TAG, "[downloadFile] resultFile == null || !resultFile.exists");
                    return null;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "[downloadFile] Exception = " + e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "[downloadFile] downloadFile end = " + resultFile.getPath());
        return resultFile.getPath();
    }

    private void syncToAppManager(RemoteAppInfo appInfo) {
        if (!appInfo.isAvailable() || !appInfo.isDownload()) {
            Log.d(TAG, "[syncToAppManager] return");
            return;
        }
        XMLParser.writeAppConfigXml(appInfo);

        for (int i = 0; i < appInfo.getVxpNum(); i++) {
            Log.d(TAG, "[syncToAppManager] VxpName = " + appInfo.getVxpName(i));
            String vxptFile = FileUtils.getAppManagerFile(appInfo.getVxpName(i));
            FileUtils.copyFile(appInfo.getVxpPath(i), vxptFile);
        }
        Log.d(TAG, "[syncToAppManager] ApkName = " + appInfo.getApkName());
        String apkFile = FileUtils.getAppManagerFile(appInfo.getApkName());
        FileUtils.copyFile(appInfo.getApkPath(), apkFile);
    }

    private void syncIconToAppManager(RemoteAppInfo appInfo) {
        Log.d(TAG, "[syncIconToAppManager] IconName = " + appInfo.getIconName());
        String iconFile = FileUtils.getAppManagerFile(appInfo.getIconName());
        FileUtils.copyFile(appInfo.getIconPath(), iconFile);
    }
}
