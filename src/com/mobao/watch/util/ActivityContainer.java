package com.mobao.watch.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/** 管理打开了的activity，可在oncreate方法里添加Activity
 * @author 幽灵登录者
 *
 */
public class ActivityContainer extends Application {

	private List<Activity> activityList = new ArrayList<Activity>();
	private static ActivityContainer instance;
	private List<String> activitySignList = new ArrayList<String>();

	private ActivityContainer() {
	}

	/** 单例模式中获取唯一的MyApplication实例
	 * @return
	 */
	public static ActivityContainer getInstance() {
		if (null == instance) {
			instance = new ActivityContainer();
		}
		return instance;
	}
	
	/**
	 *  清除容器中的所有Activity
	 */
	public void cleanFinsher(){
		activityList = new ArrayList<Activity>();
		activitySignList = new ArrayList<String>();
	}

	/** 添加Activity到容器中
	 * @param activity 
	 * @param activitySign activity标记，可用此标记关闭对应的activity,一般为activity的类名
	 */
	public void addActivity(Activity activity, String activitySign) {
		
		//判断activity是否存在
		int index = activityList.indexOf(activity);
		if(index != -1){
			return;
		}
		
		activityList.add(activity);
		activitySignList.add(activitySign);
	}
	
	/**  关闭对应标记的activity
	 * @param activitySign
	 */
	public void finshActivity(String activitySign){
		int index = activitySignList.indexOf(activitySign);
		if(index == -1){
			return;
		}
		activityList.get(index).finish();
		activityList.remove(index);
		activitySignList.remove(index);
	}

	/**
	 * 遍历所有Activity并finish
	 */
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}

}
