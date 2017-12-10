package com.mobao.watch.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.ActiveWatchActivity;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.LogInfoActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.activity.VerifyActivity;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.WaitDialog;

public class SmsverificationHandler extends Handler {
	private ProgressDialog progDialog = null; // 圆形进度条
	private EventHandler eh = null;
	private String number = null;
	private Context context;
	private String phonenumber;
	private String password;
	private String token;
	private String URL_REGIS = CommonUtil.baseUrl + "regist";// 注册的url
	boolean ishavingbaby=false;

	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no

	public SmsverificationHandler(EventHandler eh, Context context) {
		this.eh = eh;
		this.context = context;

	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		int event = msg.arg1;
		int result = msg.arg2;

		progDialog = new ProgressDialog(context);

		// 效验验证码
		if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
			Context context = VerifyActivity.getContext();
			if (result == SMSSDK.RESULT_COMPLETE) { // 效验成功
				VerifyActivity.canOnclickNext = true;
				// 注销SMSSDK
				SMSSDK.unregisterEventHandler(eh);
				if (CheckNetworkConnectionUtil.isNetworkConnected(context)) {
					WaitDialog.getIntence(context).dismiss();
					showDialog();
					// 提交注册信息（只有phone、password、token）
					new Thread(new Runnable() {
						@Override
						public void run() {
							regist();
						}
					}).start();
				} else {
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.networkunusable), 3000).show();
					WaitDialog.getIntence(context).dismiss();
				}

				// // 跳转到扫描页面，和密码userPassword处理
				// Intent intent = new Intent(context,
				// ActiveWatchActivity.class);
				// context.startActivity(intent);

			} else if (result == SMSSDK.RESULT_ERROR) { // 效验失败
				VerifyActivity.canOnclickNext = true;
				WaitDialog.getIntence(context).dismiss();
				// 提示验证错误
				Toast.makeText(context,
						context.getResources().getString(R.string.codeworng),
						3000).show();
			}

			// 请求获取短息验证码
		} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
			if (result == SMSSDK.RESULT_COMPLETE) { // 请求成功
				Toast.makeText(context,
						context.getResources().getString(R.string.codesended),
						3000).show();
				// 跳到输入验证码界面
				if (VerifyActivity.getContext() == null) {
					Intent intent = new Intent(context, VerifyActivity.class);

					Bundle bundle = new Bundle();
					bundle.putString("phonenumber", phonenumber);
					bundle.putString("password", password);
					bundle.putString("token", token);
					bundle.putString("number", number);
					bundle.putBoolean("isShowDialog", true);
					System.out.println(bundle.getString("phonenumber") + "="
							+ bundle.getString("password") + "="
							+ bundle.getString("token"));
					intent.putExtras(bundle);

					// intent.putExtra("number", number);
					// intent.putExtra("isShowDialog", true);
					context.startActivity(intent);
				}
				WaitDialog.getIntence(context).dismiss();

			} else if (result == SMSSDK.RESULT_ERROR) { // 请求失败
				Toast.makeText(context,
						context.getResources().getString(R.string.getcodefial),
						3000).show();
				LogInfoActivity.setFristSend(true);
				WaitDialog.getIntence(context).dismiss();
			}
		}
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void regist() {

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_REGIS = "http://"+ip+":8088/api/regist";// 注册的url
		}
		
		System.out.println("regist()");
		HttpPost request = new HttpPost(URL_REGIS);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();
		try {
			param.put("phone", phonenumber);
			param.put("token", token);
			param.put("password", password);
			System.out.println(param.toString());

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);

			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();

			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断注册是否成功
				int status = result.getInt("status");
				System.out.println("status：" + status);
				if (status == 200) {
					arg2 = 1;
					// 成功后摇干啥这里搞

					// 登陆成功，保存数据，下次自动登陆
					SharedPreferences autologin = context.getSharedPreferences(
							"logindata", context.MODE_PRIVATE);
					Editor dataedt = autologin.edit();
					dataedt.putString("phonember", phonenumber);
					dataedt.putString("password", password);
					dataedt.commit();

					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);

					// 取出服务端返回的userid、phone、havingbaby
					String userid = datajson.getString("userid");
					MbApplication.getGlobalData().getNowuser().setUserid(userid);
					int havingbaby = datajson.getInt("havingbaby"); // 0表示没有宝贝，1表示有宝贝
					
					// 将注册获得的参数存起来啊
					Message msgs = Message.obtain();
					msgs.what = what;
					msgs.arg1 = 606;
					msgs.arg2 = arg2;
					handel.sendMessage(msgs);

					// 判断该用户是否有宝贝，如果有就跳到宝贝列表，没有就跳到扫描页面;
					if (havingbaby == 1) {
						// 宝贝列表需要userid，故传递过去
						ishavingbaby=true;
						Intent intent = new Intent(context,
								BabyFragmentActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("userid", userid);
						intent.putExtras(bundle);
						context.startActivity(intent);
					} else {
						// 扫描页面也需要userid，故传递过去
						Intent intent = new Intent(context,
								ActiveWatchActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("userid", userid);
						intent.putExtras(bundle);
						context.startActivity(intent);
					}

				} else {
					arg2 = -1;
					erro = result.getString("msg");
					
					Message msgs = Message.obtain();
					msgs.what = what;
					msgs.arg1 = 606;
					msgs.arg2 = arg2;
					handel.sendMessage(msgs);
					// 失败后摇干啥这里搞
					/* System.out.println("失败：" + result.getString("msg")); */
				}

			} else {
				what = -1;
				
				Message msgs = Message.obtain();
				msgs.what = what;
				msgs.arg1 = 606;
				msgs.arg2 = arg2;
				handel.sendMessage(msgs);
				/*
				 * Toast.makeText(context, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG) .show();
				 */
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Aut-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(context.getResources().getString(R.string.pleasewait_regist));
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;

			if (lj == 1) {

				if (key == 606 && arg2 == 1) {
					dismissDialog();
					if(ishavingbaby){
						Toast.makeText(context, context.getResources().getString(R.string.loginsuccess), Toast.LENGTH_LONG)
						.show();
					}else{
					Toast.makeText(context, context.getResources().getString(R.string.registersuccess), Toast.LENGTH_LONG)
							.show();}
				}
				if (key == 606 && arg2 == -1) {
					dismissDialog();
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(context);
					erromsg=change.chang(erro);
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.registerror)
									+ erromsg, 3000).show();
					Intent i = new Intent(context, LogInfoActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
				}

			} else {
				dismissDialog();
				Toast.makeText(context,
						context.getResources().getString(R.string.serverbusy),
						3000).show();
			}

		}

	}
}
