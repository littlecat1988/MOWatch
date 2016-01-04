package com.mtk.bluetoothle;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.leprofiles.bas.BatteryChangeListener;
import com.mediatek.leprofiles.fmppxp.CalibrateListener;
import com.mediatek.leprofiles.fmppxp.PxpEventProcessor;
import com.mediatek.leprofiles.hr.HRListener;
import com.mediatek.leprofiles.pdms.PDMSListener;
import com.mtk.btnotification.R;

public class LocalPxpFmpController {

    // custimizable values
    private static final int RANGE_ALERT_THRESH_NEAR = 80;//modified by lixiang for finding watch function ---70 20150609 
    private static final int RANGE_ALERT_THRESH_FAR = 95;//modified by lixiang for finding watch function ---90 20150609 
    private static final int RSSI_TOLERANCE_NEAR = 3;
    private static final int RSSI_TOLERANCE_FAR = 5;
    private static final int READ_RSSI_DEALY = 500;

    // battery level threshold
    public static final int BATTERY_LEVEL_1 = 33;
    public static final int BATTERY_LEVEL_2 = 66;
    public static final int BATTERY_LEVEL_3 = 100;
    public static void initPxpFmpFunctions(final Context context) {
        int profiles = context.getResources().getInteger(R.integer.supported_gatt_profiles);
        LocalBluetoothLEManager.getInstance().init(context, profiles);
        updatePxpParams(context);
        // This is an example for customize the rssi processor for PXP feature
        PxpEventProcessor testProcessor = new PxpEventProcessor() {
            int mTxpower = 0;
            private static final String TAG = "TEST_PROCESSOR";
            @Override
            public void onTxPowerRead(int txPower) {
                Log.d(TAG, "onTxPowerRead: " + txPower);
                mTxpower = txPower;
            }

            @Override
            public void onReadRssi(int rssi) {
                Log.d(TAG, "onReadRssi: " + rssi);
                if (mTxpower - rssi < RANGE_ALERT_THRESH_NEAR) {
                    Log.d(TAG, "normal");
                    LocalBluetoothLEManager.getInstance().notifyPxpAlertChanged(
                            BlePxpFmpConstants.STATE_NO_ALERT);
                } else {
                    Log.d(TAG, "out range");
                    LocalBluetoothLEManager.getInstance().notifyPxpAlertChanged(
                            BlePxpFmpConstants.STATE_OUT_RANGE_ALERT);
                }
            }
        };
        /*
         * This is an example for customize the rssi processor for PXP feature
        LocalBluetoothLEManager.getInstance().setCustomerPxpEventProcessor(testProcessor);
        */
    }

    public static void updatePxpParams(Context context) {
        boolean alertEnable = AlertSettingReadWriter.getSwtichPreferenceEnabled(context,
                AlertSettingPreference.ALERT_ENABLER_PREFERENCE,
                AlertSettingPreference.DEFAULT_ALERT_ENABLE);
        boolean rangeAlertEnable = AlertSettingReadWriter.getSwtichPreferenceEnabled(context,
                AlertSettingPreference.RANGE_ALERT_CHECK_PREFERENCE,
                AlertSettingPreference.DEFAULT_RANGE_ALERT_ENABLE);
        int rangeType = AlertSettingReadWriter.getRangePreferenceStatus(context,
                AlertSettingPreference.RANGE_TYPE_PREFERENCE,
                AlertSettingPreference.DEFAULT_RANGE_TYPE);
        int rangeSizeType = AlertSettingReadWriter.getRangePreferenceStatus(context,
                AlertSettingPreference.RANGE_SIZE_PREFERENCE,
                AlertSettingPreference.DEFAULT_RANGE_SIZE);
        boolean disconAlertEnable = AlertSettingReadWriter.getSwtichPreferenceEnabled(context,
                AlertSettingPreference.DISCONNECT_WARNING_PREFERENCE,
                AlertSettingPreference.DEFAULT_DISCONNECT_WARNING_ENABLE);

        String threshouldKey = AlertSettingPreference.RANGE_CALIBRATED_THRESHOLD_PREFERENCE
                + rangeSizeType;
        String toleranceKey = AlertSettingPreference.RANGE_CALIBRATED_TOLERANCE_PREFERENCE
                + rangeSizeType;
        int rangeSize = AlertSettingReadWriter.getRangePreferenceStatus(context, threshouldKey, 0);
        int tolerance = AlertSettingReadWriter.getRangePreferenceStatus(context, toleranceKey, -1);
        if (rangeSize == 0) {
            if (rangeSizeType == AlertSettingPreference.RANGE_SIZE_NEAR) {
                rangeSize = RANGE_ALERT_THRESH_NEAR;
            } else {
                rangeSize = RANGE_ALERT_THRESH_FAR;
            }
        }
        if (tolerance < 0) {
            if (rangeSize == AlertSettingPreference.RANGE_SIZE_NEAR) {
                tolerance = RSSI_TOLERANCE_NEAR;
            } else {
                tolerance = RSSI_TOLERANCE_FAR;
            }
        }
        LocalBluetoothLEManager.getInstance().updatePxpParams(alertEnable, rangeAlertEnable,
                rangeType, rangeSize, disconAlertEnable, tolerance, READ_RSSI_DEALY);
    }

    public static void findTargetDevice(int level) {
        LocalBluetoothLEManager.getInstance().findTargetDevice(level);
    }

    public static void stopRemotePxpAlert(BluetoothDevice device) {
        LocalBluetoothLEManager.getInstance().stopRemotePxpAlert(device);
    }

    public static void calibrateAlertThreshold(CalibrateListener listener, long time) {
        LocalBluetoothLEManager.getInstance().calibrateAlertThreshold(listener, time);
    }

    public static void registerBatteryLevelListener(BatteryChangeListener listener) {
        LocalBluetoothLEManager.getInstance()
                .registerBatteryLevelListener(listener);
    }

    public static void unregisterBatteryLevelListener() {
        LocalBluetoothLEManager.getInstance()
                .unregisterBatteryLevelListener();
    }
    
}
