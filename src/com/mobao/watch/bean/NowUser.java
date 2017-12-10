package com.mobao.watch.bean;

import java.util.ArrayList;

import android.content.Context;

import com.mobao.watch.util.SPUtil;

public class NowUser {
	private  int havingbaby;
	private  ArrayList<Authlist> authlistarray= new ArrayList<Authlist>();
    
    private  boolean isadmin=false;
    private  Context context;
    
    private String adminphone;
    
    public NowUser(Context context){
    	this.context = context;
    }

	public String getUserid() {
		return SPUtil.getUserid(context);
	}

	public void setUserid(String userid) {
		SPUtil.setUserid(context, userid);
	}

	public void setHavingbaby(int havingbaby) {
		this.havingbaby = havingbaby;
	}

	public ArrayList<Authlist> getAuthlistarray() {
		return authlistarray;
	}

	public void setAuthlistarray(ArrayList<Authlist> authlistarray) {
		this.authlistarray = authlistarray;
	}

	public String getImei() {
		return SPUtil.getImei(context);
	}

	public void setImei(String imei) {
		SPUtil.setImei(context, imei);
	}

	public boolean isIsadmin() {
		return isadmin;
	}

	public void setIsadmin(boolean isadmin) {
		this.isadmin = isadmin;
	}

	public String getAdminphone() {
		return adminphone;
	}

	public void setAdminphone(String adminphone) {
		this.adminphone = adminphone;
	}
    
}
