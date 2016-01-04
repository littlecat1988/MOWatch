package com.mtk.main;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.mtk.app.notification.AppList;

public class Utils {
    // Debugging
    private static final String TAG = "AppManager/Util";

    // message id, for both notification and SMS
    private static int sMessageId = 0x9000;

    private static int currLCDWidth = 0;

    private static int currLCDHeight = 0;

    /**
     * Return message id, it is unique for all notification or SMS
     * 
     * @return message id
     */
    public static int genMessageId() {
        Log.i(TAG, "genMessageId(), messageId=" + sMessageId);

        return sMessageId++;
    }

    public static String getKeyFromValue(CharSequence charSequence) {
        Map<Object, Object> appList = AppList.getInstance().getAppList();
        Set<?> set = appList.entrySet();
        Iterator<?> it = set.iterator();
        String key = null;
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() != null && entry.getValue().equals(charSequence)) {
                key = entry.getKey().toString();
                break;
            }
        }
        return key;
    }

    /**
     * Returns whether the application is system application.
     * 
     * @param appInfo
     * @return Return true, if the application is system application, otherwise,
     *         return false.
     */
    public static boolean isSystemApp(ApplicationInfo appInfo) {
        boolean isSystemApp = false;
        if (((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                || ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
            isSystemApp = true;
        }

        // Log.i(LOG_TAG, "isSystemApp(), packageInfo.packageName=" +
        // appInfo.packageName
        // + ", isSystemApp=" + isSystemApp);
        return isSystemApp;
    }

    /**
     * Returns whether the mobile phone screen is locked.
     * 
     * @param context
     * @return Return true, if screen is locked, otherwise, return false.
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        Boolean isScreenLocked = km.inKeyguardRestrictedInputMode();

        Log.i(TAG, "isScreenOn(), isScreenOn=" + isScreenLocked);
        return isScreenLocked;
    }

    /**
     * Returns whether the mobile phone screen is currently on.
     * 
     * @param context
     * @return Return true, if screen is on, otherwise, return false.
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Boolean isScreenOn = pm.isScreenOn();

        Log.i(TAG, "isScreenOn(), isScreenOn=" + isScreenOn);
        return isScreenOn;
    }

    /**
     * Lookup contact name from phonebook by phone number.
     * 
     * @param context
     * @param phoneNum
     * @return the contact name
     */
    public static String getContactName(Context context, String phoneNum) {
        // Lookup contactName from phonebook by phoneNum
        if (phoneNum == null) {
            return null;
        } else if (phoneNum.equals("")) {
            return null;
        } else {
            String contactName = phoneNum;
            try {
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(contactName));
                Cursor cursor = context.getContentResolver().query(uri, new String[] {
                    "display_name"
                }, null, null, null);
                if ((cursor != null) && cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                }
                cursor.close();
                Log.i(TAG, "getContactName(), contactName=" + contactName);
                return contactName;
            } catch (Exception e) {
                Log.i(TAG, "getContactName Exception");
                return contactName;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static long getAvailableStore(String filePath) {
        // get sdcard path
        StatFs statFs = new StatFs(filePath);
        // get block SIZE
        long blockSize = statFs.getBlockSize();
        // getBLOCK numbers
        // long totalBlocks = statFs.getBlockCount();
        // get available Blocks
        long availaBlock = statFs.getAvailableBlocks();
        // long total = totalBlocks * blocSize;
        long availableSpace = availaBlock * blockSize;
        return availableSpace / 1024;
    }

    public static boolean isTaskRunning(AsyncTask task) {
        return task != null
                && (task.getStatus() == AsyncTask.Status.PENDING || task.getStatus() == AsyncTask.Status.RUNNING);
    }

    public static void setCurrWidth(int width) {
        currLCDWidth = width;
    }

    public static void setCurrHeight(int height) {
        currLCDHeight = height;
    }

    public static int getCurrWidth() {
        return currLCDWidth;
    }

    public static int getCurrHeight() {
        return currLCDHeight;
    }
}
