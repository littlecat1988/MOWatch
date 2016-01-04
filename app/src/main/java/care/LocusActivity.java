package care;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mtk.btnotification.R;

import care.menu.KCalendar.OnCalendarClickListener;
import care.menu.KCalendar.OnCalendarDateChangedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import care.bean.BaoBeiBean;
import care.bean.Position;
import care.menu.KCalendar;
import care.utils.BeanUtils;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;
import care.utils.XcmTools;

public class LocusActivity extends Activity implements OnClickListener,
		SeekBar.OnSeekBarChangeListener,OnGeocodeSearchListener {

	private ImageButton public_head_back_button, public_head_right_button;
	private TextView public_head_text_center;
	private ImageButton iocus_left_button, iocus_right_button;
	private ImageView pointAndLine;
	private static final int REGISTER = 0x0001;
	private SeekBar seekBar1;

	private TextView locus_ceshi;

	String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式
	String lastDate = null;
	String position[] = {};
	String guiji[] = {};
	String guiji2[] = {};

	private Handler mHandler;
//	private Handler mHandlerDate;
	private XcmTools tools;
	private ArrayList<Position> positionList;
	private ArrayList<Position> positionList2;
	private SimpleDateFormat sdf;
	private int queryResult=1;
	private MapView mapView;
	private AMap aMap;
	private GeocodeSearch geocoderSearch;
	private View markerView;
	private TextView locAddress;
	private TextView babyName;
	private MarkerOptions markerOption2;
	private Marker selecedMarker;
	private PolylineOptions polylineOptions;
	private Polyline polyline;
	private boolean isScroll=false;
	private Button map_type2;
	private boolean is3D=false;
	private ImageButton to_location,toLocus,to_listen,to_chat;
	private BaoBeiBean currentBaby;
	private boolean isClick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.locus_activity);
		mapView = (MapView)findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		init();
		toLocus.setImageResource(R.drawable.icon_locus_pressed);
		new Thread(mRunnable).start();
	}
	
	private final Handler mHandlerDate = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case REGISTER:
				break;
			
			}
		}
	};
	
	private void sendMessage(int what, Object obj) {
		
		Message msg = mHandlerDate.obtainMessage(what, obj);
		mHandlerDate.sendMessage(msg);
	}
	
	
	private void init() {
		map_type2 = (Button) findViewById(R.id.map_type2);
		map_type2.setOnClickListener(this);
		to_location=(ImageButton)findViewById(R.id.to_location);
		to_location.setOnClickListener(this);
		toLocus=(ImageButton)findViewById(R.id.toLocus);
		toLocus.setOnClickListener(this);
		to_listen=(ImageButton)findViewById(R.id.to_listen);
		to_listen.setOnClickListener(this);
		to_chat=(ImageButton)findViewById(R.id.to_chat);
		to_chat.setOnClickListener(this);
		public_head_back_button = (ImageButton) findViewById(R.id.public_head_back_button);
		public_head_right_button = (ImageButton) findViewById(R.id.public_head_right_button);
		public_head_right_button.setVisibility(View.VISIBLE);
		public_head_back_button.setOnClickListener(this);
		public_head_right_button.setOnClickListener(this);
		public_head_text_center = (TextView) findViewById(R.id.public_head_text_center);
		public_head_text_center.setText(getString(R.string.locus_title));

		iocus_left_button = (ImageButton) findViewById(R.id.iocus_left_button);
		iocus_right_button = (ImageButton) findViewById(R.id.iocus_right_button);
		pointAndLine = (ImageView) findViewById(R.id.pointAndLine);
		pointAndLine.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isClick){
					if(polyline==null){
						polylineOptions.width(7).color(Color.argb(255, 58, 221, 162));
						polyline=aMap.addPolyline(polylineOptions);
						polyline.setVisible(true);
						//pointAndLine.setText(R.string.off_line);
						pointAndLine.setImageResource(R.drawable.lineoff);
						
					}else{
						polyline.setVisible(true);
						//pointAndLine.setText(R.string.off_line);
						pointAndLine.setImageResource(R.drawable.lineoff);
					}
					isClick = false;
				}else{
					if(polyline!=null){
						polyline.setVisible(false);
						pointAndLine.setImageResource(R.drawable.lineoff);
					}
					isClick = true;
					
				}
			}
		});
		iocus_left_button.setOnClickListener(this);
		iocus_right_button.setOnClickListener(this);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar1.setOnSeekBarChangeListener(this);
		
//		locus_ceshi = (TextView) findViewById(R.id.locus_ceshi);

		mHandler = new Handler();
//		mHandlerDate = new Handler();
		tools = new XcmTools(this);
		positionList=new ArrayList<Position>();
		positionList2=new ArrayList<Position>();
		sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		date=sdf2.format(new Date());
		lastDate=sdf2.format(new Date());
		if(aMap==null){
			aMap=mapView.getMap();
			aMap.getUiSettings().setMyLocationButtonEnabled(false);
			aMap.getUiSettings().setZoomControlsEnabled(false);
			aMap.getUiSettings().setScaleControlsEnabled(false);
		}
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		polylineOptions=new PolylineOptions();
		initCurrentBaby();
	}
	
	public void initCurrentBaby(){
		String currentImei=tools.get_current_device_id();
		String babyList=tools.get_babyList();
		try {
			JSONArray baby_list=new JSONArray(babyList);
			for(int i=0;i<baby_list.length();i++){
				JSONObject babyObject=baby_list.getJSONObject(i);
//				Trace2.i("babyObject===="+babyObject.toString());
				HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
				BaoBeiBean baby= BeanUtils.getBaoBei(babyMap);
				if(baby.getImei().equals(currentImei)){
					currentBaby=baby;
					break;
				}
			}
		} catch (JSONException e) {
			Trace.i("exception====" + e.toString());
			e.printStackTrace();
		}
	}
	
	public void onResume(){
		super.onResume();
		mapView.onResume();
	}
	
	public void onPause(){
		super.onPause();
		mapView.onPause();
	}
	
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	public void onDestroy(){
		super.onDestroy();
		mapView.onDestroy();
	}
	

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.to_listen:
			Calldialog();
			break;
		case R.id.to_location:
			finish();
			break;
		case R.id.to_chat:
            Intent mIntent=new Intent(LocusActivity.this, ChatActivity.class);
			startActivity(mIntent);
			finish();
			break;
		case R.id.public_head_back_button:
			finish();
			break;
		case R.id.public_head_right_button:
			new PopupWindows(LocusActivity.this, public_head_right_button);
			break;
		case R.id.iocus_left_button:
			isScroll=false;
			seekBar1.setProgress(seekBar1.getProgress() - 1);
			break;
		case R.id.iocus_right_button:
			isScroll=false;
			seekBar1.setProgress(seekBar1.getProgress() + 1);
			break;
		case R.id.map_type2:
			if(is3D){
				CameraUpdate cameraUpdate=CameraUpdateFactory.changeTilt(0);
				aMap.moveCamera(cameraUpdate);
				map_type2.setText("2D");
				is3D=false;
			}else{
				CameraUpdate cameraUpdate=CameraUpdateFactory.changeTilt(90);
				aMap.moveCamera(cameraUpdate);
				map_type2.setText("3D");
				is3D=true;
			}
			break;
		}
	}

	public void initPositionList(){
		
	}
	/*
	 * SeekBar停止滚动的回调函数
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress=seekBar.getProgress();
		if(positionList2.size()>0){
			toSeekLocus(progress);
		}
			
	}

	/*
	 * SeekBar开始滚动的回调函数
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		isScroll=true;
	}

	/*
	 * SeekBar滚动时的回调函数
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {

		case R.id.seekBar1: {
			if(!isScroll){
				if(positionList2.size()>0){
					toSeekLocus(progress);
				}
			}
			break;
		}
		default:
			break;
		}
	}

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
					e.printStackTrace();
				}
			}
			
		}
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.popupwindow_calendar,
					null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_in));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_1));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			final TextView popupwindow_calendar_month = (TextView) view
					.findViewById(R.id.popupwindow_calendar_month);
			final KCalendar calendar = (KCalendar) view
					.findViewById(R.id.popupwindow_calendar);
			Button popupwindow_calendar_bt_enter = (Button) view
					.findViewById(R.id.popupwindow_calendar_bt_enter);

			popupwindow_calendar_month.setText(calendar.getCalendarYear() + "-"
					+ calendar.getCalendarMonth());

			if (null != date) {

				int years = Integer.parseInt(date.substring(0,
						date.indexOf("-")));
				int month = Integer.parseInt(date.substring(
						date.indexOf("-") + 1, date.lastIndexOf("-")));
				popupwindow_calendar_month.setText(years + "年" + month + "月");

				calendar.showCalendar(years, month);
				calendar.setCalendarDayBgColor(date,
						R.drawable.calendar_date_focused);

			}

			List<String> list = new ArrayList<String>(); // 设置标记列表
			list.add("2014-04-01");
			list.add("2014-04-02");
			calendar.addMarks(list, 0);

			// 监听所选中的日期
			calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

				public void onCalendarClick(int row, int col, String dateFormat) {
					int month = Integer.parseInt(dateFormat.substring(
							dateFormat.indexOf("-") + 1,
							dateFormat.lastIndexOf("-")));

					if (calendar.getCalendarMonth() - month == 1// 跨年跳转
							|| calendar.getCalendarMonth() - month == -11) {
						calendar.lastMonth();

					} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
							|| month - calendar.getCalendarMonth() == -11) {
						calendar.nextMonth();

					} else {
						calendar.removeAllBgColor();
						calendar.setCalendarDayBgColor(dateFormat,
								R.drawable.calendar_date_focused);
						
						date = dateFormat;// 最后返回给全局 date
						public_head_text_center.setText(date+getString(R.string.locus_title2));
//						locus_ceshi.setText("date = " + date);
						if(!lastDate.equals(date)){
							new Thread(mRunnable).start();
							lastDate=dateFormat;
						}
						
//						new Thread(mRunnable).start();

//						seekBar1.setMax(guiji.length);

						dismiss();
					}
				}
			});

			// 监听当前月份
			calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
				public void onCalendarDateChanged(int year, int month) {
					popupwindow_calendar_month
							.setText(year + "年" + month + "月");
				}
			});

			// 上月监听按钮
			LinearLayout popupwindow_calendar_last_month = (LinearLayout) view
					.findViewById(R.id.popupwindow_calendar_last_month);
			popupwindow_calendar_last_month
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							calendar.lastMonth();
						}

					});

			// 下月监听按钮
			LinearLayout popupwindow_calendar_next_month = (LinearLayout) view
					.findViewById(R.id.popupwindow_calendar_next_month);
			popupwindow_calendar_next_month
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							calendar.nextMonth();
						}
					});

			// 关闭窗口
			popupwindow_calendar_bt_enter
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							dismiss();
						}
					});
		}
	}


	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			String positionListResult=GetLocus("");
//			Trace2.i("query_hispos======"+positionListResult);
			try {
				HashMap<String,String> posListMap= BeanUtils.getJSONParserResult(positionListResult);
				String result=posListMap.get("resultCode");
				Trace.i("1");
				if("1".equals(result)){
					Trace.i("2");
					positionList.clear();
					positionList2.clear();
					polylineOptions=new PolylineOptions();
					polyline=null;
					String posList=posListMap.get("track");
					if(posList.length()>0){
						JSONArray posListArray=new JSONArray(posList);
						Date time1=new Date();
						for(int i=0;i<posListArray.length();i++){
							JSONObject posObject=posListArray.getJSONObject(i);
							HashMap<String,String> posMap= BeanUtils.getJSONParserResult(posObject.toString());
							Position position= BeanUtils.getPosition(posMap);
							positionList.add(position);
						}
						Date time2=new Date();
						Trace.i("array time===" + (time2.getTime() - time1.getTime()));
						Trace.i("positionList size==" + positionList.size());
						if(positionList.size()>0){
//							sortPositionList(positionList);
							dealPositionList();
							Trace.i(" positionList22 size==" + positionList2.size());
						}
					}
					queryResult=0;
				}else if("0".equals(result)){
					queryResult=1;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
				Trace.i("exception=====" + e.toString());
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if(queryResult==0){
						aMap.clear();
						isClick=false;
						if(positionList2.size()>0){
							seekBar1.setMax(positionList2.size()-1);
							showLocus();
							if(!lastDate.equals(date)){
								isScroll=false;
								seekBar1.setProgress(0);
							}
						}
					}else if(queryResult==1){
						
					}
				}
			});

		}

	};
	/**
	 * 处理位置列表，将重复的位置合并
	 */
	public void dealPositionList(){
		int tag=0;
		Date time1=new Date();
		if(positionList.size() == 1) {
			Trace.i("positionList size is 1");
			Position p = positionList.get(0);
			positionList2.add(p);
		}else {
		for(int i=0;i<positionList.size()-1;i++){
			Position p1=positionList.get(i);
			Position p2=positionList.get(i+1);
			Trace.i("position i===============" + i);
			String x1=p1.getNoTransLng();
			Trace.i("x1===============" + x1);
			String y1=p1.getNoTransLat();
			Trace.i("y1===============" + y1);
			String x2=p2.getNoTransLng();
			Trace.i("x2===============" + x2);
			String y2=p2.getNoTransLat();
			Trace.i("y2===============" + y2);
			if(x1.equals(x2)&&y1.equals(y2)){//同一个位置
//				timeStart=p1.getTime();
//				positionList2.add(p1);
				if(positionList.size()==i+2){
					Position pa=positionList.get(tag);
					Position pb=positionList.get(i+1);
					pb.setTimeInterval(dealTime(pa.getTime().split(" ")[1])+"-"+dealTime(pb.getTime().split(" ")[1]));
					positionList2.add(pb);
				}
			}else{
				Position pa=positionList.get(tag);
				Position pb=positionList.get(i);
				if(pa.getTime().equals(pb.getTime())){
					pb.setTimeInterval(dealTime(pb.getTime().split(" ")[1]));
				}else{
					pb.setTimeInterval(dealTime(pa.getTime().split(" ")[1])+"-"+dealTime(pb.getTime().split(" ")[1]));
				}
				positionList2.add(pb);
				tag=i+1;
				if(tag==positionList.size()-1){
					positionList2.add(p2);
				}
			}
		}
	}
		Date time2=new Date();
		Trace.i("deal time===" + (time2.getTime() - time1.getTime()));
		Trace.i("positionList21 size==" + positionList2.size());
	}
	public void showLocus(){
		Date time1=new Date();
		LatLngBounds.Builder boundsBuilder=new LatLngBounds.Builder();
		for(int i=0;i<positionList2.size();i++){
			Position p=positionList2.get(i);
			double lat=Double.parseDouble(p.getLng());
			double lng=Double.parseDouble(p.getLat());
			String pType=p.getPtype();
			String pTypeString="";
			LatLng latLng=new LatLng(lng,lat);
			MarkerOptions markerOption=new MarkerOptions();
			if("0".equals(pType)){
				pTypeString=getString(R.string.loc_lbs);
//				markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locus_point));
			}else if("1".equals(pType)){
				pTypeString=getString(R.string.loc_gps);
//				markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locus_point));
			}
			boundsBuilder.include(latLng);
			polylineOptions.add(latLng);
//			markerOption.position(latLng);
//			markerOption.anchor(0.5f, 0.5f);
			markerOption.setFlat(true);
			Marker m=aMap.addMarker(markerOption);
			if(i>0){
				Position lastP=positionList2.get(i-1);
				double lastLat=Double.parseDouble(lastP.getLng());
				double lastLng=Double.parseDouble(lastP.getLat());
				LatLng lastLatLng=new LatLng(lastLng,lastLat);
				float angle = (float)rotateAngle(latLng,lastLatLng);
				m.setRotateAngle(angle);
			}
			if(i==0){
				CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng, 16);
				aMap.moveCamera(cameraUpdate);
			}
		}
		CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100);
		aMap.moveCamera(cameraUpdate);
		Date time2=new Date();
		Trace.i("showLocus time===" + (time2.getTime() - time1.getTime()));
	}
	
	
	public void setBatteryIcon(ImageView batteryIconView,String batteryPercent){
		int battery=Integer.parseInt(batteryPercent);
		if(battery<=20){
			batteryIconView.setBackgroundResource(R.drawable.battery_icon1);
		}else if(battery>20&&battery<=40){
			batteryIconView.setBackgroundResource(R.drawable.battery_icon2);
		}else if(battery>40&&battery<=60){
			batteryIconView.setBackgroundResource(R.drawable.battery_icon3);
		}else if(battery>60&&battery<=80){
			batteryIconView.setBackgroundResource(R.drawable.battery_icon4);
		}else if(battery>80){
			batteryIconView.setBackgroundResource(R.drawable.battery_icon5);
		}
	}
	
	public void toSeekLocus(int i){
		int size=positionList2.size();
		Position p=positionList2.get(size-i-1);
		double lat=Double.parseDouble(p.getLng());
		double lng=Double.parseDouble(p.getLat());
		String timeInterval=p.getTimeInterval();
		String batteryPercent=p.getBattery()+"%";
		String pType=p.getPtype();
		String pTypeString="";
		LatLng latLng=new LatLng(lng,lat);
		markerOption2=new MarkerOptions();
		markerOption2.position(latLng);
		markerView=LayoutInflater.from(this).inflate(R.layout.location_marker, null);
		TextView locTimeView=(TextView)markerView.findViewById(R.id.loc_time);
		TextView batteryView=(TextView)markerView.findViewById(R.id.battery_percent);
		ImageView batteryIconView=(ImageView)markerView.findViewById(R.id.battery_icon);
		locAddress=(TextView)markerView.findViewById(R.id.loc_address);
		ImageView locType=(ImageView)markerView.findViewById(R.id.loc_type);
		TextView babyName=(TextView)markerView.findViewById(R.id.loc_baby_name);
		babyName.setText(currentBaby.getName());
//		TextView locPrecision=(TextView)markerView.findViewById(R.id.loc_precision);
		LatLonPoint point=new LatLonPoint(lng,lat);
		locTimeView.setText(timeInterval);
		batteryView.setText(batteryPercent);
		if("0".equals(pType)){
			pTypeString=getString(R.string.loc_lbs);
			locType.setBackgroundResource(R.drawable.gps_icon);
		}else if("1".equals(pType)){
			pTypeString=getString(R.string.loc_gps);
			locType.setBackgroundResource(R.drawable.gps_icon);
		}
		setBatteryIcon(batteryIconView,p.getBattery());
//		locPrecision.setText(pTypeString);
//		CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng, 16);
//		aMap.moveCamera(cameraUpdate);
		RegeocodeQuery query = new RegeocodeQuery(point,200,GeocodeSearch.AMAP);
		geocoderSearch.getFromLocationAsyn(query);
	}

	String GetLocus(String m) {

		String post_date = date;
//		String post_imei = tools.get_imei();
		JSONObject json_login = new JSONObject();
		try {
			json_login.put("user_id", tools.get_user_id());
			json_login.put("device_id", tools.get_current_device_id());
			json_login.put("start_time", post_date + " 00:00:00");
			json_login.put("end_time", post_date + " 23:59:59");

			String json_login_result = Utils.GetService(json_login, Constants.LOCUS);
			if (json_login_result.equals("0")) {
				return "0";
			} else if (json_login_result.equals("-1")) {
				return "-1";

			} else {
				return json_login_result;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


		return "";

	}


	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		
	}


	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if(rCode==0){
			if(result!=null&&result.getRegeocodeAddress()!=null&&result.getRegeocodeAddress().getFormatAddress()!=null){
				String address=result.getRegeocodeAddress().getFormatAddress();
				locAddress.setText(address);
//				aMap.clear();
				Trace.i("1");
				if(selecedMarker!=null){
					selecedMarker.remove();
				}
				markerOption2.icon(BitmapDescriptorFactory.fromView(markerView));
				selecedMarker=aMap.addMarker(markerOption2);
			}
		}else{
			
		}
		
	}
	
	public static double rotateAngle(LatLng startPoint, LatLng destPoint){
		double latDouble = destPoint.latitude - startPoint.latitude;
		double lonDouble = destPoint.longitude - startPoint.longitude;
		if(latDouble ==0.0d && lonDouble >0.0d){
			return 270.0d;
		}
		if(latDouble ==0.0d && lonDouble <0.0d){
			return 90.0d;
		}
		if(latDouble >0.0d && lonDouble ==0.0d){
			return 0.0d;
		}
		if(latDouble <0.0d && lonDouble ==0.0d){
			return 180.0d;
		}
		if(latDouble >0.0d && lonDouble>0.0d){
			return 270d+Math.toDegrees(Math.atan(latDouble / lonDouble));
		}
		if(latDouble>0.0d && lonDouble<0.0d){
			return 90d-Math.toDegrees(Math.atan(latDouble / -lonDouble));
		}
		if(latDouble<0.0d && lonDouble<0.0d){
			return 90d+Math.toDegrees(Math.atan(-latDouble / -lonDouble));
		}
		if(latDouble<0.0d && lonDouble>0.0d){
			return 270d-Math.toDegrees(Math.atan(latDouble / -lonDouble));
		}
		return 0d;
	}
	
	String listen(String m) {
		JSONObject json_listen = new JSONObject();
		try {
			json_listen.put("phone", tools.get_login_phone());
			json_listen.put("serial_number", tools.get_current_device_id());
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
							Toast.makeText(LocusActivity.this, getString(R.string.listen_success), Toast.LENGTH_SHORT).show();
						}else if("-1".equals(result)){
							Toast.makeText(LocusActivity.this, getString(R.string.listen_failed), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						Trace.i("exception=======" + e.toString());
						e.printStackTrace();
					}
					
				}
				
			});
			
		}
		
	};
	
	void Calldialog() {

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { // 为对话框里的按钮增加监听事件
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case AlertDialog.BUTTON_POSITIVE: // 点击了确定
					new Thread(mRunnable2).start();
					break; // 结束

				case AlertDialog.BUTTON_NEGATIVE: // 点击了取消
					
					break; // 结束
				}
			}
		};

		AlertDialog ad = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.qingtin_alertdialog_title))
				.setMessage(
						getString(R.string.qingtin_alertdialog_count1)
								+ getString(R.string.qingtin_alertdialog_count2))
				.setPositiveButton(getString(R.string.ok),
						listener)
				.setNegativeButton(getString(R.string.cancel),
						listener).show();

	}
	
	public String dealTime(String time){
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		Date date=new Date();
		try {
			date=sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sdf.format(date);
	}
}
