
package com.mtk.app.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.mediatek.ctrl.notification.NotificationController;

/**
 * This class will receive and process all new SMS.
 */
public class SmsService extends BroadcastReceiver {
    // Debugging
    private static final String TAG = "AppManager/SmsService";

    private static final String SMS_RECEIVED = "com.mtk.btnotification.SMS_RECEIVED";

    // public static final String SMS_ACTION = "SenderSMSFromeFP";
    private static String preID = null;

    // Received parameters
    private Context mContext = null;

    public SmsService() {
        Log.i(TAG, "SmsReceiver(), SmsReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()");

        mContext = context;
        if (intent.getAction().equals(SMS_RECEIVED)) {
            sendSms();
        }
    }

    void sendSms() {
        String msgbody;
        String address;
        String id;

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Uri.parse("content://sms/inbox"), null,
                    null, null, "_id desc");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msgbody = cursor.getString(cursor.getColumnIndex("body"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    id = cursor.getString(cursor.getColumnIndex("_id"));
                    if (id.equals(preID)) {
                        break;
                    } else {
                        preID = id;
                        if ((msgbody != null) && (address != null)) {
                            Log.i(TAG, "SmsReceiver(),sendSmsMessage, msgbody = " + msgbody
                                    + ", address = " + address);
                            NotificationController.getInstance(mContext).sendSmsMessage(msgbody,
                                    address);
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

}
