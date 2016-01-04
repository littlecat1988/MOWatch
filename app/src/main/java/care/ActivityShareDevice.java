package care;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import care.adapter.ManagerAdapter;
import care.bean.BaoBeiBean;
import care.bean.User;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;

public class ActivityShareDevice extends CommonBaseActivity implements OnClickListener{

	private ListView managerListView;
	private ImageButton mRelativesAdd;
	private Handler mHandler;
	private ArrayList<User> userList=new ArrayList<User>();
	private ManagerAdapter mManagerAdapter;
	private String babyImei;
	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_share_device);
		babyImei=getIntent().getExtras().get("babyImei").toString();
	}

	@Override
	protected void initFindView() {
		titleString.setText(R.string.device_share);
		managerListView = (ListView) findViewById(R.id.manager_list);
		mRelativesAdd = (ImageButton) findViewById(R.id.relatives_add);
		mRelativesAdd.setVisibility(View.VISIBLE);
		mRelativesAdd.setOnClickListener(this);
		mHandler=new Handler();
		mManagerAdapter = new ManagerAdapter(this);
		managerListView.setAdapter(mManagerAdapter);
		new Thread(mRunnable).start();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.relatives_add:
			Intent i = new Intent(this, ActivityInvitePeople.class);
			Bundle bundle = new Bundle();
			bundle.putString("babyImei", babyImei);
			i.putExtras(bundle);
			startActivity(i);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode,final int resultCode,final Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		    case 0://����ɹ�
		    	if(resultCode==0){
		    		if(data!=null){
		    			User user=(User)data.getSerializableExtra("user");
		    			userList.add(user);
		    			mManagerAdapter.setDataList(userList);
		    			mManagerAdapter.notifyDataSetChanged();
		    		}
		    	}
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

	String downloadData(String m) {
		String p_sid = tools.get_user_id();
		JSONObject json_download = new JSONObject();
		try {
			json_download.put("user_id", p_sid);
			String json_download_result = Utils.GetService(json_download, Constants.DOWNLOAD_DATA);
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
						/*Toast.makeText(ActivityBabyControl.this, getString(R.string.no_device), Toast.LENGTH_SHORT).show();
						Intent i = new Intent(ActivityBabyControl.this,BondDeviceActivity.class);
						startActivity(i);*/
					}else{
						for(int i=0;i<length;i++){
							JSONObject babyObject=(JSONObject)babyArray.get(i);
							String imei=babyObject.get("device_imei").toString();
							if(babyImei.equals(imei)){
								HashMap babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
								BaoBeiBean baby= BeanUtils.getBaoBei(babyMap);
								userList=baby.getManagerList();
								mManagerAdapter.setDataList(userList);
				    			mManagerAdapter.notifyDataSetChanged();
							}
						}
					}
				}else if("0".equals(result)){
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
