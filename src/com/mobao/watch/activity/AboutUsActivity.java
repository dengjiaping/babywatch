package com.mobao.watch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.testin.agent.TestinAgent;

public class AboutUsActivity extends Activity {
	private ImageButton ibLast = null;
	private RelativeLayout btnBack;// 上一步按钮
	private ImageButton btnFeedback = null;//反馈
	private TextView verText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		//云测崩溃分析
		TestinAgent.init(this);
		
		setContentView(R.layout.about_us_activity);
		
		//设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
						R.anim.base_slide_remain);


		initIbLast(); // 初始化上一步按钮

		verText = (TextView) findViewById(R.id.Textversions);
		
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;		
			String ver = getString(R.string.versions) + version;
			verText.setText(ver);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initIbLast() {
		ibLast = (ImageButton) findViewById(R.id.btnBack);
		ibLast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
		btnBack =  (RelativeLayout) findViewById(R.id.rel_back);
		ibLast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
		
		btnFeedback=(ImageButton) findViewById(R.id.btnFeedback);
		btnFeedback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					//发送邮件
					Intent data=new Intent(Intent.ACTION_SENDTO); 
					data.setData(Uri.parse("mailto:38623270@qq.com")); 
					startActivity(data);
				} catch (Exception e) {
					Toast.makeText(AboutUsActivity.this, getString(R.string.helperror), 3000).show();
					e.printStackTrace();
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
