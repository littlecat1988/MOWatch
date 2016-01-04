package care.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import care.application.XcmApplication;
import care.db.ProtocolData;
import care.db.manager.UpdateDB;
import care.utils.Constants;

public class MessageService extends Service {

	private XcmApplication mXcmApplication;
	private UpdateDB mUpdateDB; // 数据库更新类
	private ProtocolData mProtocol;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mXcmApplication = XcmApplication.getInstance();
		mProtocol = mXcmApplication.getProtocolData();
		mUpdateDB = mXcmApplication.getUpdateDB(mXcmApplication);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		String action = intent.getAction();
		if (Constants.INTERFILTER.equals(action)) {
			Bundle bundle = intent.getExtras(); //更新消息到界面
			sendMsgToChatWin(bundle);
		}
		return Service.START_NOT_STICKY;
	}

	private void sendMsgToChatWin(Bundle bundle) {
		// TODO Auto-generated method stub
		Intent msgIntent = new Intent(Constants.INTERFILTER);
		msgIntent.putExtras(bundle);
		LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);
	}
}
