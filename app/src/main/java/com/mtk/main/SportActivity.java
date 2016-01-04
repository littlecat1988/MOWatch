package com.mtk.main;

import java.util.ArrayList;
import java.util.Calendar;

import org.achartengine.GraphicalView;
import org.json.JSONException;
import org.json.JSONObject;

import com.adapter.SportsListAdapter;
import com.gomtel.database.DatabaseProvider;
import com.gomtel.util.DialogHelper;
import com.gomtel.util.Global;
import com.gomtel.util.GoalProgressbar;
import com.gomtel.util.HistoryDay;
import com.gomtel.util.HomeChart;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.LogUtil;
import com.gomtel.util.SportInfo;
import com.mediatek.wearable.WearableManager;
import com.mediatek.wearableProfiles.WearableClientProfile;
import com.mtk.bluetoothle.CustomizedBleClient;
import com.mtk.bluetoothle.CustomizedBleFeaturesIniter;
import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import care.utils.XcmTools;

public class SportActivity extends Activity implements HttpUtils.HttpCallback {

    public static final String DATA_STEP = "data_step";
    public static final String TIME = "time";
    protected static final String TAG = "SportActivity";
    public static final String DATA_TOTAL_STEP = "data_total_step";
    public static final String TOTAL_LIST = "total_list";
    public static final String TOTAL_STEP = "total_step";
    public static final String DATA_PERSON = "data_person";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String GOAL = "goal";
    public static final double HEIGHT_PARAM = 0.37;
    public static final double CAL_PARAM = 0.069;
    public static final String ENABLE_PEDOMETER = "enable_pedometer";
    public static final String ENABLE_SPORT = "enable_sport";
    //	public static final String START_SPORT = "start_sport";
    public static final String WALK_OR_RUN = "walk_or_run";
    public static final String STATE_SPORT = "state_sport";
    public static final String SPORT_NUM = "SPORT_NUM";
    public static final String DEVICE_IMEI = "DEVICE_IMEI";
    private TextView step;
    private BluetoothGatt gatt;
    private int time;
    private ArrayList<HistoryHour> list;
    int totalStep = 0;
    private Runnable runnable_sync = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "isShowing()= " + dialog_sync.isShowing());
            if (dialog_sync.isShowing()) {
                DialogHelper.dismissDialog(dialog_sync);
                Toast.makeText(SportActivity.this, getResources().getString(R.string.sync_failed_sport), Toast.LENGTH_SHORT).show();

            }
            mHandler.removeCallbacks(runnable_sync);
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    initTotalStep(msg.obj);
                    break;
                case 1:
//				calculateData();
                    break;
                case 2:
                    DialogHelper.dismissDialog(dialog_sync);
                    break;
                case 3:
                    initSportsList(msg.obj);
                    break;
                default:
                    break;
            }


//			list.clear();
        }

    };

    private void initTotalStep(Object obj) {
        String json = (String) obj;

        try {
            JSONObject jsonOfStep = new JSONObject(json);
            totalStep = Integer.valueOf(jsonOfStep.getString("totalTake"));
            step.setText(String.valueOf(totalStep));
            progress_goal_percent.setProgress((int) (totalStep / (goal * 10)));
            calculateData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getTotalstep(Object obj) {
        ArrayList<JSONObject> list = (ArrayList<JSONObject>) obj;
        int total = 0;
        for (JSONObject json : list) {
            try {
                int step_num = json.getInt("qty");
                int sport_type = json.getInt("sportType");
                if (sport_type == 1 || sport_type == 2) {
                    total += step_num;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return total;
    }

    private void initSportsList(Object obj) {
        ArrayList<JSONObject> sportsList = (ArrayList<JSONObject>) obj;
        SportsListAdapter adapter = new SportsListAdapter(this);
        adapter.addAll(sportsList);
        list_sports.setAdapter(adapter);
        list_sports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SportActivity.this, DetailInfo.class);
                intent.putExtra(SPORT_NUM, ((SportsListAdapter) parent.getAdapter()).getSportNum(position));
                intent.putExtra(DEVICE_IMEI, tools.get_current_device_id());
                startActivity(intent);
            }
        });
    }

    protected int height = 170;
    protected int weight = 50;
    protected int goal = 10;
    protected int walk_or_run;
    protected int enable_sport = -1;
    protected boolean data_completed;
    protected int enable_sucess;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Log.e(TAG, "action= " + action);
            if (action.equals(DATA_STEP)) {
                Log.e(TAG, "onReceive= " + intent.getIntExtra(TIME, 1));
                time = intent.getIntExtra(TIME, 1);
//                getStepData(time);
            }
            if (action.equals(DATA_TOTAL_STEP)) {
                list = (ArrayList<HistoryHour>) intent.getSerializableExtra(TOTAL_LIST);
                Log.e(TAG, "DATA_TOTAL_STEP= " + intent.getIntExtra(TOTAL_STEP, 0));
//                totalStep = intent.getIntExtra(TOTAL_STEP, 0);
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
//				getPersonData();
//                getSportState();
                DatabaseProvider.saveHistoryHour(SportActivity.this, list, 0);
            }
            if (action.equals(DATA_PERSON)) {
                Message msg = new Message();
                msg.what = 1;
                height = intent.getIntExtra(HEIGHT, 170);
                weight = intent.getIntExtra(WEIGHT, 70);
                goal = intent.getIntExtra(GOAL, 10);
//				data_completed = true;
                mHandler.sendMessage(msg);
            }
            if (action.equals(ENABLE_PEDOMETER)) {
                Message msg = new Message();
                msg.what = 2;
                enable_sucess = intent.getIntExtra(ENABLE_SPORT, 0);
                Log.e(TAG, "enable_sport= " + enable_sucess);
//				mHandler.sendMessage(msg);
            }
            if (action.equals(STATE_SPORT)) {
                Message msg = new Message();
                msg.what = 2;
                walk_or_run = intent.getIntExtra(WALK_OR_RUN, 0);
                enable_sport = intent.getIntExtra(ENABLE_SPORT, 0);
                data_completed = true;
                mHandler.sendMessage(msg);
            }
        }
    };
    private TextView distance;
    private TextView calories;
    private TextView walk;
    private LinearLayout chart_show24;
    private HomeChart homechartfor24;
    private GraphicalView chart_24;
    private TextView history;
    private OnClickListener myClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
                case R.id.history:
//				getHistoryData();
                    startActivity(new Intent(SportActivity.this, HistorySportActivity.class));
                    break;

                case R.id.step:
//				if(data_completed){
                    if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED) {
//                        getStepData(0);
                    }
//				}
                    break;
                default:
                    break;
            }
        }

    };
    private Calendar mClendar = Calendar.getInstance();
    private GoalProgressbar progress_goal_percent;
    private TextView open_sport;
    private RelativeLayout layout_circle;
    private HttpUtils mHttpUtils;
    private SharedPreferences mPrefs;
    private int userId;
    private double num_distance;
    private double num_calories;
    private ProgressDialog dialog_sync;
    private ListView list_sports;
    private XcmTools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport);
        // CustomizedBleFeaturesIniter.initBleClients();//20150707
        IntentFilter filter = new IntentFilter();
        filter.addAction(DATA_STEP);
        filter.addAction(DATA_TOTAL_STEP);
        filter.addAction(DATA_PERSON);
        filter.addAction(ENABLE_PEDOMETER);
        filter.addAction(STATE_SPORT);
        registerReceiver(mReceiver, filter);
        if (mHttpUtils == null)
            mHttpUtils = HttpUtils.getInstance();
        if (mPrefs == null)
            mPrefs = getSharedPreferences("WATCH", 0);
        userId = mPrefs.getInt("LOG_USERID", 0);
        tools = new XcmTools(this);
        initUI();
    }


    protected void calculateData() {
        // TODO Auto-generated method stub
        Log.e(TAG, "totalStep= " + totalStep);
        num_distance = (totalStep * height * HEIGHT_PARAM / 100000);
        num_calories = (num_distance * CAL_PARAM * 1000);
        distance.setText(String.valueOf(Global.df_2.format(num_distance)));
        calories.setText(String.valueOf(Global.df_1_1.format(num_calories)));
        DatabaseProvider.saveHistoryDay(SportActivity.this, totalStep, num_calories, num_distance, mClendar, 0);

//        SportInfo sportInfo = new SportInfo();
//        sportInfo.userid = userId;
//        sportInfo.step = totalStep;
//        sportInfo.date = Global.sdf_2.format(mClendar.getTime());
//        sportInfo.burn = (int) num_calories;
//        mHttpUtils.postSportInfo(this, sportInfo, this);
    }

//    protected void getHistoryData() {
//        // TODO Auto-generated method stub
//        byte[] arrayOfByte = new byte[2];
//        arrayOfByte[0] = 0x01;
//        arrayOfByte[1] = 0x0;
//        MainService.getInstance().writeCharacteristic(gatt,
//                MainService.UUID_SERVICE,
//                MainService.UUID_CHARACTERISTIC_WRITE_AND_READ, arrayOfByte);
//
//    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		initUI();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        mHttpUtils.getTotalSportsInfo(this, tools.get_current_device_id(), "3070", Global.sdf_2.format(mClendar.getTime()) + Global.STARTTIME, Global.sdf_2.format(mClendar.getTime()) + Global.ENDTIME, this);
        mHttpUtils.getTotalSportsStep(this, tools.get_current_device_id(), "3070", Global.sdf_2.format(mClendar.getTime()) + Global.STARTTIME, Global.sdf_2.format(mClendar.getTime()) + Global.ENDTIME, this);
        step = (TextView) findViewById(R.id.step);
        step.setOnClickListener(myClickListener);
        history = (TextView) findViewById(R.id.history);
        history.setOnClickListener(myClickListener);
//        walk = (TextView) findViewById(R.id.walk_or_run);
//        walk.setOnClickListener(myClickListener);
        open_sport = (TextView) findViewById(R.id.open_sport);
        open_sport.setOnClickListener(myClickListener);
        distance = (TextView) findViewById(R.id.distance);
        calories = (TextView) findViewById(R.id.calories);
        progress_goal_percent = (GoalProgressbar) findViewById(R.id.progress_goal_percent);
        list_sports = (ListView) findViewById(R.id.list_sports);
//		chart_show24 = (LinearLayout) findViewById(R.id.chart_show24);
//		homechartfor24 = new HomeChart();
//        chart_24 = homechartfor24.getHomeChartGraphicalView(this);
//        chart_show24.addView(chart_24,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
//        gatt = CustomizedBleClient.getGatt();
//        initPersonData();
//        if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED) {
////		showSyncDialog();
//            getStepData(0);
//        }
//        getHistoryStep();

    }

    private void getSportsInfo() {

    }

//    private void initPersonData() {
//        // TODO Auto-generated method stub
//        if (mPrefs == null)
//            mPrefs = getSharedPreferences("WATCH", 0);
//        height = mPrefs.getInt("HEIGHT", 170);
//        weight = mPrefs.getInt("WEIGHT", 70);
//    }

//    private void showSyncDialog() {
//        if (dialog_sync == null) {
//            dialog_sync = DialogHelper.showProgressDialog(this, getString(R.string.synchronizing));
//            this.dialog_sync.setCanceledOnTouchOutside(false);
//            this.dialog_sync.show();
//
//            return;
//        }
//        this.dialog_sync.show();
//    }
//
//    private void getHistoryStep() {
//        // TODO Auto-generated method stub
//        HistoryDay historyday = DatabaseProvider.queryHistoryDate(this, 0, mClendar);
//        if (historyday != null) {
//            totalStep = historyday.getStep();
//            num_distance = historyday.getDistance();
//            num_calories = historyday.getBurn();
//            step.setText(String.valueOf(totalStep));
//            distance.setText(String.valueOf(Global.df_2.format(num_distance)));
//            calories.setText(String.valueOf(Global.df_1_1.format(num_calories)));
//        }
//
//    }
//
//    private void getSportState() {
//        // TODO Auto-generated method stub
//        byte[] stateOfSport = new byte[1];
//        stateOfSport[0] = 0x06;
//        MainService.getInstance().writeCharacteristic(
//                CustomizedBleClient.getGatt(),
//                MainService.UUID_SERVICE,
//                MainService.UUID_CHARACTERISTIC_WRITE_AND_READ,
//                stateOfSport);
//    }
//
//    private void getStepData(int time) {
//        // TODO Auto-generated method stub
////		data_completed = false;
//        showSyncDialog();
//        if (time == 0) {
//            mHandler.postDelayed(runnable_sync, 30000);
//        }
//        byte[] arrayOfByte = new byte[2];
//        arrayOfByte[0] = 0x02;
//        arrayOfByte[1] = (byte) time;
//        MainService.getInstance().writeCharacteristic(gatt,
//                MainService.UUID_SERVICE,
//                MainService.UUID_CHARACTERISTIC_WRITE_AND_READ, arrayOfByte);
//
//    }

//    private void getPersonData() {
//        // TODO Auto-generated method stub
//        byte[] arrayOfByte = new byte[2];
//        arrayOfByte[0] = 0x05;
//        arrayOfByte[1] = 0;
//        MainService.getInstance().writeCharacteristic(gatt,
//                MainService.UUID_SERVICE,
//                MainService.UUID_CHARACTERISTIC_WRITE_AND_READ, arrayOfByte);
//
//    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
        DialogHelper.dismissDialog(dialog_sync);
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        // TODO Auto-generated method stub
        switch (what) {
            case HttpUtils.HTTP_REQUEST_SESULT_SPORTINFO:
                Log.e(TAG, "HTTP_REQUEST_SESULT_SPORTINFO= " + result);
                break;
            case HttpUtils.HTTP_REQUEST_TOTALSPORTS_INFO:
                Message msg = new Message();
                msg.what = 3;
                msg.obj = object;
                mHandler.sendMessage(msg);
                break;
            case HttpUtils.HTTP_REQUEST_TOTALSPORTS_STEP:
                Message msg_step = new Message();
                msg_step.what = 0;
                msg_step.obj = object;
                mHandler.sendMessage(msg_step);
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
