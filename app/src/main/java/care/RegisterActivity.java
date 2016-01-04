package care;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mtk.btnotification.R;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.util.HashMap;

import care.userinfo.model.UserInfo;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends CommonBaseActivity implements
View.OnClickListener{

//	private CircularImage register_photo;
	private TextView use_condition;
	private TextView use_pri;
	private EditText register_phone_id;
	private EditText phone_sms_id;
	private EditText pass_edit_first;
	private EditText pass_edit_sec;	
	private Button valida_btn_id;
	
	private String registerPhoneString = "";
	private String phoneSms = "";
	private String passStringFirst = "";   //第一密码
	private String passStringSecond = "";  //第二次密码
	private boolean isVerify = false;
	//定时60s
	private final int mCodeTimeCount = 60 * 1000;
	private TimeCount time;

	private class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			valida_btn_id.setEnabled(true);
			valida_btn_id.setText(R.string.get_sms_string);
			valida_btn_id.setTextColor(getResources().getColor(R.color.color_blue));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			valida_btn_id.setEnabled(false);   //是否可点击
			valida_btn_id.setText((millisUntilFinished / 1000)+"");
			valida_btn_id.setTextColor(getResources().getColor(R.color.color_gray));
		}

	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_register);
		mInstance.addActivity(Constants.REGISTERACTIVITY, RegisterActivity.this);
		time = new TimeCount(mCodeTimeCount, 1000); //设置60秒，以1秒为单位倒计时
	}

	@Override
	protected void initFindView() {
		titleString.setText(R.string.register_string);
		next_step.setVisibility(View.VISIBLE);

		use_condition = (TextView)findViewById(R.id.use_condition);
		use_pri = (TextView)findViewById(R.id.use_pri);
		register_phone_id = (EditText)findViewById(R.id.register_phone_id);
		phone_sms_id = (EditText)findViewById(R.id.phone_sms_id);
		pass_edit_first = (EditText)findViewById(R.id.pass_edit_first);
		pass_edit_sec = (EditText)findViewById(R.id.pass_edit_sec);

		valida_btn_id = (Button)findViewById(R.id.valida_btn_id);
		initSmsValite();
		setOnClickListener();
		if(tools.get_token_id().equals("0")){
			XGPushManager.registerPush(RegisterActivity.this,mXGIOperateCallback);
		}
	}

	private void initSmsValite() {
//		// TODO Auto-generated method stub
//		SMSSDK.initSDK(this, "879eade955bb", "8baebb20c33b1453a18810e6beb4000b");
		SMSSDK.initSDK(this, "c739ab229670", "1fd245fb7dd72e96092d813e0d2fd93a");
		EventHandler eventHandler = new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				msg.what = -9;
				handler.sendMessage(msg);
			}
		};
		//注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	}

	private Handler handler  = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 验证码验证成功
						updateDataToBack();	
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						isVerify = false;
						showToast(R.string.sms_sent);
						if(dialog.isShowing()){
							dialog.cancel();
						}
					} else{
						isVerify = false;
						if(dialog.isShowing()){
							dialog.cancel();
						}
						((Throwable) data).printStackTrace();
					}
				}else if(result == SMSSDK.RESULT_ERROR){
					isVerify = false;
					if(dialog.isShowing()){
						dialog.cancel();
					}
					showToast(R.string.error_sms);
				}
			}
		}
	};
	private void setOnClickListener() {
		next_step.setOnClickListener(this);
		use_condition.setOnClickListener(this);
		use_pri.setOnClickListener(this);
		valida_btn_id.setOnClickListener(this);
		
	}

	protected void updateDataToBack() {
		// TODO Auto-generated method stub
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("user_name", registerPhoneString);
		map.put("user_password", passStringFirst);
		map.put("user_phone", registerPhoneString);
		map.put("user_imei", "000000000000000");
		map.put("user_imsi", "100000000000000");
		map.put("phone_version", "android" + Build.VERSION.RELEASE);
		map.put("phone_model", Build.MODEL);   //手机型号
		map.put("app_version", Constants.getCurrentVersion_name(getApplicationContext()));
		map.put("xg_tokenid",tools.get_token_id());   //获取信鸽的设备tokenID
		String mJr = mProtocolData.transFormToJson(map);
		new ConnectToLinkTask().execute(Constants.REGISTER,mJr);
	}

	@Override
	protected void onDestoryActivity() {
		mInstance.removeActivity(Constants.REGISTERACTIVITY);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.next_step:  //下一步
			if(onRegister()){
//				updateDataToBack();
				if(Constants.IS_OPEN_NETWORK){
					if(!Constants.isCeshi(registerPhoneString)){
						isVerify = true;
						String message = getString(R.string.wait_minute);
						dialog.setMessage(message);
						dialog.setCanceledOnTouchOutside(false);  //点击屏幕其他地方dialog消失，也防止在4.0系统中报错
						dialog.show();
						SMSSDK.submitVerificationCode("86", registerPhoneString, phoneSms);
					}else{
						updateDataToBack();
					}
				}else{
					showToast(R.string.network_error);
				}
			}
		break;
		case R.id.use_condition:    //使用条款
			showToast(R.string.no_function);
		break;
		case R.id.use_pri:          //隐私政策
			showToast(R.string.no_function);
		break;
		case R.id.valida_btn_id:    //验证码
			if(onValidate()){
				if(Constants.IS_OPEN_NETWORK){
					time.start();
					SMSSDK.getVerificationCode("86", registerPhoneString);
				}else{
					showToast(R.string.network_error);
				}
			}	
		break;
		}
	}
	public boolean onRegister(){
		register_phone_id.setError(null);
		phone_sms_id.setError(null);
		pass_edit_first.setError(null);
		pass_edit_sec.setError(null);
		registerPhoneString = register_phone_id.getText().toString().trim();
		if (registerPhoneString.equals("")) {
			register_phone_id.setError(getString(R.string.input_phone_string));
			register_phone_id.requestFocus();
			return false;
		}
		if(!Constants.isCeshi(registerPhoneString)){
			if (!Constants.isMobileNO(registerPhoneString)) {
				register_phone_id.setError(getString(R.string.error_string1));
				register_phone_id.requestFocus();
				return false;
			}
		}
		phoneSms = phone_sms_id.getText().toString().trim();
		if (phoneSms.equals("")) {
			phone_sms_id.setError(getString(R.string.error_string2));
			phone_sms_id.requestFocus();
			return false;
		}
		passStringFirst = pass_edit_first.getText().toString().trim();
		if (passStringFirst.equals("")) {
			pass_edit_first.setError(getString(R.string.error_string3));
			pass_edit_first.requestFocus();
			return false;
		}
		if (passStringFirst.length()>12||passStringFirst.length()<6) {
			pass_edit_first.setError(getString(R.string.error_string5));
			pass_edit_first.requestFocus();
			return false;
		}
		passStringSecond = pass_edit_sec.getText().toString().trim();
		if (passStringSecond.equals("")) {
			pass_edit_sec.setError(getString(R.string.error_string4));
			pass_edit_sec.requestFocus();
			return false;
		}
		if (passStringSecond.length()>12||passStringSecond.length()<6) {
			pass_edit_sec.setError(getString(R.string.error_string5));
			pass_edit_sec.requestFocus();
			return false;
		}
		if(!passStringSecond.equals(passStringFirst)){
			pass_edit_sec.setError(getString(R.string.error_string6));
			pass_edit_sec.requestFocus();
			return false;
		}
		return true;
	}
	//判断电话号码的输入    
	public boolean onValidate(){
		register_phone_id.setError(null);
		registerPhoneString = register_phone_id.getText().toString().trim();
		if (registerPhoneString.equals("")) {
			register_phone_id.setError(getString(R.string.input_phone_string));
			register_phone_id.requestFocus();
			return false;
		}
		if (!Constants.isMobileNO(registerPhoneString)) {
			register_phone_id.setError(getString(R.string.error_string1));
			register_phone_id.requestFocus();
			return false;
		}
		return true;
	}
	
	private String editWarn() {
		// TODO Auto-generated method stub
		String register_string = "0";
		
		registerPhoneString = register_phone_id.getText().toString().trim();
		phoneSms = phone_sms_id.getText().toString().trim();
		passStringFirst = pass_edit_first.getText().toString().trim();
		passStringSecond = pass_edit_sec.getText().toString().trim();
		
		if(TextUtils.isEmpty(registerPhoneString)){
			register_phone_id.requestFocus();
			register_string = "-1";
		}else if(registerPhoneString.length() > 11 || !Constants.isMobileNO(registerPhoneString)){
			register_phone_id.requestFocus();
			register_string = "-2";
		}else if(TextUtils.isEmpty(phoneSms)){
			phone_sms_id.requestFocus();
			register_string = "-3";
		}else if(TextUtils.isEmpty(passStringFirst)){
			pass_edit_first.requestFocus();
			register_string = "-4";
		}else if(TextUtils.isEmpty(passStringSecond)){
			pass_edit_sec.requestFocus();
			register_string = "-5";
		}else if(passStringFirst.length() < 6 || passStringFirst.length() > 12){
			pass_edit_first.requestFocus();
			register_string = "-6";
		}else if(!passStringFirst.equals(passStringSecond)){
			pass_edit_sec.requestFocus();
			register_string = "-7";
		}
		
		return register_string;
	}

	@Override
	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub
		if(dialog.isShowing()){
			dialog.cancel();
		}
		
		HashMap<String, Object> map = mProtocolData.getBackResult(result);
		int resultCode = (Integer)map.get("resultCode");
		switch (resultCode) {
		case 1:   //成功
			String user_id = "" + map.get("user_id");
			UserInfo userInfo = new UserInfo();
			userInfo.setUserBirthday("2015-12-31");
			userInfo.setUserHeadUrl("0");
			userInfo.setUserId(user_id);
			userInfo.setUserLogin("1");
			userInfo.setUserName(registerPhoneString);  
			userInfo.setUserNickName(registerPhoneString);  //第一次默认电话号码
			userInfo.setUserPassword(passStringFirst);
			userInfo.setUserSex("0");  //0男1女
			userInfo.setUserHeight("170");
			userInfo.setUserWeight("80");
			Constants.USERID = user_id;
			Constants.PHONE = registerPhoneString;
			Constants.USERHEADURL = "0";
			tools.set_user_id(user_id);
			mUpdateDB.insertToDataBases(UserInfo.class, userInfo);

			String personstring = userInfo.getUserHeadUrl() + "," + userInfo.getUserNickName() + "," + userInfo.getUserSex() + "," + userInfo.getUserBirthday() + "," + userInfo.getUserHeight() + "," + userInfo.getUserWeight();
			tools.set_person(personstring);
			tools.set_login_phone(userInfo.getUserName());
            //注册成功后跳往绑定页面
			Intent dataIntent = new Intent();
			dataIntent.setClass(this, BondDeviceActivity.class);
			startActivity(dataIntent);
		    break;
		case 0:   //失败
			isVerify = false;
			showToast(R.string.error_register);
			register_phone_id.requestFocus();
			break;
		case -1:   //异常
			isVerify = false;
			String exception = "" + map.get("exception");
			showToast(R.string.exception_code);
			Trace.i("exception++" + exception);
			break;
		case -6:
			isVerify = false;
			showToast(R.string.link_chaoshi_code);
			break;
		}
	}
	XGIOperateCallback mXGIOperateCallback=new XGIOperateCallback() {
		
		@Override
		public void onSuccess(Object data, int flag) {
			Log.i("TPush","reg="+data);
			tools.set_token_id(data+"");
		}
		
		@Override
		public void onFail(Object data, int errCode, String msg) {
		}
	};
}
