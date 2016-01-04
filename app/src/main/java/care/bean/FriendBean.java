package care.bean;
import android.os.Parcel;
import android.os.Parcelable;

public class FriendBean implements Parcelable{

	private int id;             //唯一id

	private String friendUrl;   //头像地址

	private String friendName;  //朋友昵称

	private String friendPhone; //朋友电话号码

	private String friendSex; //朋友性别
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}

	public String getFriendUrl() {
		return this.friendUrl;
	}
	public void setFriendUrl(String friendUrl) {
		this.friendUrl = friendUrl;
	}

	public String getFriendName() {
		return this.friendName;
	}
	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getFriendPhone() {
		return this.friendPhone;
	}
	public void setFriendPhone(String friendPhone) {
		this.friendPhone = friendPhone;
	}

	public String getFriendSex() {
		return this.friendSex;
	}
	public void setFriendSex(String friendSex) {
		this.friendSex = friendSex;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		return sb
				.append("id").append(id)
				.append("friendUrl").append(friendUrl)
				.append("friendName").append(friendName)
				.append("friendPhone").append(friendPhone)
				.append("friendSex").append(friendSex)
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
		dest.writeString(friendUrl);
		dest.writeString(friendName);
		dest.writeString(friendPhone);
		dest.writeString(friendSex);
	}
	public static final Creator<FriendBean> CREATOR = new Creator<FriendBean>(){

		public FriendBean createFromParcel(Parcel source) {
			FriendBean friendBean = new FriendBean();

			friendBean.setId(source.readInt());
			friendBean.setFriendUrl(source.readString());
			friendBean.setFriendName(source.readString());
			friendBean.setFriendPhone(source.readString());
			friendBean.setFriendSex(source.readString());

			return friendBean;
		}

		@Override
		public FriendBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FriendBean[size];
		}
	};
}
