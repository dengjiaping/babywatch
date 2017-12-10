package com.mobao.watch.bean;

public class SosRecordInfo {

	private String sos_address; // 求救地点
	private String sos_date;//求救日期
	private String sos_month;//求救月份
	private String sos_hour;// 求救小时
	private String sos_minute;// 求救分钟
	private String sos_lon;
	private String sos_lat;

	public SosRecordInfo() {

	}

	public SosRecordInfo(String sos_date,String sos_hour, String sos_minute, String sos_address) {
		this.sos_address = sos_address;
		this.sos_hour = sos_hour;
		this.sos_minute = sos_minute;
		this.sos_date=sos_date;
	}
	
	//加了经纬度的 构造方法
	public SosRecordInfo(String sos_date,String sos_month,String sos_hour, String sos_minute, String sos_address,String sos_lon,String sos_lat) {
		this.sos_address = sos_address;
		this.sos_hour = sos_hour;
		this.sos_minute = sos_minute;
		this.sos_date=sos_date;
		this.sos_month=sos_month;
		this.sos_lat=sos_lat;
		this.sos_lon=sos_lon;
		
	}
	
	
	//添加一个月份 构造方法
	public SosRecordInfo(String sos_date,String sos_month,String sos_hour, String sos_minute, String sos_address) {
		this.sos_address = sos_address;
		this.sos_hour = sos_hour;
		this.sos_minute = sos_minute;
		this.sos_date=sos_date;
		this.sos_month=sos_month;
		
	}

	public String getSos_address() {
		return sos_address;
	}

	public void setSos_address(String sos_address) {
		this.sos_address = sos_address;
	}

	public String getSos_hour() {
		return sos_hour;
	}

	public void setSos_hour(String sos_hour) {
		this.sos_hour = sos_hour;
	}

	public String getSos_minute() {
		return sos_minute;
	}

	public void setSos_minute(String sos_minute) {
		this.sos_minute = sos_minute;
	}

	public String getSos_date() {
		return sos_date;
	}

	public void setSos_date(String sos_date) {
		this.sos_date = sos_date;
	}

	public String getSos_month() {
		return sos_month;
	}

	public void setSos_month(String sos_month) {
		this.sos_month = sos_month;
	}

	public String getSos_lon() {
		return sos_lon;
	}

	public void setSos_lon(String sos_lon) {
		this.sos_lon = sos_lon;
	}

	public String getSos_lat() {
		return sos_lat;
	}

	public void setSos_lat(String sos_lat) {
		this.sos_lat = sos_lat;
	}

}
