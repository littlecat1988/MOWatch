package care;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mtk.btnotification.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import care.deviceinfo.model.DeviceInfo;
import care.userinfo.model.UserInfo;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.widget.CircularImage;

public class AddBaoBeiInfoActivity extends CommonBaseActivity implements OnClickListener{

	private CircularImage register_baobei;
	private Button complete_id;
	private EditText device_edit_id;
	private EditText device_phone_id;
	private EditText device_height_id;
	private EditText device_weight_id;
	private ViewGroup line_nan_id,line_nv_id;
	private ImageButton nv_btn_id;
	private ImageButton nan_btn_id;
	private ImageButton nan_select_id;
	private ImageButton nv_select_id;
		
	private String device_head_id = "";
	private String deviceId = "0";
	private String deviceImei = "";
	private String deviceHeadUrl = "0";  //���ճɹ���,�����û�ͷ���ַ
	private String deviceNickName = ""; //�ǳ�
	private String devicePhone = "";    //�绰
	private String deviceHeight = "170";//���
	private String deviceWeight = "100";//����
	private String deviceBirthday = "2015-12-12 23:59:59"; //�?����
	
	private int sex_type = 0;   //0��1Ů
	
	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.add_toy_info_main);
//		device_head_id = getIntent().getExtras().getString("head_id","0");  //��Ӻ����豸��id�ش�
		Bundle bundle = getIntent().getExtras();
		deviceImei = bundle.getString("deviceImei","0");  //��Ӻ����豸��imei
		deviceId = bundle.getString("deviceId","0");      //��Ӻ����豸��id�ش�
		Constants.DEVICEID = deviceImei;
		device_head_id = device_head_id+"_"+ Constants.getCurrentTime(System.currentTimeMillis());
	}

	@Override
	protected void initFindView() {
		// TODO Auto-generated method stub
		titleString.setText(R.string.add_bond_string);
		register_baobei = (CircularImage)findViewById(R.id.register_baobei);
		device_edit_id = (EditText)findViewById(R.id.device_edit_id);
		device_phone_id = (EditText)findViewById(R.id.device_phone_id);
		device_height_id = (EditText)findViewById(R.id.device_height_id);
		device_weight_id = (EditText)findViewById(R.id.device_weight_id);
		line_nan_id = (ViewGroup)findViewById(R.id.line_nan_id);
		line_nv_id = (ViewGroup)findViewById(R.id.line_nv_id);
		nv_btn_id = (ImageButton)findViewById(R.id.nv_btn_id);
		nan_btn_id = (ImageButton)findViewById(R.id.nan_btn_id);
		nan_select_id = (ImageButton)findViewById(R.id.nan_select_id);
		nv_select_id = (ImageButton)findViewById(R.id.nv_select_id);
		complete_id = (Button)findViewById(R.id.complete_id);
		
		setOnClickListener();
	}

	protected void onStart(){
		super.onStart();
		sexSelect();
	}
	private void sexSelect(){
		switch (sex_type) {
		case 0:
			nan_btn_id.setImageResource(R.drawable.sex_nan_p);
			line_nan_id.setBackgroundResource(R.drawable.sex_select_left_p);
			nan_select_id.setVisibility(View.VISIBLE);
			
			//Ů����Ϣ��û��ѡ��
			nv_btn_id.setImageResource(R.drawable.sex_nv_n);
			line_nv_id.setBackgroundResource(R.drawable.sex_select_right_n);
			nv_select_id.setVisibility(View.INVISIBLE);
			break;

        case 1:
        	//�е���Ϣ��û��ѡ��
        	nan_btn_id.setImageResource(R.drawable.sex_nan_n);
			line_nan_id.setBackgroundResource(R.drawable.sex_select_left_n);
			nan_select_id.setVisibility(View.INVISIBLE);
					
			nv_btn_id.setImageResource(R.drawable.sex_nv_p);
			line_nv_id.setBackgroundResource(R.drawable.sex_select_right_p);
			nv_select_id.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	private void setOnClickListener() {
		// TODO Auto-generated method stub
		register_baobei.setOnClickListener(this);
		line_nan_id.setOnClickListener(this);
		line_nv_id.setOnClickListener(this);
		complete_id.setOnClickListener(this);
	}

	@Override
	protected void onDestoryActivity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.register_baobei:
			
		break;
		case R.id.line_nan_id:
			sex_type = 0;
			sexSelect();
			break;
		case R.id.line_nv_id:
			sex_type = 1;
			sexSelect();
			break;
			
		case R.id.complete_id:
			if(!Constants.IS_OPEN_NETWORK){
				showToast(R.string.network_error);
			}else{
				saveBaoBeiInfo();
			}		
		break;
		}
	}

	private void saveBaoBeiInfo() {
		// TODO Auto-generated method stub
		String result = editWarn();
		
		if("-1".equals(result)){  //�豸�ǳ�
			showToast(R.string.device_error1);
		}else if("-2".equals(result)){ //�豸�ǳƸ�ʽ����
			showToast(R.string.device_error2);
		}else if("-3".equals(result)){ //�豸�绰���벻��Ϊ��
			showToast(R.string.device_error3);
		}else if("-4".equals(result)){ //�豸�绰�����ʽ����
			showToast(R.string.device_error4);
		}else if("-5".equals(result)){ //�豸�绰�����ʽ����
			showToast(R.string.device_error5);
		}else if("-6".equals(result)){ //�豸�绰�����ʽ����
			showToast(R.string.device_error6);
		}else{
			updateDataToBack();
		}
	}

	private void updateDataToBack() {
		// TODO Auto-generated method stub
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("user_id", Constants.USERID);
		map.put("device_imei", deviceImei);
		map.put("device_phone", devicePhone);
		try {
			map.put("device_name", URLEncoder.encode(deviceNickName,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("device_head", "0");
		map.put("device_sex", String.valueOf(sex_type));
		map.put("device_age", deviceBirthday);
		map.put("device_height", deviceHeight);
		map.put("device_weight", deviceWeight);
		
		String mJr = mProtocolData.transFormToJson(map);
		new ConnectToLinkTask().execute(Constants.ADDDEVICE,mJr);
	}

	private String editWarn() {
		// TODO Auto-generated method stub
		String register_string = "0";
		
		deviceNickName = device_edit_id.getText().toString().trim(); //�ǳ�
		devicePhone = device_phone_id.getText().toString().trim();    //�绰
		deviceHeight = device_height_id.getText().toString().trim(); //���
		deviceWeight = device_weight_id.getText().toString().trim();//����
				
		if(TextUtils.isEmpty(deviceNickName)){
			device_edit_id.requestFocus();
			register_string = "-1";
		}else if(deviceNickName.length() > 12 || deviceNickName.length() < 4){
			device_edit_id.requestFocus();
			register_string = "-2";
		}else if(TextUtils.isEmpty(devicePhone)){
			device_phone_id.requestFocus();
			register_string = "-3";
		}else if(devicePhone.length() > 11 || !Constants.isMobileNO(devicePhone)){
			device_phone_id.requestFocus();
			register_string = "-4";
		}else if(TextUtils.isEmpty(deviceHeight)){
			device_height_id.requestFocus();
			register_string = "-5";
		}else if(TextUtils.isEmpty(deviceWeight)){
			device_weight_id.requestFocus();
			register_string = "-6";
		}
		
		return register_string;
	}

	@Override
	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = mProtocolData.getBackResult(result);
		int resultCode = (Integer)map.get("resultCode");
		switch (resultCode) {
		case 1:
			DeviceInfo deviceInfo = new DeviceInfo();
			deviceInfo.setDeviceId(deviceId);
			deviceInfo.setDeviceAge("12");
			deviceInfo.setDeviceHeadUrl(deviceHeadUrl);
			deviceInfo.setDeviceImei(deviceImei);
			deviceInfo.setDeviceName(deviceNickName);
			deviceInfo.setDevicePhone(devicePhone);
			deviceInfo.setDeviceSex(String.valueOf(sex_type));
			
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(Constants.USERID);
			deviceInfo.setUserInfo(userInfo);		
			mUpdateDB.insertToDataBases(DeviceInfo.class, deviceInfo);
			
			BaoBeiManagerActivity manager = (BaoBeiManagerActivity)mInstance.getActivity(Constants.BAOBEIMANAGERACTIVITY);
			if(manager != null){
				manager.finish();
			}
			BondDeviceActivity addBaoBei = (BondDeviceActivity)mInstance.getActivity(Constants.ADDTOYACTIVITY);
			if(addBaoBei != null){
				addBaoBei.finish();
			}
			LoginActivity loginActivity = (LoginActivity)mInstance.getActivity(Constants.LOGINACTIVITY);
			if(loginActivity != null){
				loginActivity.finish();
			}
			RegisterActivity registerActivity = (RegisterActivity)mInstance.getActivity(Constants.REGISTERACTIVITY);
			if(registerActivity != null){
				registerActivity.finish();
			}
			
			Intent dataIntent = new Intent(AddBaoBeiInfoActivity.this,LocationActivity.class);
			startActivity(dataIntent);
			finish();
			break;
		case 0:
			
			break;
		case -1:
			String exception = "" + map.get("exception");
			showToast(R.string.exception_code);
			Trace.i("exception++" + exception);
			break;
		case -6:
			showToast(R.string.link_chaoshi_code);
			break;
		}
	}

}
