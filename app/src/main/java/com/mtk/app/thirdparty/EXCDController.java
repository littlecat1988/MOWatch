
package com.mtk.app.thirdparty;

import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mediatek.wearable.Controller;
import com.mediatek.wearable.WearableManager;
import com.mtk.main.BTNotificationApplication;

public class EXCDController extends Controller {
    private static final String sControllerTag = "EXCDController";

    private static final String TAG = "AppManager/EXCDController";

    private static EXCDController mInstance;

    private Context mContext = BTNotificationApplication.getInstance().getApplicationContext();

    public static final String EXTRA_DATA = "EXTRA_DATA";

    private EXCDController() {
        super(sControllerTag, CMD_9);
    }

    public static EXCDController getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new EXCDController();
        }
        return mInstance;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Override
    public void onConnectionStateChange(int state) {
        super.onConnectionStateChange(state);
    }

    @Override
    public void send(String cmd, byte[] dataBuffer, boolean response, boolean progress, int priority) {
        try {
            super.send(cmd, dataBuffer, response, progress, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(byte[] dataBuffer) {
        super.onReceive(dataBuffer);
        String command = new String(dataBuffer);
        String[] commands = command.split(" ");
        for (Controller c : (HashSet<Controller>) WearableManager.getInstance()
              .getControllers()) {
            if (c.getCmdType() == 9) {
                HashSet<String> receivers = c.getReceiverTags();
                if (receivers != null && receivers.size() > 0 && receivers.contains(commands[1])) {
                    return;
                }
            }
        }
        Log.i(TAG, "onReceive(), command :" + command);
//        HashSet<String> receivers = getReceiverTags();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(commands[1]);
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // Fill extra data, it is optional
        if (dataBuffer != null) {
            broadcastIntent.putExtra(EXTRA_DATA, dataBuffer);
        }
        mContext.sendBroadcast(broadcastIntent);

    }

}
