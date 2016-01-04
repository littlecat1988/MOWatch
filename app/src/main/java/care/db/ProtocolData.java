package care.db;

import com.mina.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;

import care.bean.FileBean;
import care.utils.Constants;

/**
 * 
 * 数据解析类
 */
public class ProtocolData {

	private volatile static ProtocolData mProtocol;
	
	/**
	 * 单例模式
	 * @return
	 */
	public static ProtocolData getInstance() {
		if(mProtocol == null){
			synchronized (ProtocolData.class) {
				if(mProtocol == null){
					mProtocol = new ProtocolData();
				}		
			}	
		}
		return mProtocol;
	}
	
	/**
	 * 数据
	 */
	public String transFormToJson(HashMap<String,Object> values){
		JSONObject js = new JSONObject();
		try {
			for(String key : values.keySet()){
				js.put(key, values.get(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return js.toString();
	}
	/**
	 * 获取数据后解析
	 */
	public HashMap<String,Object> getBackResult(String result){
		HashMap<String, Object> backMessage = new HashMap<String, Object>();
		JSONTokener jsonParser = new JSONTokener(result);
		JSONObject mes;
		try {
			mes = (JSONObject) jsonParser.nextValue();
			String request = mes.has("request") ? mes.getString("request")
					: "";
			backMessage.put("request", request);
			int resultCode = mes.has("resultCode") ? mes.getInt("resultCode") : 0;
			backMessage.put("resultCode", resultCode);
			
			String exception = mes.has("exception") ? mes.getString("exception")
					: "";
			backMessage.put("exception", exception);
			if(Constants.REGISTER.contains(request)){  //注册
				String user_id = mes.has("user_id") ? mes.getString("user_id") : "0";
				backMessage.put("user_id", user_id);
			}else if(Constants.LOGIN.contains(request)){
				String user_id = ""+mes.get("user_id");
				String user_nick = ""+mes.get("user_nick");
				String user_sex = ""+mes.get("user_sex");
				String user_age = ""+mes.get("user_age");
				String user_height = ""+mes.get("user_height");
				String user_weight = ""+mes.get("user_weight");
				String user_head = ""+mes.get("user_head");
//				String device_imei = ""+mes.get("device_imei");
//				backMessage.put("device_imei", device_imei);
				backMessage.put("user_id", user_id);			
				backMessage.put("user_nick", user_nick);			
				backMessage.put("user_sex", user_sex);				
				backMessage.put("user_age", user_age);			
				backMessage.put("user_height", user_height);
				backMessage.put("user_weight", user_weight);				
				backMessage.put("user_head", user_head);
				
			}else if(Constants.VERIFYCODE.contains(request)){
				String device_id = ""+mes.get("device_id");
				backMessage.put("device_id", device_id);	
			}else if(Constants.GETMESSAGE.contains(request)){
				String msg_count = mes.has("msg_count")?mes.getString("msg_count"):"0";
				String msg_array = mes.has("msg_array")?mes.getString("msg_array"):"0";
//				JSONArray array = new JSONArray(msg_array);
				backMessage.put("msg_count", msg_count);
				backMessage.put("msg_array", msg_array);
//				{“msg_count”,”msg_count”} 回传个数
//				{“msg_array”,”msg_array”}  消息数组
//				其中msg_array包括
//				{“msg_id”,“msg_content”,“msg_date”}
//				String[] array = {"msg_id","msg_content","msg_date"};

			}else if(Constants.QinQingAdd.contains(request))
			{
				String relative_id = mes.has("relative_id")?mes.getString("relative_id"):"0";
				backMessage.put("relative_id", relative_id);
				
			}
			else if(Constants.PERSONETTING.contains(request))
			{
				String user_head = mes.has("user_head")?mes.getString("user_head"):"0";
				backMessage.put("user_head", user_head);
				
			}else if(Constants.BABYSETTING.contains(request))
			{				
				String device_head = mes.has("device_head")?mes.getString("device_head"):"0";
				backMessage.put("device_head", device_head);
			}else if(Constants.DOGETFALL.contains(request)){
				String fall = mes.has("fall")?mes.getString("fall"):"0";
				backMessage.put("fall", fall);
				String repellent = mes.has("repellent")?mes.getString("repellent"):"0";
				backMessage.put("repellent", repellent);
				String gps_on = mes.has("gps_on")?mes.getString("gps_on"):"0";
				backMessage.put("gps_on", gps_on);
			}else if(Constants.GET_LOWELECTRICITY.contains(request)){
				String isLow = mes.has("isLow")?mes.getString("isLow"):"0";
				backMessage.put("isLow", isLow);
				String electricity = mes.has("electricity")?mes.getString("electricity"):"0";
				backMessage.put("electricity", electricity);
			}else if(Constants.SET_CLOCK.contains(request)){

			}else if(Constants.GET_CLOCK.contains(request)){
				String clock = mes.has("clock")?mes.getString("clock"):"0";
				backMessage.put("clock", clock);
			}else if(Constants.GET_SLEEP.contains(request)){
				String clock = mes.has("clock")?mes.getString("clock"):"0";
				backMessage.put("clock", clock);
			}else if(Constants.GET_DISTURB.contains(request)){
				String moday = mes.has("moday")?mes.getString("moday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("moday", moday);
				String tuesday = mes.has("tuesday")?mes.getString("tuesday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("tuesday", tuesday);
				String wednesday = mes.has("wednesday")?mes.getString("wednesday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("wednesday", wednesday);
				String thursday = mes.has("thursday")?mes.getString("thursday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("thursday", thursday);
				String friday = mes.has("friday")?mes.getString("friday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("friday", friday);
				String saturday = mes.has("saturday")?mes.getString("saturday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("saturday", saturday);
				String sunday = mes.has("sunday")?mes.getString("sunday"):"07:00,09:00;09:00,11:00;14:00,17:00";
				backMessage.put("sunday", sunday);
				String distrub = mes.has("distrub")?mes.getString("distrub"):"1";   //默认是打开
				backMessage.put("distrub", distrub);
			}else if(Constants.SET_DISTURB.contains(request)){

			}else if(Constants.UPDATEAPP.contains(request)){
				String download_url = mes.has("download_url")?mes.getString("download_url"):"0";   //下载地址
				backMessage.put("download_url", download_url);
				String name = mes.has("apk_version_name")?mes.getString("apk_version_name"):"0";   //最新版本号
				backMessage.put("name", name);

			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return backMessage;
	}

	/**
	 * 数据回传(长连接)
	 * @return
	 */
	public HashMap<String, Object> getMsgBean(Object result) throws Exception{
		HashMap<String, Object> map = new HashMap<String, Object>();
		BaseMessage baseMessage=(BaseMessage)result;   
		map.put("DataType", baseMessage.getDataType());
		FileBean bean=(FileBean) baseMessage.getData();
		map.put("fileExName",bean.getFlieExName());
		map.put("fileName", bean.getFileName());
		map.put("fileSize", bean.getFileSize());
		map.put("fileContent", bean.getFileContent());
		map.put("messageExInfo", baseMessage.getMessageExInfo());
		return map;
	}
	/**
	 * 数据回传(长连接)
	 * @return
	 */
	public HashMap<String,Object> getSocketBackResult(String result){
		HashMap<String, Object> backMessage = new HashMap<String, Object>();
		JSONTokener jsonParser = new JSONTokener(result);
		JSONObject mes;
		try {
			mes = (JSONObject) jsonParser.nextValue();
			String request = mes.has("request") ? mes.getString("request")
					: "";
			backMessage.put("request", request);
			int resultCode = mes.has("resultCode") ? mes.getInt("resultCode") : 0;
			backMessage.put("resultCode", resultCode);
			if(Constants.MSG_SENDMSG.contains(request)){

			}
		}catch (JSONException e) {
				e.printStackTrace();
			}
			return backMessage;
	}
	/**
	 * fun name: getChatResult
	 *
	 * @description
	 * @param
	 * @return
	 */
	public HashMap<String, Object> getChatResult(String json) {

		HashMap<String, Object> backMessage = new HashMap<String, Object>();
		JSONTokener jsonParser = new JSONTokener(json);
		JSONObject mes;
		try {
			mes = (JSONObject) jsonParser.nextValue();
			String request = mes.has("request") ? mes.getString("request")
					: null;
			backMessage.put("request", request);
			if ("MSG_RECEIVEMSG".equals(request)) {
				int chatType = mes.has("type") ? mes.getInt("type") : -1;
				backMessage.put("type", chatType);
				int sendType = mes.has("send_type") ? mes.getInt("send_type")
						: -1;
				backMessage.put("send_type", sendType);
				Integer fromID = mes.has("from_id") ? mes.getInt("from_id") : 0;
				backMessage.put("from_id", fromID);
				String fromUserName = mes.has("from_username") ? mes
						.getString("from_username") : null;
				backMessage.put("from_username", fromUserName);
				int contentType = mes.has("c_type") ? mes.getInt("c_type") : 0;
				backMessage.put("c_type", contentType);
				String timesend = mes.has("timesend") ? mes
						.getString("timesend") : null;
				backMessage.put("timesend", timesend);
				String content = mes.has("content") ? mes.getString("content")
						: null;
				backMessage.put("content", content);
				String contentName = mes.has("content_name") ? mes
						.getString("content_name") : null;
				backMessage.put("content_name", contentName);
				int timeLen = mes.has("timelen") ? mes.getInt("timelen")
						: 0;
				backMessage.put("timelen", timeLen);
				int chatgroupid = mes.has("chatgroupid") ? mes
						.getInt("chatgroupid") : 0;
				backMessage.put("chatgroupid", chatgroupid);
				int fromType = mes.has("from_type") ? mes.getInt("from_type")
						: 0;
				backMessage.put("from_type", fromType);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return backMessage;
	}
}
