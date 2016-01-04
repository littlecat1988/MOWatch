
package com.mtk.bluetoothle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LeProfileUtils {

    private static final String TAG = "[wearable][Fit]LeProfileUtils";

    private static final String GOOGLE_PLAY_SERVICE = "com.google.android.gms";

    private static boolean sGooglePlayAvailable = false;

    private static boolean sNetworkAvailable = false;

    private static Context sContext = null;

    public static void init(Context context) {
        sContext = context;
        sNetworkAvailable = isNetworkAvailable();
        sGooglePlayAvailable = isGooglePlayAvailable();
        Log.d(TAG, "[init] sNetworkAvailable=" + sNetworkAvailable + " sGooglePlayAvailable="
                + sGooglePlayAvailable);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        sContext.registerReceiver(sNetworkReceiver, intentFilter);

        sHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_GOOGLEPLAY, 60 * 1000);
    }

    public static boolean isFitnessAvailable() {
        return sGooglePlayAvailable && sNetworkAvailable;
    }


    /// M: Check GooglePlay service and Network @{
    public static boolean isNetworkAvailable() {
        if (sContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) sContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isGooglePlayAvailable() {
        try {
            PackageInfo packageInfo = sContext.getPackageManager().getPackageInfo(
                    GOOGLE_PLAY_SERVICE, 0);
            if (packageInfo != null) {
                String version = packageInfo.versionName;
                Log.d(TAG, "[isGooglePlayAvailable] version = " + version);
                String[] versions = version.split("\\.");
                int fristVersion = 0;
                int secondVersion = 0;
                if (versions != null && versions.length > 2) {
                    fristVersion = Integer.valueOf(versions[0]);
                    secondVersion = Integer.valueOf(versions[1]);
                }
                if (fristVersion > 6 || (fristVersion == 6 && secondVersion >= 1)) {
                    int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(sContext);
                    Log.d(TAG, "[isGooglePlayAvailable] available = " + available);
                    if (ConnectionResult.SUCCESS == available) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "[isGooglePlayAvailable] Exception = " + e);
        } catch (Error e) {
            Log.d(TAG, "[isGooglePlayAvailable] Error = " + e);
        }
        return false;
    }

    private static BroadcastReceiver sNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) sContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    sNetworkAvailable = true;
                    FitnessHelper.getInstance().connect();
                } else {
                    sNetworkAvailable = false;
                }
                Log.d(TAG, "mNetworkReceiver = " + sNetworkAvailable);
            }
        }
    };

    private static final int MESSAGE_CHECK_GOOGLEPLAY = 0;

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "[sHandler] handleMessage, msg.what = " + msg.what);
            switch (msg.what) {
                case MESSAGE_CHECK_GOOGLEPLAY:
                    sGooglePlayAvailable = isGooglePlayAvailable();
                    Log.d(TAG, "[sHandler] handleMessage sGooglePlayAvailable = " + sGooglePlayAvailable);
                    sHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_GOOGLEPLAY, 60 * 1000);
                    break;
                default:
                    break;
            }
        }
    };
    /// @}
}
