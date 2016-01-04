package com.mtk.app.fota;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gmobi.fota.GmFotaService;
import com.gmobi.handler.GmBasicHandler;
import com.mediatek.ctrl.fota.common.FotaOperator;
import com.mediatek.ctrl.fota.common.FotaVersion;
import com.mediatek.ctrl.fota.common.IFotaOperatorCallback;
import com.mediatek.wearable.WearableManager;
import com.mtk.btnotification.R;

public class SmartDeviceFirmware extends PreferenceActivity {

    private static final String TAG = "[FOTA_UPDATE][SmartDeviceFirmware]";

    private static final String VERSION_PERFERENCE_KEY = "current_version_preference";
    private static final String RELEASE_DATE_PERFERENCE_KEY = "release_date_preference";
    private static final String USB_PREFERENCE_KEY = "usb_preference";
    private static final String BUTTON_PREFERENCE_KEY = "button_preference";

    private static boolean mHasInited = false;
    
    private static final int DOWNLOAD_CHANNEL = 1;
    private static final int CHECK_CHANNEL = 0;

    private File mFile = null;

    private String mVersionString = null;
    private String mModuleString = null;
    private String mReleaseNoteString = null;
    private String mPlatformString = null;
    private String mDeviceId = null;
    private boolean mIsFeaturePhoneLowPower = false;

    private String mBrandString = null;
    private String mDomainString = null;
    private String mPinCodeString = null;

    private String mDownloadKeyString = null;
    
    private Preference mVersionPreference;
    private Preference mReleaseDatePreference;
    private PreferenceScreen mTopPreferenceScreen;
    private Preference mUsbPreference;
    private CheckNewVersionButtonPreference mButtonPreference;

    private int mFirmwareMethod = -1;

    private AlertDialog mVersionUpdateDialog;
    private AlertDialog mConnectInformDialog;
    private AlertDialog mDownloadInformDialog;
    private AlertDialog mDownloadConfirmDialog;

    private boolean mIsDestroyed = false;

    // private Context mContext;

    private String mNewVersionString;
    private String mNewReleaseNoteString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "[onCreate] eneter");

        mFirmwareMethod = this.getIntent().getIntExtra(
                FotaUtils.INTENT_EXTRA_INFO, -1);
        Log.e(TAG,"mFirmwareMethod1= "+mFirmwareMethod);
        if (mFirmwareMethod == -1) {
            Log.d(TAG, "[onCreate] mFirmwareMetod is -1");
            this.finish();
            return;
        }
        if (mFirmwareMethod != FotaUtils.FIRMWARE_REDBEND_FOTA
                && mFirmwareMethod != FotaUtils.FIRMWARE_UBIN
                && mFirmwareMethod != FotaUtils.FIRMWARE_VIA_USB
                && mFirmwareMethod != FotaUtils.FIRMWARE_ROCK_FOTA) {
            Log.d(TAG,
                    "[onCreate] mFirmwareMetod is not 0 && 1, cann't do firmware");
            this.finish();
            return;
        }

        Log.d(TAG, "[onCreate] mHasInited : " + mHasInited);
        if (!mHasInited && mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
            Log.d(TAG, "[onCreate] call GmFotaService.updateFinished method");
            String model = FotaUtils.getPreferenceString(
                    getApplicationContext(), FotaUtils.FOTA_BT_MODEL_STRING);
            String devId = FotaUtils.getPreferenceString(
                    getApplicationContext(), FotaUtils.FOTA_BT_DEV_ID_STRING);
            String fwVer = FotaUtils.getPreferenceString(
                    getApplicationContext(), FotaUtils.FOTA_BT_VERSION_STRING);
            if (model != null && devId != null && fwVer != null) {
                GmFotaService.initGmFota(getApplicationContext(), devId, "MTK",
                        model, fwVer);
                // GmFotaService.updateFinished(false);
                FotaUtils.callUpdateFinished(getApplicationContext(), mHandler,
                        false);
            } else {
                Log.d(TAG, "[onCreate] call update finished with default value");
                GmFotaService.initGmFota(this.getApplicationContext(),
                        FotaUtils.REDBEND_FOTA_DEFAULT_VALUES, "MTK",
                        FotaUtils.REDBEND_FOTA_DEFAULT_VALUES,
                        FotaUtils.REDBEND_FOTA_DEFAULT_VALUES);
                FotaUtils.callUpdateFinished(getApplicationContext(), mHandler,
                        false);
            }
            // FotaUtils.clearPreference(getApplicationContext());
        }

        this.addPreferencesFromResource(R.xml.smart_device_firmware_preference);

        mVersionPreference = this.findPreference(VERSION_PERFERENCE_KEY);
        mReleaseDatePreference = this
                .findPreference(RELEASE_DATE_PERFERENCE_KEY);
        mUsbPreference = this.findPreference(USB_PREFERENCE_KEY);
        mButtonPreference = (CheckNewVersionButtonPreference) this
                .findPreference(BUTTON_PREFERENCE_KEY);
        mTopPreferenceScreen = this.getPreferenceScreen();

        if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB) {
        } else if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
            mTopPreferenceScreen.removePreference(mUsbPreference);
        }

        mFile = UpdateFirmwareActivity.initFilePath(getApplication(),
                mFirmwareMethod);

        IntentFilter filter = new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mBTReceiver, filter);

        boolean b = FotaOperator.getInstance(this).registerFotaCallback(mFotaCallback);
        Log.d(TAG, "[onCreate] register callback : " + b);

        mHasInited = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "[onResume] enter");

        mIsFeaturePhoneLowPower = false;
        sendGetVersionCmd();
        mButtonPreference.setButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFeaturePhoneLowPower) {
                    showToast(R.string.feature_phone_low_battery);
                    return;
                }
                if (!hasClicked) {
                    hasClicked = true;
                    showProgressDialog(CHECK_CHANNEL);
                    hasClicked = true;
                    Log.e(TAG,"mFirmwareMethod= "+mFirmwareMethod);
                    if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                        NewVersionChecker.startCheckNewVersion(
                                SmartDeviceFirmware.this,
                                NewVersionChecker.NEW_VERSION_CHECKER_REDBEND,
                                mPlatformString, mModuleString, mVersionString,
                                mBrandString, mDomainString, mPinCodeString,
                                mDeviceId, mDownloadKeyString,
                                mCheckNewVersionCallback);
                    } else if (mFirmwareMethod == FotaUtils.FIRMWARE_UBIN) {
                        NewVersionChecker
                                .startCheckNewVersion(
                                        SmartDeviceFirmware.this,
                                        NewVersionChecker.NEW_VERSION_CHECKER_MTK_SERVER_UBIN,
                                        mPlatformString, mModuleString,
                                        mVersionString, mBrandString, mDomainString,
                                        mPinCodeString, mDeviceId, mDownloadKeyString,
                                        mCheckNewVersionCallback);
                    } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB) {
                        NewVersionChecker
                                .startCheckNewVersion(
                                        SmartDeviceFirmware.this,
                                        NewVersionChecker.NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN,
                                        mPlatformString, mModuleString,
                                        mVersionString, mBrandString, mDomainString,
                                        mPinCodeString, mDeviceId, mDownloadKeyString,
                                        mCheckNewVersionCallback);
                    }
                }
            }
        });
    }

    ProgressDialog pgDialog = null;
    boolean hasClicked = false;

    /**
     * which used to show check new version dialog.
     */
    void showProgressDialog(int which) {
        if (which != CHECK_CHANNEL && which != DOWNLOAD_CHANNEL) {
            Log.d(TAG, "[showProgressDialog] unknown id");
            return;
        }
        pgDialog = new ProgressDialog(SmartDeviceFirmware.this);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (which == CHECK_CHANNEL) {
            pgDialog.setTitle(R.string.checking_new_version_text);
        } else if (which == DOWNLOAD_CHANNEL) {
            pgDialog.setTitle(R.string.downloadingt_text);
        }
        String str = this.getString(R.string.wating_text);
        pgDialog.setMessage(str);
        pgDialog.setCancelable(false);
        if (!mIsDestroyed) {
            pgDialog.show();
        }
    }

    /**
     * while the check new version success, and a new version existed, show the
     * update dialog to make a confirm update or not
     */
    private void showVersionUpdateDialog(final String version,
            final String releaseNote) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Builder builder = new Builder(SmartDeviceFirmware.this);
                builder.setTitle(R.string.firmware_to_the_newest_version);
                View v = LayoutInflater
                        .from(SmartDeviceFirmware.this.getApplicationContext())
                        .inflate(
                                R.layout.firmware_download_confirm_dialog_layout,
                                null);
                TextView versionView = (TextView) v
                        .findViewById(R.id.new_version_content_text);
                TextView releaseNoteView = (TextView) v
                        .findViewById(R.id.release_note_content_text);
                versionView.setText(version);
                releaseNoteView.setText(releaseNote);
                boolean b = checkURL(releaseNote);
                if (b) {
                    releaseNoteView.setTextColor(Color.BLUE);
                    releaseNoteView.getPaint().setFlags(
                            Paint.UNDERLINE_TEXT_FLAG);
                    releaseNoteView
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG,
                                            "[showVersionUpdateDialog] url clicked, to show in browser");
                                    Intent intent = new Intent(
                                            Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(releaseNote));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    SmartDeviceFirmware.this
                                            .startActivity(intent);
                                }
                            });
                }
                builder.setView(v);
                builder.setPositiveButton(R.string.update_text,
                        mFirmwareUpdateDialogClickListener);
                builder.setNegativeButton(R.string.cancel,
                        mFirmwareUpdateDialogClickListener);
                builder.setCancelable(false);
                mVersionUpdateDialog = builder.create();
                if (!mIsDestroyed) {
                    mVersionUpdateDialog.show();
                }
            }

        });

    }

    private void showDownloadConfirmDialog(final String version,
            final String releaseNote) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SmartDeviceFirmware.this);
                builder.setTitle(R.string.new_version_exist);
                View v = LayoutInflater
                        .from(SmartDeviceFirmware.this.getApplicationContext())
                        .inflate(
                                R.layout.version_download_confirm_dialog_layout,
                                null);
                TextView versionView = (TextView) v
                        .findViewById(R.id.which_version_text_view);
                TextView releaseNoteView = (TextView) v
                        .findViewById(R.id.which_release_date_text_view);
                versionView.setText(version);
                releaseNoteView.setText(releaseNote);
                boolean b = checkURL(releaseNote);
                if (b) {
                    releaseNoteView.setTextColor(Color.BLUE);
                    releaseNoteView.getPaint().setFlags(
                            Paint.UNDERLINE_TEXT_FLAG);
                    releaseNoteView
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG,
                                            "[showDownloadConfirmDialog] url clicked, to show in browser");
                                    Intent intent = new Intent(
                                            Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(releaseNote));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    SmartDeviceFirmware.this
                                            .startActivity(intent);
                                }
                            });
                }
                builder.setView(v);
                builder.setPositiveButton(R.string.download_text,
                        mDownloadPositiveListener);
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Log.d(TAG,
                                        "[showDownloadConfirmDialog] NEVIGATE button clicked, call not cancel!");
                                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                                    GmFotaService.cancelUpdate();
                                }
                                arg0.dismiss();
                            }
                        });
                builder.setCancelable(false);
                mDownloadConfirmDialog = builder.create();
                if (!mIsDestroyed) {
                    mDownloadConfirmDialog.show();
                }
            }

        });
    }

    /**
     * check the str is a URL or not
     * 
     * @param str
     *            which used to generate URL
     * @return if the str is a URL, return true, else return false
     */
    private boolean checkURL(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        try {
            new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * start UpdateFirmwareActivity activity to do update firmware action
     */
    private void startFirmwareActivity() {
        Intent itn = new Intent(this, UpdateFirmwareActivity.class);
        itn.putExtra(FotaUtils.INTENT_EXTRA_INFO, mFirmwareMethod);
        if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
            itn.putExtra(FotaUtils.INTENT_EXTRA_MODEL, mModuleString);
            itn.putExtra(FotaUtils.INTENT_EXTRA_VERSION, mVersionString);
            itn.putExtra(FotaUtils.INTENT_EXTRA_DEV_ID, mDeviceId);
        }
        this.startActivity(itn);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void showToast(final int id) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(SmartDeviceFirmware.this, id, Toast.LENGTH_LONG)
                        .show();
            }

        });
    }

    /**
     * update version text & release date text
     * 
     * @param version
     * @param releaseDate
     */
    private void updateVersionAndReleaseDate() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mVersionString != null) {
                    mVersionPreference.setSummary(mVersionString);
                }
                if (mReleaseNoteString != null) {
                    mReleaseDatePreference.setSummary(mReleaseNoteString);
                }
            }

        });
    }

    /**
     * show the infor dialog which is bt
     */
    private void showConnectInformDialog() {
        Log.d(TAG, "[showInformDialog] enter");
        Builder builder = new Builder(this);
        if (mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                || mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
            builder.setMessage(R.string.connect_to_bt);
        } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB) {
            builder.setMessage(R.string.update_via_usb_first_text);
        }
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                                || mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                                || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
                            Log.d(TAG, "[showInformDialog] FIRMWARE_VIA_BT");
                            dialog.dismiss();
                            Intent intent = new Intent(
                                    Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(intent);
                        }
                    }
        });
        mConnectInformDialog = builder.create();
        mConnectInformDialog.show();
    }

    private NewVersionChecker.INewVersionCheckerCallback mCheckNewVersionCallback = new NewVersionChecker.INewVersionCheckerCallback() {

        @Override
        public void onSystemError() {
            Log.d(TAG, "[mCheckNewVersionCallback] [onSystemError] enter");
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.check_fail_message);
        }

        @Override
        public void onSuccessed(String newVersion, String newReleaseNote) {
            Log.d(TAG,
                    "[mCheckNewVersionCallback] [onSuccessed] enter nerVersion : "
                            + newVersion + ", newReleaseNote : "
                            + newReleaseNote);
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            mNewVersionString = newVersion;
            mNewReleaseNoteString = newReleaseNote;
            showDownloadConfirmDialog(newVersion, newReleaseNote);
        }

        @Override
        public void onProgress() {
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.background_is_busy_text);
        }

        @Override
        public void onNewVersionExisted() {
            Log.d(TAG, "[mCheckNewVersionCallback] [onNewVersionExisted] enter");
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.no_update);
        }

        @Override
        public void onNetworkError() {
            Log.d(TAG, "[mCheckNewVersionCallback] [onNetworkError] enter");
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.check_network_message);
        }
    };

    private NewVersionChecker.INewVersionCheckerCallback mDownloadVersionCallback = new NewVersionChecker.INewVersionCheckerCallback() {

        @Override
        public void onSystemError() {
            Log.d(TAG, "[onSystemError] enter");
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.check_fail_message);
        }

        @Override
        public void onSuccessed(String newVersion, String newReleaseNote) {
            Log.d(TAG, "[mDownloadVersionCallback] [onSuccessed] enter");
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showVersionUpdateDialog(mNewVersionString, mNewReleaseNoteString);
        }

        @Override
        public void onProgress() {
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.background_is_busy_text);
        }

        @Override
        public void onNewVersionExisted() {
            Log.d(TAG, "[mDownloadVersionCallback] [onNewVersionExisted] enter");
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
        }

        @Override
        public void onNetworkError() {
            Log.d(TAG, "[mDownloadVersionCallback] [onNetworkError] enter");
            hasClicked = false;
            if (null != pgDialog && pgDialog.isShowing()) {
                pgDialog.dismiss();
            }
            showToast(R.string.check_network_message);
        }
    };

    private DialogInterface.OnClickListener mDownloadPositiveListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            showProgressDialog(DOWNLOAD_CHANNEL);
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Log.d(TAG,
                        "[mDownloadPositiveListener] POSITIVE button clicked to download");
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                    NewVersionChecker.downloadNewVersion(
                            SmartDeviceFirmware.this,
                            NewVersionChecker.NEW_VERSION_CHECKER_REDBEND,// NEW_VERSION_CHECKER_GMOBI,
                            mFile, mDownloadVersionCallback);
                } else if (mFirmwareMethod == FotaUtils.FIRMWARE_UBIN) {
                    NewVersionChecker
                            .downloadNewVersion(
                                    SmartDeviceFirmware.this,
                                    NewVersionChecker.NEW_VERSION_CHECKER_MTK_SERVER_UBIN,
                                    mFile, mDownloadVersionCallback);
                } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB) {
                    NewVersionChecker
                            .downloadNewVersion(
                                    SmartDeviceFirmware.this,
                                    NewVersionChecker.NEW_VERSION_CHECKER_MTK_SERVER_FULL_BIN,
                                    mFile, mDownloadVersionCallback);
                }
            }
        }
    };

    /**
     * update to new version confirm dialog button click listener
     */
    private DialogInterface.OnClickListener mFirmwareUpdateDialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Log.d(TAG,
                        "[mFirmwareUpdateDialogClickListener] POSITIVE button clicked");

                /* check the smart device is connected to SP.Like BT or USB */
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                        || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                        || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
                    // / if there is no device connected via bt, show the
                    // information dialog
                    if (!WearableManager.getInstance().isAvailable()) {
                        if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                            Log.e(TAG,
                                    "[mFirmwareUpdateDialogClickListener] BT disconnected, call update finished");
                            FotaUtils.callUpdateFinished(
                                    getApplicationContext(), mHandler, false);
                        }
                        showConnectInformDialog();
                    } else {
                        // if a device is connected, just start the update
                        // activity
                        startFirmwareActivity();
                    }
                } else {
                    startFirmwareActivity();
                }
                dialog.dismiss();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                Log.d(TAG,
                        "[mFirmwareUpdateDialogClickListener] NEGATIVE button clicked");
                dialog.dismiss();
                // GmFotaService.updateFinished(false);
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                    FotaUtils.callUpdateFinished(getApplicationContext(),
                            mHandler, false);
                }
            } else {
                Log.d(TAG,
                        "[mFirmwareUpdateDialogClickListener] unrecognize button id");
            }
        }
    };

    private void sendGetVersionCmd() {
        FotaOperator.getInstance(this).sendFotaVersionGetCommand(mFirmwareMethod);
    }
    
    private IFotaOperatorCallback mFotaCallback = new IFotaOperatorCallback() {

        @Override
        public void onFotaTypeReceived(int fotaType) {
            
        }

        @Override
        public void onCustomerInfoReceived(String information) {
            
        }

        @Override
        public void onFotaVersionReceived(FotaVersion version) {
            Log.d(TAG, "[onFotaVersionReceived] enter");
            if (version == null) {
                showToast(R.string.get_smart_device_version_fail);
                return;
            }
            mVersionString = version.mVersionString;
            mReleaseNoteString = version.mReleaseDateString;
            mModuleString = version.mModuleString;
            mPlatformString = version.mPlatformString;
            mBrandString = version.mBrandString;
            mDomainString = version.mDomainString;
            mDownloadKeyString = version.mDownloadKeyString;
            mDeviceId = version.mDeviceIdString;
            mIsFeaturePhoneLowPower = version.mIsFeaturePhoneLowPower;
            mPinCodeString = version.mPinCodeString;
            
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mVersionString : " + mVersionString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mReleaseNote : " + mReleaseNoteString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mModule : " + mModuleString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mPlatformString : " + mPlatformString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mDeviceId : " + mDeviceId);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mBrandString : " + mBrandString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mDomainString : " + mDomainString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mPinCodeString : " + mPinCodeString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mDownloadKeyString : " + mDownloadKeyString);
            Log.d(TAG, "[mFotaCallback][onFotaVersionReceived] mIsFeaturePhoneLowPower : " + mIsFeaturePhoneLowPower);
            
            updateVersionAndReleaseDate();
        }

        @Override
        public void onStatusReceived(int status) {
            
        }

        @Override
        public void onConnectionStateChange(int newConnectionState) {
            
        }

        @Override
        public void onProgress(int progress) {
            
        }
        
    };
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FotaUtils.UPDATE_REDBEND_FINISHED) {
                boolean b = (Boolean) msg.obj;
                if (GmBasicHandler.getFotaService().ctx != null) {
                    Log.d(TAG,
                            "[Handler.handleMessage] handle message update to success : "
                                    + b);
                    GmFotaService.updateFinished(b);
                }
            }
        }

    };

    private BroadcastReceiver mBTReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                int state = arg1.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_OFF) {
                    if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                        Log.d(TAG,
                                "[mBTReceiver] BluetoothAdapter is STATE_OFF, change finished to be false");
                        if (GmBasicHandler.getFotaService().ctx != null) {
                            // GmFotaService.updateFinished(false);
                            FotaUtils.callUpdateFinished(
                                    getApplicationContext(), mHandler, false);
                        }
                    }
                }
            }
        }

    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "[onDestroy] eneter");
        mIsDestroyed = true;
        if (pgDialog != null && pgDialog.isShowing()) {
            pgDialog.dismiss();
        }
        if (mVersionUpdateDialog != null && mVersionUpdateDialog.isShowing()) {
            mVersionUpdateDialog.dismiss();
        }
        if (mConnectInformDialog != null && mConnectInformDialog.isShowing()) {
            mConnectInformDialog.dismiss();
        }
        if (mDownloadInformDialog != null && mDownloadInformDialog.isShowing()) {
            mDownloadInformDialog.dismiss();
        }
        if (mDownloadConfirmDialog != null
                && mDownloadConfirmDialog.isShowing()) {
            mDownloadConfirmDialog.dismiss();
        }
        unregisterReceiver(mBTReceiver);
        FotaOperator.getInstance(this).unregisterFotaCallback(mFotaCallback);
        super.onDestroy();
    }
}
