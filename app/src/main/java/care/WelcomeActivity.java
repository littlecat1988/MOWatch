package care;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gomtel.database.DatabaseProvider;
import com.gomtel.util.AdInfo;
import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.UpdateManager;
import com.gomtel.util.WindowAdUtils;
import com.mtk.btnotification.R;
import com.mtk.main.FirstActivity;
import com.mtk.main.MainService;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import care.application.XcmApplication;
import care.bean.BaoBeiBean;
import care.db.ProtocolData;
import care.db.manager.UpdateDB;
import care.deviceinfo.model.DeviceInfo;
import care.userinfo.model.UserInfo;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.XcmTools;
import cn.domob.android.ads.RTSplashAd;
import cn.domob.android.ads.RTSplashAdListener;
import cn.domob.android.ads.SplashAd;
import cn.domob.android.ads.SplashAdListener;

public class WelcomeActivity extends Activity implements HttpUtils.HttpCallback {

    /**
     * Splash screen duration time in milliseconds
     */
    //她让我可以走在深圳地铁换乘站的人群里，不因为渺小和平凡而心慌
    private static final int delayMillis = 1000;

    private XcmApplication mInstance = null;
    private UpdateDB mUpdateDB = null;
    private ProtocolData mProtocolData;
    private static XcmTools tools = null;
    private SparseArray<BaoBeiBean> mDataList = new SparseArray<BaoBeiBean>();
    private Context context;
    private HttpUtils httpUtil;
    private LinearLayout indicators;
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showMobAd();
                    break;
                case 1:
                    if (isShown)
                        initAdView();
                    break;
                case 2:
                    getDownloadFile(msg.obj);
                    break;
                case 3:
                    jump();
                    break;
                default:
                    break;
            }
        }

    };


    private void getDownloadFile(Object obj) {
        String version = (String) obj;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("version", version);
        map.put("url", Global.URL_DOWNLOAD_SOFTWARE);
        map.put("name", "WATCH");
//        mUpdateManager.checkUpdate(map);
    }

    private boolean isSplash = false;
    private SplashAd splashAd;
    private RTSplashAd rtSplashAd;
    private RelativeLayout splash_holder;
    private double latitude;
    private double longitude;
    private boolean isShown = true;
    private UpdateManager mUpdateManager;

    private void showMobAd() {
        if (isShown) {
            if (isSplash) {
//			 缓存开屏广告
//			Cache splash ad
                splashAd = new SplashAd(this, Global.PUBLISHER_ID, Global.SPLASH_PPID,
                        SplashAd.SplashMode.SplashModeFullScreen);
//		    setSplashTopMargin is available when you choose non-full-screen splash mode.
//			splashAd.setSplashTopMargin(200);
                splashAd.setSplashAdListener(new SplashAdListener() {
                    @Override
                    public void onSplashPresent() {
                        Log.i("DomobSDKDemo", "onSplashStart");
                    }

                    @Override
                    public void onSplashDismiss() {
                        Log.i("DomobSDKDemo", "onSplashClosed");
//					 开屏回调被关闭时，立即进行界面跳转，从开屏界面到主界面。
//				    When splash ad is closed, jump to the next(main) Activity immediately.
                        jump();
//					如果应用没有单独的闪屏Activity，需要调用closeSplash方法去关闭开屏广告
//					If you do not carry a separate advertising activity, you need to call closeRTSplash way to close the splash ad
//					splashAd.closeSplash();
                    }

                    @Override
                    public void onSplashLoadFailed() {
                        Log.i("DomobSDKDemo", "onSplashLoadFailed");
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (splashAd.isSplashAdReady()) {
                            splashAd.splash(WelcomeActivity.this, WelcomeActivity.this.findViewById(R.id.splash_holder));
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Splash ad is NOT ready.", Toast.LENGTH_SHORT).show();
                            jump();
                        }
                    }
                }, 1);
            } else {
//			 实时开屏广告
//			Real-time splash ad
                rtSplashAd = new RTSplashAd(this, Global.PUBLISHER_ID, Global.SPLASH_PPID,
                        SplashAd.SplashMode.SplashModeFullScreen);
//		    setRTSplashTopMargin is available when you choose non-full-screen splash mode.
//			rtSplashAd.setRTSplashTopMargin(200);
                rtSplashAd.setRTSplashAdListener(new RTSplashAdListener() {
                    @Override
                    public void onRTSplashDismiss() {
                        Log.i("DomobSDKDemo", "onRTSplashClosed");
//					 开屏回调被关闭时，立即进行界面跳转，从开屏界面到主界面。
//					When rtSplash ad is closed, jump to the next(main) Activity immediately.
                        jump();
//					如果应用没有单独的闪屏Activity，需要调用closeRTSplash方法去关闭开屏广告
//					If you do not carry a separate advertising activity, you need to call closeRTSplash way to close the splash ad

//					rtSplashAd.closeRTSplash();
                    }

                    @Override
                    public void onRTSplashLoadFailed() {
                        Log.i("DomobSDKDemo", "onRTSplashLoadFailed");
                    }

                    @Override
                    public void onRTSplashPresent() {
                        Log.i("DomobSDKDemo", "onRTSplashStart");
                    }

                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rtSplashAd.splash(WelcomeActivity.this, WelcomeActivity.this.findViewById(R.id.splash_holder));
                    }
                }, 1);
            }
        }
        isShown = false;
    }

    private void jump() {
        Intent dataIntent = new Intent();
        if (!flag) {
            dataIntent.setClass(WelcomeActivity.this, LoginActivity.class);
        } else {
            dataIntent.setClass(WelcomeActivity.this, FirstActivity.class);
            dataIntent.putExtra(Global.MOB_AD, 1);
        }

        startActivity(dataIntent);
        finish();
    }

    private List<AdInfo> listOfAd;
    private ImageView[] indicatorIcons;
    private ArrayList AdPageViews;
    private int AdPageNum;
    private ViewPager viewPager;
    private boolean flag;

    private void initAdView() {
        File f_dir = new File(Global.AD_PATH);
        AdPageViews = new ArrayList();
        listOfAd = DatabaseProvider.queryAdInfo(this);
        Log.e("gomtel", "listOfAd= " + listOfAd.size());
        if (listOfAd.size() < 1) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
//                    flag = doLogin();
                    Intent dataIntent = new Intent();
                    if (!flag) {
                        dataIntent.setClass(WelcomeActivity.this, LoginActivity.class);
                    } else {
                        dataIntent.setClass(WelcomeActivity.this, FirstActivity.class);
                    }
                    startActivity(dataIntent);
                    finish();
                }

            }, delayMillis);
        }
        if (f_dir.exists()) {
            File[] subFile = f_dir.listFiles();
            AdPageNum = subFile.length;
            if (AdPageNum == 0) {
                jump();
            }
//            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
//                if (!subFile[iFileLength].isDirectory()) {
//                    String filename = subFile[iFileLength].getName();
//                    ImageView img = new ImageView(this);
//                    img.setImageURI(Uri.parse(Global.AD_PATH +filename));
//                    Log.e("gomtel","filename= "+filename);
//                    img.setScaleType(ImageView.ScaleType.FIT_XY);
//                    AdPageViews.add(img);
//                }
//            }
            AdPageNum = listOfAd.size();
            for (int i = 0; i < AdPageNum; i++) {
//                Log.e("GOMTEL","listOfAd= "+listOfAd.get(i).getAd_type());
//                if(Global.AD_TYPE_WELCOM.equals(listOfAd.get(i).getAd_type())) {
                if ("1".equals(listOfAd.get(i).getAd_type())) {
                    ImageView img = new ImageView(this);
                    img.setImageURI(Uri.parse(Global.AD_PATH + listOfAd.get(i).getImag_name()));
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                    AdPageViews.add(img);
                }
            }
        }
        indicatorIcons = new ImageView[AdPageNum];

//        for (int i = 0; i < AdPageNum; i++) {
//            ImageView img = new ImageView(this);
//            img.setImageURI(Uri.parse(Global.AD_PATH + "GT_001B.JPG"));
//            img.setScaleType(ImageView.ScaleType.FIT_XY);
//            AdPageViews.add(img);
//        }
        indicators.removeAllViews();
        for (int i = 0; i < AdPageNum; i++) {
            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            icon.setPadding(20, 0, 20, 0);
            indicatorIcons[i] = icon;

            if (i == 0) {
                indicatorIcons[i].setBackgroundResource(R.drawable.screen_dot_current);
            } else {
                indicatorIcons[i].setBackgroundResource(R.drawable.screen_dot_normal);
            }

            indicators.addView(indicatorIcons[i]);
        }
        indicators.setVisibility(View.VISIBLE);
        viewPager = ((ViewPager) findViewById(R.id.adPages));
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(new AdPageAdapter());
        viewPager.setOnPageChangeListener(new AdPageChangeListener());
        try {
            Field leftEdgeField = viewPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = viewPager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(viewPager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(viewPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShown = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_main);
//        mPrefs = getSharedPreferences("WATCH", 0);
//        AdIds = mPrefs.getString("AD_IDS","NULL");
//        new File(Global.AD_PATH).mkdir();
//        mUpdateManager = new UpdateManager(this);
        mInstance = XcmApplication.getInstance();
        mUpdateDB = UpdateDB.getInstance(this);
        mProtocolData = mInstance.getProtocolData();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                Message message = new Message();
//                message.what = 3;
//                mHandler.sendMessage(message);
                mInstance.initClientSocket();
            }
        });
        thread.start();

//        mInstance.initClientSocket();
        initAd();
        tools = new XcmTools(this);
        flag = doLogin();
    }

    private void initAd() {
        indicators = (LinearLayout) findViewById(R.id.indicator);
        splash_holder = (RelativeLayout) findViewById(R.id.splash_holder);
        httpUtil = HttpUtils.getInstance();
        getLocation();
//        httpUtil = MainService.getInstance().httpUtil;
        if (httpUtil != null) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String PhoneNumber = tm.getLine1Number();
            httpUtil.getHeartBeat(this, "0", "13412345678", String.valueOf(longitude), String.valueOf(latitude), this);
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
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
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

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
    }

    @Override
    public void onStart() {
        super.onStart();
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                initAd();
//                flag = doLogin();
//                Intent dataIntent = new Intent();
//                if (!flag) {
//                    dataIntent.setClass(WelcomeActivity.this, LoginActivity.class);
//                } else {
//                    dataIntent.setClass(WelcomeActivity.this, FirstActivity.class);
//                }
//                startActivity(dataIntent);
//                finish();
//            }
//
//        }, delayMillis);
    }

    private boolean doLogin() {
        boolean flag = false;
        String user_id = tools.get_user_id();
        if (!"0".equals(user_id)) {
            List<?> userInfoOne = mUpdateDB.queryDataToBases(UserInfo.class, new String[]{user_id}, new String[]{UserInfo.USER_ID}, null, false, 1);
            int length = userInfoOne.size();
            if (length > 0) {
                UserInfo userInfo = (UserInfo) userInfoOne.get(0);
                Constants.USERID = userInfo.getUserId();
                Constants.USERNICKNAME = userInfo.getUserNickName();
                Constants.USERHEADURL = userInfo.getUserHeadUrl();
                Constants.PHONE = userInfo.getUserName();
                tools.set_login_phone(userInfo.getUserName());

                tools.set_user_id(userInfo.getUserId());
                tools.set_user_phone(userInfo.getUserNickName());

                String user_nick = "" + userInfo.getUserNickName();
                String user_sex = "" + userInfo.getUserSex();
                String user_age = "" + userInfo.getUserHeight();
                String user_height = "" + userInfo.getUserHeight();
                String user_weight = "" + userInfo.getUserWeight();
                String user_head = "" + userInfo.getUserHeadUrl();
                String passWord = "" + userInfo.getUserPassword();

                if (user_nick.equals("") || user_nick.equals(null)) {
                    user_nick = "0";
                }

                if (user_sex.equals("") || user_sex.equals(null)) {
                    user_sex = "0";
                }
                if (user_age.equals("") || user_age.equals(null)) {
                    user_age = "0";
                }
                if (user_height.equals("") || user_height.equals(null)) {
                    user_height = "0";
                }
                if (user_weight.equals("") || user_weight.equals(null)) {
                    user_weight = "0";
                }
                if (user_head.equals("") || user_head.equals(null)) {
                    user_weight = "0";
                }


                if (tools.get_person().equals("") || tools.get_person().equals(null)) {
                    String personstring = user_head + "," + user_nick + "," + user_sex + "," + user_age + "," + user_height + "," + user_weight;


                    tools.set_person(personstring);
                }
                huoQuDeviceInfo();
                flag = true;
            }
        }
        return flag;
    }

    private void huoQuDeviceInfo() {
        // TODO Auto-generated method stub
        List<?> deviceInfos = mUpdateDB.queryDataToBases(DeviceInfo.class, new String[]{Constants.USERID}, new String[]{DeviceInfo.DEVICE_BOND_USER}, null, false, 1);
        int length = deviceInfos.size();
        Trace.i("device list size===" + length);
        for (int i = 0; i < length; i++) {
            BaoBeiBean BaoBeiBeanTmp = new BaoBeiBean();
            DeviceInfo tmp = (DeviceInfo) deviceInfos.get(i);
            if (i == 0) {
                Constants.DEVICEID = tmp.getDeviceId();
//				tools.set_current_device_id(Constants.DEVICEID);
            }
            BaoBeiBeanTmp.setBaoBeiUrl(tmp.getDeviceHeadUrl());
            BaoBeiBeanTmp.setBaoBeiName(tmp.getDeviceName());
            BaoBeiBeanTmp.setBaoBeiPhone(tmp.getDevicePhone());
            BaoBeiBeanTmp.setBaoBeiSelect("0");

            mDataList.append(i, BaoBeiBeanTmp);
        }

    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        switch (what) {
            case HttpUtils.HTTP_REQUEST_SESULT_HEARTBEAT:
                Log.e("lixiang","result= "+result);
                if (result == 1) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = object;
                    mHandler.sendMessage(msg);
                }
                if (result == 0) {
                    Message msg = new Message();
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
                if (result == 2) {
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                }
                break;
            case HttpUtils.HTTP_REQUEST_DOWNLOAD_SOFTWARE:
                Message msg_download = new Message();
                msg_download.what = 2;
                msg_download.obj = object;
                mHandler.sendMessage(msg_download);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int what) {

    }

    class AdPageAdapter extends PagerAdapter {
        AdPageAdapter() {
        }

        @Override
        public void destroyItem(View paramView, int paramInt, Object paramObject) {
            ((ViewPager) paramView).removeView((View) WelcomeActivity.this.AdPageViews.get(paramInt));
        }

        @Override
        public void finishUpdate(View paramView) {
        }

        public int getCount() {
            return WelcomeActivity.this.AdPageViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int Adposition = position;
            View view = (View) AdPageViews.get(position);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Log.e(TAG, "position= " + Adposition);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//                    Uri content_url = Uri.parse(URLString[Adposition]);
                    Uri content_url = Uri.parse(getUrl(Adposition));
                    intent.setData(content_url);
                    startActivity(intent);

                }
            });
            return super.instantiateItem(container, position);

        }

        public int getItemPosition(Object paramObject) {
            return super.getItemPosition(paramObject);
        }

        @Override
        public Object instantiateItem(View view, int position) {
            ((ViewPager) view).addView((View) WelcomeActivity.this.AdPageViews.get(position));
            return WelcomeActivity.this.AdPageViews.get(position);
        }

        public boolean isViewFromObject(View paramView, Object paramObject) {
            return paramView == paramObject;
        }

        public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View paramView) {
        }
    }

    private String getUrl(int adposition) {
        String URL = null;
        if (listOfAd != null) {
            URL = listOfAd.get(adposition).getAd_url();
        }
        return URL;
    }

    class AdPageChangeListener
            implements ViewPager.OnPageChangeListener {
        AdPageChangeListener() {
        }

        public void onPageScrollStateChanged(int state) {
            if (rightEdge != null && !rightEdge.isFinished()) {
                viewPager.setVisibility(View.GONE);
                indicators.setVisibility(View.GONE);
//                DatabaseProvider.clearAdInfo(WelcomeActivity.this);
                flag = doLogin();
                Intent dataIntent = new Intent();
                if (!flag) {
                    dataIntent.setClass(WelcomeActivity.this, LoginActivity.class);
                } else {
                    dataIntent.setClass(WelcomeActivity.this, FirstActivity.class);
                }
                startActivity(dataIntent);
                finish();
                startPopWindow();
            }
        }

        public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
            if ((paramInt1 != AdPageNum) || (paramFloat != 0.0D) || (paramInt2 != 0))
                return;
        }

        public void onPageSelected(int paramInt) {
            for (int i = 0; i < indicatorIcons.length; i++) {
                indicatorIcons[paramInt].setBackgroundResource(R.drawable.screen_dot_current);
                if (paramInt != i) {
                    indicatorIcons[i].setBackgroundResource(R.drawable.screen_dot_normal);
                }
            }
        }
    }

    private void startPopWindow() {
        String pathOfAdBottom = null;
        for (AdInfo info : listOfAd) {
            if (Global.AD_TYPE_BOTTOM.equals(info.getAd_type())) {
                pathOfAdBottom = info.getAd_url();
            }
        }
        WindowAdUtils.showPopupWindow(this, this.getWindowManager(), pathOfAdBottom);
    }
}
