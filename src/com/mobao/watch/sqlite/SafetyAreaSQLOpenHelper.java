package com.mobao.watch.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SafetyAreaSQLOpenHelper extends SQLiteOpenHelper {

	public SafetyAreaSQLOpenHelper(Context context) {
		super(context, "SafetyArea", null, 1);
		// TODO Auto-generated constructor stub
	}

	/*
	 * id 主键 safety_name; 自定义地点名称 safety_address; 地址 safety_range;安全范围 坐标 double
	 * lat; double lon;
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists safety"
				+ "(id integer primary key autoincrement,safety_name varchar,safety_address varchar,safety_range integer,lat double,lon double)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
