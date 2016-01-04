package care.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import care.service.BackHttpService;

public class RePeatReceive extends BroadcastReceiver{

	private final String netACTION = "org.care.lower";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(netACTION)){
			Intent service = new Intent(context,BackHttpService.class);
			service.setAction(netACTION);
			context.startService(service);
		}
	}

}
