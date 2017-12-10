package com.mobao.watch.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.smssdk.SMSSDK;

import com.mb.zjwb1.R;
import com.mobao.watch.broadcastReceiver.DialogCountDownReceiver;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.UniformDialog;
import com.mobao.watch.util.WaitDialog;
import com.testin.agent.TestinAgent;

public class VerifyActivity extends Activity {

	private ImageButton ibLast = null;
	private RelativeLayout rel_back = null;// 上一步
	private TextView tvNext = null; // 下一步
	private EditText etVerificationCode = null; // 验证码输入框
	private TextView tvNumber = null; //
	private static ImageButton ibAgain = null; // 重发验证码
	private static Context context;
	private static String number = ""; // 手机号码
	private String phonenumber = ""; // 手机号码
	private String password = ""; // 密码
	private String token = ""; // token
	private String URL_REGIS = "http://115.28.62.126:8088/api/regist";// 注册的url
	public static boolean canOnclickNext = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.verify_activity);
		context = VerifyActivity.this;

		initViews(); // 初始化其他组件

		// 获得前面页面传过来的参数，为了避免混乱，号码取了两次

		Bundle bundle = this.getIntent().getExtras();
		phonenumber = bundle.getString("phonenumber");
		password = bundle.getString("password");
		token = bundle.getString("token");
		number = bundle.getString("number");

		//进入过验证码页面就将isinto设置为false
		MbApplication.getGlobalData().setIsinto(false);

		if (number != null) {
			tvNumber.setText(number);
		}

		// 判断是否弹出倒数框
		if (DialogCountDownReceiver.nowNum > 0) {
			DialogCountDownReceiver.showDialog(DialogCountDownReceiver.nowNum,
					VerifyActivity.this, getResources().getString(R.string.sms_verification_code_has_sent));
		}

		initIbLast(); // 初始化上一步按钮
		initTvNext(); // 初始化下一步按钮
		initIbAgain(); // 初始化重发验证码按钮

		ActivityContainer.getInstance().addActivity(this, "VerifyActivity");
	}

	private void initIbAgain() {
		ibAgain = (ImageButton) findViewById(R.id.btnResendVertical);
		ibAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DialogCountDownReceiver.nowNum <= 0) {
					WaitDialog waiter = WaitDialog.getIntence(context);
					waiter.setContent(getResources().getString(R.string.request_verifiton_code_ing));
					waiter.show();
					
					
					if(!CheckNetworkConnectionUtil.isNetworkConnected(VerifyActivity.this)){
						ToastUtil.show(VerifyActivity.this, getResources().getString(R.string.nonetwork));
						waiter.dismiss();
						return;
					}
					
					SMSSDK.getVerificationCode("86", number);
					DialogCountDownReceiver.showDialog(60, VerifyActivity.this,
							getResources().getString(R.string.sms_verification_code_has_sent));
					Intent intent = new Intent(
							DialogCountDownReceiver.ACTION_START_COUNT_DOWN);
					intent.putExtra("startNum", 60);
					VerifyActivity.this.sendBroadcast(intent);
				} else {
					DialogCountDownReceiver.showDialog(
							DialogCountDownReceiver.nowNum,
							VerifyActivity.this, getResources().getString(R.string.sms_verification_code_has_sent));
				}
			}
		});
	}

	private void initViews() {
		etVerificationCode = (EditText) findViewById(R.id.edtVertical);
		tvNumber = (TextView) findViewById(R.id.textPhoneNumber);

	}

	private void initTvNext() {
		tvNext = (TextView) findViewById(R.id.textNext);
		tvNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				WaitDialog waiter = WaitDialog.getIntence(context);
				waiter.setContent(getResources().getString(R.string.verify_verification_code));
				waiter.show();
				
				if(!CheckNetworkConnectionUtil.isNetworkConnected(VerifyActivity.this)){
					ToastUtil.show(VerifyActivity.this, getResources().getString(R.string.nonetwork));
					waiter.dismiss();
					return;
				}
				
				String code = etVerificationCode.getText().toString().trim();
				
				if(canOnclickNext){
					SMSSDK.submitVerificationCode("86", number, code);
				}
				
				canOnclickNext = false;
			}
		});
	}

	private void initIbLast() {
		ibLast = (ImageButton) findViewById(R.id.btnBack);
		ibLast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VerifyActivity.this.finish();
				UniformDialog dialog = UniformDialog.getInstance();
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});
		rel_back = (RelativeLayout) findViewById(R.id.rel_back);
		rel_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VerifyActivity.this.finish();
				UniformDialog dialog = UniformDialog.getInstance();
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		context = VerifyActivity.this;
	}

	@Override
	public void onBackPressed() {
		VerifyActivity.this.finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		context = null;
	}

	public static Context getContext() {
		return context;
	}

	public static ImageButton getIbAgain() {
		return ibAgain;
	}
	

	@Override
	protected void onDestroy() {
		ActivityContainer.getInstance().finshActivity("VerifyActivity");
		super.onDestroy();
	}

}
