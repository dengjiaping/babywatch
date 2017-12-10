package com.mobao.watch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.mb.zjwb1.R;
import com.testin.agent.TestinAgent;

public class HelpActivity extends Activity {

	private WebView helpcontent;
	private ImageButton btn_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);

		setContentView(R.layout.help);

		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		// 实现返回按钮功能
		intview();

		helpcontent = (WebView) findViewById(R.id.helpwebview);
		// 在view中浏览网页，不调用第三方应用
		helpcontent.setWebViewClient(new WebViewClient());
		// 加载使用javascript代码
		helpcontent.getSettings().setJavaScriptEnabled(true);
		// 不使用缓存
		helpcontent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
			// 显示在线网页
			helpcontent.loadUrl("http://watch.ios16.com/help.html");
		} else if (getResources().getConfiguration().locale.getCountry()
				.equals("TW")) {
			// 显示在线网页
			helpcontent.loadUrl("http://watch.ios16.com/help.html");
		} else {
			// 显示在线网页
			helpcontent.loadUrl("http://watch.ios16.com/en/help.html");
		}

	}

	private void intview() {
		// TODO Auto-generated method stub
		btn_back = (ImageButton) findViewById(R.id.btn_Back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	private void intiwebview(String url) {

		helpcontent = (WebView) findViewById(R.id.helpwebview);
		// 在view中浏览网页，不调用第三方应用
		helpcontent.setWebViewClient(new WebViewClient());
		// 加载使用javascript代码
		helpcontent.getSettings().setJavaScriptEnabled(true);
		// 加载本地连接
		helpcontent.loadUrl("file:///android_asset/" + url);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
