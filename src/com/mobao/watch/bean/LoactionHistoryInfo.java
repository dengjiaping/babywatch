package com.mobao.watch.bean;


public class LoactionHistoryInfo {
	private String history_time; // 时间
	private String location_name; // 地点名称
	private String location_address; // 地点地址
	private String hour;
	private String minute;
	private String staytime;
	// 地点坐标
	private double lat;
	private double lon;

	public LoactionHistoryInfo() {

	}

	public LoactionHistoryInfo(String history_time, String location_name,
			String location_address, double lat, double lon,String staytime) {
		super();
		this.history_time = history_time;
		this.location_name = location_name;
		this.location_address = location_address;
		this.lat = lat;
		this.lon = lon;
		this.staytime = staytime;
	}

	public LoactionHistoryInfo(String history_time, String location_name,
			String location_address, String hour, String minute, double lat,
			double lon,String staytime) {
		super();
		this.history_time = history_time;
		this.location_name = location_name;
		this.location_address = location_address;
		this.hour = hour;
		this.minute = minute;
		this.lat = lat;
		this.lon = lon;
		this.staytime = staytime;
	}

	public String getHistory_time() {
		return history_time;
	}

	public void setHistory_time(String history_time) {
		this.history_time = history_time;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getLocation_address() {
		return location_address;
	}

	public void setLocation_address(String location_address) {
		this.location_address = location_address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getStaytime() {
		return staytime;
	}

	public void setStaytime(String staytime) {
		this.staytime = staytime;
	}

	
}
