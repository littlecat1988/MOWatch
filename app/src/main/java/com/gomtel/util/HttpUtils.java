package com.gomtel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.gomtel.database.DatabaseProvider;
import com.mtk.main.FirstActivity;
import com.mtk.main.SleepActivity;
import com.mtk.main.SleepDetailInfo;
import com.mtk.main.SportActivity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HttpUtils {
    public static final int HTTP_REQUEST_SESULT_HEARTBEAT = 10;
    public static final int HTTP_REQUEST_TOTALSPORTS_STEP = 15;
    public static final int HTTP_REQUEST_DETAILSPORT_INFO = 12;
    public static final int HTTP_REQUEST_TOTALSLEEP_INFO = 13;
    public static final int HTTP_REQUEST_DETAILSLEEP_INFO = 14;
    public static final int HTTP_REQUEST_TOTALSPORTS_INFO = 11;
    public static final int HTTP_REQUEST_DOWNLOAD_SOFTWARE = 15;
    public static final int HTTP_REQUEST_DOWNLOAD_VERSION = 16;
    public final String TAG = "HttpUtils";
    //    public static final int NETWORK_STATE_INAVAILABLE = 0;
//    public static final int NETWORK_STATE_TIMEOUT = 1;
    public static final int HTTP_REQUEST_SESULT_REGISTER = 0;

    public static final int HTTP_REQUEST_SESULT_LOGIN = 1;

    public static final int HTTP_REQUEST_SESULT_VERSION = 2;

    public static final int HTTP_REQUEST_SESULT_RECORDER = 3;

    public static final int HTTP_REQUEST_SESULT_TOTAL = 4;

    public static final int HTTP_REQUEST_SESULT_WEATHER = 5;

    public static final int HTTP_REQUEST_SESULT_USERINFO = 6;
    public static final int HTTP_REQUEST_SESULT_SPORTINFO = 7;
    public static final int HTTP_REQUEST_SESULT_SLEEPINFO = 8;
    public static final int HTTP_REQUEST_SESULT_HRINFO = 9;
    private HttpParams httpParams;
    private HttpClient httpClient;
    //private Context mContext;
    public static final String BASE_REMOTE_URL = "http://gomtel.54322.net/wear";
    private static HttpUtils sInstance;
    private String mUserAgent;
    private static final HandlerThread sWorkerThread = new HandlerThread(
            "HttpUtils");

    static {
        sWorkerThread.start();
    }

    private static final Handler sWorker = new Handler(
            sWorkerThread.getLooper());
    private boolean timeout = false;
    private boolean no_network = false;
    private boolean unable_network = true;
    private Header[] file_name;

    private HttpUtils() {
        //mContext = c;
    }

    public synchronized static HttpUtils getInstance() {
        if (sInstance == null) {
            sInstance = new HttpUtils();
        }
        return sInstance;
    }

    public boolean isNetworkAvailable(Context c) {

        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();

        return network != null && network.isConnectedOrConnecting();

    }

    private HttpClient getHttpClient(Context c) {
        this.httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        HttpClientParams.setRedirecting(httpParams, true);
        String userAgent = getUserAgent(c);// "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        HttpProtocolParams.setUserAgent(httpParams, userAgent);
        httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }

    private JSONObject getCommomsJsonParams(Context c) {
        try {
            JSONObject jsonObject = new JSONObject();
            String bpve = getBPVE();
            String bcve = getBCVE(c);
            String bmod = getBMOD();
            String bpme = getBPME();
            int buid = getBUID();
            String ua = getUserAgent(c);
            String imei = getIMEI(c);
            String imsi = getIMSI(c);
            jsonObject.put(HttpParamsKey.BPVE, bpve);
            jsonObject.put(HttpParamsKey.BCVE, bcve);
            jsonObject.put(HttpParamsKey.BMOD, bmod);
            jsonObject.put(HttpParamsKey.BPME, bpme);
            jsonObject.put(HttpParamsKey.BUID, buid);
            jsonObject.put(HttpParamsKey.UA, ua);
            jsonObject.put(HttpParamsKey.IMEI, imei);
            jsonObject.put(HttpParamsKey.IMSI, imsi);
            return jsonObject;
        } catch (Exception e) {

        }
        return null;
    }

    private String getBPVE() {
        return "ap1.0.1";
    }

    private String getBCVE(Context c) {

        return getVersionCode(c);
    }

    private String getBMOD() {
        return "W300";
    }

    private String getBPME() {
        return "0001";
    }

    private int getBUID() {
        return 0;
    }

    private String getIMEI(Context c) {
        TelephonyManager mTelephonyManager = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTelephonyManager.getDeviceId();
        return imei;
    }

    private String getIMSI(Context c) {
        TelephonyManager mTelephonyManager = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyManager.getSubscriberId();

        return imsi;
    }

    private String getUserAgent(Context c) {
        if (mUserAgent == null) {
            WebView webview = new WebView(c);
            WebSettings settings = webview.getSettings();
            mUserAgent = settings.getUserAgentString();
        }
        return mUserAgent;
    }

    private InputStream inputStreamPost(String url, StringEntity se, HttpCallback cb) {
        HttpPost httpRequest = new HttpPost(url);
        InputStream inputStream = null;
        try {
            httpRequest.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            Log.d("", "zhjp ,httpResponse.getStatusLine().getStatusCode() = "
                    + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
//                file_name = httpResponse.getHeaders("filename");
                inputStream = entity.getContent();
            } else {
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "inputStreamPost---onError");
            unable_network = false;
            if (cb != null)
                cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HEARTBEAT, 2, null);
            cb.onError(0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return inputStream;
    }

    private InputStream getStream(String url, StringEntity se, HttpCallback cb) {
        HttpPost httpRequest = new HttpPost(url);
        InputStream inputStream = null;
        try {
            httpRequest.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                inputStream = entity.getContent();
            } else {
                // timeout
//                timeout = true;
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "inputStreamPost---onError");
            unable_network = false;
            if (cb != null)
                cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HEARTBEAT, 1, null);
            cb.onError(0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return inputStream;
    }


    private JSONObject getSportRecorderParams(Context c) {
        try {
            JSONObject jsonObject = getCommomsJsonParams(c);
            jsonObject.put(HttpParamsKey.METHOD, "loadSportRecorder");
            // jsonObject.put(HttpParamsKey.VERSION, getVersionCode(mContext));
            return jsonObject;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }


    private ArrayList<SportInfo> getSportInfoFromJson(String json) {
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int result = jsonObject.getInt(HttpParamsKey.RESULT);
                if (result == 1) {
                    // successed
                    JSONArray P_array = jsonObject
                            .getJSONArray(HttpParamsKey.RECORDERLIST);
                    ArrayList<SportInfo> list = new ArrayList<SportInfo>();
                    for (int i = 0; i < P_array.length(); i++) {
                        JSONObject item = P_array.getJSONObject(i);
                        SportInfo info = new SportInfo();
                        if ((info.date = item.getString(HttpParamsKey.DATE)) != null) {
                            info.step = item.getInt(HttpParamsKey.STEP);
                            info.burn = item.getInt(HttpParamsKey.BURN);
                            list.add(info);
                        }
                    }
                    return list;
                } else {
                    // failed
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    private ArrayList<SportInfo> getTotalSportInfoFromJson(String json) {
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int result = jsonObject.getInt(HttpParamsKey.RESULT);
                if (result == 1) {
                    // successed
//Log.e(SettingsCloudFragment.TAG,"result= "+result);
                    JSONObject sportJson = jsonObject.getJSONObject(HttpParamsKey.SPORTTOTALLIST);
                    ArrayList<SportInfo> list = new ArrayList<SportInfo>();
                    SportInfo info = new SportInfo();
                    if ((info.totalDays = sportJson
                            .getInt(HttpParamsKey.SPORT_TOTAL_DAY)) != 0) {
                        info.totalSteps = sportJson
                                .getInt(HttpParamsKey.SPORT_TOTAL_STEP);
                        info.totalBurns = sportJson
                                .getInt(HttpParamsKey.SPORT_TOTAL_BURN);
                        list.add(info);
                    }
                    return list;
                } else {
                    // failed
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        Log.e(SettingsCloudFragment.TAG,"getTotalSportInfoFromJson");
        return null;
    }

    //add by lixiang for  get sleep data
    private ArrayList<SportInfo> getSleepInfoFromJson(String json) {
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int result = jsonObject.getInt(HttpParamsKey.RESULT);
                if (result == 1) {
                    // successed
                    Log.e(TAG, "result= " + result);
                    JSONObject sportJson = jsonObject.getJSONObject(HttpParamsKey.SLEEPLIST);
                    ArrayList<SportInfo> list = new ArrayList<SportInfo>();
                    SportInfo info = new SportInfo();
                    if ((info.totalDays = sportJson
                            .getInt(HttpParamsKey.SPORT_TOTAL_DAY)) != 0) {
                        info.totalSteps = sportJson
                                .getInt(HttpParamsKey.SPORT_TOTAL_STEP);
                        info.totalBurns = sportJson
                                .getInt(HttpParamsKey.SPORT_TOTAL_BURN);
                        list.add(info);
                    }
                    return list;
                } else {
                    // failed
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        Log.e(SettingsCloudFragment.TAG,"getTotalSportInfoFromJson");
        return null;
    }

    //add by lixiang for get heartrate data
    private ArrayList<SportInfo> getHRInfoFromJson(String json) {
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int result = jsonObject.getInt(HttpParamsKey.RESULT);
                if (result == 1) {
                    // successed
//Log.e(SettingsCloudFragment.TAG,"result= "+result);
                    JSONObject sportJson = jsonObject.getJSONObject(HttpParamsKey.HEARTRATELIST);
                    ArrayList<SportInfo> list = new ArrayList<SportInfo>();
                    SportInfo info = new SportInfo();
                    if ((info.totalDays = sportJson
                            .getInt(HttpParamsKey.SPORT_TOTAL_DAY)) != 0) {
                        info.totalSteps = sportJson
                                .getInt(HttpParamsKey.SPORT_TOTAL_STEP);
                        info.totalBurns = sportJson
                                .getInt(HttpParamsKey.SPORT_TOTAL_BURN);
                        list.add(info);
                    }
                    return list;
                } else {
                    // failed
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        Log.e(SettingsCloudFragment.TAG,"getTotalSportInfoFromJson");
        return null;
    }


    private ArrayList<WeatherInfo> getWeatherInfo(String json) {
        if (json != null) {
            try {
                Log.e(TAG, "weather json= " + json);
                JSONObject jsonObject = new JSONObject(json);
                int result = jsonObject.getInt(HttpParamsKey.RESULT);
                if (result == 1) {
                    // successed
//					JSONArray p_array = jsonObject
//							.getJSONArray(HttpParamsKey.WEATHER);
                    ArrayList<WeatherInfo> list = new ArrayList<WeatherInfo>();
//					for (int i = 0; i < p_array.length(); i++) {
                    JSONObject item = jsonObject.getJSONObject(HttpParamsKey.WEATHER);
                    WeatherInfo info = new WeatherInfo();
                    if ((info.weather = item
                            .getString(HttpParamsKey.WEATHER_WEATHER)) != null) {
                        info.cityname = item
                                .getString(HttpParamsKey.WEATHER_CITY);
                        info.lowTemperature = item
                                .getInt(HttpParamsKey.WEATHER_LOWTEMPERATURE);
                        info.highTemperature = item
                                .getInt(HttpParamsKey.WEATHER_HIGHTEMPERATURE);
                        list.add(info);
//						}
                    }
                    return list;
                } else {
                    // failed
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    private UserInfo getUserInfoFromJson(String json) {
        Log.e(TAG, "json= " + json);
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int result = jsonObject.getInt(HttpParamsKey.RESULT);
                Log.e(TAG, "result= " + result);
                if (result == 1) {
                    // successed
                    JSONObject infoObject = jsonObject.getJSONObject(HttpParamsKey.USERINFO);
                    UserInfo info = new UserInfo();
                    info.userid = infoObject.getInt(HttpParamsKey.USER_ID);
                    info.name = infoObject.getString(HttpParamsKey.KEY_USER_NAME);
                    info.nickname = infoObject.getString(HttpParamsKey.KEY_USER_NICKNAME);
                    info.sex = infoObject.getInt(HttpParamsKey.KEY_USER_SEX);
                    info.age = infoObject.getString(HttpParamsKey.KEY_USER_AGE);
                    info.weight = infoObject.getString(HttpParamsKey.KEY_USER_WEIGHT);
                    info.height = infoObject.getString(HttpParamsKey.KEY_USER_HEIGHT);
                    return info;
                } else {
                    // failed
                    return null;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.e(TAG, "json01= " + json);
        return null;
    }

    /**
     * 锟斤拷锟斤拷锟矫伙拷锟斤拷POST锟斤拷锟斤拷
     *
     * @param info
     * @return
     */
    private JSONObject generateUserParams(Context c, UserInfo info) {
        // json example:
        // {"method":"uploadUserInfo","userinfo":[{"user_name":"锟斤拷锟斤拷","user_sex":"锟斤拷","user_age":"18","user_height":"180cm","user_weight":"85kg"}]}
        JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "uploadUserInfo");
            // JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put(HttpParamsKey.USER_ID, info.userid);
            jsonObject2.put(HttpParamsKey.KEY_USER_NAME, info.name);
            jsonObject2.put(HttpParamsKey.KEY_USER_NICKNAME, info.nickname);
            jsonObject2.put(HttpParamsKey.KEY_USER_SEX, info.sex);
            jsonObject2.put(HttpParamsKey.KEY_USER_AGE, info.age);
            jsonObject2.put(HttpParamsKey.KEY_USER_HEIGHT, info.height);
            jsonObject2.put(HttpParamsKey.KEY_USER_WEIGHT, info.weight);
            // jsonArray.put(jsonObject2);
            jsonObject.put(HttpParamsKey.USERINFO, jsonObject2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 锟斤拷锟斤拷锟剿讹拷锟斤拷POST锟斤拷锟斤拷
     *
     * @param info
     * @return
     */
    private JSONObject generateSportParams(Context c, SportInfo info) {
        // json example:
        // {"method":"uploadSportInfo","sportinfo":[{"KEY_DATE":"2015-04-09","KEY_STEP":"950","KEY_BURN":"1024"}]}
        JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "uploadSportInfo");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put(HttpParamsKey.USER_ID, info.userid);
            jsonObject2.put(HttpParamsKey.DATE, info.date);
            jsonObject2.put(HttpParamsKey.STEP, info.step);
            jsonObject2.put(HttpParamsKey.BURN, info.burn);
            jsonArray.put(jsonObject2);

            jsonObject.put(HttpParamsKey.SPORTINFO, jsonArray);
        } catch (Exception e) {

        }
        return jsonObject;
    }

    private JSONObject generateSleepParams(Context c, SleepInfo info) {
        JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "uploadSleepInfo");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put(HttpParamsKey.USER_ID, info.userid);
            jsonObject2.put(HttpParamsKey.DATE, info.date);
            jsonObject2.put(HttpParamsKey.START_TIME, info.starttime);
            jsonObject2.put(HttpParamsKey.TOTAL_TIME, info.totaltime);
            jsonObject2.put(HttpParamsKey.LIGHT_SLEEP, info.lightsleep);
            jsonObject2.put(HttpParamsKey.DEEP_SLEEP, info.deepsleep);
            jsonArray.put(jsonObject2);

            jsonObject.put(HttpParamsKey.SLEEPLIST, jsonArray);
        } catch (Exception e) {

        }
        return jsonObject;
    }

    private JSONObject generateHrParams(Context c, HrInfo info) {
        JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "loadHrRecorder");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put(HttpParamsKey.USER_ID, info.userid);
            jsonObject2.put(HttpParamsKey.DATE, info.date);
            jsonObject2.put(HttpParamsKey.TIME, info.time);
            jsonObject2.put(HttpParamsKey.HEART, info.heart);
            jsonArray.put(jsonObject2);
            jsonObject.put(HttpParamsKey.HEARTRATELIST, jsonArray);
        } catch (Exception e) {

        }
        return jsonObject;
    }

    private String inputstreamToStr(final InputStream is) {
        // {"recorderlist":[{"KEY_DATE":"2015-04-09","KEY_STEP":"950","KEY_BURN":"1024"}]}
        try {
            BufferedReader br;

            br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            String response = "";
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                response = response + readLine;
            }

            Log.d("", "zhjp , response = " + response);
            return response;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private boolean inputstreamToFile(final InputStream inStream, String fileName) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            byte[] data = outStream.toByteArray();
//            (new File(Global.AD_PATH)).mkdirs();
            File imageFile = new File(fileName);
            FileOutputStream fileOutStream = new FileOutputStream(imageFile);
            fileOutStream.write(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.e(TAG, "inputstreamToImage");
            e.printStackTrace();
        } finally {
            return true;
        }
    }


    /**
     * cb锟斤拷锟截碉拷锟斤拷锟斤拷锟斤拷锟斤拷为HashMap<String,String>
     *
     * @param userid
     * @param cb
     */
    public void loadAppVersionXML(Context c, int userid, final HttpCallback cb) {
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        try {
            final JSONObject jsonObject = getCommomsJsonParams(c);
            jsonObject.put(HttpParamsKey.METHOD, "loadAppVersion");
            // jsonObject.put(HttpParamsKey.VERSION, getVersionCode(mContext));
            jsonObject.put(HttpParamsKey.USER_ID, userid);
            final StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    InputStream is = inputStreamPost(url, se, cb);
                    if (is != null) {
                        ParseXmlService xml = new ParseXmlService();
                        try {
                            HashMap<String, String> map = xml.parseXml(is);
                            if (cb != null)
                                cb.onHttpRequestComplete(
                                        HTTP_REQUEST_SESULT_VERSION, -1, map);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_VERSION);

                    }
                }

            };
            sWorker.post(r);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_VERSION);
            }
        }
    }

    // 锟斤拷锟斤拷锟剿讹拷锟斤拷录

    /**
     * ArrayList<SportInfo>
     *
     * @param userid
     * @param cb
     */
    public void loadSportRecorder(Context c, int userid, final HttpCallback cb) {
        final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getSportRecorderParams(c);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    jsonObject.put(HttpParamsKey.METHOD,
                            "loadSportRecorder");
                    jsonObject.put(HttpParamsKey.USER_ID, userId);
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(url, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        int result = 0;
                        if (json != null && json.length() > 0) {
                            JSONObject resultJson = new JSONObject(json);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        ArrayList<SportInfo> list = getSportInfoFromJson(json);
                        if (cb != null) {
                            Log.e(TAG, "result = " + result);
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_RECORDER,
                                    result, list);
                        }
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_RECORDER);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_RECORDER);
                    }
                }
            }
        };
        sWorker.post(r);

    }

    public void loadSportTotal(Context c, int userid, final HttpCallback cb) {
        final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "loadSportTotal");
            // jsonObject.put(HttpParamsKey.VERSION,
            // getVersionCode(mContext));
            jsonObject.put(HttpParamsKey.USER_ID, userId);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_TOTAL);
            }
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(url, se, cb);
                    // {"sporttotallist":[{"sport_total_day":"107","sport_total_step":"15632","sport_total_burn":"13465400000"}]}
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        int result = 0;
                        if (json != null && json.length() > 0) {
                            JSONObject resultJson = new JSONObject(json);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        ArrayList<SportInfo> list = getTotalSportInfoFromJson(json);
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_TOTAL,
                                    result, list);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_TOTAL);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_TOTAL);
                    }
                }
            }

        };
        sWorker.post(r);
        // return null;
    }

    //add by  lixiang for get sleep data
    public void loadSleepRecorder(Context c, int userid, final HttpCallback cb) {
        final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "loadSleepRecorder");
            // jsonObject.put(HttpParamsKey.VERSION,
            // getVersionCode(mContext));
            jsonObject.put(HttpParamsKey.USER_ID, userId);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_SLEEPINFO);
            }
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(url, se, cb);
                    // {"sporttotallist":[{"sport_total_day":"107","sport_total_step":"15632","sport_total_burn":"13465400000"}]}
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        Log.e(TAG, "lixiang---json= " + json);
                        int result = 0;
                        if (json != null && json.length() > 0) {
                            JSONObject resultJson = new JSONObject(json);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        ArrayList<SportInfo> list = getSleepInfoFromJson(json);
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_SLEEPINFO,
                                    result, list);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_SLEEPINFO);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_SLEEPINFO);
                    }
                }
            }

        };
        sWorker.post(r);
        // return null;
    }

    //add by lixiang for heartrate
    public void loadHRRecorder(Context c, int userid, final HttpCallback cb) {
        final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "loadHrRecorder");
            // jsonObject.put(HttpParamsKey.VERSION,
            // getVersionCode(mContext));
            jsonObject.put(HttpParamsKey.USER_ID, userId);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_HRINFO);
            }
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(url, se, cb);
                    // {"sporttotallist":[{"sport_total_day":"107","sport_total_step":"15632","sport_total_burn":"13465400000"}]}
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        int result = 0;
                        if (json != null && json.length() > 0) {
                            JSONObject resultJson = new JSONObject(json);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        ArrayList<SportInfo> list = getHRInfoFromJson(json);
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HRINFO,
                                    result, list);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_HRINFO);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_HRINFO);
                    }
                }
            }

        };
        sWorker.post(r);
        // return null;
    }

    public void loadWeather(Context c, int userid, final double longitude,
                            final double latitude, final HttpCallback cb) {
        final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            jsonObject.put(HttpParamsKey.METHOD, "loadLocalWeather");
            // jsonObject.put(HttpParamsKey.VERSION,
            // getVersionCode(mContext));
            jsonObject.put(HttpParamsKey.USER_ID, userId);
            jsonObject.put(HttpParamsKey.WEATHER_LONGITUDE, longitude);
            jsonObject.put(HttpParamsKey.WEATHER_LATITUDE, latitude);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_WEATHER);
            }
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    Log.e("", "zhjp , weather   json = " + jsonObject.toString());
                    InputStream is = inputStreamPost(url, se, cb);
                    String json = inputstreamToStr(is);
                    if (is != null) {
//					Log.d("","zhjp , result = "+result);
                        int result = 0;
                        if (json != null && json.length() > 0) {
                            JSONObject resultJson = new JSONObject(json);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        ArrayList<WeatherInfo> list = getWeatherInfo(json);
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_WEATHER,
                                    result, list);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_WEATHER);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_WEATHER);
                    }
                }
            }

        };
        sWorker.post(r);
    }


    public void postUserInfo(Context c, UserInfo info, final HttpCallback cb) {
        //final UserInfo userinfo = info;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = generateUserParams(c, info);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    Log.e(TAG, "zhjp , postUserInfo  json = " + jsonObject.toString());
                    InputStream is = inputStreamPost(url, se, cb);
                    BufferedReader br;
                    if (is != null) {
                        br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String response = "";
                        String readLine = null;
                        while ((readLine = br.readLine()) != null) {
                            response = response + readLine;
                        }
                        int result = 0;
                        if (response != null && response.length() > 0) {
                            JSONObject resultJson = new JSONObject(response);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_USERINFO,
                                    result, null);
                        Log.d("", "zhjp postUserInfo = " + response);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_USERINFO);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_USERINFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }


    public void postSportInfo(Context c, SportInfo info, final HttpCallback cb) {
//		final SportInfo sportinfo = info;
//		final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = generateSportParams(c, info);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
//					Log.i("","zhjp , postSportInfo  json = "+jsonObject.toString());
                    InputStream is = inputStreamPost(url, se, cb);
                    BufferedReader br;
                    if (is != null) {
                        br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String response = "";
                        String readLine = null;
                        while ((readLine = br.readLine()) != null) {
                            response = response + readLine;
                        }
                        Log.e("", "zhjp postSportInfo = " + response);
                        int result = 0;
                        if (response != null && response.length() > 0) {
                            JSONObject resultJson = new JSONObject(response);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_SPORTINFO,
                                    result, null);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_SPORTINFO);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_SPORTINFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void postSleepInfo(Context c, SleepInfo info, final HttpCallback cb) {
//		final SportInfo sportinfo = info;
//		final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = generateSleepParams(c, info);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    Log.e("", "zhjp , postSportInfo  json = " + jsonObject.toString());
                    InputStream is = inputStreamPost(url, se, cb);
                    BufferedReader br;
                    if (is != null) {
                        br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String response = "";
                        String readLine = null;
                        while ((readLine = br.readLine()) != null) {
                            response = response + readLine;
                        }
                        Log.e("", "zhjp postSportInfo = " + response);
                        int result = 0;
                        if (response != null && response.length() > 0) {
                            JSONObject resultJson = new JSONObject(response);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_SLEEPINFO,
                                    result, null);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_SLEEPINFO);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_SLEEPINFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void postHrInfo(Context c, HrInfo info, final HttpCallback cb) {
//		final SportInfo sportinfo = info;
//		final int userId = userid;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = generateHrParams(c, info);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
//					Log.i("","zhjp , postSportInfo  json = "+jsonObject.toString());
                    InputStream is = inputStreamPost(url, se, cb);
                    BufferedReader br;
                    if (is != null) {
                        br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String response = "";
                        String readLine = null;
                        while ((readLine = br.readLine()) != null) {
                            response = response + readLine;
                        }
                        Log.e("", "zhjp postSportInfo = " + response);
                        int result = 0;
                        if (response != null && response.length() > 0) {
                            JSONObject resultJson = new JSONObject(response);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HRINFO,
                                    result, null);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_HRINFO);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_HRINFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void registerUser(Context c, String useraccount, String passwd,
                             String phonenumber, final HttpCallback cb) {
        final String username = useraccount;
        final String password = passwd;
        final String number = phonenumber;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject.put(HttpParamsKey.METHOD, "registerUser");
            jsonObject2.put(HttpParamsKey.KEY_USER_NAME, username);
            jsonObject2.put(HttpParamsKey.KEY_USER_PASSWD, password);
            jsonObject2.put(HttpParamsKey.KEY_USER_PHONE, number);
            jsonObject.put(HttpParamsKey.USERINFO, jsonObject2);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_REGISTER);
            }
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Log.e("", "zhjp ,registerUser " + jsonObject.toString());
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(url, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        Log.e(TAG, "json= " + json);
                        int result = 0;
                        if (json != null) {
                            JSONObject resultJson = new JSONObject(json);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_REGISTER,
                                    result, null);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_REGISTER);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_SPORTINFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }


    public void loginUser(Context c, String useraccount, String passwd,
                          final HttpCallback cb) {
        final String username = useraccount;
        final String password = passwd;
        getHttpClient(c);
        final String url = BASE_REMOTE_URL;
        final JSONObject jsonObject = getCommomsJsonParams(c);
        try {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject.put(HttpParamsKey.METHOD, "login");
            jsonObject2.put(HttpParamsKey.KEY_USER_NAME, username);
            jsonObject2.put(HttpParamsKey.KEY_USER_PASSWD, password);
            jsonObject.put(HttpParamsKey.USERINFO, jsonObject2);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_SESULT_LOGIN);
            }
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(url, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        int result = 0;
                        if (json != null) {
                            JSONObject resultJson = new JSONObject(json);
                            //result = resultJson.getInt(HttpParamsKey.RESULT);
                            result = resultJson.getInt(HttpParamsKey.RESULT);
                        }
                        UserInfo info = getUserInfoFromJson(json);
                        Log.e(TAG, "info = " + info);
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_LOGIN, result, info);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_LOGIN);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_LOGIN);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    //add by lixiang for ad 20151112 begin
    public void getHeartBeat(final Context c, String sex, String number,
                             final String lon, final String lat, final HttpCallback cb) {
        getHttpClient(c);
//        final JSONObject jsonObject = getCommomsJsonParams(c);
        final JSONObject jsonObject = new JSONObject();
        try {
//            JSONObject json_heart = new JSONObject();
            jsonObject.put("bg_long", "3070");
            jsonObject.put("userSex", sex);
            jsonObject.put("phoneNum", number);
            jsonObject.put("osType", "1");
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);

        } catch (Exception e) {
            if (cb != null) {
                cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HEARTBEAT, 1, null);
            }
            return;
        }
        Runnable r = new Runnable() {
            public long heartTime = 300;
            public boolean heartBeat = false;

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_HEARTBEAT, se, cb);
                    AdInfo ad_info = null;
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        is.close();
                        JSONObject js = new JSONObject(json);
                        String advertIds = js.getString("advertIds");
                        heartTime = js.getInt("heartTime");

                        Log.e(TAG, "js = " + js);
                        String isMobAd = js.getString("isOtherAdv");
                        SharedPreferences mPrefs = c.getSharedPreferences("WATCH", 0);
                        String AdIds = null;
                        if (mPrefs != null) {



                            AdIds = mPrefs.getString(Global.AD_IDS, "NO_ID");
                            if ("1".equals(isMobAd)) {
                                if (cb != null)
                                    cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HEARTBEAT, 0, null);
                            } else {
                                if ((!AdIds.equals(advertIds)) && advertIds != null) {
                                    SharedPreferences.Editor edit = mPrefs.edit();
                                    edit.putString(Global.AD_IDS, advertIds);
                                    edit.commit();
                                    DatabaseProvider.clearAdInfo(c);
                                    deleteFiles();
                                    LogUtil.e(TAG, "advertIds= " + advertIds);
                                    JSONObject adInfo = new JSONObject();
                                    adInfo.put("bg_long", "3070");
                                    adInfo.put("advertIds", advertIds);
                                    StringEntity se_adInfo = new StringEntity(adInfo.toString(), "utf-8");
                                    InputStream is_adInfo = inputStreamPost(Global.URL_ADINFO, se_adInfo, cb);
                                    if (is_adInfo != null) {
                                        String json_adInfo = inputstreamToStr(is_adInfo);
                                        is_adInfo.close();
                                        JSONArray array_adInfo = new JSONArray(json_adInfo);
                                        int AdSize = array_adInfo.length();
                                        for (int i = 0; i < AdSize; i++) {
                                            JSONObject js_adInfo = array_adInfo.getJSONObject(i);
                                            String advertId = js_adInfo.getString("advertId");
                                            String imageName = js_adInfo.getString(getImageName(Global.density));
                                            String ad_url = js_adInfo.getString("advertUrl");
                                            String ad_num = js_adInfo.getString("advertNum");
                                            String ad_type = String.valueOf(js_adInfo.getInt("advertType"));
                                            String ad_endtime = js_adInfo.getString("endTime");
//                                String imageName = js_adInfo.getString("imgBName");
                                            JSONObject downloadAd = new JSONObject();
                                            downloadAd.put("advertId", advertId);
                                            downloadAd.put("imgName", imageName);
                                            StringEntity se_adImage = new StringEntity(downloadAd.toString(), "utf-8");
                                            InputStream is_adImage = inputStreamPost(Global.URL_DOWNLOADIMG, se_adImage, cb);
                                            if (is_adImage != null) {
                                                (new File(Global.PATH)).mkdirs();
                                                inputstreamToFile(is_adImage, (new StringBuilder(String.valueOf(Global.AD_PATH))).append(imageName).toString());
                                                ad_info = new AdInfo();
                                                ad_info.setAd_id(advertId);
                                                if ("imgSName".equals(getImageName(Global.density)))
                                                    ad_info.setImag_name(imageName);
                                                if ("imgMName".equals(getImageName(Global.density)))
                                                    ad_info.setImag_name(imageName);
                                                if ("imgBName".equals(getImageName(Global.density)))
                                                    ad_info.setImag_name(imageName);
                                                ad_info.setAd_url(ad_url);
                                                ad_info.setAd_num(ad_num);
                                                ad_info.setAd_type(ad_type);
                                                ad_info.setAd_endtime(ad_endtime);
                                                DatabaseProvider.insertAdInfo(c, ad_info);
                                            }
                                        }
                                    }
                                }
                                if (cb != null)
                                    cb.onHttpRequestComplete(HTTP_REQUEST_SESULT_HEARTBEAT, 1, null);
                            }
                        }


                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_SESULT_HEARTBEAT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_SESULT_HEARTBEAT);
                    }
                }
//                if (unable_network)
//                    sWorker.postDelayed(this, heartTime * 1000);
                LogUtil.e(TAG, "sleep");
            }

        };
        sWorker.post(r);
    }

    private void deleteFiles() {
        File f_dir = new File(Global.AD_PATH);

        if (f_dir.exists()) {
            File[] subFile = f_dir.listFiles();
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                if (!subFile[iFileLength].isDirectory()) {
                    subFile[iFileLength].delete();
                }
            }

        }
    }

    public static String getImageName(float density) {
        if (density > 0.0 && density <= 0.8) {
            return "imgSName";
        }
        if (density > 0.8 && density < 1.5) {
            return "imgMName";
        }
        if (density >= 1.5) {
            return "imgBName";
        }
        return "imageBName";
    }

    public void getTotalSportsInfo(final Context c, String imei, String projectName, String start, String end, final HttpCallback cb) {
        getHttpClient(c);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei", imei);
            jsonObject.put("bg_proj", projectName);
            jsonObject.put("startTime", start);
            jsonObject.put("endTime", end);
//            jsonObject.put("imei", "450087946791");
//            jsonObject.put("bg_proj", "3070");
//            jsonObject.put("startTime", "2015-11-12 00:00:00");
//            jsonObject.put("endTime", "2015-11-12 23:59:59");
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_TOTALSPORTS_INFO);
            }
            return;
        }
        Runnable r = new Runnable() {
            public ArrayList<JSONObject> list = new ArrayList<JSONObject>();

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_GETTOTALSPORTS, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        LogUtil.e(TAG, "json= " + json);
                        is.close();
                        int result = 0;
                        if (json != null) {
                            JSONObject resultJson = new JSONObject(json);
                            JSONArray sportsInfo = (JSONArray) resultJson.getJSONArray("sportInfoList").get(0);
                            for (int i = 0; i < sportsInfo.length(); i++) {
                                JSONObject sportList = sportsInfo.getJSONObject(i);
                                LogUtil.e(TAG, "sportList= " + sportList);
                                list.add(sportList);
                            }

                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_TOTALSPORTS_INFO, result, list);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_TOTALSPORTS_INFO);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_TOTALSPORTS_INFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void getSportDetail(final Context c, String imei, String projectName, String sportNum, final HttpCallback cb) {
        getHttpClient(c);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei", imei);
            jsonObject.put("bg_proj", projectName);
//            jsonObject.put("startTime", start);
//            jsonObject.put("endTime", end);
//            jsonObject.put("imei", "450087946791");
//            jsonObject.put("bg_proj", "3070");
            jsonObject.put("sportNum", sportNum);
            LogUtil.e(TAG, "sportNum= " + sportNum);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_DETAILSPORT_INFO);
            }
            return;
        }
        Runnable r = new Runnable() {


            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_GETDETAILSPORT, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        LogUtil.e(TAG, "json= " + json);
                        is.close();
                        int result = 0;

                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_DETAILSPORT_INFO, result, json);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_DETAILSPORT_INFO);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_DETAILSPORT_INFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void getTotalSleepInfo(final Context c, String imei, String projectName, String start, String end, final HttpCallback cb) {
        getHttpClient(c);
        final JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("imei", imei);
//            jsonObject.put("bg_proj", projectName);
//            jsonObject.put("startTime", start);
//            jsonObject.put("endTime", end);
            jsonObject.put("method", "loadTotalSleepRecorder");
            jsonObject.put("imei", "410307000000000");
            jsonObject.put("bg_long", "3070");
            jsonObject.put("startTime", "2015-11-12 00:00:00");
            jsonObject.put("endTime", "2015-11-13 23:59:59");
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_TOTALSLEEP_INFO);
            }
            return;
        }
        Runnable r = new Runnable() {
            public ArrayList<JSONObject> list = new ArrayList<JSONObject>();

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_GETSLEEPINFO, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        LogUtil.e(TAG, "json= " + json);
                        is.close();
                        int result = 0;
                        if (json != null) {
                            JSONObject resultJson = new JSONObject(json);
                            JSONArray sleepInfo = (JSONArray) resultJson.getJSONArray("sleeplist").get(0);
                            for (int i = 0; i < sleepInfo.length(); i++) {
                                JSONObject sleepList = sleepInfo.getJSONObject(i);
                                LogUtil.e(TAG, "sleepList= " + sleepList);
                                list.add(sleepList);
                            }

                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_TOTALSLEEP_INFO, result, list);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_TOTALSLEEP_INFO);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_TOTALSLEEP_INFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void getSleepDetail(final Context c, String imei, String projectName, String sleepNum, final HttpCallback cb) {
        getHttpClient(c);
        final JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("imei", imei);
//            jsonObject.put("bg_proj", projectName);
//            jsonObject.put("startTime", start);
//            jsonObject.put("endTime", end);
            jsonObject.put("method", "loadDetailSleepRecorder");
            jsonObject.put("imei", "410307000000000");
            jsonObject.put("bg_long", "3070");
            jsonObject.put("sleepNum", sleepNum);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_DETAILSLEEP_INFO);
            }
            return;
        }
        Runnable r = new Runnable() {


            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_GETSLEEPINFO, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        LogUtil.e(TAG, "json= " + json);
                        is.close();
                        int result = 0;

                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_DETAILSLEEP_INFO, result, json);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_DETAILSLEEP_INFO);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_DETAILSLEEP_INFO);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void getTotalSportsStep(final Context c, String imei, String projectName, String start, String end, final HttpCallback cb) {
        getHttpClient(c);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei", imei);
            jsonObject.put("bg_proj", projectName);
            jsonObject.put("startTime", start);
            jsonObject.put("endTime", end);
//            jsonObject.put("imei", "450087946791");
//            jsonObject.put("bg_proj", "3070");
//            jsonObject.put("startTime", "2015-11-12 00:00:00");
//            jsonObject.put("endTime", "2015-11-12 23:59:59");
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_TOTALSPORTS_STEP);
            }
            return;
        }
        Runnable r = new Runnable() {
            public ArrayList<JSONObject> list = new ArrayList<JSONObject>();

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_GETTOTALSTEP, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        LogUtil.e(TAG, "json= " + json);
                        is.close();
                        int result = 0;

                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_TOTALSPORTS_STEP, result, json);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_TOTALSPORTS_STEP);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_TOTALSPORTS_STEP);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    public void getSoftwareVersion(final Context c, String sex, String number,
                                   final String lon, final String lat, final HttpCallback cb) {
        getHttpClient(c);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bg_long", "3070");
            jsonObject.put("userSex", sex);
            jsonObject.put("phoneNum", number);
            jsonObject.put("osType", "1");
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
        } catch (Exception e) {
            if (cb != null) {
                cb.onError(HTTP_REQUEST_DOWNLOAD_VERSION);
            }
            return;
        }
        Runnable r = new Runnable() {
            public ArrayList<JSONObject> list = new ArrayList<JSONObject>();

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringEntity se = new StringEntity(jsonObject.toString(), "utf-8");
                    InputStream is = inputStreamPost(Global.URL_HEARTBEAT, se, cb);
                    if (is != null) {
                        String json = inputstreamToStr(is);
                        LogUtil.e(TAG, "json= " + json);
                        is.close();
                        int result = 0;
                        String version = null;
                        if (json != null) {
                            JSONObject json_version = new JSONObject(json);
                            version = json_version.getString("currVer");
                            LogUtil.e(TAG, "version= " + version);
                        }
                        if (cb != null)
                            cb.onHttpRequestComplete(HTTP_REQUEST_DOWNLOAD_VERSION, result, version);
                    } else {
                        if (cb != null)
                            cb.onError(HTTP_REQUEST_DOWNLOAD_VERSION);
                    }
                } catch (Exception e) {
                    if (cb != null) {
                        cb.onError(HTTP_REQUEST_DOWNLOAD_VERSION);
                    }
                }
            }
        };
        sWorker.post(r);
    }

    //add by lixiang for ad 20151112 end

    public interface HttpCallback {

        public void onHttpRequestComplete(int what, int result, Object object);

        public void onError(int what);
    }
}
