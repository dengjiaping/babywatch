package com.mobao.watch.bean;

public class LocateInfo {

	String lon; // 经度
	String lat; // 纬度
	String address; // 地点地址
	String saferegionname;// 安全位置地点名称
	// 该定位记录的时间
	String hour;
	String mintue;
    String staytime;
	
	public LocateInfo() {

	}

	public LocateInfo(String lon, String lat, String address,
			String saferegionname, String hour, String mintue,String staytime) {
		super();
		this.lon = lon;
		this.lat = lat;
		this.address = address;
		this.saferegionname = saferegionname;
		this.hour = hour;
		this.mintue = mintue;
		this.staytime = staytime;
	}
	
	public String getStaytime() {
		return staytime;
	}

	public void setStaytime(String staytime) {
		this.staytime = staytime;
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

	public String getSaferegionname() {
		return saferegionname;
	}

	public void setSaferegionname(String saferegionname) {
		this.saferegionname = saferegionname;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMintue() {
		return mintue;
	}

	public void setMintue(String mintue) {
		this.mintue = mintue;
	}

}
