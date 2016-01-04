package care.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mtk.btnotification.R;

import java.util.ArrayList;
import java.util.Calendar;

import care.bean.DisturbBean;

/**
 * Created by wid3344 on 2015/8/26.
 */
public class DisturbAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<DisturbBean> mList;

    private DisturbBean temp;
    private int currenPosition = -1;
    private int type = -1;

    public DisturbAdapter(Context mContext){
        this.context = mContext;
        this.mList = new ArrayList<DisturbBean>();
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.disturb_item,parent,false);

            viewHolder.time_id = (TextView) convertView.findViewById(R.id.time_id);
            viewHolder.time1 = (TextView) convertView.findViewById(R.id.time1);
            viewHolder.time13 = (TextView) convertView.findViewById(R.id.time13);

            viewHolder.time2 = (TextView) convertView.findViewById(R.id.time2);
            viewHolder.time23 = (TextView) convertView.findViewById(R.id.time23);

            viewHolder.time3 = (TextView) convertView.findViewById(R.id.time3);
            viewHolder.time33 = (TextView) convertView.findViewById(R.id.time33);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            resetViewHolder(viewHolder);
        }
        final DisturbBean disturbBean = (DisturbBean)getItem(position);
        String week = onStrReturn(disturbBean.getWeek());
        viewHolder.time_id.setText(week);

        viewHolder.time1.setText(disturbBean.getTimeFirst());
        viewHolder.time13.setText(disturbBean.getTimeSecond());

        viewHolder.time2.setText(disturbBean.getTimeThirth());
        viewHolder.time23.setText(disturbBean.getTimeFour());

        viewHolder.time3.setText(disturbBean.getTimeFive());
        viewHolder.time33.setText(disturbBean.getTimeSix());

        viewHolder.time1.setOnClickListener(new MyClick());
        viewHolder.time1.setTag(position);
        viewHolder.time13.setOnClickListener(new MyClick());
        viewHolder.time13.setTag(position);
        viewHolder.time2.setOnClickListener(new MyClick());
        viewHolder.time2.setTag(position);
        viewHolder.time23.setOnClickListener(new MyClick());
        viewHolder.time23.setTag(position);
        viewHolder.time3.setOnClickListener(new MyClick());
        viewHolder.time3.setTag(position);
        viewHolder.time33.setOnClickListener(new MyClick());
        viewHolder.time33.setTag(position);

        return convertView;
    }

    private String onStrReturn(int week) {
        String temp = context.getString(R.string.moday);
        switch (week){
            case 1:
                temp = context.getString(R.string.moday);
                break;
            case 2:
                temp = context.getString(R.string.tuesday);
                break;
            case 3:
                temp = context.getString(R.string.wednesday);
                break;
            case 4:
                temp = context.getString(R.string.thursday);
                break;
            case 5:
                temp = context.getString(R.string.friday);
                break;
            case 6:
                temp = context.getString(R.string.saturday);
                break;
            case 7:
                temp = context.getString(R.string.sunday);
                break;
        }
        return temp;
    }

    private void resetViewHolder(ViewHolder viewHolder) {
        viewHolder.time_id.setText(null);
        viewHolder.time1.setText(null);
        viewHolder.time13.setText(null);
        viewHolder.time2.setText(null);
        viewHolder.time23.setText(null);
        viewHolder.time3.setText(null);
        viewHolder.time33.setText(null);
    }

    public void refresh(){
        notifyDataSetChanged();
    }
    public void addAll(ArrayList<DisturbBean> mListTemp){
        mList.addAll(mListTemp);
        refresh();
    }

    public class MyClick implements View.OnClickListener{


        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.time1:
                    type = R.id.time1;
                    break;
                case R.id.time13:
                    type = R.id.time13;
                    break;
                case R.id.time2:
                    type = R.id.time2;
                    break;
                case R.id.time23:
                    type = R.id.time23;
                    break;
                case R.id.time3:
                    type = R.id.time3;
                    break;
                case R.id.time33:
                    type = R.id.time33;
                    break;
            }
            currenPosition = (Integer)v.getTag();
            temp = (DisturbBean)getItem(currenPosition);
            final Calendar calendar = Calendar.getInstance();
            MyTimePickerDialog dialog = new MyTimePickerDialog(context, mTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            dialog.show();
        }
    }

    public class MyTimePickerDialog extends TimePickerDialog {

        /**
         * @param context      Parent.
         * @param callBack     How parent is notified.
         * @param hourOfDay    The initial hour.
         * @param minute       The initial minute.
         * @param is24HourView Whether this is a 24 hour view, or AM/PM.
         */
        public MyTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
        }

        @Override
        public void onStop() {
            //super.onStop();
        }
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        /**
         * @param view      The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute    The minute that was set.
         */
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hourString = String.valueOf(hourOfDay);
            String minuteString = String.valueOf(minute);

            if (minute < 10) {
                minuteString = "0" + minuteString;
            }
            if (hourOfDay < 10) {
                hourString = "0" + hourString;
            }
            String temp = hourString + ":" + minuteString;
            onTextView(temp);
        }
    };

    private void onTextView(String temps) {
        switch (type){
            case R.id.time1:
                temp.setTimeFirst(temps);
                break;
            case R.id.time13:
                temp.setTimeSecond(temps);
                break;
            case R.id.time2:
                temp.setTimeThirth(temps);
                break;
            case R.id.time23:
                temp.setTimeFour(temps);
                break;
            case R.id.time3:
                temp.setTimeFive(temps);
                break;
            case R.id.time33:
                temp.setTimeSix(temps);
                break;
        }
        setCurrentPosition();
    }

    private void setCurrentPosition() {
        if(!mList.isEmpty() && currenPosition != -1){
            mList.set(currenPosition,temp);
            refresh();
        }
    }

    public static final class ViewHolder{
        private TextView time_id;
        private TextView time1;
        private TextView time13;
        private TextView time2;
        private TextView time23;
        private TextView time3;
        private TextView time33;
    }
}
