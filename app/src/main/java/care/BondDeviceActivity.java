package care;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtk.btnotification.R;

import care.fragment.EditInfoDialogFragment.EditInfoDialogListener;

import java.util.HashMap;

import care.fragment.EditInfoDialogFragment;
import care.utils.CommonBaseActivity;
import care.utils.Constants;

public class BondDeviceActivity extends CommonBaseActivity implements View.OnClickListener, EditInfoDialogListener {

    private Button code_validate;
    private TextView noToys;
    private ViewGroup atbtn;
    private ImageView sts;
    private TextView code_text_id;
    private String deviceImei = "";

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.add_toy_main);
        mInstance.addActivity(Constants.ADDTOYACTIVITY, BondDeviceActivity.this);
    }

    @Override
    protected void initFindView() {
        
        titleString.setText(R.string.add_baobei_title);
        next_step.setText(R.string.text_input);
        next_step.setVisibility(View.VISIBLE);

        code_validate = (Button) findViewById(R.id.validate_id);
        atbtn = (ViewGroup) findViewById(R.id.atbtn);
        noToys = (TextView) findViewById(R.id.noToys);
        code_text_id = (TextView) findViewById(R.id.code_text_id);
        sts = (ImageView) findViewById(R.id.sts);
        setOnClickListener();
    }

    private void setOnClickListener() {
        code_validate.setOnClickListener(this);
        noToys.setOnClickListener(this);
        next_step.setOnClickListener(this);
        sts.setOnClickListener(this);
        right_txt.setOnClickListener(this);
    }

    @Override
    protected void onDestoryActivity() {
        mInstance.removeActivity(Constants.ADDTOYACTIVITY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sts:
                Intent dataIntent = new Intent(BondDeviceActivity.this, CaptureActivity.class);
                startActivityForResult(dataIntent, 0);
                break;
            case R.id.noToys:
                Intent mainIntent = new Intent(BondDeviceActivity.this, LocationActivity.class);
                startActivity(mainIntent);
                LoginActivity loginActivity = (LoginActivity) mInstance.getActivity(Constants.LOGINACTIVITY);
                if (loginActivity != null) {
                    loginActivity.finish();
                }
                RegisterActivity registerActivity = (RegisterActivity) mInstance.getActivity(Constants.REGISTERACTIVITY);
                if (registerActivity != null) {
                    registerActivity.finish();
                }
                finish();
                break;
            case R.id.next_step:
                final FragmentManager fm = getFragmentManager();
                EditInfoDialogFragment edit = EditInfoDialogFragment.getIntance(deviceImei, getString(R.string.text_input_hint));
                edit.show(fm, "edit_dialog");
                break;
            case R.id.validate_id:
                if (!Constants.IS_OPEN_NETWORK) {
                    showToast(R.string.network_error);
                } else {
                    if (!"".equals(deviceImei)) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("user_id", tools.get_user_id());
                        map.put("device_imei", deviceImei);
                        String mJr = mProtocolData.transFormToJson(map);
                        new ConnectToLinkTask().execute(Constants.VERIFYCODE, mJr);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    showToast(R.string.scan_fail);
                } else {
                    String code = data.getStringExtra("Code");
                    validateImei(code);   //��ȡ��imei�ش�
                }
                break;
        }
    }

    private boolean validateImei(String content) {
        
        if (!"".equals(content)) {
            atbtn.setVisibility(View.VISIBLE);
        } else {
            atbtn.setVisibility(View.GONE);
        }
        deviceImei = content;
        code_text_id.setText(getString(R.string.code_text, deviceImei));
        return true;
    }

    @Override
    protected void doConnectLinkCallback(String result) {
        
        HashMap<String, Object> map = mProtocolData.getBackResult(result);
        int resultCode = (Integer) map.get("resultCode");
        switch (resultCode) {
            case 1:
                String device_id = "" + map.get("device_id");
                Intent dataIntent = new Intent(BondDeviceActivity.this, BaoBeiInfoActivity.class);
                Bundle bundle = new Bundle();
                tools.set_current_device_id(deviceImei);
                bundle.putString("deviceImei", deviceImei);
                bundle.putString("deviceId", device_id);
                bundle.putBoolean("toMain", true);
                dataIntent.putExtras(bundle);
                startActivity(dataIntent);
                break;
            case -2:
                showToast(R.string.device_bond_error1);
                break;
            case -3:
                showToast(R.string.device_bond_error2);
                break;
            case -1:
                String exception = "" + map.get("exception");
                showToast(R.string.exception_code);
                break;
        }
    }

    @Override
    public void onEditInfo(String content) {
        // TODO Auto-generated method stub
        validateImei(content);
    }
}
