package care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Utils;
import care.widget.CircularImage;

public class OperationQinQingActivity extends CommonBaseActivity implements
		OnClickListener {

	private CircularImage device_img;
	private TextView device_name;
	private TextView device_phone;
	private EditText operation_qinqing_name;
	private EditText operation_qinqing_phone;
	private Button add_button, mof_button, delete_button;
	private LinearLayout progressBar;
	private TextView progress_text;
	private String QinQingType = "";
	private Map<String,Object> currentFamilyMember=new HashMap<String,Object>();
	private int currentOperation;
	private int currentPosition;
	private boolean isDelete=false;
	private TextView title_string;
	private String QinqingId = "";
	private String edit_phone_number = "";
	private String edit_old = "";

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.operation_qinqing_main);
	}

	@Override
	protected void initFindView() {
		// TODO Auto-generated method stub
		// device_img = (CircularImage) findViewById(R.id.device_img);
		device_name = (TextView) findViewById(R.id.device_name);
		device_phone = (TextView) findViewById(R.id.device_phone);
		operation_qinqing_name = (EditText) findViewById(R.id.operation_qinqing_name);
		operation_qinqing_phone = (EditText) findViewById(R.id.operation_qinqing_phone);
		add_button = (Button) findViewById(R.id.add_number_button);
		mof_button = (Button) findViewById(R.id.mof_button);
		delete_button = (Button) findViewById(R.id.delete_button);
		title_string = (TextView) findViewById(R.id.title_string);

		progressBar = (LinearLayout) findViewById(R.id.progress_bar);
		progress_text = (TextView) findViewById(R.id.progress_text);
		setOnClickListener();
		init();
	}

	void init() {
		Intent data = getIntent();
		QinQingType = data.getStringExtra("type1");
		QinqingId = data.getStringExtra("qinqing_id");
		progress_text.setText(R.string.loading);
		edit_phone_number = data.getStringExtra("phone_number");

		String type2 = data.getStringExtra("type2");

		if (type2.equals("mof")) {
			edit_old = data.getStringExtra("qinqing_phone");
			operation_qinqing_name.setText(data.getStringExtra("qinqing_name"));
			operation_qinqing_phone.setText(data
					.getStringExtra("qinqing_phone"));
			mof_button.setVisibility(View.VISIBLE);
			delete_button.setVisibility(View.VISIBLE);
			currentPosition=data.getIntExtra("position", 0);
			if("0".equals(QinQingType)){
				title_string.setText(getString(R.string.qingqing_op_string));
			}else if("1".equals(QinQingType)){
				title_string.setText(getString(R.string.friend_op_string));
			}
		} else {
			if("0".equals(QinQingType)){
				title_string.setText(getString(R.string.qingqing_add_string));
			}else if("1".equals(QinQingType)){
				title_string.setText(getString(R.string.friend_add_string));
			}
			add_button.setVisibility(View.VISIBLE);
		}


	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		add_button.setOnClickListener(this);
		mof_button.setOnClickListener(this);
		delete_button.setOnClickListener(this);
	}

	@Override
	protected void onDestoryActivity() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.add_number_button:

				if (checkEditText()) { // 添加
					if (Constants.IS_OPEN_NETWORK) {

						if (Utils
								.GuoLvShiQu(edit_phone_number,
                                        operation_qinqing_phone.getText()
                                                .toString().trim())) {

							OperationDataToBack(0, operation_qinqing_name
											.getText().toString().trim(),
									operation_qinqing_phone.getText().toString()
											.trim());

							progressBar.setVisibility(View.VISIBLE);
						} else {
							showToast(R.string.qingqing_error5);
						}

					} else {
						showToast(R.string.network_error);
					}
				}

				break;
			case R.id.mof_button:

				if (checkEditText()) { // 修改
					if (Constants.IS_OPEN_NETWORK) {

						if (edit_old.equals(operation_qinqing_phone.getText()
								.toString().trim())) {
							OperationDataToBack(1, operation_qinqing_name
											.getText().toString().trim(),
									operation_qinqing_phone.getText().toString()
											.trim());
							progressBar.setVisibility(View.VISIBLE);
						} else {
							if (Utils.GuoLvShiQu(edit_phone_number,
                                    operation_qinqing_phone.getText().toString()
                                            .trim())) {
								OperationDataToBack(1, operation_qinqing_name
												.getText().toString().trim(),
										operation_qinqing_phone.getText()
												.toString().trim());
								progressBar.setVisibility(View.VISIBLE);
							} else {
								showToast(R.string.qingqing_error5);
							}
						}

					} else {
						showToast(R.string.network_error);
					}
				}
				break;

			case R.id.delete_button:

				// 删除

				TextView view = new TextView(this);
				view.setText(getString(R.string.delete_alarm));
				new AlertDialog.Builder(this)
						.setTitle(getString(R.string.alert))
						.setView(view)
						.setPositiveButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
														int arg1) {

										if (Constants.IS_OPEN_NETWORK) {
											OperationDataToBack(2,
													operation_qinqing_name
															.getText().toString()
															.trim(),
													operation_qinqing_phone
															.getText().toString()
															.trim());
											progressBar.setVisibility(View.VISIBLE);
										} else {
											showToast(R.string.network_error);
										}

									}
								})
						.setNegativeButton(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
														int arg1) {

									}

								}).show();

				break;

		}
	}

	private void OperationDataToBack(int qinqiu_type1, String name,
									 String phone) {
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
		map.put("user_id", tools.get_user_id());
		map.put("device_imei",tools.get_current_device_id());
		map.put("relation_type", QinQingType);
		map.put("device_family_number", phone);
		map.put("device_family_name", name);
		map.put("relative_id", QinqingId);

		System.out.println("id = " + tools.get_user_id() + "\n" + "imei = "
				+ tools.get_current_device_id() + "\n" + "");
		currentFamilyMember.put("family_id", map.get("user_id"));
		currentFamilyMember
				.put("family_phone", map.get("device_family_number"));
		currentFamilyMember.put("family_relative", QinQingType);
		currentFamilyMember.put("family_nick", name);
		switch (qinqiu_type1) {
			case 0:
				currentOperation=0;

				mJr = mProtocolData.transFormToJson(map);
				uRl = Constants.QinQingAdd;
				break;
			case 1:
				currentOperation=1;
				mJr = mProtocolData.transFormToJson(map);
				uRl = Constants.QinQingMof;

				break;
			case 2:
				isDelete=true;
				currentOperation=2;
//			map.put("device_family_name", name);
				mJr = mProtocolData.transFormToJson(map);
				uRl = Constants.QinQingDelete;
				break;

		}

//		Toast.makeText(OperationQinQingActivity.this, "请求", 500).show();
		new ConnectToLinkTask().execute(uRl, mJr);
	}

	private boolean checkEditText() {
		// TODO Auto-generated method stub
		boolean flag = true;
		operation_qinqing_name.setError(null);
		operation_qinqing_phone.setError(null);
		String nameString = operation_qinqing_name.getText().toString().trim();
		String phoneString = operation_qinqing_phone.getText().toString()
				.trim();

		if (TextUtils.isEmpty(nameString)) {
			operation_qinqing_name.requestFocus();
			operation_qinqing_name
					.setError(getString(R.string.qingqing_error1));
			flag = false;
			return flag;
		}
		if (nameString.length() > 10) {
			operation_qinqing_name.requestFocus();
			operation_qinqing_name
					.setError(getString(R.string.qingqing_error2));
			flag = false;
			return flag;
		}
		if (TextUtils.isEmpty(phoneString)) {
			operation_qinqing_phone.requestFocus();
			operation_qinqing_phone
					.setError(getString(R.string.qingqing_error3));
			flag = false;
			return flag;
		}
//		if (!Constants.isMobileNO(phoneString)) {
//			operation_qinqing_phone.requestFocus();
//			operation_qinqing_phone
//					.setError(getString(R.string.qingqing_error4));
//			flag = false;
//			return flag;
//		}
		return flag;
	}

	protected void doConnectLinkCallback(String result) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = mProtocolData.getBackResult(result);
		int resultCode = (Integer) map.get("resultCode");
		progressBar.setVisibility(View.GONE);
		switch (resultCode) {
			case 1: // 成功

				showToast(R.string.other1);
				Intent i=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("family",(Serializable)currentFamilyMember);
				bundle.putString("relative", QinQingType);
				bundle.putBoolean("isDelete", isDelete);
				bundle.putInt("position", currentPosition);
				i.putExtras(bundle);
				setResult(0,i);
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
}
