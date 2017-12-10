package com.mobao.watch.bean;

public class AddBabyResult {
	
	private String msg;
	private String phone;
	private String admin;
	
    public AddBabyResult() {
		// TODO Auto-generated constructor stub
	}
	
	public AddBabyResult(String msg, String phone, String admin) {
		super();
		this.msg = msg;
		this.phone = phone;
		this.admin = admin;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}

}
