package com.mobao.watch.bean;

public class SafetyAreaInfo {

	private String id; // 主键
	private String safety_name; // 自定义地点名称
	private String safety_address; // 地址
	// 坐标
	private String lat;
	private String lon;
	private String safety_range;// 安全范围

	public SafetyAreaInfo() {

	}

	public SafetyAreaInfo(String id, String safety_name, String safety_address,
			String lat, String lon, String safety_range) {
		super();
		this.id = id;
		this.safety_name = safety_name;
		this.safety_address = safety_address;
		this.lat = lat;
		this.lon = lon;
		this.safety_range = safety_range;
	}

	public SafetyAreaInfo(String safety_name, String safety_address,
			String lat, String lon, String safety_range) {
		super();
		this.safety_name = safety_name;
		this.safety_address = safety_address;
		this.lat = lat;
		this.lon = lon;
		this.safety_range = safety_range;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSafety_name() {
		return safety_name;
	}

	public void setSafety_name(String safety_name) {
		this.safety_name = safety_name;
	}

	public String getSafety_address() {
		return safety_address;
	}

	public void setSafety_address(String safety_address) {
		this.safety_address = safety_address;
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

	public String getSafety_range() {
		return safety_range;
	}

	public void setSafety_range(String safety_range) {
		this.safety_range = safety_range;
	}

}
