package care.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import care.bean.BaoBeiBean;
import care.bean.Circle;
import care.bean.Position;
import care.bean.User;

public class BeanUtils {

	private BeanUtils() {
	}
	
	public static HashMap<String,String> getJSONParserResult(String JSONString) throws JSONException{
		HashMap<String,String> hashMap = new HashMap<String,String>();
		if(JSONString!=null){
			JSONObject result = new JSONObject(JSONString);
			Iterator<String> keys = result.keys();
			while(keys.hasNext()){
				String key = keys.next();
				String value = result.getString(key);
				hashMap.put(key, value);
			}
		}
		return hashMap;
	}
	
	public static BaoBeiBean getBaby(HashMap<String,String> map){
		BaoBeiBean baby=new BaoBeiBean();
		String phone=map.containsKey("phone")?map.get("phone"):null;
		String name=map.containsKey("name")?map.get("name"):null;
		String callme=map.containsKey("callme")?map.get("callme"):null;
		String imei=map.containsKey("imei")?map.get("imei"):null;
		String type=map.containsKey("type")?map.get("type"):null;
		String photo=map.containsKey("photo")?map.get("photo"):null;
		String phoneBooks=map.containsKey("phonebook")?map.get("phonebook"):null;
		String position=map.containsKey("position")?map.get("position"):null;
		String sex=map.containsKey("sex")?map.get("sex"):null;
		String birthday=map.containsKey("birthday")?map.get("birthday"):null;
		String grade=map.containsKey("grade")?map.get("grade"):null;
		String height=map.containsKey("height")?map.get("height"):null;
		String weight=map.containsKey("weight")?map.get("weight"):null;
		ArrayList<Map<String,Object>> phoneBook=new ArrayList<Map<String,Object>>();
		if(phoneBooks.length()>0){
			String[] phoneBookMembers=phoneBooks.split(";");
			int length=phoneBookMembers.length;
			if(length>0){
				for(int i=0;i<length;i++){
					String memberName=phoneBookMembers[i].split(",")[0];
					String memberNumber=phoneBookMembers[i].split(",")[1];
					String memberShortNumber=phoneBookMembers[i].split(",")[2];
					String isMain=phoneBookMembers[i].split(",")[3];
					Map<String,Object> memberMap=new HashMap<String,Object>();
					memberMap.put("name", memberName);
					memberMap.put("phone", memberNumber);
					memberMap.put("short_number", memberShortNumber);
					memberMap.put("is_main", isMain);
					phoneBook.add(memberMap);
				}
			}else{
				String memberName=phoneBooks.split(",")[0];
				String memberNumber=phoneBooks.split(",")[1];
				String memberShortNumber=phoneBooks.split(",")[2];
				String isMain=phoneBooks.split(",")[3];
				Map<String,Object> memberMap=new HashMap<String,Object>();
				memberMap.put("name", memberName);
				memberMap.put("phone", memberNumber);
				memberMap.put("short_number", memberShortNumber);
				memberMap.put("is_main", isMain);
				phoneBook.add(memberMap);
			}

		}
		
		try {
			HashMap<String,String> positionMap=getJSONParserResult(position);
			Position positionInfo=getPosition(positionMap);
			baby.setPhone(phone);
			baby.setName(name);
			baby.setCallme(callme);
			baby.setImei(imei);
			baby.setType(type);
			baby.setPhoto(photo);
			baby.setPhoneBook(phoneBook);
			baby.setPosition(positionInfo);
			baby.setSex(sex);
			baby.setBirthDay(birthday);
			baby.setGrade(grade);
			baby.setHeight(height);
			baby.setWeight(weight);
		} catch (JSONException e) {
			Trace.i("exceptionbaby===" + e.toString());
			e.printStackTrace();
		}

		return baby;
	}

	public static BaoBeiBean getBaoBei(HashMap<String,String> map){
		BaoBeiBean baby=new BaoBeiBean();
		String phone=map.containsKey("device_phone")?map.get("device_phone"):null;
		String name=map.containsKey("device_name")?map.get("device_name"):null;
		String callme=map.containsKey("callme")?map.get("callme"):null;
		String imei=map.containsKey("device_imei")?map.get("device_imei"):null;
		String type=map.containsKey("type")?map.get("type"):null;
		String photo=map.containsKey("device_head")?map.get("device_head"):null;
		String phoneBooks=map.containsKey("device_family_group_message")?map.get("device_family_group_message"):null;
		String position=map.containsKey("position")?map.get("position"):null;
		String sex=map.containsKey("device_sex")?map.get("device_sex"):null;
		String birthday=map.containsKey("device_age")?map.get("device_age"):null;
		String grade=map.containsKey("grade")?map.get("grade"):null;
		String height=map.containsKey("device_height")?map.get("device_height"):null;
		String weight=map.containsKey("device_weight")?map.get("device_weight"):null;
		String volume=map.containsKey("device_data_volume")?map.get("device_data_volume"):null;
		String mute=map.containsKey("device_data_mute")?map.get("device_data_mute"):null;
		String power=map.containsKey("device_data_power")?map.get("device_data_power"):null;
		String light=map.containsKey("device_data_light")?map.get("device_data_light"):null;
		String toUserId=map.containsKey("to_user_id")?map.get("to_user_id"):null;
		String shareUsers=map.containsKey("share_user")?map.get("share_user"):null;
		ArrayList<User> managerList=new ArrayList<User>();
		if(shareUsers!=null&&!"".equals(shareUsers)){
			try {
				JSONArray managerArray=new JSONArray(shareUsers);
				for(int i=0;i<managerArray.length();i++){
					JSONObject managerObject=(JSONObject)managerArray.get(i);
					HashMap<String,String> managerMap= BeanUtils.getJSONParserResult(managerObject.toString());
					User user= BeanUtils.getUser(managerMap);
					managerList.add(user);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<Map<String,Object>> phoneBook=new ArrayList<Map<String,Object>>();
		if(phoneBooks!=null){
			if(phoneBooks.length()>0){
				try {
					JSONArray phoneBookArray = new JSONArray(phoneBooks);
					int length=phoneBookArray.length();
					for(int i=0;i<length;i++){
						JSONObject phoneObject=(JSONObject)phoneBookArray.get(i);
						String familyId=phoneObject.get("family_id").toString();
						String familyPhone=phoneObject.get("family_phone").toString();
						String familyRelative=phoneObject.get("family_relative").toString();
						String familyNick=phoneObject.get("family_nick").toString();
						Map<String,Object> memberMap=new HashMap<String,Object>();
						memberMap.put("family_id", familyId);
						memberMap.put("family_phone", familyPhone);
						memberMap.put("family_relative", familyRelative);
						memberMap.put("family_nick", familyNick);
						phoneBook.add(memberMap);
					}
				} catch (JSONException e) {
					Trace.i("phoneBookException===" + e.toString());
					e.printStackTrace();
				}
			}
		}

		try {
			HashMap<String,String> positionMap=getJSONParserResult(position);
			Position positionInfo=getPosition(positionMap);
			baby.setPhone(phone);
			baby.setName(name);
			baby.setCallme(callme);
			baby.setImei(imei);
			baby.setType(type);
			baby.setPhoto(photo);
			baby.setPhoneBook(phoneBook);
			baby.setPosition(positionInfo);
			baby.setSex(sex);
			baby.setBirthDay(birthday);
			baby.setGrade(grade);
			baby.setHeight(height);
			baby.setWeight(weight);
			baby.setVolume(volume);
			baby.setMute(mute);
			baby.setPower(power);
			baby.setLight(light);
			baby.setManagerList(managerList);
			baby.setToUserId(toUserId);
		} catch (JSONException e) {
			Trace.i("exceptionbaby===" + e.toString());
			e.printStackTrace();
		}
		
		return baby;
	}
	
	public static Position getPosition(HashMap<String,String> map){
		Position position=new Position();
		String lng=map.containsKey("lng")?map.get("lng"):null;
		String lat=map.containsKey("lat")?map.get("lat"):null;
		String noTransLng=map.containsKey("no_trans_lng")?map.get("no_trans_lng"):null;
		String noTransLat=map.containsKey("no_trans_lat")?map.get("no_trans_lat"):null;
		String time=map.containsKey("time")?map.get("time"):null;
		String ptype=map.containsKey("type")?map.get("type"):null;
		String battery=map.containsKey("battery")?map.get("battery"):null;
		String accuracy=map.containsKey("accuracy")?map.get("accuracy"):null;
		position.setLng(lng);
		position.setLat(lat);
		position.setNoTransLng(noTransLng);
		position.setNoTransLat(noTransLat);
		position.setTime(time);
		position.setPtype(ptype);
		position.setBattery(battery);
		position.setAccuracy(accuracy);
		return position;
	}
	public static User getUser(HashMap<String,String> map){
		User user=new User();
		String userId=map.containsKey("user_id")?map.get("user_id"):null;
		String userName=map.containsKey("user_name")?map.get("user_name"):null;
		String nickName=map.containsKey("user_nick")?map.get("user_nick"):null;
		String userHead=map.containsKey("user_head")?map.get("user_head"):null;
		user.setId(userId);
		user.setUserName(userName);
		user.setNickName(nickName);
		user.setUserHead(userHead);
		return user;
	}
	
	
	public static Circle getCircle(HashMap<String,String> map){
		Circle circle=new Circle();
		String lng=map.containsKey("longitude")?map.get("longitude"):null;
		String lat=map.containsKey("latitude")?map.get("latitude"):null;
		String name=map.containsKey("area_name")?map.get("area_name"):null;
		String radius=map.containsKey("safe_range")?map.get("safe_range"):null;
		String imei=map.containsKey("imei")?map.get("imei"):null;
		String id=map.containsKey("safe_id")?map.get("safe_id"):null;
		String addr=map.containsKey("safe_address")?map.get("safe_address"):null;
		circle.setLng(lng);
		circle.setLat(lat);
		circle.setName(name);
		circle.setRadius(radius);
		circle.setImei(imei);
		circle.setId(id);
		circle.setAddr(addr);
		return circle;
	}
}
