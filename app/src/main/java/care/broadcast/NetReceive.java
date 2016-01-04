package care.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import care.utils.Constants;

public class NetReceive extends BroadcastReceiver{

	private final String netACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(netACTION)){
			boolean isNet = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			
			Constants.IS_OPEN_NETWORK = !isNet;
		}
	}

}
