package care.bean;

public class Position {
	private String lng;//经度
	private String lat;//纬度
	private String noTransLng;
	private String noTransLat;
	private String time;
	private String ptype;
	private String battery;
	private String timeInterval;
	private String imei;
	private String accuracy;
	
	
	public String getNoTransLng() {
		return noTransLng;
	}

	public void setNoTransLng(String noTransLng) {
		this.noTransLng = noTransLng;
	}

	public String getNoTransLat() {
		return noTransLat;
	}

	public void setNoTransLat(String noTransLat) {
		this.noTransLat = noTransLat;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(String timeInterval) {
		this.timeInterval = timeInterval;
	}

	public Position(){
		
	}
	
	public String getLng() {
		return lng;
	}


	public void setLng(String lng) {
		this.lng = lng;
	}


	public String getLat() {
		return lat;
	}


	public void setLat(String lat) {
		this.lat = lat;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getPtype() {
		return ptype;
	}


	public void setPtype(String ptype) {
		this.ptype = ptype;
	}


	public String getBattery() {
		return battery;
	}


	public void setBattery(String battery) {
		this.battery = battery;
	}
	
}
