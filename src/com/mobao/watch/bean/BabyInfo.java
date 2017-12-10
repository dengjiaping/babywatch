package com.mobao.watch.bean;

import java.util.ArrayList;

public class BabyInfo {

	String name;
	String star;
	String step;
	String distance;
	ArrayList<LocateInfo> locateArray;

	public BabyInfo() {

	}

	public BabyInfo(String name, String star, String step, String distance,
			ArrayList<LocateInfo> locateArray) {
		super();
		this.name = name;
		this.star = star;
		this.step = step;
		this.distance = distance;
		this.locateArray = locateArray;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public ArrayList<LocateInfo> getLocateArray() {
		return locateArray;
	}

	public void setLocateArray(ArrayList<LocateInfo> locateArray) {
		this.locateArray = locateArray;
	}

}
