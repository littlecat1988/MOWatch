package com.mtk.bluetoothle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlertSettingReadWriter {
    public static boolean getSwtichPreferenceEnabled(Context context, String key, boolean defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean result = prefs.getBoolean(key, defValue);
        return result;
    }

    public static void setSwtichPreferenceEnabled(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getRangePreferenceStatus(Context context, String key, int defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int result = Integer.parseInt(prefs.getString(key, String.valueOf(defValue)));
        return result;
    }

    public static void setRangePreferenceStatus(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, String.valueOf(value));
        editor.apply();
    }
}
