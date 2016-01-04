package care.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * 锟芥储锟斤拷锟斤拷
 * */
public class BeanUtil {
	/**
	 * 锟街凤拷锟斤拷锟�
	 * */
  public final static  Charset charset = Charset.forName("utf-8");
  public final static int UPLOAD_FILE = 1;	//锟斤拷锟斤拷锟侥硷拷
  
  public static JSONObject putStringToJson(int isFirst,int type,String messageID,String srcPhoneNum,String chatPhoneNums
		,String sendTime){
	  JSONObject jsonObject=new JSONObject();
	  try {
      jsonObject.put("isFirstSend", isFirst);
	  jsonObject.put("type", type);
	  jsonObject.put("srcPhoneNum", srcPhoneNum);
	  jsonObject.put("chatPhoneNums", chatPhoneNums);
	  jsonObject.put("sendTime",sendTime);
	  jsonObject.put("bg_proj", 12);
	  
	  if(type==6){
		  jsonObject.put("result", 1);
		  jsonObject.put("messageID", messageID);
	  }
	  
	  } catch (JSONException e) {
			e.printStackTrace();
	}
	  return jsonObject;
  }  
}
