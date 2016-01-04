package care;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mina.BaseMessage;
import com.mtk.btnotification.R;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import care.adapter.ChatInfoListAdapter;
import care.application.XcmApplication;
import care.bean.ChatInfoBean;
import care.bean.FileBean;
import care.clientmanager.ChatOffLineInfo;
import care.clientmanager.ClientNetManager;
import care.utils.BeanUtil;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.FileHelper;
import care.utils.SoundMeter;
import care.utils.Utils;

/**
 * Created by wid3344 on 2015/8/13.
 */
public class SingleChatActivity extends CommonBaseActivity implements SwipeRefreshLayout.OnRefreshListener,
		View.OnClickListener {
   
	private LocalBroadcastManager broadcastManager;
	private SoundMeter mSoundMeter; // 声音控制类
	private ChatInfoListAdapter adapter;
	private ListView listView;
	private SwipeRefreshLayout mSwipeLayout; 
	private Button voiceTips; // 说话按钮
	private String FilePathName = ""; // 文件存储路径
	// 对话框
	private TextView text_recorde_id;
	private ImageView volumeState;
	private ImageView huatong;
	private ProgressBar progressBar;
	private View dialog_voice;

	// 聊天类型
	private int type; // 0是单聊/1是群聊
	private String chatWinName = "0"; // 聊天窗口的名字
	private String chatWinPhone = "0"; // 和哪个窗口聊天
	private long is_stop_record = 0; // 停止录音
	private String mVoiceFileName = ""; // 声音文件路径
	private String chatLen = "0"; // 录音的语音长度
	private String sendTime = "0"; // 发送时间
	private final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private ArrayList<ChatInfoBean> mList = new ArrayList<ChatInfoBean>();

	private final int MSG_NET_FAIL = -1;
	private final int RECORDS_START = 0x02; // 开始记录
	private final int RECORDS_ERROR = 0x03; // 说话时间太短
	private final int RECOEDS_YUAN = 0x01; // 隐藏所有对话框
	private final int RECOEDS_VOICE = 0x04; // 录音
	private final int RECOEDS_UPDATE = 0x05; // 说话level
	private final int RECORDS_ERROR_LONG = 0x06; // 说话时间太短
	public final int TIP_NO_SEND = 0x09; // 不发送
	public final int TIP_SEND = 0x10; // 发送
	private final int REFRESH_COMPLETE = 0X07; //下拉刷新
	public String userId = "0";
	protected ClientNetManager mClientNetManager = null;
	private BaseMessage baseMessage;
	private String path = "";

	@Override
	protected void doConnectLinkCallback(String result) {
		
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		setContentView(R.layout.single_chat_win);
		broadcastManager = LocalBroadcastManager.getInstance(this); // 广播实例化
		mSoundMeter = new SoundMeter(); // 录音压缩类
		mClientNetManager = mInstance.getClientNetManager();
		type = getIntent().getIntExtra("chat_toState", 1);// 0是单聊,1是群聊
		chatWinName = getIntent().getStringExtra("chat_toTitle");
		chatWinPhone = getIntent().getStringExtra("chat_toPhone");
		userId = tools.get_user_id();
	}

	@Override
	protected void initFindView() {
		titleString.setText(chatWinName);
		findViewByIdMethod();
		prepareSendVoice();
		setAdapter();
	}

	private void findViewByIdMethod() {
		listView = (ListView) findViewById(R.id.message_chat_listview);
		voiceTips = (Button) findViewById(R.id.voiceTips); // 说话按钮

		volumeState = (ImageView) findViewById(R.id.volumeState); // 声音按钮
		text_recorde_id = (TextView) findViewById(R.id.text_recorde_id);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		dialog_voice = findViewById(R.id.recordingView);
		huatong = (ImageView) findViewById(R.id.recordingBgViewLeft);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);  
	    mSwipeLayout.setOnRefreshListener(this);  
	    mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
	                android.R.color.holo_orange_light, android.R.color.holo_red_light); 
	    setSwipeLayoutSize();
		next_step.setOnClickListener(this);
		next_step.setVisibility(View.VISIBLE);
		next_step.setText(getString(R.string.clean));
	}

	private void doRecord() {
		if (!Environment.getExternalStorageDirectory().exists()) {
			showToast(R.string.no_sdcard);
		} else {// 开始聊天
			is_stop_record = System.currentTimeMillis(); // 记录时间
			handler.sendEmptyMessage(TIP_SEND);
			handler.sendEmptyMessageDelayed(RECORDS_START, 20); // 500ms后执行
		}
	}

	private Runnable startRecordRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			double amp = mSoundMeter.getAmplitude(); // 获取声音的强度,循环说出
			Message msg = new Message();
			msg.what = RECOEDS_UPDATE;
			msg.arg1 = (int) amp;
			handler.sendMessage(msg);
		}
	};

	/**
	 * 发送的时候向listview添加item
	 */
	private void setChatInfo(String date_time, int len, String sendType,
			String isSend) {

		ChatInfoBean chatInfoBean = new ChatInfoBean();
		chatInfoBean.setChatContent(FilePathName); // 播放路径
		chatInfoBean.setId(0);
		chatInfoBean.setChatComeFrom("0"); // 表示手机端
		chatInfoBean.setChatHeadUrl(Constants.USERHEADURL); // 用户头像
		chatInfoBean.setChatSendTime(date_time); // 发送时间
		chatInfoBean.setChatUserName(tools.get_user_phone() + ""); // 用户名,不是角色
		chatInfoBean.setChatUserId(Constants.USERID);
		chatInfoBean.setChatType(sendType); // 单聊是语音
		chatInfoBean.setChatIsRead(isSend); // 用户发的,说明在发送状态
		chatInfoBean.setChatLen(String.valueOf(len));
		mList.add(chatInfoBean);
		adapter.addChatInfo(chatInfoBean);
		refresh();
	}

	private void startUpdateMesToBack(String date_time, int len, String sendType) {
		// TODO Auto-generated method stub
		try {
			if (Constants.IS_OPEN_NETWORK) {
				setChatInfo(date_time, len, sendType, "0"); // 发送的时候向listview添加item
															// "0"为正在发送
				baseMessage = new BaseMessage();
				baseMessage.setDataType(BeanUtil.UPLOAD_FILE);
				baseMessage.setMessageExInfo(setParameter());
				FileBean bean = new FileBean();
				bean.setFlieExName(".amr");
				File file = new File(FilePathName);
				bean.setFileName(file.getName());
				bean.setFileSize((int) file.length());
				try {
					FileHelper helper = new FileHelper();
					bean.setFileContent(helper.getContent(file));
				} catch (Exception e) {
					e.printStackTrace();
				}
				baseMessage.setData(bean);
				mClientNetManager.setHandlerToClient(handler);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {

						boolean flag = mClientNetManager
								.sessionSend(baseMessage); // 发送信息
						if (!flag) { // 没有连接网络
							handler.obtainMessage(MSG_NET_FAIL).sendToTarget();
						}
					}
				}, 1000);
			} else {
				handler.obtainMessage(MSG_NET_FAIL).sendToTarget();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startRefreshData(long time, long now) {
		int len = (int) time / 1000;
		String date_time = Constants.getNowTime(now, dateFormat);
		sendTime = date_time;
		// 上传消息至后台
		startUpdateMesToBack(sendTime, len, "3");
	}

	private void prepareSendVoice() {
		voiceTips.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					doRecord();
					break;
				case MotionEvent.ACTION_UP:
					final long now = System.currentTimeMillis();
					final long time = now - is_stop_record;
					if (time < 1000) { // 说明说话时间不合法
						Constants.DeleteFileContent(FilePathName);
						handler.sendEmptyMessage(RECORDS_ERROR);
					} else if (60 * 1000 < time) { // 大于20s
						Constants.DeleteFileContent(FilePathName);
						handler.sendEmptyMessage(RECORDS_ERROR_LONG);
					} else {
						handler.sendEmptyMessageDelayed(RECOEDS_YUAN, 100);
						handler.removeCallbacks(startRecordRun);
						mSoundMeter.stop();
						// 录音结束开始刷新数据
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								startRefreshData(time, now);
							}
						}, 300);
					}
					break;
				}
				return true;
			}
		});
	}

	private void startRecording(String path, String name) {
		mSoundMeter.start(path, name);
		handler.postDelayed(startRecordRun, 100);
	}

	private void setDataToAdapter(String resultCode) {
		int position = adapter.getCount() - 1; // 找到最后一条的信息(从0开始的)
		ChatInfoBean chatInfoBean = mList.get(position);
		chatInfoBean.setChatIsRead(resultCode);
		adapter.setDataListPosition(position, chatInfoBean);
		refresh();
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_NET_FAIL:
				showToast(R.string.network_error);
				break;
			case RECORDS_START:
				dialog_voice.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				handler.sendEmptyMessageDelayed(RECOEDS_VOICE, 100);
				break;
			case RECORDS_ERROR:
				progressBar.setVisibility(View.GONE);
				text_recorde_id.setVisibility(View.VISIBLE);
				text_recorde_id.setText(R.string.recorde_short);
				huatong.setVisibility(View.GONE);
				handler.sendEmptyMessageDelayed(RECOEDS_YUAN, 20); // 去掉对话框
				break;
			case RECORDS_ERROR_LONG:
				progressBar.setVisibility(View.GONE);
				text_recorde_id.setVisibility(View.VISIBLE);
				text_recorde_id.setText(R.string.recorde_long);
				huatong.setVisibility(View.GONE);
				volumeState.setVisibility(View.GONE);
				handler.sendEmptyMessageDelayed(RECOEDS_YUAN, 20); // 去掉对话框
				break;
			case RECOEDS_VOICE:
				progressBar.setVisibility(View.GONE);
				text_recorde_id.setVisibility(View.GONE);
				huatong.setVisibility(View.VISIBLE);
				volumeState.setVisibility(View.VISIBLE);
				// 录音命名规则userId_deviceId_time
				mVoiceFileName = userId + "_" + Constants.DEVICEID+ is_stop_record + ".amr"; // 文件名
				path = XcmApplication.VOICE_FILE + "/t_" + chatWinPhone;
				Constants.CreateFileContent(path);
				FilePathName = path + "/" + mVoiceFileName;
				startRecording(path, mVoiceFileName);
				break;
			case RECOEDS_YUAN:
				voiceTips.setText(R.string.voice_tips);
				dialog_voice.setVisibility(View.GONE);
				break;
			case RECOEDS_UPDATE:
				volumeState.setImageLevel(msg.arg1);
				handler.postDelayed(startRecordRun, 100);
				break;
			case TIP_NO_SEND:
				voiceTips.setText(R.string.voice_tips);
				handler.sendEmptyMessageDelayed(RECOEDS_YUAN, 50); // 去掉对话框
				break;
			case TIP_SEND:
				voiceTips.setText(R.string.voice_tip);
				break;
			case REFRESH_COMPLETE:    //下拉刷新
                mSwipeLayout.setRefreshing(false);  
                break; 
			case ClientNetManager.RE_SUCCESS_BACK: // 默认是正在发送的
				Bundle bundle = msg.getData();
				String result = bundle.getString(ClientNetManager.RE_KEY);
				String msgID = bundle.getString(ClientNetManager.RE_MSGID);
				if ("1".equals(result)) {
					ChatOffLineInfo lineInfo = new ChatOffLineInfo();
					lineInfo.setDataType(BeanUtil.UPLOAD_FILE);
					lineInfo.setMessageExInfo(setParameter());
					lineInfo.setChatWinPhone(chatWinPhone);
					lineInfo.setLinePath(FilePathName);
					lineInfo.setSendTime(sendTime);
					lineInfo.setComefrom("0");
					lineInfo.setHeadUrl(Constants.USERHEADURL);
					lineInfo.setNickName(tools.get_user_phone());
					lineInfo.setMessageId(msgID);
					mUpdateDB.insertToDataBases(ChatOffLineInfo.class, lineInfo);
					setDataToAdapter(result);
				} else {
					setDataToAdapter("-1");
				}
				break;
			}
		}
	};
	private void setAdapter() {
		adapter = new ChatInfoListAdapter(this, mUpdateDB);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onDestoryActivity() {

	}

	/**
	 * Called when a view has been clicked.
	 * 
	 * @param v
	 *            The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		 case R.id.next_step:
			 //清空记录
			 Calldialog();
		     break;
		 default:
		      break;
		 }
	}

	// 从数据库中取出聊天记录
	private void getChatInfoList() {
		List<?> chatList = null;
		int length = 0;
		chatList = mUpdateDB.queryDataToBases(ChatOffLineInfo.class,
				new String[] { chatWinPhone },new String[] { ChatOffLineInfo.ChatWinPhone },null, false ,15);
		if (!chatList.isEmpty()) {
			length = chatList.size();
			for (int i = 0; i < length; i++) {
				ChatInfoBean chatInfoBean = new ChatInfoBean();
				ChatOffLineInfo lineInfo = (ChatOffLineInfo) chatList.get(i);
				chatInfoBean.setChatBelongType("1");
				chatInfoBean.setChatComeFrom(lineInfo.getComefrom());
				chatInfoBean.setChatContent(lineInfo.getLinePath());
				chatInfoBean.setChatHeadUrl(lineInfo.getHeadUrl());
				chatInfoBean.setChatIsRead("1");
				chatInfoBean.setChatLen("2");
				chatInfoBean.setChatSendTime(lineInfo.getSendTime());
				chatInfoBean.setChatType("3");
				chatInfoBean.setChatUserId("12345678");
				chatInfoBean.setChatUserName(lineInfo.getNickName());
				mList.add(chatInfoBean);
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mList.clear();
		adapter.clear();
		getChatInfoList();
		adapter.setChatInfoList(mList);
		refresh();
	}

	@Override
	public void onPause() {
		super.onPause();
		broadcastManager.unregisterReceiver(uMsgReceive);
	}

	@Override
	public void onResume() {
		super.onResume();
		broadcastManager.registerReceiver(uMsgReceive, makeIntentFilter());
	}

	private IntentFilter makeIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.INTERFILTER);
		return filter;
	}

	private BroadcastReceiver uMsgReceive = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle bundle = intent.getExtras();
			if (action.equals(Constants.INTERFILTER)) {
				ChatInfoBean chatInfoBean = bundle.getParcelable("data_msg");
				try {
					updateGroupChatInfo(chatInfoBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * 收到消息在listview展示
	 */
	private void updateGroupChatInfo(ChatInfoBean chatInfoBean)
			throws Exception {
		adapter.addChatInfo(chatInfoBean); // 增加后,才行
		chatInfoBean.setId(adapter.getCount()); // 暂时没有删除功能,所以id就是个数
		refresh();
	}

	private void refresh() {
		adapter.refresh();
		listView.setSelection(adapter.getCount() - 1); // 显示最后一个
	}

	public String setParameter() {
		String byteStrMessageExInfo = null;
		int sing = 3;
		if (type == 0) {
			byteStrMessageExInfo = BeanUtil.putStringToJson(0, sing, "",
                    tools.get_login_phone(),
                    SingleChatZiFu(tools.get_login_phone(), chatWinPhone),
                    sendTime).toString();
		} else {
			byteStrMessageExInfo = BeanUtil.putStringToJson(0, sing, "",
                    tools.get_login_phone(), GroupChatZiFu(), sendTime)
					.toString();
		}
		return byteStrMessageExInfo;
	}

	public String SingleChatZiFu(String phone, String tophone) {
		String res;
		String str1 = String.valueOf(phone);
		String str2 = String.valueOf(tophone);
		long str1_ = Long.parseLong(str1);
		long str2_ = Long.parseLong(str2);
		if (str1_ < str2_) {
			res = str1_ + "," + str2_;
		} else {
			res = str2_ + "," + str1_;
		}
		return res;
	}

	public String GroupChatZiFu() {
		Set<String> set = new HashSet<String>();
		set = tools.get_family_phones();
		StringBuffer buffer = new StringBuffer();
		String[] data = set.toArray(new String[set.size()]);
		String onezifu = data[0];
		String spStr[] = onezifu.split(",");
		Arrays.sort(spStr);
		for (int i = 0; i < spStr.length; i++) {
			buffer.append(spStr[i] + ",");
		}
		return "" + buffer.subSequence(0, buffer.length() - 1);
	}
	//清空记录
	public void Calldialog() {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { 
	    @Override
		public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		   case AlertDialog.BUTTON_POSITIVE: 
		    	int value =0;
		    	List<?> chatList = mUpdateDB.queryDataToBases(ChatOffLineInfo.class,
					new String[] { chatWinPhone },new String[] { ChatOffLineInfo.ChatWinPhone }, null, false,5000);
		    	if(!chatList.isEmpty()){
		    		for (int j = 0; j < chatList.size(); j++) {
		    			ChatOffLineInfo lineInfo = (ChatOffLineInfo) chatList.get(j);
		    			Constants.DeleteFileContent(lineInfo.getLinePath());
					}
			    value = mUpdateDB.deleteDataToBases(ChatOffLineInfo.class,null,
					new String[] { chatWinPhone }, new String[] { ChatOffLineInfo.ChatWinPhone });
		    	}
				Utils.makeToast(SingleChatActivity.this, getString(R.string.clean_point_1) + value + getString(R.string.clean_point_2));
				adapter.clear();
		    	refresh();
		    break; 
			case AlertDialog.BUTTON_NEGATIVE: 
			break; }} };
		AlertDialog ad = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.clean_title))
				.setMessage(getString(R.string.clean_message))
				.setPositiveButton(getString(R.string.ok),listener)
				.setNegativeButton(getString(R.string.cancel),listener).show();
	}

	@Override
	public void onRefresh() {
		handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
	}
	/**
	 * 设置下拉刷新的长度
	 */
	public void setSwipeLayoutSize(){
		ViewTreeObserver vto = mSwipeLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		public void onGlobalLayout() {
			final DisplayMetrics metrics = getResources().getDisplayMetrics();
			Float mDistanceToTriggerSync = Math.min(((View) mSwipeLayout.getParent()).getHeight() * 0.6f, 500 * metrics.density);
			try {
				Field field = SwipeRefreshLayout.class.getDeclaredField("mDistanceToTriggerSync");
				field.setAccessible(true);
				field.setFloat(mSwipeLayout, mDistanceToTriggerSync);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ViewTreeObserver obs = mSwipeLayout.getViewTreeObserver();
			obs.removeOnGlobalLayoutListener(this);
		}
	  });
	};
}
