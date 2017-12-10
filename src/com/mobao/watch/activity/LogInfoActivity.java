package com.mobao.watch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.mb.zjwb1.R;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.broadcastReceiver.DialogCountDownReceiver;
import com.mobao.watch.handler.SmsverificationHandler;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.WaitDialog;
import com.testin.agent.TestinAgent;

/**
 * 登录信息注册页面
 * 
 * @author yoine
 * 
 */
public class LogInfoActivity extends Activity {

	private TextView tvNext = null;// 下一步按钮
	private EditText edtPhoneNumber = null; // 手机号码输入框
	private EditText edtPassword = null; // 密码输入框
	private String userPassword = null; // 用于储存用户密码
	private EventHandler eh = null; // 短信验证回调
	private ImageButton ibLast = null;
	private RelativeLayout rel_back = null;// 上一步按钮
	private static String number = "";
	private static boolean isFristSend = true;
	private String token = ""; // 接收QQ或者微信登陆后传回来的token
	private String phonenumber;
	private String password;
	SmsverificationHandler handler = new SmsverificationHandler(eh, this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);

		setContentView(R.layout.loginfo_activity);

		// 接收登陆页面传回来的需要绑定的token
		token = MbApplication.getGlobalData().getToken();
		initsmssdk();// 初始化smssdk
		initViews(); // 初始化其他组件
		initIbLast(); // 初始化上一步按钮
		initTvNext(); // 初始化下一步按钮

		ActivityContainer.getInstance().addActivity(this, "LogInfoActivity");

	}

	/**
	 * 初始化上一步按钮
	 */
	private void initIbLast() {
		ibLast = (ImageButton) findViewById(R.id.btnBack);
		ibLast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogInfoActivity.this.finish();
			}
		});
		rel_back = (RelativeLayout) findViewById(R.id.rel_back);
		rel_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogInfoActivity.this.finish();
			}
		});
	}

	/**
	 * 初始化要用来验证手机的必须类SMSSDK
	 */
	private void initsmssdk() {

/*		// 测试用的（伍建鹏自己申请的）
		SMSSDK.initSDK(LogInfoActivity.this, "6749a35a5b06",
				"995b8e55ae10b37f089d45a09eede191");*/

		// 正式的key
		SMSSDK.initSDK(LogInfoActivity.this, "5adfacc4e18a",
				"e9f5fda9390c4ffefa5d8a6581808ac5");
		
		eh = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = Message.obtain();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}

			@Override
			public void beforeEvent(int event, Object data) {
				Message msg = Message.obtain();
				msg.arg1 = event;
				msg.arg2 = 100;
				msg.obj = data;
				handler.sendMessage(msg);
			}

		};

		// 设置回调事件
		SMSSDK.registerEventHandler(eh);
	}

	/**
	 * 初始化其他组件
	 */
	private void initViews() {
		edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber1);// 初始化手机号码输入框
		edtPassword = (EditText) findViewById(R.id.edtPassword1); // 初始化密码输入框

	}

	/**
	 * 初始化下一步
	 */
	private void initTvNext() {
		tvNext = (TextView) findViewById(R.id.textNext);

		/* 下一步按钮的oclick事件 */
		tvNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				WaitDialog waiter = WaitDialog.getIntence(LogInfoActivity.this);
				waiter.setContent(getResources().getString(
						R.string.request_verifiton_code_ing));
				waiter.show();

				String newNumber = edtPhoneNumber.getText().toString().trim(); // 获取的手机号码
				userPassword = edtPassword.getText().toString().trim(); // 获取输入的密码

				phonenumber = edtPhoneNumber.getText().toString();
				password = edtPassword.getText().toString();

				// 发送前验证
				if (TextUtils.isEmpty(newNumber)) {
					Toast.makeText(
							LogInfoActivity.this,
							getResources().getString(
									R.string.phone_number_can_not_null), 3000)
							.show();
					waiter.dismiss();
					return;
				}

//				if (newNumber.length() != 11) {
//					Toast.makeText(
//							LogInfoActivity.this,
//							getResources().getString(
//									R.string.phone_number_must_11_len), 3000)
//							.show();
//					waiter.dismiss();
//					return;
//				}

				if (TextUtils.isEmpty(userPassword)) {
					Toast.makeText(
							LogInfoActivity.this,
							getResources().getString(
									R.string.password_can_not_null), 3000)
							.show();
					waiter.dismiss();
					return;
				}

				handler.setNumber(newNumber);
				handler.setPhonenumber(phonenumber);
				handler.setPassword(password);
				handler.setToken(token);

				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(LogInfoActivity.this)) {
					waiter.dismiss();
					ToastUtil.show(LogInfoActivity.this, getResources()
							.getString(R.string.nonetwork));
					return;
				}

				// 判断是否重新倒数
				if (DialogCountDownReceiver.nowNum <= 0 /*
														 * 判断上一次的发送到现在是否已经超过60秒，
														 * 小于0即超过60秒
														 */
						|| !number.equals(newNumber) /* 判断上一次的手机号码与这次的手机号码是否相同 */
						|| isFristSend) { /* 判断现在输入的号码是否是第一次发送 */

					DialogCountDownReceiver.stopThread(); /* 停止上一次的倒数 */

					number = newNumber;
					isFristSend = false;

					// 向服务器请求获取验证码，这里的回调事件在SmsverificationHandler类中
					SMSSDK.getVerificationCode("86", newNumber);
					// 倒数开始
					Intent intent = new Intent(
							DialogCountDownReceiver.ACTION_START_COUNT_DOWN);
					intent.putExtra("startNum", 60);
					LogInfoActivity.this.sendBroadcast(intent);
				} else {
					Intent intent = new Intent(LogInfoActivity.this,
							VerifyActivity.class);
					// 把手机号码和密码还有token传到下个页面
					Bundle bundle = new Bundle();
					bundle.putString("phonenumber", phonenumber);
					bundle.putString("password", password);
					bundle.putString("token", token);
					bundle.putString("number", newNumber);
					System.out.println(bundle.getString("phonenumber") + "="
							+ bundle.getString("password") + "="
							+ bundle.getString("token"));
					intent.putExtras(bundle);
					LogInfoActivity.this.startActivity(intent);
					waiter.dismiss();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		ActivityContainer.getInstance().finshActivity("LogInfoActivity");
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
		DialogCountDownReceiver.stopThread(); /* 停止倒数 */
	}

	@Override
	public void onBackPressed() {
		LogInfoActivity.this.finish();
	}

	public static boolean isFristSend() {
		return isFristSend;
	}

	public static void setFristSend(boolean isFristSend) {
		LogInfoActivity.isFristSend = isFristSend;
	}

}
