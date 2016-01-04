package care.clientmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.mina.BaseMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import care.ChatActivity;
import care.application.XcmApplication;
import care.bean.ChatInfoBean;
import care.bean.FileBean;
import care.bean.OffLineCountBean;
import care.db.DataHelper;
import care.db.ProtocolData;
import care.db.manager.DataBaseManagerImpl;
import care.db.manager.UpdateDB;
import care.utils.BeanUtil;
import care.utils.Constants;
import care.utils.MathProtocolCodecFactory;
import care.utils.Trace;
import care.utils.XcmTools;

public class ClientNetManager extends IoHandlerAdapter {

	private XcmTools tools;
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private static SocketConnector mConnector = null;
	private static SocketAddress soketAddress = null;

	private static IoSession mSession;
	/** 30秒后空闲时间 */
	private final int IDELTIMEOUT =180;
	/** 设置连接时间 */
	private final int CONNECTTIME = 180*1000;
	// 服务器回复成功
	public static final int RE_SUCCESS_BACK = 0x08;

	public Handler mHandler = null;
	private volatile static ClientNetManager mClientNetManager = null;

	private DataBaseManagerImpl mDataBaseManagerImpl = null;
	private DataHelper help = null;
	private UpdateDB mUpdateDB = null;
	private ProtocolData mProtocol = null;
	private XcmApplication mIntance;
	private int sing=1;
	private int INSERTSUCCESS;
	// 后台回传成功的key
	public final static String RE_KEY = "re_key";
	public final static String RE_MSGID = "re_msgid";
    private ArrayList<OffLineCountBean> mList=new ArrayList<OffLineCountBean>(); //记录有多少条离线消息
	
	private ClientNetManager() {

	}
    //当创建一个新连接时被触发，即当开始一个新的Session时被触发
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		 Trace.i("连接创建");
		super.sessionCreated(session);
	}
    //当打开一个连接时被触发
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		Trace.i("连接打开+++++");
		mSession = session;
		if(!"".equals(Constants.USERID)){   //若有,则重连,否则只是打开连接
			Trace.i("Constants.USERID++++" + Constants.USERID);
//			reconnect();   //socket连上后,重登陆
		}
		super.sessionOpened(session);
		tools=new XcmTools(mIntance);
		String sendTime = Constants.getNowTime(System.currentTimeMillis(), dateFormat);
		BaseMessage baseMessage=new BaseMessage();
		FileBean fileBean = new FileBean();
		fileBean.setFileSize(0);
	    baseMessage.setDataType(BeanUtil.UPLOAD_FILE);
	    baseMessage.setMessageExInfo(BeanUtil.putStringToJson(1, 1, "", tools.get_login_phone(),
                "", sendTime).toString());
	    baseMessage.setData(fileBean);
	    session.write(baseMessage);
	}
    //当连接空闲时被触发
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		mSession = session;
		super.sessionIdle(session, status);
	}

	//有异常发生时被触发
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		Log.e("speak", sb.toString());
		Trace.i("连接断开____+exceptionCaught");
		super.exceptionCaught(session, cause);
	}
    //当连接关闭时被触发，即Session终止时被触发。
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Trace.i("连接断开____+sessionClosed");
		super.sessionClosed(session);
	}
    /**
     *StrictMode有多种不同的策略，每一种策略又有不同的规则，
     * 当开发者违背某个规则时，每个策略都有不同的方法去显示提醒用户
     */
	private void initNet() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
	}
    /**
     * 单例模式
     */
	public static ClientNetManager getInstance() {
		if (mClientNetManager == null) {
			synchronized (ClientNetManager.class) {
				if (mClientNetManager == null) {
					mClientNetManager = new ClientNetManager();
				}
			}
		}
		return mClientNetManager;
	}

	/**
	 * 初始化
	 */
	public void initialize() {
		mIntance = XcmApplication.getInstance();
		mUpdateDB = mIntance.getUpdateDB(mIntance);
		mDataBaseManagerImpl = mIntance.getDataBaseManagerImpl(mIntance);
		help = mDataBaseManagerImpl.getDataHelperToData();
		mProtocol = mIntance.getProtocolData();
		
	}

	/**
	 * 初始化连接
	 */
	public String initialOpenConnection() {
		if (Constants.IS_OPEN_NETWORK) {
			initNet();
			// 异步回调
			if(mConnector == null){
				Trace.i("初始化连接");
				mConnector = new NioSocketConnector();
				mConnector.setHandler(this);
				
				//设置编码格式
				mConnector.getFilterChain().addLast("codec", 
						new ProtocolCodecFilter(new MathProtocolCodecFactory(false)));
			
				mConnector.setConnectTimeoutMillis(CONNECTTIME);
				mConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
						IDELTIMEOUT);
				
				//设置1m的缓存
				mConnector.getSessionConfig().setReadBufferSize(1024*1024); 
			}
			// 设置链接地址
			soketAddress = new InetSocketAddress(Constants.HOSTIP, Constants.PORT);
			// 建立连接
			ConnectFuture mConnectFuture = mConnector.connect(soketAddress);

			// 设置后,等待异步消息返回
			mConnectFuture.awaitUninterruptibly();
			
			try{
				mSession = mConnectFuture.getSession();
				if(mSession != null){
				    Trace.i("获取到session+initialOpenConnection");
				}

			}catch(RuntimeException e){
				Trace.i("获取不到的session异常");
				return "-1";
			}
			return "1";
		}else{
			return "0";
		}
	}

	/**
	 * 关闭连接
	 */
	public void closeConnection() {
		if (mConnector != null) {
			mConnector.dispose();
			mConnector.getFilterChain().clear();
			mConnector = null;
		}
		if(mSession != null){
			mSession =null;
		}
	}  

	/**
	 * 异步回调的接受数据方法,
	 * 有消息到达时被触发，message代表接收到的消息
	 */
	@Override
	public void messageReceived(IoSession session, Object messageObj) throws Exception{
		super.messageReceived(session, messageObj);
		String message =messageObj.toString();
		Trace.i("收到消息");
		if (message!=null) {
			receiveMsgFromBack(session,messageObj);
		}
	}
    /**
     *使用hashmap打包数据,并发广播通知 
     */
	private void receiveMsgFromBack(IoSession session,Object message) {
		// TODO Auto-generated method stub
		HashMap<String, Object> backMessage = null;
		try {
			backMessage = mProtocol.getMsgBean(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      ChatInfoBean tmp=lksaveChatInfo(backMessage);
	}
     /**
      * 发广播方法
      */
	private void sendBroadcast(ChatInfoBean tmp, String sendBroad,
							   Bundle bundles) {
		Intent broastIntent = new Intent(sendBroad);
		if (tmp != null) {
			Bundle bundle = new Bundle();
			bundle.putParcelable("data_msg", tmp);
			broastIntent.putExtras(bundle);
		}
		if (bundles != null) {
			broastIntent.putExtras(bundles);
		}
		mIntance.sendBroadcast(broastIntent);// 通知
	}

//-----------------------------------------------------------------------------------------------------	
	private ChatInfoBean lksaveChatInfo(HashMap<String, Object> backMessage) {
		final ChatInfoBean chatInfoBean=new ChatInfoBean();
		String mMessageExInfo=(String) backMessage.get("messageExInfo");
    	String sendTime = null;
    	String chatPhoneNums=null;
    	String srcPhoneNum=null;
    	String messageID=null;
    	String type=null;
    	String result=null;
		try {
			JSONObject object=new JSONObject(mMessageExInfo);
			type=object.getString("type");
	    	sendTime=object.getString("sendTime");
	    	chatPhoneNums=object.getString("chatPhoneNums");
	    	srcPhoneNum=object.getString("srcPhoneNum");
		if(type.equals("2")){
			Trace.i("返回的消息2=" + mMessageExInfo);
			sendBroadcast(chatInfoBean, Constants.INTERFILTER, null); // 发广播通知
			return null;
		}else if (type.equals("4")){
			result=object.getString("result");
			messageID=object.getString("messageID");
			Trace.i("返回的消息4=" + mMessageExInfo);
			Bundle bundle = new Bundle();
			bundle.putString(RE_KEY, result);
			bundle.putString(RE_MSGID, messageID);
			Message msg = Message.obtain(mHandler, RE_SUCCESS_BACK);
			msg.setData(bundle);
			msg.sendToTarget();
			return null;
		}
		else if (type.equals("5")){
			Trace.i("返回的消息5=" + mMessageExInfo);
			messageID=object.getString("messageID");
			try {
			FileOutputStream os = null;
			File voiceFile = null;
			String saveName = "";
			int DataType=(Integer) backMessage.get("DataType");
			String tempName = Constants.getTime() + ""; // 临时名字
			tempName = tempName.replace("-", "");
			tempName = tempName.replace(":", "");
			tempName = tempName.replace(" ", "");
			tempName = tempName.replace(".", "");
			saveName = tempName + ".amr"; // 时间作为名字
			String path = XcmApplication.VOICE_FILE + "/f_"
					+ String.valueOf(srcPhoneNum);
			File voiceDir = new File(path);
			if (!voiceDir.exists()) {
				voiceDir.mkdirs();
			}
			voiceFile = new File(path + "/" + saveName);
			if (!voiceFile.exists()) {
					voiceFile.createNewFile();
			}
			byte[] b=(byte[]) backMessage.get("fileContent");
			os = new FileOutputStream(voiceFile);
			os.write(b); // 把声音输出(按流的形式输出) 
			os.close();
			chatInfoBean.setChatBelongType("1");
			chatInfoBean.setChatComeFrom("1");   //别人
			chatInfoBean.setChatContent(voiceFile.getPath());
			chatInfoBean.setChatHeadUrl("");    //头像
			chatInfoBean.setChatIsRead("1");  
			chatInfoBean.setChatSendTime(sendTime);
			chatInfoBean.setChatType("3");     //语音
			chatInfoBean.setChatLen("2");
			chatInfoBean.setChatUserId("xxx");  //暂时无用！
			chatInfoBean.setChatUserName(family_info(srcPhoneNum));
			
			ChatOffLineInfo lineInfo=new ChatOffLineInfo();
			lineInfo.setMessageExInfo(mMessageExInfo);
			lineInfo.setDataType(DataType);
			lineInfo.setLinePath(voiceFile.getPath());
			lineInfo.setSendTime(sendTime);
			lineInfo.setComefrom("1");
			lineInfo.setHeadUrl("");
    		lineInfo.setNickName(family_info(srcPhoneNum));
    		lineInfo.setMessageId(messageID);
			if(getChatPhoneNums(chatPhoneNums).length>2){
				//群聊
				lineInfo.setChatWinPhone(ChatActivity.GROUPID);
			}
			else {
				//单聊
				lineInfo.setChatWinPhone(srcPhoneNum);
			}
			long count =mUpdateDB.queryDataCountToBases(ChatOffLineInfo.class, new String[] {messageID}
		       , new String[] { ChatOffLineInfo.MessageID });
			if(count == 0){
				INSERTSUCCESS=mUpdateDB.insertToDataBases(ChatOffLineInfo.class,lineInfo);
			  if(INSERTSUCCESS==1){
				  sendBroadcast(chatInfoBean, Constants.INTERFILTER, null); // 发广播通知
			    }
			  }
			if(INSERTSUCCESS==1){
			    OffLineCountBean offLineCountBean=new OffLineCountBean();
				offLineCountBean.setMessageID(messageID);
				offLineCountBean.setChatWinPhone(lineInfo.getChatWinPhone());
				//回转给服务器
				BaseMessage baseMessage=new BaseMessage();
				FileBean fileBean = new FileBean();
				fileBean.setFileSize(0);
			    baseMessage.setDataType(BeanUtil.UPLOAD_FILE);
			    sing=6;
			    baseMessage.setMessageExInfo(BeanUtil.putStringToJson(0, sing, messageID, tools.get_login_phone(),
                        "", sendTime).toString());
			    baseMessage.setData(fileBean);
			    sessionSend(baseMessage);
			    mList.add(offLineCountBean);//data_msg_count
			  }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return chatInfoBean;
	}
//------------------------------------------------------------------------------------------------	
	/**
	 * fun name: sessionSend
	 *
	 * @description Judge whether the connection 
	 * @param str
	 *            : The string to be sent
	 * @return
	 */
	public synchronized boolean sessionSend(BaseMessage str) {
		boolean falg = false;
		try {
//			 mSession = mConnectFuture.getSession();
			if (mSession != null) {
				boolean flag = mSession.isConnected();   //说明未登录
				if (flag) {
					// 异步操作,写数据
					Trace.i("正在发送" + str.getMessageExInfo());
					mSession.write(str);
					falg = true;
				} else {
					if(Constants.IS_OPEN_NETWORK){
						mIntance.checkNet(Constants.IS_OPEN_NETWORK);
						falg = reWrite(str);
					}
					
				}

			}else{
				String result = initialOpenConnection();
				if("-1".equals(result)){  //初始化连接	
					Toast.makeText(mIntance, "连接失败", Toast.LENGTH_LONG).show();
				}else if("1".equals(result)){
					falg = reWrite(str);       
				}
				Trace.i("mSession++++为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			falg = false;
		}
		return falg;
	}

	private boolean reWrite(BaseMessage str) {
		// TODO Auto-generated method stub
		boolean falg = false;
		if (mSession != null) {
			boolean flag = mSession.isConnected();   //说明未登录
			if (flag) {
				// 异步操作,写数据
				mSession.write(str);
				falg = true;
			}
		}
		return falg;
	}

	public void setHandlerToClient(Handler mHandler) {
		this.mHandler = mHandler; // 用handler回调机制
	}
	public String family_info(String phone){
		String info=tools.get_babyList();
		Map<String,String> map=new HashMap<String, String>();
		try {
			JSONArray array=new JSONArray(info);
			for (int i = 0; i < array.length(); i++) {
			    JSONObject object=(JSONObject) array.get(i);
			    map.put(object.getString("device_phone"), object.getString("device_name"));
			    if(object.has("device_family_group_message")){
					JSONArray array2=object.getJSONArray("device_family_group_message");
					for (int j = 0; j < array2.length(); j++) {
						JSONObject object2=(JSONObject) array2.get(j);
						if(!object2.getString("family_phone").equals(tools.get_login_phone())){
							map.put(object2.getString("family_phone"), object2.getString("family_nick"));
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map.get(phone);
	}
	public String[] getChatPhoneNums(String phoneString){
		String[] data=phoneString.split(",");
		return data;
	}
}
