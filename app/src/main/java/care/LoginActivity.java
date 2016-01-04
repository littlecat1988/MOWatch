package care;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mtk.btnotification.R;
import com.mtk.main.FirstActivity;

import java.util.HashMap;

import care.clientmanager.ClientNetManager;
import care.deviceinfo.model.DeviceInfo;
import care.userinfo.model.UserInfo;
import care.utils.CommonBaseActivity;
import care.utils.Constants;

public class LoginActivity extends CommonBaseActivity implements View.OnClickListener{

	private Button login_button,register_button;

	private EditText phone_edit;
	private EditText pass_edit;

	private String phoneString = "";
	private String passString = "";

	private ClientNetManager mClientNetManager;
    private SharedPreferences mPrefs;

    @Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_login);

		mClientNetManager = mInstance.getClientNetManager();
		mInstance.addActivity(Constants.LOGINACTIVITY, LoginActivity.this);
	}

	@Override
	protected void initFindView() {
		// TODO Auto-generated method stub
//		contain_head.setVisibility(View.GONE);
        titleString.setText(R.string.login_string);
		login_button = (Button)findViewById(R.id.login_button);
		register_button = (Button)findViewById(R.id.register_button);
		phone_edit = (EditText)findViewById(R.id.phone_edit);
		pass_edit = (EditText)findViewById(R.id.pass_edit);
		setOnClickListener();
	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		login_button.setOnClickListener(this);
		register_button.setOnClickListener(this);
	}

	@Override
	protected void onDestoryActivity() {
		// TODO Auto-generated method stub
		mInstance.removeActivity(Constants.LOGINACTIVITY);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent dataIntent = new Intent();
		switch(v.getId()){
		case R.id.login_button:
			if(checkEditText()){   //直接登陆(与后台通讯)
				if(Constants.IS_OPEN_NETWORK){
					String user_name = phone_edit.getText().toString();
					String user_password = pass_edit.getText().toString();
					updateDataToBack(user_name,user_password);
				}else{
					showToast(R.string.network_error);
				}
			}
//			doLogin();
			break;
		case R.id.register_button:
			dataIntent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(dataIntent);
			break;
		}
	}

	private void updateDataToBack(String user_name, String user_password) {
		// TODO Auto-generated method stub
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("user_name", user_name);
		map.put("user_password", user_password);
		
		String mJr = mProtocolData.transFormToJson(map);
		new ConnectToLinkTask().execute(Constants.LOGIN,mJr);
	}

	private boolean checkEditText() {
		// TODO Auto-generated method stub
		boolean flag = true;
		phone_edit.setError(null);
		pass_edit.setError(null);
		phoneString = phone_edit.getText().toString().trim();
		passString = pass_edit.getText().toString().trim();

		if(TextUtils.isEmpty(phoneString)){
			phone_edit.requestFocus();
			phone_edit.setError(getString(R.string.input_phone_string));
			flag = false;
			return flag;
		}
		if(phoneString.length() < 11){
			phone_edit.requestFocus();
			phone_edit.setError(getString(R.string.error_string1));
			flag = false;
			return flag;
		}
		if(TextUtils.isEmpty(passString)){
			pass_edit.requestFocus();
			pass_edit.setError(getString(R.string.input_pass_string));
			flag = false;
			return flag;
		}
		if(passString.length() > 12||passString.length()<6){
			pass_edit.requestFocus();
			pass_edit.setError(getString(R.string.set_pass_string));
			flag = false;
			return flag;
		}
		return flag;
	}

	private void doLogin(){
		HashMap<String, Object> tmp = new HashMap<String, Object>();
		tmp.put("request", Constants.USER_LOGIN_ADDRESS);
		tmp.put("loginaccount", phoneString);
		tmp.put("passwd", passString);
		tmp.put("token", 1);

		final String mJsStr = mProtocolData.transFormToJson(tmp);

//		mClientNetManager.sessionSend(mJsStr);
	}
	@Override
	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = mProtocolData.getBackResult(result);
		int resultCode = (Integer)map.get("resultCode");
		switch (resultCode) {
		case 1:   //成功
			doLogin();
			String personstring = "";
			UserInfo userInfo = new UserInfo();
			String user_id = "" + map.get("user_id");
			String user_nick = "" + map.get("user_nick");			
			String user_sex = "" + map.get("user_sex");				
			String user_age = "" + map.get("user_age");				
			String user_height = "" + map.get("user_height");				
			String user_weight = "" + map.get("user_weight");				
			String user_head = "" + map.get("user_head");
            //add by lixiang for login 20151109
            if (mPrefs == null)
                mPrefs = getSharedPreferences("WATCH", 0);
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putInt("LOG_USERID",Integer.valueOf(user_id));
            edit.commit();
            Log.e("lixiang","id= "+Integer.valueOf(user_id));
			userInfo.setUserBirthday(user_age);
			userInfo.setUserHeadUrl(user_head);
			userInfo.setUserLogin("1");
			userInfo.setUserNickName(user_nick);  //第一次默认电话号码
			userInfo.setUserSex(user_sex);  //0男1女
			userInfo.setUserHeight(user_height);
			userInfo.setUserWeight(user_weight);
			userInfo.setUserPassword(passString);
			userInfo.setUserName(phoneString); 
			
			if(user_nick.equals("")||user_nick.equals(null))
			{
				user_nick = "0";
			}
			
			if(user_sex.equals("")||user_sex.equals(null))
			{
				user_sex = "0";
			}
			if(user_age.equals("")||user_age.equals(null))
			{
				user_age = "0";
			}
			if(user_height.equals("")||user_height.equals(null))
			{
				user_height = "0";
			}
			if(user_weight.equals("")||user_weight.equals(null))
			{
				user_weight = "0";
			}
			if(user_head.equals("")||user_head.equals(null))
			{
				user_weight = "0";
			}
			personstring = user_head +"," +user_nick + "," + user_sex+ "," + user_age + "," + user_height + "," + user_weight;
			System.out.println("登录 = " + personstring);
			tools.set_person(personstring);
			Constants.USERID = user_id;
			Constants.USERNICKNAME = user_nick;
			Constants.USERHEADURL = user_head;

			long count = mUpdateDB.queryDataCountToBases(UserInfo.class, new String[]{user_id}, new String[]{UserInfo.USER_ID});
			if(count > 0){  //没有数据			
				ContentValues values = new ContentValues();
				values.put(UserInfo.USER_BIRTHDAY, userInfo.getUserBirthday());
				values.put(UserInfo.USER_HEAD_URL, userInfo.getUserHeadUrl());
				values.put(UserInfo.USER_HEIGHT, userInfo.getUserHeight());
				values.put(UserInfo.USER_LOGIN, userInfo.getUserLogin());
				values.put(UserInfo.USER_NAME, userInfo.getUserName());
				values.put(UserInfo.USER_NICK_NAME, userInfo.getUserNickName());
				values.put(UserInfo.USER_PASSWORD, userInfo.getUserPassword());
				values.put(UserInfo.USER_SEX, userInfo.getUserSex());
				values.put(UserInfo.USER_WEIGHT, userInfo.getUserWeight());
				
				mUpdateDB.updateDataToBases(UserInfo.class, values, new String[]{user_id}, new String[]{UserInfo.USER_ID});
			}else{
				userInfo.setUserId(user_id);
				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setUserInfo(userInfo);
				
				mUpdateDB.insertToDataBases(DeviceInfo.class, deviceInfo);
				mUpdateDB.insertToDataBases(UserInfo.class, userInfo);
			}
			tools.set_login_phone(phoneString);
			tools.set_user_id(user_id);
			tools.set_user_phone(userInfo.getUserNickName());
//			Intent dataIntent  =  new Intent(this, LocationActivity.class);
            Intent dataIntent  =  new Intent(this, FirstActivity.class);
			startActivity(dataIntent);
			finish();
			
		    break;
		case 0:   //失败
			showToast(R.string.error_string8);
			break;
		case -1:   //异常
			String exception = "" + map.get("exception");
			showToast(R.string.exception_code);
			break;
		case -6:
			showToast(R.string.link_chaoshi_code);
			break;
		}
	}
}
