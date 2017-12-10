package com.mobao.watch.bean;

import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.util.UserType;

import android.graphics.Bitmap;


/**
 * 一个语音的JavaBean
 * 
 * @author way
 * 
 */
public class ChatAudioEntity {
	private String userId; // null表示自己
	private String date;// 语音日期（时间格式：yyyy-MM-dd HH:mm:ss）
	private boolean isComMsg = true;// 是否为收到的语音
	private int duration = 0;
	private String audioId;
	private UserType userType;
	private boolean isShowTime = true;
	private int sendState = SEND_SUCCESS;
	private int listenState = LISTENING;

	// 发送状态，有发送成功SEND_SUCCESS，发送失败SEND_FAIL和正在发送SEND_ING
	public static final int SEND_SUCCESS = 1;
	public static final int SEND_FAIL = 0;
	public static final int SEND_ING = 2;

	// 点击收听状态，已经收听过
	public static final int LISTENED = 1;
	public static final int LISTENING = 2;
	public static final int NOT_LISTEN = 0;
	
	public ChatAudioEntity(String audioId, UserType userType) {
		this.audioId = audioId;
		this.userId = MbApplication.getGlobalData().getNowuser().getUserid();
		this.userType = userType;

	}

	public ChatAudioEntity(String audioId, String userId, String date,
			boolean isComMeg, UserType userType) {
		this.userId = userId;
		this.audioId = audioId;
		this.date = date;
		this.isComMsg = isComMeg;
		this.userType = userType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration){
		this.duration = duration;
	}
	
	/**
	 * 获取语音类型
	 * 
	 * @return true为收到的语音，false为发出的语音
	 */
	public boolean isComMsg() {
		return this.isComMsg;
	}

	/**
	 * 设置语音类型
	 * 
	 * @param isComMsg
	 *            true为收到的语音，false为发出的语音
	 */
	public void setComMsg(boolean isComMsg) {
		this.isComMsg = isComMsg;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getAudioId() {
		return audioId;
	}

	public boolean isShowTime() {
		return isShowTime;
	}

	public void setShowTime(boolean isShowTime) {
		this.isShowTime = isShowTime;
	}
	
	public int getSendState() {
		return sendState;
	}

	public void setSendState(int sendState) {
		this.sendState = sendState;
	}

	public int getListenState() {
		return listenState;
	}

	public void setListenState(int listenState) {
		this.listenState = listenState;
	}

	public void setAudioId(String audioId) {
		this.audioId = audioId;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
}
