package com.mobao.watch.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.util.ChatOpenMonitorThread;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 自定义选择安全范围弹出框
 */
public class chatfragmentSelectCallWayActivity extends Activity implements
		OnClickListener {
	private TextView tvCallForTalk, tvCallForMonitor, text_cancel;
	private LinearLayout layout;
	private String babyImei;
	private String babyPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.selectgetphotomethodactivity);
		tvCallForMonitor = (TextView) findViewById(R.id.txt_take_photo);
		tvCallForTalk = (TextView) findViewById(R.id.txt_photo_album);
		text_cancel = (TextView) findViewById(R.id.text_cancle);

		tvCallForMonitor.setText(getResources()
				.getString(R.string.monitor_baby));
		tvCallForTalk.setText(getResources().getString(
				R.string.call_watch_phone));

		layout = (LinearLayout) findViewById(R.id.pop_layout);

		babyImei = getIntent().getStringExtra(ChatFragment.EXTRA_NOW_BABYIMEI);
		babyPhone = getIntent().getStringExtra(ChatFragment.EXTRA_NOW_BABYPHONE);
		
		WindowManager windowManager = getWindowManager(); 
		Display display = windowManager.getDefaultDisplay(); 
		WindowManager.LayoutParams lp = this.getWindow().getAttributes(); 
		lp.width = (int)(display.getWidth()); //设置宽度 
		this.getWindow().setAttributes(lp); 
		
		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		// 添加按钮监听
		tvCallForTalk.setOnClickListener(this);
		tvCallForMonitor.setOnClickListener(this);
		text_cancel.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_take_photo: // 监听宝贝
			if (babyImei == null) {
				Toast.makeText(this, getResources().getString(R.string.call_baby_fail), 3000).show();
				return;
			}
			ChatOpenMonitorThread openMonitor = new ChatOpenMonitorThread(chatfragmentSelectCallWayActivity.this,
					babyImei, babyPhone);
			openMonitor.start();
			break;
		case R.id.txt_photo_album: // 直接拨打宝贝电话
			if (babyPhone == null) {
				Toast.makeText(this, getResources().getString(R.string.call_baby_fail), 3000).show();
				return;
			}
			Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ babyPhone));
			startActivity(intent2);
			break;
		case R.id.text_cancel:
			break;
		default:
			break;
		}
		finish();
	}
}
