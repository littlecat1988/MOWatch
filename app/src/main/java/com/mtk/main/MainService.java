
package com.mtk.main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.gomtel.app.uv.UVController;
import com.mediatek.camera.service.RemoteCameraController;
import com.mediatek.ctrl.epo.EpoDownloadController;
import com.mediatek.ctrl.fota.common.FotaOperator;
import com.mediatek.ctrl.map.MapController;
import com.mediatek.ctrl.music.RemoteMusicController;
import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.sos.SOSController;
import com.mediatek.ctrl.sync.DataSyncController;
import com.mediatek.ctrl.yahooweather.YahooWeatherController;
import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.leprofiles.bas.BatteryChangeListener;
import com.mediatek.leprofiles.hr.HRListener;
import com.mediatek.leprofiles.pdms.PDMSClientProxy;
import com.mediatek.leprofiles.pdms.PDMSListener;
import com.mediatek.wearable.Controller;
import com.mediatek.wearable.VxpInstallController;
import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import android.util.Log;
import android.widget.Toast;

import com.mtk.app.applist.AppManager;
import com.mtk.app.applist.ApplistActivity;
import com.mtk.app.notification.AppList;
import com.mtk.app.notification.CallService;
import com.mtk.app.notification.NotificationReceiver;
import com.mtk.app.notification.NotificationService;
import com.mtk.app.notification.SmsService;
import com.mtk.app.notification.SystemNotificationService;
import com.mtk.app.remotecamera.RemoteCameraService;
import com.mtk.app.thirdparty.EXCDController;
import com.mtk.app.thirdparty.MREEController;
import com.mtk.bluetoothle.FitnessHelper;
import com.mtk.bluetoothle.FitnessUIInterface;
import com.mtk.bluetoothle.LocalPxpFmpController;
import com.mtk.btnotification.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * This class is the main service, it will process the most logic and interact
 * with other modules.
 */
@SuppressLint("NewApi")
public final class MainService extends Service {
    // Debugging
    private static final String TAG = "AppManager/MainService";
    //add by lixiang 20150708
    public static final UUID INTRESTING_DESC_UUID_1 = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_WRITE_AND_READ = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_UV = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_NOTI_UV = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_HR = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_NOTI_HR = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_WRITE_HR = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_WRITE_AND_READ_UV = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static final String ACTION_QUERY_MTK_BLUETOOTH_CONNECTION = "com.mtk.QUERY_MTK_BLUETOOTH_CONNECTION";

    public static final String ACTION_QUERY_BLUETOOTH_CONNECTION_TRUE = "com.mtk.QUERY_BLUETOOTH_CONNECTION_TRUE";

    public static final String ACTION_QUERY_BLUETOOTH_CONNECTION_FALSE = "com.mtk.QUERY_BLUETOOTH_CONNECTION_FALSE";

    public static final String ACTION_BLUETOOTH_SEND_EXCD_CMD = "com.mtk.ACTION_BLUETOOTH_SEND_EXCD_CMD";

    public static final String ACTION_BLUETOOTH_SEND_EXCD_DATA = "com.mtk.ACTION_BLUETOOTH_SEND_EXCD_DATA";

    public static final String ACTION_BLUETOOTH_SEND_MREE_CMD = "com.mtk.ACTION_BLUETOOTH_SEND_MREE_CMD";

    public static final String ACTION_BLUETOOTH_SEND_MREE_DATA = "com.mtk.ACTION_BLUETOOTH_SEND_MREE_DATA";

    public static final String ACTOIN_QUERY_SMARTWATCH_SYNC_DATA = "com.mtk.ACTOIN_QUERY_SMARTWATCH_SYNC_DATA";

    public static final String EXTRA_QUERY_ACTOIN_QUERY_SMARTWATCH_SYNC_DATA_DATA_BUFFER = "com.mtk.EXTRA_QUERY_ACTOIN_QUERY_SMARTWATCH_SYNC_DATA_DATA_BUFFER";

    public static final String ACTION_NOTIFICATION_BLOCK_PACKAGE = "com.matk.ACTION_NOTIFICATION_BLOCK_PACKAGE";

    // Global instance
    private static MainService sInstance = null;

    // Application context
    private static final Context sContext = BTNotificationApplication.getInstance()
            .getApplicationContext();

    // Flag to indicate whether main service has been start
    private static boolean mIsMainServiceActive = false;

    private boolean mIsSmsServiceActive = false;

    private boolean mIsCallServiceActive = false;

    // Flag to indicate whether the connection status icon shows
    private boolean mIsConnectionStatusIconShow = false;

    public static boolean mWriteAppConfigDone = false;

    // Register and unregister SMS service dynamically
    private static NotificationReceiver sNotificationReceiver = null;

    // Register and unregister SMS service dynamically
    private SmsService mSmsService = null;

    private SystemNotificationService mSystemNotificationService = null;

    // Register and unregister call service dynamically
    private CallService mCallService = null;

    private RemoteCameraService mRemoteCameraService = null;

    private NotificationService mNotificationService = null;

    public static final String EXTRA_DATA = "EXTRA_DATA";

    private int mBatteryValue = -1;

    private WearableListener mWearableListener = new WearableListener() {

        @Override
        public void onConnectChange(int oldState, int newState) {
            updateConnectionStatus();
        }

        @Override
        public void onDeviceChange(BluetoothDevice device) {
            return;
        }

        @Override
        public void onDeviceScan(BluetoothDevice device) {
            return;
        }

        @Override
        public void onModeSwitch(int newMode) {
            Log.d(TAG, "onModeSwitch newMode = " + newMode);
        }
    };

    private BatteryChangeListener mBatteryChangeListener = new BatteryChangeListener() {

        @Override
        public void onBatteryValueChanged(int currentValue, boolean needNotify) {
            Log.d(TAG, "onBatteryValueChanged() value = " + currentValue);
            mBatteryValue = currentValue;
            if (false) {
                updateNotification();
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // String action = intent.getAction();
            String action = intent.getAction();
            Log.d(TAG, "MainService onReceive action = " + action);

            if (ACTOIN_QUERY_SMARTWATCH_SYNC_DATA.equals(action)) {
                try {
                    // String mRemoteCameraCommand = new
                    // String(intent.getByteArrayExtra(EXTRA_QUERY_ACTOIN_QUERY_SMARTWATCH_SYNC_DATA_DATA_BUFFER),
                    // Constants.CHARSET);
                } catch (Exception e) {

                }
            } else if (ACTION_SHAKE_HAND_FAIL.equals(action)) {
                Toast.makeText(sContext, R.string.shake_hand_fail, Toast.LENGTH_SHORT).show();
            } else {

                if (WearableManager.getInstance().isAvailable()) {
                    if (ACTION_QUERY_MTK_BLUETOOTH_CONNECTION.equals(action)) {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(ACTION_QUERY_BLUETOOTH_CONNECTION_TRUE);
                        sContext.sendBroadcast(broadcastIntent);
                    } else if (ACTION_BLUETOOTH_SEND_EXCD_CMD.equals(action)) {
                        String cmdString = new String();
                        String dataPart = new String();
                        try {
                            byte[] datas = intent.getByteArrayExtra(EXTRA_DATA);
                            String datasString = new String(datas);
                            Log.i(TAG, "GET EXCD CMD FROM THRIDPARTY:" + Arrays.toString(datas));
                            int i = 0;
                            for (int charIndex = 0; charIndex < datasString.length(); charIndex++) {
                                char data = datasString.charAt(charIndex);
                                if (i == 5) {
                                    dataPart += data;
                                    continue;
                                }
                                cmdString += data;
                                if (data == ' ') {
                                    i++;
                                    continue;
                                }
                            }

                            EXCDController.getInstance().send(cmdString, dataPart.getBytes(), true, false, 0);
//                            for (Controller c : (HashSet<Controller>) WearableManager.getInstance()
//                                    .getControllers()) {
//                                if (c.getCmdType() == 9) {
//                                    c.send(cmdString, dataPart.getBytes(), true, false, 0);
//                                    break;
//                                }
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ACTION_BLUETOOTH_SEND_EXCD_DATA.equals(action)) {
                        Log.i(TAG, "GET EXCD DATA FROM THRIDPARTY: RETURN");
                    } else if (ACTION_BLUETOOTH_SEND_MREE_CMD.equals(action)) {
                        try {
                            byte[] datas = intent.getByteArrayExtra(EXTRA_DATA);
                            Log.i(TAG, "GET MREE CMD FROM THRIDPARTY:" + Arrays.toString(datas));

                            byte[] cmdByte = new byte[datas.length];
                            byte[] dataByte = new byte[datas.length];
                            int i = 0;
                            int cmdPos = 0;
                            int dataPos = 0;
                            for (int byteIndex = 0; byteIndex < datas.length; byteIndex++) {
                                if (i == 5) {
                                    dataByte[dataPos] = datas[byteIndex];
                                    dataPos++;
                                    continue;
                                }
                                cmdByte[cmdPos] = datas[byteIndex];
                                cmdPos++;
                                if (datas[byteIndex] == 0x20) {
                                    i++;
                                    continue;
                                }
                            }

                            byte[] cmdBytes = new byte[cmdPos];
                            byte[] dataBytes = new byte[dataPos];
                            System.arraycopy(cmdByte, 0, cmdBytes, 0, cmdPos);
                            System.arraycopy(dataByte, 0, dataBytes, 0, dataPos);
                            String cmdString = new String(cmdBytes);

                            MREEController.getInstance().send(cmdString, dataBytes, true, false, 0);
//                            for (Controller c : (HashSet<Controller>) WearableManager.getInstance()
//                                    .getControllers()) {
//                                if (c.getCmdType() == 8) {
//                                    HashSet<String> receivers = c.getReceiverTags();
//                                    if ((receivers != null && receivers.size() > 0 && receivers
//                                            .contains(cmdString.split(" ")[0]))
//                                            || receivers == null || receivers.size() == 0) {
//                                        c.send(cmdString, dataBytes, true, false, 0);
//                                        break;
//                                    }
//                                }
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (ACTION_BLUETOOTH_SEND_MREE_DATA.equals(action)) {
                        Log.i(TAG, "GET MREE DATA FROM THRIDPARTY: TETURN");
                    }
                } else {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ACTION_QUERY_BLUETOOTH_CONNECTION_FALSE);
                    sContext.sendBroadcast(broadcastIntent);
                }
            }
        }

    };

    private void WriteAppDetailFile(InputStream in, File file, String fileName) {

        if (file.exists()) {
            file.delete();
            // return;
        }
        int length;
        try {
            file.createNewFile();
            length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            in.close();

            FileOutputStream fout = new FileOutputStream(fileName);
            fout.write(buffer);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void WriteAppConfigFile() {
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
        InputStream in = null;
        in = getResources().openRawResource(R.raw.driver);
        String fileName = fileRoot + "driver.vtd";
        File file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.yahoo);
        fileName = fileRoot + "yahoo.png";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.yahooweather);
        fileName = fileRoot + "yahooweather.vxp";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.yahooweathercfg);
        fileName = fileRoot + "yahooweathercfg.xml";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.digitclock);
        fileName = fileRoot + "digitclock.vxp";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.digitclock_pic);
        fileName = fileRoot + "digitclock_pic.png";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.digitclockcfg);
        fileName = fileRoot + "digitclockcfg.xml";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.codoon_bmi055_driver);
        fileName = fileRoot + "codoon_bmi055_driver.vtp";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.codoon_bmi055);
        fileName = fileRoot + "codoon_bmi055.vxp";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.codoon_driver);
        fileName = fileRoot + "codoon_driver.vtp";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.codoon_icon);
        fileName = fileRoot + "codoon_icon.png";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.codoon);
        fileName = fileRoot + "codoon.vxp";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        in = getResources().openRawResource(R.raw.codooncfg);
        fileName = fileRoot + "codooncfg.xml";
        file = new File(fileName);
        WriteAppDetailFile(in, file, fileName);

        Log.i(TAG, "Write app config done,send broadcast ");
        mWriteAppConfigDone = true;
    }

    public static boolean getWriteAppConfigStatus() {
        return mWriteAppConfigDone;
    }

    public MainService() {
        Log.i(TAG, "MainService(), MainService in construction!");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        // updateConnectionStatus(false);

        super.onCreate();
        sInstance = this;
        mIsMainServiceActive = true;
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (applist.size() == 0) {
            applist.put(AppList.MAX_APP, (int) AppList.CREATE_LENTH);
            applist.put(AppList.CREATE_LENTH, AppList.BATTERYLOW_APPID);
            applist.put(AppList.CREATE_LENTH, AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.BATTERYLOW_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, AppList.BATTERYLOW_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.SMSRESULT_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }

        registerService();
        new Thread(new Runnable() {
            @Override
            public void run() {
                WriteAppConfigFile();
                AppManager appManager = AppManager.getInstance();
                appManager.refreshAppInfo();
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");

        this.unregisterReceiver(mReceiver);

        WearableManager manager = WearableManager.getInstance();
        manager.removeController(YahooWeatherController.getInstance(sContext));
        manager.removeController(RemoteCameraController.getInstance());
        manager.removeController(NotificationController.getInstance(sContext));
        // manager.removeController(MapDController.getInstance());
        manager.removeController(MapController.getInstance(sContext));
        manager.removeController(VxpInstallController.getInstance());
        manager.removeController(DataSyncController.getInstance(sContext));
//        manager.removeController(FotaController.getInstance());
        manager.removeController(RemoteMusicController.getInstance(sContext));
        manager.removeController(MREEController.getInstance());
        manager.removeController(EXCDController.getInstance());
        manager.removeController(SOSController.getInstance());
        manager.unregisterWearableListener(mWearableListener);
        LocalPxpFmpController.unregisterBatteryLevelListener();
        LocalBluetoothLEManager.getInstance().unregisterPDMSListener();
        LocalBluetoothLEManager.getInstance().unregisterHRListener();
        FotaOperator.getInstance(sContext).close();

        mIsMainServiceActive = false;
        unregisterReceiver(mSystemNotificationService);
        mSystemNotificationService = null;
        stopRemoteCameraService();
        stopNotificationService();

        FitnessHelper.getInstance().disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    /**
     * Return the instance of main service.
     * 
     * @return main service instance
     */
    public static MainService getInstance() {
        if (sInstance == null) {
            Log.i(TAG, "getInstance(), Main service is null.");
            startMainService();
        }

        return sInstance;
    }

    /**
     * Return whether main service is started.
     * 
     * @return Return true, if main service start, otherwise, return false.
     */
    public static boolean isMainServiceActive() {
        return mIsMainServiceActive;
    }

    private static void startMainService() {
        Log.i(TAG, "startMainService()");

        Intent startServiceIntent = new Intent(sContext, MainService.class);
        sContext.startService(startServiceIntent);
    }

    /*
     * Update connection status, if bluetooth connected, show a notification.
     */
    @SuppressWarnings("deprecation")
    public void updateConnectionStatus() {
        updateNotification();

    }

    private void updateNotification() {
        boolean isShowNotification = WearableManager.getInstance().isAvailable();
        Log.i(TAG, "updateNotification(), showNotification=" + isShowNotification);
        NotificationManager manager = (NotificationManager) sContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (isShowNotification) {
            /*
             * Create a notification to show connection status
             */
            Notification notification = new Notification();
            if (mBatteryValue < 0) {
                notification.icon = R.drawable.ic_connected_status;
            } else if (mBatteryValue < LocalPxpFmpController.BATTERY_LEVEL_1) {
                notification.icon = R.drawable.ic_battery_status_0;
            } else if (mBatteryValue < LocalPxpFmpController.BATTERY_LEVEL_2) {
                notification.icon = R.drawable.ic_battery_status_33;
            } else if (mBatteryValue < LocalPxpFmpController.BATTERY_LEVEL_3) {
                notification.icon = R.drawable.ic_battery_status_66;
            } else {
                notification.icon = R.drawable.ic_battery_status_100;
            }
            CharSequence notificationContent = sContext.getText(R.string.notification_content);
            if (mBatteryValue < 0) {
                notification.tickerText = sContext.getText(R.string.notification_ticker_text);
            } else {
                notificationContent = sContext.getString(R.string.notification_ticker_battery,
                        (mBatteryValue + 5) / 10 * 10);
//                notification.tickerText = notificationContent;//modified by lixiang for hide battery level in the statusbar 20150608
                notification.tickerText = notificationContent = null;
            }

            // Set it no clear, it will auto disappear when connection lost
            notification.flags = Notification.FLAG_ONGOING_EVENT;

            Intent intent = new Intent(sContext, FirstActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(sContext, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
//            notification.setLatestEventInfo(sContext,
//                    sContext.getText(R.string.notification_title), notificationContent,
//                    pendingIntent);

            // Show notification
            if (mIsConnectionStatusIconShow) {
                // Remove notification
                manager.cancel(R.string.app_name);
            }
            Log.i(TAG, "updateConnectionStatus(), show notification=" + notification);
            manager.notify(R.string.app_name, notification);

            mIsConnectionStatusIconShow = true;
        } else {
            if (mIsConnectionStatusIconShow) {
                // Remove notification
                manager.cancel(R.string.app_name);
                mIsConnectionStatusIconShow = false;

                Log.i(TAG, "updateConnectionStatus(),  cancel notification id=" + R.string.app_name);
            }
        }
    }

    private static final String ACTION_SHAKE_HAND_FAIL = "com.mtk.shake_hand_fail";

    private void registerService() {
        // regist battery low
        Log.i(TAG, "registerService()");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QUERY_MTK_BLUETOOTH_CONNECTION);
        filter.addAction(ACTOIN_QUERY_SMARTWATCH_SYNC_DATA);
        filter.addAction(ACTION_BLUETOOTH_SEND_EXCD_CMD);
        filter.addAction(ACTION_BLUETOOTH_SEND_EXCD_DATA);
        filter.addAction(ACTION_BLUETOOTH_SEND_MREE_CMD);
        filter.addAction(ACTION_BLUETOOTH_SEND_MREE_DATA);
        filter.addAction(ACTION_SHAKE_HAND_FAIL);
        this.registerReceiver(mReceiver, filter);

        WearableManager manager = WearableManager.getInstance();
        manager.addController(YahooWeatherController.getInstance(sContext));
        manager.addController(RemoteCameraController.getInstance());
        manager.addController(NotificationController.getInstance(sContext));
        manager.addController(MapController.getInstance(sContext));
        manager.addController(VxpInstallController.getInstance());
        manager.addController(DataSyncController.getInstance(sContext));
//        manager.addController(FotaController.getInstance());
        manager.addController(EpoDownloadController.getInstance());
//        RemoteMusicController.getInstance().setContext(getApplicationContext());
        manager.addController(RemoteMusicController.getInstance(sContext));
        manager.addController(MREEController.getInstance());
        manager.addController(EXCDController.getInstance());
        manager.addController(SOSController.getInstance());
        manager.addController(UVController.getInstance());
        manager.registerWearableListener(mWearableListener);
        LocalPxpFmpController.registerBatteryLevelListener(mBatteryChangeListener);
        LocalBluetoothLEManager.getInstance().registerHRListener(mHRListener);
//        LocalBluetoothLEManager.getInstance().updateNotifyInterval(500);
        
        LocalBluetoothLEManager.getInstance().registerPDMSListener(mPDMSListener);
        FotaOperator.getInstance(sContext);

        YahooWeatherController.loadCity(sContext);

        startSystemNotificationService();
        // start SMS service
        startSmsService();
        // start call service
        startCallService();
        // showChoiceNotification();
        startRemoteCameraService();

        startNotificationService();
    }

    public void startRemoteCameraService() {
        Log.i(TAG, "startRemoteCameraService()");
        mRemoteCameraService = new RemoteCameraService(sContext);
        RemoteCameraController.setListener(mRemoteCameraService);

    }

    public void stopRemoteCameraService() {
        Log.i(TAG, "stopRemoteCameraService()");

        RemoteCameraController.setListener(null);
        mRemoteCameraService = null;
    }

    public void startNotificationService() {
        Log.i(TAG, "startNotificationService()");
        mNotificationService = new NotificationService();
        NotificationController.setListener(mNotificationService);

    }

    public void stopNotificationService() {
        Log.i(TAG, "stopNotificationService()");

        NotificationController.setListener(null);
        mNotificationService = null;
    }

    /**
     * Start SystemNotificationService to push some other message.
     */
    void startSystemNotificationService() {
        mSystemNotificationService = new SystemNotificationService();
        IntentFilter filter = new IntentFilter("android.intent.action.BATTERY_LOW");
        registerReceiver(mSystemNotificationService, filter);

        // regist adaptor pluged
        filter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        registerReceiver(mSystemNotificationService, filter);
        filter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        registerReceiver(mSystemNotificationService, filter);

    }

    public boolean getSmsServiceStatus() {
        return mIsSmsServiceActive;
    }

    /**
     * Start SMS service to push new SMS.
     */
    public void startSmsService() {
        Log.i(TAG, "startSmsService()");

        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }

        // Start SMS service
        if (mSmsService == null) {
            mSmsService = new SmsService();
        }
        IntentFilter filter = new IntentFilter("com.mtk.btnotification.SMS_RECEIVED");
        registerReceiver(mSmsService, filter);

        mIsSmsServiceActive = true;
    }

    /**
     * Stop SMS service.
     */
    public void stopSmsService() {
        Log.i(TAG, "stopSmsService()");

        // Stop SMS service
        if (mSmsService != null) {
            unregisterReceiver(mSmsService);
            mSmsService = null;
        }

        mIsSmsServiceActive = false;
    }

    public boolean getCallServiceStatus() {
        return mIsCallServiceActive;
    }

    /**
     * Start call service to push new missed call.
     */
    public void startCallService() {
        Log.i(TAG, "startCallService()");

        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }

        // Start SMS service
        if (mCallService == null) {
            mCallService = new CallService(sContext);
        }
        TelephonyManager telephony = (TelephonyManager) sContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(mCallService, PhoneStateListener.LISTEN_CALL_STATE);

        mIsCallServiceActive = true;
    }

    /**
     * Stop call service.
     */
    public void stopCallService() {
        Log.i(TAG, "stopCallService()");

        // Stop call service
        if (mCallService != null) {
            TelephonyManager telephony = (TelephonyManager) sContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(mCallService, PhoneStateListener.LISTEN_NONE);
            mCallService.stopCallService();
            mCallService = null;
        }
        mIsCallServiceActive = false;
    }

    /**
     * Save notification service instance.
     *
     * @param notificationService
     */
    public static void setNotificationReceiver(NotificationReceiver notificationReceiver) {
        sNotificationReceiver = notificationReceiver;
    }

    /**
     * Clear notification service instance.
     */
    public static void clearNotificationReceiver() {
        sNotificationReceiver = null;
    }

    /**
     * Return whether notification service is started.
     */
    public static boolean isNotificationReceiverActived() {
        return (sNotificationReceiver != null);
    }

    /// M: Fitness feature
    private FitnessUIInterface mFitnessUIInterface;

    public void setFitnessUIInterface(FitnessUIInterface fitInterface) {
        mFitnessUIInterface = fitInterface;
        Log.d(TAG, "[Fit] setFitnessUIInterface " + mFitnessUIInterface);
    }

    public void clearFitnessUIInterface() {
        mFitnessUIInterface = null;
        Log.d(TAG, "[Fit] clearFitnessUIInterface");
    }

    private PDMSListener mPDMSListener = new PDMSListener() {
        @Override
        public void onSleepNotify(long startTime, long endTime, int sleepMode) {
            Log.d(TAG, "[Fit]onSleepNotify srartTime=" + startTime + " endTime=" + endTime + " mode=" + sleepMode);
            if (mFitnessUIInterface != null) {
                mFitnessUIInterface.onSleepNotify(startTime, endTime, sleepMode);
            }
            FitnessHelper.getInstance().uploadSleepData(startTime, endTime, sleepMode);
        }

        @Override
        public void onPedometerNotify(int stepCount, int calories, int distance) {
            Log.e(TAG, "[Fit]onPedometerNotify stepCount=" + stepCount + " calories=" + calories + " distance=" + distance);
            if (mFitnessUIInterface != null) {
                mFitnessUIInterface.onPedometerNotify(stepCount, calories, distance);
            }
            FitnessHelper.getInstance().uploadStepData(stepCount, calories, distance);
        }
    };

    private HRListener mHRListener = new HRListener() {
        @Override
        public void onHRNotify(int bpm) {
            Log.d(TAG, "[Fit]onHRNotify bpm=" + bpm);
            if (mFitnessUIInterface != null) {
                mFitnessUIInterface.onHRNotify(bpm);
            }
            FitnessHelper.getInstance().uploadHRData(bpm);
        }
    };

	private Object obj = new Object();
    //add by lixiang for writeCharacter 20150708
	public void sendBroadcast(Intent intent){
		sContext.sendBroadcast(intent);
	}
	public void setNotifyUVTrue(BluetoothGatt gatt,boolean notify)
	    {
	      
//	    	setCharactoristicNotifyAndWriteDescriptor(getBluetoothGatt(), UUID_SERVICE, INTRESTING_CHAR_UUID_1, INTRESTING_DESC_UUID_1);
	    	setCharactoristicNotifyAndWriteDescriptor(gatt, UUID_SERVICE_UV, UUID_CHARACTERISTIC_NOTI_UV, INTRESTING_DESC_UUID_1, notify);
	       
	    }
	public void setNotifyHRTrue(BluetoothGatt gatt,boolean notify)
    {
      
    	setCharactoristicNotifyAndWriteDescriptor(gatt, UUID_SERVICE_HR, UUID_CHARACTERISTIC_NOTI_HR, INTRESTING_DESC_UUID_1, notify);
       
    }
	    public void setCharactoristicNotifyAndWriteDescriptor(BluetoothGatt paramBluetoothGatt, UUID paramUUID1, UUID paramUUID2, UUID paramUUID3, boolean notify)
	    {
	      BluetoothGattCharacteristic localBluetoothGattCharacteristic = getBluetoothGattCharacteristic(getBluetoothGattService(paramBluetoothGatt, paramUUID1), paramUUID2);
	      if ((paramBluetoothGatt != null) && (localBluetoothGattCharacteristic != null))
	      {
	        paramBluetoothGatt.setCharacteristicNotification(localBluetoothGattCharacteristic, notify);
	        BluetoothGattDescriptor localBluetoothGattDescriptor = localBluetoothGattCharacteristic.getDescriptor(paramUUID3);
	        Log.e(TAG,"localBluetoothGattDescriptor= "+localBluetoothGattDescriptor);
	        if (localBluetoothGattDescriptor != null)
	        {
	          localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	          paramBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor);
	        }
	      }
	    }
    public void writeCharacteristic(BluetoothGatt paramBluetoothGatt, UUID paramUUID1, UUID paramUUID2, byte[] paramArrayOfByte)
    {
//      while (true)
//      {
        BluetoothGattCharacteristic localBluetoothGattCharacteristic;
          synchronized(obj)
        {
          localBluetoothGattCharacteristic = getBluetoothGattCharacteristic(getBluetoothGattService(paramBluetoothGatt, paramUUID1), paramUUID2);
          if ((paramBluetoothGatt != null) && (localBluetoothGattCharacteristic != null))
          {
            localBluetoothGattCharacteristic.setValue(paramArrayOfByte);
            localBluetoothGattCharacteristic.setWriteType(2);
            paramBluetoothGatt.writeCharacteristic(localBluetoothGattCharacteristic);
            return;
          }
          if (paramBluetoothGatt != null)
              if (localBluetoothGattCharacteristic != null) {
//                  continue;
              }else {
                  Log.i(this.TAG, "mBluetoothGattCharacteristic is null");
                  return;
              }
        }
//      }
    }
    
    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(BluetoothGattService paramBluetoothGattService, UUID paramUUID)
    {
      if (paramBluetoothGattService != null)
      {
        BluetoothGattCharacteristic localBluetoothGattCharacteristic = paramBluetoothGattService.getCharacteristic(paramUUID);
        if (localBluetoothGattCharacteristic != null)
          return localBluetoothGattCharacteristic;
        Log.i(this.TAG, "getBluetoothGattCharacteristic, bluetoothGattServer get characteristic uuid:" + paramUUID + " is null");
      }
        Log.i(this.TAG, "mBluetoothGattServer is null");
        return null;


    }
    
    private BluetoothGattService getBluetoothGattService(BluetoothGatt paramBluetoothGatt, UUID paramUUID)
    {
      if (paramBluetoothGatt != null)
      {
        BluetoothGattService localBluetoothGattService = paramBluetoothGatt.getService(paramUUID);
        if (localBluetoothGattService != null)
          return localBluetoothGattService;
        Log.i(this.TAG, "getBluetoothGattService, bluetoothgatt get service uuid:" + paramUUID + " is null");
      }
        Log.i(this.TAG, "mBluetoothGatt is null");
        return null;

    }
}
