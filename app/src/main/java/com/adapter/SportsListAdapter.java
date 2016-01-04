package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gomtel.util.Global;
import com.gomtel.util.LogUtil;
import com.mtk.btnotification.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by lixiang on 15-11-26.
 */
public class SportsListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private ArrayList<JSONObject> mList;
    public static final double HEIGHT_PARAM = 0.37;
    public static final double CAL_PARAM = 0.069;
    private int height = 170;
    private static final String[] sports = {"NULL", "跑步", "慢走", "骑行", "游泳", "爬楼梯"};
    private long totalstep = 0;

    public SportsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<JSONObject>();
    }

    public void addAll(ArrayList<JSONObject> mTempList) {
        mList.addAll(mTempList);
    }
    public long getTotalstep(){
        return  totalstep;
    }

    public String getSportNum(int position) {
        String temp = null;
        try {
            temp = ((JSONObject) getItem(position)).getString("sportNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.sport_item, parent, false);
            viewHolder.sports = (TextView) convertView.findViewById(R.id.sport);
            viewHolder.burn = (TextView) convertView.findViewById(R.id.burn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        long temp_sports = 0;
        String temp_starttime = null;
        String temp_endtime = null;
        long temp_burn = 0;
        int sportType = 0;
        try {
//            temp_sports = ((JSONObject)getItem(position)).getString("startTime");
            temp_sports = ((JSONObject) getItem(position)).getLong("qty");
            temp_starttime = ((JSONObject) getItem(position)).getString("startTime");
            temp_endtime = ((JSONObject) getItem(position)).getString("endTime");
            sportType = ((JSONObject) getItem(position)).getInt("sportType");
            viewHolder.sportNum = ((JSONObject) getItem(position)).getString("sportNum");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String item = null;
        try {
            item = "于" + Global.sdf_5.format(Global.sdf_1.parse(temp_starttime)) + "至" + Global.sdf_5.format(Global.sdf_1.parse(temp_endtime)) + sports[sportType] + caculateDistance(temp_sports);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.sports.setText(item);
        viewHolder.burn.setText(caculateBurn(temp_sports));

        return convertView;
    }

//    private String getSportTime(String temp_starttime, String temp_endtime) {
//        long time = temp_starttime.split(":")[1]
//        return null;
//    }

    private String convertTime(String time) {
        String temp = time.split(":")[0] + ":" + time.split(":")[1];
        return temp;
    }

    private String caculateDistance(long temp_sports) {
        double num_distance = (temp_sports * height * HEIGHT_PARAM / 100);
        return String.valueOf(Global.df_1_1.format(num_distance)) + " 米";
    }
    private String caculateBurn(long temp_sports) {
        double num_distance = (temp_sports * height * HEIGHT_PARAM / 100000);
        double num_calories = (num_distance * CAL_PARAM * 1000);
        return "大约消耗" + String.valueOf(Global.df_1_1.format(num_calories)) + " 千卡";
    }

    private class ViewHolder {
        TextView sports;
        TextView burn;
        String sportNum;
    }
}
