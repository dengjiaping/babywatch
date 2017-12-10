package com.mobao.watch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mb.zjwb1.R;
import com.mobao.watch.adapter.RelactionshipListAdapter;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 自定义选择安全范围弹出框
 */
public class SelectRelactionShipActivity extends Activity implements
		OnClickListener {
	
	private ListView listView;
	private LinearLayout pop_layout;
	public static RelactionshipListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.selectrelactionshipactivity);
		
		pop_layout = (LinearLayout) findViewById(R.id.pop_layout);
		listView = (ListView) findViewById(R.id.list_relactionship);
		adapter = new RelactionshipListAdapter(this,MbApplication.getGlobalData().getRelactionship());
		listView.setAdapter(adapter);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		pop_layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
			}
		});
		// 添加按钮监听
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
		finish();
	}
}
