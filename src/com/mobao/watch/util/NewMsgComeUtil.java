package com.mobao.watch.util;

import android.content.Context;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BabyFragmentActivity;

public class NewMsgComeUtil {

	public final static int NOTIFY_ID = 1;
	private static int newCount = 0;
	public static boolean isAtChatInterface = false;

	public static void addNewMsgNotification(Context context) {
		if (isAtChatInterface == false && newCount > 0) {
			CommonUtil.addNotificaction(context,
					context.getString(R.string.baby_watch),
					context.getString(R.string.has_new_audio_msg),
					BabyFragmentActivity.class, NOTIFY_ID,"");
			BabyFragmentActivity.isEnterFromNotifytion = true; // 通知主界面变成可见时跳到聊天界面
		} else {
			newCount = 0;
		}
	}

	public static void ShowBottomTip() {
		if (isAtChatInterface == false && newCount > 0) {
			BabyFragmentActivity.showChatTip(newCount);
		} else {
			newCount = 0;
		}
	}

	public static void enterChatInterface() {
		isAtChatInterface = true;
		newCount = 0;
	}

	public static void outChatInterface() {
		isAtChatInterface = false;
		newCount = 0;
	}

	public static void addaNewCount() {
		newCount++;
	}

	public static void setNewCount(int count) {
		newCount = count;
	}
}
