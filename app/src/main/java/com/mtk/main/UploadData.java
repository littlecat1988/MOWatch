package com.mtk.main;

import java.util.ArrayList;
import java.util.Calendar;
import com.gomtel.database.DatabaseProvider;
import com.gomtel.util.DialogHelper;
import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.SleepDay;
import com.gomtel.util.SleepInfo;
import com.mtk.btnotification.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

public class UploadData extends AsyncTask<Object, Object, Object> {

	private static final String TAG = "UploadData";
	public static final String mFormat = "HH:mm";

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	private ProgressDialog dialog_sync;
	private Calendar mCalendar = Calendar.getInstance();

	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (mHandler != null) {
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	private Handler mHandler;
	private Context context;
	private ArrayList<SleepDay> sleepList;
	private HttpUtils httpUtils;
	private int userId;

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		mHandler = (Handler) params[0];
		context = (Context) params[1];

		userId = (Integer) params[2];
		if (httpUtils == null) {
			httpUtils = HttpUtils.getInstance();
		}
		
		sleepList = (ArrayList<SleepDay>) DatabaseProvider
				.queryHistorySleep(context);
		Log.e(TAG,"sleepList= "+sleepList.size());
		for (int i = 0; i < sleepList.size(); i++) {
			SleepDay sleepDay = sleepList.get(i);
			SleepInfo sleep_info = new SleepInfo();
			sleep_info.userid = userId;
			sleep_info.date = Global.sdf_2.format(sleepDay.getDate().getTime());
			sleep_info.starttime = transformTime(sleepDay.getStartSleepTime());
			sleep_info.totaltime = (int) sleepDay.getSleepTotal();
			sleep_info.deepsleep = (int) sleepDay.getDeepSleepTime();
			sleep_info.lightsleep = (int) sleepDay.getLightSleepTime();
			httpUtils.postSleepInfo(context, sleep_info,
					(SyncDataActivity) params[1]);
		}
		
		// Log.e(TAG,"sleepList = "+sleepList.get(0));

		return null;
	}

	private String transformTime(long startSleepTime) {
		// TODO Auto-generated method stub
		mCalendar.setTimeInMillis(startSleepTime * 1000);
		return (String) DateFormat.format(mFormat, mCalendar);
	}

}
