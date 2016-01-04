package com.mtk.app.fota;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import com.gmobi.fota.GmFotaService;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FotaUtils {

    private static final String TAG = "[FOTA_UPDATE][FotaUtils]";

    // // M : update UX message begin
    public static final int MSG_UPDATE_TEXT_VIEW = 1;

    public static final int MSG_ARG1_DOWNLOAD_FINISHED = 1;
    public static final int MSG_ARG1_UPDATE_FINISHED = 2;
    public static final int MSG_ARG1_UPDATE_FAILED_CAUSE_DISCONNECTED = 3;
    public static final int MSG_ARG1_UNZIP_IMAGE_FINISHED = 4;
    public static final int MSG_ARG1_UPDATING_VIA_USB = 5;
    public static final int MSG_ARG1_DOWNLOAD_FAILED = 6;
    public static final int MSG_ARG1_NO_CFG_FOUND_EXCEPTION = 7;
    // // M : update UX message end

    // // M : update via bt signals begin
    // / M : send bin via bt success
    public static final int FOTA_SEND_VIA_BT_SUCCESS = 2;
    // update via bin success
    public static final int FOTA_UPDATE_VIA_BT_SUCCESS = 3;

    // update via bt errors
    public static final int FOTA_UPDATE_VIA_BT_COMMON_ERROR = -1;
    // FP write file failed
    public static final int FOTA_UPDATE_VIA_BT_WRITE_FILE_FAILED = -2;
    // FP disk full error
    public static final int FOTA_UPDATE_VIA_BT_DISK_FULL = -3;
    // FP data transfer failed
    public static final int FOTA_UPDATE_VIA_BT_DATA_TRANSFER_ERROR = -4;
    // FP update Fota trigger failed
    public static final int FOTA_UPDATE_VIA_BT_TRIGGER_FAILED = -5;
    // FP update fot failed
    public static final int FOTA_UPDATE_VIA_BT_FAILED = -6;
    // FP trigger failed cause of low battery
    public static final int FOTA_UPDATE_TRIGGER_FAILED_CAUSE_LOW_BATTERY = -7;
    // get FP version failed
    public static final String FOTA_VERSION_GET_FAILED = "-8";
    // //// M : update via bt signals end
    
    /**
     * 
     */
    public static final int FILE_NOT_FOUND_ERROR = -100;
    public static final int READ_FILE_FAILED = -101;

    // / M : 3 methods to update firmware
    public static final String INTENT_EXTRA_INFO = "firmware_way";
    public static final String INTENT_EXTRA_MODEL = "intent_model";
    public static final String INTENT_EXTRA_VERSION = "intent_version";
    public static final String INTENT_EXTRA_DEV_ID = "intent_dev_id";
    public static final String ZIP_FILE_PATH = "zip_file_path";
    
    public static final int FIRMWARE_REDBEND_FOTA = 0;
    public static final int FIRMWARE_UBIN = 1;
    public static final int FIRMWARE_VIA_USB = 2;
    public static final int FIRMWARE_VIA_USB_FILE_MANAGER = 3;
    public static final int FIRMWARE_ROCK_FOTA = 4;
    public static final int FIRMWARE_FULL_BIN = 5;
    // // M

    // // M : FOTA preference begin
    public static final String FOTA_UPDATE_PREFERENCE_FILE_NAME = "fota_update";
    public static final String FOTA_UPDATE_SENDING_FLAG_STRING = "sending";
    public static final String FOTA_UPDATE_STATUS_FLAG_STRING = "update_status";
    public static final String FOTA_BT_UPDATING_STATUS = "bt_updating";
    public static final String FOTA_BT_MODEL_STRING = "model";
    public static final String FOTA_BT_VERSION_STRING = "version";
    public static final String FOTA_BT_DEV_ID_STRING = "devId";
    public static final String UPDATE_FINISHED_CALLED = "should_call";
    // // M FOTA preference end

    /// M : FOTA type list begin
    public static final int FOTA_TYPE_DIFF_FOTA = 1;
    public static final int FOTA_TYPE_SEPERATE_BIN_FOTA = 2;
    public static final int FOTA_TYPE_USB_FOTA = 4;
    public static final int FOTA_TYPE_FULL_BIN_FOTA = 8;
    public static final int FOTA_TYPE_ROCK_UPDATE = 16;
    /// M : FOTA type list end
    
    // / redbend fota default value which used to update the result to server
    public static final String REDBEND_FOTA_DEFAULT_VALUES = "00000";
    // /

    public static final int UPDATE_REDBEND_FINISHED = 100;

    private static final String CFG_FILE_EXTENDS_NAME = "cfg";

    private static File sFile;

    private static Context mContext;

    public static String getDownloadPath(Context context) {
        String folderPath;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            folderPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            folderPath = context.getFilesDir().getAbsolutePath();
        }
        return folderPath;
    }

    public static void updateUpdatingStatus(Context context, boolean b,
            String model, String version, String devId) {
        SharedPreferences preference = context.getSharedPreferences(
                FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(FOTA_BT_UPDATING_STATUS, b);
        editor.putString(FOTA_BT_MODEL_STRING, model);
        editor.putString(FOTA_BT_VERSION_STRING, version);
        editor.putString(FOTA_BT_DEV_ID_STRING, devId);
        editor.commit();
    }

    public static boolean getUpdatingStatus(Context context) {
        SharedPreferences preference = context.getSharedPreferences(
                FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        boolean b = preference.getBoolean(FOTA_BT_UPDATING_STATUS, false);
        return b;
    }

    public static void clearPreference(Context context) {
        SharedPreferences preference = context.getSharedPreferences(
                FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.commit();
    }

    public static void updateFinished(Context context, boolean b) {
        if (mContext == null) {
            mContext = context;
        }
        Log.d(TAG, "[updateFinished] b : " + b);

        SharedPreferences preference = context.getSharedPreferences(
                FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(UPDATE_FINISHED_CALLED, b);
        editor.commit();

        try {
            throw new IllegalArgumentException();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void callUpdateFinished(Context context, Handler handler,
            boolean b) {
        if (mContext == null) {
            mContext = context;
        }
        SharedPreferences preference = context.getSharedPreferences(
                FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        boolean shouldCall = preference.getBoolean(UPDATE_FINISHED_CALLED,
                false);
        Log.d(TAG, "[callUpdateFinished] shouldCall : " + shouldCall + ", b : "
                + b);
        boolean exist = GmFotaService.resultFileExists(context);
        Log.d(TAG, "[callUpdateFinished] exist file or not : " + exist);
        if (shouldCall || exist) {
            updateFinished(mContext, false);
            if (handler != null) {
                Message msg = handler.obtainMessage();
                msg.what = UPDATE_REDBEND_FINISHED;
                msg.obj = b;
                handler.sendMessage(msg);
            } else {
                Log.e(TAG, "[callUpdateFinished] handler is null");
            }
        }
    }

    public static String getPreferenceString(Context context, String which) {
        if (context == null) {
            return null;
        }
        SharedPreferences preference = context.getSharedPreferences(
                FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        if (which.equals(FOTA_BT_MODEL_STRING)) {
            return preference.getString(FOTA_BT_MODEL_STRING, null);
        } else if (which.equals(FOTA_BT_VERSION_STRING)) {
            return preference.getString(FOTA_BT_VERSION_STRING, null);
        } else if (which.equals(FOTA_BT_DEV_ID_STRING)) {
            return preference.getString(FOTA_BT_DEV_ID_STRING, null);
        }
        return null;
    }

    /**
     * unZip the zip file to get the cfg file in zip file
     * 
     * @param context
     *            the application context
     * @param zipFile
     *            the zip file name
     * @return
     */
    public static File getCfgFile(Context context, File zipFile, int which) {
        if (context == null || zipFile == null) {
            throw new IllegalArgumentException("PARAMETER IS NULL");
        }
        sFile = null;
        String folderPath = getDownloadPath(context);
        if (which == FotaUtils.FIRMWARE_VIA_USB) {
            folderPath = folderPath + "/fota";
        } else if (which == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
            folderPath = folderPath + "/fota1";
        }
        Log.d(TAG, "[getCfgFile] folderPath : " + folderPath);
        unZipFile(zipFile, folderPath);

        File rootFile = new File(folderPath);
        if (!rootFile.isDirectory()) {
            Log.d(TAG, "[getCfgFile] the rootFile is not a directory");
            return null;
        }
        getFile(rootFile);
        return sFile;
    }

    private static void getFile(File file) {
        if (file == null) {
            Log.e(TAG, "[getFile] file is null");
            return;
        }
        Log.d(TAG, "[getFile] enter +++ , file name : " + file.getName());
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                String fileName = f.getName();
                int fileNameLength = fileName.length();
                int lastIndexOfDot = fileName.lastIndexOf('.');
                String extension = fileName.substring(lastIndexOfDot + 1,
                        fileNameLength);
                Log.d(TAG, "[getFile] f : " + f.getName());
                Log.d(TAG, "[getFile] extension  : " + extension);
                if (extension.equals(CFG_FILE_EXTENDS_NAME)) {
                    Log.d(TAG, "[getFile] f name : " + f.getName());
                    sFile = f;
                    // return f;
                }
            } else if (f.isDirectory()) {
                getFile(f);
            }
        }
    }

    public static void deleteUnzipFiles(Context context, int which) {
        String folderPath = getDownloadPath(context);
        if (which == FotaUtils.FIRMWARE_VIA_USB) {
            folderPath = folderPath + "/fota";
        } else if (which == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
            folderPath = folderPath + "/fota1";
        }
        File f = new File(folderPath);
        deleteFile(f);
    }

    private static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                // final File to = new File(f.getAbsolutePath() +
                // System.currentTimeMillis());
                // f.renameTo(to);
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f);
                }
            }
        }
        file.delete();
    }

    private static void unZipFile(File zipFile, String folderPath) {
        if (zipFile == null) {
            throw new IllegalArgumentException("zipFile is null");
        }
        if (folderPath == null) {
            throw new IllegalArgumentException("folderPath is null");
        }
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdir();
        }
        Log.d(TAG, "[unZipFile] zipFile path : " + zipFile.getAbsolutePath());
        Log.d(TAG, "[unZipFile] folderPath : " + folderPath);
        ZipInputStream zis = null;
        FileOutputStream fos = null;
        try {
            ZipEntry entry = null;
            String entryName = null;
            zis = new ZipInputStream(new FileInputStream(zipFile));
            while ((entry = zis.getNextEntry()) != null) {
                Log.d(TAG, "[unZipFile] begin to unzip files");
                entryName = entry.getName();
                if (entry.isDirectory()) {
                    File folder = new File(folderPath + File.separator
                            + entryName);
                    folder.mkdirs();
                } else {
                    File file = new File(folderPath + File.separator
                            + entryName);
                    File parent = file.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[5 * 1024];
                    int len = 0;
                    while ((len = zis.read(buffer)) > -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String convertInputStreamToString(InputStream input) {
        if (input == null) {
            Log.e(TAG, "[convertInputStreamToString] input stream is null");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            
        }
        return sb.toString();
    }

}
