package care.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class OffLineCountBean implements Parcelable{

	private String messageID;
	private String chatWinPhone;
 	
	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getChatWinPhone() {
		return chatWinPhone;
	}

	public void setChatWinPhone(String chatWinPhone) {
		this.chatWinPhone = chatWinPhone;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(messageID);
		dest.writeString(chatWinPhone);
	}
	
}
