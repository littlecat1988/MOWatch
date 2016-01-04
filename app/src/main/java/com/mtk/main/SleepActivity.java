package com.mtk.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.SleepListAdapter;
import com.adapter.SportsListAdapter;
import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.SleepDay;
import com.mtk.btnotification.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import care.utils.XcmTools;

public class SleepActivity extends Activity implements HttpUtils.HttpCallback {

    public static final String mFormat = "HH:mm";// h:mm
    public static final String DATA_SLEEP_HISTORY = "data_sleep_history";
    public static final String DATA_SLEEP_LIST = "data_sleep_list";
    protected static final String TAG = "SleepActivity";
    public static final String DATA_SLEEP_STATUS = "data_sleep_status";
    public static final String DATA_SLEEP_CURRENT_STATUS = "data_sleep_current_status";
    public static final String SLEEP_NUM = "SLEEP_NUM";
    public static final String DEVICE_IMEI = "DEVICE_IMEI";
    public static final String START_TIME = "START_TIME";
    public static final String END_TIME = "END_TIME";
    public static final String TOTAL_TIME = "total_time";
    public static final String DEEP_SLEEP = "DEEP_SLEEP";
    public static final String LIGHT_SLEEP = "LIGHT_SLEEP";
    public static final String NO_SLEEP = "NO_SLEEP";
    public static final String NO_SLEEP_LONG = "NO_SLEEP_LONG";
    public static final String DEEP_SLEEP_LONG = "DEEP_SLEEP_LONG";

    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<SleepDay> list;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    initSleepList(msg.obj);
                    break;
                case 1:
                    break;

                default:
                    break;
            }
        }

    };
    private int list_size;

    private void initSleepList(Object obj) {
        ArrayList<JSONObject> sleepList = (ArrayList<JSONObject>) obj;
        list_size = sleepList.size();
        SleepListAdapter adapter = new SleepListAdapter(this);
        adapter.addAll(sleepList);
        list_sleep.setAdapter(adapter);
        list_sleep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SleepActivity.this, SleepDetailInfo.class);
                intent.putExtra(SLEEP_NUM, ((SleepListAdapter) parent.getAdapter()).getSleepNum(position));
                intent.putExtra(START_TIME, ((SleepListAdapter) parent.getAdapter()).getStartTime(position));
                intent.putExtra(END_TIME, ((SleepListAdapter) parent.getAdapter()).getEndTime(position));
                intent.putExtra(TOTAL_TIME, ((SleepListAdapter) parent.getAdapter()).getTotalSleep(position));
                intent.putExtra(DEEP_SLEEP, ((SleepListAdapter) parent.getAdapter()).getDeepSleep(position));
                intent.putExtra(NO_SLEEP, ((SleepListAdapter) parent.getAdapter()).getNoSleep(position));
                intent.putExtra(LIGHT_SLEEP, ((SleepListAdapter) parent.getAdapter()).getLightSleep(position));
                intent.putExtra(DEVICE_IMEI, tools.get_current_device_id());
//                intent.putExtra(DEEP_SLEEP_LONG, ((SleepListAdapter) parent.getAdapter()).getDeepSleepInt(position));
//                intent.putExtra(NO_SLEEP_LONG, ((SleepListAdapter) parent.getAdapter()).getNoSleepInt(position));
                startActivity(intent);
            }
        });
        time_sleep.setText(adapter.getDeepSleep(0));
        sleep_num.setText(adapter.getTotalSleep(0));
    }

    private HttpUtils mHttpUtils;
    private XcmTools tools;
    private ListView list_sleep;
    private TextView sleep_num;
    private TextView time_sleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        initUI();
        if (mHttpUtils == null)
            mHttpUtils = HttpUtils.getInstance();
        tools = new XcmTools(this);
        mHttpUtils.getTotalSleepInfo(this, tools.get_current_device_id(), "3070", Global.sdf_2.format(mCalendar.getTime()) + Global.STARTTIME, Global.sdf_2.format(mCalendar.getTime()) + Global.ENDTIME, this);
    }


    private String minsToHours(long mins) {
        if (mins < 60) {
            return Global.df_1_1.format(mins)
                    + getResources().getString(R.string.min);
        } else {
            String hour = Global.df_1_1.format(mins / 60);
            String min = Global.df_1_1.format(mins % 60);
            return hour + getResources().getString(R.string.hour) + min
                    + getResources().getString(R.string.min);
        }

    }


    private void initUI() {
        // TODO Auto-generated method stub
        list_sleep = (ListView) findViewById(R.id.list_sleep);
        sleep_num = (TextView) findViewById(R.id.sleep_num);
        time_sleep = (TextView) findViewById(R.id.time_sleep);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        switch (what) {
            case HttpUtils.HTTP_REQUEST_TOTALSLEEP_INFO:
                Message msg = new Message();
                msg.what = 0;
                msg.obj = object;
                mHandler.sendMessage(msg);
                break;
        }

    }

    @Override
    public void onError(int what) {

    }
}
