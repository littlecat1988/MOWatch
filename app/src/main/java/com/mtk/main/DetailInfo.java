package com.mtk.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.SportsListAdapter;
import com.gomtel.util.BarChart;
import com.gomtel.util.DialogHelper;
import com.gomtel.util.Global;
import com.gomtel.util.HomeChart;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.LogUtil;
import com.gomtel.util.PositionInfo;
import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import care.application.XcmApplication;

/**
 * Created by lixiang on 15-11-30.
 */
public class DetailInfo extends Activity implements HttpUtils.HttpCallback {
    private static final String TAG = "DetailInfo";
    public static final String POSITION_LIST = "POSITION_LIST";
    private LinearLayout chart_detail;
    private TextView distance_num;
    private TextView step_num;
    private TextView burn_num;
    private TextView distance_run_num;
    private TextView time_run_num;
    private TextView burn_run_num;
    private TextView distance_walk_num;
    private TextView time_walk_num;
    private TextView burn_walk_num;
    private TextView location;
    private HttpUtils mHttpUtils;
    private Calendar mCalendar;
    private SharedPreferences mPrefs;
    private ArrayList<JSONObject> listOfSport = new ArrayList<JSONObject>();
    private ArrayList<PositionInfo> listOfPosition = new ArrayList<PositionInfo>();
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        handleData(msg.obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }


//			list.clear();
        }

    };
    private long step;
    private int Height;
    private long step_run;
    private long step_walk;
    private long time_run;
    private long time_walk;


    private void handleData(Object obj) throws JSONException {
        String json = (String) obj;
        if (json != null) {

            JSONObject resultJson = new JSONObject(json);
            if (resultJson.has("sportInfoList")) {
                JSONArray sportsInfo = (JSONArray) resultJson.getJSONArray("sportInfoList").get(0);
                for (int i = 0; i < sportsInfo.length(); i++) {
                    JSONObject sportList = sportsInfo.getJSONObject(i);
                    LogUtil.e(TAG, "sportInfoList= " + sportList);
                    listOfSport.add(sportList);
                }
            }
            LogUtil.e(TAG, "positionList= " + resultJson.length());
            if (resultJson.has("positionList")) {
                JSONArray positionInfos = (JSONArray) resultJson.getJSONArray("positionList").get(0);
                for (int i = 0; i < positionInfos.length(); i++) {
                    PositionInfo position_info = new PositionInfo();
                    JSONObject positionInfo = positionInfos.getJSONObject(i);
                    LogUtil.e(TAG, "positionInfo= " + positionInfo);
                    position_info.setLat(Double.parseDouble(positionInfo.getString("lat")));
                    position_info.setLon(Double.parseDouble(positionInfo.getString("lon")));
                    listOfPosition.add(position_info);
                }
            }

        }

        chart_detail.addView(new BarChart().getBarChartGraphicalView(this, listOfSport),
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        setData();
    }

    private void setData() {
        step_num.setText(getStep());
        distance_num.setText(getDistance(step)+getResources().getString(R.string.distance_unit));
        burn_num.setText(getBurn(step)+getResources().getString(R.string.calories_unit));
        distance_run_num.setText(getDistance(step_run)+getResources().getString(R.string.distance_unit));
        burn_run_num.setText(getBurn(step_run)+getResources().getString(R.string.calories_unit));
        distance_walk_num.setText(getDistance(step_walk)+getResources().getString(R.string.distance_unit));
        burn_walk_num.setText(getBurn(step_walk)+getResources().getString(R.string.calories_unit));
        time_run_num.setText(Global.df_1_1.format(time_run / Global.HOUR)+getResources().getString(R.string.min));
        time_walk_num.setText(Global.df_1_1.format(time_walk / Global.HOUR)+getResources().getString(R.string.min));
    }

    private String getBurn(long stepNum) {
        String num_calories = Global.df_1.format(stepNum * Height * Global.HEIGHT_PARAM / 100 * Global.CAL_PARAM);
        return num_calories;
    }

    private String getDistance(long stepNum) {
        String distance = Global.df_3.format(stepNum * Height * Global.HEIGHT_PARAM / 100000);
        return distance;
    }

    private String getStep() {
        int type = 0;
        for (JSONObject sport : listOfSport) {
            try {
                type = sport.getInt(Global.SPORT_TYPE);
                if (type == 1) {
                    step_run += Long.parseLong(sport.getString(Global.SPORT_STEP));
                    try {
                        time_run += Global.sdf_1.parse(sport.getString(Global.SPORT_ENDTIME)).getTime() - Global.sdf_1.parse(sport.getString(Global.SPORT_STARTTIME)).getTime() ;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (type == 2) {
                    step_walk += Long.parseLong(sport.getString(Global.SPORT_STEP));
                    try {
                        time_walk += Global.sdf_1.parse(sport.getString(Global.SPORT_ENDTIME)).getTime() - Global.sdf_1.parse(sport.getString(Global.SPORT_STARTTIME)).getTime() ;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        step = step_run + step_walk;
        return String.valueOf(step);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info);
        if (mPrefs == null)
            mPrefs = getSharedPreferences("WATCH", 0);
        Height = Integer.parseInt(mPrefs.getString(Global.HEIGHT,"170"));
        mCalendar = Calendar.getInstance();
        String sportNum = getIntent().getStringExtra(SportActivity.SPORT_NUM);
        String imei = getIntent().getStringExtra(SportActivity.DEVICE_IMEI);
        mHttpUtils = HttpUtils.getInstance();
        mHttpUtils.getSportDetail(this, imei, XcmApplication.getInstance().gettMetaData(), sportNum, this);
        initView();

    }

    private void initView() {
        chart_detail = (LinearLayout) findViewById(R.id.chart_detail);
        distance_num = (TextView) findViewById(R.id.distance_num);
        step_num = (TextView) findViewById(R.id.step_num);
        burn_num = (TextView) findViewById(R.id.burn_num);
        distance_run_num = (TextView) findViewById(R.id.distance_run_num);
        time_run_num = (TextView) findViewById(R.id.time_run_num);
        burn_run_num = (TextView) findViewById(R.id.burn_run_num);
        distance_walk_num = (TextView) findViewById(R.id.distance_walk_num);
        time_walk_num = (TextView) findViewById(R.id.time_walk_num);
        burn_walk_num = (TextView) findViewById(R.id.burn_walk_num);
        location = (TextView) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailInfo.this, LocationPage.class);
                intent.putExtra(POSITION_LIST, listOfPosition);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        switch (what) {
            case HttpUtils.HTTP_REQUEST_DETAILSPORT_INFO:
                Message msg = new Message();
                msg.what = 0;
                msg.obj = object;
                mHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int what) {

    }
}
