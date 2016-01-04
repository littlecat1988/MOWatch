package com.mtk.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gomtel.util.DialogHelper;
import com.mediatek.ctrl.fota.common.FotaOperator;
import com.mediatek.ctrl.fota.common.FotaVersion;
import com.mediatek.ctrl.fota.common.IFotaOperatorCallback;
import com.mediatek.ctrl.music.RemoteMusicController;
import com.mediatek.ctrl.sos.SOSController;
import com.mediatek.leprofiles.hr.HRListener;
import com.mediatek.leprofiles.hr.HeartRateClientProxy;
import com.mediatek.wearable.DeviceInfo;
import com.mediatek.wearable.DeviceInfoListener;
import com.mediatek.wearable.DeviceNameListener;
import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.applist.ApplistActivity;
import com.mtk.app.fota.FotaUtils;
import com.mtk.app.fota.SmartDeviceFirmware;
import com.mtk.app.fota.UpdateFirmwareActivity;
import com.mtk.app.notification.NotificationAppListActivity;
import com.mtk.app.sos.MultiKeySOSActivity;
import com.mtk.app.sos.OneKeySOSActivity;
import com.mtk.bluetoothle.FitnessActivity;
import com.mtk.bluetoothle.FitnessHelper;
import com.mtk.bluetoothle.LeProfileUtils;
import com.mtk.bluetoothle.PxpStatusChangeReceiver;
import com.mtk.btnotification.R;

public class MainActivity extends PreferenceActivity {

	private static final String TAG = "AppManager/MainActivity";
	private static final String FOTA_TAG = "[FOTA_UPDATE][MainActivity]";

	public static final Intent ACCESSIBILITY_INTENT = new Intent(
			"android.settings.ACCESSIBILITY_SETTINGS");

	public static final Intent NOTIFICATION_LISTENER_INTENT = new Intent(
			"android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");

	/**
	 * redbend fota menu item id
	 */
	private static final int MENU_ITEM_REDBEND_FOTA = 1;
	/**
	 * Separate bin fota menu item id
	 */
	private static final int MENU_ITEM_SEPARATE_BIN_FOTA = 2;
	/**
	 * USB cable fota menu item it
	 */
	private static final int MENU_ITEM_USB_CABLE_FOTA = 3;
	/**
	 * USB cable fota which zip file from file manager menu item id
	 */
	private static final int MENU_ITEM_USB_FILE_MANAGER_FOTA = 4;

	private static final int MENU_ITEM_FULL_BIN_FOTA = 5;

	private Toast mToast = null;

	public static Context mContext;

	private CustomPreference mAppInfoPreference;

	private FindMePreference mFindMePreference;

	private Preference mNotificationPreference;

	private Preference mApplicationPreference;

	private PxpAlertSwitchPreference mPxpAlertPrefernece;

	private Preference mAboutPreference;

	private Preference mScanPreference;

	private Preference mDevicePreference;

	private Preference mChangeNamePreference;

	private Preference mSwitchPreference;

	private Preference mFitPreference;

	private ListPreference mMusicPreference;

	private Preference mSOSPreference;

	private boolean mNeedUpdatePreference = false;

	/**
	 * The wearable device support RedBend fota or not
	 */
	private boolean mIsSupportRedBendFota = false;
	/**
	 * The wearable device support separate bin fota or not
	 */
	private boolean mIsSupportSeparateBinFota = false;
	/**
	 * The wearable device support USB cable fota or not
	 */
	private boolean mIsSupportUsbCableFota = false;

	private boolean mIsSupportFBinFota = false;

	private boolean mIsSupportRockFota = false;

	// BTD mark
	private static final boolean BTD = false;
	// added by lixiang for user fota 20150820 begin
	private static final String FOTA_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/FOTA";
	public static final boolean USER_FOTA = false;
	private static final String urlDownload = "http://wx.wuxinyanglao.com/dl/?item=w302_firmware";
	private static final String urlVersion = "http://wx.wuxinyanglao.com/api/watchImageQuery/";
	private static final String dirName = Environment.getExternalStorageDirectory().getPath()+"/FOTA/";
	private ProgressDialog dialog_download;
	protected Handler mHandler_download = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showSyncDialog();
				break;
			case 1:
				DialogHelper.dismissDialog(dialog_download);
				break;
			case 2:

				break;
			default:
				break;
			}
		}
	};
	protected void showSyncDialog()
	  {
	    if (dialog_download == null)
	    {
	    	dialog_download = DialogHelper.showProgressDialog(this, "");
	    	dialog_download.setCanceledOnTouchOutside(false);
	    	dialog_download.show();
	      return;
	    }
	    dialog_download.show();
	  }
	// added by lixiang for user fota 20150820 end

	private ProgressDialog mInstallingDialog;

	private static SoftReference<MainActivity> sMainActivity = null;

	private WearableListener mWearableListener = new WearableListener() {

		@Override
		public void onDeviceChange(BluetoothDevice device) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateMainActivity();
				}
			});
		}

		@Override
		public void onConnectChange(int oldState, int newState) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateMainActivity();
				}
			});
			if (oldState == WearableManager.STATE_CONNECTED
					&& newState == WearableManager.STATE_CONNECT_LOST) {
				if (mCurDialog != null && mCurDialog.isShowing()) {
					try {
						Log.d(TAG, "mCurDialog.dismiss begin");
						mCurDialog.dismiss();
					} catch (Exception e) {
						Log.d(TAG, "mCurDialog.dismiss Exception");
					}
				}
			}

			if (newState == WearableManager.STATE_CONNECTED) {
				Log.d(TAG,
						"[onConnectChange] state connected, do check fota type");
				sendFotaCheckCommand();
			}
			if (newState == WearableManager.STATE_CONNECT_LOST) {
				mIsSupportRedBendFota = false;
				mIsSupportSeparateBinFota = false;
				mIsSupportUsbCableFota = false;
				mIsSupportRockFota = false;
				mIsSupportFBinFota = false;

				MainActivity.this.invalidateOptionsMenu();
			}
		}

		private void updateMainActivity() {
			PreferenceGroup basePreference = (PreferenceGroup) findPreference("setting_base");
			PreferenceGroup csPreferenceGroup = (PreferenceGroup) findPreference("connetion_status");
			BluetoothDevice remoteDevice = WearableManager.getInstance()
					.getRemoteDevice();
			basePreference.removePreference(mSOSPreference);// added by lixiang
															// for hide SOS
															// 20150610
			if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_DOGP) {
				mSwitchPreference.setTitle(R.string.switch_mode_dogp);
			} else {
				mSwitchPreference.setTitle(R.string.switch_mode_spp);
			}
			// deleted by lixiang for hide SOS 20150610
			// if (WearableManager.getInstance().getRemoteDeviceVersion() >=
			// WearableManager.VERSION_340) {
			// basePreference.addPreference(mSOSPreference);
			// // basePreference.removePreference(mSOSPreference);//modified by
			// lixiang for hide SOS 20150610
			// } else {
			// basePreference.removePreference(mSOSPreference);
			// }

			if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_38) {
				basePreference.addPreference(mMusicPreference);
			} else {
				basePreference.removePreference(mMusicPreference);
			}
			// if (WearableManager.getInstance().getRemoteDeviceVersion() >=
			// WearableManager.VERSION_340) {
			// basePreference.addPreference(mFitPreference);
			// } else {
			// basePreference.removePreference(mFitPreference);
			// }

			if (remoteDevice == null) {
				mNeedUpdatePreference = true;
				basePreference.removeAll();
				basePreference.setTitle("");
				csPreferenceGroup.setTitle("");
				csPreferenceGroup.removePreference(mFindMePreference);
				if (!BTD) {
					basePreference.addPreference(mSwitchPreference);
				}

			} else if (mNeedUpdatePreference) {
				mNeedUpdatePreference = false;
				basePreference.setTitle(R.string.smart_accessory_application);
				csPreferenceGroup.setTitle(R.string.device);
				basePreference.addPreference(mNotificationPreference);
				if (Build.VERSION.SDK_INT >= 18) {
					csPreferenceGroup.addPreference(mFindMePreference);
					basePreference.addPreference(mPxpAlertPrefernece);
				} else {
					csPreferenceGroup.removePreference(mFindMePreference);
					basePreference.removePreference(mPxpAlertPrefernece);
					// basePreference.removePreference(mFitPreference);
				}
				basePreference.addPreference(mAboutPreference);
				basePreference.addPreference(mScanPreference);
				basePreference.addPreference(mDevicePreference);
				if (!BTD) {
					basePreference.addPreference(mApplicationPreference);
					if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_35) {
						basePreference.addPreference(mChangeNamePreference);
					} else {
						basePreference.removePreference(mChangeNamePreference);
					}
					basePreference.addPreference(mSwitchPreference);
				}

			} else if (WearableManager.getInstance().getRemoteDeviceVersion() < WearableManager.VERSION_35) {
				basePreference.removePreference(mChangeNamePreference);
			} else if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_35) {
				if (!BTD) {
					basePreference.addPreference(mChangeNamePreference);
				}
			}
			// added by lixiang for user fota 20150820 begin
			if (USER_FOTA) {
				if (remoteDevice == null && csPreferenceGroup != null)
					csPreferenceGroup.removePreference(mFotaPreference);
				if (remoteDevice != null && csPreferenceGroup != null && WearableManager.getInstance().getConnectState() != WearableManager.STATE_CONNECTED )
					csPreferenceGroup.removePreference(mFotaPreference);
//					mFotaPreference.setEnabled(false);
				if (csPreferenceGroup != null && WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED)
					csPreferenceGroup.addPreference(mFotaPreference);

				basePreference.removeAll();
				basePreference.setTitle("");

			}
			// added by lixiang for user fota 20150820 end
		}

		@Override
		public void onDeviceScan(BluetoothDevice device) {
		}

		@Override
		public void onModeSwitch(int newMode) {
			Log.d(TAG, "[wearable][MainActivity] onModeSwitch " + newMode);
			if (newMode == -1) {
				if (android.os.Build.VERSION.SDK_INT < 18
						|| !mContext.getPackageManager().hasSystemFeature(
								PackageManager.FEATURE_BLUETOOTH_LE)) {
					Toast.makeText(mContext, R.string.ble_not_supported,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext, R.string.cannot_switch_mode,
							Toast.LENGTH_LONG).show();
				}
			} else if (newMode == WearableManager.MODE_DOGP) {
				Toast.makeText(mContext, R.string.switch_mode_dogp,
						Toast.LENGTH_LONG).show();
			} else if (newMode == WearableManager.MODE_SPP) {
				Toast.makeText(mContext, R.string.switch_mode_spp,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	// add by lixiang for user fota 20150820
	private Preference mFotaPreference;
	private Preference mSearchPreference;
	private Preference mgetFilePreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[wearable][MainActivity] onCreate begin");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			FitnessHelper.getInstance().authInProgress = savedInstanceState
					.getBoolean(FitnessHelper.AUTH_PENDING);
		}

		Activity mainActivity = sMainActivity == null ? null : sMainActivity
				.get();
		if (mainActivity != null && !mainActivity.isFinishing()) {
			Log.d(TAG, "[wearable][MainActivity] mainActivity.finish");
			mainActivity.finish();
		}
		sMainActivity = new SoftReference(this);

		addPreferencesFromResource(R.xml.preferences);
		mContext = this;
		mToast = Toast.makeText(MainActivity.this, R.string.no_connect, Toast.LENGTH_SHORT);

		mAppInfoPreference = (CustomPreference) findPreference("app_info");
		Preference.OnPreferenceClickListener onAppInfoClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				BluetoothDevice remoteDevice = WearableManager.getInstance()
						.getRemoteDevice();
				if (remoteDevice == null) {
					startActivity(new Intent(MainActivity.this,
							DeviceScanActivity.class));
				} else if (!WearableManager.getInstance().isAvailable()) {
					WearableManager.getInstance().connect();
				} else if (WearableManager.getInstance().isAvailable()) {
					WearableManager.getInstance().disconnect();
				}
				return true;
			}
		};
		// add by lixiang for user fota 20150820 begin
		if (USER_FOTA) {
			mFotaPreference = (Preference) findPreference("fota");
			mFotaPreference
					.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							File file_fota = new File(FOTA_PATH);
							if (file_fota.exists()) {
								File[] files = file_fota.listFiles();
								if (files.length > 0) {
									Log.e(TAG, files[0].getPath());
								}
								Intent intent_fota = new Intent(
										MainActivity.this,
										UpdateFirmwareActivity.class);
								intent_fota.putExtra(
										FotaUtils.INTENT_EXTRA_INFO,
										FotaUtils.FIRMWARE_FULL_BIN);
								

									intent_fota.putExtra(
											FotaUtils.ZIP_FILE_PATH, files[0].getPath());

								startActivity(intent_fota);
							}else{
								Toast.makeText(MainActivity.this,
										"please check your fota file!",
										Toast.LENGTH_SHORT).show();
							}
							return true;
						}

					});
			
			mgetFilePreference = (Preference) findPreference("getfile");
			mgetFilePreference
					.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							getFileFromServer();
							return true;
						}

					});
			
			mSearchPreference = (Preference) findPreference("search");
			mSearchPreference
					.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							if (isFastDoubleClick()) {
								Log.d(TAG, "isFastDoubleClick TYPE_SCAN return");
								return true;
							}
							BluetoothAdapter btAdapter = BluetoothAdapter
									.getDefaultAdapter();
							if (!btAdapter.isEnabled()) {
								Toast.makeText(mContext, R.string.pls_switch_bt_on,
										Toast.LENGTH_SHORT).show();
								return false;
							}
							if (WearableManager.getInstance().isConnecting()
									|| WearableManager.getInstance().isAvailable()
									|| WearableManager.getInstance().getConnectState() == WearableManager.STATE_DISCONNECTING) {
								Toast.makeText(mContext, R.string.cannot_scan,
										Toast.LENGTH_SHORT).show();
								return false;
							}
							startActivity(new Intent(MainActivity.this,
									DeviceScanActivity.class));
							return true;
						}

					});
		}
		// add by lixiang for user fota 20150820 end
		mAppInfoPreference.setOnPreferenceClickListener(onAppInfoClickListener);

		mFindMePreference = (FindMePreference) findPreference("find_me");

		mNotificationPreference = (Preference) findPreference("btnotification");
		Preference.OnPreferenceClickListener onNotificationPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(MainActivity.this,
						NotificationAppListActivity.class));
				return true;
			}
		};
		mNotificationPreference
				.setOnPreferenceClickListener(onNotificationPreferenceClickListener);

		mApplicationPreference = findPreference("applications");
		Preference.OnPreferenceClickListener onApplicationPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (!WearableManager.getInstance().isAvailable()) {
					mToast.show();
					return true;
				} else if (MainService.getInstance().getWriteAppConfigStatus() == false) {
					Toast.makeText(MainActivity.this,
							getString(R.string.write_config_not_done), Toast.LENGTH_SHORT)
							.show();
					return true;
				} else {
					startActivity(new Intent(MainActivity.this,
							ApplistActivity.class));
					return true;
				}

			}
		};
		mApplicationPreference
				.setOnPreferenceClickListener(onApplicationPreferenceClickListener);

		mPxpAlertPrefernece = (PxpAlertSwitchPreference) findPreference("alert_set_preference");

		mAboutPreference = (Preference) findPreference("about");
		Preference.OnPreferenceClickListener onAboutPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(MainActivity.this, AboutActivity.class));
				return true;
			}
		};
		mAboutPreference
				.setOnPreferenceClickListener(onAboutPreferenceClickListener);

		mScanPreference = (Preference) findPreference("scan");
		Preference.OnPreferenceClickListener onScanPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (isFastDoubleClick()) {
					Log.d(TAG, "isFastDoubleClick TYPE_SCAN return");
					return true;
				}
				BluetoothAdapter btAdapter = BluetoothAdapter
						.getDefaultAdapter();
				if (!btAdapter.isEnabled()) {
					Toast.makeText(mContext, R.string.pls_switch_bt_on,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				if (WearableManager.getInstance().isConnecting()
						|| WearableManager.getInstance().isAvailable()
						|| WearableManager.getInstance().getConnectState() == WearableManager.STATE_DISCONNECTING) {
					Toast.makeText(mContext, R.string.cannot_scan,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				startActivity(new Intent(MainActivity.this,
						DeviceScanActivity.class));
				return true;
			}
		};
		mScanPreference
				.setOnPreferenceClickListener(onScanPreferenceClickListener);

		mDevicePreference = (Preference) findPreference("deviceinfo");
		Preference.OnPreferenceClickListener onDevicePreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (isFastDoubleClick()) {
					Log.d(TAG, "isFastDoubleClick TYPE_DEVICEINFO return");
					return true;
				}
				DeviceInfoListener listener = new DeviceInfoListener() {
					@Override
					public void notifyDeviceInfo(final DeviceInfo deviceInfo) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (deviceInfo != null) {
									showDeviceInfoDialog(deviceInfo);
								}
							}
						});
					}
				};
				if (!WearableManager.getInstance().isAvailable()) {
					mToast.show();
					return true;
				}
				WearableManager.getInstance().getDeviceInfo(listener);
				return true;
			}
		};
		mDevicePreference
				.setOnPreferenceClickListener(onDevicePreferenceClickListener);

		mChangeNamePreference = (Preference) findPreference("changename");
		Preference.OnPreferenceClickListener onChangeNamePreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (isFastDoubleClick()) {
					Log.d(TAG, "isFastDoubleClick TYPE_CHANGENAME return");
					return true;
				}
				if (!WearableManager.getInstance().isAvailable()) {
					mToast.show();
					return true;
				}
				showChangeNameDialog();
				return true;
			}
		};
		mChangeNamePreference
				.setOnPreferenceClickListener(onChangeNamePreferenceClickListener);

		mMusicPreference = (ListPreference) findPreference("selectmusic");
		if (mMusicPreference != null) {
			configureMusicControl();
		}
		Preference.OnPreferenceChangeListener onMusicPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String appName = newValue.toString();
				RemoteMusicController.getInstance(getApplicationContext())
						.setMusicApp(appName);
				return true;
			}
		};
		Preference.OnPreferenceClickListener onMusicPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (preference != null) {
					configureMusicControl();
				}
				return true;
			}
		};
		mMusicPreference
				.setOnPreferenceChangeListener(onMusicPreferenceChangeListener);
		mMusicPreference
				.setOnPreferenceClickListener(onMusicPreferenceClickListener);

		mSwitchPreference = (Preference) findPreference("switchmode");
		Preference.OnPreferenceClickListener onSwitchPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (isFastDoubleClick()) {
					Log.d(TAG, "isFastDoubleClick TYPE_SWITCH return");
					return true;
				}
				WearableManager.getInstance().switchMode();
				return true;
			}
		};
		mSwitchPreference
				.setOnPreferenceClickListener(onSwitchPreferenceClickListener);
		if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_DOGP) {
			mSwitchPreference.setTitle(R.string.switch_mode_dogp);
		} else {
			mSwitchPreference.setTitle(R.string.switch_mode_spp);
		}

		mSOSPreference = (Preference) findPreference("soscall");
		Preference.OnPreferenceClickListener onSOSPreferenceClickListener = new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (isFastDoubleClick()) {
					return true;
				}
				if (!WearableManager.getInstance().isAvailable()) {
					mToast.show();
					return true;
				}
				if (SOSController.getInstance().getKeyCount() < 1) {
					Toast.makeText(MainActivity.this, R.string.cant_enter_sos,
                            Toast.LENGTH_SHORT).show();
					return true;
				} else if (SOSController.getInstance().getKeyCount() == 1) {
					startActivity(new Intent(MainActivity.this,
							OneKeySOSActivity.class));
				} else {
					startActivity(new Intent(MainActivity.this,
							MultiKeySOSActivity.class));
				}
				return true;
			}
		};
		mSOSPreference
				.setOnPreferenceClickListener(onSOSPreferenceClickListener);

		// mFitPreference = (Preference) findPreference("fittest");
		// Preference.OnPreferenceClickListener onFitPreferenceClickListener =
		// new Preference.OnPreferenceClickListener() {
		// @Override
		// public boolean onPreferenceClick(Preference preference) {
		// if (isFastDoubleClick()) {
		// Log.d(TAG, "isFastDoubleClick TYPE_FIT return");
		// return true;
		// }
		// if (!WearableManager.getInstance().isAvailable()) {
		// mToast.show();
		// /// write Fitness data
		// writeFitnessData();
		// return true;
		// }
		// startActivity(new Intent(MainActivity.this, FitnessActivity.class));
		// return true;
		// }
		// };
		// mFitPreference.setOnPreferenceClickListener(onFitPreferenceClickListener);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < 18) {
			if (!MainService.isNotificationReceiverActived()) {
				showAccessibilityPrompt();
			}
		} else {
			if (!isNotificationListenerActived()) {
				showNotifiListnerPrompt();
			}
		}

		PreferenceGroup basePreference = (PreferenceGroup) findPreference("setting_base");
		PreferenceGroup csPreferenceGroup = (PreferenceGroup) findPreference("connetion_status");
		BluetoothDevice remoteDevice = WearableManager.getInstance()
				.getRemoteDevice();
		// added by lixiang for user fota 20150820 begin
		if (USER_FOTA) {
			basePreference.removeAll();
			basePreference.setTitle("");

		}
		// added by lixiang for user fota 20150820 end
		// modified by lixiang for hide SOS 20150610
		basePreference.removePreference(mSOSPreference);

		// if (WearableManager.getInstance().getRemoteDeviceVersion() >=
		// WearableManager.VERSION_340) {
		// basePreference.addPreference(mSOSPreference);
		// } else {
		// basePreference.removePreference(mSOSPreference);
		// }
		// modified by lixiang for hide SOS 20150610
		// if (WearableManager.getInstance().getRemoteDeviceVersion() >=
		// WearableManager.VERSION_340) {
		// basePreference.addPreference(mFitPreference);
		// } else {
		// basePreference.removePreference(mFitPreference);
		// }
		if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_38) {
			// added by lixiang for user fota 20150820 begin
			if (!USER_FOTA) {
				// added by lixiang for user fota 20150820 end
				basePreference.addPreference(mMusicPreference);
			}
		} else {
			basePreference.removePreference(mMusicPreference);
		}

		if (remoteDevice == null) {
			mNeedUpdatePreference = true;
			basePreference.removeAll();
			basePreference.setTitle("");
			basePreference.addPreference(mSwitchPreference);
			csPreferenceGroup.setTitle("");
			csPreferenceGroup.removePreference(mFindMePreference);
			// added by lixiang for user fota 20150820 begin
			if (USER_FOTA) {
				basePreference.removeAll();
				if (csPreferenceGroup != null)
					csPreferenceGroup.removePreference(mFotaPreference);
			}
			// added by lixiang for user fota 20150820 end
		}

		WearableManager.getInstance().registerWearableListener(
				mWearableListener);

		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int width = mDisplayMetrics.widthPixels;
		int height = mDisplayMetrics.heightPixels;
		Utils.setCurrHeight(height);
		Utils.setCurrWidth(width);

		// HotKnot feature
		handleIntent(getIntent());

		if (Build.VERSION.SDK_INT < 18) {
			csPreferenceGroup.removePreference(mFindMePreference);
			basePreference.removePreference(mPxpAlertPrefernece);
			// basePreference.removePreference(mFitPreference);
		}
		if (BTD) {
			basePreference.removePreference(mApplicationPreference);
			basePreference.removePreference(mChangeNamePreference);
			basePreference.removePreference(mSwitchPreference);
		}
		if (WearableManager.getInstance().getRemoteDeviceVersion() < WearableManager.VERSION_35) {
			basePreference.removePreference(mChangeNamePreference);
		}

		FotaOperator.getInstance(this).registerFotaCallback(mFotaCallback);

		Log.d("FOTA_UPDATE", "[mainactivity][onCreate] sIsSending : "
				+ UpdateFirmwareActivity.sIsSending);
		if (!UpdateFirmwareActivity.sIsSending) {
			if (WearableManager.getInstance().isAvailable()) {
				sendFotaCheckCommand();
			}
		} else {
			Intent in = new Intent(this, UpdateFirmwareActivity.class);
			in.putExtra(FotaUtils.INTENT_EXTRA_INFO,
					UpdateFirmwareActivity.sCurrentSendingFirmware);
			in.putExtra("isFromMain", true);
			this.startActivity(in);
			return;
		}

		if (ApplistActivity.mGetStautsState != 0) {
			Log.d(TAG,
					"[wearable][MainActivity] vxp install background, show dialog after 1s, state = "
							+ ApplistActivity.mGetStautsState);
			mInstallingDialog = new ProgressDialog(this);
			mInstallingDialog.setTitle(R.string.install_application);
			mInstallingDialog
					.setMessage(getString(R.string.install_background));
			mInstallingDialog.setCancelable(false);
			mInstallingDialog.show();
			Timer timer = new Timer(true);
			TimerTask task = new TimerTask() {
				public void run() {
					if (ApplistActivity.mGetStautsState == 0) {
						cancel();
						if (Build.VERSION.SDK_INT >= 17) {
							if (MainActivity.this.isDestroyed()
									|| MainActivity.this.isFinishing()) {
								return;
							}
						} else {
							if (MainActivity.this.isFinishing()) {
								return;
							}
						}
						if (mInstallingDialog != null
								&& mInstallingDialog.isShowing()) {
							mInstallingDialog.dismiss();
						}
					}
				}
			};
			timer.schedule(task, 1000, 1000);
		}

		// / Fitness
		FitnessHelper.getInstance().initFitnessConnection(this);
	}
	// add by lixiang for user fota 20150820 begin
	protected void getFileFromServer() {
		// TODO Auto-generated method stub
	    
	    File f_dir = new File(dirName);
	    if(!f_dir.exists())
	    {
	    	f_dir.mkdir();
	    }
	    
        HttpResponse ressponse;
		try {
			StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
				         StrictMode.setThreadPolicy(policy);
			HttpClient client = new DefaultHttpClient();
		    HttpGet request = new HttpGet(urlVersion);
			ressponse = client.execute(request);
//			Log.e(TAG,"response"+ressponse.getStatusLine().getStatusCode());
			if(ressponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				
				String version = new JSONObject(EntityUtils.toString(ressponse.getEntity())).getString("version");
				File[] subFile = f_dir.listFiles();
				for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
		            if (!subFile[iFileLength].isDirectory()) {
		                String filename = subFile[iFileLength].getName();     

		                	 if(filename.equals(version)){
				                	return ;
				                }else{
				                	subFile[iFileLength].delete();
				                	new ReadHttpGet().execute(urlDownload,dirName+version,mHandler_download);
				                	return ;
				                }
		                }
		            }
		        
				Log.e(TAG,"ReadHttpGet version= "+version);
				new ReadHttpGet().execute(urlDownload,dirName+version,mHandler_download);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        

	}
	// add by lixiang for user fota 20150820 end

	@Override
	protected void onResume() {
		Log.d(TAG, "[wearable][MainActivity] onResume begin");
		// HeartRateClientProxy.getInstance().registerHRListener(new
		// HRListener(){
		// public void onHRNotify(int bpm){
		// Log.e("lixiang","HeartRate= "+bpm);
		// }
		// });
		// add by lixiang for HeartRate 20150605
		super.onResume();
		if (mPxpAlertPrefernece != null) {
			mPxpAlertPrefernece.updatePreference();
		}
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "[wearable][MainActivity] onDestroy begin");
		WearableManager.getInstance().unregisterWearableListener(
				mWearableListener);
		// HeartRateClientProxy.getInstance().unregisterHReListener();//add by
		// lixiang for HeartRate 20150605
		if (mFindMePreference != null) {
			mFindMePreference.releaseListeners();
		}
		if (mAppInfoPreference != null) {
			mAppInfoPreference.releaseListeners();
		}
		FotaOperator.getInstance(this).unregisterFotaCallback(mFotaCallback);

		if (Build.VERSION.SDK_INT >= 17) {
			if (!this.isDestroyed() && !this.isFinishing()) {
				if (mInstallingDialog != null && mInstallingDialog.isShowing()) {
					mInstallingDialog.dismiss();
				}
			}
		} else {
			if (!this.isFinishing()) {
				if (mInstallingDialog != null && mInstallingDialog.isShowing()) {
					mInstallingDialog.dismiss();
				}
			}
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// if (!BTD) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.main, menu);
		// }
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (mIsSupportRedBendFota) {
			menu.add(0, MENU_ITEM_REDBEND_FOTA, 0,
					R.string.fimrware_update_redbend_fota);
		}
		if (mIsSupportSeparateBinFota) {
			menu.add(0, MENU_ITEM_SEPARATE_BIN_FOTA, 0,
					R.string.firmware_update_separate_bin);
		}
		if (mIsSupportUsbCableFota) {
			menu.add(0, MENU_ITEM_USB_CABLE_FOTA, 0,
					R.string.firmware_update_usb_otg);
		}
		if (mIsSupportFBinFota) {
			menu.add(0, MENU_ITEM_FULL_BIN_FOTA, 0,
					R.string.firmware_update_full_bin);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		Intent intent = null;
		switch (item.getItemId()) {
		case MENU_ITEM_REDBEND_FOTA:
			if (!WearableManager.getInstance().isAvailable()) {
				mToast.show();
				return true;
			}
			intent = new Intent(MainActivity.this, SmartDeviceFirmware.class);
			intent.putExtra(FotaUtils.INTENT_EXTRA_INFO,
					FotaUtils.FIRMWARE_REDBEND_FOTA);
			startActivity(intent);
			return true;

		case MENU_ITEM_SEPARATE_BIN_FOTA:
			// here should to check the BT is connected or not
			if (!WearableManager.getInstance().isAvailable()) {
				mToast.show();
				return true;
			}
			intent = new Intent(MainActivity.this, SmartDeviceFirmware.class);
			intent.putExtra(FotaUtils.INTENT_EXTRA_INFO,
					FotaUtils.FIRMWARE_UBIN);
			startActivity(intent);
			return true;

		case MENU_ITEM_USB_CABLE_FOTA:
			if (!WearableManager.getInstance().isAvailable()) {
				if (readUpdateSuccessState()) {
					mToast.show();
					Log.d("[FOTA_UPDATE][MainActivity]",
							"[onOptionItemSelected] "
									+ "last fota update is succeed, and BT is not connected, just return");
					return true;
				} else {
					intent = new Intent(MainActivity.this,
							UpdateFirmwareActivity.class);
					intent.putExtra(FotaUtils.INTENT_EXTRA_INFO,
							FotaUtils.FIRMWARE_VIA_USB);
					startActivity(intent);
					return true;
				}
			} else {
				intent = new Intent(MainActivity.this,
						SmartDeviceFirmware.class);
				intent.putExtra(FotaUtils.INTENT_EXTRA_INFO,
						FotaUtils.FIRMWARE_VIA_USB);
				startActivity(intent);
				return true;
			}

		case MENU_ITEM_FULL_BIN_FOTA:

			Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
			intent1.setType("*/*");// ("application/octet-stream");
			intent1.addCategory(Intent.CATEGORY_OPENABLE);
			this.startActivityForResult(intent1, 200);
			// modified by lixang for full bin 20150820
			// File[] files =new File(FOTA_PATH).listFiles();
			// if(files.length > 0){
			// Log.e(TAG,files[0].getPath());
			// }
			// Intent intent_fota = new Intent(MainActivity.this,
			// UpdateFirmwareActivity.class);
			// intent_fota.putExtra(FotaUtils.INTENT_EXTRA_INFO,
			// FotaUtils.FIRMWARE_FULL_BIN);
			// String path =
			// Environment.getExternalStorageDirectory().getPath()+"/FOTA";
			// if (path == null) {
			// // intent.setData(uri);
			// // Log.e("[FOTA_UPDATE][MainActivity]",
			// "[onActivityResult] set uri : " + uri);
			// } else {
			// intent_fota.putExtra(FotaUtils.ZIP_FILE_PATH, path);
			// }
			//
			// startActivity(intent_fota);

			break;

		case R.id.menu_item_smart_device_manager:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("[FOTA_UPDATE][MainActivity]",
				"[onActivityResult] requestCode : " + requestCode
						+ ", resultCode : " + resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == 200) {
				Uri uri = data.getData();
				Log.d("[FOTA_UPDATE][MainActivity]",
						"[onActivityResult] uri : " + uri);
				String path = this.getFilePath(uri);
				Log.d("[FOTA_UPDATE][MainActivity]",
						"[onActivityResult] path : " + path);
				Log.d("[FOTA_UPDATE][MainActivity]",
						"[onActivityResult] path01 : "
								+ Environment.getExternalStorageDirectory());

				Intent intent = new Intent(MainActivity.this,
						UpdateFirmwareActivity.class);
				intent.putExtra(FotaUtils.INTENT_EXTRA_INFO,
						FotaUtils.FIRMWARE_FULL_BIN);
				path = Environment.getExternalStorageDirectory().getPath()
						+ "/image.bin";
				if (path == null) {
					intent.setData(uri);
					Log.e("[FOTA_UPDATE][MainActivity]",
							"[onActivityResult] set uri : " + uri);
				} else {
					intent.putExtra(FotaUtils.ZIP_FILE_PATH, path);
				}

				startActivity(intent);
			}
		}
		if (requestCode == FitnessHelper.getInstance().REQUEST_OAUTH) {
			FitnessHelper.getInstance().authInProgress = false;
			if (resultCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to
				// connect
				FitnessHelper.getInstance().connect();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(FitnessHelper.AUTH_PENDING,
				FitnessHelper.getInstance().authInProgress);
	}

	private String getFilePath(Uri uri) {
		if (uri == null) {
			Log.e("[FOTA_UPDATE][MainActivity]", "[getFilePath] uri is null");
			return null;
		}
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { MediaColumns.DATA };
			Cursor cursor = null;
			try {
				cursor = this.getContentResolver().query(uri, projection, null,
						null, null);
				if (cursor == null) {
					Log.e("[FOTA_UPDATE][MainActivity]",
							"[getFilePath] cursor is null");
					return null;
				}

				int column = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
				Log.d("[FOTA_UPDATE][MainActivity]", "[getFilePath] column : "
						+ column);
				String path = null;
				if (cursor.moveToFirst()) {
					path = cursor.getString(column);
					Log.d("[FOTA_UPDATE][MainActivity]",
							"[getFilePath] string : " + path);
				}
				return path;
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	private boolean readUpdateSuccessState() {
		SharedPreferences sp = getSharedPreferences(
				FotaUtils.FOTA_UPDATE_PREFERENCE_FILE_NAME,
				Context.MODE_PRIVATE);
		boolean b = sp.getBoolean(FotaUtils.FOTA_UPDATE_STATUS_FLAG_STRING,
				true);
		android.util.Log.d("[FOTA_UPDATE][MainActivity]",
				"[readUpdateSuccessState] b : " + b);
		return b;
	}

	private void showNotifiListnerPrompt() {
		Builder builder = new Builder(this);
		builder.setTitle(R.string.notificationlistener_prompt_title);
		builder.setMessage(R.string.notificationlistener_prompt_content);

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		// Go to notification listener settings
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(NOTIFICATION_LISTENER_INTENT);
					}
				});
		builder.create().show();
	}

	private void showAccessibilityPrompt() {
		Builder builder = new Builder(this);
		builder.setTitle(R.string.accessibility_prompt_title);
		builder.setMessage(R.string.accessibility_prompt_content);

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		// Go to accessibility settings
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(ACCESSIBILITY_INTENT);
					}
				});
		builder.create().show();
	}

	// / M: auto connect RemoteDevice by Hotknot @{
	public static final String HOTKNOT_EXTRA = "com.mediatek.hotknot.extra.DATA";

	private static final String HOTKNOT_TAG = "[HotKnot]";

	public void handleIntent(Intent intent) {
		Log.d(HOTKNOT_TAG, "[handleIntent] begin");
		if (intent != null && intent.hasExtra(HOTKNOT_EXTRA)) {
			byte[] extraAddress = intent.getByteArrayExtra(HOTKNOT_EXTRA);
			String targetAddress = convertToAddress(extraAddress);
			Log.d(HOTKNOT_TAG, "[onStartCommand] TARGET_ADDRESS = "
					+ targetAddress);
			autoConnectDevice(targetAddress);
		}
		return;
	}

	private void autoConnectDevice(final String address) {
		Log.d(HOTKNOT_TAG, "[autoConnectDevice] begin");
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!BluetoothAdapter.checkBluetoothAddress(address)) {
			Log.d(HOTKNOT_TAG, "[autoConnectDevice] invalid BT address");
			return;
		}

		Boolean isBTOn = btAdapter.isEnabled();
		if (!isBTOn) {
			Log.d(HOTKNOT_TAG, "[autoConnectDevice] BT is off");
			Toast.makeText(getApplicationContext(), R.string.pls_switch_bt_on,
					Toast.LENGTH_LONG).show();
			return;
		} else {
			if (WearableManager.getInstance().isConnecting()
					|| WearableManager.getInstance().isAvailable()) {
				BluetoothDevice device = WearableManager.getInstance()
						.getRemoteDevice();
				String connectName = device.getName();
				Log.d(HOTKNOT_TAG, "[autoConnectDevice] Connected -> "
						+ connectName);
				return;
			}
			BluetoothDevice device = btAdapter.getRemoteDevice(address);
			WearableManager.getInstance().setRemoteDevice(device);
			Log.d(HOTKNOT_TAG, "[autoConnectDevice] connectToRemoteDevice");
			WearableManager.getInstance().connect();
		}
	}

	private String convertToAddress(byte[] extraAddress) {
		// extraAddress = new byte[] {(byte)0x7E, (byte)0x42, (byte)0xF0,
		// (byte)0x30, (byte)0x62, (byte)0x61};
		if (extraAddress == null) {
			return "";
		}
		String targetAddress = "";
		for (int i = 0; i < extraAddress.length; i++) {
			String s = String.format("%X", extraAddress[i]);
			if (s.length() == 1) {
				s = "0" + s;
			}
			targetAddress += s;
			if (i != extraAddress.length - 1) {
				targetAddress += ":";
			}
		}
		return targetAddress;
	}

	// / @}

	public boolean isNotificationListenerActived() {
		String strListener = Secure.getString(this.getContentResolver(),
				"enabled_notification_listeners");
		return strListener != null
				&& strListener
						.contains("com.mtk.btnotification/com.mtk.app.notification.NotificationReceiver18");
	}

	// / show DeviceInfo Dialog
	private void showDeviceInfoDialog(DeviceInfo deviceInfo) {
		Log.d(TAG, "showDeviceInfoDialog begin");
		if (deviceInfo == null) {
			Log.d(TAG, "showDeviceInfoDialog return");
			return;
		}
		Builder builder = new Builder(this);
		builder.setTitle(R.string.device_info);
		builder.setMessage(deviceInfo.displayString());
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		mCurDialog = dialog;
	}

	// / change name Dialog
	private class DeviceNameListenerImpl implements DeviceNameListener {

		@Override
		public void notifyDeviceName(final String name) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (!TextUtils.isEmpty(name)) {
						Log.d(TAG, "notifyDeviceName name = " + name);
						mAppInfoPreference.setDeviceName(name);
						mAppInfoPreference.saveDeviceName(name);
						Intent intent = new Intent();
						intent.setAction(PxpStatusChangeReceiver.DEVICE_NAME_CHANGED_ACTION);
						sendBroadcast(intent);
					}
				}
			});
		}
	}

	private void showChangeNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		LayoutInflater factory = LayoutInflater.from(builder.getContext());
		final View dialogView = factory.inflate(R.layout.change_name_dialog,
				null);
		final EditText contentSelector = (EditText) dialogView
				.findViewById(R.id.content_selector);
		final String curName = mAppInfoPreference.getCurrentName();
		contentSelector.setText(curName);
		contentSelector.setSelection(curName != null ? curName.length() : 0);
		builder.setTitle(R.string.change_name);
		builder.setView(dialogView);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						DeviceNameListenerImpl listener = new DeviceNameListenerImpl();
						String name = contentSelector.getText().toString();
						if (name != null && name.equals(curName)) {
							Log.d(TAG, "showChangeNameDialog same name");
							return;
						}
						WearableManager.getInstance().modifyDeviceName(name,
								listener);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		mCurDialog = dialog;
	}

	private void configureMusicControl() {
		PackageManager localPackageManager = getPackageManager();
		List localList = localPackageManager.queryBroadcastReceivers(
				new Intent("android.intent.action.MEDIA_BUTTON"), 96);
		CharSequence[] arrayOfCharSequence1 = new CharSequence[1 + localList
				.size()];
		CharSequence[] arrayOfCharSequence2 = new CharSequence[1 + localList
				.size()];
		arrayOfCharSequence1[0] = "";
		arrayOfCharSequence2[0] = getString(R.string.none);
		for (int i = 0; i < localList.size(); i++) {
			ResolveInfo localResolveInfo = (ResolveInfo) localList.get(i);
			arrayOfCharSequence1[(i + 1)] = localResolveInfo.activityInfo.packageName;
			arrayOfCharSequence2[(i + 1)] = localResolveInfo.activityInfo.applicationInfo
					.loadLabel(localPackageManager).toString();
		}
		mMusicPreference.setEntryValues(arrayOfCharSequence1);
		mMusicPreference.setEntries(arrayOfCharSequence2);
	}

	private long mLastClickTime = 0L;

	private boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long slotT = 0;
		slotT = time - mLastClickTime;
		mLastClickTime = time;
		if (0 < slotT && slotT < 800) {
			return true;
		}
		return false;
	}

	private AlertDialog mCurDialog;

	private void sendFotaCheckCommand() {
		FotaOperator.getInstance(this).sendFotaTypeCheckCommand();
	}

	private IFotaOperatorCallback mFotaCallback = new IFotaOperatorCallback() {

		@Override
		public void onFotaTypeReceived(int fotaType) {
			Log.d(FOTA_TAG, "[onFotaTypeReceived] fotaType : " + fotaType);
			if (fotaType == 0) {
				Toast.makeText(MainActivity.this,
						R.string.get_fota_type_failed, Toast.LENGTH_SHORT)
						.show();
				return;
			}

			mIsSupportRedBendFota = false;
			mIsSupportSeparateBinFota = false;
			mIsSupportUsbCableFota = false;
			mIsSupportFBinFota = false;
			mIsSupportRockFota = false;

			if ((fotaType & FotaUtils.FOTA_TYPE_DIFF_FOTA) == FotaUtils.FOTA_TYPE_DIFF_FOTA) {
				mIsSupportRedBendFota = true;
			}
			if ((fotaType & FotaUtils.FOTA_TYPE_SEPERATE_BIN_FOTA) == FotaUtils.FOTA_TYPE_SEPERATE_BIN_FOTA) {
				mIsSupportSeparateBinFota = true;
			}
			if ((fotaType & FotaUtils.FOTA_TYPE_USB_FOTA) == FotaUtils.FOTA_TYPE_USB_FOTA) {
				mIsSupportUsbCableFota = true;
			}
			if ((fotaType & FotaUtils.FOTA_TYPE_FULL_BIN_FOTA) == FotaUtils.FOTA_TYPE_FULL_BIN_FOTA) {
				mIsSupportFBinFota = true;
			}
			if ((fotaType & FotaUtils.FOTA_TYPE_ROCK_UPDATE) == FotaUtils.FOTA_TYPE_ROCK_UPDATE) {
				mIsSupportRockFota = true;
			}
			Log.d(FOTA_TAG, "[onFotaTypeReceived] mIsSupportRedBendFota : "
					+ mIsSupportRedBendFota + ", mIsSupportSeparateBinFota : "
					+ mIsSupportSeparateBinFota + ", mIsSupportUsbCableFota : "
					+ mIsSupportUsbCableFota + ", mIsSupportFBinFota : "
					+ mIsSupportFBinFota + ", mIsSupportRockFota : "
					+ mIsSupportRockFota);
			MainActivity.this.invalidateOptionsMenu();

		}

		@Override
		public void onCustomerInfoReceived(String information) {

		}

		@Override
		public void onFotaVersionReceived(FotaVersion version) {

		}

		@Override
		public void onStatusReceived(int status) {

		}

		@Override
		public void onConnectionStateChange(int newConnectionState) {
			if (newConnectionState == WearableManager.STATE_CONNECT_LOST) {
				mIsSupportRedBendFota = false;
				mIsSupportSeparateBinFota = false;
				mIsSupportUsbCableFota = false;
				mIsSupportFBinFota = false;
				mIsSupportRockFota = false;
			}
		}

		@Override
		public void onProgress(int progress) {

		}

	};

	// / write Fitness data
	private void writeFitnessData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (LeProfileUtils.isFitnessAvailable()) {
						String fileRoot = "";
						if (Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED)) {
							fileRoot = Environment
									.getExternalStorageDirectory() + "/FitLog";
						} else {
							fileRoot = Environment.getRootDirectory()
									+ "/FitLog";
						}

						File dir = new File(fileRoot);
						Log.d(TAG, "[writeFitnessData] = " + dir);
						if (!dir.exists()) {
							dir.mkdir();
						}
						String rootPath = dir.getAbsolutePath();
						long time = System.currentTimeMillis();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"MM-dd_HH-mm-ss");
						String name = dateFormat.format(time) + ".log";
						String fileName = rootPath + "/" + name;
						Log.d(TAG, "[writeFitnessData] fileName = " + fileName);
						File file = new File(fileName);

						file.createNewFile();
						FileOutputStream fileStream = new FileOutputStream(
								file, false);
						FitnessHelper.getInstance().writeStepCount(fileStream);
						Thread.sleep(2000);
						FitnessHelper.getInstance().writeHeartRate(fileStream);
					}
				} catch (FileNotFoundException e) {
					Log.d(TAG, "[writeFitnessData] FileNotFoundException " + e);
				} catch (IOException e) {
					Log.d(TAG, "[writeFitnessData] IOException " + e);
				} catch (Exception e) {
					Log.d(TAG, "[writeFitnessData] Exception " + e);
				} catch (Error e) {
					Log.d(TAG, "[writeFitnessData] Error " + e);
				}
			}
		}).start();

	}
}
