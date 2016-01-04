package care.singlechatinfo.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import care.userinfo.model.UserInfo;

@DatabaseTable(tableName = "single_chat_info")
public class SingleChatInfo {

	/**
	 * ����
	 */
	public static final String ID = "id";   //��������id
	public static final String CHAT_USER_ID = "user_id"; //����������û�id
	public static final String CHAT_TO_USER_ID = "to_user_id"; //���ĸ��û�����Ϣ(��֧�ֵ��ĵ�),���԰�СV��id
	public static final String CHAT_USER_NAME = "chat_name"; //�����ߵ�����,���԰�СV�����ֺ��û�������
	public static final String CHAT_USER_HEAD_URL = "chat_user_head_url";  //������û�ͷ��
	public static final String CHAT_CONTENT = "chat_content";  //���������(������������),ָ���ļ��ĵ�ַ
	public static final String CHAT_SEND_TIME = "chat_send_time"; //����ķ���ʱ��
	public static final String CHAT_SEND_SUCCESS = "chat_send_success"; //����ķ���״̬,0��ʾʧ��,1��ʾ�ɹ�
	public static final String CHAT_TYPE = "chat_type";  //���������,1������,2��ͼƬ,3������
	public static final String CHAT_IS_COMEFROM = "chat_is_comefrom"; //��Ϣ�����ֻ��(0)�����ƶ�(1) 
	public static final String CHAT_IS_READ = "chat_is_read";  //�Ƿ��Ѷ�,"0"��ʾδ��,"1"��ʾ�Ѷ�
	public static final String CHAT_IS_LEN = "chat_is_len";  //���쳤��
	
	public SingleChatInfo(){
		super();
	}
	
	//����id,������
	@DatabaseField(generatedId = true,useGetSet = true,columnName=ID)
	private int id;
	
	//����id,������
	@DatabaseField(foreign = true,foreignAutoRefresh = true,useGetSet = true,columnName=CHAT_USER_ID,foreignColumnName= UserInfo.USER_ID)
	private UserInfo userInfo;
	
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = CHAT_TO_USER_ID)
	private String chatToUserId;
	
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = CHAT_USER_NAME)
	private String chatUserName;
	
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = CHAT_USER_HEAD_URL)
	private String chatUserHeadUrl;
	
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = CHAT_CONTENT)
	private String chatContent;
	
	@DatabaseField(useGetSet = true, canBeNull = false, columnName = CHAT_SEND_TIME)
	private String chatSendTime;
	
	@DatabaseField(useGetSet = true, defaultValue = "1", columnName = CHAT_SEND_SUCCESS)
	private String chatSendSuccess;   //Ĭ���ǳɹ���
	
	@DatabaseField(useGetSet = true, defaultValue = "1", columnName = CHAT_TYPE)
	private String chatType;   //Ĭ��������
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = CHAT_IS_COMEFROM)
	private String chatComeFrom;   //Ĭ�����ֻ��
	
	@DatabaseField(useGetSet = true, defaultValue = "1", columnName = CHAT_IS_READ)
	private String chatIsRead;   //Ĭ����δ��,���������������Ѷ�״̬
	
	@DatabaseField(useGetSet = true, defaultValue = "0", columnName = CHAT_IS_LEN)
	private String chatLen;   //��������
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getChatToUserId() {
		return this.chatToUserId;
	}
	public void setChatToUserId(String chatToUserId) {
		this.chatToUserId = chatToUserId;
	}
	
	public String getChatUserName() {
		return this.chatUserName;
	}
	public void setChatUserName(String chatUserName) {
		this.chatUserName = chatUserName;
	}
	
	public String getChatUserHeadUrl() {
		return this.chatUserHeadUrl;
	}
	public void setChatUserHeadUrl(String chatUserHeadUrl) {
		this.chatUserHeadUrl = chatUserHeadUrl;
	}
	
	public String getChatContent() {
		return this.chatContent;
	}
	public void setChatContent(String chatContent) {
		this.chatContent = chatContent;
	}
	
	public String getChatSendTime() {
		return this.chatSendTime;
	}
	public void setChatSendTime(String chatSendTime) {
		this.chatSendTime = chatSendTime;
	}
	
	public String getChatSendSuccess() {
		return this.chatSendSuccess;
	}
	public void setChatSendSuccess(String chatSendSuccess) {
		this.chatSendSuccess = chatSendSuccess;
	}
	
	public String getChatType() {
		return this.chatType;
	}
	public void setChatType(String chatType) {
		this.chatType = chatType;
	}
	
	public String getChatComeFrom() {
		return this.chatComeFrom;
	}
	public void setChatComeFrom(String chatComeFrom) {
		this.chatComeFrom = chatComeFrom;
	}
	
	public String getChatIsRead() {
		return this.chatIsRead;
	}
	public void setChatIsRead(String chatIsRead) {
		this.chatIsRead = chatIsRead;
	}
	
	public String getChatLen() {
		return this.chatLen;
	}
	public void setChatLen(String chatLen) {
		this.chatLen = chatLen;
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
		return buffer.append("id=").append(id)
				.append("chatToUserId=").append(chatToUserId)
				.append("chatUserName=").append(chatUserName)
				.append("chatUserHeadUrl=").append(chatUserHeadUrl)
				.append("chatContent=").append(chatContent)
				.append("chatSendTime=").append(chatSendTime)
				.append("chatSendSuccess=").append(chatSendSuccess)
				.append("chatType=").append(chatType)
				.append("chatComeFrom=").append(chatComeFrom)
				.append("chatIsRead=").append(chatIsRead)
				.append("chatLen=").append(chatLen)
				.append("userInfo=").append(userInfo)
				.toString();
	}
}
