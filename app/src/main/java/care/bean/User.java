package care.bean;

import java.io.Serializable;

public class User implements Serializable{

   

	private String id;             //Î¨Ò»id
	
	private String userHead;   //Í·ÏñµØÖ·
	
	private String userName;  //ÅóÓÑêÇ³Æ
	
	private String nickName; //ÅóÓÑµç»°ºÅÂë
	
	 public String getId() {
			return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
