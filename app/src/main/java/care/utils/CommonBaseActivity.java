package care.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gomtel.util.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.mtk.btnotification.R;

import care.application.XcmApplication;
import care.db.ProtocolData;
import care.db.manager.UpdateDB;

public abstract class CommonBaseActivity extends Activity {

	public ViewGroup contain_head;
	public ImageButton back_button;
	public TextView titleString;
	public TextView right_txt;
	public ImageButton add_button;
	
	protected InputMethodManager mInputManager;

	public XcmApplication mInstance;
	public static XcmTools tools;
	public UpdateDB mUpdateDB;
	public Button next_step;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	
	public ProgressDialog dialog = null;
	public ProtocolData mProtocolData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInstance = XcmApplication.getInstance();
		mUpdateDB = mInstance.getUpdateDB(getApplicationContext());
		mProtocolData = mInstance.getProtocolData();
		imageLoader = mInstance.getmImageLoader();
		options = mInstance.getImageOption();
		
		tools = new XcmTools(this);
		
		dialog = new ProgressDialog(this);
		mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		onCreateView(savedInstanceState);
		onViewCreate(savedInstanceState);
	}
	
	@SuppressWarnings("unused")
	private void setCacheImage() {
		// TODO Auto-generated method stub
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.head_baby1)
				.showImageOnFail(R.drawable.head_baby1)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(20)).build();
	}

	private void onViewCreate(Bundle savedInstanceState) {
		contain_head = (ViewGroup)findViewById(R.id.contain_head);
		titleString = (TextView)findViewById(R.id.title_string);
		right_txt = (TextView)findViewById(R.id.right_txt);
		back_button = (ImageButton)findViewById(R.id.back_button);
		add_button = (ImageButton)findViewById(R.id.relatives_add);
		next_step = (Button)findViewById(R.id.next_step);
		back_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		initFindView();
	}

	public class ConnectToLinkTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String messageReceive = "-3";   
			try {
				messageReceive = HttpServerUtil.invokeServer(params[0], params[1]);
				LogUtil.e("lixiang", "gomtel---" + params.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return messageReceive;
		}
		
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			
			doConnectLinkCallback(result);
		}
	}
	

	protected abstract void doConnectLinkCallback(String result);
	

	protected abstract void onCreateView(final Bundle savedInstanceState);



	protected abstract void initFindView();
	
	
	protected abstract void onDestoryActivity();
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		
		onDestoryActivity();
	}
	
//	protected void onDestoryView(String activity){
//		if(activity != null){
//			mInstance.removeActivity(activity);
//			finish();
//		}
//	}
	
	public void showToast(int resId){
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}
	
//	protected void onDestoryView(String activity){
//		if(activity != null){
//			mInstance.removeActivity(activity);
//			finish();
//		}
//	}
//	/**
//	 * ��Ӧ�ɹ�����
//	 */
//	public abstract void onResponseToBack(JSONObject response);
//	/**
//	 * �������
//	 * @param error
//	 */
//	public abstract void onErrorToBack(VolleyError error);
//	
//	public void getJsonDataByVolley(String url,String requestTxt) {
//		dialog.setCanceledOnTouchOutside(false);  //����ȡ��
//		dialog.setMessage(requestTxt);
//		dialog.show();
//		
//		JsonObjectRequest jsonRequest = new JsonObjectRequest(
//				Request.Method.POST, url, null,
//				new Response.Listener<JSONObject>() {
//
//					@Override
//					public void onResponse(JSONObject response) {
//						// TODO Auto-generated method stub
//						dialog.dismiss();
//						onResponseToBack(response);
//					}
//
//				}, new Response.ErrorListener() {
//
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						// TODO Auto-generated method stub
//						dialog.dismiss();
//						onErrorToBack(error);
//					}
//
//				});
//		mInstance.addToRequestQueue(jsonRequest);
//	}
}
