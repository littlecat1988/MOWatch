package care.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import care.service.MessageService;
import care.utils.Constants;

public class MessageReceive extends BroadcastReceiver {
    public MessageReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        Intent service = new Intent(context,MessageService.class);
        if(Constants.INTERFILTER.equals(action)){
            Bundle bundle = intent.getExtras();
            service.putExtras(bundle);
        }
        service.setAction(action);
        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(service);
    }
}
