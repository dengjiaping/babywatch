package com.mobao.watch.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mb.zjwb1.R;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.adapter.HistoryLogListAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.LocationLog;
import com.mobao.watch.datespinner.WheelMain;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.DateTimePickDialogUtil;
import com.mobao.watch.util.DialogUtil;
import com.mobao.watch.util.SetTimePickerDialog;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

public class GoogleLocationLogActivity extends FragmentActivity {

	private RelativeLayout rel_backtoloaction;
	private TextView select_baby_txt;

	private PopupWindow window; // popupWindow
	private ListView lv_group; // popupWindow子view
	private List<Baby> babyGroups;// 宝贝列表
	private RelativeLayout rel; // 顶部操作栏
	private RelativeLayout rel_center;// 中间操作栏
	private GroupAdapter groupAdapter;// 顶部选择框adapter
	// 时间下拉选择框
	private RelativeLayout spinner;
	private Dialog dialog;
	public static String now_babyimei = null;// 当前宝贝id
	private Baby now_baby = null;
	// 当前选择的时间
	private String selected_starttiem = null;
	private String selected_endtiem = null;
	WheelMain wheelMain;
	TextView txttime;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	// 当前时间
	private String date;

	// 轨迹listView
	private ListView listView;
	public static HistoryLogListAdapter historyadapter;// 轨迹Adapter
	private ArrayList<LocationLog> loactionlogList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_location_log_activity);
		// 云测崩溃分析
		TestinAgent.init(this);

		initView();

		// 获取全部宝贝
		babyGroups = LocationFragment.public_groups;
		now_baby = LocationFragment.now_baby;

		if (now_baby != null) {
			now_babyimei = now_baby.getBabyimei();
			// 设置selectBabyText
			select_baby_txt.setText(now_baby.getBabyname());
			babyGroups.remove(now_baby);
		}
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		date = sDateFormat.format(new java.util.Date());
		selected_starttiem = date + " 00:00:00";
		selected_endtiem = date + " 23:59:59";
		getInfo();
		IntentFilter filter = new IntentFilter();
		filter.addAction(DateTimePickDialogUtil.SELECTION_TIME_ACTION);
		registerReceiver(broadcastReceiver, filter);
	}

	private void initView() {
		rel_backtoloaction = (RelativeLayout) findViewById(R.id.rel_backtoloaction);
		select_baby_txt = (TextView) findViewById(R.id.select_baby_txt);
		rel = (RelativeLayout) findViewById(R.id.top_rel);
		listView = (ListView) findViewById(R.id.list_address);
		listView = (ListView) findViewById(R.id.list_address);
		listView.setDividerHeight(0);

		spinner = (RelativeLayout) findViewById(R.id.layout_spinnerdate);
		txttime = (TextView) findViewById(R.id.text_spinnerdate);
		Calendar calendar = Calendar.getInstance();
		txttime.setText(calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "");
		setOnClickListener();

	}

	private void setOnClickListener() {

		rel_backtoloaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GoogleLocationLogActivity.this.finish();
			}
		});

		// select_baby_txt.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // 初始化PopupWindow
		// initPopupWindow();
		// }
		// });

		spinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						GoogleLocationLogActivity.this, "");
				dateTimePicKDialog.dateTimePicKDialog(txttime);
				// // TODO Auto-generated method stub
				// String time = txttime.getText().toString();
				// final SetTimePickerDialog timepicker = new
				// SetTimePickerDialog(
				// GoogleLocationLogActivity.this,
				// R.style.chatfragment_call_dialog_style,
				// GoogleLocationLogActivity.this, time);
				// timepicker.setBtnOkonclik(new OnClickListener() {
				//
				// @Override
				// public void onClick(View arg0) {
				// // TODO Auto-generated method stub
				// txttime.setText(timepicker.getTime());
				// selected_starttiem = timepicker.getTime() + " 00:00:00";
				// selected_endtiem = timepicker.getTime() + " 23:59:59";
				// getInfo();
				// timepicker.dismiss();
				//
				// }
				// });
				//
				// timepicker.setBtnNoonclik(new OnClickListener() {
				//
				// @Override
				// public void onClick(View arg0) {
				// // TODO Auto-generated method stub
				// timepicker.dismiss();
				// }
				// });
				// timepicker.show();
			}
		});
	}

	// 获取数据
	public void getInfo() {
		final DialogUtil du = new DialogUtil(GoogleLocationLogActivity.this,
				getString(R.string.get_date_ing));
		du.showDialog();
		// 获取位置信息
		new Thread() {
			public void run() {
				final ArrayList<LocationLog> loactionList = BabyLocateServer
						.getBabyHistoryLog(now_babyimei, selected_starttiem,
								selected_endtiem);
				if (loactionList != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							loactionlogList = loactionList;
							historyadapter = new HistoryLogListAdapter(
									GoogleLocationLogActivity.this,
									loactionlogList);
							listView.setAdapter(historyadapter);
							historyadapter.notifyDataSetChanged();
							du.dismissDialog();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							ToastUtil.show(GoogleLocationLogActivity.this,
									getString(R.string.nowdaynodata));
							loactionlogList = null;
							historyadapter = new HistoryLogListAdapter(
									GoogleLocationLogActivity.this,
									loactionlogList);
							listView.setAdapter(historyadapter);
							historyadapter.notifyDataSetChanged();
							du.dismissDialog();
						}
					});
				}
			}
		}.start();
	}

	protected void initPopupWindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.group_list, null);

		lv_group = (ListView) view.findViewById(R.id.lvGroup);
		// 加载数据

		groupAdapter = new GroupAdapter(this, babyGroups);
		lv_group.setAdapter(groupAdapter);

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		window = new PopupWindow(view, 300,
				WindowManager.LayoutParams.WRAP_CONTENT);

		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);

		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.AnimationFade);
		// 在底部显示
		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int y = rel.getHeight() + statusBarHeight;
		window.showAtLocation(spinner, Gravity.TOP, 0, y);

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(GoogleLocationLogActivity.this)) {
					ToastUtil.show(GoogleLocationLogActivity.this,
							getResources().getString(R.string.networkunusable));
					window.dismiss();
					return;
				}
				Baby cur_baby = babyGroups.get(position);
				String lasttext = (String) select_baby_txt.getText();
				select_baby_txt.setText(babyGroups.get(position).getBabyname());
				now_babyimei = babyGroups.get(position).getBabyimei();
				LocationFragment.now_baby = cur_baby;
				LocationFragment.now_babyimei = now_babyimei;
				babyGroups.remove(position);
				babyGroups.add(now_baby);
				now_baby = cur_baby;
				groupAdapter.notifyDataSetChanged();
				getInfo();
				if (window != null) {
					window.dismiss();
				}
			}
		});
	}

	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	};
	
	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
             if(intent.getAction().equals(DateTimePickDialogUtil.SELECTION_TIME_ACTION)){
            	 selected_starttiem = intent.getStringExtra("dateTime") + " 00:00:00";
 				 selected_endtiem = intent.getStringExtra("dateTime") + " 23:59:59";
 			     getInfo();
             }
		}
	};
}
