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
public class SelectGetPhotoMethodActivity extends Activity implements
		OnClickListener {
	private TextView txt_take_photo, txt_photo_album, text_cancel;
	private LinearLayout layout;
	public static final String GET_PHOTO_METHOD = "GET_PHOTO_METHOD_ACTION";
	public static final String TAKE_PHOTO = "TAKE_PHOTO";
	public static final String PHOTO_ALBUM = "PHOTO_ALBUM";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.selectgetphotomethodactivity);
		txt_take_photo = (TextView) findViewById(R.id.txt_take_photo);
		txt_photo_album = (TextView) findViewById(R.id.txt_photo_album);
		text_cancel = (TextView) findViewById(R.id.text_cancle);

		layout = (LinearLayout) findViewById(R.id.pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
			}
		});
		// 添加按钮监听
		txt_take_photo.setOnClickListener(this);
		txt_photo_album.setOnClickListener(this);
		text_cancel.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		Intent intent = new Intent(GET_PHOTO_METHOD);
		switch (v.getId()) {
		case R.id.txt_take_photo:
			intent.putExtra("method", TAKE_PHOTO);
			sendBroadcast(intent);
			break;
		case R.id.txt_photo_album:
			intent.putExtra("method", PHOTO_ALBUM);
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
