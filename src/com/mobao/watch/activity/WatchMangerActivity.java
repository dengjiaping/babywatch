package com.mobao.watch.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.adapter.WatchMangerListAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.SPUtil;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

public class WatchMangerActivity extends Activity {
	private ImageButton ibLast = null;
	private RelativeLayout rel_back = null;// 上一步按钮
	private LinearLayout lin_gotoVerifyActivuty;// 进入二维码扫描页面
	private ListView listView;
	public static WatchMangerListAdapter adapter;
	// 获取宝贝列表
	private ArrayList<Baby> babyList = new ArrayList<Baby>();
	private ProgressDialog progDialog = null; // 圆形进度条

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.watch_manger_activity);

		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		progDialog = new ProgressDialog(this);
		showDialog();
		initIbLast(); // 初始化上一步按钮和进入二维码扫描页面
		// 获取宝贝列表数据
		getBabyList();

	}

	private void getBabyList() {
		if(!CheckNetworkConnectionUtil.isNetworkConnected(this)){
			Toast.makeText(WatchMangerActivity.this, getString(R.string.networkunusable),
					3000).show();
			dismissDialog();
			return;
		}
		new Thread() {
			public void run() {
				final ArrayList<Baby> arrayList = BabyServer.getBabyList();
				MbApplication.getGlobalData().setBabycount(arrayList.size());
				if (arrayList != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							if(arrayList.size()==0){
								/*伍建鹏////////////////////////*/
								Intent intent = new Intent(WatchMangerActivity.this,ActiveWatchActivity.class);
								intent.putExtra(ActiveWatchActivity.INTENT_EXTRA_IS_REGISTER, false);
								/*///////////////////////*/
								startActivity(intent);
								finish();
							}
							babyList = arrayList;
							listView = (ListView) findViewById(R.id.watch_manaer_listView);
							adapter = new WatchMangerListAdapter(
									WatchMangerActivity.this, babyList);
							listView.setAdapter(adapter);
							dismissDialog();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(WatchMangerActivity.this, getString(R.string.serverbusy),
									3000).show();
							dismissDialog();
						}
					});
				}
			}
		}.start();
	}

	private void initIbLast() {
		ibLast = (ImageButton) findViewById(R.id.btnBack);
		ibLast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WatchMangerActivity.this.finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
		rel_back = (RelativeLayout) findViewById(R.id.rel_back);
		rel_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WatchMangerActivity.this.finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
		if (SPUtil.getIsAdmin(WatchMangerActivity.this) == true) {
			lin_gotoVerifyActivuty = (LinearLayout) findViewById(R.id.lin_gotoVerifyActivuty);
			lin_gotoVerifyActivuty.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ToastUtil.show(WatchMangerActivity.this, getResources().getString(R.string.opneCameraing));
					startActivity(new Intent(WatchMangerActivity.this,
							ActiveWatchActivity.class).putExtra("addother",
							"addother").putExtra(ActiveWatchActivity.INTENT_EXTRA_IS_REGISTER, false));
					
					WatchMangerActivity.this.finish();
				}
			});
		} else {
			lin_gotoVerifyActivuty = (LinearLayout) findViewById(R.id.lin_gotoVerifyActivuty);
			lin_gotoVerifyActivuty.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getString(R.string.gettingdata));
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
