package com.mobao.watch.bean;

public class SafetyArea {

	String id;
	String imei;
	String babyname;
	String safename;
	String address;
	String radius;
	String lat;
	String lon;

	public SafetyArea() {

	}

	public SafetyArea(String imei, String name, String address, String radius,
			String lat, String lon) {
		super();
		this.imei = imei;
		this.safename = name;
		this.address = address;
		this.radius = radius;
		this.lat = lat;
		this.lon = lon;
	}

	public SafetyArea(String id, String imei, String babyname, String safename,
			String address, String radius, String lat, String lon) {
		super();
		this.id = id;
		this.imei = imei;
		this.babyname = babyname;
		this.safename = safename;
		this.address = address;
		this.radius = radius;
		this.lat = lat;
		this.lon = lon;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getName() {
		return safename;
	}

	public void setName(String name) {
		this.safename = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBabyname() {
		return babyname;
	}

	public void setBabyname(String babyname) {
		this.babyname = babyname;
	}

	public String getSafename() {
		return safename;
	}

	public void setSafename(String safename) {
		this.safename = safename;
	}
	
	
}
