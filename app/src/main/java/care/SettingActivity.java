package care;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.util.HashMap;

import care.utils.CommonBaseActivity;
import care.utils.Constants;

public class SettingActivity extends CommonBaseActivity implements
        OnClickListener {

    private LinearLayout setting_person, setting_feedback, is_lower_id, version_id;
    private TextView title_string;
    private TextView is_tuo;
    private TextView is_lower;
    private TextView version_text;
    private Button power_switch_id;
    private Switch qu_wen_id;
    private RatingBar ratingBar;
    private RadioGroup switch_way;
    private RadioButton switch_way_lbs;
    private RadioButton switch_way_gps;

    private boolean is_Checked = false;
    private boolean is_click_submit = false;
    private int radioButtonId = 2;  //默认

    private DownloadManager downloadManager;
    private boolean is_click = false;
    private boolean is_quwen = false;
    private boolean is_gps = false;
    private String url = "0";

    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.setting_layout);
        url = tools.get_download_url();
    }

    protected void initFindView() {
        // TODO Auto-generated method stub
        title_string = (TextView) findViewById(R.id.title_string);
        right_txt.setText(R.string.feedback_button_text);
        right_txt.setVisibility(View.VISIBLE);
        setting_person = (LinearLayout) findViewById(R.id.setting_person);
        setting_feedback = (LinearLayout) findViewById(R.id.setting_feedback);
        is_lower_id = (LinearLayout) findViewById(R.id.is_lower_id);
        version_id = (LinearLayout) findViewById(R.id.version_id);
        version_text = (TextView) findViewById(R.id.version_text);
        is_tuo = (TextView) findViewById(R.id.is_tuo);
        is_lower = (TextView) findViewById(R.id.is_lower);
        power_switch_id = (Button) findViewById(R.id.power_switch_id);
        qu_wen_id = (Switch) findViewById(R.id.qu_wen_id);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        switch_way = (RadioGroup) findViewById(R.id.switch_way);
        switch_way_lbs = (RadioButton) findViewById(R.id.switch_way_lbs);
        switch_way_gps = (RadioButton) findViewById(R.id.switch_way_gps);

        setOnClickListener();
        init();
        updateDataToBack(0);
    }

    protected void updateDataToBack(int type) {
        // TODO Auto-generated method stub
        HashMap<String, Object> map = new HashMap<String, Object>();
        String address = "";
        switch (type) {
            case 0:         //检测脱落
                map.put("serie_no", tools.get_current_device_id());
                address = Constants.DOGETFALL;
                break;
            case 1:        //远程关机
                map.put("no", tools.get_current_device_id());
                map.put("type", "0");
                address = Constants.REMOTE;
                break;
            case 2:        //驱蚊
                map.put("no", tools.get_current_device_id());
                map.put("type", "1");
                if (is_Checked) {
                    map.put("repellent", "1");
                } else {
                    map.put("repellent", "0");
                }
                address = Constants.REMOTE;
                break;
            case 3:
                map.put("no", tools.get_current_device_id());
                map.put("type", "2");
                map.put("heart", (int) ratingBar.getRating());
                address = Constants.REMOTE;
                break;
            case 4:
                map.put("serie_no", tools.get_current_device_id());
                address = Constants.GET_LOWELECTRICITY;
                break;
            case 5:
                map.put("no", tools.get_current_device_id());
                map.put("type", "3");
                if (radioButtonId == R.id.switch_way_gps) {
                    map.put("gps_on", "1");
                } else {
                    map.put("gps_on", "0");
                }
                Log.i("radioButtonId", String.valueOf(radioButtonId));
                address = Constants.REMOTE;
                break;
        }

        String mJr = mProtocolData.transFormToJson(map);
        new ConnectToLinkTask().execute(address, mJr);
    }

    void init() {
        title_string.setText(getString(R.string.setting_title));

        if(url.contains("upload")){
            version_text.setText(getString(R.string.version_update,url.split("@")[1]));
        }else{
            version_text.setText(getString(R.string.version_current, Constants.getCurrentVersion_name(getApplicationContext())));
        }
        String message = getString(R.string.wait_minute1);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void downloadApk(String downloadUrl) {
        is_click = true;  //升级
        downloadManager = (DownloadManager) this
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(getString(R.string.app_name));
        request.setDescription(getString(R.string.app_name));
        // request.setNotificationVisibility(DownloadManager.Request.);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "xcm.apk");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    private void setOnClickListener() {
        // TODO Auto-generated method stub
        setting_person.setOnClickListener(this);
        setting_feedback.setOnClickListener(this);
        is_lower_id.setOnClickListener(this);
        power_switch_id.setOnClickListener(this);
        right_txt.setOnClickListener(this);
        version_text.setOnClickListener(this);
        qu_wen_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_Checked = isChecked;
                qu_wen_id.setChecked(isChecked);
                if(is_quwen){
                    updateDataToBack(2);
                }else{
                    is_quwen = true;   //第一次不上传
                }

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
            }
        });
        switch_way.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonId = group.getCheckedRadioButtonId();
                if(is_gps){
                    updateDataToBack(5);
                }else{
                    is_gps = true;
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_person:
                Intent i2 = new Intent(SettingActivity.this, PersonInfoActivity.class);
                startActivity(i2);
                break;
            case R.id.setting_feedback:
                Intent i = new Intent(SettingActivity.this, FeedBackActivity.class);
                startActivity(i);
                break;
            case R.id.power_switch_id:
                updateDataToBack(1);
                break;
            case R.id.qu_wen_id:
//                updateDataToBack(2);
                break;
            case R.id.right_txt:
                is_click_submit = true;
                updateDataToBack(3);
                break;
            case R.id.is_lower_id:
                updateDataToBack(4);
                break;
            case R.id.version_text:
                if(url.contains("http") && !is_click){
                    downloadApk(url.split("@")[0]);
                }
                break;
        }
    }

    protected void doConnectLinkCallback(String result) {
        // TODO Auto-generated method stub
        if (dialog.isShowing()) {
            dialog.cancel();
        }
        HashMap<String, Object> map = mProtocolData.getBackResult(result);
        int resultCode = (Integer) map.get("resultCode");
        String request = "" + map.get("request");

        if (Constants.DOGETFALL.contains(request)) {
            String fall = "" + map.get("fall");
            String repellent = "" + map.get("repellent");
            String gps_on = "" + map.get("gps_on");
            if (resultCode == 1 && fall.equals("1")) {
                is_tuo.setText(R.string.nos_tuo);
            }
            if (resultCode == 1 && repellent.equals("1")) {
                qu_wen_id.setChecked(true);
            } else {
                qu_wen_id.setChecked(false);
                is_quwen = true;  //默认关闭,不会触发事件
            }
            if (resultCode == 1 && gps_on.equals("0")) {
                switch_way_lbs.setChecked(true);
                is_gps = true;
            } else {
                switch_way_gps.setChecked(true);
            }
        } else if (Constants.GET_LOWELECTRICITY.contains(request) && resultCode == 1) {
            String electricity = "" + map.get("electricity");
            String isLow = "" + map.get("isLow");
            if (isLow.equals("0")) {
                is_lower.setText(electricity);
            } else if (isLow.equals("1")) {
                is_lower.setText(R.string.low_string);
            }

        } else if (Constants.REMOTE.contains(request) && !is_click_submit) {
            if(resultCode == 1){
                showToast(R.string.other1);
            }else{
                showToast(R.string.other2);
            }
        } else {
            switch (resultCode) {
                case 1: // 成功
                    showToast(R.string.other1);
                    finish();
                    break;
                case 0: // 失败
                    showToast(R.string.other2);
                    break;
                case -1: // 异常
                    String exception = "" + map.get("exception");
                    showToast(R.string.exception_code);
                    break;
                case -6:
                    showToast(R.string.link_chaoshi_code);
                    break;
            }
        }
    }

    protected void onDestoryActivity() {

    }

}
