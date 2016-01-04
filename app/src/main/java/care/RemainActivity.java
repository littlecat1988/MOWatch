package care;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mtk.btnotification.R;

import care.utils.CommonBaseActivity;

/**
 * Created by wid3344 on 2015/8/24.
 */
public class RemainActivity extends CommonBaseActivity implements
        View.OnClickListener {

    public LinearLayout remainClockId;
    public LinearLayout noDisturb;
    public LinearLayout sleepRemainId;

    @Override
    protected void doConnectLinkCallback(String result) {

    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.remain_layout);

    }

    @Override
    protected void initFindView() {
        titleString.setText(R.string.remain_string);

        remainClockId = (LinearLayout) findViewById(R.id.remain_clock_id);
        noDisturb = (LinearLayout) findViewById(R.id.no_disturb);
        sleepRemainId = (LinearLayout) findViewById(R.id.sleep_remain_id);

        remainClockId.setOnClickListener(this);
        noDisturb.setOnClickListener(this);
        sleepRemainId.setOnClickListener(this);
    }

    @Override
    protected void onDestoryActivity() {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent dataIntent = new Intent();
        Bundle bundle = new Bundle();
        String temp = "0";
        switch (v.getId()){
            case R.id.remain_clock_id:
                dataIntent.setClass(this, RemainClockActivity.class);
                bundle.putString("deviceImei", tools.get_current_device_id());
                temp = "14:00,15:00,16:00";
                bundle.putString("remainClock",temp);
                bundle.putString("type","0");
                dataIntent.putExtras(bundle);
                startActivity(dataIntent);
                break;
            case R.id.no_disturb:
                dataIntent.setClass(this, DisturbActivity.class);
                bundle.putString("deviceImei", tools.get_current_device_id());
                bundle.putString("time1","07:00,07:00");
                bundle.putString("time2","08:00,08:00");
                bundle.putString("time3","09:00,09:00");
                dataIntent.putExtras(bundle);
                startActivity(dataIntent);
                break;
            case R.id.sleep_remain_id:
                dataIntent.setClass(this, RemainClockActivity.class);
                bundle.putString("deviceImei", tools.get_current_device_id());
                temp = "14:00,23:00";
                bundle.putString("remainClock",temp);
                bundle.putString("type","1");
                dataIntent.putExtras(bundle);
                startActivity(dataIntent);
                break;
        }
    }
}
