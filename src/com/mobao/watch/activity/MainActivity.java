package com.mobao.watch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mb.zjwb1.R;
import com.testin.agent.TestinAgent;

public class MainActivity extends Activity {
	private Button btnLogin;
	private Button btnLogInfo;
	private Button btnVerify;
	private Button btnActivationWatch;
	private Button btnAddBaby;
	private Button btnAboutUs;
	private Button btnMoreFragmen;

	private Button btnFamilyMember;
	private Button btnWatchManger;

	private Button btnFragment;

	private Button btnAward; // 要跳到奖励页面的按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		
		setContentView(R.layout.activity_main);

		initView();
	}

	private void initView() {
		btnLogin = (Button) findViewById(R.id.btnLoginActivity);
		btnLogInfo = (Button) findViewById(R.id.btnLogInfoActivity);
		btnVerify = (Button) findViewById(R.id.btnVerifyActivity);
		btnActivationWatch = (Button) findViewById(R.id.btnActivationWatchActivity);
		btnAddBaby = (Button) findViewById(R.id.btnAddBabyActivity);
		btnAboutUs = (Button) findViewById(R.id.btnAboutUsActivity);

		btnFamilyMember = (Button) findViewById(R.id.btnFamilyMemberActivity);
		btnWatchManger = (Button) findViewById(R.id.btnWatchMangerActivity);

		btnFragment = (Button) findViewById(R.id.babyFragmentActivity);

		btnAward = (Button) findViewById(R.id.btnAward);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(intent);

			}
		});

		btnLogInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						LogInfoActivity.class);
				startActivity(intent);

			}
		});

		btnVerify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						VerifyActivity.class);
				startActivity(intent);

			}
		});

		btnActivationWatch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						ActiveWatchActivity.class);
				startActivity(intent);

			}
		});

		btnAddBaby.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						AddBabyActivity.class);
				startActivity(intent);

			}
		});

		btnAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						AboutUsActivity.class);
				startActivity(intent);

			}
		});

		btnFamilyMember.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						FamilyMemberActivity.class);
				startActivity(intent);

			}
		});

		btnWatchManger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						WatchMangerActivity.class);
				startActivity(intent);

			}
		});

		btnFragment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						BabyFragmentActivity.class);
				startActivity(intent);

			}
		});

		btnAward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						RewardFragmentActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
