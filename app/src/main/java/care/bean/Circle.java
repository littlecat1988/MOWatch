package care.bean;

import java.io.Serializable;

public class Circle implements Serializable{
	private String lng;//缁忓害
	private String lat;//绾害
	private String name;//鍥存爮鍚嶇О
	private String addr;//鍥存爮鍦板潃
	private String radius;//鍗婂緞
	private String imei;//鍥存爮鎵�睘鐨勮〃
	private String id;//鍥存爮id
	
	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}


	public Circle(){
		
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}
	
}
