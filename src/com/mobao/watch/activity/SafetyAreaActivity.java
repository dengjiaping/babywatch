package com.mobao.watch.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.mb.zjwb1.R;
import com.mobao.watch.adapter.SafetyAreaListAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.SafetyAreaInfo;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 安全区域Activity
 */
public class SafetyAreaActivity extends Activity {

	// 返回轨迹Activity
	private RelativeLayout rel_backtohistory;
	private ImageButton btn_backtohistory;
	// 添加地点
	private TextView text_add_safety_area;
	private RelativeLayout rel_add_safety_area;
	// 安全区域列表
	private ListView safetyListView;
	public static ArrayList<SafetyAreaInfo> safety_area_list = new ArrayList<SafetyAreaInfo>();
	// public static ArrayList<String> safety_name_list;//自定义地点名称
	// public static ArrayList<String> safety_address_list;//地址
	public static ArrayList<LatLng> safety_LatLng_list = new ArrayList<LatLng>();// 坐标
	// public static ArrayList<Integer> safety_range_list;//安全范围
	// 列表适配器
	public static SafetyAreaListAdapter adapter;
	private Baby now_baby;
	private String now_babyimei;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.safety_area_activity);
		now_baby = LocationFragment.now_baby;
		now_babyimei = now_baby.getBabyimei();
		// 初始化组件
		initView();
		// 添加监听事件
		setOnClickListener();
		// 获取数据
		initList();
		// 初始化列表适配器
		// inintAdapter();
	}

	private void inintAdapter() {
		adapter = new SafetyAreaListAdapter(this, safety_area_list);
		safetyListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void initList() {
		// safety_area_list = getInfoFromDB();
		if(!CheckNetworkConnectionUtil.isNetworkConnected(this)){
			ToastUtil.show(this, getResources().getString(R.string.networkunusable));
			return;
		}
		new Thread() {
			public void run() {
				final ArrayList<SafetyAreaInfo> area_list = BabyLocateServer
						.getSafetyAreaList(now_babyimei);
				if (area_list != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							safety_area_list = area_list;
							// 初始化列表适配器
							inintAdapter();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(SafetyAreaActivity.this,
									getString(R.string.notsetsafearea), 3000).show();
						}
					});
				}
			}
		}.start();
	}

	private void initView() {
		text_add_safety_area = (TextView) findViewById(R.id.text_add_safety_area);
		rel_add_safety_area = (RelativeLayout) findViewById(R.id.rel_add_safety_area);
		rel_backtohistory = (RelativeLayout) findViewById(R.id.rel_backtohistory);
		btn_backtohistory = (ImageButton) findViewById(R.id.btn_backtohistory);
		safetyListView = (ListView) findViewById(R.id.list_safety_area);
		safetyListView.setDividerHeight(0);
	}

	private void setOnClickListener() {

		rel_backtohistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SafetyAreaActivity.this.finish();
			}
		});
		btn_backtohistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SafetyAreaActivity.this.finish();
			}
		});
		rel_add_safety_area.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SafetyAreaActivity.this,
						AddSafetyAreaActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

	// 获取数据
	public ArrayList<SafetyAreaInfo> getInfoFromDB() {

		ArrayList<SafetyAreaInfo> list = new ArrayList<SafetyAreaInfo>();
		Uri uri = Uri
				.parse("content://com.mobao.watch.sqlite.SafetyAreaContentProvider/query");
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor == null) {
			return null;
		}
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String safety_name = cursor.getString(cursor
					.getColumnIndex("safety_name"));
			String safety_address = cursor.getString(cursor
					.getColumnIndex("safety_address"));
			int safety_range = cursor.getInt(cursor
					.getColumnIndex("safety_range"));
			double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
			double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
			LatLng latlng = new LatLng(lat, lon);
			safety_LatLng_list.add(latlng);
			// SafetyAreaInfo info = new SafetyAreaInfo(id,safety_name,
			// safety_address, lat, lon, safety_range);
			// list.add(info);
		}
		cursor.close();
		return list;
	}
}
