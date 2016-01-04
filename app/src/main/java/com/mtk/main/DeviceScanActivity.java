/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtk.main;

import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private final static String TAG = "AppManager/DeviceScan";

    public static String REMOTE_DEVICE_INFO = "REMOTE_DEVICE_INFO";

    private DeviceListAdapter mDeviceListAdapter;

    private int mWorkingMode;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;

    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mStopRunnable begin");
            mScanning = false;
            WearableManager.getInstance().scanDevice(false);
            invalidateOptionsMenu();
        }
    };

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60 * 1000;

    private final BroadcastReceiver mDeviceScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionString = intent.getAction();
            if (actionString.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int currState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if (currState == BluetoothAdapter.STATE_OFF) {
                    Log.d(TAG, "mDeviceScanReceiver off begin");
                    mHandler.removeCallbacks(mStopRunnable);
                    scanDevice(false);
                    mDeviceListAdapter.clear();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "DeviceScanActivity onCreate");
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mWorkingMode = WearableManager.getInstance().getWorkingMode();

        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (android.os.Build.VERSION.SDK_INT >= 18 && mWorkingMode == WearableManager.MODE_DOGP
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        WearableManager.getInstance().registerWearableListener(mWearableListener);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mDeviceScanReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DeviceScanActivity onDestroy");
        super.onDestroy();
        WearableManager.getInstance().unregisterWearableListener(mWearableListener);
        unregisterReceiver(mDeviceScanReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
                mDeviceListAdapter.clear();
                scanDevice(true);
                break;
            case R.id.menu_stop:
                scanDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
            return;
        }

        // Initializes list view adapter.
        mDeviceListAdapter = new DeviceListAdapter();
        setListAdapter(mDeviceListAdapter);
        Log.d(TAG, "DeviceScanActivity onResume scanDevice(true)");
        scanDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "DeviceScanActivity onPause");
        super.onPause();
        scanDevice(false);
        mDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
        if (device == null)
            return;

        try {
            Log.d(TAG, "DeviceScanActivity onListItemClick");
            WearableManager.getInstance().setRemoteDevice(device);
            WearableManager.getInstance().connect();
            WearableManager.getInstance().unregisterWearableListener(mWearableListener);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.connect_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void scanDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.removeCallbacks(mStopRunnable);
            mHandler.postDelayed(mStopRunnable, SCAN_PERIOD);

            mScanning = true;
            mDeviceListAdapter.addConnectedDevice();
            WearableManager.getInstance().scanDevice(true);
        } else {
            mHandler.removeCallbacks(mStopRunnable);
            mScanning = false;
            WearableManager.getInstance().scanDevice(false);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class DeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mDevices;

        private LayoutInflater mInflator;

        public DeviceListAdapter() {
            super();
            mDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mDevices.get(position);
        }

        public void clear() {
            Log.d(TAG, "clear begin");
            mDevices.clear();
            mDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void addConnectedDevice() {
            if (android.os.Build.VERSION.SDK_INT >= 18) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_DOGP) {
                    List<BluetoothDevice> devices = bluetoothManager
                            .getConnectedDevices(BluetoothProfile.GATT);
                    for (BluetoothDevice device : devices) {
                        if (device != null) {
                            if (android.os.Build.VERSION.SDK_INT < 18) {
                                addDevice(device);
                                Log.d(TAG, "addConnectedDevice GATT < 18 " + device.getAddress());
                                mDeviceListAdapter.notifyDataSetChanged();
                            } else if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                                addDevice(device);
                                Log.d(TAG, "addConnectedDevice GATT " + device.getAddress());
                                mDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (devices != null
                    && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
                for (BluetoothDevice device : devices) {
                    if (device != null) {
                        if (android.os.Build.VERSION.SDK_INT < 18) {
                            mDeviceListAdapter.addDevice(device);
                            Log.d(TAG, "addConnectedDevice BondedDevices " + device.getAddress());
                            mDeviceListAdapter.notifyDataSetChanged();
                        } else if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
                            Log.d(TAG, "addConnectedDevice BondedDevices " + device.getAddress()
                                    + " " + device.getType());
                            mDeviceListAdapter.addDevice(device);
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            Log.d(TAG, "getView");
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = i < mDevices.size() ? mDevices.get(i) : null;
            if (device != null) {
                String deviceName = device.getName();
                String name = queryDeviceName(device.getAddress());
                if (!TextUtils.isEmpty(name) && !name.equals(deviceName)) {
                    deviceName = name;
                }
                if (deviceName != null && deviceName.length() > 0) {
                    viewHolder.deviceName.setText(deviceName);
                } else {
                    viewHolder.deviceName.setText(R.string.unknown_device);
                }
                viewHolder.deviceAddress.setText(device.getAddress());
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceAddress.setText("");
            }

            return view;
        }
    }

    // register WearableListener
    private WearableListener mWearableListener = new WearableListener() {

        @Override
        public void onDeviceChange(BluetoothDevice device) {
        }

        @Override
        public void onConnectChange(int oldState, int newState) {
            Log.d(TAG, "onConnectChange old = " + oldState + " new = " + newState);
            if (oldState != WearableManager.STATE_CONNECTED
                    && newState == WearableManager.STATE_CONNECTED) {
                finish();
            }
        }

        @Override
        public void onDeviceScan(final BluetoothDevice device) {
            Log.d(TAG, "onDeviceScan " + device.getName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDeviceListAdapter.addDevice(device);
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onModeSwitch(int newMode) {
            Log.d(TAG, "onModeSwitch newMode = " + newMode);
        }
    };

    static class ViewHolder {
        TextView deviceName;

        TextView deviceAddress;

        TextView signalStrength;
    }

    private String queryDeviceName(String address) {
        SharedPreferences prefs = DeviceScanActivity.this.getSharedPreferences("device_name",
                Context.MODE_PRIVATE);
        String name = prefs.getString(address, "");
        Log.d(TAG, "[wearable][queryDeviceName] begin " + address + " " + name);
        return name;
    }
}
