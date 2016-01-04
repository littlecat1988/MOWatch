package com.mtk.bluetoothle;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;

import com.mediatek.wearableProfiles.WearableClientProfile;
import com.mtk.main.HRActivity;
import com.mtk.main.MainService;
import com.mtk.main.UVActivity;

import java.util.TreeSet;
import java.util.UUID;

@SuppressLint("NewApi")
public class UvBleClient extends WearableClientProfile {

	private static final String TAG = "UvBleClient";
	// private static final UUID UUID_SERVICE = UUID
	// .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	// private static final UUID UUID_CHARACTERISTIC_WRITE_AND_READ = UUID
	// .fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	// private static final UUID INTRESTING_CHAR_UUID_1 = UUID
	// .fromString("0000fff2-0000-1000-8000-00805f9b34fb");
	private static final UUID INTRESTING_DESC_UUID_1 = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_SERVICE_UV = UUID
			.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHARACTERISTIC_NOTI_UV = UUID
			.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHARACTERISTIC_WRITE_AND_READ_UV = UUID
			.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	private static final boolean NEED_READ_RSSI = false;
	Object obj = new Object();
	private static BluetoothGatt gatt;

	public UvBleClient() {
		TreeSet<UUID> uuidSet = new TreeSet<UUID>();
		// uuidSet.add(UUID_SERVICE);
		// uuidSet.add(INTRESTING_CHAR_UUID_1);
		// uuidSet.add(UUID_CHARACTERISTIC_WRITE_AND_READ);
		// // If The descriptor is included by Characteristic A, but you don't
		// care
		// // about the callback event of Characteristic A, you only need add
		// the
		// // UUID of the descriptor.
		uuidSet.add(INTRESTING_DESC_UUID_1);
		uuidSet.add(UUID_SERVICE_UV);
		uuidSet.add(UUID_CHARACTERISTIC_NOTI_UV);
		uuidSet.add(UUID_CHARACTERISTIC_WRITE_AND_READ_UV);
		addUuids(uuidSet);
		if (NEED_READ_RSSI) {
			enableRssi(true);
		}
	}

	public void writeCharacteristic(BluetoothGatt paramBluetoothGatt,
			UUID paramUUID1, UUID paramUUID2, byte[] paramArrayOfByte) {
		while (true) {
			BluetoothGattCharacteristic localBluetoothGattCharacteristic;
			synchronized (obj) {
				localBluetoothGattCharacteristic = getBluetoothGattCharacteristic(
						getBluetoothGattService(paramBluetoothGatt, paramUUID1),
						paramUUID2);
				if ((paramBluetoothGatt != null)
						&& (localBluetoothGattCharacteristic != null)) {
					localBluetoothGattCharacteristic.setValue(paramArrayOfByte);
					localBluetoothGattCharacteristic.setWriteType(2);
					paramBluetoothGatt
							.writeCharacteristic(localBluetoothGattCharacteristic);
					return;
				}
				if (paramBluetoothGatt != null)
					if (localBluetoothGattCharacteristic != null) {
						continue;
					} else {
						Log.i(this.TAG, "mBluetoothGattCharacteristic is null");
						return;
					}
			}
		}
	}

	private BluetoothGattCharacteristic getBluetoothGattCharacteristic(
			BluetoothGattService paramBluetoothGattService, UUID paramUUID) {
		if (paramBluetoothGattService != null) {
			BluetoothGattCharacteristic localBluetoothGattCharacteristic = paramBluetoothGattService
					.getCharacteristic(paramUUID);
			if (localBluetoothGattCharacteristic != null)
				return localBluetoothGattCharacteristic;
			Log.i(this.TAG,
					"getBluetoothGattCharacteristic, bluetoothGattServer get characteristic uuid:"
							+ paramUUID + " is null");
		}
		Log.i(this.TAG, "mBluetoothGattServer is null");
		return null;

	}

	private BluetoothGattService getBluetoothGattService(
			BluetoothGatt paramBluetoothGatt, UUID paramUUID) {
		if (paramBluetoothGatt != null) {
			BluetoothGattService localBluetoothGattService = paramBluetoothGatt
					.getService(paramUUID);
			if (localBluetoothGattService != null)
				return localBluetoothGattService;
			Log.i(this.TAG,
					"getBluetoothGattService, bluetoothgatt get service uuid:"
							+ paramUUID + " is null");
		}
		Log.i(this.TAG, "mBluetoothGatt is null");
		return null;

	}

	private void setNotifyTrue(BluetoothGatt gatt) {

		// setCharactoristicNotifyAndWriteDescriptor(getBluetoothGatt(),
		// UUID_SERVICE, INTRESTING_CHAR_UUID_1, INTRESTING_DESC_UUID_1);
		// setCharactoristicNotifyAndWriteDescriptor(getBluetoothGatt(),
		// UUID_SERVICE_UV, UUID_CHARACTERISTIC_NOTI_UV,
		// INTRESTING_DESC_UUID_1);

	}

	public void setCharactoristicNotifyAndWriteDescriptor(
			BluetoothGatt paramBluetoothGatt, UUID paramUUID1, UUID paramUUID2,
			UUID paramUUID3) {
		BluetoothGattCharacteristic localBluetoothGattCharacteristic = getBluetoothGattCharacteristic(
				getBluetoothGattService(paramBluetoothGatt, paramUUID1),
				paramUUID2);
		if ((paramBluetoothGatt != null)
				&& (localBluetoothGattCharacteristic != null)) {
			paramBluetoothGatt.setCharacteristicNotification(
					localBluetoothGattCharacteristic, true);
			BluetoothGattDescriptor localBluetoothGattDescriptor = localBluetoothGattCharacteristic
					.getDescriptor(paramUUID3);
			Log.e(TAG, "localBluetoothGattDescriptor= "
					+ localBluetoothGattDescriptor);
			if (localBluetoothGattDescriptor != null) {
				localBluetoothGattDescriptor
						.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				paramBluetoothGatt
						.writeDescriptor(localBluetoothGattDescriptor);
			}
		}
	}

	// This demo only override these methods, but you can override any method
	// of BluetoothGattCallback
	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status,
			int newState) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onConnectionStateChange");
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int status) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDescriptorWrite= " + status);
		byte[] arrayOfByte = new byte[1];
		arrayOfByte[0] = 1;
		writeCharacteristic(gatt, MainService.UUID_SERVICE_UV,
				MainService.UUID_CHARACTERISTIC_WRITE_AND_READ_UV, arrayOfByte);
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		// TODO Auto-generated method stub
		this.gatt = gatt;
		Log.e(TAG, "onServicesDiscovered= " + status);
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCharacteristicChanged");
		byte[] arrayOfByte = characteristic.getValue();
//		for (int i = 0; i < arrayOfByte.length; i++) {
//			Log.e(TAG, "uv_arrayOfByte[" + i + "]= " + arrayOfByte[i]);
//		}
		int index_uv = arrayOfByte[1];	
		Intent intent_uv = new Intent(UVActivity.DATA_UV);
		intent_uv.putExtra(UVActivity.INDEX_UV, index_uv);
		MainService.getInstance().sendBroadcast(intent_uv);
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCharacteristicRead");
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCharacteristicWrite");
		Intent intent_noti = new Intent(HRActivity.INIT_NOTI);
		MainService.getInstance().sendBroadcast(intent_noti);
		
	}

	@Override
	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		// TODO Auto-generated method stub
	}

	public static BluetoothGatt getGatt() {
		// TODO Auto-generated method stub
		return gatt;
	}
}
