
package com.mtk.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This class is the static utility class to get preference data.
 */
public final class PreferenceData {
    // Debugging
    private static final String TAG = "AppManager/PreferenceData";

    // Preference Keys
    public static final String PREFERENCE_KEY_APP_INFO = "app_info";

    public static final String PREFERENCE_KEY_SMS = "enable_sms_service_preference";

    public static final String PREFERENCE_KEY_NOTIFI = "enable_notifi_service_preference";

    public static final String PREFERENCE_KEY_CALL = "enable_call_service_preference";

    public static final String PREFERENCE_KEY_ACCESSIBILITY = "show_accessibility_menu_preference";

    public static final String PREFERENCE_KEY_SELECT_NOTIFICATIONS = "select_notifi_preference";

    public static final String PREFERENCE_KEY_SELECT_BLOCKS = "select_blocks_preference";

    public static final String PREFERENCE_KEY_SHOW_CONNECTION_STATUS = "show_connection_status_preference";

    public static final String PREFERENCE_KEY_ALWAYS_FORWARD = "always_forward_preference";

    public static final String PREFERENCE_KEY_CURRENT_VERSION = "current_version_preference";

    private static final Context sContext = BTNotificationApplication.getInstance()
            .getApplicationContext();

    private static final SharedPreferences sSharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(sContext);

    /**
     * Return whether the enable_sms_service_preference is checked.
     */
    public static boolean isSmsServiceEnable() {
        boolean isEnable = sSharedPreferences.getBoolean(PREFERENCE_KEY_SMS, true);

        Log.i(TAG, "isSmsServiceEnable(), isEnable=" + isEnable);
        return isEnable;
    }

    /**
     * Return whether the enable_notifi_service_preference is checked.
     */
    public static boolean isNotificationServiceEnable() {
        boolean isEnable = sSharedPreferences.getBoolean(PREFERENCE_KEY_NOTIFI, true);

        Log.i(TAG, "isNotifiServiceEnable(), isEnable=" + isEnable);
        return isEnable;
    }

    /**
     * Return whether the enable_call_service_preference is checked.
     */
    public static boolean isCallServiceEnable() {
        boolean isEnable = sSharedPreferences.getBoolean(PREFERENCE_KEY_CALL, true);

        Log.i(TAG, "isCallServiceEnable(), isEnable=" + isEnable);
        return isEnable;
    }

    /**
     * Return whether the show_accessibility_menu_preference is checked.
     */
    public static boolean isShowConnectionStatus() {
        boolean isShow = sSharedPreferences.getBoolean(PREFERENCE_KEY_SHOW_CONNECTION_STATUS, true);

        Log.i(TAG, "isShowConnectionStatus(), isShow=" + isShow);
        return isShow;
    }

    /**
     * Return whether the always_forward_preference is checked.
     */
    private static boolean isAlwaysForward() {
        boolean isAlways = sSharedPreferences.getBoolean(PREFERENCE_KEY_ALWAYS_FORWARD, true);

        Log.i(TAG, "isAlwaysForward(), isAlways=" + isAlways);
        return isAlways;
    }

    /**
     * Return whether need to push message to remote device. Push message if
     * always forward preference is enable or phone screen is locked.
     */
    public static boolean isNeedPush() {
        boolean needPush = (PreferenceData.isAlwaysForward() || Utils.isScreenLocked(sContext));

        Log.i(TAG, "isNeedForward(), needPush=" + needPush);
        return needPush;
    }

}
