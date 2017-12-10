package com.mobao.watch.bean;

import com.amap.api.maps2d.model.LatLng;

public class BabyNowLocationInfo {

	LatLng latlng;
	String time;
	String address;
	String state;
	
	public BabyNowLocationInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	public BabyNowLocationInfo(LatLng latlng, String time, String address,
			String state) {
		super();
		this.latlng = latlng;
		this.time = time;
		this.address = address;
		this.state = state;
	}


	public LatLng getLatlng() {
		return latlng;
	}
	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
}
