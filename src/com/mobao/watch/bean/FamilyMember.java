package com.mobao.watch.bean;

public class FamilyMember {

	private String userid;
	private String phone;
	private String imei;
	private String relate;
	private String value;
	private String admin;
	
	public FamilyMember() {
		// TODO Auto-generated constructor stub
	}

	public FamilyMember(String userid, String phone, String imei,
			String relate, String admin,String value) {
		super();
		this.userid = userid;
		this.phone = phone;
		this.imei = imei;
		this.relate = relate;
		this.admin = admin;
		this.value = value;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
