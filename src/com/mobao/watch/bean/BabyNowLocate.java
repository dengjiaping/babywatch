package com.mobao.watch.bean;

public class BabyNowLocate {
	String imei;
	String name;
	String lon;
	String lat;
	String address;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public BabyNowLocate() {
		// TODO Auto-generated constructor stub
	}
	
	public BabyNowLocate(String imei, String name, String lon, String lat,
			String address) {
		super();
		this.imei = imei;
		this.name = name;
		this.lon = lon;
		this.lat = lat;
		this.address = address;
	}
	
}
