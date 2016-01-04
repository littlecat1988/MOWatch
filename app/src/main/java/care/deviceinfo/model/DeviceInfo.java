package care.deviceinfo.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import care.userinfo.model.UserInfo;

@DatabaseTable(tableName = "device_info")
public class DeviceInfo {

	/**
	 * ����
	 */
	public static final String ID = "id"; // ��������id
	public static final String DEVICE_ID = "device_id"; // �豸id,��̨���صĲ���
	public static final String DEVICE_NAME = "device_name"; //�豸�ǳ�
	public static final String DEVICE_PHONE = "device_phone"; //�豸�绰����
	public static final String DEVICE_IMEI = "device_imei"; //�豸��imei
	public static final String DEVICE_HEAD_URL = "device_head_url"; //�豸ͷ��
	public static final String DEVICE_SEX = "device_sex"; //�豸�Ա�,"0"��,1Ů
	public static final String DEVICE_AGE = "device_age"; // �豸����(Ĭ��6)
	public static final String DEVICE_BOND_USER = "user_id";    //���豸���û�id
	
	public DeviceInfo(){
    	super();
    }
	
	 // ����id,������
	@DatabaseField(generatedId = true, useGetSet = true, columnName = ID)
	private int id;

	@DatabaseField(useGetSet = true, canBeNull = false, columnName = DEVICE_ID)
	private String deviceId;
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = DEVICE_NAME)
	private String deviceName;
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = DEVICE_PHONE)
	private String devicePhone;
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = DEVICE_IMEI)
	private String deviceImei;
	
	@DatabaseField(useGetSet = true, defaultValue = "6", columnName = DEVICE_AGE)
	private String deviceAge;

	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = DEVICE_SEX)
	private String deviceSex;
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = DEVICE_HEAD_URL)
	private String deviceHeadUrl;
	
	@DatabaseField(foreign = true,foreignAutoRefresh = true,columnName = DEVICE_BOND_USER,foreignColumnName= UserInfo.USER_ID)
	private UserInfo userInfo;
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceImei() {
		return this.deviceImei;
	}

	public void setDeviceImei(String deviceImei) {
		this.deviceImei = deviceImei;
	}
	
	public String getDeviceName() {
		return this.deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDevicePhone() {
		return this.devicePhone;
	}

	public void setDevicePhone(String devicePhone) {
		this.devicePhone = devicePhone;
	}
	
	public String getDeviceAge() {
		return this.deviceAge;
	}

	public void setDeviceAge(String deviceAge) {
		this.deviceAge = deviceAge;
	}
	
	public String getDeviceSex() {
		return this.deviceSex;
	}

	public void setDeviceSex(String deviceSex) {
		this.deviceSex = deviceSex;
	}
	
	public String getDeviceHeadUrl() {
		return this.deviceHeadUrl;
	}

	public void setDeviceHeadUrl(String deviceHeadUrl) {
		this.deviceHeadUrl = deviceHeadUrl;
	}
	
	public UserInfo getUserInfo(){
		return this.userInfo;
	}
	public void setUserInfo(UserInfo userInfo){
		this.userInfo = userInfo;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		return buffer
				.append("id=").append(id)
				.append("deviceId=").append(deviceId)
				.append("deviceImei=").append(deviceImei)
				.append("deviceName=").append(deviceName)
				.append("deviceAge=").append(deviceAge)
				.append("deviceSex=").append(deviceSex)
				.append("deviceHeadUrl=").append(deviceHeadUrl)
				.append("userInfo").append(userInfo)
				.toString();
	}
}
