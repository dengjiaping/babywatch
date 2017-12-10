package com.mobao.watch.bean;

public class InformationCenter {

	private String info_address; // 消息地点
	private String info_name;//事件名称
	private String info_date;// 消息日期
	private String info_state;// 消息状态（已到达/已离开）
	private String info_hour;// 消息小时
	private String info_minute;// 消息分钟
	private String lat;
	private String lon;
	private String type;

	// 无参数构造方法，用于调用内部方法
	public InformationCenter(){
		
	}
	
	//加入经纬度的构造函数
	public InformationCenter(String type,String info_name, String info_date,String info_state, String info_hour, String info_minute, String lon, String lat) {
		this.type=type;
		this.info_name=info_name;
		this.info_date=info_date;
		this.info_hour=info_hour;
		this.info_minute=info_minute;
		this.info_state=info_state;
		this.lat=lat;
		this.lon=lon;
		
	}

	
	public InformationCenter(String info_address, String info_date,
			String info_state, String info_hour, String info_minute) 
	{

		this.info_address=info_address;
		this.info_date=info_date;
		this.info_hour=info_hour;
		this.info_minute=info_minute;
		this.info_state=info_state;
		
	}

	public String getInfo_address() {
		return info_address;
	}

	public void setInfo_address(String info_address) {
		this.info_address = info_address;
	}

	public String getInfo_date() {
		return info_date;
	}

	public void setInfo_date(String info_date) {
		this.info_date = info_date;
	}

	public String getInfo_state() {
		return info_state;
	}

	public void setInfo_state(String info_state) {
		this.info_state = info_state;
	}

	public String getInfo_hour() {
		return info_hour;
	}

	public void setInfo_hour(String info_hour) {
		this.info_hour = info_hour;
	}

	public String getInfo_minute() {
		return info_minute;
	}

	public void setInfo_minute(String info_minute) {
		this.info_minute = info_minute;
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

	public String getInfo_name() {
		return info_name;
	}

	public void setInfo_name(String info_name) {
		this.info_name = info_name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
