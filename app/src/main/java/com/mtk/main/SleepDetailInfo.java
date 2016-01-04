package com.mtk.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.LogUtil;
import com.gomtel.util.SleepChart;
import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import care.application.XcmApplication;
import care.utils.XcmTools;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by lixiang on 15-12-11.
 */
public class SleepDetailInfo extends Activity implements HttpUtils.HttpCallback {
    private static final String TAG = "SleepDetailInfo";
    private static final long HOUR = 1000 * 60 * 60;
    private static final long SEC = 1000;
    private static final long MIN = 1000 * 60;
    private String imei;
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
                case 1:
                    getSleepDetail(msg.obj);

                    break;
                default:
                    break;
            }


//			list.clear();
        }

    };
    private long deep_sleep_long;
    private long no_sleep_long;
    private long total_time_long;
    private long light_sleep_long;

    private void getSleepDetail(Object obj) {
        ArrayList<JSONObject> sleepList = (ArrayList<JSONObject>) obj;
        int listSize = sleepList.size();
        if (listSize > 0) {
            JSONObject jsonObject = sleepList.get(listSize - 1);
            try {
                start_time = jsonObject.getString("starttime");
                end_time = jsonObject.getString("endtime");
                total_time_long = Global.sdf_1.parse(jsonObject.getString("endtime")).getTime() - Global.sdf_1.parse(jsonObject.getString("starttime")).getTime();
                total_time = convertTime(total_time_long);
                deep_sleep_long = Long.parseLong(jsonObject.getString("deepsleep")) * SEC;
                deep_sleep = convertTime(deep_sleep_long);
                no_sleep_long = Long.parseLong(jsonObject.getString("noSleep")) * SEC;
                no_sleep = convertTime(no_sleep_long);
                light_sleep_long = total_time_long - deep_sleep_long - no_sleep_long;
                light_sleep = convertTime(light_sleep_long);
                mHttpUtils.getSleepDetail(this, imei, XcmApplication.getInstance().gettMetaData(), jsonObject.getString("sleepNum"), this);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertTime(long time) {
        int hour = (int) (time / HOUR);
        int min = (int) ((time - hour * HOUR) / MIN);
        return String.valueOf(hour) + "小时" + String.valueOf(min) + "分";
    }

    private HttpUtils mHttpUtils;
    private TextView deep_sleep_time;
    private TextView light_sleep_time;
    private TextView wake_time;
    private TextView start_sleep_time;
    private TextView stop_sleep_time;
    private TextView total_sleep_time;
    private LinearLayout chart_sleep_detail;
    private ArrayList<JSONObject> listOfSleep = new ArrayList<JSONObject>();
    private String start_time;
    private String end_time;
    private String total_time;
    private String light_sleep;
    private String deep_sleep;
    private String no_sleep;
    private ColumnChartView chart;
//    private boolean hasLabels = true;
//    private boolean hasLabelForSelected = true;
//    private ColumnChartData data;
//    private boolean hasAxes = true;
//    private boolean hasAxesNames = false;
    private SleepChart chart_sleep;
    private XcmTools tools;
    private Calendar mCalendar = Calendar.getInstance();

    private void handleData(Object obj) throws JSONException {
        String json = (String) obj;
        if (json != null) {
            JSONObject resultJson = new JSONObject(json);
            JSONArray sleepInfo = (JSONArray) resultJson.getJSONArray("sleeplist").get(0);
            for (int i = 0; i < sleepInfo.length(); i++) {
                JSONObject sleepList = sleepInfo.getJSONObject(i);
                LogUtil.e(TAG, "sleepList= " + sleepList);
                listOfSleep.add(sleepList);
            }
        }

        setData();
//            generateData();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        chart_sleep.setupChart(listOfSleep, deep_sleep_long + no_sleep_long,dm.density);
        chart_sleep.invalidate();

    }

//    private void generateData() throws JSONException, ParseException {
//        List<Column> columns = new ArrayList<Column>();
//        List<SubcolumnValue> values;
//        for (JSONObject json : listOfSleep) {
//            values = new ArrayList<SubcolumnValue>();
//            String type = json.getString("type");
//            float temp_time = (Global.sdf_1.parse(json.getString("endtime")).getTime() - Global.sdf_1.parse(json.getString("starttime")).getTime()) / Global.MIN;
//            if ("1".equals(type)) {
//                values.add(new SubcolumnValue(temp_time, getResources().getColor(R.color.light_blue)).setLabel("清醒"));
//            }
//            if ("2".equals(type)) {
//                values.add(new SubcolumnValue(temp_time, Color.GREEN).setLabel("深度睡眠"));
//            }
//            Column column = new Column(values);
//            column.setHasLabels(hasLabels);
//            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
//            columns.add(column);
//        }
//        data = new ColumnChartData(columns);
//
//        if (hasAxes) {
//            Axis axisX = new Axis();
//            Axis axisY = new Axis().setHasLines(true);
//            if (hasAxesNames) {
//                axisX.setName("Axis X");
//                axisY.setName("Axis Y");
//            }
//            data.setAxisXBottom(null);
//            data.setAxisYLeft(axisY);
//        } else {
//            data.setAxisXBottom(null);
//            data.setAxisYLeft(null);
//        }
//
//        chart.setColumnChartData(data);
//    }

    private void setData() {

        try {
            start_sleep_time.setText(Global.sdf_5.format(Global.sdf_1.parse(start_time)));
            stop_sleep_time.setText(Global.sdf_5.format(Global.sdf_1.parse(end_time)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        total_sleep_time.setText(total_time);
        deep_sleep_time.setText(deep_sleep);
        light_sleep_time.setText(light_sleep);
        wake_time.setText(no_sleep);
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        switch (what) {
            case HttpUtils.HTTP_REQUEST_DETAILSLEEP_INFO:
                Message msg = new Message();
                msg.what = 0;
                msg.obj = object;
                mHandler.sendMessage(msg);
                break;
            case HttpUtils.HTTP_REQUEST_TOTALSLEEP_INFO:
                Message msg_total = new Message();
                msg_total.what = 1;
                msg_total.obj = object;
                mHandler.sendMessage(msg_total);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_detail_info);
//        String sleepNum = getIntent().getStringExtra(SleepActivity.SLEEP_NUM);
//        String imei = getIntent().getStringExtra(SleepActivity.DEVICE_IMEI);
//        start_time = getIntent().getStringExtra(SleepActivity.START_TIME);
//        end_time = getIntent().getStringExtra(SleepActivity.END_TIME);
//        total_time = getIntent().getStringExtra(SleepActivity.TOTAL_TIME);
//        deep_sleep = getIntent().getStringExtra(SleepActivity.DEEP_SLEEP);
//        light_sleep = getIntent().getStringExtra(SleepActivity.LIGHT_SLEEP);
//        no_sleep = getIntent().getStringExtra(SleepActivity.NO_SLEEP);
        mHttpUtils = HttpUtils.getInstance();
        tools = new XcmTools(this);
        mHttpUtils.getTotalSleepInfo(this, tools.get_current_device_id(), "3070", Global.sdf_2.format(mCalendar.getTime()) + Global.STARTTIME, Global.sdf_2.format(mCalendar.getTime()) + Global.ENDTIME, this);

        initView();
    }

    private void initView() {
        deep_sleep_time = (TextView) findViewById(R.id.deep_sleep_time);
        light_sleep_time = (TextView) findViewById(R.id.light_sleep_time);
        wake_time = (TextView) findViewById(R.id.wake_time);
        start_sleep_time = (TextView) findViewById(R.id.start_sleep_time);
        stop_sleep_time = (TextView) findViewById(R.id.stop_sleep_time);
        total_sleep_time = (TextView) findViewById(R.id.total_sleep_time);
//        chart_sleep_detail = (LinearLayout) findViewById(R.id.chart);
//        chart = (ColumnChartView) findViewById(R.id.chart);
//        chart.setOnValueTouchListener(new ValueTouchListener());
        chart_sleep = (SleepChart) findViewById(R.id.chart_sleep);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class ValueTouchListener implements ColumnChartOnValueSelectListener {
        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(SleepDetailInfo.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }
}
