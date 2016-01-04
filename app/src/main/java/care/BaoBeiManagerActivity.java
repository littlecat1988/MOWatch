package care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import care.adapter.BaoBeiManagerAdapter;
import care.adapter.OtherBaoBeiManagerAdapter;
import care.bean.BaoBeiBean;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Utils;

public class BaoBeiManagerActivity extends CommonBaseActivity implements View.OnClickListener{

	private ListView baobei_manager_id,other_share_manager_id;
	private BaoBeiManagerAdapter adapter;
	private OtherBaoBeiManagerAdapter sharedAdapter;
	private SparseArray<BaoBeiBean> mDataList = new SparseArray<BaoBeiBean>();
	private SparseArray<BaoBeiBean> mSharedDataList = new SparseArray<BaoBeiBean>();
	private ImageButton mDeviceAdd;
	private Handler mHandler;
	private LinearLayout progressBar;
	private TextView progress_text;
	private String currentOperationId;

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.baobei_manager_main);
		mInstance.addActivity(Constants.BAOBEIMANAGERACTIVITY, BaoBeiManagerActivity.this);
	}

	@Override
	protected void initFindView() {
		progressBar=(LinearLayout)findViewById(R.id.progress_bar);
		progress_text=(TextView)findViewById(R.id.progress_text);
		progress_text.setText(R.string.loading);
		mDeviceAdd = (ImageButton)findViewById(R.id.device_add);
		mDeviceAdd.setVisibility(View.VISIBLE);
		mDeviceAdd.setOnClickListener(this);
		titleString.setText(R.string.baobei_manager_string);
		baobei_manager_id = (ListView)findViewById(R.id.baobei_manager_id);
		other_share_manager_id = (ListView)findViewById(R.id.other_share_manager_id);
		baobei_manager_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
									long id) {
				BaoBeiBean baobei=mDataList.get(position);
				Intent i = new Intent (BaoBeiManagerActivity.this,BaobeiInfo_chose_Activity.class);
				Bundle bundle=new Bundle();
				bundle.putString("babyImei", baobei.getImei());
				bundle.putString("babyName", baobei.getName());
				bundle.putString("babyPhone", baobei.getPhone());
				bundle.putString("babyPhoto", baobei.getPhoto());
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		baobei_manager_id.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
										   final int position, long id) {
/*				BaoBeiBean baobei=mDataList.get(position);
				if(baobei.isCurrent()){
					TextView view = new TextView(BaoBeiManagerActivity.this);
					view.setText(getString(R.string.baby_now_already));
					new AlertDialog.Builder(BaoBeiManagerActivity.this)
							.setTitle(getString(R.string.alert))
							.setView(view)
							.setPositiveButton(getString(R.string.ok),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0,
												int arg1) {

										}
									})
							.show();
				}else{*/
				TextView view = new TextView(BaoBeiManagerActivity.this);
				view.setText(getString(R.string.unbind_alarm));
				new AlertDialog.Builder(BaoBeiManagerActivity.this)
						.setTitle(getString(R.string.alert))
						.setView(view)
						.setPositiveButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
														int arg1) {

										BaoBeiBean baobei=mDataList.get(position);
										currentOperationId=baobei.getImei();
										new Thread(mUnbondRunnable).start();
										refreshCurrentDevice();
									}
								})
						.setNegativeButton(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
														int arg1) {

									}

								}).show();
//				}
				return true;
			}

		});
		/*other_share_manager_id.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					final int position, long id) {
				TextView view = new TextView(BaoBeiManagerActivity.this);
				view.setText(getString(R.string.set_baby_now));
				new AlertDialog.Builder(BaoBeiManagerActivity.this)
						.setTitle(getString(R.string.alert))
						.setView(view)
						.setPositiveButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {

//										new Thread(mRunnable).start();
										BaoBeiBean baobei=mSharedDataList.get(position);
										tools.set_current_device_id(baobei.getImei());
										refreshCurrentDevice();
									}
								})
						.setNegativeButton(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {

									}

								}).show();
				return false;
			}});*/
		adapter = new BaoBeiManagerAdapter(this,tools,imageLoader,options,this);
		baobei_manager_id.setAdapter(adapter);
		sharedAdapter = new OtherBaoBeiManagerAdapter(this,tools,imageLoader,options,this);
		other_share_manager_id.setAdapter(sharedAdapter);
		mHandler=new Handler();

		setOnClickListener();
	}
	@Override
	protected void onResume(){
		super.onResume();
		mDataList.clear();
		mSharedDataList.clear();
		progressBar.setVisibility(View.VISIBLE);
		new Thread(mRunnable).start();
	}
	private void tmp() {
		// TODO Auto-generated method stub
		for(int i=0;i<4;i++){
			BaoBeiBean BaoBeiBeanTmp = new BaoBeiBean();
			BaoBeiBeanTmp.setBaoBeiName("宝贝"+i);
			BaoBeiBeanTmp.setBaoBeiPhone("136123"+i);
			BaoBeiBeanTmp.setBaoBeiUrl("0");
			if(i == 0){
				BaoBeiBeanTmp.setBaoBeiSelect("1");
			}else{
				BaoBeiBeanTmp.setBaoBeiSelect("0");
			}
			mDataList.append(i, BaoBeiBeanTmp);
		}
		BaoBeiBean BaoBeiBeanTmp = new BaoBeiBean();
		BaoBeiBeanTmp.setBaoBeiUrl("1");
		BaoBeiBeanTmp.setBaoBeiName(getString(R.string.add_baobei_title));
		BaoBeiBeanTmp.setBaoBeiPhone("0");
		BaoBeiBeanTmp.setBaoBeiSelect("0");

		mDataList.append(4, BaoBeiBeanTmp);
		adapter.setCurrentPosition(0); //默认第一个被选中

		adapter.setDataList(mDataList);
		adapter.refresh();
	}
	public void refreshCurrentDevice(){
		for(int i=0;i<mDataList.size();i++){
			BaoBeiBean baobei=mDataList.get(i);
			String imei=baobei.getImei();
			if(imei.equals(tools.get_current_device_id())){
				baobei.setCurrent(true);
			}else{
				baobei.setCurrent(false);
			}
		}
		for(int i=0;i<mSharedDataList.size();i++){
			BaoBeiBean baobei=mSharedDataList.get(i);
			String imei=baobei.getImei();
			if(imei.equals(tools.get_current_device_id())){
				baobei.setCurrent(true);
			}else{
				baobei.setCurrent(false);
			}
		}
		adapter.refresh();
		sharedAdapter.refresh();
	}
	private void huoQuDeviceInfo() {
		String babyList = tools.get_babyList();
		String myUserId = tools.get_user_id();
		try {
			JSONArray babyArray=new JSONArray(babyList);
			int length=babyArray.length();
			String currentId=tools.get_current_device_id();
			for(int i=0;i<length;i++){
				JSONObject babyObject=(JSONObject)babyArray.get(i);
				HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
				BaoBeiBean baobei= BeanUtils.getBaoBei(babyMap);
				if(baobei.getImei().equals(currentId)){
					baobei.setCurrent(true);
				}else{
					baobei.setCurrent(false);
				}
				if(myUserId.equals(baobei.getToUserId())){
					int size=mDataList.size();
					mDataList.append(size, baobei);
				}else{
					int size=mSharedDataList.size();
					mSharedDataList.append(size, baobei);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		/*List<?> deviceInfos = mUpdateDB.queryDataToBases(DeviceInfo.class, new String[]{Constants.USERID}, new String[]{DeviceInfo.DEVICE_BOND_USER}, null, false);
		int length = deviceInfos.size();
		for(int i=0;i<length;i++){
			BaoBeiBean BaoBeiBeanTmp = new BaoBeiBean();
			DeviceInfo tmp = (DeviceInfo)deviceInfos.get(i);

			BaoBeiBeanTmp.setBaoBeiUrl(tmp.getDeviceHeadUrl());
			BaoBeiBeanTmp.setBaoBeiName(tmp.getDeviceName());
			BaoBeiBeanTmp.setBaoBeiPhone(tmp.getDevicePhone());
			BaoBeiBeanTmp.setBaoBeiSelect("0");

			mDataList.append(i, BaoBeiBeanTmp);
		}
		BaoBeiBean BaoBeiBeanTmp = new BaoBeiBean();
		BaoBeiBeanTmp.setBaoBeiUrl("1");
		BaoBeiBeanTmp.setBaoBeiName(getString(R.string.add_baobei_title));
		BaoBeiBeanTmp.setBaoBeiPhone("0");
		BaoBeiBeanTmp.setBaoBeiSelect("0");

		mDataList.append(length, BaoBeiBeanTmp);
		if(length != 0){
			adapter.setCurrentPosition(0); //默认第一个被选中
		}	*/

		adapter.setDataList(mDataList);
		adapter.refresh();
		sharedAdapter.setDataList(mSharedDataList);
		sharedAdapter.refresh();
	}


	private void setOnClickListener() {
		// TODO Auto-generated method stub
//		baobei_manager_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if(position < adapter.getCount() - 1){
//					setPositionBaoBeiInfo(position);
//				}else{
//					Intent dataIntent = new Intent(BaoBeiManagerActivity.this,BondDeviceActivity.class);
//					startActivity(dataIntent);
//				}
//			}
//		});
	}

	private void setPositionBaoBeiInfo(int position){
		BaoBeiBean tmp= mDataList.get(position);
		tmp.setBaoBeiSelect("1");
		String imei = tmp.getImei();
		Constants.DEVICEID = imei;

		adapter.setBaoBeiInfoPosition(position, tmp);
		adapter.refresh();
	}

	@Override
	protected void onDestoryActivity() {
		// TODO Auto-generated method stub
		mInstance.removeActivity(Constants.BAOBEIMANAGERACTIVITY);
	}

	@Override
	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.device_add:
				Intent i = new Intent(this, BondDeviceActivity.class);
				startActivity(i);
				break;
		}

	}

	String downloadData(String m) {
		String p_sid = tools.get_user_id();
		JSONObject json_download = new JSONObject();
		try {
			json_download.put("user_id", p_sid);
			String json_download_result = Utils.GetService(json_download, Constants.DOWNLOAD_DATA);
			   Log.i("lk", json_download_result+"");
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
			showToast(R.string.server_time_out);
		} else {
			HashMap<String, String> map;
			try {
				map = BeanUtils.getJSONParserResult(result_check);
				String result=map.get("resultCode");
				if("1".equals(result)){
					String babyList=map.get("device_message");
					tools.set_babyList(babyList);
					JSONArray babyArray=new JSONArray(babyList);
					int length=babyArray.length();
					int count=0;
					for(int i=0;i<length;i++){
						JSONObject babyObject=(JSONObject)babyArray.get(i);
						HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
						BaoBeiBean baobei= BeanUtils.getBaoBei(babyMap);
						String currentId=tools.get_current_device_id();
						if(currentId==null||"".equals(currentId)||"null".equals(currentId)||"0".equals(currentId)){
							tools.set_current_device_id(baobei.getImei());
							break;
						}else{
							if(baobei.getImei().equals(currentId)){
								count++;
								break;
							}
						}
					}
					if(length>0){
						if(count==0){
							JSONObject babyObject=(JSONObject)babyArray.get(0);
							HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
							BaoBeiBean baobei= BeanUtils.getBaoBei(babyMap);
							tools.set_current_device_id(baobei.getImei());
						}
					}else{
						tools.set_current_device_id("");
						tools.set_babyList("");
					}
					huoQuDeviceInfo();

				}else if("0".equals(result)){

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		progressBar.setVisibility(View.GONE);
	}

	String unbind() {
		String p_sid = tools.get_user_id();
		String p_imei = currentOperationId;
		JSONObject json_unbind = new JSONObject();
		try {
			json_unbind.put("user_id", p_sid);
			json_unbind.put("device_imei", p_imei);
			String json_download_result = Utils.GetService(json_unbind,
                    Constants.BABYDELETE);

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

	Runnable mUnbondRunnable = new Runnable() {

		@Override
		public void run() {
			final String add_result = unbind();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					unbindResultCheck(add_result);
				}
			});

		}

	};

	private void unbindResultCheck(String result_check) {

		if (result_check.equals("0") || result_check.equals("-1")) {

		} else {
			HashMap<String, String> map;
			try {
				map = BeanUtils.getJSONParserResult(result_check);
				String result=map.get("resultCode");
				if("1".equals(result)){
					showToast(R.string.unbind_success);
					for(int i=0;i<mDataList.size();i++){
						BaoBeiBean baobei=mDataList.get(i);
						String imei=baobei.getImei();
						if(imei.equals(currentOperationId)){
							mDataList.remove(i);
							if(imei.equals(tools.get_current_device_id())){
								tools.set_current_device_id("");
							}
							break;
						}
					}
					tools.set_babyList("");
					adapter.notifyDataSetChanged();
				}else if("0".equals(result)){
					showToast(R.string.unbind_failed);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
