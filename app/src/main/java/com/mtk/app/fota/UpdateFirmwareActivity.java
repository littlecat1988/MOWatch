package com.mtk.app.fota;

import java.io.File;
import java.io.IOException;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.gmobi.fota.GmFotaService;
import com.gmobi.handler.GmBasicHandler;
import com.mediatek.wearable.WearableManager;
import com.mtk.btnotification.R;
import com.mtk.main.MainActivity;

import com.mediatek.ctrl.fota.common.FotaOperator;
import com.mediatek.ctrl.fota.common.FotaVersion;
import com.mediatek.ctrl.fota.common.IFotaOperatorCallback;
import com.mediatek.ctrl.fota.downloader.Downloader;

public class UpdateFirmwareActivity extends PreferenceActivity {

    private static String mfilePath;
    private static String mfileName;//= "firmware.zip";
    private Uri mSelectFileUri;
    private boolean hasSend = false;
    private static final String SEND_OVER = "sendOver";
    private static final String TAG = "[FOTA_UPDATE][UpdateFirmwareActivity]";
    
    private static final String UPDATING_PREFERENCE_KEY = "top_preference";
    private static final String STATE_PREFERENCE_KEY = "bottom_preference";
    
    private Preference mTopPreference;
    private Preference mBottomPreference;
    
    private AlertDialog mUSBInformDialog;
    
    public static boolean sIsSending = false;
    public static int sCurrentSendingFirmware = -1;
    private boolean mIsDeCompressing = false;
//    private boolean mIsUpdating = false;
    
    private int mFirmwareMethod;
    
    private String mModelString = null;
    private String mVersionString = null;
    private String mDevIdString = null;
    
    private boolean mIsUsbDownloadFinished = false;
    private boolean mIsBtTransferFinished = false;
    private boolean mIsDestroyed = false;
    
    private Context mContext;
    
//    private boolean mUpdateFinishedCalled = false;
    
    private Downloader mDownloader;
    
    private static final int MSG_UPDATE_UPDATING_STATUS = 10;
    private static final int MSG_UPDATE_FINISHED = 20;
    private static final int MSG_UPDATE_TIMEOUT = 5 * 60 * 1000;
    
//    private int mMaxBtTransferCount;
//    private int mBtTransferedCount;
    
    private boolean mTransferViaBTErrorHappened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mFirmwareMethod = this.getIntent().getIntExtra(FotaUtils.INTENT_EXTRA_INFO, -1);
        Log.d(TAG, "[onCreate] mFirmwareMethod : " + mFirmwareMethod);
        if (mFirmwareMethod == -1) {
            Log.d(TAG, "[onCreate] intent extra is -1");
            this.finish();
            return;
        }
        if (mFirmwareMethod != FotaUtils.FIRMWARE_REDBEND_FOTA
                && mFirmwareMethod != FotaUtils.FIRMWARE_UBIN
                && mFirmwareMethod != FotaUtils.FIRMWARE_VIA_USB
                && mFirmwareMethod != FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER
                && mFirmwareMethod != FotaUtils.FIRMWARE_FULL_BIN
                && mFirmwareMethod != FotaUtils.FIRMWARE_ROCK_FOTA) {
            Log.d(TAG, "[onCreate] unrecognized id");
            this.finish();
            return;
        }
        
        sCurrentSendingFirmware = mFirmwareMethod;
        
        if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER
                || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN) {
            mfilePath = this.getIntent().getStringExtra(FotaUtils.ZIP_FILE_PATH);
            if (!this.getIntent().getBooleanExtra("isFromMain", false)) {
                Log.d(TAG, "[onCreate] zip file path is : " + mfilePath);
                if (mfilePath == null) {
                    mSelectFileUri = this.getIntent().getData();
                    Log.d(TAG, "[onCreate] select file uri is : " + mSelectFileUri);
                    if (mSelectFileUri == null) {
                        this.finish();
                        return;
                    }
                }
            }
        }
        
        if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
            this.mModelString = this.getIntent().getStringExtra(FotaUtils.INTENT_EXTRA_MODEL);
            this.mVersionString = this.getIntent().getStringExtra(FotaUtils.INTENT_EXTRA_VERSION);
            this.mDevIdString = this.getIntent().getStringExtra(FotaUtils.INTENT_EXTRA_DEV_ID);
        }
        
        this.addPreferencesFromResource(R.xml.update_firmware_preference);
        
        mTopPreference = this.findPreference(UPDATING_PREFERENCE_KEY);
        mBottomPreference = this.findPreference(STATE_PREFERENCE_KEY);

        updateCurrentProgress(0);
        if (mFirmwareMethod != FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER
                && mFirmwareMethod != FotaUtils.FIRMWARE_FULL_BIN) {
            initFilePath(getApplicationContext(), mFirmwareMethod);
        }
        
        mContext = getApplicationContext();
        
        FotaOperator.getInstance(this).registerFotaCallback(mFotaCallback);
        
        IntentFilter filter1 = new IntentFilter();
//        filter1.addAction(BluetoothManager.BT_BROADCAST_ACTION);
        filter1.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        this.registerReceiver(mReceiver, filter1);
        
        
        if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN
                || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
            if (!WearableManager.getInstance().isAvailable()) {
                mTopPreference.setSummary(R.string.bt_disconnected_before_transfer);
            } else {
                mTopPreference.setSummary(R.string.updating_firmware);
            }
        } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
            //TODO : should change the image view to be USB image
            if (sIsSending) {
                mTopPreference.setSummary(R.string.updating_firmware_via_usb_text);
            } else {
                mTopPreference.setSummary(R.string.unziping_text);
            }
        }
        
        if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
            mDownloader = new Downloader(mContext, mDownloadInterface);
        }
        
        Log.d(TAG, "[onCreate] hasSend : " + hasSend);
        Log.d(TAG, "[onCreate] sIsSending : " + sIsSending);
        if (!hasSend && !sIsSending) {
            mTransferTask.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "[onStart] enter");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsDeCompressing) {
                Log.d(TAG, "[onKeyDown] current is on decompressing, do not exit the ui");
                return true;
            }
            if (sIsSending) {
                Log.d(TAG, "[onKeyDown] updating is runing, please wait");
                String str = this.getApplicationContext().getString(R.string.warning_updating_text);
                showToast(str);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "[handleMessage] msg.what" + msg.what);
            switch (msg.what) {
            case FotaUtils.MSG_UPDATE_TEXT_VIEW:
                updateTextView(msg.arg1);
                break;

//            case FotaUtils.MSG_UPDATE_PROGRESS_VIEW:
//                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
//                        || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
//                        || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN
//                        || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
//                    mMaxBtTransferCount = msg.arg2;
//                } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
//                        || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
//                    updateCurrentProgress(msg.arg1, msg.arg2);
//                }
//                break;

            case MSG_UPDATE_UPDATING_STATUS:
                Log.d(TAG, "[handleMessage] MSG_UPDATE_UPDATING_STATUS");
                // GmFotaService.updateFinished(false);
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                    FotaUtils.callUpdateFinished(mContext, mHandler, false);
                    FotaUtils.updateUpdatingStatus(mContext, false,
                            mModelString, mVersionString, mDevIdString);
                }
//                mIsUpdating = false;
                break;

            case MSG_UPDATE_FINISHED:
                Log.d(TAG, "[handleMessage] MSG_UPDATE_FINISHED");
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                    if (GmBasicHandler.getFotaService().ctx != null) {
                        // GmFotaService.updateFinished(false);
                        boolean b = (Boolean) msg.obj;
                        FotaUtils.callUpdateFinished(mContext, mHandler, b);
                    }
                }
                break;

            case FotaUtils.UPDATE_REDBEND_FINISHED:
                boolean success = (Boolean) msg.obj;
                if (GmBasicHandler.getFotaService().ctx != null) {
                    Log.d(TAG,
                            "[Handler.handleMessage] handle message update to success : "
                                    + success);
                    GmFotaService.updateFinished(success);
                }
                break;

            default:
                return;
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SEND_OVER, hasSend);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        hasSend = savedInstanceState.getBoolean(SEND_OVER, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "[onDestroy] enter");
        mIsDestroyed = true;
        if (mUSBInformDialog != null && mUSBInformDialog.isShowing()) {
            Log.d(TAG, "[onDestroy] dismiss the dialog");
            mUSBInformDialog.dismiss();
        }
//        unregisterReceiver(mFirmwareUpdateReceiver);
        FotaOperator.getInstance(this).unregisterFotaCallback(mFotaCallback);
        unregisterReceiver(mReceiver);
        this.mTransferTask.cancel(true);
//        sIsSending = false;
        
        super.onDestroy();
    }

    // Initialize file path
    public static File initFilePath(Context context, int which) {
        // Prefer to save log in external storage
        Log.d(TAG, "[initFilePath] which : " + which);
        if (which == -1) {
            throw new IllegalArgumentException("WRONG FILE CHOOSER1");
        }

        switch (which) {
        case FotaUtils.FIRMWARE_REDBEND_FOTA:
            mfileName = "firmware.bin";
            break;

        case FotaUtils.FIRMWARE_UBIN:
            mfileName = "firmware.bin";
            break;

        case FotaUtils.FIRMWARE_VIA_USB:
            mfileName = "firmware.zip";
            break;

        default:
            throw new IllegalArgumentException("WRONG FILE CHOOSER2");
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mfilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            mfilePath = context.getFilesDir().getAbsolutePath();
        }

        File file = new File(mfilePath, mfileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        mfilePath = file.getAbsolutePath();
        Log.v(TAG, "initFilePath file " + mfilePath);
        return file;
    }

    private AsyncTask<Void, Void, Void> mTransferTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... parameters) {
            sIsSending = true;

//            mBtTransferedCount = 0;
//            mMaxBtTransferCount = 0;

            switch (mFirmwareMethod) {
            case FotaUtils.FIRMWARE_REDBEND_FOTA:
                Log.d(TAG, "[doInBackground] begin FIRMWARE_REDBEND_FOTA");
//                mIsUpdating = true;
                FotaUtils.updateUpdatingStatus(mContext, true, mModelString, mVersionString, mDevIdString);
//                FotaUtils.sendDataViaBt(FotaUtils.FIRMWARE_REDBEND_FOTA, sendFile, mMaxProgressListener);
                FotaOperator.getInstance(UpdateFirmwareActivity.this).sendFotaFirmwareData(FotaUtils.FIRMWARE_REDBEND_FOTA, mfilePath);
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_UPDATE_UPDATING_STATUS;
                mHandler.sendMessageDelayed(msg, MSG_UPDATE_TIMEOUT);
                break;
                
            case FotaUtils.FIRMWARE_UBIN:
                Log.d(TAG, "[doInBackground] begin FIRMWARE_UBIN");
//                mIsUpdating = true;
//                FotaUtils.sendDataViaBt(FotaUtils.FIRMWARE_UBIN, sendFile, mMaxProgressListener);
                FotaOperator.getInstance(UpdateFirmwareActivity.this).sendFotaFirmwareData(FotaUtils.FIRMWARE_UBIN, mfilePath);
                break;
                
            case FotaUtils.FIRMWARE_FULL_BIN:
                Log.d(TAG, "[doInBackground] begin FIRMWARE_FULL_BIN");
//                mIsUpdating = true;
//                FotaUtils.sendDataViaBt(FotaUtils.FIRMWARE_FULL_BIN, sendFile, mMaxProgressListener);
                if (mfilePath != null) {
                    FotaOperator.getInstance(UpdateFirmwareActivity.this).sendFotaFirmwareData(FotaUtils.FIRMWARE_FULL_BIN, mfilePath);
                } else if (mSelectFileUri != null) {
                    FotaOperator.getInstance(UpdateFirmwareActivity.this).sendFotaFirmwareData(FotaUtils.FIRMWARE_FULL_BIN, mSelectFileUri);
                } else {
                    
                }
                break;
                
            case FotaUtils.FIRMWARE_ROCK_FOTA:
                Log.d(TAG, "[doInBackground] begin FIRMWARE_ROCK_FOTA");
//                mIsUpdating = true;
//                FotaUtils.sendDataViaBt(FotaUtils.FIRMWARE_ROCK_FOTA, sendFile, mMaxProgressListener);
                FotaOperator.getInstance(UpdateFirmwareActivity.this).sendFotaFirmwareData(FotaUtils.FIRMWARE_ROCK_FOTA, mfilePath);
                break;
                
            case FotaUtils.FIRMWARE_VIA_USB:
            case FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER:
                Log.d(TAG, "[doInBackground] begin FIRMWARE_VIA_USB || FIRMWARE_VIA_USB_FILE_MANAGER");
                mIsDeCompressing = true;
                updateSendSccuessState(false);
                File sendFile = new File(mfilePath);
                File f = FotaUtils.getCfgFile(UpdateFirmwareActivity.this, sendFile, mFirmwareMethod);
                if(f == null) {
                    sIsSending = false;
                    updateSendSccuessState(false);
                    showToast(mContext.getString(R.string.no_cfg_file_existed_exception));
                    sendNoCfgFileFoundException();
                    FotaUtils.deleteUnzipFiles(UpdateFirmwareActivity.this, mFirmwareMethod);
                    mIsDeCompressing = false;
                    return null;
                }
                if(mDownloader.GetCFGFile(f.getAbsolutePath()).isEmpty()
                        || mDownloader.GetCFGFile(f.getAbsolutePath())==null) {
                    Log.d(TAG, "[doInBackground] can not find the cfg file!");
                    sIsSending = false;
                    updateSendSccuessState(false);
                    sendNoCfgFileFoundException();
                    FotaUtils.deleteUnzipFiles(UpdateFirmwareActivity.this, mFirmwareMethod);
                    mIsDeCompressing = false;
                    return null;
                }

                mIsDeCompressing = false;
                showConfirmDialog();
                break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(TAG, "[mTransferTaks] onPostExecute called");
//            doFinishAction();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "[mTransferTaks] onCancelled is called, update UX");
        }
    };
    
    private void sendNoCfgFileFoundException() {
        Message msg = mHandler.obtainMessage();
        msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
        msg.arg1 = FotaUtils.MSG_ARG1_UNZIP_IMAGE_FINISHED;
        mHandler.sendMessage(msg);
    }
    
    private void showConfirmDialog() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateFirmwareActivity.this);
                builder.setTitle("Information");
                builder.setMessage("Click OK to begin download, and plugin the USB cable into smart device");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        Runnable r = new Runnable() {

                            @Override
                            public void run() {
                                Log.d(TAG, "[r] begin to download via usb");
                                Message msg = mHandler.obtainMessage();
                                msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                                msg.arg1 = FotaUtils.MSG_ARG1_UPDATING_VIA_USB;
                                mHandler.sendMessage(msg);
                                boolean b = mDownloader.startDownload();
                                Log.d(TAG, "[r] begin to download via usb B : " + b);
                                if (!b) {
                                    Message msg1 = mHandler.obtainMessage();
                                    msg1.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                                    msg1.arg1 = FotaUtils.MSG_ARG1_DOWNLOAD_FAILED;
                                    mHandler.sendMessage(msg1);
                                    sIsSending = false;
                                    updateSendSccuessState(false);
                                    FotaUtils.deleteUnzipFiles(UpdateFirmwareActivity.this, mFirmwareMethod);
                                }
                            }
                            
                        };
                        new Thread(r).start();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Log.d(TAG, "[R] cancel the download action by use click cancel button");
                        sIsSending = false;
                        updateSendSccuessState(true);
                        FotaUtils.deleteUnzipFiles(UpdateFirmwareActivity.this, mFirmwareMethod);
                        arg0.dismiss();
                    }
                });
                builder.setCancelable(false);
                mUSBInformDialog = builder.create();
                if (!mIsDestroyed) {
                    mUSBInformDialog.show();
                }
            }
            
        });
        
    }

    private void doFinishAction() {
        if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN
                || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
            Log.d(TAG, "[doFinishAction] BT donwload finished");
            hasSend = true;
            sIsSending = false;
            showToast(mContext.getString(R.string.send_firmware_date_success));
        } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
            if (mIsUsbDownloadFinished) {
                Log.d(TAG, "[doFinishAction] USB donwload finished");
                hasSend = true;
                sIsSending = false;
                updateSendSccuessState(true);
                FotaUtils.deleteUnzipFiles(this, mFirmwareMethod);
            }
        }
    }
    
    private void showToast(final String str) {
        if (str == null) {
            return;
        }
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(UpdateFirmwareActivity.this,
                        str, Toast.LENGTH_SHORT).show();
            }
            
        });
    }
    
    private void updateCurrentProgress(final int progress) {
        if (progress < 0) {
            return;
        }
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String str = String.valueOf(progress) + " %";
                
                Log.d(TAG, "[updateCurrentProgress] str : " + str);
                mBottomPreference.setSummary(str);
              //add by lixiang for fota 20150909
                if(MainActivity.USER_FOTA){
                if(progress == 100){
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);  
                startActivity(intent);
                finish();
                }
                }
            }
            
        });
    }
    
    private void updateTextView(final int which) {
        if (which == FotaUtils.MSG_ARG1_UPDATE_FINISHED) {
            this.mTopPreference.setSummary(R.string.updated_firmware);
        } else if (which == FotaUtils.MSG_ARG1_UPDATE_FAILED_CAUSE_DISCONNECTED) {
            if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                    || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                    || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN
                    || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
                this.mTopPreference.setSummary(R.string.bt_disconnected_while_transfer);
            } else if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                    || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
                this.mTopPreference.setSummary(R.string.usb_disconnected_while_transfer);
            }
        } else if (which == FotaUtils.MSG_ARG1_UNZIP_IMAGE_FINISHED) {
            if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                    || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
                this.mTopPreference.setSummary(R.string.unzip_image_finished_text);
            }
        } else if (which == FotaUtils.MSG_ARG1_UPDATING_VIA_USB) {
            if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                    || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
                this.mTopPreference.setSummary(R.string.updating_firmware_via_usb_text);
            }
        } else if (which == FotaUtils.MSG_ARG1_DOWNLOAD_FAILED) {
            this.mTopPreference.setSummary(R.string.download_failed_text);
        } else if (which == FotaUtils.MSG_ARG1_DOWNLOAD_FINISHED) {
            this.mTopPreference.setSummary(R.string.download_succeed_via_bt);
            
            //add by lixiang for fota 20150909
        } else if (which == FotaUtils.MSG_ARG1_NO_CFG_FOUND_EXCEPTION) {
            this.mTopPreference.setSummary(R.string.no_cfg_file_existed_exception);
        } else if (which == FotaUtils.FILE_NOT_FOUND_ERROR) {
            this.mTopPreference.setSummary(R.string.firmware_file_not_found);
        } else if (which == FotaUtils.READ_FILE_FAILED) {
            this.mTopPreference.setSummary(R.string.failed_to_read_firmware_file);
        }
    }

    private void updateSendSccuessState(boolean b) {
        Log.d(TAG, "[updateSendSccuessState] update success state to be : " + b);
        SharedPreferences sp = getSharedPreferences(FotaUtils.FOTA_UPDATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FotaUtils.FOTA_UPDATE_STATUS_FLAG_STRING, b);
        editor.commit();
    }

    private void sendUpdateFinishedMessage(boolean b) {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_UPDATE_FINISHED;
        msg.obj = b;
        mHandler.sendMessage(msg);
    }
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.e(TAG, "[mReceiver] the intent is null");
                return;
            }
            String action = intent.getAction();
            Log.d(TAG, "[mReceiver] intent action : " + action);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Log.d(TAG, "[mReceiver] received BluetoothAdapter.ACTION_STATE_CHANGED");
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Log.d(TAG, "[mReceiver] bluetooth adapter state : " + state);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Log.e(TAG, "[mReceiver] do cancel transfer action");
                    mTransferTask.cancel(true);
                    if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                            || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                            || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN
                            || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
                        sIsSending = false;
                        hasSend = false;
                        if (!mIsBtTransferFinished) {
//                            mIsUpdating = false;
                            Message msg = mHandler.obtainMessage();
                            msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                            msg.arg1 = FotaUtils.MSG_ARG1_UPDATE_FAILED_CAUSE_DISCONNECTED;
                            mHandler.sendMessage(msg);
                            if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
                                FotaUtils.updateUpdatingStatus(mContext, false, mModelString, mVersionString, mDevIdString);
                                sendUpdateFinishedMessage(false);
                            }
                        }
                    }
                }
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //// check the usb cable has been plugout or not
                Log.e(TAG, "[mReceiver] received ACTION_USB_DEVICE_DETACHED");
                if (mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB
                        || mFirmwareMethod == FotaUtils.FIRMWARE_VIA_USB_FILE_MANAGER) {
                    mTransferTask.cancel(true);
                    sIsSending = false;
                    hasSend = false;
                    if (!mIsUsbDownloadFinished) {
                        updateSendSccuessState(false);
                        Message msg = mHandler.obtainMessage();
                        msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                        msg.arg1 = FotaUtils.MSG_ARG1_UPDATE_FAILED_CAUSE_DISCONNECTED;
                        mHandler.sendMessage(msg);
                    }
                    FotaUtils.deleteUnzipFiles(UpdateFirmwareActivity.this, mFirmwareMethod);
                }
            }
        }

    };
    
    private IFotaOperatorCallback mFotaCallback = new IFotaOperatorCallback() {

        @Override
        public void onFotaTypeReceived(int fotaType) {
            
        }

        @Override
        public void onCustomerInfoReceived(String information) {
            
        }

        @Override
        public void onFotaVersionReceived(FotaVersion version) {
            
        }

        @Override
        public void onStatusReceived(int status) {
            Log.d(TAG, "[onStatusReceived] status : " + status);
            
            switch(status) {
            case FotaUtils.FOTA_SEND_VIA_BT_SUCCESS:
                Log.d(TAG, "[onStatusReceived] send succeed. update text view");
                Message msg = mHandler.obtainMessage();
                msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                msg.arg1 = FotaUtils.MSG_ARG1_DOWNLOAD_FINISHED;
                mHandler.sendMessage(msg);
                mIsBtTransferFinished = true;
//                mIsUpdating = false;
                mTransferViaBTErrorHappened = false;
                break;
                
            case FotaUtils.FOTA_UPDATE_VIA_BT_SUCCESS:
                Message msg1 = mHandler.obtainMessage();
                msg1.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                msg1.arg1 = FotaUtils.MSG_ARG1_UPDATE_FINISHED;
                mHandler.sendMessage(msg1);
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
//                    GmFotaService.updateFinished(true);
                    FotaUtils.updateUpdatingStatus(mContext, false, mModelString, mVersionString, mDevIdString);
                    sendUpdateFinishedMessage(true);
                }
                mHandler.removeMessages(MSG_UPDATE_UPDATING_STATUS);
                mTransferViaBTErrorHappened = false;
                break;
                
            case FotaUtils.FOTA_UPDATE_VIA_BT_COMMON_ERROR:
            case FotaUtils.FOTA_UPDATE_VIA_BT_WRITE_FILE_FAILED:
            case FotaUtils.FOTA_UPDATE_VIA_BT_DISK_FULL:
            case FotaUtils.FOTA_UPDATE_VIA_BT_DATA_TRANSFER_ERROR:
                Log.d(TAG, "[onStatusReceived] transfer error happened, set mTransferViaBTErrorHappened to be TRUE");
                mTransferViaBTErrorHappened = true;
            case FotaUtils.FOTA_UPDATE_VIA_BT_TRIGGER_FAILED:
            case FotaUtils.FOTA_UPDATE_VIA_BT_FAILED:
            case FotaUtils.FOTA_UPDATE_TRIGGER_FAILED_CAUSE_LOW_BATTERY:
            case FotaUtils.FILE_NOT_FOUND_ERROR:
            case FotaUtils.READ_FILE_FAILED:
                Log.d(TAG, "[onStatusReceived] update failed!");
                if (!mTransferTask.isCancelled() && mTransferTask.getStatus() == AsyncTask.Status.RUNNING) {
                    Log.d(TAG, "[onStatusReceived] cancel the transfer action");
                    mTransferTask.cancel(true);
                }
                sIsSending = false;
                
                String str = null;
                if (status == FotaUtils.FOTA_UPDATE_TRIGGER_FAILED_CAUSE_LOW_BATTERY) {
                    Log.d(TAG, "[onStatusReceived] FOTA_UPDATE_TRIGGER_FAILED_CAUSE_LOW_BATTERY");
                    str = UpdateFirmwareActivity.this.getString(R.string.trigger_failed_due_to_low_battery);
                } else {
                    str = UpdateFirmwareActivity.this.getString(R.string.update_failed);
                }
                showToast(str);
                mIsBtTransferFinished = false;
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
//                    GmFotaService.updateFinished(false);
                    sendUpdateFinishedMessage(false);
                    FotaUtils.updateUpdatingStatus(mContext, false, mModelString, mVersionString, mDevIdString);
                }
                mHandler.removeMessages(MSG_UPDATE_UPDATING_STATUS);
//                mIsUpdating = false;
                
                Message msg2 = mHandler.obtainMessage();
                msg2.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                if (status == FotaUtils.FILE_NOT_FOUND_ERROR) {
                    msg2.arg1 = FotaUtils.FILE_NOT_FOUND_ERROR;
                } else if (status == FotaUtils.READ_FILE_FAILED) {
                    msg2.arg1 = FotaUtils.READ_FILE_FAILED;
                } else {
                    msg2.arg1 = FotaUtils.MSG_ARG1_DOWNLOAD_FAILED;
                }
                mHandler.sendMessage(msg2);
                
                break;
            
            default:
                break;
           }
        }

        @Override
        public void onConnectionStateChange(int newConnectionState) {
            if (newConnectionState == WearableManager.STATE_CONNECT_LOST) {
                Log.d(TAG, "[onConnectionStateChange] the state is : STATE_CONNECT_LOST");
                mTransferTask.cancel(true);
                if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA
                        || mFirmwareMethod == FotaUtils.FIRMWARE_UBIN
                        || mFirmwareMethod == FotaUtils.FIRMWARE_FULL_BIN
                        || mFirmwareMethod == FotaUtils.FIRMWARE_ROCK_FOTA) {
                    sIsSending = false;
                    hasSend = false;
                    if (!mIsBtTransferFinished) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
                        msg.arg1 = FotaUtils.MSG_ARG1_UPDATE_FAILED_CAUSE_DISCONNECTED;
                        mHandler.sendMessage(msg);
                        if (mFirmwareMethod == FotaUtils.FIRMWARE_REDBEND_FOTA) {
//                            GmFotaService.updateFinished(false);
                            FotaUtils.updateUpdatingStatus(mContext, false, mModelString, mVersionString, mDevIdString);
                            sendUpdateFinishedMessage(false);
                        }
//                        mIsUpdating = false;
                        mHandler.removeMessages(MSG_UPDATE_UPDATING_STATUS);
                    }
                }
            }
        }

        @Override
        public void onProgress(int progress) {
            if (!mTransferViaBTErrorHappened) {
                /*if (progress == 1.0) {
                    mBtTransferedCount ++;
                    Log.d(TAG, "[onProgress] mBtTransferedCount : " + mBtTransferedCount
                            + ", mMaxBtTransferCount : " + mMaxBtTransferCount);
                    updateCurrentProgress(mBtTransferedCount, mMaxBtTransferCount);
                    if (mBtTransferedCount == mMaxBtTransferCount) {
                        doFinishAction();
                    }
                }*/
                Log.d(TAG, "[onProgress] progress : " + progress);
                updateCurrentProgress(progress);
                if (progress == 100) {
                    doFinishAction();
                }
            } else {
                Log.d(TAG, "[onProgress] mTransferViaBTErrorHappened is TRUE, no need to update progress");
            }
        }
        
    };
    
    private com.mediatek.ctrl.fota.downloader.IDownloadInterface mDownloadInterface =
            new com.mediatek.ctrl.fota.downloader.IDownloadInterface() {

        @Override
        public void onProgressUpdated(int progress) {
            Log.d(TAG, "[onProgressUpdated] progress : " + progress);
            updateCurrentProgress(progress);
        }

        @Override
        public void onDownloadFinished() {
            Log.d(TAG, "[onDownloadFinished] enter ");
            mIsUsbDownloadFinished = true;
            Message msg = mHandler.obtainMessage();
            msg.what = FotaUtils.MSG_UPDATE_TEXT_VIEW;
            msg.arg1 = FotaUtils.MSG_ARG1_UPDATE_FINISHED;
            mHandler.sendMessage(msg);
            doFinishAction();
        }

        @Override
        public void onStatus(int which) {
            String str = null;
            switch (which) {
            case Downloader.USB_DEATTACHED:
                str = mContext.getString(R.string.device_detach);
                break;
                
            case Downloader.USB_DEVICE_LOAD_MISMATCH:
                str = mContext.getString(R.string.load_device_mismatch);
                break;
                
            case Downloader.USB_DEVICE_NOT_FOUND:
                str = mContext.getString(R.string.no_device);
                break;
                
            case Downloader.USB_DOWNLOAD_FAILED:
                str = mContext.getString(R.string.DL_fail);
                break;
                
            case Downloader.USB_DOWNLOAD_SUCCESSFUL:
                str = mContext.getString(R.string.DL_success);
                break;
                
            case Downloader.USB_FEATURE_NOT_SUPPORTED:
                str = mContext.getString(R.string.no_feature);
                break;
                
            case Downloader.USB_PERMISSION_DENY:
                str = mContext.getString(R.string.permission_deny);
                break;
                
            case Downloader.USB_SMART_PHONE_LOAD_NOT_SUPPORT:
                str = mContext.getString(R.string.load_not_right);
                break;
                
                default:
                    break;
            }
            Log.d(TAG, "[onStatus] showToast str : " + str);
            if (str != null) {
                showToast(str);
            }
        }
        
    };

}
