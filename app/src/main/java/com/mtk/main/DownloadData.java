package com.mtk.main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Iterator;

import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.MyCircleImageView;
import com.gomtel.util.SportInfo;
import com.mtk.btnotification.R;

/**
 * Created by lixiang on 15-5-7.
 */
public class DownloadData extends Activity implements HttpUtils.HttpCallback{
    public static final String TAG = "DownloadData";
    private MyCircleImageView image_portrait;
    private SharedPreferences mPrefs;
    private TextView text_name;
    private Typeface face_number;
    private TextView sum_day_title;
    private TextView sum_day;
    private TextView sum_distance_title;
    private TextView sum_cal_title;
    private TextView sum_distance;
    private TextView sum_cal;
    private int gender;
    private HttpUtils mHttpUtils;
    private View.OnClickListener myOnClickListener = new View.OnClickListener()
    {
        public void onClick(View paramView)
        {
            switch (paramView.getId()) {
                default:
                    break;
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getRemoteDataAndUpdate(msg.obj);
                    break;
                case 2:
                    updateDatabase();
                    break;
                default:
                    break;
            }
        }

    };

    private void updateDatabase() {

    }

    private int userId;

    private void getRemoteDataAndUpdate(Object object) {
        ArrayList<SportInfo> list = (ArrayList<SportInfo>) object;
        SportInfo mTotalInfo = null;
        if(list != null) {
            mTotalInfo = list.get(0);
        }
        if(mTotalInfo != null){
            sum_day.setText(mTotalInfo.totalDays+getResources().getString(R.string.days_cloud));
            sum_distance.setText(Global.df_2.format(mTotalInfo.totalSteps*0.7/1000)+getResources().getString(R.string.km_cloud));
            sum_cal.setText(mTotalInfo.totalBurns+getResources().getString(R.string.cal_cloud));
//            if(mPrefs == null){
//                mPrefs = getSharedPreferences("WATCH", 0);
//            }
//            SharedPreferences.Editor edit_cloud = mPrefs.edit();
//            edit_cloud.putInt("KEY_WRISTBAND_DAY",mTotalInfo.totalDays);
//            edit_cloud.putString("KEY_WRISTBAND_DISTANCE",Global.df_2.format(mTotalInfo.totalSteps*0.7/1000));
//            edit_cloud.putInt("KEY_WRISTBAND_CAL",mTotalInfo.totalBurns);
//            edit_cloud.commit();
        }
    }

    private TextView button_back;

    private void initUI()
    {
        image_portrait = (MyCircleImageView)findViewById(R.id.image_portrait);
        text_name = (TextView)findViewById(R.id.nick_name);
//        Log.e(TAG,"text_name0= "+text_name);
        sum_day_title = (TextView)findViewById(R.id.sum_day_title);
        sum_day = (TextView)findViewById(R.id.sum_day);
        sum_distance_title = (TextView)findViewById(R.id.sum_distance_title);
        sum_distance = (TextView)findViewById(R.id.sum_distance);
        sum_cal_title = (TextView)findViewById(R.id.sum_cal_title);
        sum_cal = (TextView)findViewById(R.id.sum_cal);
    }

    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.download_data);
        if(mPrefs == null){
            mPrefs = getSharedPreferences("WATCH", 0);
        }
//        gender = mPrefs.getInt("KEY_GENDER_GLOBAL", 0);
        userId = mPrefs.getInt("LOG_USERID",0);
      
        mHttpUtils = HttpUtils.getInstance();
        initUI();
        initData();
    }


    private void initData() {
        if(mPrefs == null){
            mPrefs = getSharedPreferences("WATCH", 0);
        }
        String NickName = mPrefs.getString("NICKNAME", "NAME");
        if ((NickName.equalsIgnoreCase("NAME"))) {
//        	Log.e(TAG,"text_name= "+text_name);
            text_name.setText("NAME");
        }else{
            text_name.setText(NickName);
        }
        mHttpUtils.loadSportTotal(DownloadData.this,userId,DownloadData.this);
        Bitmap localBitmap = BitmapFactory.decodeFile(Global.PATH + "head.jpg");
        if (localBitmap != null)
            image_portrait.setImageBitmap(localBitmap);

//        sum_day.setText(mPrefs.getInt("KEY_WRISTBAND_DAY",0)+getResources().getString(R.string.days_cloud));
//        sum_distance.setText(mPrefs.getString("KEY_WRISTBAND_DISTANCE","0")+getResources().getString(R.string.km_cloud));
//        sum_cal.setText(mPrefs.getInt("KEY_WRISTBAND_CAL",0)+getResources().getString(R.string.cal_cloud));

    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        switch(what){
           case HttpUtils.HTTP_REQUEST_SESULT_TOTAL:
               Log.e(TAG,"HTTP_REQUEST_SESULT_TOTAL = "+result);
               if(result == 1){
                   Message msg = new Message();
                   msg.what = 1;
                   msg.obj = object;
//                   Log.e(TAG,"object= "+object);
                   mHandler.sendMessage(msg);
               }
               break;
           case HttpUtils.HTTP_REQUEST_SESULT_RECORDER:
               if(result == 1){
                   Message msg = new Message();
                   msg.what = 2;
                   msg.obj = object;
                   mHandler.sendMessage(msg);
               }
               break;
        }
    }

    @Override
    public void onError(int what) {

    }
}
