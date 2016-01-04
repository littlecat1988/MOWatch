
package com.mtk.main;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.gomtel.util.Global;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.notification.NotificationReceiver18;
import com.mtk.bluetoothle.CustomizedBleFeaturesIniter;
import com.mtk.bluetoothle.LeProfileUtils;
import com.mtk.bluetoothle.LocalPxpFmpController;
import com.mtk.btnotification.R;
import com.mtk.ipc.IPCControllerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import care.application.XcmApplication;

/**
 * This class is the application enter, when it created, begin record logs.
 */
public class BTNotificationApplication extends XcmApplication {
    // Debugging
    private static final String TAG = "AppManager/Application";

    private final List<Activity> activityList = new LinkedList<Activity>();

    private static BTNotificationApplication sInstance = null;

    /**
     * Return the instance of our application.
     * 
     * @return the application instance
     */
    public static BTNotificationApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate(), BTNoticationApplication create!");
        Log.d(TAG, "onCreate(), SDK level = " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 18) {
            Log.d(TAG, "onCreate(), LE support = " + getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
        }
        super.onCreate();
        sInstance = this;
         Global.density = getApplicationContext().getResources().getDisplayMetrics().density;
        new File(Global.AD_PATH);
        IPCControllerFactory.getInstance().init();

        ///Fitness
        LeProfileUtils.init(sInstance);
        if (!LeProfileUtils.isGooglePlayAvailable()) {
            Toast.makeText(getApplicationContext(), R.string.gms_not_supported, Toast.LENGTH_LONG).show();
        } else {
            if (!LeProfileUtils.isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(), R.string.network_not_supported, Toast.LENGTH_LONG).show();
            }
        }

        // wearable init
      CustomizedBleFeaturesIniter.initBleClients();//20150703
        LocalPxpFmpController.initPxpFmpFunctions(this);
        Log.e(TAG,"getApplicationContext()= "+getApplicationContext());
        boolean isSuccess = WearableManager.getInstance().init(true, getApplicationContext(), "we had", R.xml.wearable_config);
        Log.d(TAG, "WearableManager init " + isSuccess);
        if (!MainService.isMainServiceActive()) {
            Log.i(TAG, "start MainService!");
            getApplicationContext().startService(
                    new Intent(getApplicationContext(), MainService.class));
        }

        String strListener = Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");
        Log.i(TAG, "strListener = " + strListener);
        if (strListener != null
                && strListener
                        .contains("com.mtk.btnotification/com.mtk.app.notification.NotificationReceiver18")) {
            ComponentName localComponentName = new ComponentName(this, NotificationReceiver18.class);
            PackageManager localPackageManager = this.getPackageManager();
            localPackageManager.setComponentEnabledSetting(localComponentName, 2, 1);
            localPackageManager.setComponentEnabledSetting(localComponentName, 1, 1);
            Log.i(TAG, "setComponentEnabledSetting");
        }
        // Print log begin
        // LogUtil.getInstance(getApplicationContext()).start();
        initBTProxy();
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    private void initBTProxy() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED
                || btAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTING) {
            btAdapter.getProfileProxy(this, mProxyListener, BluetoothProfile.A2DP);
        }
        if (btAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_CONNECTED
                || btAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_CONNECTING) {
            btAdapter.getProfileProxy(this, mProxyListener, BluetoothProfile.HEADSET);
        }
    }

    private BluetoothProfile.ServiceListener mProxyListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (proxy != null
                    && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
                List<BluetoothDevice> deviceList = proxy.getConnectedDevices();
                if (deviceList != null && deviceList.size() > 0) {
                    BluetoothDevice remoteDevice = deviceList.get(0);
                    WearableManager.getInstance().setRemoteDevice(remoteDevice);
                    Log.d(TAG,
                            "[wearable][onCreate], BTNoticationApplication WearableManager connect!");
                    WearableManager.getInstance().connect();
                }
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
