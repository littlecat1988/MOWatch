package com.mina;

import java.io.Serializable;
import java.util.Date;

public class BaseMessage implements Serializable {

    private static final long serialVersionUID = 1849925923239151278L;
    private int dataType;		
    private Object data;		
    private String messageExInfo;
    private Date serverSendTime;
    private String targPhoneNum;
    
    public String getTargPhoneNum() {
        return targPhoneNum;
    }
    public void setTargPhoneNum(String targPhoneNum) {
        this.targPhoneNum = targPhoneNum;
    }
    public Date getServerSendTime() {
        return serverSendTime;
    }
    public void setServerSendTime(Date serverSendTime) {
        this.serverSendTime = serverSendTime;
    }
    public BaseMessage(int dataType,Object data){
        this.dataType = dataType;
        this.data = data;
    }
    public BaseMessage(){
    	  
    }
    
    public String getMessageExInfo() {
        return messageExInfo;
    }
    public void setMessageExInfo(String messageExInfo) {
        this.messageExInfo = messageExInfo;
    }
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
}
