package care;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.BusinessArea;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mtk.btnotification.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import care.utils.BeanUtils;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;
import care.utils.XcmTools;

public class FenceAddActivity extends Activity implements View.OnClickListener,LocationSource,AMapLocationListener,OnCameraChangeListener,OnGeocodeSearchListener,SeekBar.OnSeekBarChangeListener{

	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private GeocodeSearch geocoderSearch;
	private ImageButton backButton,okButton,editButton,fenceAdd,square_circle;
	private TextView locationMessage,locationAddress,fenceRadius,public_head_text_center,editName;
	private Circle mCircle,point;
	private SeekBar radiusSelect;
	private int radius;
	private XcmTools tools;
	private double lng;
	private double lat;
	private Handler mHandler;
	private ImageButton reduButton,plusButton,editNameButton;
	private RelativeLayout name_edit_area;
	private LinearLayout radius_change_area;
	private care.bean.Circle circle;
	private boolean isAdd;
	private boolean isEdit=false;
	private int position;
	private boolean hasInit=false;
	private int circleColor=Color.argb(100, 155, 200, 89);
	private int pointColor=Color.argb(255, 155, 171, 10);
	private int strokeColor=Color.argb(255, 155, 171, 10);
	private boolean isCircle=true;
	private Polygon mPolygon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fence_add);
		mapView = (MapView)findViewById(R.id.fence_add_map);
		mapView.onCreate(savedInstanceState);
		init();
		Intent data=getIntent();
		if(data.getSerializableExtra("editCircle")==null){//添加
			isAdd=true;
	    	okButton.setVisibility(View.VISIBLE);
	    	aMap.setMyLocationEnabled(true);
			aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			hasInit=true;
		}else{//编辑
			isAdd=false;
	    	editButton.setVisibility(View.VISIBLE);
	    	name_edit_area.setVisibility(View.GONE);
	    	radius_change_area.setVisibility(View.GONE);
			if(data.getSerializableExtra("editCircle")!=null){
	    		circle=(care.bean.Circle)data.getSerializableExtra("editCircle");
	    		position=data.getExtras().getInt("position");
	    		radius=Integer.parseInt(circle.getRadius());
	    		lng=Double.parseDouble(circle.getLng());
	    		lat=Double.parseDouble(circle.getLat());
	    		int progress=(radius-500)/50;
	    		radiusSelect.setProgress(progress);
	    		hasInit=true;
	    		addCircle();
	    	}
		}
	}
	
	//鍒濆鍖朅Map瀵硅薄
	private void init(){
		name_edit_area=(RelativeLayout)findViewById(R.id.name_edit_area);
		radius_change_area=(LinearLayout)findViewById(R.id.radius_change_area);
		public_head_text_center=(TextView)findViewById(R.id.public_head_text_center);
		public_head_text_center.setText(getString(R.string.fence));
		backButton=(ImageButton)findViewById(R.id.public_head_back_button);
		backButton.setOnClickListener(this);
		okButton=(ImageButton)findViewById(R.id.fenceAddButton);
		okButton.setOnClickListener(this);
		editButton=(ImageButton)findViewById(R.id.fenceEditButton);
		editButton.setOnClickListener(this);
		radiusSelect=(SeekBar)findViewById(R.id.seekBar1);
		radiusSelect.setOnSeekBarChangeListener(this);
		locationMessage=(TextView)findViewById(R.id.location_message);
		locationAddress=(TextView)findViewById(R.id.location_address);
		fenceRadius=(TextView)findViewById(R.id.fence_radius);
		reduButton=(ImageButton)findViewById(R.id.redu_button);
		plusButton=(ImageButton)findViewById(R.id.plus_button);
		editNameButton=(ImageButton)findViewById(R.id.fence_edit);
		reduButton.setOnClickListener(this);
		plusButton.setOnClickListener(this);
		editNameButton.setOnClickListener(this);
		square_circle = (ImageButton)findViewById(R.id.square_circle);
		square_circle.setOnClickListener(this);
		tools=new XcmTools(this);
		mHandler=new Handler();
		if(aMap==null){
			aMap=mapView.getMap();
			aMap.setLocationSource(this);
			aMap.getUiSettings().setMyLocationButtonEnabled(false);
			aMap.getUiSettings().setZoomControlsEnabled(true);
			aMap.getUiSettings().setScaleControlsEnabled(true);
		}
		aMap.setOnCameraChangeListener(this);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		radius=500;
		circle=new care.bean.Circle();
	}
	
	protected void onResume(){
		super.onResume();
		mapView.onResume();
	}
	
	protected void onPause(){
		super.onPause();
		mapView.onPause();
		deactivate();
	}
	
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	protected void onDestroy(){
		super.onDestroy();
		mapView.onDestroy();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.square_circle:
			if(isCircle){
				square_circle.setImageResource(R.drawable.square);
				isCircle=false;
				if(mCircle!=null&&point!=null){
					mCircle.remove();
					point.remove();
				}
				LatLng center=mCircle.getCenter();
				mPolygon=aMap.addPolygon(new PolygonOptions()
				.addAll(createRectangle(center, 1, 1))
				.fillColor(circleColor).strokeColor(strokeColor).strokeWidth(1));
			}else{
				if(mPolygon!=null){
					mPolygon.remove();
				}
				square_circle.setImageResource(R.drawable.circle);
				isCircle=true;
				LatLng center=mCircle.getCenter();
				CircleOptions circleOption=new CircleOptions();
				circleOption.center(center);
				circleOption.radius(radius);
				circleOption.fillColor(circleColor);
				circleOption.strokeWidth(2);
				circleOption.strokeColor(strokeColor);
				mCircle=aMap.addCircle(circleOption);
				circleOption.radius(8);
				circleOption.fillColor(pointColor);
				circleOption.strokeWidth(0);
				point=aMap.addCircle(circleOption);
			}
			break;
		case R.id.public_head_back_button:
			Trace.i("back button is click!!!");
			finish();
			break;
		case R.id.fenceAddButton:
			Trace.i("add button is click!!!");
			new Thread(mRunnable).start();
			break;
		case R.id.fenceEditButton:
			isAdd=true;
			isEdit=true;
			editButton.setVisibility(View.GONE);
			okButton.setVisibility(View.VISIBLE);
	    	name_edit_area.setVisibility(View.VISIBLE);
	    	radius_change_area.setVisibility(View.VISIBLE);
			break;
		case R.id.redu_button:
			if(radiusSelect.getProgress()>0){
				radiusSelect.setProgress(radiusSelect.getProgress() - 1);
			}
			break;
		case R.id.plus_button:
			if(radiusSelect.getProgress()<radiusSelect.getMax()){
				radiusSelect.setProgress(radiusSelect.getProgress() + 1);
			}
			break;
		case R.id.fence_edit:
//			showSelectFenceNameDialog();
			break;
		}
	}

	/**
	 * 
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if(mListener!=null&&amapLocation!=null){
			if(amapLocation.getAMapException().getErrorCode()==0){
				Trace.i("location changed!!!");
//				mListener.onLocationChanged(amapLocation);
				LatLng center=new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
				CircleOptions circleOption=new CircleOptions();
				circleOption.center(center);
				circleOption.radius(500);
				circleOption.fillColor(Color.argb(180, 224, 171, 10));
				circleOption.strokeWidth(1);
				mCircle=aMap.addCircle(circleOption);
				circleOption.radius(2);
				circleOption.fillColor(Color.argb(255, 90, 150, 10));
				circleOption.strokeWidth(0);
				point=aMap.addCircle(circleOption);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 16));
//				locationAddress.setText(amapLocation.getAddress());
//				Bundle locBundle=amapLocation.getExtras();
//				if(locBundle!=null){
//					locationMessage.setText(locBundle.getString("desc"));
//				}
				
			}
		}
		
	}
	
	
	/**婵�椿瀹氫綅
	 * 
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if(mAMapLocationManager==null){
			Trace.i("location activate!!!!!");
			mAMapLocationManager=LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60*1000, 10, this);
			
		}
	}
	
	/**鍏抽棴瀹氫綅
	 * 
	 */
	@Override
	public void deactivate() {
		mListener=null;
		if(mAMapLocationManager!=null){
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager=null;
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		if(isAdd){
			LatLng center=new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude);
			if(isCircle){
				if(mCircle!=null&&point!=null){
					point.remove();
					mCircle.remove();
				}
				CircleOptions circleOption=new CircleOptions();
				circleOption.center(center);
				circleOption.radius(radius);
				circleOption.fillColor(circleColor);
				circleOption.strokeColor(strokeColor);
				circleOption.strokeWidth(2);
				mCircle=aMap.addCircle(circleOption);
				circleOption.radius(8);
				circleOption.fillColor(pointColor);
				circleOption.strokeWidth(0);
				point=aMap.addCircle(circleOption);
			}else{
				if(mPolygon!=null){
					mPolygon.remove();
				}
				mPolygon=aMap.addPolygon(new PolygonOptions()
				.addAll(createRectangle(center, 1, 1))
				.fillColor(circleColor).strokeColor(strokeColor).strokeWidth(1));
			}
			
		}
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		if(isAdd){
			lng=cameraPosition.target.longitude;
			lat=cameraPosition.target.latitude;
			LatLng center=new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude);
			LatLonPoint latLonPoint=new LatLonPoint(cameraPosition.target.latitude,cameraPosition.target.longitude);
			RegeocodeQuery query = new RegeocodeQuery(latLonPoint,200,GeocodeSearch.AMAP);
			geocoderSearch.getFromLocationAsyn(query);
			aMap.clear();
			if(isCircle){
				if(mCircle!=null&&point!=null){
					point.remove();
					mCircle.remove();
				}
				CircleOptions circleOption=new CircleOptions();
				circleOption.center(center);
				circleOption.radius(radius);
				circleOption.fillColor(circleColor);
				circleOption.strokeColor(strokeColor);
				circleOption.strokeWidth(2);
				mCircle=aMap.addCircle(circleOption);
				circleOption.radius(8);
				circleOption.fillColor(pointColor);
				circleOption.strokeWidth(0);
				point=aMap.addCircle(circleOption);
			}else{
				if(mPolygon!=null){
					mPolygon.remove();
				}
				mPolygon=aMap.addPolygon(new PolygonOptions()
				.addAll(createRectangle(center, 1, 1))
				.fillColor(circleColor).strokeColor(strokeColor).strokeWidth(1));
			}
		}
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if(rCode==0){
			if(result!=null&&result.getRegeocodeAddress()!=null&&result.getRegeocodeAddress().getFormatAddress()!=null){
				String address=result.getRegeocodeAddress().getFormatAddress();
				List<BusinessArea> buList=result.getRegeocodeAddress().getBusinessAreas();
				List<PoiItem> pList=result.getRegeocodeAddress().getPois();
				String buAreas="";
				for(int i=0;i<buList.size();i++){
					buAreas=buList.get(i).getName()+";";
				}
				String number=result.getRegeocodeAddress().getStreetNumber().getNumber();
				String street=result.getRegeocodeAddress().getStreetNumber().getStreet();
				String nei=result.getRegeocodeAddress().getNeighborhood();
				if(pList.size()>0){
					locationAddress.setText(pList.get(0).toString());
				}
				locationMessage.setText(address);
			}
		}else{
			Toast.makeText(this,"net_work_error",Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		radius=500+progress*50;
		fenceRadius.setText(getString(R.string.circle_radius)+radius+"m");
		if(hasInit){
			circleChange();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	
	String fenceAdd(String m) {
		String p_sid = tools.get_user_id();
		String p_imei = tools.get_current_device_id();//tools.get_current_baby_imei();
		double p_lng = lng;
		double p_lat = lat;
		String p_name = locationAddress.getText().toString();
		String p_addr = locationMessage.getText().toString();
		String p_radius = Integer.toString(radius);
		String p_id="";
		String type="";
		if(!isEdit){
			p_id = "0";
			type = "1";
		}else{
			p_id = circle.getId();
			type = "0";
		}
		circle.setImei(p_imei);
		circle.setLng(Double.toString(lng));
		circle.setLat(Double.toString(lat));
		circle.setName(p_name);
		circle.setAddr(p_addr);
		circle.setRadius(p_radius);
		JSONObject json_fence_add = new JSONObject();
		try {
			json_fence_add.put("type", type);
			json_fence_add.put("user_id", p_sid);
			Trace.i("fence user id===" + p_sid);
			json_fence_add.put("device_imei", p_imei);
			Trace.i("fence device id===" + p_imei);
			json_fence_add.put("lng", p_lng);
			json_fence_add.put("lat", p_lat);
			json_fence_add.put("device_safe_name",URLEncoder.encode(p_name,"utf-8"));
			json_fence_add.put("device_safe_addr", URLEncoder.encode(p_addr,"utf-8"));
			json_fence_add.put("device_safe_range", p_radius);
			json_fence_add.put("device_safe_id", p_id);
			json_fence_add.put("device_safe_effect_time", "");
			String json_change_result = Utils.GetService(json_fence_add, Constants.FENCE_EDIT);
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

	Runnable mRunnable=new Runnable(){

		@Override
		public void run() {
			final String add_result = fenceAdd("range-edit");
			mHandler.post(new Runnable(){
				@Override
				public void run() {
//					JSONObject object=new JSONObject();
//					try {
//						object.put("result", "0");
//						object.put("reason", "正确");
//						object.put("id", "23");
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					String add_result=object.toString();
					fenceAddResultCheck(add_result);
				}
			});
			
		}
		
	};
	
	private void fenceAddResultCheck(String result_check) {

		if (result_check.equals("0") || result_check.equals("-1")) {
			Toast.makeText(this, "服务器问题", Toast.LENGTH_SHORT).show();
		} else {
			HashMap<String, String> map;
			try {
				Trace.i("result_check====" + result_check);
				map = BeanUtils.getJSONParserResult(result_check);
				String result=map.get("resultCode");
//				String reason=map.get("reason");
				String id=map.get("device_safe_id");
				if("1".equals(result)){
					Intent data = new Intent();
	            	Bundle bundle=new Bundle();
	            	if(!isEdit){
	            		Toast.makeText(this, getString(R.string.fence_add_success), Toast.LENGTH_SHORT).show();
	            		circle.setId(id);
	            		bundle.putSerializable("circle",circle);
		            	data.putExtras(bundle);
		            	setResult(0,data);
	            	}else{
	            		Toast.makeText(this, getString(R.string.fence_edit_success), Toast.LENGTH_SHORT).show();
	            		bundle.putSerializable("editCircle",circle);
	            		bundle.putInt("position", position);
		            	data.putExtras(bundle);
		            	setResult(0,data);
	            	}
	            	
					finish();
				}else if("0".equals(result)){
					if(!isEdit){
						Toast.makeText(this, getString(R.string.fence_add_fail), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(this, getString(R.string.fence_edit_fail), Toast.LENGTH_SHORT).show();
					}
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void circleChange() {
		mCircle.setRadius(radius);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void showSelectFenceNameDialog(){
		final View view=LayoutInflater.from(this).inflate(R.layout.dialog_fence_name_select, null);
		final EditText relationEdit=(EditText)view.findViewById(R.id.fence_name_edit);
//		relationEdit.setText(relationInput.getText().toString());
		Button fatherButton=(Button)view.findViewById(R.id.home1);
		fatherButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				relationEdit.setText(getString(R.string.fence_name1));
				
			}
		});
		Button momButton=(Button)view.findViewById(R.id.home2);
		momButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				relationEdit.setText(getString(R.string.fence_name2));
				
			}
		});
		Button grandpaButton=(Button)view.findViewById(R.id.home3);
		grandpaButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				relationEdit.setText(getString(R.string.fence_name3));
				
			}
		});
		Button grandmoButton=(Button)view.findViewById(R.id.home4);
		grandmoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				relationEdit.setText(getString(R.string.fence_name4));
				
			}
		});
		Button auntButton=(Button)view.findViewById(R.id.home5);
		auntButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				relationEdit.setText(getString(R.string.fence_name5));
				
			}
		});
		Button otherButton=(Button)view.findViewById(R.id.home6);
		otherButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				relationEdit.setText(getString(R.string.fence_name6));
				
			}
		});
		AlertDialog dialog=new AlertDialog.Builder(this).setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				locationAddress.setText(relationEdit.getText().toString());
				
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
			
		}).setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss(DialogInterface arg0) {
				
			}
			
		}).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	public void addCircle(){
		if(mCircle!=null&&point!=null){
			point.remove();
			mCircle.remove();
		}
		LatLng center=new LatLng(Double.parseDouble(circle.getLat()),Double.parseDouble(circle.getLng()));
		CircleOptions circleOption=new CircleOptions();
		circleOption.center(center);
		circleOption.radius(radius);
		circleOption.fillColor(circleColor);
		circleOption.strokeWidth(2);
		circleOption.strokeColor(strokeColor);
		mCircle=aMap.addCircle(circleOption);
		circleOption.radius(8);
		circleOption.fillColor(pointColor);
		circleOption.strokeWidth(0);
		point=aMap.addCircle(circleOption);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 16));
		locationAddress.setText(circle.getName());
		locationMessage.setText(circle.getAddr());
	}

	/**
	 * 生成一个长方形的四个坐标点
	 */
	private List<LatLng> createRectangle(LatLng center, double halfWidth,
			double halfHeight) {
		return Arrays.asList(new LatLng(center.latitude - halfHeight,
				center.longitude - halfWidth), new LatLng(center.latitude
				- halfHeight, center.longitude + halfWidth), new LatLng(
				center.latitude + halfHeight, center.longitude + halfWidth),
				new LatLng(center.latitude + halfHeight, center.longitude
						- halfWidth));
	}
}
