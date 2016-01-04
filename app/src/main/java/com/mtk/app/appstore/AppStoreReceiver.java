
package com.mtk.app.appstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class receives ACTION_SHUTDOWN action to save Downloading App ID.
 */
public class AppStoreReceiver extends BroadcastReceiver {

    private static final String TAG = "AppManager/AppStoreReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AppStoreReceiver onReceive() intent = " + intent.toString());
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_SHUTDOWN)) {
            AppStoreManager.getInstance().saveDownloadingStatus(context);
        }
    }
}
