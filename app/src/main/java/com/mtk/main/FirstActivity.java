package com.mtk.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gomtel.database.DatabaseProvider;
import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.SportInfo;
import com.gomtel.util.UpdateManager;
import com.gomtel.util.WeatherInfo;

import com.mediatek.wearable.WearableManager;
import com.mtk.bluetoothle.AlertSettingPreference;
import com.mtk.bluetoothle.CustomizedBleClient;
import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

import care.LocationActivity;
import care.WelcomeActivity;
import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdManager;
import cn.domob.android.ads.AdView;

public class FirstActivity extends Activity implements HttpUtils.HttpCallback {

    private int mob_ad;
    private AdView mAdview;
    private FrameLayout mAdContainer;
    private ImageView delete;

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // locationManager.removeUpdates(locationListener);
    }

    // private ImageView main_set;
    private final static String mFormat = "yyyy-MM-dd  EEEE";// h:mm:ss aa
    private static final int HTTP_REQUEST_SESULT_RECORDER = 3;
    private static final int HTTP_REQUEST_SESULT_SPORTINFO = 7;
    private static final String TAG = "FirstActivity";
    private static final String UNIT_TEMP = "℃";
    public static final String RSP_WEATHER = "rsp_weather";
    public static final String RSP_TEMP = "rsp_temp";
    public static final String RSP_CITY = "rsp_city";
    private FormatChangeObserver mFormatChangeObserver;
    private HttpUtils httpUtil;
    private TextView city;
    private TextView weather;
    private TextView temperature;
    private TextView date;
    // private ImageView weather;
    private RelativeLayout layout_uv;
    private RelativeLayout personal_information;
    private RelativeLayout layout_sports;
    private RelativeLayout layout_hr;
    private RelativeLayout layout_sleep;
    private RelativeLayout layout_friends;
    private RelativeLayout layout_binding;
    private RelativeLayout layout_radar;
    private RelativeLayout layout_setting;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RSP_WEATHER)) {
                if (!weatherOfDay.equals(""))
                    sendCity();
                Log.e(TAG, "send complete1");
            }
            if (action.equals(RSP_TEMP)) {
                sendWeather();
                Log.e(TAG, "send complete2");
            }
            if (action.equals(RSP_CITY)) {
                Log.e(TAG, "send complete3");
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "msg.obj= " + msg.obj);
                    updateWeather(msg.obj);
                    break;
                case 2:
                    int version = Integer.valueOf((String) msg.obj);
                    int local_version = getLocalVersion();
                    if (version > local_version) {
                        mUpdateManager.showNoticeDialog();
                    }
                    break;
            }
        }

    };

    private int getLocalVersion() {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = info.versionCode;
        return version;
    }

//    private void getSoftware() {
////        httpUtil.getDownloadSoftware(this, String.valueOf(longitude), String.valueOf(latitude), this);
//    }

    private OnClickListener myListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.layout_setting:
                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                    break;
                case R.id.layout_sports:
                    startActivity(new Intent(FirstActivity.this,
                            SportActivity.class));
                    break;
//                case R.id.layout_uv:
//                    startActivity(new Intent(FirstActivity.this, UVActivity.class));
//                    break;
                case R.id.layout_hr:
                    startActivity(new Intent(FirstActivity.this, HRActivity.class));
                    break;
                case R.id.personal_information:
                    startActivity(new Intent(FirstActivity.this, DeviceInfoActivity.class));
//                    startActivity(new Intent(FirstActivity.this, PIActivity.class));
                    break;
                case R.id.layout_radar:
                    startActivity(new Intent(FirstActivity.this,
                            AlertSettingPreference.class));
                    break;
                case R.id.layout_sleep:
                    startActivity(new Intent(FirstActivity.this,
                            SleepDetailInfo.class));
                    break;
                case R.id.layout_location:
//				startActivity(new Intent(FirstActivity.this, MainActivity.class));
                    startActivity(new Intent(FirstActivity.this, LocationActivity.class));
                    break;
                default:
                    break;
            }
        }

    };
    private Calendar mCalendar;
    private LocationManager locationManager;
    private double latitude;
    private double longitude;
    private String provider;
    private SharedPreferences mPrefs;
    private int userId;
    private String cityName;
    private int lowTemp;
    private int highTemp;
    private String weatherOfDay;
    private HttpUtils mHttpUtils;
    private HashMap<String, String> map;
    private UpdateManager mUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RSP_WEATHER);
        filter.addAction(RSP_TEMP);
        filter.addAction(RSP_CITY);
        registerReceiver(mReceiver, filter);
        mob_ad = getIntent().getIntExtra(Global.MOB_AD, 0);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (httpUtil == null) {
            httpUtil = HttpUtils.getInstance();
        }
        if (mPrefs == null)
            mPrefs = getSharedPreferences("WATCH", 0);
        initUI();
        if (httpUtil != null) {
            httpUtil.getSoftwareVersion(this, "0", "13412345678", String.valueOf(longitude), String.valueOf(latitude), this);
        }
        setWeather();
        mUpdateManager = new UpdateManager(this,String.valueOf(longitude), String.valueOf(latitude));
    }

    protected void sendWeather() {
        // TODO Auto-generated method stub
        if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED) {
            byte[] arrayOfByte = new byte[20];
            // string2unicode(weatherOfDay);
            arrayOfByte[0] = 0x31;
            arrayOfByte[1] = (byte) (weatherOfDay.length() * 2);
            Log.e(TAG, "Weather= " + weatherOfDay);
            if (weatherOfDay.length() <= 0)
                return;
            for (int i = 0; i < weatherOfDay.length(); i++) {
                arrayOfByte[2 * i + 2] = (byte) weatherOfDay.codePointAt(i);
                arrayOfByte[2 * i + 3] = (byte) (weatherOfDay.codePointAt(i) >> 8);
                Log.e(TAG, "Weather= " + arrayOfByte[2 * i + 2] + arrayOfByte[2 * i + 3]);
            }
            MainService.getInstance()
                    .writeCharacteristic(CustomizedBleClient.getGatt(),
                            MainService.UUID_SERVICE,
                            MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
                            arrayOfByte);
        }
    }

    protected void sendCity() {
        // TODO Auto-generated method stub
        if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED) {
            byte[] arrayOfByte = new byte[20];
            arrayOfByte[0] = 0x32;
            arrayOfByte[1] = (byte) (cityName.length() * 2);
//            Log.e(TAG,"cityName= "+arrayOfByte[1]);
            if (cityName.length() <= 0)
                return;
            for (int i = 0; i < cityName.length(); i++) {
                arrayOfByte[2 * i + 2] = (byte) cityName.codePointAt(i);
                arrayOfByte[2 * i + 3] = (byte) (cityName.codePointAt(i) >> 8);
                Log.e(TAG, "cityName= " + arrayOfByte[2 * i + 2] + arrayOfByte[2 * i + 3]);
            }
            MainService.getInstance()
                    .writeCharacteristic(CustomizedBleClient.getGatt(),
                            MainService.UUID_SERVICE,
                            MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
                            arrayOfByte);
        }
    }


    public static byte[] string2unicode(String s) {
        int size = s.length();
        byte[] unicode = new byte[size];
        for (int i = 0; i < size; i++) {
            unicode[i] = (byte) s.codePointAt(i);
            Log.e(TAG, "lixiang---st= " + unicode[i]);
        }
        return unicode;
    }

    protected void updateWeather(Object obj) {
        // TODO Auto-generated method stub
        ArrayList<WeatherInfo> weatherList = (ArrayList<WeatherInfo>) obj;
        WeatherInfo weatherInfo = weatherList.get(0);
        if (weatherInfo != null) {
            cityName = weatherInfo.cityname;
            lowTemp = weatherInfo.lowTemperature;
            highTemp = weatherInfo.highTemperature;
            weatherOfDay = weatherInfo.weather;
            city.setText(cityName);
            // string2unicode(weatherOfDay);
            if (highTemp == 999) {
                temperature.setText(String.valueOf(lowTemp) + UNIT_TEMP);
            } else {
                temperature.setText(String.valueOf(lowTemp) + UNIT_TEMP + "~"
                        + String.valueOf(highTemp) + UNIT_TEMP);
            }
            weather.setText(weatherOfDay);
            temperature.setVisibility(View.VISIBLE);
            city.setVisibility(View.VISIBLE);
            weather.setVisibility(View.VISIBLE);
            sendTemp(highTemp, lowTemp);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    private void initUI() {
        // TODO Auto-generated method stub
        // weather = (ImageView) findViewById(R.id.weather);
        city = (TextView) findViewById(R.id.city);
        weather = (TextView) findViewById(R.id.weather);
        temperature = (TextView) findViewById(R.id.temperature);
//        layout_uv = (RelativeLayout) findViewById(R.id.layout_uv);
        personal_information = (RelativeLayout) findViewById(R.id.personal_information);
        layout_sports = (RelativeLayout) findViewById(R.id.layout_sports);
        layout_hr = (RelativeLayout) findViewById(R.id.layout_hr);
        layout_sleep = (RelativeLayout) findViewById(R.id.layout_sleep);
        layout_binding = (RelativeLayout) findViewById(R.id.layout_location);
        layout_radar = (RelativeLayout) findViewById(R.id.layout_radar);
        layout_setting = (RelativeLayout) findViewById(R.id.layout_setting);
        date = (TextView) findViewById(R.id.date);
        layout_setting.setOnClickListener(myListener);
        layout_sports.setOnClickListener(myListener);
//        layout_uv.setOnClickListener(myListener);
        layout_hr.setOnClickListener(myListener);
        personal_information.setOnClickListener(myListener);
        layout_radar.setOnClickListener(myListener);
        layout_sleep.setOnClickListener(myListener);
        layout_binding.setOnClickListener(myListener);
        setDate();

        userId = mPrefs.getInt("LOG_USERID", 0);
        if (mob_ad == 1) {
            startBanner(this);
        }
    }

    private void startBanner(Context conttext) {
        mAdContainer = (FrameLayout) findViewById(R.id.adcontainer);
        delete = (ImageView) findViewById(R.id.delete);
        mAdview = new AdView(this, Global.PUBLISHER_ID, Global.BANNER_ID);
        mAdview.setKeyword("game");
        mAdview.setUserGender("female");
        mAdview.setUserBirthdayStr("2000-08-08");
        mAdview.setUserPostcode("123456");
        mAdview.setAdEventListener(new AdEventListener() {
            @Override
            public void onAdOverlayPresented(AdView adView) {
                Log.i("DomobSDKDemo", "overlayPresented");
            }

            @Override
            public void onAdOverlayDismissed(AdView adView) {
                Log.i("DomobSDKDemo", "Overrided be dismissed");
            }

            @Override
            public void onAdClicked(AdView arg0) {
                Log.i("DomobSDKDemo", "onDomobAdClicked");
            }

            @Override
            public void onLeaveApplication(AdView arg0) {
                Log.i("DomobSDKDemo", "onDomobLeaveApplication");
            }

            @Override
            public Context onAdRequiresCurrentContext() {
                return FirstActivity.this;
            }

            @Override
            public void onAdFailed(AdView arg0, AdManager.ErrorCode arg1) {
                Log.i("DomobSDKDemo", "onDomobAdFailed");
            }

            @Override
            public void onEventAdReturned(AdView arg0) {
                Log.i("DomobSDKDemo", "onDomobAdReturned");
            }
        });
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mAdview.setLayoutParams(layout);
//        ImageView delete = new ImageView(this);
//        delete.setImageResource(R.drawable.delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdContainer.setVisibility(View.GONE);
            }
        });
//        LinearLayout.LayoutParams layout_delete = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        layout_delete.gravity = Gravity.RIGHT|Gravity.TOP;
//        delete.setLayoutParams(layout_delete);
        mAdContainer.addView(mAdview, 0);
//        mAdContainer.addView(delete);
    }

    private void setWeather() {
        // TODO Auto-generated method stub
        // if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        // {
        // // Intent intent = new
        // Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // // startActivityForResult(intent, 0);
        // // return;
        // }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                httpUtil.loadWeather(this, userId, longitude, latitude, this);
            } else {
                LocationListener locationListener = new LocationListener() {

                    @Override
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            Log.e("Map",
                                    "Location changed : Lat: "
                                            + location.getLatitude() + " Lng: "
                                            + location.getLongitude());
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if (location.getLongitude() != 0.0
                                    && location.getLatitude() != 0.0) {
                                httpUtil.loadWeather(FirstActivity.this,
                                        userId, location.getLongitude(),
                                        location.getLatitude(),
                                        FirstActivity.this);
                            }
                        }
                    }
                };
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000000, 100,
                        locationListener);
                Location location1 = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location1 != null) {
                    latitude = location1.getLatitude();
                    longitude = location1.getLongitude();
                }
            }
        }
        Log.e(TAG, "latitude= " + latitude + "   longitude = " + longitude);

        // sendWeatherInfo();
        // weather.setText(String.valueOf(latitude));

    }

    private void sendTemp(int highTemp, int lowTemp) {
        // TODO Auto-generated method stub
        if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED) {
            byte[] arrayOfByte = new byte[3];
            arrayOfByte[0] = 0x30;
            arrayOfByte[1] = (byte) lowTemp;
            arrayOfByte[2] = (byte) highTemp;
            Log.e(TAG, "highTemp= " + arrayOfByte[2]);
            MainService.getInstance()
                    .writeCharacteristic(CustomizedBleClient.getGatt(),
                            MainService.UUID_SERVICE,
                            MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
                            arrayOfByte);
        }
    }

    private String getProvider() {
        // TODO Auto-generated method stub
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);
        return provider;
    }

    private void setDate() {
        // TODO Auto-generated method stub
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        date.setText(DateFormat.format(mFormat, mCalendar));
        mFormatChangeObserver = new FormatChangeObserver();
        getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    private class FormatChangeObserver extends ContentObserver {

        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {

        }
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        // TODO Auto-generated method stub
        switch (what) {
            case HttpUtils.HTTP_REQUEST_SESULT_WEATHER:
                // WeatherInfo weather = (WeatherInfo) object;
                // Log.e(TAG, "sp= " + weather.cityname);
                Log.e(TAG, "result_weather= " + result);
                if (result == 1) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = object;
                    mHandler.sendMessage(msg);
                }
                break;

            case HttpUtils.HTTP_REQUEST_SESULT_HEARTBEAT:
                Log.e(TAG, "HTTP_REQUEST_SESULT_HEARTBEAT= " + (String) object);

                break;

            case HttpUtils.HTTP_REQUEST_DOWNLOAD_VERSION:
                Message msg_version = new Message();
                msg_version.what = 2;
                msg_version.obj = object;
                mHandler.sendMessage(msg_version);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int what) {
        // TODO Auto-generated method stub

    }

}
