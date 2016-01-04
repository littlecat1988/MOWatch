package com.mtk.main;

import com.gomtel.app.uv.UVController;
import com.mtk.bluetoothle.CustomizedBleClient;
import com.mtk.bluetoothle.UvBleClient;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class UVActivity extends Activity {
	public static final String DATA_UV = "data_uv";
	public static final String INDEX_UV = "index_uv";
	private static final String TAG = "UVActivity";
	private int index_uv = 0;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			updateData(index_uv);
		}

	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.e(TAG,"index_uv= "+intent.getIntExtra(INDEX_UV, 0));
			index_uv = intent.getIntExtra(INDEX_UV, 0);
			Message msg = new Message();
			mHandler.sendMessage(msg);
		}
	};
	private ImageView index;
	private TextView tips_uv;
	byte[] arrayOfByte = new byte[1];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uv);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DATA_UV);
		registerReceiver(mReceiver, filter);
		MainService.getInstance().setNotifyUVTrue(UvBleClient.getGatt(), true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initUI();
	}

	private void initUI() {
		// TODO Auto-generated method stub
		 
		arrayOfByte[0] = 0x01;
//		byte[] arrayOfStep = new byte[2]; 
//		arrayOfStep[0] = 0x02;
//		arrayOfStep[1] = (byte)0;
		 MainService.getInstance().writeCharacteristic(UvBleClient.getGatt(),MainService.UUID_SERVICE_UV,MainService.UUID_CHARACTERISTIC_WRITE_AND_READ_UV,arrayOfByte);
//		 UVController.getInstance().send("GT_UVDET gt_uvdet 0 0 1 ",
//		 arrayOfByte, false, false, 0);
//		 UVController.getInstance().send("GT_HRP gt_hrp 0 0 1 ",
//		 arrayOfByte, false, false, 0);
//		 UVController.getInstance().send("GT_PED gt_ped 0 0 2 ",
//				 arrayOfStep, false, false, 0);
		index = (ImageView) findViewById(R.id.index);
		tips_uv = (TextView) findViewById(R.id.tips_uv);
	}
	protected void updateData(int indexOfUV) {
		// TODO Auto-generated method stub
		Log.e(TAG,"indexOfUV= "+indexOfUV);
		switch (indexOfUV) {
		case 1:
			index.setImageResource(R.drawable.uv_01);
			tips_uv.setText(getResources().getString(R.string.content_uv1));
			break;
			
		case 2:
			index.setImageResource(R.drawable.uv_02);
			tips_uv.setText(getResources().getString(R.string.content_uv1));
			break;
			
		case 3:
			index.setImageResource(R.drawable.uv_03);
			tips_uv.setText(getResources().getString(R.string.content_uv2));
			break;
			
		case 4:
			index.setImageResource(R.drawable.uv_04);
			tips_uv.setText(getResources().getString(R.string.content_uv2));
			break;
			
		case 5:
			index.setImageResource(R.drawable.uv_05);
			tips_uv.setText(getResources().getString(R.string.content_uv2));
			break;
			
		case 6:
			index.setImageResource(R.drawable.uv_06);
			tips_uv.setText(getResources().getString(R.string.content_uv3));
			break;
			
		case 7:
			index.setImageResource(R.drawable.uv_07);
			tips_uv.setText(getResources().getString(R.string.content_uv3));
			break;
			
		case 8:
			index.setImageResource(R.drawable.uv_08);
			tips_uv.setText(getResources().getString(R.string.content_uv3));
			break;
		case 9:
			index.setImageResource(R.drawable.uv_09);
			tips_uv.setText(getResources().getString(R.string.content_uv4));
			break;
		case 10:
			index.setImageResource(R.drawable.uv_10);
			tips_uv.setText(getResources().getString(R.string.content_uv4));
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        arrayOfByte[0] = 0x0;
        MainService.getInstance().writeCharacteristic(UvBleClient.getGatt(),MainService.UUID_SERVICE_UV,MainService.UUID_CHARACTERISTIC_WRITE_AND_READ_UV,arrayOfByte);
//		MainService.getInstance().setNotifyUVTrue(UvBleClient.getGatt(), false);
        UVController.getInstance().send("GT_UVDET gt_uvdet 0 0 1 ",
       		 arrayOfByte, false, false, 0);
		unregisterReceiver(mReceiver);
	}

}
