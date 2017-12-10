package com.mobao.watch.bean;

public class RelactionShip {

	String relate;
	String value;
	
	public RelactionShip(){
		
	}

	public RelactionShip(String relate, String value) {
		super();
		this.relate = relate;
		this.value = value;
	}

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
