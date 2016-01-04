
package com.mtk.bluetoothle;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.fmppxp.CalibrateListener;
import com.mtk.btnotification.R;

public class AlertSettingPreference extends PreferenceActivity {

    private static final String TAG = "[AlertSettingPreference]";

    public static final String ALERT_ENABLER_PREFERENCE = "alert_set_preference";
    public static final String RANGE_ALERT_CHECK_PREFERENCE = "range_alert_check_preference";
    public static final String RANGE_TYPE_PREFERENCE = "range_type_preference";
    public static final String RANGE_SIZE_PREFERENCE = "range_size_preference";
    public static final String RANGE_CALIBRATE_PREFERENCE = "calibrate_preference";
    public static final String DISCONNECT_WARNING_PREFERENCE = "disconnect_warning_preference";
    public static final String RINGTONE_PREFERENCE = "ringtone_preference";
    public static final String VIBRATION_PREFERENCE = "vibration_preference";
    public static final String ALERT_ENABLER_PREFERENCE_FIRST = "ALERT_ENABLER_PREFERENCE_FIRST";//add by lixiang for tips 20150610
    public static final String RANGE_CALIBRATED_THRESHOLD_PREFERENCE = "range_calibrated_threshold_preference";
    public static final String RANGE_CALIBRATED_TOLERANCE_PREFERENCE = "range_calibrated_tolerance_preference";

    private static final int MESSAGE_DISMISS_PROGRESS_DIALOG = 0;
    private static final int MESSAGE_CALIBRATE_FINISHED = 1;
    
    private static final int CALIBRATION_SUCCESSFUL = 1;
    private static final int CALIBRATION_FAILED = 0;

    public static final int RANGE_SIZE_NEAR = 0;
    public static final int RANGE_SIZE_FAR = 1;

    public static final int CALIBRATE_TIME = 10000;
    public static final boolean DEFAULT_ALERT_ENABLE = true;
    public static final boolean DEFAULT_ALERT_ENABLE_FIRST = false;//add by lixiang for tips 20150610
    public static final boolean DEFAULT_RANGE_ALERT_ENABLE = false;
    public static final int DEFAULT_RANGE_TYPE = BlePxpFmpConstants.RANGE_ALERT_OUT;
    public static final int DEFAULT_RANGE_SIZE = RANGE_SIZE_FAR;
    public static final boolean DEFAULT_DISCONNECT_WARNING_ENABLE = true;
    public static final boolean DEFAULT_RINGTONE_ENABLE = true;
    public static final boolean DEFAULT_VIBRATION_ENABLE = true;

    private SwitchPreference mAlertPreference;
    private SwitchPreference mRangeAlertPreference;
    private ListPreference mRangeTypePreference;
    private ListPreference mRangeSizePreference;
    private Preference mRangeCalibratePreference;
    private SwitchPreference mDisWarningPreference;
    private SwitchPreference mRingtonePreference;
    private SwitchPreference mVibrationPreference;

    private ProgressDialog mProgressDialog;
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DISMISS_PROGRESS_DIALOG:
                    try {
                        /// M: MAUI_03534143 !AlertSettingPreference.this.isFinishing()
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "mProgressDialog.dismiss: " + e);
                    }
                    break;
                case MESSAGE_CALIBRATE_FINISHED:
                    if (msg.arg1 == CALIBRATION_SUCCESSFUL) {
                        Toast.makeText(AlertSettingPreference.this, R.string.calibrate_success,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AlertSettingPreference.this, R.string.calibrate_failed,
                                Toast.LENGTH_LONG).show();
                    }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.alert_setting_preference);

        mAlertPreference = (SwitchPreference) findPreference(ALERT_ENABLER_PREFERENCE);
        mRangeAlertPreference = (SwitchPreference) findPreference(RANGE_ALERT_CHECK_PREFERENCE);
        mRangeTypePreference = (ListPreference) findPreference(RANGE_TYPE_PREFERENCE);
        mRangeSizePreference = (ListPreference) findPreference(RANGE_SIZE_PREFERENCE);
        mRangeCalibratePreference = (Preference) findPreference(RANGE_CALIBRATE_PREFERENCE);
        mDisWarningPreference = (SwitchPreference) findPreference(DISCONNECT_WARNING_PREFERENCE);
        mRingtonePreference = (SwitchPreference) findPreference(RINGTONE_PREFERENCE);
        mVibrationPreference = (SwitchPreference) findPreference(VIBRATION_PREFERENCE);
        initPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initActivityState();
        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);
        return true;
    }

    private void initActionBar() {
        ActionBar bar = this.getActionBar();
        bar.setTitle(R.string.alert_preference_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * do preference initialization about click listeners.
     */
    private void initPreferences() {
        mAlertPreference.setOnPreferenceChangeListener(mOnChangeListener);
        mRangeAlertPreference.setOnPreferenceChangeListener(mOnChangeListener);
        mRangeTypePreference.setOnPreferenceChangeListener(mOnChangeListener);
        mRangeSizePreference.setOnPreferenceChangeListener(mOnChangeListener);
        mDisWarningPreference.setOnPreferenceChangeListener(mOnChangeListener);
        mRingtonePreference.setOnPreferenceChangeListener(mOnChangeListener);
        mVibrationPreference.setOnPreferenceChangeListener(mOnChangeListener);

        mRangeCalibratePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                calibratePxpThreshold();
                return false;
            }
        });
    }

    private void initActivityState() {
        updatePreferences();
    }
    
    private void updatePreferences() {
        updateAlertEnablerRelatedPreference();
        updatePreferenceSummary();
    }

    private void updateAlertEnablerRelatedPreference() {
        Context context = getApplicationContext();
        
        boolean alertEnabled = AlertSettingReadWriter.getSwtichPreferenceEnabled(context,
                ALERT_ENABLER_PREFERENCE,
                DEFAULT_ALERT_ENABLE);
        boolean alertRangeEnabled = AlertSettingReadWriter.getSwtichPreferenceEnabled(context,
                RANGE_ALERT_CHECK_PREFERENCE, DEFAULT_RANGE_ALERT_ENABLE);
//        mAlertPreference.setEnabled(alertEnabled);
        mRangeAlertPreference.setEnabled(alertEnabled);
        mRangeTypePreference.setEnabled(alertEnabled && alertRangeEnabled);
        mRangeSizePreference.setEnabled(alertEnabled && alertRangeEnabled);
        mRangeCalibratePreference.setEnabled(alertEnabled && alertRangeEnabled);
        mDisWarningPreference.setEnabled(alertEnabled);
        mRingtonePreference.setEnabled(alertEnabled);
        mVibrationPreference.setEnabled(alertEnabled);
    }

    private void updatePreferenceSummary() {
        Context context = getApplicationContext();
        int rangeType = AlertSettingReadWriter.getRangePreferenceStatus(context,
                RANGE_TYPE_PREFERENCE, DEFAULT_RANGE_TYPE);
        int rangeSize = AlertSettingReadWriter.getRangePreferenceStatus(context,
                RANGE_SIZE_PREFERENCE, DEFAULT_RANGE_SIZE);
        Resources res = context.getResources();
        String[] rangeTypeStrings = res.getStringArray(R.array.range_alert_type_text);
        String[] rangeSizeStrings = res.getStringArray(R.array.range_alert_size_text);
        if (rangeType >= rangeTypeStrings.length) {
            rangeType = rangeTypeStrings.length - 1;
        }
        mRangeTypePreference.setSummary(rangeTypeStrings[rangeType]);
        if (rangeSize >= rangeSizeStrings.length) {
            rangeSize = rangeSizeStrings.length - 1;
        }
        mRangeSizePreference.setSummary(rangeSizeStrings[rangeSize]);
    }

    private void calibratePxpThreshold() {
        Context context = AlertSettingPreference.this;
        int rangeSize = AlertSettingReadWriter.getRangePreferenceStatus(
                context, RANGE_SIZE_PREFERENCE, DEFAULT_RANGE_SIZE);
        String[] rangeSizeStrings = context.getResources().getStringArray(R.array.range_alert_size_text);
        if (rangeSize >= rangeSizeStrings.length) {
            rangeSize = rangeSizeStrings.length - 1;
            AlertSettingReadWriter.setRangePreferenceStatus(context, RANGE_SIZE_PREFERENCE,
                    rangeSize);
        }
        String rangeSizeName = rangeSizeStrings[rangeSize];
        String title = context.getResources().getString(R.string.calibrate_dialog_title,
                rangeSizeName);
        String message = context.getResources().getString(R.string.calibrate_dialog_message);
        mProgressDialog = ProgressDialog.show(AlertSettingPreference.this, title, message, true,
                false);
        Log.d(TAG, "calibratePxpThreshold(): " + rangeSize + ", " + rangeSizeName);
        CalibrateListener listener = new CalibrateListener() {

            @Override
            public void onCalibrateFinished(boolean success, int threshold, int tolerance) {
                Log.d(TAG, "onCalibrateFinished: " + success + ", threshold: " + threshold
                        + ", tolerance: " + tolerance);
                mHander.obtainMessage(MESSAGE_DISMISS_PROGRESS_DIALOG).sendToTarget();
                if (success) {
                    mHander.obtainMessage(MESSAGE_CALIBRATE_FINISHED, CALIBRATION_SUCCESSFUL, 0)
                            .sendToTarget();
                    Context context = getApplicationContext();
                    int rangeSize = AlertSettingReadWriter.getRangePreferenceStatus(
                            context, RANGE_SIZE_PREFERENCE, DEFAULT_RANGE_SIZE);
                    Log.d(TAG, "onCalibrateFinished for: " + rangeSize);
                    String thresholdKey = RANGE_CALIBRATED_THRESHOLD_PREFERENCE + rangeSize;
                    String toleranceKey = RANGE_CALIBRATED_TOLERANCE_PREFERENCE + rangeSize;
                    AlertSettingReadWriter.setRangePreferenceStatus(context, thresholdKey,
                            threshold);
                    AlertSettingReadWriter.setRangePreferenceStatus(context, toleranceKey,
                            tolerance + 2);
                    LocalPxpFmpController.updatePxpParams(context);
                } else {
                    mHander.obtainMessage(MESSAGE_CALIBRATE_FINISHED, CALIBRATION_FAILED, 0)
                            .sendToTarget();
                }
            }
        };
        LocalPxpFmpController.calibrateAlertThreshold(listener, CALIBRATE_TIME);
    }

    OnPreferenceChangeListener mOnChangeListener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if (preference == mAlertPreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mAlertPreference"
                                + ": "
                                + ((Boolean) newValue).toString());
                AlertSettingReadWriter.setSwtichPreferenceEnabled(getApplicationContext(),
                        ALERT_ENABLER_PREFERENCE,
                        (Boolean) newValue);
                updateAlertEnablerRelatedPreference();
                LocalPxpFmpController.updatePxpParams(getApplicationContext());
            } else if (preference == mRangeAlertPreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mRangeAlertPreference"
                                + ": "
                                + ((Boolean) newValue).toString());
                AlertSettingReadWriter.setSwtichPreferenceEnabled(getApplicationContext(),
                        RANGE_ALERT_CHECK_PREFERENCE,
                        (Boolean) newValue);
              //add by lixiang for tips 20150610
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean alert_first = prefs.getBoolean(ALERT_ENABLER_PREFERENCE_FIRST, DEFAULT_ALERT_ENABLE_FIRST);
                if(!alert_first){
                	Editor localEditor = prefs.edit();
                    localEditor.putBoolean(ALERT_ENABLER_PREFERENCE_FIRST, true);
                    localEditor.commit();
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.first_alert),Toast.LENGTH_SHORT).show();
                }
                
                updateAlertEnablerRelatedPreference();
                LocalPxpFmpController.updatePxpParams(getApplicationContext());
            } else if (preference == mRangeTypePreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mRangeTypePreference"
                                + ": "
                                + (String) newValue);
                AlertSettingReadWriter.setRangePreferenceStatus(getApplicationContext(),
                        RANGE_TYPE_PREFERENCE,
                        Integer.parseInt((String) newValue));
                updatePreferenceSummary();
                LocalPxpFmpController.updatePxpParams(getApplicationContext());
            } else if (preference == mRangeSizePreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mRangeSizePreference"
                                + ": "
                                + (String) newValue);
                AlertSettingReadWriter.setRangePreferenceStatus(getApplicationContext(),
                        RANGE_SIZE_PREFERENCE,
                        Integer.parseInt((String) newValue));
                updatePreferenceSummary();
                LocalPxpFmpController.updatePxpParams(getApplicationContext());
                Toast.makeText(AlertSettingPreference.this, R.string.pxp_range_size_warning,
                        Toast.LENGTH_LONG).show();
            } else if (preference == mDisWarningPreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mDisWarningPreference"
                                + ": "
                                + ((Boolean) newValue).toString());
                AlertSettingReadWriter.setSwtichPreferenceEnabled(getApplicationContext(),
                        DISCONNECT_WARNING_PREFERENCE,
                        (Boolean) newValue);
                LocalPxpFmpController.updatePxpParams(getApplicationContext());
            } else if (preference == mRingtonePreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mRingtonePreference"
                                + ": "
                                + ((Boolean) newValue).toString());
                AlertSettingReadWriter.setSwtichPreferenceEnabled(getApplicationContext(),
                        RINGTONE_PREFERENCE,
                        (Boolean) newValue);
            } else if (preference == mVibrationPreference) {
                Log.d(TAG,
                        "onPreferenceChange"
                                + "mVibrationPreference"
                                + ": "
                                + ((Boolean) newValue).toString());
                AlertSettingReadWriter.setSwtichPreferenceEnabled(getApplicationContext(),
                        VIBRATION_PREFERENCE,
                        (Boolean) newValue);
            } else {
                Log.e(TAG, "onPreferenceChange error preference");
                return false;
            }
            return true;
        }
    };
}
