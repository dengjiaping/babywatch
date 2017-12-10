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
import com.mobao.watch.bean.GlobalData;
import com.testin.agent.TestinAgent;

/**
 * @author 性别选择框
 */
public class SelectSetSexActivity extends Activity implements
		OnClickListener {
	private TextView  txt_man, txt_woman;
	private LinearLayout layout;
	public static String SELECT_SEX_ACTION = "SELECT_SEX_ACTION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.selectsexactivity);
	

		txt_man=(TextView) findViewById(R.id.txt_man);
        txt_woman=(TextView) findViewById(R.id.text_woman);
		layout = (LinearLayout) findViewById(R.id.pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
			}
		});
		// 添加按钮监听
	txt_man.setOnClickListener(this);
	txt_woman.setOnClickListener(this);

	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
	    Intent intent = new Intent(SELECT_SEX_ACTION);
		switch (v.getId()) {
		case R.id.txt_man:
			MbApplication.getGlobalData().setNowsex(txt_man.getText().toString());
			intent.putExtra("sex", "男");
			sendBroadcast(intent);
			break;
		case R.id.text_woman:
			MbApplication.getGlobalData().setNowsex(txt_woman.getText().toString());
			intent.putExtra("sex", "女");
			sendBroadcast(intent);
			break;

		default:
			break;
		}
		finish();
	}
}
