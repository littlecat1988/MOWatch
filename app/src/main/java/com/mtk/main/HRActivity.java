package com.mtk.main;

import java.util.ArrayList;

import com.gomtel.util.DialogHelper;
import com.mediatek.wearable.WearableManager;
import com.mtk.bluetoothle.CustomizedBleClient;
import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HRActivity extends Activity {

	public static final String DATA_HR = "data_hr";
	public static final String NUMBER_HR = "number_hr";
	public static final String DATA_HR_HISTORY = "data_hr_history";
	public static final String NUMBER_HR_HISTORY = "number_hr_history";
	public static final String INIT_NOTI = "init_noti";
	private TextView status_hr;
	private TextView num_hr;
	private TextView time_count;
	private int number_hr;
	private ProgressDialog dialog_sync;
	private int time = 30;
	private Runnable runnable = new Runnable() {   
		@Override  
        public void run() {  
			time--;  
			time_count.setText("" + time); 
			if(time == 0){
            	return ;
            }
            mHandler.postDelayed(this, 1000);
            
        }  
    };  
    private Runnable runnable_sync = new Runnable() {   
		@Override  
        public void run() {  
			DialogHelper.hideDialog(dialog_sync);
        }  
    };  
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				updateData(number_hr);
				break;
			case 1:
				setHistoryHR();
				break;
			case 2:
				DialogHelper.hideDialog(dialog_sync);
				break;
			default:
				break;
			}
			
		}

	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(DATA_HR)){
			number_hr = intent.getIntExtra(NUMBER_HR, 0);
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendMessage(msg);
			}
			
			if(action.equals(DATA_HR_HISTORY)){
				number_hr_history = intent.getIntExtra(NUMBER_HR_HISTORY, 0);
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				}
			if(action.equals(INIT_NOTI)){
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				}
		}
	};
	private Button start;
//	private ImageView icon_hr;
	private int number_hr_history;
	private TextView tips_hr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heartrate);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DATA_HR);
		filter.addAction(DATA_HR_HISTORY);
		filter.addAction(INIT_NOTI);
		registerReceiver(mReceiver, filter);
		byte[] arrayOfByte = new byte[2];
		arrayOfByte[0] = 0x10;
		arrayOfByte[1] = 0x0;
        if(MainService.getInstance() != null) {
            MainService.getInstance().writeCharacteristic(
                    CustomizedBleClient.getGatt(), MainService.UUID_SERVICE,
                    MainService.UUID_CHARACTERISTIC_WRITE_AND_READ, arrayOfByte);
            showSyncDialog();
            mHandler.postDelayed(runnable_sync, 15000);
        }

	}

	protected void setHistoryHR() {
		// TODO Auto-generated method stub
		tips_hr.setText(getResources().getString(R.string.tips_title_hr)+String.valueOf(number_hr_history)+getResources().getString(R.string.unit_hr));
	}
	
	private void showSyncDialog()
	  {
	    if (dialog_sync == null)
	    {
	      dialog_sync = DialogHelper.showProgressDialog(this, getString(R.string.init_ble));
	      this.dialog_sync.setCanceledOnTouchOutside(false);
	      this.dialog_sync.show();
	      return;
	    }
	    this.dialog_sync.show();
	  }

	protected void updateData(int number_hr) {
		// TODO Auto-generated method stub
		if(number_hr > 20){
		status_hr.setText(R.string.complete);
		num_hr.setText(String.valueOf(number_hr));
		start.setClickable(true);
		start.setText(getResources().getString(R.string.start));
		time_count.setVisibility(View.GONE);
		mHandler.removeCallbacks(runnable);
		time = 30;
//		icon_hr.clearAnimation();
//		icon_hr.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initUI();
//		 MainService.getInstance().setNotifyHRTrue(CustomizedBleClient.getGatt(),true);
	}

	private void initUI() {
		// TODO Auto-generated method stub
//		icon_hr = (ImageView) findViewById(R.id.icon_hr);
//		icon_hr.setVisibility(View.GONE);
		tips_hr = (TextView) findViewById(R.id.status_hr);;
		status_hr = (TextView) findViewById(R.id.status_hr);
		num_hr = (TextView) findViewById(R.id.num_hr);
		start = (Button) findViewById(R.id.start);
		time_count = (TextView) findViewById(R.id.time_count);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// MainService.getInstance().setNotifyHRTrue(CustomizedBleClient.getGatt(),true);
				if(WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED){
				byte[] arrayOfByte = new byte[1];
				arrayOfByte[0] = 0x1;
				MainService.getInstance().writeCharacteristic(
						CustomizedBleClient.getGatt(),
						MainService.UUID_SERVICE_HR,
						MainService.UUID_CHARACTERISTIC_WRITE_HR, arrayOfByte);
				status_hr.setText(R.string.detecting);
				start.setClickable(false);
				start.setText("...");
				time_count.setVisibility(View.VISIBLE);
				mHandler.postDelayed(runnable, 1000);  
//				AlphaAnimation animation = new AlphaAnimation(0.2f,1.0f);   
//				animation.setDuration(1000);
//				animation.setRepeatCount(Animation.INFINITE);
//				animation.setRepeatMode(Animation.REVERSE);
//				icon_hr.startAnimation(animation);  
//				icon_hr.setVisibility(View.VISIBLE);
				}else{
//					start.setClickable(false);
					Toast.makeText(HRActivity.this,HRActivity.this.getResources().getString(R.string.hr_disconnected),Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
		DialogHelper.dismissDialog(dialog_sync);
	}

}
