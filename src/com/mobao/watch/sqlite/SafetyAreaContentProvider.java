package com.mobao.watch.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class SafetyAreaContentProvider extends ContentProvider {

	private SafetyAreaSQLOpenHelper openHelper;
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static int INSERT = 1;
	private static int QUERY = 2;
	private static int DELETE = 3;
	private static int UPDATE = 4;

	public static String authority = "com.mobao.watch.sqlite.SafetyAreaContentProvider";
	public static Uri uri_insert = Uri
			.parse("content://com.mobao.watch.sqlite.SafetyAreaContentProvider/insert");
	public static Uri uri_query = Uri
			.parse("content://com.mobao.watch.sqlite.SafetyAreaContentProvider/query");
	public static Uri uri_delete = Uri
			.parse("content://com.mobao.watch.sqlite.SafetyAreaContentProvider/delete");
	public static Uri uri_update = Uri
			.parse("content://com.mobao.watch.sqlite.SafetyAreaContentProvider/update");
	static {
		matcher.addURI(authority, "insert", INSERT);
		matcher.addURI(authority, "query", QUERY);
		matcher.addURI(authority, "delete", DELETE);
		matcher.addURI(authority, "update", UPDATE);
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		if (matcher.match(uri) == DELETE) {
			SQLiteDatabase db = openHelper.getReadableDatabase();
			return db.delete("safety", whereClause, whereArgs);
		}
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (matcher.match(uri) == INSERT) {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			db.insert("safety", null, values);
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		openHelper = new SafetyAreaSQLOpenHelper(getContext());
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri url = SafetyAreaContentProvider.uri_insert;
				ContentValues values = new ContentValues();
				values.put("id", 1);
				values.put("safety_name", "家");
				values.put("safety_address", "广东省广州市天河区天河东75");
				values.put("safety_range", 500);
				values.put("lat", "23.135339");
				values.put("lon", "113.333427");
				getContext().getContentResolver().insert(url, values);

				ContentValues values1 = new ContentValues();
				values1.put("id", 2);
				values1.put("safety_name", "学校");
				values1.put("safety_address", "广州市天河区华康小学");
				values1.put("safety_range", 300);
				values1.put("lat", "23.135779");
				values1.put("lon", "113.331382");
				getContext().getContentResolver().insert(url, values1);

				ContentValues values2 = new ContentValues();
				values2.put("id", 3);
				values2.put("safety_name", "购物");
				values2.put("safety_address", "广东省广州市天河区太古汇");
				values2.put("safety_range", 400);
				values2.put("lat", "23.13418");
				values2.put("lon", "113.332466");
				getContext().getContentResolver().insert(url, values2);
			}
		}).start();

		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		if (matcher.match(uri) == QUERY) {
			SQLiteDatabase db = openHelper.getReadableDatabase();
			return db.query("safety", columns, selection, selectionArgs, null,
					null, orderBy);
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause,
			String[] whereArgs) {
		if (matcher.match(uri) == UPDATE) {
			SQLiteDatabase db = openHelper.getReadableDatabase();
			return db.update("safety", values, whereClause, whereArgs);
		}
		return 0;
	}

}
