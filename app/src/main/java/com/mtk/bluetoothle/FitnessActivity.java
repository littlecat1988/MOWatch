
package com.mtk.bluetoothle;

import com.mediatek.leprofiles.pdms.PDMSClientProxy;
import com.mtk.btnotification.R;
import com.mtk.main.MainService;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class FitnessActivity extends ListActivity {

    private final static String TAG = "[Fit]FitnessActivity";

    private FitDataAdapter mFitDataAdapter;

    private static Bitmap sSleepBitmap;

    private static Bitmap sSportBitmap;

    private static Bitmap sHRBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "FitnessActivity onCreate");
        super.onCreate(savedInstanceState);

        sSleepBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sleep);
        sSportBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sport);
        sHRBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heartrate);

        MainService.getInstance().setFitnessUIInterface(mFitnessUIInterface);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainService.getInstance().clearFitnessUIInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Initializes list view adapter.
        mFitDataAdapter = new FitDataAdapter();
        setListAdapter(mFitDataAdapter);
    }

    // Adapter for holding devices found through scanning.
    private class FitDataAdapter extends BaseAdapter {
        private ArrayList<FitData> mFitDatas;

        private LayoutInflater mInflator;

        public FitDataAdapter() {
            super();
            mFitDatas = new ArrayList<FitData>();
            mInflator = FitnessActivity.this.getLayoutInflater();
        }

        public void addFitData(FitData data) {
            if (!mFitDatas.contains(data)) {
                mFitDatas.add(data);
            }
        }

        public FitData getData(int position) {
            return mFitDatas.get(position);
        }

        public void clear() {
            Log.d(TAG, "clear begin");
            mFitDatas.clear();
            mFitDataAdapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFitDatas.size();
        }

        @Override
        public Object getItem(int i) {
            return mFitDatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            Log.d(TAG, "getView");
            if (view == null) {
                view = mInflator.inflate(R.layout.fitdata_list, null);
                viewHolder = new ViewHolder();
                viewHolder.mImageView = (ImageView) view.findViewById(R.id.fit_icon);
                viewHolder.mTimeView = (TextView) view.findViewById(R.id.time_stamp);
                viewHolder.mFitdataView = (TextView) view.findViewById(R.id.fit_data);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            FitData fitData = i < mFitDatas.size() ? mFitDatas.get(i) : null;
            if (fitData != null) {
                if (fitData.mFitType == FitData.FIT_TYPE_SLEEP) {
                    viewHolder.mImageView.setImageBitmap(sSleepBitmap);
                } else if (fitData.mFitType == FitData.FIT_TYPE_SPORT) {
                    viewHolder.mImageView.setImageBitmap(sSportBitmap);
                } else if (fitData.mFitType == FitData.FIT_TYPE_HR) {
                    viewHolder.mImageView.setImageBitmap(sHRBitmap);
                }
                viewHolder.mTimeView.setText(fitData.mTimeStamp);
                viewHolder.mFitdataView.setText(fitData.mFitData);
            } else {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
                long time = System.currentTimeMillis();
                viewHolder.mTimeView.setText(dateFormat.format(time));
                viewHolder.mFitdataView.setText("FitData = null");
            }

            return view;
        }
    }

    private static class FitData {

        static final int FIT_TYPE_SLEEP = 0;

        static final int FIT_TYPE_SPORT = 1;

        static final int FIT_TYPE_HR = 20;

        int mFitType;

        String mFitData;

        String mTimeStamp;
    }

    static class ViewHolder {
        ImageView mImageView;

        TextView mTimeView;

        TextView mFitdataView;
    }

    // register WearableListener
    private FitnessUIInterface mFitnessUIInterface = new FitnessUIInterface() {
        @Override
        public void onSleepNotify(final long startTime, final long endTime, final int sleepMode) {
            Log.d(TAG, "[onSleepNotify] startTime=" + startTime + " endTime" + endTime
                    + " sleepMode=" + sleepMode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FitData fitData = new FitData();
                    fitData.mFitType = FitData.FIT_TYPE_SLEEP;
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
                    long time = System.currentTimeMillis();

                    fitData.mTimeStamp = dateFormat.format(time);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String startTimeStr = dateFormat.format(startTime);
                    String endTimeStr = dateFormat.format(endTime);
                    Log.d(TAG, "[onSleepNotify] Start: " + startTimeStr + " End: " + endTimeStr);
                    if (sleepMode == PDMSClientProxy.SLEEP_MODE_INBED) {
                        fitData.mFitData = startTimeStr + "  -->  " + endTimeStr + "     In Bed";
                    } else {
                        fitData.mFitData = startTimeStr + "  -->  " + endTimeStr + "     Sleep";
                    }

                    mFitDataAdapter.addFitData(fitData);
                    mFitDataAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onPedometerNotify(final int stepCount, final int calories, final int distance) {
            Log.d(TAG, "[onPedometerNotify] stepCount=" + stepCount + " calories=" + calories
                    + " distance=" + distance);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FitData fitData = new FitData();
                    fitData.mFitType = FitData.FIT_TYPE_SPORT;
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
                    long time = System.currentTimeMillis();
                    fitData.mTimeStamp = dateFormat.format(time);

                    fitData.mFitData = "stepCount: " + stepCount + " calories: " + calories + " distance: "
                            + distance;
                    mFitDataAdapter.addFitData(fitData);
                    mFitDataAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onHRNotify(final int bpm) {
            Log.d(TAG, "[onHRNotify] bpm=" + bpm);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FitData fitData = new FitData();
                    fitData.mFitType = FitData.FIT_TYPE_HR;
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
                    long time = System.currentTimeMillis();
                    fitData.mTimeStamp = dateFormat.format(time);

                    fitData.mFitData = "Heart Rate: " + bpm;
                    mFitDataAdapter.addFitData(fitData);
                    mFitDataAdapter.notifyDataSetChanged();
                }
            });
        }

    };
}
