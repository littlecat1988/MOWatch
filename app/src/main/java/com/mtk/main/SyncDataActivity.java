package com.mtk.main;

import java.util.ArrayList;

import com.gomtel.util.DialogHelper;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.SleepInfo;
import com.gomtel.util.SportInfo;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SyncDataActivity extends Activity implements HttpUtils.HttpCallback{

	private static final String TAG = "SyncDataActivity";
	private ProgressDialog dialog_upload;
	private Button upload_data;
	private Button download_data;
	protected Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showSyncDialog();
				break;
			case 1:
				DialogHelper.hideDialog(dialog_upload);
				Toast.makeText(SyncDataActivity.this,getResources().getString(R.string.upload_complete),Toast.LENGTH_LONG).show();
				break;
			case 2:

				break;
			default:
				break;
			}
		}
	};
	private OnClickListener myClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.upload_data:
				new UploadData().execute(mHandler,SyncDataActivity.this,userId);
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				}
//				showSyncDialog();
//				SleepInfo sleep_info = new SleepInfo();
//				sleep_info.userid = userId;
//				sleep_info.date = "2015-08-07";
//				sleep_info.starttime = "22:10";
//				sleep_info.totaltime = 420;
//				sleep_info.deepsleep = 100;
//				sleep_info.lightsleep = 120;
//				httpUtils.postSleepInfo(SyncDataActivity.this,sleep_info,SyncDataActivity.this);
				break;
			case R.id.download_data:
//				httpUtils.loadSleepRecorder(SyncDataActivity.this,userId,SyncDataActivity.this);
//				httpUtils.loadSportRecorder(SyncDataActivity.this,userId,SyncDataActivity.this);
				startActivity(new Intent(SyncDataActivity.this, DownloadData.class));
				break;

			default:
				break;
			}
		}
		
	};
	private HttpUtils httpUtils;
	private SharedPreferences mPrefs;
	private int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syncdata);
		initUI();
	}
	
	protected void showSyncDialog()
	  {
	    if (dialog_upload == null)
	    {
	    	dialog_upload = DialogHelper.showProgressDialog(SyncDataActivity.this, getResources().getString(R.string.upload_wait));
	    	dialog_upload.setCanceledOnTouchOutside(false);
	    	dialog_upload.show();
	      return;
	    }
	    dialog_upload.show();
	  }

	private void initUI() {
		// TODO Auto-generated method stub
		upload_data = (Button)findViewById(R.id.upload_data);
		download_data = (Button)findViewById(R.id.download_data);
		upload_data.setOnClickListener(myClickListener);
		download_data.setOnClickListener(myClickListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (httpUtils == null) {
			httpUtils = HttpUtils.getInstance();
		}
		if (mPrefs == null)
			mPrefs = getSharedPreferences("WATCH", 0);
		userId = mPrefs.getInt("LOG_USERID",0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DialogHelper.dismissDialog(dialog_upload);
	}

	@Override
	public void onHttpRequestComplete(int what, int result, Object object) {
		// TODO Auto-generated method stub
		switch (what) {
		case HttpUtils.HTTP_REQUEST_SESULT_SLEEPINFO:
			Log.e(TAG,"result= "+result);
			break;
		case HttpUtils.HTTP_REQUEST_SESULT_HRINFO:
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onError(int what) {
		// TODO Auto-generated method stub

	}

}
