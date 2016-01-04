/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.PxpFmStatusChangeListener;
import com.mediatek.leprofiles.PxpFmStatusRegister;
import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import com.mtk.btnotification.R;

/**
 * This is an example of a custom preference type. The preference counts the
 * number of clicks it has received and stores/retrieves it from the storage.
 */
public class CustomPreference extends Preference {

    private static final String TAG = "AppManager/CustomPreference";

    private View mCurrView = null;

    private Context mContext;

    private boolean mInConnecting;

    private static final int MESSAGE_UPDATE = 0;

    private static final int UNKNOWN_STATE = Integer.MAX_VALUE;

    private BluetoothDevice mRemoteDevice;

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "mMainHandler handleMessage, msg.what = " + msg.what);
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    updatePreference(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    private WearableListener mWearableListener = new WearableListener() {
        @Override
        public void onDeviceChange(BluetoothDevice device) {
            Message msg = mMainHandler.obtainMessage();
            msg.what = MESSAGE_UPDATE;
            msg.arg1 = -1;
            mMainHandler.sendMessage(msg);
        }

        @Override
        public void onConnectChange(int oldState, int newState) {
            Message msg = mMainHandler.obtainMessage();
            msg.what = MESSAGE_UPDATE;
            msg.arg1 = newState;
            mMainHandler.sendMessage(msg);
        }

        @Override
        public void onDeviceScan(BluetoothDevice device) {
        }

        @Override
        public void onModeSwitch(int newMode) {
            Log.d(TAG, "onModeSwitch newMode = " + newMode);
        }
    };

    public PxpFmStatusChangeListener mPxpFmStatusChangeListener = new PxpFmStatusChangeListener() {

        @Override
        public void onStatusChange() {
            Message msg = mMainHandler.obtainMessage();
            msg.what = MESSAGE_UPDATE;
            msg.arg1 = UNKNOWN_STATE;
            mMainHandler.sendMessage(msg);
        }
    };

    // This is the constructor called by the inflater
    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "[wearable][CustomPreference] new begin");
        mContext = context;
        setWidgetLayoutResource(R.layout.main_info_preference_layout);
        mInConnecting = false;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mCurrView = view;
        ImageButton icon = (ImageButton) view.findViewById(R.id.item_image);
        TextView summary = (TextView) mCurrView.findViewById(R.id.item_summary);
        ViewGroup viewGroup = (ViewGroup) mCurrView.findViewById(R.id.custom_pre_layout_id);
        TextView ringingState = (TextView) mCurrView.findViewById(R.id.ringing_state);
        icon.setBackgroundColor(0);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick begin");
                if (isFastDoubleClick()) {
                    Log.d(TAG, "isFastDoubleClick return");
                    return;
                }

                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice remoteDevice = WearableManager.getInstance().getRemoteDevice();
                boolean isConnected = WearableManager.getInstance().isAvailable();

                if (remoteDevice == null || !adapter.isEnabled()) {
                    if (!adapter.isEnabled()) {
                        Toast toast = Toast.makeText(mContext, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    Intent intent = new Intent(mContext, DeviceScanActivity.class);
                    mContext.startActivity(intent);
                } else if (!isConnected) {
                    mInConnecting = true;
                    if (mCurrView != null) {
                        ImageButton icon = (ImageButton) mCurrView.findViewById(R.id.item_image);
                        TextView connectState = (TextView) mCurrView.findViewById(R.id.connect_state);

                        icon.setImageResource(R.drawable.watch_disconnected);
                        connectState.setText(R.string.connecting);
                    }
                    WearableManager.getInstance().connect();
                } else if (isConnected) {
                    showDisconnectPrompt();
                }
            }
        });
        icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((ImageButton) v).setBackgroundColor(Color.parseColor("#87cefa"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setBackgroundColor(0);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                        || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    ((ImageButton) v).setBackgroundColor(0);
                }
                return false;
            }
        });

        TextView connectState = (TextView) view.findViewById(R.id.connect_state);
        int pxpAlertStatus = PxpFmStatusRegister.getInstance().getPxpAlertStatus();
        int fmAlertStatus = PxpFmStatusRegister.getInstance().getFindMeStatus();
        if (fmAlertStatus == PxpFmStatusRegister.FIND_ME_STATUS_USING) {
            ringingState.setVisibility(TextView.VISIBLE);
        } else {
            ringingState.setVisibility(TextView.INVISIBLE);
        }
        Log.d(TAG, "[wearable][onBindView] mInConnecting = " + mInConnecting + " state = " + WearableManager.getInstance().getConnectState());
        if (mInConnecting || WearableManager.getInstance().isConnecting()) {
            connectState.setText(R.string.connecting);
        } else if (WearableManager.getInstance().isAvailable()) {
            if (pxpAlertStatus == BlePxpFmpConstants.STATE_IN_RANGE_ALERT) {
                connectState.setText(R.string.main_menu_in_range);
                connectState.setTextColor(mContext.getResources().getColor(R.color.red));
            } else if (pxpAlertStatus == BlePxpFmpConstants.STATE_OUT_RANGE_ALERT) {
                connectState.setText(R.string.main_menu_out_of_range);
                connectState.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                connectState.setText(R.string.connected);
                connectState.setTextColor(mContext.getResources().getColor(R.color.black));
            }
            icon.setImageResource(R.drawable.watch_connected);
        } else if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_DISCONNECTING) {
            connectState.setText(R.string.disconnecting);
        } else {
            connectState.setText(R.string.disconnected);
            icon.setImageResource(R.drawable.watch_disconnected);
        }

        TextView deviceName = (TextView) view.findViewById(R.id.device_name);
        BluetoothDevice remoteDevice = WearableManager.getInstance().getRemoteDevice();
        if (remoteDevice == null) {
            deviceName.setText("");
            connectState.setText("");
            icon.setImageResource(R.drawable.watch_add);
        } else {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            icon.setLayoutParams(layoutParams);
//            icon.setPadding(80, 48, 0, 70);
            Log.d(TAG, "[wearable][onBindView] device = " + remoteDevice.getName());
            if (remoteDevice.getName() != null) {
                mRemoteDevice = remoteDevice;
                String name = queryDeviceName(remoteDevice.getAddress());
                if (!TextUtils.isEmpty(name) && !name.equals(remoteDevice.getName())) {
                    setDeviceName(name);
                } else {
                    setDeviceName(remoteDevice.getName());
                    saveDeviceName(remoteDevice.getAddress(), remoteDevice.getName());
                }
            } else {
                String curName = (String) deviceName.getText();
                Log.d(TAG, "[wearable][onBindView] curName = " + curName);
                if (TextUtils.isEmpty(curName)
                        || curName.equals(mContext.getResources().getString(R.string.str_mtksmartdevice))
                        || curName.equals(mContext.getResources().getString(R.string.unknown_device))) {
                    String name = queryDeviceName(remoteDevice.getAddress());
                    if (TextUtils.isEmpty(name)) {
                        deviceName.setText(R.string.unknown_device);
                    } else {
                        deviceName.setText(name);
                    }
                }
            }
            viewGroup.removeView(summary);
        }

        setSelectable(true);
        setEnabled(true);
        // Set our custom views inside the layout
    }

    private void showDisconnectPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.disconnect_dialog_title);
        builder.setMessage(R.string.disconnect_dialog_message);

        // Cancel, do nothing
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WearableManager.getInstance().disconnect();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        Log.d(TAG, "[wearable][onCreateView] begin");
        WearableManager.getInstance().registerWearableListener(mWearableListener);
        PxpFmStatusRegister.getInstance().registerPxpListener(mPxpFmStatusChangeListener);
        PxpFmStatusRegister.getInstance().registerFmListener(mPxpFmStatusChangeListener);
        return LayoutInflater.from(getContext()).inflate(R.layout.main_info_preference_layout,
                parent, false);
    }

    public void releaseListeners() {
        WearableManager.getInstance().unregisterWearableListener(mWearableListener);
        PxpFmStatusRegister.getInstance().unregisterPxpListener(mPxpFmStatusChangeListener);
        PxpFmStatusRegister.getInstance().unregisterFmListener(mPxpFmStatusChangeListener);
    }

    private void updatePreference(int newState) {
        TextView connectState = (TextView) mCurrView.findViewById(R.id.connect_state);
        TextView ringingState = (TextView) mCurrView.findViewById(R.id.ringing_state);
        TextView deviceName = (TextView) mCurrView.findViewById(R.id.device_name);
        TextView summary = (TextView) mCurrView.findViewById(R.id.item_summary);
        ImageView icon = (ImageView) mCurrView.findViewById(R.id.item_image);
        ViewGroup viewGroup = (ViewGroup) mCurrView.findViewById(R.id.custom_pre_layout_id);
        LayoutParams layoutParams;

        if (newState == WearableManager.STATE_CONNECTED || newState == WearableManager.STATE_CONNECT_LOST) {
            mInConnecting = false;
            if (mCurrView == null) {
                return;
            }
        } else if (newState == WearableManager.STATE_CONNECT_FAIL) {
            mInConnecting = false;
            connectState.setText(R.string.disconnected);
            icon.setImageResource(R.drawable.watch_disconnected);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            icon.setLayoutParams(layoutParams);
            if (!WearableManager.getInstance().isReConnecting()) {
                Toast.makeText(mContext, R.string.connect_fail, Toast.LENGTH_SHORT).show();
            }
        }

        Log.d(TAG, "[wearable][updatePreference] newState = " + newState + " state = " + WearableManager.getInstance().getConnectState());
        if (WearableManager.getInstance().isAvailable()) {
            int pxpAlertStatus = PxpFmStatusRegister.getInstance().getPxpAlertStatus();
            int fmAlertStatus = PxpFmStatusRegister.getInstance().getFindMeStatus();
            if (fmAlertStatus == PxpFmStatusRegister.FIND_ME_STATUS_USING) {
                ringingState.setVisibility(TextView.VISIBLE);
            } else {
                ringingState.setVisibility(TextView.INVISIBLE);
            }
            if (pxpAlertStatus == BlePxpFmpConstants.STATE_IN_RANGE_ALERT) {
                connectState.setText(R.string.main_menu_in_range);
                connectState.setTextColor(mContext.getResources().getColor(R.color.red));
            } else if (pxpAlertStatus == BlePxpFmpConstants.STATE_OUT_RANGE_ALERT) {
                connectState.setText(R.string.main_menu_out_of_range);
                connectState.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                connectState.setText(R.string.connected);
                connectState.setTextColor(mContext.getResources().getColor(R.color.black));
            }
            icon.setImageResource(R.drawable.watch_connected);
        } else {
            ringingState.setVisibility(TextView.INVISIBLE);
            connectState.setTextColor(mContext.getResources().getColor(R.color.black));
            if (WearableManager.getInstance().isConnecting()) {
                connectState.setText(R.string.connecting);
            } else if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_DISCONNECTING) {
                connectState.setText(R.string.disconnecting);
            } else if (newState != -1
                    && WearableManager.getInstance().getConnectState() != WearableManager.STATE_CONNECTED) {
                connectState.setText(R.string.disconnected);
            }
            icon.setImageResource(R.drawable.watch_disconnected);
        }

        BluetoothDevice remoteDevice = WearableManager.getInstance().getRemoteDevice();
        if (remoteDevice != null) {
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            icon.setLayoutParams(layoutParams);
//            icon.setPadding(80, 48, 0, 70);
            Log.d(TAG, "[wearable][updatePreference] device = " + remoteDevice.getName());
            if (remoteDevice.getName() != null) {
                mRemoteDevice = remoteDevice;
                String name = queryDeviceName(remoteDevice.getAddress());
                if (!TextUtils.isEmpty(name) && !name.equals(remoteDevice.getName())) {
                    setDeviceName(name);
                } else {
                    setDeviceName(remoteDevice.getName());
                    saveDeviceName(remoteDevice.getAddress(), remoteDevice.getName());
                }
            } else {
                String curName = (String) deviceName.getText();
                Log.d(TAG, "updatePreference curName = " + curName);
                if (TextUtils.isEmpty(curName)
                        || curName.equals(mContext.getResources().getString(R.string.str_mtksmartdevice))
                        || curName.equals(mContext.getResources().getString(R.string.unknown_device))) {
                    String name = queryDeviceName(remoteDevice.getAddress());
                    if (TextUtils.isEmpty(name)) {
                        deviceName.setText(R.string.unknown_device);
                    } else {
                        deviceName.setText(name);
                    }
                }
            }
            viewGroup.removeView(summary);
        } else {
            deviceName.setText("");
            connectState.setText("");
            connectState.setTextColor(mContext.getResources().getColor(R.color.black));
            layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            icon.setLayoutParams(layoutParams);
            icon.setImageResource(R.drawable.watch_add);
        }
    }

    public WearableListener getWearableListener() {
        return mWearableListener;
    }

    /// M: change name feature
    public String getCurrentName() {
        TextView deviceName = (TextView) mCurrView.findViewById(R.id.device_name);
        return (String) deviceName.getText();
    }

    public void setDeviceName(String name) {
        if (!TextUtils.isEmpty(name)) {
            TextView deviceName = (TextView) mCurrView.findViewById(R.id.device_name);
            deviceName.setText(name);
        }
    }

    public void saveDeviceName(String name) {
        Log.d(TAG, "[wearable][saveDeviceName] begin " + name);
        String address = "";
        if (mRemoteDevice != null) {
            address = mRemoteDevice.getAddress();
        }
        if (TextUtils.isEmpty(address) || !BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.d(TAG, "[wearable][saveDeviceName] invalid address");
            return;
        }
        SharedPreferences prefs_address = mContext.getSharedPreferences("device_address", Context.MODE_PRIVATE);
        String bond_address = prefs_address.getString(address, "");

        SharedPreferences prefs = mContext.getSharedPreferences("device_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(address, name);
        if (!TextUtils.isEmpty(bond_address) && BluetoothAdapter.checkBluetoothAddress(bond_address)) {
            editor.putString(bond_address, name);
        }
        editor.commit();
    }

    private void saveDeviceName(String address, String name) {
        Log.d(TAG, "[wearable][saveDeviceName] begin " + address + " " + name);
        SharedPreferences prefs = mContext.getSharedPreferences("device_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(address, name);
        editor.commit();
    }

    private String queryDeviceName(String address) {
        SharedPreferences prefs = mContext.getSharedPreferences("device_name", Context.MODE_PRIVATE);
        String name = prefs.getString(address, "");
        Log.d(TAG, "[wearable][queryDeviceName] begin " + address + " " + name);
        return name;
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
}
