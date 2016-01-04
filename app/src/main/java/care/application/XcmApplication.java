package care.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import com.mtk.btnotification.R;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import care.clientmanager.ClientNetManager;
import care.db.DataHelper;
import care.db.ProtocolData;
import care.db.manager.DataBaseManagerImpl;
import care.db.manager.UpdateDB;
import care.utils.Constants;
import care.utils.XcmTools;

public class XcmApplication extends Application {

    public final static String TAG = "reqe";
    /**
     * 保存每一个activity的状态
     */
    private HashMap<String, Activity> activityList = new HashMap<String, Activity>();

    //存储路径
    public static String SDCARD = Environment
            .getExternalStorageDirectory() + "/xcm/";
    //log输出路径
    public static String LOG_FILE = SDCARD + "logs";
    //db
    public static String DB_FILE = SDCARD + "db";
    //头部图片路径
    public static String HEAD_IMAGE_FILE = SDCARD + "head";

    private static XcmApplication mInstance;
    private DataBaseManagerImpl mDataBaseManagerImpl = null;
    private UpdateDB mUpdateDB = null;
    private ProtocolData mProtocolData = null;
    private ImageLoader mImageLoader = null;
    private ClientNetManager mClientNetManager = null;
    private DataHelper mDataHelper=null;
    private static XcmTools tools;

    //保存语音的文件夹
    public static String VOICE_FILE = SDCARD + "voice";
    public static int OFF_LINE_COUNT=0;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        creatFileToApp();
        saveScreenToWidthAndHeight();
        checkNetwork();  //判断网络状态
        initImageLoader(getApplicationContext());
        tools=new XcmTools(mInstance);
        XGPushManager.registerPush(mInstance,mXGIOperateCallback);
    }

    public String gettMetaData() {
        String msg = "3070";
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.metaData;
//            msg = String.valueOf(bundle.getString("projectName"));
//            msg = msg.substring(msg.lastIndexOf('p')+1);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getUserId(){
        XcmTools tools = new XcmTools(this);
        return tools.get_user_id();
    }
    public DisplayImageOptions getImageOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.button_register_photo)
                .showImageOnFail(R.drawable.button_register_photo)
                .showStubImage(R.drawable.button_register_photo)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(30))
                .build();

        return options;
    }

    public static synchronized XcmApplication getInstance() {
        return mInstance;
    }

    public DataHelper getDataHelper(Context context) {
        if (mDataHelper == null) {
        	mDataHelper = DataHelper.getInstance(context);
        }
        return mDataHelper;
    }

    public DataBaseManagerImpl getDataBaseManagerImpl(Context context) {
        if (mDataBaseManagerImpl == null) {
            mDataBaseManagerImpl = DataBaseManagerImpl.getInstance(context);
        }
        return mDataBaseManagerImpl;
    }
    
    
    public UpdateDB getUpdateDB(Context context) {
        if (mUpdateDB == null) {
            mUpdateDB = UpdateDB.getInstance(context);
        }
        return mUpdateDB;
    }

    public ProtocolData getProtocolData() {
        if (mProtocolData == null) {
            mProtocolData = ProtocolData.getInstance();
        }
        return mProtocolData;
    }

    public ImageLoader getmImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
        }
        return mImageLoader;
    }

    private void saveScreenToWidthAndHeight() {
        // TODO Auto-generated method stub
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics display = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(display);

        Constants.WIDTH = display.widthPixels;
        Constants.HEIGHT = display.heightPixels;
    }

    private void creatFileToApp() {
        // TODO Auto-generated method stub
        File file_log = new File(LOG_FILE);
        if (!file_log.exists()) {
            file_log.mkdirs();   //如果路径不存在，则创建
        }

        File file_db = new File(DB_FILE);
        if (!file_db.exists()) {
            file_db.mkdirs();   //如果数据库路径不存在，则创建
        }

        File file_head = new File(HEAD_IMAGE_FILE);
        if (!file_head.exists()) {
            file_head.mkdirs();   
        }

        File file_voice = new File(VOICE_FILE);
        if (!file_voice.exists()) {
            file_voice.mkdirs();   //创建语音文件
        }
    }

    /**
     * 检查网络状态
     */
    private boolean checkNetwork() {
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cwjManager.getActiveNetworkInfo();
        if (network == null)
            return false;
        Constants.IS_OPEN_NETWORK = network.isAvailable();
        return network.isAvailable();
    }

    public void checkNet(boolean checkNet) {
        if (checkNet) {   //网络状态
            getClientNetManager();
            String result = mClientNetManager.initialOpenConnection();
        }
    }

    public ClientNetManager getClientNetManager() {
        if (mClientNetManager == null) {
            mClientNetManager = ClientNetManager.getInstance();
        }
        return mClientNetManager; 
    }

    private void initImageLoader(Context context) {
        // TODO Auto-generated method stub
        File cacheDir = new File(StorageUtils.getCacheDirectory(context), "family/cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5加密 
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new BaseImageDownloader(context, 5000, 15000))//超时时间
                .writeDebugLogs() 
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void initClientSocket() {
        if (mClientNetManager == null) {
            getClientNetManager();
        }
        mClientNetManager.initialize();
        checkNet(Constants.IS_OPEN_NETWORK);
    }

    /**
     * @param activityName
     * @param activity
     */
    public void addActivity(String activityName, Activity activity) {
        activityList.put(activityName, activity);
    }

    public Activity getActivity(String acString) {
        Activity activity = null;
        if (!activityList.isEmpty()) {
            activity = activityList.get(acString);
        }
        return activity;
    }

    public boolean removeActivity(String acString) {
        if (!activityList.isEmpty()) {
            activityList.remove(acString);
        }
        return true;
    }

    /**
     * 移除activity
     */
    public void killAll() {
        Iterator<String> iter = activityList.keySet().iterator();
        while (iter.hasNext()) {
            activityList.get(iter.next()).finish();
        }
        activityList.clear();  
        mUpdateDB = null;
    }

    public void exit() {
        // TODO Auto-generated method stub
        killAll();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    //信鸽回调事件
    XGIOperateCallback mXGIOperateCallback=new XGIOperateCallback() {
		@Override
		public void onSuccess(Object data, int flag) {
			tools.set_token_id(data+"");
		}
		
		@Override
		public void onFail(Object data, int errCode, String msg) {
		}
	};

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
