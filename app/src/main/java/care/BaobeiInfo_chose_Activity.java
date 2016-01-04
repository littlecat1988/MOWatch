package care;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mtk.btnotification.R;
import com.xcm.ui.HorizontalListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import care.bean.BaoBeiBean;
import care.fragment.PictureSelectFragment;
import care.picturehead.CropHandler;
import care.picturehead.CropHelper;
import care.picturehead.CropParams;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Utils;
import care.widget.CircularImage;

public class BaobeiInfo_chose_Activity extends CommonBaseActivity implements
		OnClickListener, CropHandler, PictureSelectFragment.PictureSelectInterface {

	private CircularImage baby_photo;
	private Calendar c;
	private LinearLayout baby_name;
	private LinearLayout baby_sex;
	private LinearLayout baby_birthday;
	private LinearLayout baby_height;
	private LinearLayout baby_width;
	private LinearLayout baby_phone;
	private LinearLayout baby_call;
	private LinearLayout baby_relieve;

	private TextView baby_name_text;
	private TextView baby_sex_text;
	private TextView baby_birthday_text;
	private TextView baby_height_text;
	private TextView baby_width_text;
	private TextView baby_phone_text;
	private TextView baby_call_text;
	private TextView baby_imei_text;

	private LinearLayout progressBar;
	private TextView progress_text;

	private boolean Sextype = true;
	private BaoBeiBean currentBaby;
	private Handler mHandler;
	private String babyImei;

	private CropParams mCropParams;
	private PictureSelectFragment picture;
	private FragmentManager fm;


	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.baobei_info_main);
		mInstance.addActivity(Constants.BAOBEIINFO_CHOSE_ACTIVITY, BaobeiInfo_chose_Activity.this);
		babyImei=getIntent().getExtras().get("babyImei").toString();
		mCropParams = new CropParams("3");
	}

	@Override
	protected void initFindView() {
		// TODO Auto-generated method stub
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			back_button.setVisibility(View.VISIBLE);
		}
		progressBar = (LinearLayout) findViewById(R.id.progress_bar);
		progress_text = (TextView) findViewById(R.id.progress_text);

		baby_photo = (CircularImage) findViewById(R.id.baby_photo);
		titleString.setText(R.string.baobei_info_string);
		right_txt.setVisibility(View.VISIBLE);
		right_txt.setText(R.string.save_string);
		baby_name_text = (TextView) findViewById(R.id.baby_name_text);
		baby_sex_text = (TextView) findViewById(R.id.baby_sex_text);
		baby_birthday_text = (TextView) findViewById(R.id.baby_birthday_text);
		baby_height_text = (TextView) findViewById(R.id.baby_height_text);
		baby_width_text = (TextView) findViewById(R.id.baby_width_text);
		baby_phone_text = (TextView) findViewById(R.id.baby_phone_text);
		baby_imei_text = (TextView)findViewById(R.id.baby_imei_text);
		baby_call_text = (TextView) findViewById(R.id.baby_call_text);

		baby_name = (LinearLayout) findViewById(R.id.baby_name);
		baby_sex = (LinearLayout) findViewById(R.id.baby_sex);
		baby_birthday = (LinearLayout) findViewById(R.id.baby_birthday);
		baby_height = (LinearLayout) findViewById(R.id.baby_height);
		baby_width = (LinearLayout) findViewById(R.id.baby_width);
		baby_phone = (LinearLayout) findViewById(R.id.baby_phone);
		baby_call = (LinearLayout) findViewById(R.id.baby_call);
		baby_relieve = (LinearLayout) findViewById(R.id.baby_relieve);
		c = Calendar.getInstance();
		mHandler=new Handler();
		setOnClickListener();
		init();
	}	

	void init() {
		String babyList = tools.get_babyList();
		try {
			JSONArray babyArray = new JSONArray(babyList);
			int length = babyArray.length();
//			String currentId = tools.get_current_device_id();
			String currentId = babyImei;
			for (int i = 0; i < length; i++) {
				JSONObject babyObject = (JSONObject) babyArray.get(i);
				HashMap<String, String> babyMap = BeanUtils
						.getJSONParserResult(babyObject.toString());
				BaoBeiBean baobei = BeanUtils.getBaoBei(babyMap);
				if (baobei.getImei().equals(currentId)) {
					currentBaby = baobei;
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(currentBaby!=null){
			baby_name_text.setText(currentBaby.getName());
			String sex="";
			
			if("0".equals(currentBaby.getSex())){
				sex="男";
			}else if("1".equals(currentBaby.getSex())){
				sex="女";
			}
			baby_sex_text.setText(sex);
			String birthDay=currentBaby.getBirthDay();
			String birthDayFormat="";
			if(birthDay!=null&&!"".equals(birthDay)){
				if(birthDay.contains(" ")){
					birthDayFormat=birthDay.split(" ")[0];
				}
			}
			if (birthDay.equals("2015-12-12 23:59:59")) {
				birthDay = "2015-11-11";
			}
			
			baby_birthday_text.setText(birthDay);
			baby_height_text.setText(currentBaby.getHeight());
			baby_width_text.setText(currentBaby.getWeight());
			;
			baby_phone_text.setText(currentBaby.getPhone());
//			baby_imei_text.setText(tools.get_current_device_id());
			baby_imei_text.setText(babyImei);
			baby_call_text.setText("");

			imageLoader.displayImage(currentBaby.getPhoto(), baby_photo,
					options);
		}

		progress_text.setText(R.string.loading);
		if (checkBabyType()) {
			right_txt.setVisibility(View.VISIBLE);
		} else {
			right_txt.setVisibility(View.GONE);
		}
		// System.out.println("add baby" + tools.get_babyList());
	}

	public void onStart() {
		super.onStart();
	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		baby_name = (LinearLayout) findViewById(R.id.baby_name);
		baby_sex = (LinearLayout) findViewById(R.id.baby_sex);
		baby_birthday = (LinearLayout) findViewById(R.id.baby_birthday);
		baby_height = (LinearLayout) findViewById(R.id.baby_height);
		baby_width = (LinearLayout) findViewById(R.id.baby_width);
		baby_phone = (LinearLayout) findViewById(R.id.baby_phone);
		baby_call = (LinearLayout) findViewById(R.id.baby_call);
		baby_relieve = (LinearLayout) findViewById(R.id.baby_relieve);

		baby_name.setOnClickListener(this);
		baby_sex.setOnClickListener(this);
		baby_birthday.setOnClickListener(this);
		baby_height.setOnClickListener(this);
		baby_width.setOnClickListener(this);
		baby_phone.setOnClickListener(this);
		baby_call.setOnClickListener(this);
		baby_relieve.setOnClickListener(this);
		baby_photo.setOnClickListener(this);

		right_txt.setOnClickListener(this);
	}

	@Override
	protected void onDestoryActivity() {
		// TODO Auto-generated method stub
		mInstance.removeActivity(Constants.BAOBEIINFO_CHOSE_ACTIVITY);
	}

	boolean checkBabyType() {
		String myUserId = tools.get_user_id();
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			return true;
		}
		if (myUserId.equals(currentBaby.getToUserId())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.baby_photo:
			if (checkBabyType()) {
				final FragmentManager fm = getFragmentManager();
				picture = PictureSelectFragment.getInstance(this, R.string.update_head_image_string, R.string.xiangji_string, R.string.xiangce_string);
				picture.show(fm, "picture_dialog");
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_name:
			if (checkBabyType()) {
				showNameSettingDialog();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_sex:

			if (checkBabyType()) {
				showSexSettingDialog();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_birthday:
			if (checkBabyType()) {
				showBirthdayDialog();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_height:

			if (checkBabyType()) {
				// showHeightSettingDialog();
				showHeightDialog2();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_width:

			if (checkBabyType()) {
				// showWidthSettingDialog();
				showWidthDialog2();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_phone:

			if (checkBabyType()) {
				showPhoneSettingDialog();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_call:
			if (checkBabyType()) {
				showCallSettingDialog();
			} else {
				showToast(R.string.baobei_error8);
			}

			break;
		case R.id.baby_relieve:
			// // Toast.makeText(BaoBeiInfoActivity.this, "解除绑定", 500).show();
			// TextView view = new TextView(this);
			// view.setText(getString(R.string.unbind_alarm));
			// new AlertDialog.Builder(this)
			// .setTitle(getString(R.string.alert))
			// .setView(view)
			// .setPositiveButton(getString(R.string.ok),
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface arg0,
			// int arg1) {
			//
			// new Thread(mRunnable).start();
			//
			// }
			// })
			// .setNegativeButton(getString(R.string.cancel),
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface arg0,
			// int arg1) {
			//
			// }
			//
			// }).show();
			// break;
		case R.id.right_txt:
//			Toast.makeText(BaoBeiInfoActivity.this, "保存信息", 500).show();
			if (Constants.IS_OPEN_NETWORK) {

				if (checkInfo()) {
					SetBabyDataToBack(Utils.bitmaptoString(1, baby_photo),
							baby_name_text.getText().toString().trim(),
							baby_sex_text.getText().toString().trim(),
							baby_birthday_text.getText().toString().trim(),
							baby_height_text.getText().toString().trim(),
							baby_width_text.getText().toString().trim(),
							baby_phone_text.getText().toString().trim(),
							baby_call_text.getText().toString().trim());
					progressBar.setVisibility(View.VISIBLE);
				}

			} else {
				showToast(R.string.network_error);
			}
			break;

		}
	}

	boolean checkInfo() {

		if (baby_name_text.getText().toString().trim().equals("")) {
			showToast(R.string.baobei_error1);
			return false;
		} else if (baby_name_text.getText().toString().trim().length() > 8) {
			showToast(R.string.baobei_error6);
			return false;
		}

		else if (baby_birthday_text.getText().toString().trim().equals("")) {
			showToast(R.string.baobei_error2);
			return false;
		} else if (baby_height_text.getText().toString().trim().equals("")
				|| baby_height_text.getText().toString().trim().equals("0")) {
			showToast(R.string.baobei_error3);
			return false;
		} else if (baby_width_text.getText().toString().trim().equals("")
				|| baby_width_text.getText().toString().trim().equals("0")) {
			showToast(R.string.baobei_error4);
			return false;
		} else if (baby_phone_text.getText().toString().trim().equals("")) {
			showToast(R.string.baobei_error5);
			return false;
		}else if (baby_phone_text.getText().toString().trim().length()<11) {
			showToast(R.string.baobei_error7);
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 与后台通讯的方法
	 */
	private void SetBabyDataToBack(String head, String name, String sex,
			String birthday, String height, String width, String phone,
			String call) {
		// TODO Auto-generated method stub
		
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		String mJr = "";
		String uRl = "";
		System.out.println("id " + tools.get_user_id());
		System.out.println("imei " + babyImei);
		if (sex.equals("男")) {
			sex = "0";
		} else {
			sex = "1";
		}
		map.put("user_id", tools.get_user_id());
//		map.put("device_imei", tools.get_current_device_id());
		map.put("device_imei", babyImei);
		map.put("device_head", head);
		map.put("device_name", name);
		map.put("device_sex", sex);
		map.put("device_age", birthday);
		map.put("device_height", height);
		map.put("device_weight", width);
		map.put("device_phone", phone);

		mJr = mProtocolData.transFormToJson(map);
		uRl = Constants.BABYSETTING;
		// Toast.makeText(BaoBeiInfoActivity.this, "请求", 500).show();

		new ConnectToLinkTask().execute(uRl, mJr);
	}

	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = mProtocolData.getBackResult(result);
		int resultCode = (Integer) map.get("resultCode");
		System.out.println("宝贝修改 + " + resultCode);
		progressBar.setVisibility(View.GONE);
		switch (resultCode) {
		case 1: // 成功
			showToast(R.string.other1);
			System.out.println("宝贝头像 = " + map.get("device_head"));
			Bundle bundle=getIntent().getExtras();
			if(bundle!=null){
				boolean toMain=bundle.getBoolean("toMain");
				if(toMain){
					Intent i=new Intent();
					i.setClass(BaobeiInfo_chose_Activity.this, LocationActivity.class);
					startActivity(i);
				}
			}
			finish();
			break;
		case 0: // 失败
			showToast(R.string.other2);
			break;
		case -1: // 异常
			String exception = "" + map.get("exception");
			showToast(R.string.exception_code);
			break;
		case -6:
			showToast(R.string.link_chaoshi_code);
			break;
		}
	}

	public void showNameSettingDialog() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_name, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final EditText name = (EditText) view.findViewById(R.id.name);
		name.setText(baby_name_text.getText().toString());

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (name.getText().toString().length() < 2
						|| name.getText().toString().length() > 9) {
					showToast(R.string.pserson_error1);
				} else {
					baby_name_text.setText(name.getText().toString());
					dialog.dismiss();
				}

			
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}

	boolean getSex() {
		if (baby_sex_text.getText().toString().equals("男")) {
			System.out.println("cc = " + baby_sex_text.getText().toString());
			return true;
		} else {
			return false;
		}

	}

	void setSex(boolean type) {
		if (type) {
			baby_sex_text.setText("男");
		} else {
			baby_sex_text.setText("女");
		}

	}

	String guolvBirthdayDate(int i) {

		String old = baby_birthday_text.getText().toString();
//		String old = "2015-05-07-08";

		String result = "";
		String[] date = {};
		if (old.equals("") || old.equals(null)) {
			date = "2000-5-6".split("-");
		} else {
			date = old.split("-");
		}

		switch (i) {
		
		case 1:
			result = date[0];
			break;
		case 2:
			result = date[1];
			break;

		case 3:
			result = date[2];
			break;
		}
		return result;

	}

	public void showBirthdayDialog() {
		
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker arg0, int year, int monthOfYear,
					int dayOfMonth) {
				String date = "";
				String Month = "";
				String day = "";

				if (monthOfYear > 8) {
					Month = String.valueOf((monthOfYear + 1));
				} else {
					Month = "0" + String.valueOf((monthOfYear + 1));
				}

				if (dayOfMonth > 8) {
					day = String.valueOf(dayOfMonth);
				} else {
					day = "0" + String.valueOf(dayOfMonth);
				}

				String birthDay = year + "-" + Month + "-" + day;

				baby_birthday_text.setText(birthDay);

				// if (!birthDay.equals(babySetValues[3])) {
				// babySetting.setVisibility(View.VISIBLE);
				// }
				// babySetValues[3] = birthDay;
				// babySetAdapter.notifyDataSetChanged();
			}
		}, Integer.valueOf(guolvBirthdayDate(1)).intValue(), Integer.valueOf(
				guolvBirthdayDate(2)).intValue() - 1, Integer.valueOf(
				guolvBirthdayDate(3)).intValue()).show();
		System.out.println("c1 =  " + guolvBirthdayDate(1) + "c2 =  "
				+ guolvBirthdayDate(2) + "c3 = " + guolvBirthdayDate(3));
	}

	public void showSexSettingDialog() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_sex, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final LinearLayout lin_b = (LinearLayout) view.findViewById(R.id.lin_b);
		final LinearLayout lin_g = (LinearLayout) view.findViewById(R.id.lin_g);
		final ImageView b_img = (ImageView) view.findViewById(R.id.b_img);
		final ImageView g_img = (ImageView) view.findViewById(R.id.g_img);
//
		if (getSex()) {
			lin_b.setBackgroundResource(R.drawable.sex_b);
			lin_g.setBackgroundResource(R.drawable.sex_no);
			b_img.setImageResource(R.drawable.sex_nan_p);
			g_img.setImageResource(R.drawable.sex_nv_n);
			Sextype = true;
		} else {
			lin_b.setBackgroundResource(R.drawable.sex_no);
			lin_g.setBackgroundResource(R.drawable.sex_g);
			b_img.setImageResource(R.drawable.sex_nan_n);
			g_img.setImageResource(R.drawable.sex_nv_p);
			Sextype = false;
		}

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
				setSex(Sextype);
				dialog.dismiss();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		lin_b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				lin_b.setBackgroundResource(R.drawable.sex_b);
				lin_g.setBackgroundResource(R.drawable.sex_no);
				b_img.setImageResource(R.drawable.sex_nan_p);
				g_img.setImageResource(R.drawable.sex_nv_n);
				Sextype = true;
			}
		});

		lin_g.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				lin_b.setBackgroundResource(R.drawable.sex_no);
				lin_g.setBackgroundResource(R.drawable.sex_g);
				b_img.setImageResource(R.drawable.sex_nan_n);
				g_img.setImageResource(R.drawable.sex_nv_p);
				Sextype = false;
			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}

	public void showHeightSettingDialog() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_height, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final TextView number = (TextView) view.findViewById(R.id.number);
		number.setText(baby_height_text.getText().toString());
		final HorizontalListView horizontalListView = (HorizontalListView) view
				.findViewById(R.id.testlistview);
		List<Map<String, Object>> list = getData();
		horizontalListView.setAdapter(new FriendSystemInfoAdspter(this, list));

		// ;

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "也是", 500).show();
				baby_height_text.setText(number.getText().toString());
				dialog.dismiss();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		horizontalListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Toast.makeText(getApplicationContext(), "" + position,
						Toast.LENGTH_SHORT).show();
				// dialog.dismiss();
				number.setText(String.valueOf(position));

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}

	public void showPhoneSettingDialog() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_phone, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final EditText name = (EditText) view.findViewById(R.id.name);
		name.setText(baby_phone_text.getText().toString());

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
				if(name.getText().toString().trim().length()<11||name.getText().toString().trim().length()>11){
					Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_LONG).show();
					baby_phone_text.setText("");
					dialog.dismiss();
				}else{
					baby_phone_text.setText(name.getText().toString());
					dialog.dismiss();
				}
			
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}
	
	public void showWidthDialog2() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_phone2, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final EditText name = (EditText) view.findViewById(R.id.name);
		String str = baby_width_text.getText().toString();
		if(str.equals("0"))
		{
			str = "";
		}
		
		name.setText(str);
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
				baby_width_text.setText(name.getText().toString());
				dialog.dismiss();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}
	
	
	public void showHeightDialog2() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_phone1, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final EditText name = (EditText) view.findViewById(R.id.name);
		
		
		String str = baby_height_text.getText().toString();
		if(str.equals("0"))
		{
			str = "";
		}
		
		name.setText(str);

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
				baby_height_text.setText(name.getText().toString());
				dialog.dismiss();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}

	public void showCallSettingDialog() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_call, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final EditText name = (EditText) view.findViewById(R.id.name);
		name.setText(baby_call_text.getText().toString());

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
				baby_call_text.setText(name.getText().toString());
				dialog.dismiss();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}

	public void showWidthSettingDialog() {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_baby_width, null);
		final Button yes = (Button) view.findViewById(R.id.yes);
		final Button no = (Button) view.findViewById(R.id.no);
		final TextView number = (TextView) view.findViewById(R.id.number);
		number.setText(baby_width_text.getText().toString());
		final HorizontalListView horizontalListView = (HorizontalListView) view
				.findViewById(R.id.testlistview);
		List<Map<String, Object>> list = getData();
		horizontalListView.setAdapter(new FriendSystemInfoAdspter(this, list));

		// ;

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
				.create();

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Toast.makeText(BaoBeiInfoActivity.this, "也是", 500).show();
				baby_width_text.setText(number.getText().toString());
				dialog.dismiss();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
				dialog.dismiss();

			}
		});

		horizontalListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Toast.makeText(getApplicationContext(), "" + position,
						Toast.LENGTH_SHORT).show();
				// dialog.dismiss();
				number.setText(String.valueOf(position));
				System.out.println(horizontalListView.getLastVisiblePosition());

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// setDialogWidth(dialog, 0.8);
	}

	public List<Map<String, Object>> getData() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 100; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("number_list_text1", String.valueOf(i));
			list.add(map);

		}

		return list;
	}

	@Override
	public void onPictureSelectXiangji() {
		// TODO Auto-generated method stub
		onDeleteCache();
		Intent intent = CropHelper.buildCaptureIntent(mCropParams.uri);
		startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
	}

	@Override
	public void onPictureSelectXiangce() {
		// TODO Auto-generated method stub
		onDeleteCache();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //4.4版本
			startActivityForResult(CropHelper.buildCropFromGalleryIntent(mCropParams), CropHelper.SELECET_A_PICTURE_AFTER_KIKAT);
		} else {
			startActivityForResult(CropHelper.buildCropFromGalleryIntent(mCropParams), CropHelper.REQUEST_CROP);
		}
	}

	@Override
	public void onPhotoCropped(Uri uri) {
		// TODO Auto-generated method stub
		picture.dismiss();
//		onHeadUrlUpload();   //ͷ���ϴ��ӿ���ʱ��д
		onSuccess();
	}

	@Override
	public void onCropCancel() {
		// TODO Auto-generated method stub
		picture.dismiss();
		showToast(R.string.photo_huoqu_cancle);
	}

	@Override
	public void onCropFailed(String message) {
		// TODO Auto-generated method stub
		picture.dismiss();
		showToast(R.string.photo_huoqu_fail);
	}

	@Override
	public CropParams getCropParams() {
		// TODO Auto-generated method stub
		return mCropParams;
	}

	@Override
	public Activity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case CropHelper.REQUEST_CAMERA:
			case CropHelper.REQUEST_CROP:
			case CropHelper.SELECET_A_PICTURE_AFTER_KIKAT:
				CropHelper.handleResult(this, requestCode, resultCode, data);
				break;
		}
	}
	private void onSuccess() {
		// TODO Auto-generated method stub
		baby_photo.setImageBitmap(CropHelper.decodeUriAsBitmap(this, mCropParams.uri));
//		ContentValues values = new ContentValues();
//		values.put(UserInfo.USER_HEAD_URL, head_id);
//		mUpdateDB.updateDataToBases(UserInfo.class, values, new String[]{Constants.USERID}, new String[]{UserInfo.USER_ID});
	}

	private void onDeleteCache() {
		if (CropHelper.clearCachedCropFile(mCropParams.uri)) {
			mCropParams = new CropParams("1" + "_" + Constants.getCurrentTimeLong() + ".jpg");
		}
	}
	private class FriendSystemInfoAdspter extends BaseAdapter {

		private List<Map<String, Object>> data;
		private LayoutInflater layoutInflater;
		private Context context;

		// private int phone_type;

		public FriendSystemInfoAdspter(Context context,
				List<Map<String, Object>> data) {
			this.context = context;
			this.data = data;
			this.layoutInflater = LayoutInflater.from(context);
		}

		/**
		 * ������ϣ���Ӧlist.xml�еĿؼ�
		 * 
		 * @author Administrator
		 */
		public final class Zujian {
			public TextView number_list_text1;

		}

		@Override
		public int getCount() {
			return data.size();
		}

		/**
		 * ���ĳһλ�õ����
		 */
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		/**
		 * ���Ψһ��ʶ
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			Zujian zujian = null;
			if (convertView == null) {
				zujian = new Zujian();
				convertView = layoutInflater
						.inflate(R.layout.number_list, null);

				zujian.number_list_text1 = (TextView) convertView
						.findViewById(R.id.number_list_text1);

				convertView.setTag(zujian);
			} else {
				zujian = (Zujian) convertView.getTag();
			}

			zujian.number_list_text1.setText((String) data.get(position).get(
					"number_list_text1"));

			return convertView;
		}
	}

	String unbind() {
		String p_sid = tools.get_user_id();
//		String p_imei = tools.get_current_device_id();
		String p_imei = babyImei;
		JSONObject json_unbind = new JSONObject();
		try {
			json_unbind.put("user_id", p_sid);
			json_unbind.put("device_imei", p_imei);
			String json_download_result = Utils.GetService(json_unbind,
                    Constants.BABYDELETE);

			System.out.println("测试结果是" + json_download_result);
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
}
