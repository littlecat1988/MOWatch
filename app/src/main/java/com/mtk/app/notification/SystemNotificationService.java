
package com.mtk.app.notification;

import java.util.HashSet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.util.Log;

import com.mediatek.ctrl.notification.NotificationController;
import com.mtk.btnotification.R;
import com.mtk.main.Utils;

/**
 * This class will receive and process all new LowBattery
 */
public class SystemNotificationService extends BroadcastReceiver {
    // Debugging
    private static final String TAG = "AppManager/SystemNoti";

    // Received parameters
    private Context mContext = null;

    private static float mBatteryCapacity = 0;

    private static float mLastBatteryCapacity = 0;

    public SystemNotificationService() {
        Log.i(TAG, "SystemNotificationService(), SystemNotificationService created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.i(LOG_TAG, "onReceive()");
        mContext = context;
        String intentAction = intent.getAction();
        if (Intent.ACTION_BATTERY_LOW.equalsIgnoreCase(intentAction)) {
//            Log.i(TAG, "mLastBatteryCapacity = " + mLastBatteryCapacity);
//            Log.i(TAG, "mBatteryCapacity = " + mBatteryCapacity);
            String title = mContext.getResources().getString(R.string.batterylow);
            String content = mContext.getResources().getString(R.string.pleaseconnectcharger);
            String appID = Utils.getKeyFromValue(AppList.BATTERYLOW_APPID);
            HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
            if (mLastBatteryCapacity == 0) {
                Log.i(TAG, "mLastBatteryCapacity = 0");
                if (!blockList.contains(AppList.BATTERYLOW_APPID)) {
                    NotificationController.getInstance(mContext).sendLowBatteryMessage(title,
                            content, appID, String.valueOf((int)(mBatteryCapacity * 100)));
                }
                mLastBatteryCapacity = mBatteryCapacity;
            } else {
                if (mLastBatteryCapacity != mBatteryCapacity) {
                    if (!blockList.contains(AppList.BATTERYLOW_APPID)) {
                        NotificationController.getInstance(mContext).sendLowBatteryMessage(title,
                                content, appID, String.valueOf((int) (mBatteryCapacity * 100)));
                    }
                    mLastBatteryCapacity = mBatteryCapacity;
                }
            }

        } else if (Intent.ACTION_BATTERY_CHANGED.equalsIgnoreCase(intentAction)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale;
//            Log.i(TAG, "Battery level scale and pct is " + level +", " + scale);
//            Log.i(TAG, "BatteryCapacity = " + batteryPct);
            mBatteryCapacity = batteryPct;
        } else if (Intent.ACTION_POWER_CONNECTED.equalsIgnoreCase(intentAction)) {
            mLastBatteryCapacity = 0;
        }

    }
}
