package care;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mtk.btnotification.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import care.adapter.BaoBeiAdapter;
import care.utils.CommonBaseActivity;
import care.utils.Constants;

/**
 * Created by wid3344 on 2015/8/24.
 */
public class RemainClockActivity extends CommonBaseActivity implements View.OnClickListener {

    protected String deviceImei = "0";
    protected String type = "0";   //0表示闹钟,1表示睡眠
    protected int typeClock = 0;   //0表示闹钟,1表示睡眠
    protected int position = 0;   //位置信息
    protected ArrayList<String> list = new ArrayList<String>();

    public TextView titleString;
    public ImageButton device_add;
    public Button save_id;
    public ListView list_clock;
    public BaoBeiAdapter adapter;

    @Override
    protected void doConnectLinkCallback(String result) {
        if(dialog.isShowing()){
            dialog.cancel();
        }
        HashMap<String, Object> map = mProtocolData.getBackResult(result);
        int resultCode = (Integer)map.get("resultCode");
        String request = ""+map.get("request");
        if(Constants.GET_CLOCK.contains(request)){
            String clock = ""+map.get("clock");
            initData(clock);
            initList();
        }else if(Constants.GET_SLEEP.contains(request)){
            String clock = ""+map.get("clock");
            initData(clock);
            initList();
        }else{
            switch (resultCode) {
                case 1:
                    showToast(R.string.other1);
                    finish();
                    break;
                case 0:
                    showToast(R.string.other2);
                    break;
                case -1:
                    showToast(R.string.exception_code);
                    break;
                case -6:
                    showToast(R.string.link_chaoshi_code);
                    break;
            }
        }

    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_remain_clock);
        Bundle bundle = getIntent().getExtras();
        deviceImei = bundle.getString("deviceImei");
        type = bundle.getString("type");
        initData();
        updateData();
    }

    private void initData() {
        String message = getString(R.string.wait_minute1);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void updateData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("serie_no", deviceImei);
        String address = "";
        if(type.equals("0")){   //闹钟
            address = Constants.GET_CLOCK;
        }else if(type.equals("1")){ //睡眠
            address = Constants.GET_SLEEP;
        }
        String mJr = mProtocolData.transFormToJson(map);
        new ConnectToLinkTask().execute(address, mJr);
    }

    @Override
    protected void initFindView() {
        titleString = (TextView) findViewById(R.id.title_string);
        device_add = (ImageButton) findViewById(R.id.device_add);
        save_id = (Button)findViewById(R.id.save_id);
        list_clock = (ListView)findViewById(R.id.list_clock);

        if(type.equals("0")){
            titleString.setText(R.string.remain_clock);
        }else{
            titleString.setText(R.string.remain_sleep);
        }

        device_add.setVisibility(View.VISIBLE);
        device_add.setOnClickListener(this);
        save_id.setOnClickListener(this);
        list_clock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                onShow(position);
            }
        });
    }

    public void onShow(final int position){
        TextView textView = new TextView(RemainClockActivity.this);
        textView.setText(getString(R.string.edit_device));
        new AlertDialog.Builder(RemainClockActivity.this)
                .setTitle(getString(R.string.alert))
                .setView(textView)
                .setPositiveButton(getString(R.string.delete_string),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                adapter.remove(position);
                            }
                        })
                .setNegativeButton(getString(R.string.edit_string),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                onEditClock(position);
                            }

                        }).show();
    }

    private void onEditClock(int position) {
        this.position = position;
        typeClock = 1;
        final Calendar calendar = Calendar.getInstance();
        MyTimePickerDialog dialog = new MyTimePickerDialog(this, mTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    @Override
    protected void onDestoryActivity() {

    }

    private void initList() {
        adapter = new BaoBeiAdapter(this);
        adapter.addAll(list);
        list_clock.setAdapter(adapter);
    }

    private ArrayList<String> initData(String remainClock) {
        if(!remainClock.equals("0")){
            String[] remainArray = remainClock.split(",");
            int length = remainArray.length;
            for (int i = 0; i < length; i++) {
                String temp = remainArray[i];
                list.add(temp);
            }
        }
        return list;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_add:
                typeClock = 0;
                final Calendar calendar = Calendar.getInstance();
                MyTimePickerDialog dialog = new MyTimePickerDialog(this, mTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                dialog.show();
                break;
            case R.id.save_id:
                saveClockData();
                break;
        }
    }

    private void saveClockData() {
        int length = adapter.getCount();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<length;i++){
            String temp = (String)adapter.getItem(i);
            if(stringBuffer.length() <= 0){
                stringBuffer.append(temp);
            }else{
                stringBuffer.append(",").append(temp);
            }

        }
        Log.i("clock", stringBuffer.toString());
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("serie_no",deviceImei);
        map.put("clock", stringBuffer.toString());

        String mJr = mProtocolData.transFormToJson(map);
        if(type.equals("0")){
            new ConnectToLinkTask().execute(Constants.SET_CLOCK,mJr);
        }else if(type.equals("1")){
            new ConnectToLinkTask().execute(Constants.SET_SLEEP,mJr);
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
            String hourString = String.valueOf(hourOfDay);;
            String minuteString = String.valueOf(minute);

            if (minute < 10) {
                minuteString = "0" +minuteString;
            }
            if (hourOfDay < 10) {
                hourString = "0" + hourString;
            }
            String temp = hourString + ":" +minuteString;
            if(typeClock == 0){
                adapter.add(temp);
            }else{
                adapter.setCurrentString(position,temp);
            }

        }
    };
}
