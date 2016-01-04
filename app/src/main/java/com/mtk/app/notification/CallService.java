
package com.mtk.app.notification;

import java.util.Timer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mediatek.ctrl.notification.NotificationController;
import com.mtk.btnotification.R;
import com.mtk.main.Utils;

/**
 * This class will receive and process phone information, when phone state
 * changes.
 */
public class CallService extends PhoneStateListener {
    // Debugging
    private static final String TAG = "AppManager/CallService";

    private static final int MSG_NEED_UPDATE_MISSED_CALL = 100;

    private Context mContext = null;

    private int mLastState = TelephonyManager.CALL_STATE_IDLE; // the last phone
                                                               // state

    private String mIncomingNumber = null;

    private Timer mTimer = null;

    private MissedCallContentOberserver mMCOberserver = null;

    private ContentResolver mContentResolver = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEED_UPDATE_MISSED_CALL: {
                    // sendCallMessage();
                    String phoneNum = mIncomingNumber;
                    String sender = Utils.getContactName(mContext, phoneNum);
                    String content = getMessageContent(sender);
                    int missedCallCount = getMissedCallCount();
                    NotificationController.getInstance(mContext).sendCallMessage(mIncomingNumber,
                            sender, content, missedCallCount);
                    break;
                }
            }
        }
    };

    public CallService(Context context) {
        Log.i(TAG, "CallService(), CallService created!");
        mContext = context;
        mContentResolver = context.getContentResolver();

        mMCOberserver = new MissedCallContentOberserver(mHandler);
        mContentResolver.registerContentObserver(Calls.CONTENT_URI, false, mMCOberserver);
    }

    public void stopCallService() {
        Log.i(TAG, "StopCallService(), CallService stoped!");

        mContentResolver.unregisterContentObserver(mMCOberserver);
        mMCOberserver = null;
        mContentResolver = null;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        Log.i(TAG, "onCallStateChanged(), incomingNumber" + incomingNumber);
        if ((state == TelephonyManager.CALL_STATE_RINGING) && (incomingNumber != null)) {
            mIncomingNumber = incomingNumber;
        }
        mLastState = state;
    }

    private String getMessageContent(String sender) {
        StringBuilder content = new StringBuilder();
        content.append(mContext.getText(R.string.missed_call));
        content.append(": ");
        content.append(sender);

        // TODO: Only for test
        content.append("\r\n");
        content.append("Missed Call Count:");
        content.append(getMissedCallCount());

        Log.i(TAG, "getMessageContent(), content=" + content);
        return content.toString();
    }

    private int getMissedCallCount() {
        // setup query spec, look for all Missed calls that are new.
        StringBuilder queryStr = new StringBuilder("type = ");
        queryStr.append(Calls.MISSED_TYPE);
        queryStr.append(" AND new = 1");
        Log.i(TAG, "getMissedCallCount(), query string=" + queryStr);

        // start the query
        int missedCallCount = 0;
        Cursor cur = null;
        cur = mContext.getContentResolver().query(Calls.CONTENT_URI, new String[] {
            Calls._ID
        }, queryStr.toString(), null, Calls.DEFAULT_SORT_ORDER);
        if (cur != null) {
            missedCallCount = cur.getCount();
            cur.close();
        }

        Log.i(TAG, "getMissedCallCount(), missed call count=" + missedCallCount);
        return missedCallCount;
    }

    private class MissedCallContentOberserver extends ContentObserver {

        private int mPreviousMissedCallCount;

        private Handler mHandler;

        public MissedCallContentOberserver(Handler handler) {
            super(handler);
            mPreviousMissedCallCount = 0;
            mHandler = handler;
        }

        public void onChange(boolean onSelf) {
            super.onChange(onSelf);
            Log.i(TAG, "DataBase State Changed");
            int missedCallCount = getMissedCallCount();
            if (missedCallCount == 0) {
                NotificationController.getInstance(mContext).sendReadMissedCallData();
            } else if (mPreviousMissedCallCount < missedCallCount) {
                Message msg = new Message();
                msg.what = MSG_NEED_UPDATE_MISSED_CALL;
                mHandler.sendMessage(msg);
            }

            mPreviousMissedCallCount = missedCallCount;
        }
    }

}
