package care;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import care.adapter.ContactListAdapter;
import care.bean.ChatInfoBean;
import care.bean.OffLineCountBean;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;

/**
 * Created with IntelliJ IDEA. Author: wangjie email:wangjie@cyyun.com Date:
 * 13-6-14 Time: 下午2:39
 */
public class ChatActivity extends CommonBaseActivity implements OnClickListener,OnItemClickListener{
	private LinearLayout groups_layout;
	private ListView contactlist_listview;
	private ContactListAdapter contactListAdapter;
    private String CHAT_PHONE="chat_phone";
	private String CHAT_CICKNAME="chat_nickName";
	private String RELATIVE="relative";
	public static String GROUPID="00xx18";
	private ImageButton toLocus,to_location,to_listen,to_chat;
	private Handler mHandler;
	private TextView isRead;
	private ArrayList<OffLineCountBean> list_count=new ArrayList<OffLineCountBean>();
	private LocalBroadcastManager mbroadcastManager;
	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_chat);
	}
	@Override
	protected void initFindView() {
		mbroadcastManager = LocalBroadcastManager.getInstance(this);  //广播实例化
		groups_layout=(LinearLayout)findViewById(R.id.groups_layout);
		groups_layout.setOnClickListener(this);
		contactlist_listview=(ListView)findViewById(R.id.contactlist_listview);
		to_location=(ImageButton)findViewById(R.id.to_location);
		to_location.setOnClickListener(this);
		toLocus=(ImageButton)findViewById(R.id.toLocus);
		toLocus.setOnClickListener(this);
		to_listen=(ImageButton)findViewById(R.id.to_listen);
		to_listen.setOnClickListener(this);
		to_chat=(ImageButton) findViewById(R.id.to_chat);
		to_chat.setOnClickListener(this);
		to_chat.setImageResource(R.drawable.icon_chat_pressed);
		isRead=(TextView) findViewById(R.id.isRead);
		mHandler=new Handler();
		mSetAdapter();
	}

	private void mSetAdapter() {
        try {
			contactListAdapter=new ContactListAdapter(ChatActivity.this, getData());
			contactlist_listview.setAdapter(contactListAdapter);
			contactlist_listview.setOnItemClickListener(this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mbroadcastManager.unregisterReceiver(uMsgReceive);
	}
	@Override
    public void onResume(){
	    super.onResume();
		mbroadcastManager.registerReceiver(uMsgReceive, makeIntentFilter());
    }
	private IntentFilter makeIntentFilter() {
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(Constants.INTERFILTER);
	        return filter;
	    }
	 private BroadcastReceiver uMsgReceive = new BroadcastReceiver(){
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            Bundle bundle = intent.getExtras();
	            if(action.equals(Constants.INTERFILTER)){
	                ChatInfoBean chatInfoBean = bundle.getParcelable("data_msg");
	                
	            }
	        }
	    };
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.groups_layout:
			startIntent(1,GROUPID,"群聊");
			break;
		case R.id.to_location:
			finish();
			break;
		case R.id.toLocus:
			Intent i=new Intent(this, LocusActivity.class);
			startActivity(i);
			finish();
			break;
		case R.id.to_listen:
			Calldialog();
			break;
		case R.id.to_chat:
			break;
		default:
			break;
		}
	}
	private List<Map<String, String>> getData() throws JSONException{
		List<Map<String, String>> list=new ArrayList<Map<String,String>>();
		StringBuffer sb = new StringBuffer();
		JSONArray jsonArray=new JSONArray(tools.get_babyList());
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject=(JSONObject) jsonArray.get(i);
			if(jsonObject.has("device_family_group_message")){
				JSONArray array=jsonObject.getJSONArray("device_family_group_message");
				String dfgm=jsonObject.getString("device_family_group_message");
				tools.set_family_info(dfgm);
				for (int j = 0; j < array.length(); j++) {
					Map<String,String> map=new HashMap<String, String>();
					JSONObject object=(JSONObject) array.get(j);
					if(!object.getString("family_phone").equals(tools.get_login_phone())){
						map.put(CHAT_PHONE, object.getString("family_phone"));
						sb. append(object.getString("family_phone")+",");
						map.put(CHAT_CICKNAME, object.getString("family_nick"));
						map.put(RELATIVE, object.getString("family_relative"));
						list.add(map);
					}
				}
			}
			sb.append(jsonObject.getString("device_phone")+","); 
		}
		if(!sb.toString().isEmpty()){
			 Set<String> set=new HashSet<String>();
		     set.add(tools.get_login_phone()+","+sb.subSequence(0,sb.length()-1));
		     tools.set_family_phones(set);
		     }
		return list;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			List<Map<String,String>> mList=getData();
			String toPhone=mList.get(position).get(CHAT_PHONE)+"";
			String toTltle=mList.get(position).get(CHAT_CICKNAME)+"";
			startIntent(0, toPhone,toTltle);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void startIntent(int state,String phone,String title){
		Intent mIntent=new Intent();
		mIntent.putExtra("chat_toState",state);
		mIntent.putExtra("chat_toPhone",phone);
		mIntent.putExtra("chat_toTitle", title);
		mIntent.setClass(ChatActivity.this, SingleChatActivity.class);
		startActivity(mIntent);
	}
	@Override
	protected void doConnectLinkCallback(String result) {
		
	}

	@Override
	protected void onDestoryActivity() {
		mHandler.removeCallbacks(mRunnable2);
	};
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
							Toast.makeText(ChatActivity.this, getString(R.string.listen_success), Toast.LENGTH_SHORT).show();
						}else if("0".equals(result)){
							Toast.makeText(ChatActivity.this, getString(R.string.listen_failed), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						Trace.i("exception=======" + e.toString());
						e.printStackTrace();
					}
				}
			});
		}
	};
}
