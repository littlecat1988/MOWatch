package care.clientmanager;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "chatoff_line")
public class ChatOffLineInfo {
	/**
	 * 列名
	 */
	public static final String ID = "id"; // 自增长的id
	public static final String ChatWinPhone="chatwinphone";
	public static final String MessageExInfo="messageexinfo";
	public static final String SendTime = "sendtime"; 
	public static final String DataType = "datatype"; 
    public static final String LinePath ="linepath"; //保存路径
    public static final String COMEFROM ="comefrom"; //来自手机还是亲情号码
    
    public static final String HeadUrl="headurl";   //头像
    public static final String NickName="nickname"; //昵称
    public static final String MessageID="messageid";
	
	public ChatOffLineInfo(){
		super();
	}
	// 主键id,自增长
	@DatabaseField(generatedId = true, useGetSet = true, columnName = ID)
	private int id;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = ChatWinPhone)
	private String chatWinPhone;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = MessageExInfo)
	private String messageExInfo;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = SendTime)
	private String sendTime;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = DataType)
	private int dataType;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = LinePath)
	private String linePath;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = COMEFROM)
	private String comefrom;

	@DatabaseField(useGetSet = true, canBeNull = false, columnName = HeadUrl)
	private String headUrl;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = NickName)
	private String nickName;
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = MessageID)
	private String messageId;
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSendTime() {
		return this.sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public int getDataType() {
		return this.dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public String getLinePath() {
		return this.linePath;
	}
	public void setLinePath(String linePath) {
		this.linePath = linePath;
	}
	
	public String getChatWinPhone() {
		return chatWinPhone;
	}
	public void setChatWinPhone(String chatWinPhone) {
		this.chatWinPhone = chatWinPhone;
	}
	public String getMessageExInfo() {
		return messageExInfo;
	}
	public void setMessageExInfo(String messageExInfo) {
		this.messageExInfo = messageExInfo;
	}
	public String getComefrom() {
		return this.comefrom;
	}
	public void setComefrom(String comefrom) {
		this.comefrom = comefrom;
	}
	
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		return buffer
				.append("id=").append(id)
				.append("ChatWinPhone=").append(chatWinPhone)
				.append("messageExInfo=").append(messageExInfo)
				.append("sendTime=").append(sendTime)
				.append("dataType=").append(dataType)			
				.append("linePath=").append(linePath)
				.append("comefrom=").append(comefrom)
				.append("headurl=").append(headUrl)
				.append("nickname=").append(nickName)
				.toString();
	}
}
