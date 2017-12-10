package com.mobao.watch.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.mobao.watch.sqlite.HistoryContentProvider;

/**
 * @author 坤 插入轨迹数据
 */
public class InsertHistoryInfoUtil {

	Context context;

	public InsertHistoryInfoUtil(Context context) {
		this.context = context;
	}

	public void insert() {
		Uri url = HistoryContentProvider.uri_insert;
		ContentValues values = new ContentValues();
		values.put("time", "9:00");
		values.put("location_name", "家");
		values.put("location_address", "广东省广州市天河区天河东75");
		values.put("lat", "23.135339");
		values.put("lon", "113.333427");
		context.getContentResolver().insert(url, values);

		ContentValues values1 = new ContentValues();
		values1.put("time", "10:00");
		values1.put("location_name", "学校");
		values1.put("location_address", "广州市天河区华康小学");
		values1.put("lat", "23.135779");
		values1.put("lon", "113.331382");
		context.getContentResolver().insert(url, values1);

		ContentValues values2 = new ContentValues();
		values2.put("time", "11:00");
		values2.put("location_name", "购物");
		values2.put("location_address", "广东省广州市天河区太古汇");
		values2.put("lat", "23.13418");
		values2.put("lon", "113.332466");
		context.getContentResolver().insert(url, values2);
		Toast.makeText(context, "添加数据成功", 3000).show();
		// //发送数据库内容改变消息
		context.getContentResolver().notifyChange(url, null);
	}

	public void insetHistoryInfo() {

	}
}
