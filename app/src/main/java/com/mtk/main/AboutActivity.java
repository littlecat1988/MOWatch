
package com.mtk.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.UUID;

import com.mediatek.wearable.WearableManager;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

    private static final String TAG = "AboutActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // The About UI will enable "LogCatcher" button after you clicked the "Version"
        // TextView 5 times quickly.
        // The "LogCatcher" feature only work in the WearableManager "Connected" state.
        // The "LogCatcher" SPP Client will connect the "WearableManager connected" Remote Device Log Server,
        // then received MAUI log from SPP Log server.
        // The remote log device will be SPP connected device if APK mode is SPP and connected successfully.
        // Otherwise user must select a remote log device from scanning UI.
        TextView versionText = (TextView) findViewById(R.id.version_text);
        try {
            versionText
                    .setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            versionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "versionText Click");
                    if (mVersionClickTime == 0) {
                        mVersionClickTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - mVersionClickTime >= 0
                            && System.currentTimeMillis() - mVersionClickTime < 600) {
                        mClickCount++;
                        Log.d(LOG_TAG, "versionText mClickCount: " + mClickCount);
                        if (mClickCount == CLICK_COUNT) {
                            Log.d(LOG_TAG, "showLogButton start");
                            mClickCount = 0;
                            updateLogButton();
                            showLogButton();
                        }
                    }
                    if (System.currentTimeMillis() - mVersionClickTime > 2000) {
                        mClickCount = 1;
                        Log.d(LOG_TAG, "showLogButton mClickCount = 1");
                    }
                    mVersionClickTime = System.currentTimeMillis();
                }
            });
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Button testButton = (Button) findViewById(R.id.button1);
        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // YahooWeatherCore.LoadCity();
                if (isFastDoubleClick()) {
                    Log.d(TAG, "AboutButton return");
                    return;
                }
                createNotificaction();
            }
        });

        mCatchLog = (Button) findViewById(R.id.log_test);
        mCatchLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // YahooWeatherCore.LoadCity();
                if (isFastDoubleClick()) {
                    Log.d(LOG_TAG, "AboutButton CatchLog return");
                    return;
                }
                if (mConnectState == 0) {
                    startCatchLog();
                } else if (mConnectState == 2) {
                    stopCatchLog();
                }
            }
        });
        showLogButton();
    }

    private void createNotificaction() {
        NotificationManager manager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = Utils.genMessageId();
        // Send a notification to show connection status
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_connected_status;
        notification.tickerText = "Ticker Text" + String.valueOf(notificationId);
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        notification.setLatestEventInfo(this, "Title",
//                "Content: Hello!" + String.valueOf(notificationId), pendingIntent);
        manager.notify(notificationId, notification);
    }

    private long mLastClickTime = System.currentTimeMillis();;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;
    }


    /// M: show Log Button
    private int mClickCount = 0;

    private long mVersionClickTime = 0;

    private static final int CLICK_COUNT = 5;

    private void showLogButton() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        boolean isSwitchMode = prefs.getBoolean("log_button", false);
        Log.d(LOG_TAG, "[showLogButton] " + isSwitchMode);
        if (isSwitchMode) {
            mCatchLog.setVisibility(View.VISIBLE);
            mCatchLog.setEnabled(true);
        }
    }

    private void updateLogButton() {
        Log.d(LOG_TAG, "[updateLogButton] start");
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("log_button", true);
        editor.commit();
    }

    /// M: Catch log test @{
    private BluetoothAdapter mBluetoothAdapter;

    private Button mCatchLog;

    private static final String LOG_SPP_UUID = "97a42c60-a826-11e4-8b1c-0002a5d5c51b";

    // 0 = disconnected, 1 = connecting, 2 = connected
    private int mConnectState = 0;

    private static final int READ_BUFFER = 1024 * 5;

    private BluetoothDevice mConnectedDevice;

    private BluetoothSocket mClientSocket;

    private BluetoothSocket mReadSocket;

    private Thread mClientThread;

    private Thread mReadThread;

    private FileOutputStream mRecFile;

    private String mLogFileName;

    private static final int CLIENT_THREAD = 1;

    private static final int READ_THREAD = 2;

    private static final int REQUEST_CODE_SCAN = 0;

    private static final String LOG_DEVICE = "LOG_DEVICE";

    private static final String LOG_TAG = "LogCatcher";

    private void startCatchLog() {
        Log.d(LOG_TAG, "startCatchLog ConnectState: " + mConnectState);
        if (mConnectState == 0) {
            if (mBluetoothAdapter == null || !mBluetoothAdapter.enable()) {
                Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                return;
            }
            if (WearableManager.getInstance().isAvailable()
                    && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
                mConnectedDevice = WearableManager.getInstance().getRemoteDevice();
                if (mConnectedDevice != null) {
                    Log.d(LOG_TAG, "startCatchLog device: " + mConnectedDevice.getAddress());
                }
                mClientThread = new Thread(mSPPClientRunnable);
                mClientThread.start();
            } else {
                Log.d(LOG_TAG, "startCatchLog log_error");
                Toast.makeText(getApplicationContext(), R.string.log_scan, Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent(AboutActivity.this, LogDeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String address = data.getStringExtra(LOG_DEVICE);
                Log.d(LOG_TAG, "[onActivityResult] address: " + address);
                if (BluetoothAdapter.checkBluetoothAddress(address)) {
                    mConnectedDevice = mBluetoothAdapter.getRemoteDevice(address);
                    if (mConnectedDevice != null) {
                        Log.d(LOG_TAG, "[onActivityResult] device: " + mConnectedDevice.getAddress());
                        mClientThread = new Thread(mSPPClientRunnable);
                        mClientThread.start();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void stopCatchLog() {
        Log.d(LOG_TAG, "startCatchLog begin");
        Log.d(TAG, "disconnect begin");
        if (mClientThread != null) {
            cancelThread(CLIENT_THREAD);
            mClientThread = null;
        }
        if (mReadThread != null) {
            cancelThread(READ_THREAD);
            mReadThread = null;
        }
    }

    private void connectFail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.log_fail), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCatchLogButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnectState == 0) {
                    mCatchLog.setEnabled(true);
                    mCatchLog.setText(R.string.log_start);
                } else if (mConnectState == 1) {
                    mCatchLog.setEnabled(false);
                    mCatchLog.setText(R.string.log_connecting);
                } else {
                    mCatchLog.setEnabled(true);
                    mCatchLog.setText(R.string.log_stop);
                }
            }
        });
    }

    private Runnable mSPPClientRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "SPPClientThread begin");
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                UUID LOG_UUID = UUID.fromString(LOG_SPP_UUID);
                tmp = mConnectedDevice.createRfcommSocketToServiceRecord(LOG_UUID);
            } catch (IOException e) {
                Log.e(LOG_TAG, "SPPClientThread create socket IOException" + e.getMessage());
                return;
            }
            mClientSocket = tmp;

            // mAdapter.cancelDiscovery();
            try {
                Log.d(LOG_TAG, "SPPClientThread connect begin");
                mConnectState = 1;
                updateCatchLogButton();
                mClientSocket.connect();
                Log.d(LOG_TAG, "SPPClientThread.connect end");
            } catch (IOException e) {
                mConnectState = 0;
                connectFail();
                updateCatchLogButton();
                Log.e(LOG_TAG, "SPPClientThread.connect fail: " + e.getMessage());
                try {
                    if (mClientSocket != null) {
                        mClientSocket.close();
                    }
                } catch (IOException e2) {
                    Log.e(LOG_TAG, "SPPClientThread.connect close fail: " + e2.getMessage());
                }
                return;
            }

            mClientThread = null;

            // Start the connected thread
            connected(mClientSocket, mConnectedDevice);
            Log.d(LOG_TAG, "SPPClientThread end");
        }
    };

    private Runnable mSPPReadRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "[SPPReadThread] begin");

            InputStream tmpIn = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = mReadSocket.getInputStream();
            } catch (IOException e) {
                Log.e(LOG_TAG, "[SPPReadThread] getInputStream fail: " + e.getMessage());
            }

            // Keep listening to the InputStream while connected
            while (tmpIn != null) {
                try {
                    // Read from the InputStream buffer control
                    byte[] data = new byte[READ_BUFFER];
                    int byteLength = 0;
                    byteLength = tmpIn.read(data);
                    // Send the obtained bytes to the manager
                    Log.d(LOG_TAG, "[SPPReadThread] read length = " + byteLength);
                    if (byteLength > 0) {
                        // write log to file
                        writeLog(data, byteLength);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "[SPPReadThread] read IOException" + e.getMessage());
                    connectionLost();
                    break;
                }
            }
            Log.d(LOG_TAG, "[SPPReadThread] end");
        }
    };

    private void cancelThread(int thread) {
        if (thread == CLIENT_THREAD) {
            try {
                if (mClientSocket != null) {
                    Log.d(LOG_TAG, "[cancelThread] mClientSocket.close");
                    mClientSocket.close();
                    mClientSocket = null;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "[cancelThread] SPPCancelCallback.cancel client fail: " + e.getMessage());
            }
        } else if (thread == READ_THREAD) {
            try {
                synchronized (mReadSocket) {
                    if (mReadSocket != null) {
                        Log.d(LOG_TAG,
                                "[cancelThread] mReadSocket.close begin " + mReadSocket.isConnected());
                        mReadSocket.close();
                        Log.d(LOG_TAG,
                                "[cancelThread] mReadSocket.close end " + mReadSocket.isConnected());
                        mReadSocket = null;
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "[cancelThread] SPPCancelCallback.cancel read failed: " + e.getMessage());
            }
        } else {
            Log.e(LOG_TAG, "[cancelThread] SPPCancelCallback.cancel invaild thread");
        }
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(LOG_TAG, "[connected], socket = " + socket + ", device = " + device);

        if (mClientThread != null) {
            cancelThread(CLIENT_THREAD);
            mClientThread = null;
        }
        if (mReadThread != null) {
            cancelThread(READ_THREAD);
            mReadThread = null;
        }

        mReadSocket = socket;
        mReadThread = new Thread(mSPPReadRunnable);
        mReadThread.start();

        mConnectState = 2;
        updateCatchLogButton();

        // create log file
        createLogFile();
    }

    private void connectionLost() {
        Log.d(LOG_TAG, "[connectionLost] begin");

        if (mClientThread != null) {
            cancelThread(CLIENT_THREAD);
            mClientThread = null;
        }
        if (mReadThread != null) {
            cancelThread(READ_THREAD);
            mReadThread = null;
        }
        mConnectState = 0;
        updateCatchLogButton();

        if (mRecFile != null) {
            try {
                mRecFile.close();
            } catch (IOException e) {
                Log.d(LOG_TAG, "[connectionLost] IOException " + e.getMessage());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(getApplicationContext(),
                          getResources().getString(R.string.log_path) + mLogFileName, Toast.LENGTH_LONG).show();
                  mLogFileName = null;
                }
            });
            mRecFile = null;
        }
    }

    private void createLogFile() {
        String fileRoot = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileRoot = Environment.getExternalStorageDirectory() + "/AsterLog";
        } else {
            fileRoot = Environment.getRootDirectory() + "/AsterLog";
        }

        File dir = new File(fileRoot);
        if (!dir.exists()) {
            dir.mkdir();
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String fileName = fileRoot + "/" + dateFormat.format(System.currentTimeMillis());
        fileName = fileName.replace(".", "_").replace(":", "-") + ".log";
        Log.d(LOG_TAG, "[connected] fileName: " + fileName);

        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.d(LOG_TAG, "[connected] IOException " + e);
            return;
        }
        if (mRecFile == null) {
            try {
                mRecFile = new FileOutputStream(file, false);
                mLogFileName = fileName;
            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "[connected] FileNotFoundException " + e);
            }
        }
    }

    private void writeLog(byte[] data, int length) {
        if (mRecFile != null) {
            try {
                int writed = 0;
                final int write_len = 2 * 1024;
                while (writed != length) {
                    if (length - writed >= write_len) {
                        mRecFile.write(data, writed, write_len);
                        writed += write_len;
                    } else {
                        mRecFile.write(data, writed, length - writed);
                        writed += length - writed;
                    }
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "[writeLog] IOException ", e);
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.write_fail), Toast.LENGTH_LONG).show();
            }
        }
    }
    /// @}
}
