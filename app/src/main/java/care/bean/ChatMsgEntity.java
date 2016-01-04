
package care.bean;

/**
 * @ClassName ChatMsgEntity
 * 
 * @description  Chat message Object
 *               
 */
public class ChatMsgEntity {
	
	private String mHeadURI;
	private String mName;
    private String mDate, mSendTime;
    private String mText;
    private String mVoiceTimeLen;
    private boolean mIsComMeg = true;
    private String mDeviceID;
    private String mUserID;
    private String mSingleChat;
    private String mContentType;
    
	/**
	 * fun name: ChatMsgEntity
	 * @description construction method 
	 */
    public ChatMsgEntity() { }

	/**
	 * fun name: ChatMsgEntity
	 * 
	 * @description construction method 
	 * @param  name: message owner
	 * 		   date: message date
	 * 		   text: message content
	 * 		   isComMsg: whether the message is from server 
	 */
    public ChatMsgEntity(String name, String date, String text, boolean isComMsg) {
        super();
        this.mName = name;
        this.mDate = date;
        this.mText = text;
        this.mIsComMeg = isComMsg;
    }
    
    public String getHeadURI() {
		return mHeadURI;
	}

	public void setHeadURI(String headURI) {
		this.mHeadURI = headURI;
	}
	
    public String getVoiceTimeLen() {
		return mVoiceTimeLen;
	}
    
	public void setVoiceTimeLen(String time) {
		this.mVoiceTimeLen = time;
	}

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getSendTime() {
        return mSendTime;
    }

    public void setSendTime(String sendTime) {
        this.mSendTime = sendTime;
    }
    
    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public boolean getMsgType() {
        return mIsComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	mIsComMeg = isComMsg;
    }

    public String getDeviceID() {
		return mDeviceID;
	}

	public void setDeviceID(String deviceID) {
		this.mDeviceID = deviceID;
	}
	
    public String getUserID() {
		return mUserID;
	}

	public void setUserID(String uID) {
		this.mUserID = uID;
	}
	
    public String getSingleChat() {
		return mSingleChat;
	}

	public void setSingleChat(String singleChat) {
		this.mSingleChat = singleChat;
	}
	
    public String getContentType() {
		return mContentType;
	}

	public void setContentType(String type) {
		this.mContentType = type;
	}
}
