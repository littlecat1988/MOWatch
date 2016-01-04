package care.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatInfoBean implements Parcelable{

	private int id;            //��¼ÿ����¼��id
	
	private String chatHeadUrl; // �û�ͷ��
	 /**
     * 发送状态：0/发送中,1/发送成功,-1/发送失败
     */
	private String chatIsRead; 

	private String chatComeFrom; // 1是亲情号码，0是自己

	private String chatSendTime; // ����ʱ��(�����ֻ�˺��ƶ�)

	private String chatUserName; // ������û���

	private String chatType; // ��������,0��ʾ����,1��ʾ����

	private String chatContent; // ��������,���ֺ�����(ָ�򲥷ŵ�ַ)

	private String chatLen; // �����ʱ��(�������)

	private String chatUserId; // id(�����û�id���豸id,���ڵ�����������Ϣ����)

	private String chatBelongType;  //��������,1��ʾ����,2��ʾȺ��
	
	/**
	 * lk添加的
	 */
	private int dataType;		
	private Object data;		
	private String mainTokenID;
	private String srcTokenID;
	private String sendTime;
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	public String getChatHeadUrl() {
		return this.chatHeadUrl;
	}

	public void setChatHeadUrl(String chatHeadUrl) {
		this.chatHeadUrl = chatHeadUrl;
	}

	public String getChatIsRead() {
		return this.chatIsRead;
	}

	public void setChatIsRead(String chatIsRead) {
		this.chatIsRead = chatIsRead;
	}

	public String getChatComeFrom() {
		return this.chatComeFrom;
	}

	public void setChatComeFrom(String chatComeFrom) {
		this.chatComeFrom = chatComeFrom;
	}

	public String getChatSendTime() {
		return this.chatSendTime;
	}

	public void setChatSendTime(String chatSendTime) {
		this.chatSendTime = chatSendTime;
	}

	public String getChatUserName() {
		return this.chatUserName;
	}

	public void setChatUserName(String chatUserName) {
		this.chatUserName = chatUserName;
	}

	public String getChatType() {
		return this.chatType;
	}

	public void setChatType(String chatType) {
		this.chatType = chatType;
	}

	public String getChatContent() {
		return this.chatContent;
	}

	public void setChatContent(String chatContent) {
		this.chatContent = chatContent;
	}

	public String getChatLen() {
		return this.chatLen;
	}

	public void setChatLen(String chatLen) {
		this.chatLen = chatLen;
	}

	public String getChatUserId() {
		return this.chatUserId;
	}

	public void setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
	}

	public String getChatBelongType() {
		return this.chatBelongType;
	}

	public void setChatBelongType(String chatBelongType) {
		this.chatBelongType = chatBelongType;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		return sb
				.append("id=").append(id)
				.append("chatHeadUrl=").append(chatHeadUrl)
				.append("chatUserName=").append(chatUserName)
				.append("chatComeFrom=").append(chatComeFrom)
				.append("chatIsRead=").append(chatIsRead)
				.append("chatSendTime=").append(chatSendTime)
				.append("chatType=").append(chatType).append("chatContent=")
				.append(chatContent).append("chatLen=").append(chatLen)
				.append("chatUserId=").append(chatUserId)
				.append("chatBelongType=").append(chatBelongType)
				.toString();
	}
	//---------------------------------------------------------------------------------------
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getMainTokenID() {
		return mainTokenID;
	}
	public void setMainTokenID(String mainTokenID) {
		this.mainTokenID = mainTokenID;
	}
	public String getSrcTokenID() {
		return srcTokenID;
	}
	public void setSrcTokenID(String srcTokenID) {
		this.srcTokenID = srcTokenID;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	//----------------------------------------------------------------------------------------
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(chatHeadUrl);
		dest.writeString(chatIsRead);
		dest.writeString(chatComeFrom);
		dest.writeString(chatSendTime);
		dest.writeString(chatUserName);
		dest.writeString(chatType);
		dest.writeString(chatContent);
		dest.writeString(chatLen);
		dest.writeString(chatUserId);
		dest.writeString(chatBelongType);
		
		
		dest.writeInt(dataType);
		dest.writeValue(data);
		dest.writeString(mainTokenID);
		dest.writeString(sendTime);
		dest.writeString(srcTokenID);
	}
	public static final Creator<ChatInfoBean> CREATOR = new Creator<ChatInfoBean>(){

		@Override
		public ChatInfoBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			ChatInfoBean chatInfoBean = new ChatInfoBean();
			chatInfoBean.setChatHeadUrl(source.readString());
			chatInfoBean.setChatIsRead(source.readString());
			chatInfoBean.setChatComeFrom(source.readString());
			chatInfoBean.setChatSendTime(source.readString());
			chatInfoBean.setChatUserName(source.readString());
			chatInfoBean.setChatType(source.readString());
			chatInfoBean.setChatContent(source.readString());		
			chatInfoBean.setChatLen(source.readString());		
			chatInfoBean.setChatUserId(source.readString());
			chatInfoBean.setChatBelongType(source.readString());
			
			
			chatInfoBean.setDataType(source.readInt());
			chatInfoBean.setData(source.readString());
			chatInfoBean.setMainTokenID(source.readString());
			chatInfoBean.setSrcTokenID(source.readString());
			chatInfoBean.setSendTime(source.readString());
			return chatInfoBean;
		}

		@Override
		public ChatInfoBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ChatInfoBean[size];
		}
		
	};
}
