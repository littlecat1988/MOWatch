package care;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import care.adapter.FenceListAdapter;
import care.bean.Circle;
import care.utils.BeanUtils;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;
import care.utils.XcmTools;

public class FenceActivity extends Activity implements View.OnClickListener{

	private ImageButton fenceAdd,backButton;
	private TextView headName;
	private ListView fenceList;
	private ArrayList<Circle> circleList;
	private FenceListAdapter fenceListAdapter;
	private XcmTools tools;
	private Handler mHandler,mHandlerupdate;
	private String operateFenceId;
	private int operatePosition;
	private LinearLayout progressBar;
	private TextView progress_text;
	private TextView not_add;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fence);
		init();
		initCircle();
		new Thread(mRunnable2).start();
	}
	

	private void init(){
		backButton=(ImageButton)findViewById(R.id.back_button);
		backButton.setOnClickListener(this);
		fenceAdd=(ImageButton)findViewById(R.id.fence_add_button);
		fenceAdd.setVisibility(View.VISIBLE);
		fenceAdd.setOnClickListener(this);
		headName=(TextView)findViewById(R.id.title_string);
		headName.setText(getString(R.string.fence));
		fenceList=(ListView)findViewById(R.id.fence_list);
		not_add = (TextView)findViewById(R.id.not_add);
		circleList=new ArrayList<Circle>();
		tools = new XcmTools(this);
		mHandler = new Handler();
		mHandlerupdate = new Handler();
//		badylist_jiexi();
//		badyInit(Integer.valueOf(tools.get_babylist_code()));
	}
	
	protected void onResume(){
		super.onResume();
		not_add.setVisibility(View.GONE);
	}
	
	protected void onPause(){
		super.onPause();
	}
	
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}
	
	protected void onDestroy(){
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_button:
			finish();
			break;
		case R.id.fence_add_button:
			Intent i=new Intent(FenceActivity.this, FenceAddActivity.class);
			startActivityForResult(i, 0);
			break;
		}
	}

	
	@Override
	public void onActivityResult(int requestCode,final int resultCode,final Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Trace.i("requestCode===" + requestCode);
		Trace.i("resultCode===" + resultCode);
		switch(requestCode){
		    case 0://添加围栏
		    	if(resultCode==0){
		    		if(data!=null){
		    			Circle circle=(Circle)data.getSerializableExtra("circle");
		    			circleList.add(circle);
		    			fenceListAdapter.notifyDataSetChanged();
		    			
		    		}
		    	}
		    	break;
		    case 1://编辑围栏
		    	if(resultCode==0){
		    		if(data!=null){
		    			if(data.getSerializableExtra("editCircle")!=null){
		    				Circle circle=(Circle)data.getSerializableExtra("editCircle");
		    				int position=data.getExtras().getInt("position");
			    			circleList.set(position,circle);
			    			fenceListAdapter.notifyDataSetChanged();
		    			}
		    			
		    		}
		    	}
		    	break;
		}
	}
	

	public void initCircle() {
		String circles = "";//tools.get_circleList();
		String currentImei = tools.get_current_device_id();
	

		this.circleList.clear();
		// int circleCode =
		// Integer.valueOf(tools.get_babylist_code()).intValue();

		try {
			JSONArray circle_list = new JSONArray(circles);
			// Toast.makeText(getActivity(), "code = " + circle_list.length(),
			// 500).show();
			for (int i = 0; i < circle_list.length(); i++) {
				JSONObject circleObject = circle_list.getJSONObject(i);
				Trace.i("circleObject====" + circleObject.toString());
				HashMap<String, String> circleMap = BeanUtils
						.getJSONParserResult(circleObject.toString());
				Circle circle = BeanUtils.getCircle(circleMap);
				if (circle.getImei().equals(currentImei)) {
					this.circleList.add(circle);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fenceListAdapter=new FenceListAdapter(this,circleList);
		if(circleList.size()>0){
			fenceList.setAdapter(fenceListAdapter);
			fenceListAdapter.notifyDataSetChanged();

		}else{
			fenceList.setAdapter(fenceListAdapter);
		}
		fenceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				if (circleList.size() > 0) {
					Circle circle = circleList.get(position);
					Intent i = new Intent(FenceActivity.this,
							FenceAddActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("editCircle", circle);
					bundle.putInt("position", position);
					i.putExtras(bundle);
					startActivityForResult(i, 1);
				}
			}
			
		});
		fenceList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int position, long id) {
				if(circleList.size()>0){
					Circle circle=circleList.get(position);
					operateFenceId=circle.getId();
					operatePosition=position;
				}
//				if (tools.get_phone_type().equals("0")) {
					TextView view = new TextView(FenceActivity.this);
					view.setText(getString(R.string.fence_delete_alert));
					new AlertDialog.Builder(FenceActivity.this)
							.setTitle(getString(R.string.alert))
							.setView(view)
							.setPositiveButton(getString(R.string.ok),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0,
												int arg1) {
											
											new Thread(mRunnable).start();
											
//											progressBar
//													.setVisibility(View.VISIBLE);

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
		
	}
	
	Runnable mRunnable=new Runnable(){

		@Override
		public void run() {
			final String add_result = fenceDelete("range-del",operateFenceId);
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					fenceDeleteResultCheck(add_result);
				}
			});
			
		}
		
	};
	
	String fenceDelete(String m,String id) {

		String user_id = tools.get_user_id();
		String p_imei = tools.get_current_device_id();
		String p_id = id;
		JSONObject json_fence_delete = new JSONObject();
		try {
			Trace.i("user_id===" + user_id);
			Trace.i("delet fence id===" + p_id);
			json_fence_delete.put("user_id", user_id);
			json_fence_delete.put("device_imei", p_imei);
			json_fence_delete.put("device_safe_id", p_id);
			String json_change_result = Utils.GetService(json_fence_delete, Constants.FENCE_DELETE);
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
	
	private void fenceDeleteResultCheck(String result_check) {

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
					circleList.remove(operatePosition);
					fenceListAdapter.notifyDataSetChanged();
					Toast.makeText(this,
							getString(R.string.fence_delete_success),
							Toast.LENGTH_SHORT).show();
//					new Thread(mUpdate).start();
				} else if ("0".equals(result)) {
					Toast.makeText(
							this,
							getString(R.string.fence_delete_fail)
							, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	Runnable mRunnable2=new Runnable(){

		@Override
		public void run() {
			final String get_result = getFence("",operateFenceId);
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					fenceGetResultCheck(get_result);
				}
			});
			
		}
		
	};
	
	String getFence(String m,String id) {

		String user_id = tools.get_user_id();
		String p_imei = tools.get_current_device_id();
		String p_id = id;
		JSONObject json_fence_get = new JSONObject();
		try {
			Trace.i("user_id===" + user_id);
			Trace.i("get fence id===" + p_id);
			json_fence_get.put("user_id", user_id);
			json_fence_get.put("device_imei", p_imei);
			String json_change_result = Utils.GetService(json_fence_get, Constants.FENCE_GET);
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
	
	private void fenceGetResultCheck(String result_check) {

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
					circleList.clear();
					String imei=tools.get_current_device_id();
					String safe_message=map.get("safe_message");
					JSONArray safeList=new JSONArray(safe_message);
					if(safeList.length()==0){
						not_add.setVisibility(View.VISIBLE);
					}
					for(int i=0;i<safeList.length();i++){
						JSONObject safeObject=(JSONObject)safeList.get(i);
						HashMap<String,String> safeMap= BeanUtils.getJSONParserResult(safeObject.toString());
						Circle circle= BeanUtils.getCircle(safeMap);
						circle.setImei(imei);
						circleList.add(circle);
					}
/*					fenceListAdapter=new FenceListAdapter(this,circleList);
					fenceList.setAdapter(fenceListAdapter);*/
					fenceListAdapter.notifyDataSetChanged();
//					Toast.makeText(this,
//							getString(R.string.fence_delete_success),
//							Toast.LENGTH_SHORT).show();
				} else if ("0".equals(result)) {
//					Toast.makeText(
//							this,
//							getString(R.string.fence_delete_fail)
//							, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
