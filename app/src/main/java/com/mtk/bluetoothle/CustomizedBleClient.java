package com.mtk.bluetoothle;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.gomtel.util.SleepDay;
import com.mediatek.wearableProfiles.WearableClientProfile;
import com.mtk.main.FirstActivity;
import com.mtk.main.HRActivity;
import com.mtk.main.MainService;
import com.mtk.main.SleepActivity;
import com.mtk.main.SportActivity;
import com.mtk.main.UVActivity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.UUID;

@SuppressLint("NewApi")
public class CustomizedBleClient extends WearableClientProfile {
	public static final String mFormat = "yyyy-MM-dd HH:mm";// h:mm:ss aa
	public static final DecimalFormat df_1 = new DecimalFormat("00");
	public static final DecimalFormat df_1_1 = new DecimalFormat("0");
	public static final DecimalFormat df_2 = new DecimalFormat("0.0");
	public static final DecimalFormat df_3 = new DecimalFormat("0.00");
	public static final SimpleDateFormat sdf_3 = new SimpleDateFormat(
			"yyyy-MM-dd HH");
	private static final String TAG = "CustomizedBleClient";
	private static final UUID UUID_SERVICE = UUID
			.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHARACTERISTIC_WRITE_AND_READ = UUID
			.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	private static final UUID INTRESTING_CHAR_UUID_1 = UUID
			.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
	private static final UUID INTRESTING_DESC_UUID_1 = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_SERVICE_UV = UUID
			.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHARACTERISTIC_NOTI_UV = UUID
			.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHARACTERISTIC_WRITE_AND_READ_UV = UUID
			.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	public static final UUID UUID_SERVICE_HR = UUID
			.fromString("0000180d-0000-1000-8000-00805f9b34fb");
	public static final UUID UUID_CHARACTERISTIC_NOTI_HR = UUID
			.fromString("00002a37-0000-1000-8000-00805f9b34fb");
	private static final boolean NEED_READ_RSSI = false;
	private static final int HISTORY_STEP = 129;
	private static final int TODAY_STEP = 130;
	private static final int DATA_HR = 0;
	private static final int DATA_UV = -28;
	private static final int DATA_PERSON = 133;
	private static final int DATA_HR_HISTORY = 144;
	private static final int DATA_CLEAR_HR = 145;
	private static final int DATA_SLEEP_HISTORY = 160;
	private static final int DATA_SLEEP_START = 162;
	private static final int DATA_SLEEP_DEEP = 163;
	private static final int DATA_SLEEP_WAKE = 164;
	private static final int DATA_SLEEP_ENABLE = 136;
	private static final int DATA_SLEEP_STATUS = 137;
	private static final int DATA_CLEAR_SLEEP = 161;
	private static final long TIME_ZONE = 8*60*60;
	private static final int ENABLE_PEDOMETER = 132;
	private static final int STATE_SPORT = 134;
	private static final int RSP_TEMP = 176;
	private static final int RSP_WEATHER = 177;
	private static final int RSP_CITY = 178;
	Object obj = new Object();
	private Calendar mCalendar = Calendar.getInstance();
	private ArrayList listOfSteps = new ArrayList();
	private ArrayList listOfSleep = new ArrayList();
	private int totalstep;
	private int number_hr;
	private int hour;
	private int count = 0x0;
	private long totalTime;
	private long deepSleepTime;
	private long wakeTime;
	private long startSleepTime;
	private boolean last_completed;
	// private int TotalStep = 0;
	private static BluetoothGatt gatt;

	public CustomizedBleClient() {
		TreeSet<UUID> uuidSet = new TreeSet<UUID>();
		uuidSet.add(UUID_SERVICE);
		uuidSet.add(INTRESTING_CHAR_UUID_1);
		uuidSet.add(UUID_CHARACTERISTIC_WRITE_AND_READ);
		// If The descriptor is included by Characteristic A, but you don't care
		// about the callback event of Characteristic A, you only need add the
		// UUID of the descriptor.
		uuidSet.add(INTRESTING_DESC_UUID_1);
		uuidSet.add(UUID_SERVICE_HR);
		uuidSet.add(UUID_CHARACTERISTIC_NOTI_HR);
		// uuidSet.add(UUID_CHARACTERISTIC_WRITE_AND_READ_UV);
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

		setCharactoristicNotifyAndWriteDescriptor(getBluetoothGatt(),
				UUID_SERVICE, INTRESTING_CHAR_UUID_1, INTRESTING_DESC_UUID_1);
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
		byte[] arrayOfByte = new byte[2];
		arrayOfByte[0] = 0x02;
		arrayOfByte[1] = 12;
		this.gatt = gatt;
		// MainService.getInstance().writeCharacteristic(gatt,UUID_SERVICE,UUID_CHARACTERISTIC_WRITE_AND_READ,arrayOfByte);
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		// TODO Auto-generated method stub
		// if(gatt.)
		setNotifyTrue(gatt);
		Log.e(TAG, "onServicesDiscovered= " + status);
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCharacteristicChanged");
		int n = 0;
		int[] arrayOfInt = new int[20];
		String[] arrayOfStr = new String[20];
		byte[] arrayOfByte = characteristic.getValue();
		// while (n < arrayOfByte.length) {
		// arrayOfStr[n] = String.valueOf((char) arrayOfByte[n]);
		// ++n;
		// }
		while (n < arrayOfByte.length) {
			if (arrayOfByte[n] < 0) {
				arrayOfInt[n] = arrayOfByte[n] + 256;
			} else {
				arrayOfInt[n] = arrayOfByte[n];
			}
			++n;
		}
//		for (int m = 0; m < arrayOfInt.length; m++) {
//			Log.e(TAG, "arrayOfInt[" + m + "]= " + arrayOfInt[m]);
//		}

		int flag = arrayOfInt[0];
//		Log.e(TAG, "flag= " + flag);
		switch (flag) {
		case HISTORY_STEP:
//			for (int m = 0; m < arrayOfInt.length; m++) {
//				Log.e(TAG, "arrayOfInt[" + m + "]= " + arrayOfInt[m]);
//			}
			int getHistoryDay = arrayOfInt[1];
			// try {
			// getHistoryDay = Integer.parseInt(arrayOfStr[3] + arrayOfStr[4]);
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			if (getHistoryDay > 0) {
				byte[] day_remind = new byte[2];
				day_remind[0] = 0x01;
				day_remind[1] = (byte) (31 - getHistoryDay);
				MainService.getInstance().writeCharacteristic(gatt,
						MainService.UUID_SERVICE,
						MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
						day_remind);
			}
			break;
		case TODAY_STEP:
			if (arrayOfByte[1] == 1) {
//				Log.e(TAG,"arrayOfByte[2]= "+arrayOfByte[2]);
				if (arrayOfByte[2] == 0) {
					totalstep = 0;
					listOfSteps.clear();
				}
				if (arrayOfByte[2] < 18) {
					hour = arrayOfByte[2];
					Intent intent = new Intent(SportActivity.DATA_STEP);
					intent.putExtra(SportActivity.TIME, arrayOfByte[2] + 6);
					MainService.getInstance().sendBroadcast(intent);
					totalstep += parseStep(arrayOfByte);
				}

				if (arrayOfByte[2] == 18) {
//					last_completed = true;
					totalstep += parseStep(arrayOfByte);
//					Log.e(TAG, "listOfSteps= " + listOfSteps.size());
					Intent intent = new Intent(SportActivity.DATA_TOTAL_STEP);
					intent.putExtra(SportActivity.TOTAL_STEP, totalstep);
					intent.putExtra(SportActivity.TOTAL_LIST,
							(Serializable) listOfSteps);
					MainService.getInstance().sendBroadcast(intent);
					totalstep = 0;
					listOfSteps.clear();
				}
			} else {
				Intent intent = new Intent(SportActivity.DATA_STEP);
				intent.putExtra(SportActivity.TIME, hour + 6);
				MainService.getInstance().sendBroadcast(intent);
			}
			break;
		case DATA_UV:
			// int index_uv = arrayOfByte[1];
			//
			// Intent intent_uv = new Intent(UVActivity.DATA_UV);
			// intent_uv.putExtra(UVActivity.INDEX_UV, index_uv);
			// MainService.getInstance().sendBroadcast(intent_uv);
			break;
		case DATA_HR:
			if (arrayOfByte.length > 1) {
				number_hr = arrayOfByte[1];
			}
			if (number_hr < 0) {
				number_hr += 256;
			}
			Intent intent_hr = new Intent(HRActivity.DATA_HR);
			intent_hr.putExtra(HRActivity.NUMBER_HR, number_hr);
			MainService.getInstance().sendBroadcast(intent_hr);
			break;
		case DATA_PERSON:
			Intent intent_person = new Intent(SportActivity.DATA_PERSON);
			intent_person.putExtra(SportActivity.HEIGHT, arrayOfInt[2]);
			intent_person.putExtra(SportActivity.WEIGHT, arrayOfInt[3]);
			intent_person.putExtra(SportActivity.GOAL, arrayOfInt[4]);
			MainService.getInstance().sendBroadcast(intent_person);
//			byte[] stateOfSport = new byte[1];
//			stateOfSport[0] = 0x06;
//			MainService.getInstance().writeCharacteristic(
//					CustomizedBleClient.getGatt(),
//					MainService.UUID_SERVICE,
//					MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
//					stateOfSport);
			break;
		case DATA_HR_HISTORY:
//			parseHR(arrayOfInt);
			Intent intent_hr_history = new Intent(HRActivity.DATA_HR_HISTORY);
			intent_hr_history.putExtra(HRActivity.NUMBER_HR_HISTORY,
					arrayOfInt[6]);
			MainService.getInstance().sendBroadcast(intent_hr_history);
			MainService.getInstance().setNotifyHRTrue(
					CustomizedBleClient.getGatt(), true);
			break;

		case DATA_SLEEP_HISTORY:
			for (int m = 0; m < arrayOfInt.length; m++)
				Log.e(TAG, "sleep[" + m + "]= " + arrayOfInt[m]);
			
//			if (arrayOfInt[2] > 0) {
				parseSleepTime(arrayOfInt);
//				count ++;
//				Log.e(TAG,"count= "+count);
				SleepDay sleepDay = new SleepDay();
//				sleepDay.setDate(mCalendar);
				sleepDay.setStartSleepTime(startSleepTime);
				sleepDay.setDeepSleepTime(deepSleepTime);
				sleepDay.setWakeTime(wakeTime);
				sleepDay.setSleepTotal(totalTime);
//				sleepDay.setDeepSleepMin(step);
				listOfSleep.add(sleepDay);
//				byte[] sleepOfByte = new byte[2];
//				sleepOfByte[0] = 0x20;
//				sleepOfByte[1] = (byte)count;
//				MainService.getInstance().writeCharacteristic(
//						CustomizedBleClient.getGatt(),
//						MainService.UUID_SERVICE,
//						MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
//						sleepOfByte);

//			}else{
//				count = 0;
				Intent intent_sleep_history = new Intent(SleepActivity.DATA_SLEEP_HISTORY);
				intent_sleep_history.putExtra(SleepActivity.DATA_SLEEP_LIST,
						(Serializable) listOfSleep);
				MainService.getInstance().sendBroadcast(intent_sleep_history);
				listOfSleep.clear();
				byte[] arrayOfSleep = new byte[1];
				arrayOfByte[0] = 0x09;
				MainService.getInstance().writeCharacteristic(
						CustomizedBleClient.getGatt(), MainService.UUID_SERVICE,
						MainService.UUID_CHARACTERISTIC_WRITE_AND_READ, arrayOfByte);
//			}

			break;
		case ENABLE_PEDOMETER:
			Intent intent_sport = new Intent(SportActivity.ENABLE_PEDOMETER);
			intent_sport.putExtra(SportActivity.ENABLE_SPORT,
					arrayOfInt[1]);
			MainService.getInstance().sendBroadcast(intent_sport);
			break;
		case STATE_SPORT:
			Log.e(TAG,"STATE_SPORT");
			Intent intent_state = new Intent(SportActivity.STATE_SPORT);
			intent_state.putExtra(SportActivity.ENABLE_SPORT,
					arrayOfInt[1]);
			intent_state.putExtra(SportActivity.WALK_OR_RUN,
					arrayOfInt[2]);
			MainService.getInstance().sendBroadcast(intent_state);
			break;
			
//		case DATA_SLEEP_START:
//			if(arrayOfInt[1] == 1){
//				startSleepTime = Long.parseLong(
//						Integer.toHexString(arrayOfInt[5])
//						+ Integer.toHexString(arrayOfInt[4])
//						+ Integer.toHexString(arrayOfInt[3])
//						+ Integer.toHexString(arrayOfInt[2]), 16)- TIME_ZONE;
//				totalTime = Long.parseLong(
//						Integer.toHexString(arrayOfInt[9])
//						+ Integer.toHexString(arrayOfInt[8])
//						+ Integer.toHexString(arrayOfInt[7])
//						+ Integer.toHexString(arrayOfInt[6]), 16);
//				
//				
//			}
//			break;
//		case DATA_SLEEP_DEEP:
//			break;
//		case DATA_SLEEP_WAKE:
//			break;
		case DATA_SLEEP_ENABLE:
			break;
		case DATA_SLEEP_STATUS:
			Log.e(TAG,"DATA_SLEEP_STATUS= "+arrayOfInt[1]);
			Intent intent_sleep_status = new Intent(SleepActivity.DATA_SLEEP_STATUS);
			intent_sleep_status.putExtra(SleepActivity.DATA_SLEEP_CURRENT_STATUS,
					arrayOfInt[1]);
			MainService.getInstance().sendBroadcast(intent_sleep_status);
			break;
		case RSP_TEMP:
			Log.e(TAG,"RSP_TEMP= "+arrayOfInt[1]);
			Intent intent_rsp_temp = new Intent(FirstActivity.RSP_TEMP);
			MainService.getInstance().sendBroadcast(intent_rsp_temp);
			break;
			
		case RSP_WEATHER:
			Log.e(TAG,"RSP_WEATHER= "+arrayOfInt[1]);
			Intent intent_rsp_weather = new Intent(FirstActivity.RSP_WEATHER);
			MainService.getInstance().sendBroadcast(intent_rsp_weather);
			break;
			
		case RSP_CITY:
			Log.e(TAG,"RSP_CITY= "+arrayOfInt[1]);
			Intent intent_rsp_city = new Intent(FirstActivity.RSP_CITY);
			MainService.getInstance().sendBroadcast(intent_rsp_city);
			break;
		default:
			break;
		}
		// for (int m = 0; m < arrayOfStr.length; m++) {
		// Log.e(TAG, "arrayOfStr[" + m + "]= " + arrayOfStr[m]);
		// }

	}

	private void parseHR(int[] arrayOfInt) {
		// TODO Auto-generated method stub
		mCalendar.setTimeInMillis(Long.parseLong(
				Integer.toHexString(arrayOfInt[5])
						+ Integer.toHexString(arrayOfInt[4])
						+ Integer.toHexString(arrayOfInt[3])
						+ Integer.toHexString(arrayOfInt[2]), 16) * 1000);
		Log.e(TAG, "time= " + DateFormat.format(mFormat, mCalendar));
		Log.e(TAG, "hr= " + arrayOfInt[6]);
	}
	
	private void parseSleepTime(int[] arrayOfInt) {
		// TODO Auto-generated method stub
		mCalendar.setTimeInMillis((Long.parseLong(
				Integer.toHexString(arrayOfInt[5])
						+ Integer.toHexString(arrayOfInt[4])
						+ Integer.toHexString(arrayOfInt[3])
						+ Integer.toHexString(arrayOfInt[2]), 16)- TIME_ZONE) * 1000);
		startSleepTime = Long.parseLong(
				Integer.toHexString(arrayOfInt[5])
				+ Integer.toHexString(arrayOfInt[4])
				+ Integer.toHexString(arrayOfInt[3])
				+ Integer.toHexString(arrayOfInt[2]), 16)- TIME_ZONE;
		totalTime = Long.parseLong(
				Integer.toHexString(arrayOfInt[9])
				+ Integer.toHexString(arrayOfInt[8])
				+ Integer.toHexString(arrayOfInt[7])
				+ Integer.toHexString(arrayOfInt[6]), 16);

		deepSleepTime = Long.parseLong(
				Integer.toHexString(arrayOfInt[13])
				+ Integer.toHexString(arrayOfInt[12])
				+ Integer.toHexString(arrayOfInt[11])
				+ Integer.toHexString(arrayOfInt[10]), 16);
		wakeTime = Long.parseLong(
				Integer.toHexString(arrayOfInt[17])
				+ Integer.toHexString(arrayOfInt[16])
				+ Integer.toHexString(arrayOfInt[15])
				+ Integer.toHexString(arrayOfInt[14]), 16);
		Log.e(TAG, "totalTime= " + totalTime);
		Log.e(TAG, "time= " + DateFormat.format(mFormat, mCalendar));
	}

	private int parseStep(byte[] arrayOfByte) {
		// TODO Auto-generated method stub
		int n = 0;
		int[] arrayOfInt = new int[arrayOfByte.length];
		int TotalStep = 0;
		while (n < arrayOfByte.length) {
			if (arrayOfByte[n] >= 0)
				arrayOfInt[n] = arrayOfByte[n];
			else
				arrayOfInt[n] = (256 + arrayOfByte[n]);
			++n;
		}
		int i = 0;
		int year = 100 * arrayOfInt[4] + arrayOfInt[5];
		int month = arrayOfInt[6];
		int day = arrayOfInt[7];
		int startHour = arrayOfInt[2];
		while (i < 6) {
			String str = year + "-" + df_1.format(month) + "-"
					+ df_1.format(day) + " " + (startHour + i);
			int high = 8 + i * 2;
			int low = 9 + i * 2;
			// int step =
			// Integer.parseInt(byteToBinaryString((int)arrayOfByte[high])
			// + byteToBinaryString((int)arrayOfByte[low]), 2);
			int step = arrayOfInt[high] * 256 + arrayOfInt[low];
			Date localDate;
			try {
				localDate = sdf_3.parse(str);
				Calendar localCalendar = Calendar.getInstance();
				localCalendar.setTime(localDate);
				HistoryHour localHistoryHour = new HistoryHour();
				localHistoryHour.setDate(localCalendar);
				localHistoryHour.setStep(step);
				listOfSteps.add(localHistoryHour);
				TotalStep += step;
				i++;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return TotalStep;
	}

	private static String byteToBinaryString(int paramInt) {
		String str = Integer.toBinaryString(paramInt);
		Log.e(TAG, "byteToBinaryString= " + str);
		int i = 0;
		if (str != null) {
			i = str.length();
			if (i >= 8)
				return str;
		}
		for (int j = 0;; ++j) {
			if (j >= 8 - i)
				return str;
			str = (new StringBuilder("0")).append(str).toString();
		}
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
