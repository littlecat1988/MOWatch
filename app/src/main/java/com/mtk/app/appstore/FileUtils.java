
package com.mtk.app.appstore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mtk.app.applist.AppManager;

public class FileUtils {

    private static final String TAG = "AppManager/FileUtils";

    private static final int FILE_BUFFER = 4 * 1024;

    private static final String PACKAGE_NAME = ".com.mtk.btnotification";

    private static File ROOTDIR = createRootDir();

    private static File createRootDir() {
        if (ROOTDIR == null || !ROOTDIR.exists()) {
            String fileRoot = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                fileRoot = Environment.getExternalStorageDirectory() + "/" + PACKAGE_NAME;
            } else {
                fileRoot = Environment.getRootDirectory() + "/" + PACKAGE_NAME;
            }
            ROOTDIR = new File(fileRoot);
            if (!ROOTDIR.exists()) {
                ROOTDIR.mkdirs();
            }
        }
        return ROOTDIR;
    }

    private static String getAppManagerPath() {
        String fileRoot = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileRoot = Environment.getExternalStorageDirectory() + AppManager.APP_MANAGER_FILE_DIR;
        } else {
            fileRoot = Environment.getRootDirectory() + AppManager.APP_MANAGER_FILE_DIR;
        }

        File dir = new File(fileRoot);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }

    private static File createSDAppDir(String appDirName) {
        File dir = new File(appDirName);
        Log.d(TAG, "[createSDAppDir] dir = " + appDirName);
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        } else {
            boolean success = dir.mkdir();
            if (success) {
                return dir;
            }
        }
        return null;
    }

    private static File createSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.d(TAG, "[createSDFile] IOException = " + e.getMessage());
            return null;
        }
        return file;
    }

    public static String getRootPath() {
        return ROOTDIR.getAbsolutePath() + "/";
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return true;
        }
        return false;
    }

    public static File writeFileFromNet(String appPath, String fileName, InputStream inputStream) {
        File file = null;
        OutputStream output = null;
        try {
            createSDAppDir(getRootPath() + appPath);
            file = createSDFile(getRootPath() + appPath + fileName);

            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILE_BUFFER];
            int length = 0;
            while ((length = (inputStream.read(buffer))) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } catch (Exception e) {
            Log.d(TAG, "[writeFileFromNet] Exception = " + e.getMessage());
            if (file != null && file.isFile() && file.exists()) {
                Log.d(TAG, "[writeFileFromNet] Exception delete " + file.getPath());
                file.delete();
            }
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getAppConfigFile(String appName) {
        return getAppManagerPath() + "/" + appName + "cfg.xml";
    }

    public static String getAppManagerFile(String apkName) {
        return getAppManagerPath() + "/" + apkName;
    }

    public static void copyFile(String sourceFile, String targetFile) {
        Log.d(TAG, "[copyFile] " + sourceFile + " to " + targetFile);
        File target = new File(targetFile);
        if (target != null && target.isFile() && target.exists() && target.length() > 0) {
            Log.d(TAG, "[copyFile] old exist file = " + targetFile);
            if (target.delete()) {
                Log.d(TAG, "[copyFile] delete successfully");
            } else {
                Log.d(TAG, "[copyFile] delete fail");
            }
        }

        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buf = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(buf)) != -1) {
                outBuff.write(buf, 0, len);
            }
            outBuff.flush();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "[copyFile] FileNotFoundException = " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "[copyFile] IOException = " + e.getMessage());
        } finally {
            try {
                if (inBuff != null) {
                    inBuff.close();
                }
                if (outBuff != null) {
                    outBuff.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteAppFile(String appFolder) {
        Log.d(TAG, "[deleteAppFile] folder = " + appFolder);
        File folder = new File(appFolder);
        if (folder == null || !folder.exists() || folder.isFile()) {
            Log.d(TAG, "[deleteAppFile] folder return");
            return;
        }
        File[] files = folder.listFiles();
        if (files == null || files.length <= 0) {
            Log.d(TAG, "[deleteAppFile] files return");
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Log.d(TAG, "[deleteAppFile] file = " + file.getAbsolutePath());
            String fileName = file.getName().toLowerCase();
            if (file.isFile()
                    && (fileName.endsWith("vxp") || fileName.endsWith("vtp") || fileName
                            .endsWith("apk"))) {
                Log.d(TAG, "[deleteAppFile] delete " + file.delete());
            }
        }
    }
}
