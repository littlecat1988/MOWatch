package care;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mtk.btnotification.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import care.bean.BaoBeiBean;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;
import care.widget.WiperSwitch;


public class ActivityBabyControl extends CommonBaseActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener{

	private ListView managerListView;
	private ImageButton invite_confirm;
	private EditText invite_phone;
	private TextView volume_precent;
	private SeekBar volumeSeekBar;
	private int mCurrentVolume;
	private ImageButton reduButton,plusButton;
	private Handler mHandler;
	private WiperSwitch auto_silence,auto_off;
	private BaoBeiBean currentBaby;
	private Button setting_complete;
	private String silence;
	private String off;
	private TextView title_string;
	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_baby_control);
	}

	@Override
	protected void initFindView() {
		title_string = (TextView)findViewById(R.id.title_string);
		title_string.setText(getString(R.string.setting_far));
		setting_complete = (Button)findViewById(R.id.setting_complete);
		setting_complete.setOnClickListener(this);
		auto_silence = (WiperSwitch)findViewById(R.id.auto_silence);
		auto_silence.setOnChangedListener(new WiperSwitch.OnChangedListener(){

			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				if(checkState){
					silence="1";
				}else{
					silence="0";
				}
			}});
		auto_off = (WiperSwitch)findViewById(R.id.auto_off);
		auto_off.setOnChangedListener(new WiperSwitch.OnChangedListener(){

			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				if(checkState){
					off="1";
				}else{
					off="0";
				}
			}});
		/*auto_silence = (WiperSwitch)findViewById(R.id.auto_silence);
		auto_silence.setOnChangedListener(new WiperSwitch.OnChangedListener(){

			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				
			}});*/
		volume_precent = (TextView)findViewById(R.id.volume_precent);
		volumeSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		volumeSeekBar.setOnSeekBarChangeListener(this);
		reduButton=(ImageButton)findViewById(R.id.redu_button);
		plusButton=(ImageButton)findViewById(R.id.plus_button);
		reduButton.setOnClickListener(this);
		plusButton.setOnClickListener(this);
		
		mHandler = new Handler();
		initSetting();
//		silence="0";
//		off="0";
		/*titleString.setText(R.string.invite_people);
		invite_phone = (EditText) findViewById(R.id.invite_phone);
		invite_confirm = (ImageButton) findViewById(R.id.invite_confirm);
		invite_confirm.setOnClickListener(this);*/
	}

	public void initSetting(){
		new Thread(mRunnable).start();
		/*String babyList = tools.get_babyList();
		try {
			JSONArray babyArray=new JSONArray(babyList);
			int length=babyArray.length();
			String currentId=tools.get_current_device_id();
			for(int i=0;i<length;i++){
				JSONObject babyObject=(JSONObject)babyArray.get(i);
				HashMap<String,String> babyMap=BeanUtils.getJSONParserResult(babyObject.toString());
				BaoBeiBean baobei=BeanUtils.getBaoBei(babyMap);
				Trace.i("baobei name==="+baobei.getBaoBeiName());
				Trace.i("baobei phone==="+baobei.getBaoBeiPhone());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.invite_confirm:
			
			break;
		case R.id.redu_button:
			if(volumeSeekBar.getProgress()>0){
				volumeSeekBar.setProgress(volumeSeekBar.getProgress()-1);
			}
			break;
		case R.id.plus_button:
			if(volumeSeekBar.getProgress()<10){
				volumeSeekBar.setProgress(volumeSeekBar.getProgress()+1);
			}
			break;
		case R.id.setting_complete:
			new Thread(mRunnable2).start();
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int precent = progress*10;
		mCurrentVolume = precent;
		String precentString = precent+"%";
		volume_precent.setText(precentString);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	
	String downloadData(String m) {
		String p_sid = tools.get_user_id();
		JSONObject json_download = new JSONObject();
		try {
			json_download.put("user_id", p_sid);
			String json_download_result = Utils.GetService(json_download, Constants.DOWNLOAD_DATA);
			
			Log.i("lk",json_download_result+"");
			
			
			if (json_download_result.equals("0")) {
				return "0";
			} else if (json_download_result.equals("-1")) {
				return "-1";
			} else {
				return json_download_result;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return "";

	}
	
	Runnable mRunnable=new Runnable(){

		@Override
		public void run() {
			final String add_result = downloadData("");
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					downloadResultCheck(add_result);
				}
			});
			
			
		}
		
	};
	private void downloadResultCheck(String result_check) {

		if (result_check.equals("0") || result_check.equals("-1")) {
//			Toast.makeText(this, "鏈嶅姟鍣ㄩ棶棰�, Toast.LENGTH_SHORT).show();
		} else {
			HashMap<String, String> map;
			try {
				Trace.i("result_check====" + result_check);
				map = BeanUtils.getJSONParserResult(result_check);
				String result=map.get("resultCode");
				if("1".equals(result)){
					String babyList=map.get("device_message");
					tools.set_babyList(babyList);
					String currentId=tools.get_current_device_id();
					JSONArray babyArray=new JSONArray(babyList);
					int length=babyArray.length();
					if(length==0){
						Toast.makeText(ActivityBabyControl.this, getString(R.string.no_device), Toast.LENGTH_SHORT).show();
						Intent i = new Intent(ActivityBabyControl.this, BondDeviceActivity.class);
						startActivity(i);
					}else{
						for(int i=0;i<length;i++){
							JSONObject babyObject=(JSONObject)babyArray.get(i);
							String imei=babyObject.get("device_imei").toString();
							if(currentId.equals(imei)){
								HashMap babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
								currentBaby= BeanUtils.getBaoBei(babyMap);
								break;
							}
						}
						if(currentBaby!=null){
							setSettings();	
						}
						
					}
				}else if("0".equals(result)){
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	Runnable mRunnable2=new Runnable(){

		@Override
		public void run() {
			final String get_result = controlBaby("");
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					controlResultCheck(get_result);
				}
			});
			
		}
		
	};
	
	String controlBaby(String m) {

		String user_id = tools.get_user_id();
		String p_imei = tools.get_current_device_id();
		JSONObject json_baby_control = new JSONObject();
		try {
			Trace.i("user_id===" + user_id);
			Trace.i("p_imei===" + p_imei);
			json_baby_control.put("user_id", user_id);
			json_baby_control.put("device_imei", p_imei);
			json_baby_control.put("device_data_mute", silence);
			json_baby_control.put("device_data_volume", mCurrentVolume);
			json_baby_control.put("device_data_power", off);
			json_baby_control.put("device_data_light", "0");
			Trace.i("json_baby_control===" + json_baby_control.toString());
			String json_change_result = Utils.GetService(json_baby_control, Constants.BABYCONTROL);
			if (json_change_result.equals("0")) {
				return "0";
			} else if (json_change_result.equals("-1")) {
				return "-1";

			} else {
				return json_change_result;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";

	}
	
	private void controlResultCheck(String result_check) {

		if (result_check.equals("0") || result_check.equals("-1")) {
			Toast.makeText(this, "server error", Toast.LENGTH_SHORT)
					.show();
		} else {
			HashMap<String, String> map;
			try {
				Trace.i("result_check====" + result_check);
				map = Utils.getJSONParserResult(result_check);
				String result = map.get("resultCode");
				if ("1".equals(result)) {
					Toast.makeText(this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
				} else if ("0".equals(result)) {
					Toast.makeText(this, getString(R.string.setting_failed), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setSettings(){
		String volume=currentBaby.getVolume();
		Trace.i("volume====" + volume);
		String mute=currentBaby.getMute();
		Trace.i("mute====" + mute);
		String power=currentBaby.getPower();
		Trace.i("power====" + power);
		String light=currentBaby.getLight();
		volume_precent.setText(volume+"%");
		int volumeNumber=Integer.parseInt(volume);
		mCurrentVolume=volumeNumber;
		int progress=volumeNumber/10;
		volumeSeekBar.setProgress(progress);
		if(mute.equals("1")){
			auto_silence.setChecked(true);
			silence="1";
		}else{
			auto_silence.setChecked(false);
			silence="0";
		}
		if(power.equals("1")){
			auto_off.setChecked(true);
			off="1";
		}else{
			auto_off.setChecked(false);
			off="0";
		}
	}

}
