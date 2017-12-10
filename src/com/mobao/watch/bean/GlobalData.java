package com.mobao.watch.bean;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.facebook.c;

import com.mobao.watch.util.SPUtil;

import android.content.Context;


public class GlobalData {
	private  Context context;
	private  NowUser nowuser;// 当前用户
	private  String activewatch;// 扫描获得的腕表
    private  String nowsilentid;//当前静音时间段
    private  String weeks="1,2,3,4,5,6,7,";//当前选择的重复
    private  String now_silent_num;
    private  boolean isinto=false;//是否进入主界面
    private  int intervalminute=2;//定位间隔
    private  String token="";//需要绑定的token
    private  boolean textForgotPassword=false;
    private  String nowsex;
    private List<Baby> groups=null;
	private Baby nowBaby=null;
	private String babyhead = null;//宝贝头像
	private ArrayList<RelactionShip> relactionship;// 关系列表
	private int babycount;//用户的宝贝数量
	
	
	public ArrayList<RelactionShip> getRelactionship() {
		return relactionship;
	}

	public void setRelactionship(ArrayList<RelactionShip> relactionship) {
		this.relactionship = relactionship;
	}

	public GlobalData(Context context){
		this.context = context;
		if(nowuser == null){
			nowuser= new NowUser(context);
		}		
	}

	public NowUser getNowuser() {
		if(nowuser == null){
			nowuser= new NowUser(context);
		}	
		
		return nowuser;
	}

	public void setNowuser(NowUser nowuser) {
		this.nowuser = nowuser;
	}

	public String getActivewatch() {
		return activewatch;
	}

	public void setActivewatch(String activewatch) {
		this.activewatch = activewatch;
	}

	public String getNowsilentid() {
		return nowsilentid;
	}

	public void setNowsilentid(String nowsilentid) {
		this.nowsilentid = nowsilentid;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public String getNow_silent_num() {
		return now_silent_num;
	}

	public void setNow_silent_num(String now_silent_num) {
		this.now_silent_num = now_silent_num;
	}

	public boolean isAdmin() {
		return SPUtil.getIsAdmin(context);
	}

	public void setAdmin(boolean isAdmin) {
		SPUtil.setIsAdmin(context, isAdmin);
	}

	public boolean isIsinto() {
		return isinto;
	}

	public void setIsinto(boolean isinto) {
		this.isinto = isinto;
	}

	public int getIntervalminute() {
		return intervalminute;
	}

	public void setIntervalminute(int intervalminute) {
		this.intervalminute = intervalminute;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isTextForgotPassword() {
		return textForgotPassword;
	}

	public void setTextForgotPassword(boolean textForgotPassword) {
		this.textForgotPassword = textForgotPassword;
	}

	public String getNowsex() {
		return nowsex;
	}

	public  void setNowsex(String nowsex) {
		this.nowsex = nowsex;
	}

	public List<Baby> getGroups() {
		return groups;
	}

	public void setGroups(List<Baby> groups) {
		this.groups = groups;
	}

	public Baby getNowBaby() {
		return nowBaby;
	}

	public void setNowBaby(Baby nowBaby) {
		this.nowBaby = nowBaby;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getBabyhead() {
		return babyhead;
	}

	public void setBabyhead(String babyhead) {
		this.babyhead = babyhead;
	}

	public int getBabycount() {
		return babycount;
	}

	public void setBabycount(int babycount) {
		this.babycount = babycount;
	}
	
}
