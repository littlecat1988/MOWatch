
package com.mtk.main;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mtk.bluetoothle.AlertSettingPreference;
import com.mtk.bluetoothle.AlertSettingReadWriter;
import com.mtk.bluetoothle.LocalPxpFmpController;

public class PxpAlertSwitchPreference extends SwitchPreference {

    private static final String TAG = "PxpAlertSwitchPreference";

    private Context mContext;

    private OnPreferenceChangeListener mOnPrefChangeListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean newStatus = (Boolean) newValue;
            Log.d(TAG, "onPreferenceChange: " + newStatus);
            AlertSettingReadWriter.setSwtichPreferenceEnabled(mContext,
                    AlertSettingPreference.ALERT_ENABLER_PREFERENCE, newStatus);
            LocalPxpFmpController.updatePxpParams(mContext);
            setChecked(newStatus);
            return true;
        }
    };

    public PxpAlertSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        updatePreference();
        setOnPreferenceChangeListener(mOnPrefChangeListener);
        return super.onCreateView(parent);
    }

    @Override
    protected void onClick() {
        Log.d(TAG, "onClick");
        Intent intent = new Intent(mContext.getApplicationContext(), AlertSettingPreference.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    public void updatePreference() {
        boolean pxpEnalbed = AlertSettingReadWriter.getSwtichPreferenceEnabled(mContext,
                AlertSettingPreference.ALERT_ENABLER_PREFERENCE,
                AlertSettingPreference.DEFAULT_ALERT_ENABLE);
        setChecked(pxpEnalbed);
    }
}
