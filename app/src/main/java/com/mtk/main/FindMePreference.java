package com.mtk.main;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.PxpFmStatusChangeListener;
import com.mediatek.leprofiles.PxpFmStatusRegister;
import com.mtk.bluetoothle.LocalPxpFmpController;
import com.mtk.btnotification.R;

public class FindMePreference extends Preference {

    private static final String TAG = "[refactorPxp]FindMePreference";

    private static final int MESSAGE_UPDATE = 1;

    private Context mContext;

    private Button mButton;

    public FindMePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setWidgetLayoutResource(R.layout.find_me_preference_layout);
    }

    private Handler mFindMeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "mFindMeHandler handleMessage, msg.what = " + msg.what);
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    updatePreference();
                    break;
                default:
                    break;
            }
        }
    };

    private PxpFmStatusChangeListener mStatusListener = new PxpFmStatusChangeListener() {

        @Override
        public void onStatusChange() {
            mFindMeHandler.removeMessages(MESSAGE_UPDATE);
            mFindMeHandler.obtainMessage(MESSAGE_UPDATE).sendToTarget();
        }

    };

    @Override
    protected View onCreateView(ViewGroup parent) {
        Log.d(TAG, "onCreateView");
        PxpFmStatusRegister.getInstance().registerFmListener(mStatusListener);

        return LayoutInflater.from(getContext()).inflate(R.layout.find_me_preference_layout,
                parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mButton = (Button) view.findViewById(R.id.find_me_button);
        updatePreference();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setClickable(false);
                int status = PxpFmStatusRegister.getInstance().getFindMeStatus();
                if (status == PxpFmStatusRegister.FIND_ME_STATUS_USING) {
                    LocalPxpFmpController.findTargetDevice(BlePxpFmpConstants.FMP_LEVEL_NO);
                } else {
                    LocalPxpFmpController.findTargetDevice(BlePxpFmpConstants.FMP_LEVEL_HIGH);
                }
                mFindMeHandler.removeMessages(MESSAGE_UPDATE);
                mFindMeHandler.obtainMessage(MESSAGE_UPDATE).sendToTarget();
            }
        });
    }

    private void updatePreference() {
        int status = PxpFmStatusRegister.getInstance().getFindMeStatus();
        Log.d(TAG, "updatePreference, status: " + status);
        if (mButton != null) {
            if (status == PxpFmStatusRegister.FIND_ME_STATUS_DISABLED) {
                mButton.setEnabled(false);
                mButton.setText(R.string.find_me_button);
            } else {
                mButton.setEnabled(true);
                mButton.setClickable(true);
                if (status == PxpFmStatusRegister.FIND_ME_STATUS_NORMAL) {
                    mButton.setText(R.string.find_me_button);
                } else if (status == PxpFmStatusRegister.FIND_ME_STATUS_USING) {
                    mButton.setText(R.string.find_me_using);
                }
            }
        }
    }

    public void releaseListeners() {
        Log.d(TAG, "releaseListeners");
        PxpFmStatusRegister.getInstance().unregisterFmListener(mStatusListener);
    }
}
