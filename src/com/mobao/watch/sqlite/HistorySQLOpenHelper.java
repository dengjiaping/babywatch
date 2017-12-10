package com.mobao.watch.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 历史轨迹数据库
 */
public class HistorySQLOpenHelper extends SQLiteOpenHelper {

	public HistorySQLOpenHelper(Context context) {
		super(context, "HistoryLocation", null, 1);
	}

	/*
	 * time 时间点 location_name 自定义地点名称 location_address 详细地址 lat lon 坐标
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists history"
				+ "(time varchar(8),location_name varchar(10),location_address varchar,lat double,lon double)";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
