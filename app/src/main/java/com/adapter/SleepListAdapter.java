package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gomtel.util.Global;
import com.mtk.btnotification.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lixiang on 15-11-26.
 */
public class SleepListAdapter extends BaseAdapter {

    private static final long HOUR = 1000 * 60 * 60;
    private static final long MIN = 1000 * 60;
    private final LayoutInflater mInflater;
    private ArrayList<JSONObject> mList;
    public static final double HEIGHT_PARAM = 0.37;
    public static final double CAL_PARAM = 0.069;
    private int height = 170;
    private static final String[] sports = {"NULL", "清醒", "深度睡眠", "浅度睡眠"};
    private long totalstep = 0;
    private long total_sleep;

    public SleepListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<JSONObject>();
    }

    public void addAll(ArrayList<JSONObject> mTempList) {
        mList.addAll(mTempList);
        Collections.reverse(mList);
    }


    public String getSleepNum(int position) {
        String temp = null;
        try {
            temp = ((JSONObject) getItem(position)).getString("sleepNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public String getStartTime(int position) {
        String temp = null;
        try {
            temp = ((JSONObject) getItem(position)).getString("starttime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public String getEndTime(int position) {
        String temp = null;
        try {
            temp = ((JSONObject) getItem(position)).getString("endtime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }
    public String getDeepSleep(int position) {
        String temp = null;
        try {
            temp = convertTime(Long.parseLong(((JSONObject) getItem(position)).getString("deepsleep")) * MIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public String getNoSleep(int position) {
        String temp = null;
        try {
            temp = convertTime(Long.parseLong(((JSONObject) getItem(position)).getString("noSleep"))*MIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public String getLightSleep(int position) {
        String temp = null;
        try {
            temp = convertTime(total_sleep-Long.parseLong(((JSONObject) getItem(position)).getString("noSleep"))*MIN-Long.parseLong(((JSONObject) getItem(position)).getString("deepsleep")) * MIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }
    public String getTotalSleep(int position) {
        String temp = null;
        try {
            total_sleep = Global.sdf_1.parse(((JSONObject) getItem(position)).getString("endtime")).getTime() - Global.sdf_1.parse(((JSONObject) getItem(position)).getString("starttime")).getTime();
            temp = convertTime(total_sleep);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
            convertView = mInflater.inflate(R.layout.sleep_item, parent, false);
            viewHolder.sleep = (TextView) convertView.findViewById(R.id.sleep);
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
            temp_starttime = ((JSONObject) getItem(position)).getString("starttime");
            temp_endtime = ((JSONObject) getItem(position)).getString("endtime");
            viewHolder.deepSleep = ((JSONObject) getItem(position)).getString("deepsleep");
//            viewHolder.sleepNum = ((JSONObject) getItem(position)).getString("sleepNum");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String item = null;

        try {
            long sleep_time = Global.sdf_1.parse(temp_endtime).getTime() - Global.sdf_1.parse(temp_starttime).getTime();
            viewHolder.convert_time = convertTime(sleep_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            item = Global.sdf_1.format(Global.sdf_1.parse(temp_starttime)) + " 至 " + Global.sdf_1.format(Global.sdf_1.parse(temp_endtime)) + "\n共计睡眠" + viewHolder.convert_time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.sleep.setText(item);

        return convertView;
    }

    private String convertTime(long time) {
        int hour = (int) (time / HOUR);
        int min = (int) ((time - hour * HOUR) / MIN);
        return String.valueOf(hour) + "小时" + String.valueOf(min) + "分";
    }


    private class ViewHolder {
        TextView sleep;
        //        String sleepNum;
        String deepSleep;
        String convert_time;
    }
}
