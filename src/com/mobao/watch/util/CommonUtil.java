package com.mobao.watch.util;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.mb.zjwb1.R;

public class CommonUtil {

	public static final String baseUrl = "http://hedy.ios16.com:8088/api/";

	public static final String CourseAudioDirPath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/tianyu/audio/course/";

	public static final String ChataudioDirPath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/tianyu/audio/chat/";

	/**
	 * 判断字符串中是否全部都是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String getServerStr(Context context, String errorCode) {
		ErroNumberChange errorchanger = new ErroNumberChange(context);
		String head = context.getString(R.string.server_error_because_);
		String error = errorchanger.chang(errorCode);
		String tail = context.getString(R.string._try_again);
		return head + error + tail;
	}

	/**
	 * 递归删除文件或文件夹
	 * 
	 * @param file
	 *            要删除的根目录或文件
	 * @param isDeleteOneSelf
	 */
	public static void DeleteFile(File file, boolean isDeleteOneSelf) {
		DeleteFile(file);
		if (isDeleteOneSelf == false) {
			file.mkdirs();
		}

	}

	private static void DeleteFile(File file) {
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null) {
					return;
				}

				if (childFile.length == 0) {
					file.delete();
					return;
				}

				for (File f : childFile) {
					DeleteFile(f);
				}

				file.delete();
			}
		}
	}

	/**
	 * 
	 * 
	 * @param file
	 *            要判断的根目录
	 */
	/**
	 * 递归判断文件夹中的文件个数
	 * 
	 * @param file
	 *            要判断的根目录
	 * @return 如果是目录不存在，返回-1，如果是文件返回-2，如果是目录就返回里面文件的个数
	 */
	public static int CheckFileCount(File file) {
		if (file.exists() == false) {
			return -1;
		} else {
			if (file.isFile()) {
				return -2;
			}
			if (file.isDirectory()) {
				return CheckFileCount(file, 0);
			} else {
				return -2;
			}
		}

	}

	private static int CheckFileCount(File file, int count) {

		File[] childFile = file.listFiles();

		if (childFile == null) {
			return 0;
		}

		if (childFile.length == 0) {
			return 0;
		}

		for (int i = 0; i < childFile.length; i++) {
			if (childFile[i].isDirectory()) {
				count += CheckFileCount(childFile[i], 0);
			} else {
				count++;
			}
		}

		return count;

	}

	/**
	 * 添加一个notification
	 * 
	 * @param title
	 *            提醒是的标题
	 * @param content
	 *            提醒内容
	 * @param toClass
	 *            点击后要跳到activity，为空时不跳转
	 * @param imei 
	 */
	public static void addNotificaction(Context context, String title,
			String content, Class<?> toClass, int NOTIFY_ID, String imei) {
		int icon = R.drawable.not_qidong_logo;

		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, title, when);

		// 定义下拉通知栏时要展现的内容信息

		Intent notificationIntent = new Intent(context, toClass);
		if(!TextUtils.isEmpty(imei)){
		notificationIntent.putExtra("imei", imei);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, title, content, contentIntent);
		notification.defaults = Notification.DEFAULT_SOUND;// 声音默认
		 notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFY_ID, notification);
	}

}
