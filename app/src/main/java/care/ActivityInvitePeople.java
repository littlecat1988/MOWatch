package care;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mtk.btnotification.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import care.bean.User;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;

public class ActivityInvitePeople extends CommonBaseActivity implements OnClickListener{

	private ListView managerListView;
	private Button invite_confirm;
	private EditText invite_phone;
	private Handler mHandler;
	private String shareId;
	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_invite_people);
		shareId=getIntent().getExtras().getString("babyImei");
	}

	@Override
	protected void initFindView() {
		titleString.setText(R.string.invite_people);
		invite_phone = (EditText) findViewById(R.id.invite_phone);
		invite_confirm = (Button) findViewById(R.id.invite_confirm);
		invite_confirm.setOnClickListener(this);
		mHandler = new Handler();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.invite_confirm:
			new Thread(mRunnable).start();
			break;
		default:
			break;
		}
	}
	

	@Override
	protected void onDestoryActivity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub
		
	}
	
	Runnable mRunnable=new Runnable(){

		@Override
		public void run() {
			final String invite_result = invitePeople("");
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					invitePeopleResultCheck(invite_result);
				}
			});
			
		}
		
	};
	
	String invitePeople(String m) {

		String user_id = tools.get_user_id();
		String p_imei = shareId;
		String invitePhone = invite_phone.getText().toString();
		String inviteWord = getString(R.string.user)+tools.get_user_phone()+getString(R.string.share_word);
		JSONObject json_baby_control = new JSONObject();
		try {
			Trace.i("user_id===" + user_id);
			Trace.i("p_imei===" + p_imei);
			json_baby_control.put("user_id", user_id);
			json_baby_control.put("device_imei", p_imei);
			json_baby_control.put("to_user_phone", invitePhone);
			json_baby_control.put("to_message", URLEncoder.encode(inviteWord,"utf-8"));
			json_baby_control.put("message_level", "0");
			String json_change_result = Utils.GetService(json_baby_control, Constants.BABYSHARE);
			if (json_change_result.equals("0")) {
				return "0";
			} else if (json_change_result.equals("-1")) {
				return "-1";

			} else {
				return json_change_result;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";

	}
	
	private void invitePeopleResultCheck(String result_check) {

		if (result_check.equals("0") || result_check.equals("-1")) {
			Toast.makeText(this, "server error", Toast.LENGTH_SHORT)
					.show();
		} else {
			HashMap<String, String> map;
			try {
				map = Utils.getJSONParserResult(result_check);
				String result = map.get("resultCode");
				if ("1".equals(result)) {
					
					Toast.makeText(this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
					Intent data = new Intent();
	            	Bundle bundle=new Bundle();
	            	User user = new User();
	            	user.setId(map.get("user_id"));
	            	user.setUserName(map.get("user_name"));
	            	user.setNickName(map.get("nick_name"));
	            	user.setUserHead(map.get("user_head"));
					bundle.putSerializable("user", user);
	            	data.putExtras(bundle);
	            	setResult(0,data);
					finish();
				} else if ("0".equals(result)) {
					Toast.makeText(this, getString(R.string.share_failed), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
