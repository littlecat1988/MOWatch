package care;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.BusinessArea;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import com.mtk.btnotification.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import care.application.XcmApplication;
import care.bean.BaoBeiBean;
import care.bean.Circle;
import care.bean.Position;
import care.menu.LeftDrawerView;
import care.utils.BeanUtils;
import care.utils.Constants;
import care.utils.DealImage;
import care.utils.Trace;
import care.utils.Utils;
import care.utils.XcmTools;

public class LocationActivity extends Activity implements View.OnClickListener,LocationSource,AMapLocationListener,OnGeocodeSearchListener{
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private GeocodeSearch geocoderSearch;
//	private ImageButton /*menuOpen,*/toLocus;
	private TextView headName;
	private XcmTools tools;
	private ArrayList<BaoBeiBean> babyList;
	private ArrayList<Circle> circleList;
	private String lastTime;
	private View markerView;
	private TextView babyName;
	private TextView locDayView;
	private TextView locTimeView;
	private TextView batteryView;
	private ImageView batteryIconView;
	private TextView locAddress;
	private ImageView locType;
//	private TextView locPrecision;
	private Marker currentMarker,haloMarker;
	private MarkerOptions markerOption;
	private CameraUpdate cameraUpdate;
	private Handler mHandler;
	private Handler dataHandler;
	private SimpleDateFormat sdf;
	private int queryResult=1;
	private LinearLayout progressBar;
	private TextView progress_text;
	private int circleColor=Color.argb(100, 155, 200, 89);
	private int pointColor=Color.argb(255, 155, 171, 10);
	private int strokeColor=Color.argb(255, 155, 171, 10);
	private View fenceMarkerView;
	private TextView fenceNameView;
	private String currentImei;
	private Position currentPosition;
	private Button mapType;
	private boolean is3D=false;
	private ImageButton toLocus,to_location,to_listen,to_chat;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private ImageButton backButton;
	private BaoBeiBean currentBaby;
	
	private LeftDrawerView drawerView;
	private SlidingMenu slideMenu;
	private AlarmManager alarmManager;
	
	private TextView isRead;
	private XcmApplication mInstance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		mInstance= XcmApplication.getInstance();
		initData();
		initSlidingMenu();
		mapView = (MapView)findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		init();
		to_location.setImageResource(R.drawable.icon_location_pressed);
		progressBar.setVisibility(View.VISIBLE);
		reLoad();
	}

	private void startBroadcast() {
		Intent serviceIntent = new Intent(Constants.GETDOWNLOADURL);
		sendBroadcast(serviceIntent);
	}
	private void downloadResultCheck(String result_check) {

//		drawerView.init_head();//delete by lxiang for hide back button 20151224
		if (result_check.equals("0") || result_check.equals("-1")) {
			Toast.makeText(this, "服务器出错啦！", Toast.LENGTH_SHORT).show();
		} else {
			HashMap<String, String> map;
			try {
				map = BeanUtils.getJSONParserResult(result_check);
				String result=map.get("resultCode");
				if("1".equals(result)){
					String babyList=map.get("device_message");
					tools.set_babyList(babyList);   //将设备信息存到本地
					JSONArray babyArray=new JSONArray(babyList);
					int length=babyArray.length();
					for(int i=0;i<length;i++){
						JSONObject babyObject=(JSONObject)babyArray.get(i);
						HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
						BaoBeiBean baobei= BeanUtils.getBaoBei(babyMap);
						String currentId=tools.get_current_device_id();
						if(currentId==null||"".equals(currentId)||"null".equals(currentId)||"0".equals(currentId)){
						
							tools.set_current_device_id(baobei.getImei());
						}
					}
					if(length==0){
						Toast.makeText(LocationActivity.this, getString(R.string.no_device), Toast.LENGTH_SHORT).show();
						Intent i = new Intent(LocationActivity.this, BondDeviceActivity.class);
						startActivity(i);
					}
				}else if("0".equals(result)){
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
	Runnable dataRunnable=new Runnable(){

		@Override
		public void run() {
			final String add_result = downloadData("");
			dataHandler.post(new Runnable(){
				@Override
				public void run() {
					downloadResultCheck(add_result);
				}
			});
		}
	};
	public void initData(){
		dataHandler=new Handler();
		tools=new XcmTools(this);
		new Thread(dataRunnable).start();
	}
	private void initSlidingMenu() {
//		drawerView = new LeftDrawerView(this);
//		slideMenu = drawerView.initSlidingMenu();
		//delete by lxiang for hide back button 20151224
	}
	public void init(){
		isRead=(TextView) findViewById(R.id.isRead);
		backButton=(ImageButton)findViewById(R.id.back_button);
		backButton.setImageResource(R.drawable.head_left1);
		backButton.setOnClickListener(this);
		mapType=(Button)findViewById(R.id.map_type);
		mapType.setOnClickListener(this);
		to_location=(ImageButton)findViewById(R.id.to_location);
		to_location.setOnClickListener(this);
		toLocus=(ImageButton)findViewById(R.id.toLocus);
		toLocus.setOnClickListener(this);
		to_listen=(ImageButton)findViewById(R.id.to_listen);
		to_listen.setOnClickListener(this);
		to_chat=(ImageButton) findViewById(R.id.to_chat);
		to_chat.setOnClickListener(this);
		headName=(TextView)findViewById(R.id.title_string);
		headName.setText(getString(R.string.position));
		progressBar=(LinearLayout)findViewById(R.id.progress_bar);
		progress_text=(TextView)findViewById(R.id.progress_text);
		progress_text.setText(R.string.loading);
		if(aMap==null){
			aMap=mapView.getMap();
			aMap.setLocationSource(this);
			aMap.getUiSettings().setMyLocationButtonEnabled(false);
			aMap.getUiSettings().setZoomControlsEnabled(false);
			aMap.getUiSettings().setScaleControlsEnabled(false);
		}
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		babyList=new ArrayList<BaoBeiBean>();
		circleList=new ArrayList<Circle>();
//		markerView=LayoutInflater.from(getActivity()).inflate(R.layout.location_marker, null);
//		locTimeView=(TextView)markerView.findViewById(R.id.loc_time);
//		batteryView=(TextView)markerView.findViewById(R.id.battery_percent);
//		locAddress=(TextView)markerView.findViewById(R.id.loc_address);
		markerOption=new MarkerOptions();
		mHandler=new Handler();
		sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mTimer = new Timer();
		mTimerTask = new MyTimerTask();
		initBabyList();
	}

	public void onResume(){
		super.onResume();
		initData();
		mapView.onResume();
		mTimer.cancel();
		mTimerTask.cancel();
		mTimer = new Timer();
		mTimerTask = new MyTimerTask();
		mTimer.schedule(mTimerTask, 60*1000, 60*1000);
		startBroadcast();
	}
	public void reLoad(){
		progressBar.setVisibility(View.VISIBLE);
		aMap.clear();
//		babyList.clear();
		circleList.clear();
		mHandler.postDelayed(new Runnable(){

			@Override
			public void run() {
				mHandler.post(new Runnable(){

					@Override
					public void run() {
						progressBar.setVisibility(View.GONE);
						
					}});
				
			}}, 1000);
		mHandler.removeCallbacks(mRunnable);
		new Thread(mRunnable).start();
//		mHandler.postDelayed(mRunnable, 1000*30);
//		initCircleList();
		setCurrentCircles();
	}
	
	public void onPause(){
		super.onPause();
		mTimer.cancel();
		mTimerTask.cancel();
		mHandler.removeCallbacks(mRunnable);
		mapView.onPause();
		deactivate();
	}
	
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	public void onDestroy(){
		super.onDestroy();
		mTimer.cancel();
		mHandler.removeCallbacks(mRunnable);
		mapView.onDestroy();
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
//		case R.id.menu_open:
//			mMenu.toggle();
//			break;
		case R.id.back_button:
//			if (slideMenu.isMenuShowing()) {
//				slideMenu.showContent();
//			} else {
//				slideMenu.showMenu();
//			}
			//delete by lxiang for hide back button 20151224
			break;
		case R.id.to_location:
			reLoad();
			break;
		case R.id.toLocus:
			Intent i=new Intent(this, LocusActivity.class);
			startActivity(i);
			break;
		case R.id.to_listen:
			Calldialog();
			break;
		case R.id.to_chat:
			Intent chatIn=new Intent(this, ChatActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("type","1");
			chatIn.putExtras(bundle);
			startActivity(chatIn);
			break;
			
		case R.id.map_type:
			if(is3D){
				CameraUpdate cameraUpdate=CameraUpdateFactory.changeTilt(0);
				aMap.moveCamera(cameraUpdate);
				mapType.setText("2D");
				is3D=false;
			}else{
				CameraUpdate cameraUpdate=CameraUpdateFactory.changeTilt(90);
				aMap.moveCamera(cameraUpdate);
				mapType.setText("3D");
				is3D=true;
			}
			break;
		}
		
	}
	
	public MapView getMapView(){
		return mapView;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if(mListener!=null&&amapLocation!=null){
			if(amapLocation.getAMapException().getErrorCode()==0){
				mListener.onLocationChanged(amapLocation);
			}
		}
		
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if(mAMapLocationManager==null){
			mAMapLocationManager=LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60*1000, 10, this);
			
		}
		
	}

	@Override
	public void deactivate() {
		mListener=null;
		if(mAMapLocationManager!=null){
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager=null;
	}
	
	public void initBabyList(){
		currentImei=tools.get_current_device_id();
		String babyList=tools.get_babyList();
		try {
			JSONArray baby_list=new JSONArray(babyList);
			for(int i=0;i<baby_list.length();i++){
				JSONObject babyObject=baby_list.getJSONObject(i);
				HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
				BaoBeiBean baby= BeanUtils.getBaoBei(babyMap);
				if(baby.getImei().equals(currentImei)){
					currentBaby=baby;
				}
//				this.babyList.add(baby);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void initCircleList(){
		String circleList=tools.get_circleList();
		try {
			JSONArray circle_list=new JSONArray(circleList);
			for(int i=0;i<circle_list.length();i++){
				JSONObject circleObject=circle_list.getJSONObject(i);
				HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(circleObject.toString());
				Circle circle= BeanUtils.getCircle(babyMap);
				if(circle.getImei().equals(currentImei)){
					this.circleList.add(circle);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setCurrentCircles(){
		for(int i=0;i<circleList.size();i++){
			Circle circle=circleList.get(i);
			LatLng center=new LatLng(Double.parseDouble(circle.getLat()),Double.parseDouble(circle.getLng()));
			CircleOptions circleOption=new CircleOptions();
			circleOption.center(center);
			circleOption.radius(Integer.parseInt(circle.getRadius()));
			circleOption.fillColor(circleColor);
			circleOption.strokeWidth(2);
			aMap.addCircle(circleOption);
			fenceMarkerView=LayoutInflater.from(this).inflate(R.layout.fence_marker, null);
			fenceNameView=(TextView)fenceMarkerView.findViewById(R.id.fence_name);
			fenceNameView.setText(circle.getName());
			fenceNameView.setTextColor(pointColor);
			MarkerOptions markerOption=new MarkerOptions();
			markerOption.position(center);
			markerOption.perspective(false);
			markerOption.icon(BitmapDescriptorFactory.fromView(fenceMarkerView));
			aMap.addMarker(markerOption);
		}
	}
	
	public void setCurrentPosition(){
//		Baby currentBaby=this.babyList.get(this.babyList.size()-1);
//		Position p=currentBaby.getPosition();
		Trace.i("setCurrentPosition");
		Position p=currentPosition;
	
		double lat=Double.parseDouble(p.getLat());
		double lng=Double.parseDouble(p.getLng());
//		double lat=22.533709;
//		double lng=114.029235;
		String times=p.getTime();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		try {
			date=sdf.parse(times);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String locTime=sdf.format(date);
		lastTime=locTime;
		String batteryPercent=p.getBattery()+"%";
		String pType=p.getPtype();
		String pTypeString="";
		LatLng latLng=new LatLng(lat,lng);
		LatLonPoint point=new LatLonPoint(lat,lng);
		markerView=LayoutInflater.from(this).inflate(R.layout.location_marker, null);
		locDayView=(TextView)markerView.findViewById(R.id.loc_day);
		locTimeView=(TextView)markerView.findViewById(R.id.loc_time);
		batteryView=(TextView)markerView.findViewById(R.id.battery_percent);
		batteryIconView=(ImageView)markerView.findViewById(R.id.battery_icon);
		locAddress=(TextView)markerView.findViewById(R.id.loc_address);
		locType=(ImageView)markerView.findViewById(R.id.loc_type);
		babyName=(TextView)markerView.findViewById(R.id.loc_baby_name);
		if(currentBaby!=null){
			babyName.setText(currentBaby.getName());
		}
		RegeocodeQuery query = new RegeocodeQuery(point,200,GeocodeSearch.AMAP);
		geocoderSearch.getFromLocationAsyn(query);
		String day="";
		String time="";
		if(locTime.contains(" ")){
			day=locTime.split(" ")[0];
			time=locTime.split(" ")[1];
		}
		locDayView.setText(day);
		locTimeView.setText(time);
		batteryView.setText(batteryPercent);
		if("0".equals(pType)){
			pTypeString=getString(R.string.loc_lbs);
			locType.setBackgroundResource(R.drawable.gps_icon);
		}else if("1".equals(pType)){
			pTypeString=getString(R.string.loc_gps);
			locType.setBackgroundResource(R.drawable.gps_icon);
		}
		setBatteryIcon(p.getBattery());
//		locPrecision.setText(pTypeString);
		markerOption=new MarkerOptions();
		markerOption.position(latLng);
		cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng, 16);
		aMap.moveCamera(cameraUpdate);
		ArrayList<BitmapDescriptor> haloList = new ArrayList<BitmapDescriptor>();
		TypedArray array = this.getResources().obtainTypedArray(R.array.halos);
		for(int i=0;i<array.length();i++){
			haloList.add(BitmapDescriptorFactory.fromResource(array.getResourceId(i,0)));
		}
		array.recycle();
		MarkerOptions options = new MarkerOptions();
		options.icons((ArrayList<BitmapDescriptor>) haloList).period(7).position(latLng).anchor(0.5f, 0.52f);
		if(haloMarker==null){
			haloMarker=aMap.addMarker(options);
		}else{
			haloMarker.remove();
			haloMarker=aMap.addMarker(options);
		}
	}
	
	public void setBatteryIcon(String batteryPercent){
		int battery=Integer.parseInt(batteryPercent);
		BitmapDrawable drawable=new BitmapDrawable();
		if(battery<=20){
			drawable= DealImage.readBitmapDrawable(this, R.drawable.battery_icon1);
		}else if(battery>20&&battery<=40){
			drawable= DealImage.readBitmapDrawable(this, R.drawable.battery_icon2);
		}else if(battery>40&&battery<=60){
			drawable= DealImage.readBitmapDrawable(this, R.drawable.battery_icon3);
		}else if(battery>60&&battery<=80){
			drawable= DealImage.readBitmapDrawable(this, R.drawable.battery_icon4);
		}else if(battery>80){
			drawable= DealImage.readBitmapDrawable(this, R.drawable.battery_icon5);
		}
		batteryIconView.setBackgroundDrawable(drawable);
	}

	String GetPosition(String m) {

		JSONObject json_get_position = new JSONObject();
		try {
			String deviceId=tools.get_current_device_id();
			json_get_position.put("serial_number", deviceId);
			String get_position_result = Utils.GetService(json_get_position, Constants.LOCATION);
			if (get_position_result.equals("0")) {
				return "0";
			} else if (get_position_result.equals("-1")) {
				return "-1";

			} else {
				return get_position_result;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";

	}
	public class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			String position_result=GetPosition("");
			try {
				HashMap<String,String> map= BeanUtils.getJSONParserResult(position_result);
				String result=map.get("resultCode");
				Trace.i("1");
				if("1".equals(result)){
					String location=map.get("location");
					Trace.i("location mess====" + location);
					if(location.length()>0){
						Trace.i("2");
						JSONObject posObject=new JSONObject(location);
						HashMap<String,String> posMap= BeanUtils.getJSONParserResult(posObject.toString());
						Position position = BeanUtils.getPosition(posMap);
						currentPosition=position;
					}
					queryResult=0;
				}else if("0".equals(result)){
					queryResult=1;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					if(queryResult==0){
						if(currentPosition!=null){
							setCurrentPosition();
						}
					}else if(queryResult==1){
						
					}
				}
			});
		}
	}
	Runnable mRunnable=new Runnable(){

		@Override
		public void run() {
			final String position_result=GetPosition("");
			
//			String position_result="{'msglist':[{'msg':'sos button click','msgdate':'2015-06-23 10:04:33','msgid':'1','msgtype':'sos','positon':{'ac':'0','battery':'83','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'100','status':'GSM100;BAT083','time':'2015-06-23 10:04:33','x':'113.941645689116','y':'22.548454612788234'}},{'msg':'sos button click','msgdate':'2015-06-23 10:05:32','msgid':'2','msgtype':'sos','positon':{'ac':'0','battery':'83','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'100','status':'GSM100;BAT083','time':'2015-06-23 10:05:32','x':'113.941645689116','y':'22.548454612788234'}},{'msg':'sos button click','msgdate':'2015-06-23 10:06:32','msgid':'3','msgtype':'sos','positon':{'ac':'0','battery':'83','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'100','status':'GSM100;BAT083','time':'2015-06-23 10:06:32','x':'113.941645689116','y':'22.548454612788234'}}],'postionlist':[{'ac':'0','battery':'55','did':'9','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'57','status':'GSM057;BAT055','time':'2015-07-13 11:53:21','x':'113.94093683911603','y':'22.550980112788235'}],'reason':'','result':'0'}";
			try {
				Trace.i("p_result====" + position_result);
				HashMap<String,String> map= BeanUtils.getJSONParserResult(position_result);
				String result=map.get("resultCode");
				Trace.i("1");
				if("1".equals(result)){
					String location=map.get("location");
					Trace.i("location mess====" + location);
					if(location.length()>0){
						Trace.i("2");
						JSONObject posObject=new JSONObject(location);
						HashMap<String,String> posMap= BeanUtils.getJSONParserResult(posObject.toString());
						Position position = BeanUtils.getPosition(posMap);
						currentPosition=position;
					}
					queryResult=0;
				}else if("-1".equals(result)){
					queryResult=1;
				}
			} catch (JSONException e) {
				Trace.i("exception=======" + e.toString());
				e.printStackTrace();
			}
			mHandler.post(new Runnable(){
				@Override
				public void run() {
//					new Thread(mRunnable1).start();
					if (position_result.equals("0") || position_result.equals("-1")) {
						Toast.makeText(LocationActivity.this, getString(R.string.server_time_out), Toast.LENGTH_SHORT)
								.show();
					} 
					if(queryResult==0){
						if(currentPosition!=null){
							setCurrentPosition();
						}else{
							Toast.makeText(LocationActivity.this, getString(R.string.server_no_data), Toast.LENGTH_SHORT)
							.show();
						}
					}else if(queryResult==1){
						
					}
					
				}
				
			});
			
		}
		
	};
	
	Runnable mRunnable1=new Runnable(){

		@Override
		public void run() {
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String position_result=GetPosition("");
//			String position_result="{'msglist':[{'msg':'sos button click','msgdate':'2015-06-23 10:04:33','msgid':'1','msgtype':'sos','positon':{'ac':'0','battery':'83','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'100','status':'GSM100;BAT083','time':'2015-06-23 10:04:33','x':'113.941645689116','y':'22.548454612788234'}},{'msg':'sos button click','msgdate':'2015-06-23 10:05:32','msgid':'2','msgtype':'sos','positon':{'ac':'0','battery':'83','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'100','status':'GSM100;BAT083','time':'2015-06-23 10:05:32','x':'113.941645689116','y':'22.548454612788234'}},{'msg':'sos button click','msgdate':'2015-06-23 10:06:32','msgid':'3','msgtype':'sos','positon':{'ac':'0','battery':'83','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'100','status':'GSM100;BAT083','time':'2015-06-23 10:06:32','x':'113.941645689116','y':'22.548454612788234'}}],'postionlist':[{'ac':'0','battery':'55','did':'9','imei':'352158041656000','ptype':'0','siggps':'0','siggsm':'57','status':'GSM057;BAT055','time':'2015-07-13 11:53:21','x':'113.94093683911603','y':'22.550980112788235'}],'reason':'','result':'0'}";
			try {
				HashMap<String,String> map= BeanUtils.getJSONParserResult(position_result);
				String result=map.get("resultCode");
				Trace.i("1");
				if("1".equals(result)){
					String location=map.get("location");
					Trace.i("location mess====" + location);
					if(location.length()>0){
						Trace.i("2");
						JSONObject posObject=new JSONObject(location);
						HashMap<String,String> posMap= BeanUtils.getJSONParserResult(posObject.toString());
						Position position = BeanUtils.getPosition(posMap);
						currentPosition=position;
					}
					queryResult=0;
				}else if("-1".equals(result)){
					queryResult=1;
				}
			} catch (JSONException e) {
				Trace.i("exception=======" + e.toString());
				e.printStackTrace();
			}
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					new Thread(mRunnable1).start();
					if(queryResult==0){
						if(currentPosition!=null){
							setCurrentPosition();
						}
					}else if(queryResult==1){
						
					}
				}
			});
		}
	};
	
	public void sortPositionList(ArrayList<Position> posList){
		for(int i=0;i<posList.size()-1;i++){
			for(int j=i+1;j<posList.size();j++){
				Position positionA=posList.get(i);
				Position positionB=posList.get(j);
				try {
					Date timeA=sdf.parse(positionA.getTime());
					Date timeB=sdf.parse(positionB.getTime());
					if(timeA.getTime()>timeB.getTime()){
						posList.set(i, positionB);
						posList.set(j, positionA);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		
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
				locAddress.setText(address);
				
//				aMap.clear();
//				Trace2.i("1");
				if(currentMarker==null){
					markerOption.icon(BitmapDescriptorFactory.fromView(markerView));
					currentMarker=aMap.addMarker(markerOption);
				}else{
					currentMarker.remove();
					TextView text=(TextView)markerView.findViewById(R.id.loc_time);
					String loc_time=text.getText().toString();
					markerOption.icon(BitmapDescriptorFactory.fromView(markerView));
					currentMarker=aMap.addMarker(markerOption);
				}
			}
		}else{
			
		}
	}
	
	void Calldialog() {

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // Ϊ�Ի�����İ�ť���Ӽ����¼�
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case AlertDialog.BUTTON_POSITIVE: // �����ȷ��
					new Thread(mRunnable2).start();
					break; // ����

				case AlertDialog.BUTTON_NEGATIVE: // �����ȡ��
					
					break; // ����
				}
			}
		};

		AlertDialog ad = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.qingtin_alertdialog_title))
				.setMessage(getString(R.string.qingtin_alertdialog_count1)
								+ getString(R.string.qingtin_alertdialog_count2))
				.setPositiveButton(getString(R.string.ok),
						listener)
				.setNegativeButton(getString(R.string.cancel),
						listener).show();

	}
	
	String listen(String m) {
		JSONObject json_listen = new JSONObject();
		String phone = tools.get_login_phone();
		String deviceId = tools.get_current_device_id();
		try {
			json_listen.put("phone", phone);
			json_listen.put("serial_number", deviceId);
			String get_position_result = Utils.GetService(json_listen, Constants.LISTEN);
			if (get_position_result.equals("0")) {
				return "0";
			} else if (get_position_result.equals("-1")) {
				return "-1";

			} else {
				return get_position_result;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";

	}
	
	Runnable mRunnable2=new Runnable(){

		@Override
		public void run() {
			final String listen_result=listen("");
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					try {
						HashMap<String,String> map= BeanUtils.getJSONParserResult(listen_result);
						String result=map.get("resultCode");
						if("1".equals(result)){
							Toast.makeText(LocationActivity.this, getString(R.string.listen_success), Toast.LENGTH_SHORT).show();
						}else if("0".equals(result)){
							Toast.makeText(LocationActivity.this, getString(R.string.listen_failed), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						Trace.i("exception=======" + e.toString());
						e.printStackTrace();
					}
					
				}
				
			});
			
		}
		
	};
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
                isRead.setVisibility(View.VISIBLE);
                isRead.setText(mInstance.OFF_LINE_COUNT+"");
            }
        }
    };
}
