package com.mobao.watch.bean;

public class BabyState {
     String imei;
     String name;
     String address;
     String lon;
     String lat;
     String condition;
     String time;
     
     public BabyState() {
		// TODO Auto-generated constructor stub
	}

	public BabyState(String imei, String name, String address, String lon,
			String lat, String condition, String time) {
		super();
		this.imei = imei;
		this.name = name;
		this.address = address;
		this.lon = lon;
		this.lat = lat;
		this.condition = condition;
		this.time = time;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
     
     
}
