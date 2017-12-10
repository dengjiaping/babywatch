package com.mobao.watch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 自定义选择安全范围弹出框
 */
public class SelectSafetyRangePopupWindowActivity extends Activity implements
		OnClickListener {
	private TextView text_300, text_400, text_500, text_1000, text_cancel;
	private LinearLayout layout;
	public static final String CHANGE_SAFETY_RADIUS_ACTION = "CHANGE_SAFETY_RADIUS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.selectsafetyrangepopupwindowactivity);
		text_300 = (TextView) findViewById(R.id.text_300);
		text_400 = (TextView) findViewById(R.id.text_400);
		text_500 = (TextView) findViewById(R.id.text_500);
		text_1000 = (TextView) findViewById(R.id.text_1000);
		text_cancel = (TextView) findViewById(R.id.text_cancel);

		layout = (LinearLayout) findViewById(R.id.pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		// 添加按钮监听
		text_300.setOnClickListener(this);
		text_400.setOnClickListener(this);
		text_500.setOnClickListener(this);
		text_1000.setOnClickListener(this);
		text_cancel.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		Intent intent = new Intent(CHANGE_SAFETY_RADIUS_ACTION);
		switch (v.getId()) {
		case R.id.text_300:
			intent.putExtra("safetyRadius", 300);
			intent.putExtra("showArea", 16);
			sendBroadcast(intent);
			break;
		case R.id.text_400:
			intent.putExtra("safetyRadius", 400);
			intent.putExtra("showArea", 16);
			sendBroadcast(intent);
			break;
		case R.id.text_500:
			intent.putExtra("safetyRadius", 500);
			intent.putExtra("showArea", 16);
			sendBroadcast(intent);
			break;
		case R.id.text_1000:
			intent.putExtra("safetyRadius", 1000);
			intent.putExtra("showArea", 15);
			sendBroadcast(intent);
			break;
		case R.id.text_cancel:
			break;
		default:
			break;
		}
		finish();
	}
}
