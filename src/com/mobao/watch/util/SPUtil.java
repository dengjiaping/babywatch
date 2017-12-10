package com.mobao.watch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtil {

	private static final String NOW_USER = "NOW_USER";
	private static final String USER_ID = "USER_ID";
	private static final String IMEI = "IMEI";
	private static final String ISADMIN = "ISADMIN";

	public static String getUserid(Context context) {
		SharedPreferences sp = context.getSharedPreferences(NOW_USER,
				Context.MODE_PRIVATE);
		return sp.getString(USER_ID, "uninitUserid");
	}

	public static void setUserid(Context context, String userid) {
		SharedPreferences sp = context.getSharedPreferences(NOW_USER,
				Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putString(USER_ID, userid);
		ed.commit();
	}

	public static String getImei(Context context) {
		SharedPreferences sp = context.getSharedPreferences(NOW_USER,
				Context.MODE_PRIVATE);
		return sp.getString(IMEI, "uninitImei");
	}

	public static void setImei(Context context, String imei) {
		SharedPreferences sp = context.getSharedPreferences(NOW_USER,
				Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putString(IMEI, imei);
		ed.commit();
	}

	public static boolean getIsAdmin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(NOW_USER,
				Context.MODE_PRIVATE);
		return sp.getBoolean(ISADMIN, false);
	}

	public static void setIsAdmin(Context context, boolean isAdmin) {
		SharedPreferences sp = context.getSharedPreferences(NOW_USER,
				Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putBoolean(ISADMIN, isAdmin);
		ed.commit();
	}

}
