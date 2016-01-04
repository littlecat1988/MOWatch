package care;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.mtk.btnotification.R;

import java.util.ArrayList;
import java.util.HashMap;

import care.adapter.DisturbAdapter;
import care.bean.DisturbBean;
import care.utils.CommonBaseActivity;
import care.utils.Constants;

/**
 * Created by wid3344 on 2015/8/25.
 */
public class DisturbActivity extends CommonBaseActivity {

    protected Button save_id;
    protected ListView listView;
    protected Switch class_id;
    protected DisturbAdapter adapter;

    protected ArrayList<DisturbBean> mList;
    private String deviceImei = "0";
    private String distrub = "1";
    @Override
    protected void doConnectLinkCallback(String result) {
        if (dialog.isShowing()) {
            dialog.cancel();
        }
        HashMap<String, Object> map = mProtocolData.getBackResult(result);
        int resultCode = (Integer) map.get("resultCode");
        String request = "" + map.get("request");
        if (Constants.GET_DISTURB.contains(request)) {
            if(resultCode == 1){
                String moday = "" + map.get("moday");
                String tuesday = "" + map.get("tuesday");
                String wednesday = "" + map.get("wednesday");
                String thursday = "" + map.get("thursday");
                String friday = "" + map.get("friday");
                String saturday = "" + map.get("saturday");
                String sunday = "" + map.get("sunday");
                String distrub = ""+map.get("distrub");
                updateUI(moday, tuesday, wednesday, thursday, friday, saturday, sunday,distrub);
            }else{

            }

        } else {
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

    private void updateUI(String moday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday,String distrub) {
        if(distrub.equals("1")){
            class_id.setChecked(true);
        }else{
            class_id.setChecked(false);
        }
        HashMap<Integer, String> temp = new HashMap<Integer, String>();
        temp.put(1, moday);
        temp.put(2, tuesday);
        temp.put(3, wednesday);
        temp.put(4, thursday);
        temp.put(5, friday);
        temp.put(6, saturday);
        temp.put(7, sunday);

        for (int key : temp.keySet()) {
            DisturbBean disturbBean = new DisturbBean();
            String tempString = temp.get(key);
            String[] tempArray = tempString.split(";");
            for (int i = 0; i < tempArray.length; i++) {
                String[] tempTimeArray = tempArray[i].split(",");
                switch (i) {
                    case 0:
                        disturbBean.setTimeFirst(tempTimeArray[0]);
                        disturbBean.setTimeSecond(tempTimeArray[1]);
                        break;
                    case 1:
                        disturbBean.setTimeThirth(tempTimeArray[0]);
                        disturbBean.setTimeFour(tempTimeArray[1]);
                        break;
                    case 2:
                        disturbBean.setTimeFive(tempTimeArray[0]);
                        disturbBean.setTimeSix(tempTimeArray[1]);
                        break;
                }
            }
            disturbBean.setWeek(key);
            mList.add(disturbBean);
        }
        adapter.addAll(mList);
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_disturb);
        Bundle bundle = getIntent().getExtras();
        deviceImei = bundle.getString("deviceImei");
    }

    private void updateData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("serie_no", deviceImei);
        String mJr = mProtocolData.transFormToJson(map);
        new ConnectToLinkTask().execute(Constants.GET_DISTURB, mJr);
    }

    private void initData() {
        String message = getString(R.string.wait_minute1);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void initFindView() {
        titleString.setText(R.string.remain_disturb);
        listView = (ListView) findViewById(R.id.list_disturb);
        class_id = (Switch)findViewById(R.id.class_id);
        adapter = new DisturbAdapter(this);
        listView.setAdapter(adapter);
        mList = new ArrayList<DisturbBean>();
        save_id = (Button) findViewById(R.id.save_id);
        save_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveData();
            }
        });
        class_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    distrub = "1";
                } else {
                    distrub = "0";
                }
            }
        });
        initData();
        updateData();
    }

    @Override
    protected void onDestoryActivity() {

    }

    private void onSaveData() {
        HashMap<String, Object> map = new HashMap<String, Object>();

        int length = adapter.getCount();
        for (int i = 0; i < length; i++) {
            StringBuffer stringBuffer = new StringBuffer();
            DisturbBean disturbBean = (DisturbBean) adapter.getItem(i);

            stringBuffer.append(disturbBean.getTimeFirst()).append(",").append(disturbBean.getTimeSecond()).append(";")
                    .append(disturbBean.getTimeThirth()).append(",").append(disturbBean.getTimeFour()).append(";")
                    .append(disturbBean.getTimeFive()).append(",").append(disturbBean.getTimeSix());
            
            switch (i){
                case 0:
                    map.put("moday",stringBuffer.toString());
                    break;
                case 1:
                    map.put("tuesday",stringBuffer.toString());
                    break;
                case 2:
                    map.put("wednesday",stringBuffer.toString());
                    break;
                case 3:
                    map.put("thursday",stringBuffer.toString());
                    break;
                case 4:
                    map.put("friday",stringBuffer.toString());
                    break;
                case 5:
                    map.put("saturday",stringBuffer.toString());
                    break;
                case 6:
                    map.put("sunday",stringBuffer.toString());
                    break;
            }
        }
        map.put("serie_no",deviceImei);
        map.put("distrub",distrub);
        String mJr = mProtocolData.transFormToJson(map);
        Log.i("params", mJr);
        new ConnectToLinkTask().execute(Constants.SET_DISTURB, mJr);
    }
}
