package care;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;

public class FeedBackActivity extends CommonBaseActivity implements OnClickListener {

	private EditText feed_back_connect;
	private Button feed_back_button;
	private TextView title_string;
	private LinearLayout progressBar;
	private TextView progress_text;

	protected void onCreateView(Bundle savedInstanceState) {
		setContentView(R.layout.feedback_layout);

	}

	protected void initFindView() {
		// TODO Auto-generated method stub
		title_string = (TextView)findViewById(R.id.title_string);
		feed_back_connect = (EditText) findViewById(R.id.feed_back_connect);
//		feed_back_connect.setText("意见反馈测试内容");
		progressBar = (LinearLayout) findViewById(R.id.progress_bar);
		progress_text = (TextView) findViewById(R.id.progress_text);
		feed_back_button = (Button) findViewById(R.id.feed_back_button);
		setOnClickListener();
		init();
	}

	void init() {
		title_string.setText(getString(R.string.feedback_title));
		progress_text.setText(R.string.loading);
	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		feed_back_button.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.feed_back_button:
			if (checkEditText()) {
				if (Constants.IS_OPEN_NETWORK) {
					FeedBackDataToBack(feed_back_connect.getText().toString().trim());
					progressBar.setVisibility(View.VISIBLE);
					
				} else {
					showToast(R.string.network_error);
				}
			}

			break;
		}
	}

	private void FeedBackDataToBack(String feedback_content) {
		// TODO Auto-generated method stub
		try {
			feedback_content = URLEncoder.encode(feedback_content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		String mJr = "";
		String uRl = "";
		map.put("user_id", tools.get_user_id());
		map.put("user_feedback_content", feedback_content);
		mJr = mProtocolData.transFormToJson(map);
		uRl = Constants.FEEDBACK;
//		Toast.makeText(FeedBackActivity.this, "请求", 500).show();

		new ConnectToLinkTask().execute(uRl, mJr);
	}

	private boolean checkEditText() {
		// TODO Auto-generated method stub
		boolean flag = true;
		feed_back_connect.setError(null);
		String feedbackString = feed_back_connect.getText().toString().trim();

		if (TextUtils.isEmpty(feedbackString)) {
			feed_back_connect.requestFocus();
			feed_back_connect.setError(getString(R.string.feedback_error1));
			flag = false;
			return flag;
		}

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
			finish();
			break;
		case 0: // 失败
			showToast(R.string.other2);
			break;
		case -1: // 异常
			String exception = "" + map.get("exception");
			showToast(R.string.exception_code);
			Trace.i("exception++" + exception);
			break;
		case -6:
			showToast(R.string.link_chaoshi_code);
			break;
		}
	}

	protected void onDestoryActivity() {
		// TODO Auto-generated method stub

	}

}
