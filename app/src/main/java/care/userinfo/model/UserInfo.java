package care.userinfo.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_info")
public class UserInfo {

	/**
	 * 列名
	 */
	public static final String ID = "id"; // 自增长的id
	public static final String USER_ID = "user_id"; // 用户唯一ID(后台返回的)
	public static final String USER_PASSWORD = "user_password"; // 用户密码
	public static final String USER_NAME = "user_name"; // 用户登陆账号(电话)
	public static final String USER_NICK_NAME = "user_nick_name"; //用户昵称
	public static final String USER_HEAD_URL = "user_header_url"; // 用户头像
	public static final String USER_SEX = "user_sex"; // 用户性别,"0"默认是男,"1"默认是女
	public static final String USER_BIRTHDAY = "user_birthday"; //用户出生年月日
	public static final String USER_HEIGHT = "user_height"; //用户身高
	public static final String USER_WEIGHT = "user_weight";  //用户体重
	public static final String USER_LOGIN = "user_login"; // 是否自动登录,0表示未记住,1表示记住

	public UserInfo() {
		super();
	}

	// 主键id,自增长
	@DatabaseField(generatedId = true, useGetSet = true, columnName = ID)
	private int id;

	@DatabaseField(useGetSet = true, canBeNull = false, columnName = USER_ID)
	private String userId;

	@DatabaseField(useGetSet = true, canBeNull = false, columnName = USER_PASSWORD)
	private String userPassword;

	@DatabaseField(useGetSet = true, canBeNull = false, columnName = USER_NAME)
	private String userName;  //第一次默认手机号

	@DatabaseField(useGetSet = true, canBeNull = false, columnName = USER_NICK_NAME)
	private String userNickName;  //第一次默认手机号
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = USER_HEAD_URL)
	private String userHeadUrl;

	@DatabaseField(useGetSet = true, defaultValue = "1", columnName = USER_SEX)
	private String userSex;

	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = USER_BIRTHDAY)
	private String userBirthday;

	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = USER_LOGIN)
	private String userLogin;

	@DatabaseField(useGetSet = true, defaultValue = "170", columnName = USER_HEIGHT)
	private String userHeight;
	
	@DatabaseField(useGetSet = true, defaultValue = "170", columnName = USER_WEIGHT)
	private String userWeight;
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNickName() {
		return this.userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	
	public String getUserSex() {
		return this.userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getUserHeadUrl() {
		return this.userHeadUrl;
	}

	public void setUserHeadUrl(String userHeadUrl) {
		this.userHeadUrl = userHeadUrl;
	}

	public String getUserBirthday() {
		return this.userBirthday;
	}

	public void setUserBirthday(String userBirthday) {
		this.userBirthday = userBirthday;
	}

	public String getUserLogin() {
		return this.userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	
	public String getUserHeight() {
		return this.userHeight;
	}

	public void setUserHeight(String userHeight) {
		this.userHeight = userHeight;
	}
	
	public String getUserWeight() {
		return this.userWeight;
	}

	public void setUserWeight(String userWeight) {
		this.userWeight = userWeight;
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		return buffer
				.append("id=").append(id)
				.append("userId=").append(userId)
				.append("userPassword=").append(userPassword)
				.append("userName=").append(userName)
				.append("userNickName=").append(userNickName)			
				.append("userSex=").append(userSex)
				.append("userHeadUrl=").append(userHeadUrl)
				.append("userRelationWithDevice=")
				.append(userLogin)
				.append("userWeight=").append(userWeight)
				.append("userHeight=").append(userHeight)
				.toString();
	}
}
