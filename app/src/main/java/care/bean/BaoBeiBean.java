package care.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

public class BaoBeiBean implements Parcelable{

    private int id;             //脦篓脪禄id
	
	private String baoBeiUrl;   //脥路脧帽碌脴脰路
	
	private String baoBeiName;  //脜贸脫脩锚脟鲁脝
	
	private String baoBeiPhone; //脜贸脫脩碌莽禄掳潞脜脗毛
	
	private String baoBeiSelect; //脢脟路帽卤禄脩隆脰脨,0脢脟虏禄卤禄脩隆脰脨,1脢脟卤禄脩隆脰脨
	private String fromUser; //鐢辫皝鍒嗕韩鐨�
	private boolean isShareAgree=false;
	private boolean isCurrent=false;
	private String phone;
	private String name;
	private String callme;
	private String imei;
	private String type;
	private String photo;
	private String sex;
	private String birthDay;
	private String grade;
	private String height;
	private String weight;
	private ArrayList<Map<String,Object>> phoneBook;
	private Position position;
	private String volume;

	private String mute;
	private String power;
	private String light;
	private ArrayList<User> managerList;
	private String toUserId;

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public ArrayList<User> getManagerList() {
		return managerList;
	}

	public void setManagerList(ArrayList<User> managerList) {
		this.managerList = managerList;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public BaoBeiBean(){
		phoneBook=new ArrayList<Map<String,Object>>();
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCallme() {
		return callme;
	}
	public void setCallme(String callme) {
		this.callme = callme;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public ArrayList<Map<String, Object>> getPhoneBook() {
		return phoneBook;
	}
	public void setPhoneBook(ArrayList<Map<String, Object>> phoneBook) {
		this.phoneBook = phoneBook;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getMute() {
		return mute;
	}

	public void setMute(String mute) {
		this.mute = mute;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getLight() {
		return light;
	}

	public void setLight(String light) {
		this.light = light;
	}

	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}

	public String getBaoBeiUrl() {
		return this.baoBeiUrl;
	}
	public void setBaoBeiUrl(String baoBeiUrl) {
		this.baoBeiUrl = baoBeiUrl;
	}

	public String getBaoBeiName() {
		return this.baoBeiName;
	}
	public void setBaoBeiName(String baoBeiName) {
		this.baoBeiName = baoBeiName;
	}

	public String getBaoBeiPhone() {
		return this.baoBeiPhone;
	}
	public void setBaoBeiPhone(String baoBeiPhone) {
		this.baoBeiPhone = baoBeiPhone;
	}

	public String getBaoBeiSelect() {
		return this.baoBeiSelect;
	}
	public void setBaoBeiSelect(String baoBeiSelect) {
		this.baoBeiSelect = baoBeiSelect;
	}

	public boolean isShareAgree() {
		return isShareAgree;
	}

	public void setShareAgree(boolean isShareAgree) {
		this.isShareAgree = isShareAgree;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		return sb
				.append("id").append(id)
				.append("baoBeiUrl").append(baoBeiUrl)
				.append("baoBeiName").append(baoBeiName)
				.append("baoBeiPhone").append(baoBeiPhone)
				.append("baoBeiSelect").append(baoBeiSelect)
				.toString();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(baoBeiUrl);
		dest.writeString(baoBeiName);
		dest.writeString(baoBeiPhone);
		dest.writeString(baoBeiSelect);
	}
	public static final Creator<BaoBeiBean> CREATOR = new Creator<BaoBeiBean>(){
		
		public BaoBeiBean createFromParcel(Parcel source) {
			BaoBeiBean baoBeiBean = new BaoBeiBean();
			
			baoBeiBean.setId(source.readInt());
			baoBeiBean.setBaoBeiUrl(source.readString());
			baoBeiBean.setBaoBeiName(source.readString());
			baoBeiBean.setBaoBeiPhone(source.readString());	
			baoBeiBean.setBaoBeiSelect(source.readString());	
			
			return baoBeiBean;
		}

		@Override
		public BaoBeiBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new BaoBeiBean[size];
		}
	};
}
