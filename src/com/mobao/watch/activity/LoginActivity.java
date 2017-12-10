package com.mobao.watch.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.amap.api.location.c;
import com.mb.zjwb1.R;
import com.mobao.watch.bean.Authlist;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.NowUser;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.service.ChatNewMsgService;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.AudioRecorder;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.SPUtil;
import com.mobao.watch.util.SetStepGoalDialog;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.YunBaStart;
import com.testin.agent.TestinAgent;

public class LoginActivity extends Activity implements PlatformActionListener {

	/* 伍建鹏//////////////////////////////////// */
	public static String appDataDir; // 程序数据目录
	/* //////////////////////////////////////// */
	private boolean isfimily = false;
	private boolean isadmin = false;
	private String imei;// 当前用户的第一个imei
	private ProgressDialog progDialog = null; // 圆形进度条
	private ImageButton qqlogin;
	private ImageButton weixinlogin;
	RelativeLayout mainlayout;
	private int weixinId;
	private TextView tvRegister = null; // 注册按钮
	private ImageButton btnLogIn = null; // 普通登录按钮
	private TextView textForgotPassword = null;// 忘记密码
	private String URL_LOGIN = CommonUtil.baseUrl + "login";// 登陆的url
	private String URL_IFFAMILY = CommonUtil.baseUrl + "iffamily";// 验证是否为家庭成员的url
	private String URL_IFADMIN = CommonUtil.baseUrl + "ifadmin";// 验证是否为管理员的url
	private String URL_GETLOCALINTERVAL = CommonUtil.baseUrl + "getlocalinterval";// 获取定位间隔
	private String URL_CONFIRMAUTH = CommonUtil.baseUrl + "confirmauth";// 同意审核的的url
	private String URL_DISAVOW = CommonUtil.baseUrl + "disavow";// 拒绝审核的的url
	private int LOGIN_TYPE; // 登陆类型 1为普通登陆 2为token登陆
	private String phonenumber; // 手机号码
	private String password; // 密码
	private String token; // token
	private EditText et_phonenumber; // 手机号码输入框
	private EditText et_password; // 密码输入框
	private String[] authlistArray;
	private String model = "";// 手机型号
	
	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	private boolean networkConnection = true;
	private int havingbaby;
	private String adminphone = "";
	private boolean[] ischeckid; // 被选中的项
	public static boolean isCN = true; // 判断当前系统语言

	public static String dnspodIp;

	private String mDeviceID;  //设备id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化Testin崩溃分析
		TestinAgent.init(LoginActivity.this,
				"0187d4e7b8c0a3a4f262f27caa3f3eda", "ertongwanbiao");

		// 云巴推送
		YunBaStart.yunBaStart(LoginActivity.this);

		// 获取ip
		GetIp();
		Log.i("keis", "解析域名得到的dnspodIp：" + dnspodIp);
		if (dnspodIp != null) {
			URL_LOGIN = "http://" + dnspodIp + ":8088/api/login";// 登陆的url
			URL_IFFAMILY = "http://" + dnspodIp + ":8088/api/iffamily";// 验证是否为家庭成员的url
			URL_IFADMIN = "http://" + dnspodIp + ":8088/api/ifadmin";// 验证是否为管理员的url
			URL_GETLOCALINTERVAL = "http://" + dnspodIp
					+ ":8088/api/getlocalinterval";// 获取定位间隔
			URL_CONFIRMAUTH = "http://" + dnspodIp + ":8088/api/confirmauth";// 同意审核的的url
			URL_DISAVOW = "http://" + dnspodIp + ":8088/api/disavow";// 拒绝审核的的url
		}else{
			dnspodIp="hedy.ios16.com";
		}

		setContentView(R.layout.login_activity);
		mainlayout = (RelativeLayout) findViewById(R.id.login_mainlLayout);
		// 判断当前系统的语言
		checkLanguage();

		/* 伍建鹏//////////////////////////////////// */
		model = getResources().getString(R.string.unknown_model);
		appDataDir = getApplicationContext().getFilesDir().getAbsolutePath();
		
		getDeviceId();
		
		/* //////////////////////////////////////// */

		/*
		 * if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy
		 * policy = new StrictMode.ThreadPolicy.Builder() .permitAll().build();
		 * StrictMode.setThreadPolicy(policy); }
		 */

		ShareSDK.initSDK(LoginActivity.this);
		progDialog = new ProgressDialog(LoginActivity.this);
		// 获取上次登陆时保存的数据
		SharedPreferences autologin = getSharedPreferences("logindata",
				MODE_PRIVATE);
		phonenumber = autologin.getString("phonember", "");
		password = autologin.getString("password", "");
		token = autologin.getString("token", "");
		initNormalLogin(); // 初始化普通登录
		// 判断是否联网
		networkConnection = isNetworkConnected(this);
		if (networkConnection == false) {
			ToastUtil.show(LoginActivity.this,
					getResources().getString(R.string.networkunusable));
			mainlayout.setVisibility(View.VISIBLE);
			return;
		}
		// 如果有保存账号和密码则直接登陆(有token优先使用token登陆)
		if (token != null && token.length() > 0) {
			showDialog();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					login_token();
				}
			}).start();

		} else {
			// 没有token则使用普通登陆
			if (phonenumber != null && phonenumber.length() > 0){
				if (password != null && password.length() > 0) {
					showDialog();
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							login_pt();
						}
					}).start();

				}
			} else {
				mainlayout.setVisibility(View.VISIBLE);
			}
		}

		initQQandWeixinLogin(); // 初始化QQ和微信登录

		initTvRegister(); // 初始化注册按钮和忘记密码按钮

		ActivityContainer.getInstance().addActivity(this, "LoginActivity");
		if (networkConnection == false) {
			ToastUtil.show(LoginActivity.this,
					getResources().getString(R.string.networkunusable));
			return;
		}
	}

	private void getDeviceId() {
		mDeviceID = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		Editor editor = getSharedPreferences(ChatNewMsgService.TAG,
				MODE_PRIVATE).edit();
		editor.putString(ChatNewMsgService.PREF_DEVICE_ID, mDeviceID);
		editor.commit();
	}

	private void checkLanguage() {
		if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
			isCN = true;
		} else {
			isCN = false;
		}
	}

	private void initNormalLogin() {

		// TODO 点击登录跳往宝贝页面 如果没有宝贝还需进一步处理
		btnLogIn = (ImageButton) findViewById(R.id.btnLogin);
		btnLogIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 判断是否联网
				networkConnection = isNetworkConnected(LoginActivity.this);
				if (networkConnection == false) {
					ToastUtil.show(LoginActivity.this,
							getString(R.string.networkunusable));
					return;
				}
				// 手机号码登陆

				showDialog();
				
				btnLogIn.setClickable(false);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						login_pt();
					}
				}).start();

			}
		});

		/*
		 * // 方便测试 后期删除 start if (phonenumber == null || phonenumber.length() <
		 * 1) { phonenumber = "18819318767"; password = "123456"; } // 方便测试 后期删除
		 * end
		 */
		et_phonenumber = (EditText) findViewById(R.id.edtPhoneNumber);
		et_phonenumber.setText(phonenumber);
		et_password = (EditText) findViewById(R.id.edtPassword);
		et_password.setText(password);

		// 登陆成功的页面先放着
		/*
		 * btnLogIn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent i = new
		 * Intent(LoginActivity.this, BabyFragmentActivity.class);
		 * startActivity(i); } });
		 */

	}

	protected void login_pt() {
		// TODO Auto-generated method stub

		LOGIN_TYPE = 1; // 设置登陆方式为普通登陆
		phonenumber = et_phonenumber.getText().toString();
		password = et_password.getText().toString();

		HttpPost request = new HttpPost(URL_LOGIN);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("logintype", String.valueOf(LOGIN_TYPE));
			param.put("phone", phonenumber);
			param.put("password", password);
			param.put("token", "");
			param.put("model", model);// 机型
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					arg2 = 1;

					/*
					 * msg.what = what; msg.arg1 = 500; msg.arg2 = arg2;
					 * handel.sendMessage(msg);
					 */
					// 登陆成功，保存数据，下次自动登陆
					SharedPreferences autologin = getSharedPreferences(
							"logindata", MODE_PRIVATE);
					Editor dataedt = autologin.edit();
					dataedt.putString("phonember", phonenumber);
					dataedt.putString("password", password);
					dataedt.commit();

					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);
					System.out.println("成功：    data:" + data);
					// 取出服务端返回的userid、phone、havingbaby、authlist、portrait
					String userid = datajson.getString("userid");
					String phone = datajson.getString("adminphone");
					havingbaby = datajson.getInt("havingbaby"); // 0表示没有宝贝，1表示有宝贝

					MbApplication.getGlobalData().getNowuser().setUserid(userid);
					MbApplication.getGlobalData().getNowuser().setHavingbaby(havingbaby);
					MbApplication.getGlobalData().getNowuser().setAdminphone(phone);
					// 判断该用户是否有宝贝，如果有就跳到宝贝列表，没有就跳到扫描页面;
					if (havingbaby == 1) {
						isadmin();
						if (isadmin) {
//							// 将是否为管理员储存在全局变量中
							MbApplication.getGlobalData().getNowuser().setIsadmin(true);
							// 将是否为管理员储存在SP中
							SPUtil.setIsAdmin(LoginActivity.this, true);
							System.out.println("用户是管理员");
							// 用户为管理员
							JSONArray authlist = datajson
									.getJSONArray("authlist");
							if (authlist.length() != 0) {
								// 将待审核的列表传到NowUser里面

								ArrayList<Authlist> authlistarray = new ArrayList<Authlist>();// 待审核列表

								for (int i = 0; i < authlist.length(); i++) {
									Authlist empt = new Authlist();
									JSONObject emptjson = authlist
											.getJSONObject(i);
									empt.setImei(emptjson.getString("imei"));
									empt.setUserid(emptjson.getString("userid"));
									empt.setPhone(emptjson.getString("phone"));
									System.out.println(emptjson
											.getString("phone"));
									empt.setRelate(emptjson.getString("relate"));
									empt.setTime(emptjson.getString("time"));
									authlistarray.add(empt);
								}
								MbApplication.getGlobalData().getNowuser().setAuthlistarray(
										authlistarray);

								Message msg = Message.obtain();
								// 发送消息处理待审核列表
								msg.what = what;
								msg.arg1 = 510;
								msg.arg2 = arg2;
								handel.sendMessage(msg);

							} else {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 500;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
								
								Intent intent = new Intent(LoginActivity.this,
										BabyFragmentActivity.class);
								intent.putExtra("ifadmin",isadmin);
								startActivity(intent);
								finish();
							}

						} else {
//							// 将是否为管理员储存在全局变量中
							MbApplication.getGlobalData().getNowuser().setIsadmin(false);
						
							// 将是否为管理员储存在SP中
							SPUtil.setIsAdmin(LoginActivity.this, false);
							// 不是管理员直接登陆的则登陆成功
							System.out.println("用户不是管理员");
							Message msg = Message.obtain();
							msg.what = what;
							msg.arg1 = 500;
							msg.arg2 = arg2;
							handel.sendMessage(msg);
							
							Intent intent = new Intent(LoginActivity.this,
									BabyFragmentActivity.class);
							intent.putExtra("ifadmin",isadmin);
							startActivity(intent);
							finish();
						}

						final ArrayList<Baby> babyList = BabyLocateServer
								.getBabyList(userid);
						if (babyList != null) {

							if (babyList.size() == 0) {
								Intent intent = new Intent(LoginActivity.this,
										ActiveWatchActivity.class);
								intent.putExtra("userid", userid);
								startActivity(intent);
								return;
							}
							Baby baby = babyList.get(0);
							imei = baby.getBabyimei();

						}

					} else {

						JSONArray authresult = datajson
								.getJSONArray("authresult");
						if (authresult.length() > 0) {
							if (authresult.getJSONObject(0).getString("result")
									.equals("failed")) {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 502;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
							} else {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 500;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
							}

						} else {
							JSONArray authing = datajson
									.getJSONArray("authing");
							if (authing.length() == 0) {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 480;
								msg.arg2 = arg2;
								handel.sendMessage(msg);

							} else {
								adminphone = authing.getJSONObject(0)
										.getString("phone");
								
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 501;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
							}

						}

						// 扫描页面也需要userid，故传递过去
						/*
						 * Intent intent = new Intent(LoginActivity.this,
						 * ActiveWatchActivity.class); Bundle bundle = new
						 * Bundle(); bundle.putString("userid", userid);
						 * intent.putExtras(bundle); startActivity(intent);
						 */
					}

				} else {
					arg2 = -1;
					erro = result.getString("msg");
					
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 500;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
					
					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 500;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				
				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
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

	protected boolean isadmin() {
		// TODO 用户是否为管理员
		HttpPost request = new HttpPost(URL_IFADMIN);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {

					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);
					if (datajson.getString("status").equals("yes")) {
						isadmin = true;
					}

				} else {

					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 500;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				
				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
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
		return isadmin;

	}

	protected void disavow(String userid, String imei) {
		// TODO 拒绝审核
		HttpPost request = new HttpPost(URL_DISAVOW);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", userid);
			param.put("imei", imei);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {

				} else {

					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 500;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				
				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
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

	protected void confirmauth(String userid, String imei) {
		// TODO 同意审核
		HttpPost request = new HttpPost(URL_CONFIRMAUTH);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", userid);
			param.put("imei", imei);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					/*
					 * arg2=1; msg=new Message(); msg.what = what; msg.arg1 =
					 * 498; msg.arg2 = arg2; handel.sendMessage(msg);
					 */
				} else {
					/*
					 * erro=result.getString("msg"); msg=new Message(); msg.what
					 * = what; msg.arg1 = 498; msg.arg2 = arg2;
					 * handel.sendMessage(msg);
					 */
					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 500;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				
				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
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

	protected boolean iffamily() {
		// TODO 验证是否加入家庭圈
		HttpPost request = new HttpPost(URL_IFFAMILY);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", imei);
			param.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {

				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);
					System.out.println("成功：    data:" + data);
					// 取出服务端返回的userid、phone、havingbaby、authlist、portrait
					String family = datajson.getString("status");
					if (family.equals("yes")) {
						isfimily = true;
					}
				} else {

					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {

				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
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
		return isfimily;

	}

	protected void onDestroy() {
		progDialog.dismiss();
		new Thread() {
			@Override
			public void run() {
				try {
					ChatUtil.checkAudiosFile(false);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}.start();

		super.onDestroy();
		ActivityContainer.getInstance().finshActivity("LoginActivity");
	}

	private void initQQandWeixinLogin() {
		// TODO Auto-generated method stub
		qqlogin = (ImageButton) findViewById(R.id.btnQQ);
		weixinlogin = (ImageButton) findViewById(R.id.btnWeiXin);
		qqlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				networkConnection = isNetworkConnected(LoginActivity.this);
				if (networkConnection == false) {
					ToastUtil.show(LoginActivity.this,
							getString(R.string.networkunusable));
					return;
				}

				Platform qqLogin = ShareSDK.getPlatform( LoginActivity.this,
				 QQ.NAME);
				 qqLogin.setPlatformActionListener(LoginActivity.this);
				 qqLogin.authorize();
				 


			}
		});

		weixinlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				networkConnection = isNetworkConnected(LoginActivity.this);
				if (networkConnection == false) {
					ToastUtil.show(LoginActivity.this,
							getString(R.string.networkunusable));
					return;
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						Platform weibo = ShareSDK.getPlatform(Wechat.NAME);
						weibo.SSOSetting(false); // 设置false表示使用SSO授权方式
						weibo.setPlatformActionListener(LoginActivity.this); // 设置分享事件回调
						weibo.authorize();

						/*
						 * Platform weixinLogin = ShareSDK.getPlatform(
						 * LoginActivity.this, Wechat.NAME);
						 * 
						 * weixinLogin
						 * .setPlatformActionListener(LoginActivity.this);
						 * weixinLogin.authorize();
						 */

					}
				}).start();

			}

		});
	}

	private void initTvRegister() {
		// 获取手机型号
		TelephonyManager phoneMgr = (TelephonyManager) LoginActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		model = Build.MODEL;

		tvRegister = (TextView) findViewById(R.id.RegisteredAccount);
		tvRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				networkConnection = isNetworkConnected(LoginActivity.this);
				if (networkConnection == false) {
					ToastUtil.show(LoginActivity.this,
							getString(R.string.networkunusable));
					return;
				}
				Intent intent = new Intent(LoginActivity.this,
						LogInfoActivity.class);
				startActivity(intent); // 调到注册页面
			}
		});
		textForgotPassword = (TextView) findViewById(R.id.textForgotPassword);
		textForgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				networkConnection = isNetworkConnected(LoginActivity.this);
				if (networkConnection == false) {
					ToastUtil.show(LoginActivity.this,
							getString(R.string.networkunusable));
					return;
				}
				MbApplication.getGlobalData().setTextForgotPassword(true);
				Intent intent = new Intent(LoginActivity.this,
						LogInfoActivity.class);
				startActivity(intent); // 调到注册页面
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
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete(Platform platform, int arg1,
			HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		// 获取token
		token = platform.getDb().getToken();
		// 选择token登陆
		login_token();

	}

	private void login_token() {
		// TODO Auto-generated method stub
		LOGIN_TYPE = 2; // 设置登陆方式为token登陆
		phonenumber = et_phonenumber.getText().toString();
		password = et_password.getText().toString();

		HttpPost request = new HttpPost(URL_LOGIN);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("logintype", String.valueOf(LOGIN_TYPE));
			param.put("phone", phonenumber);
			param.put("password", "");
			param.put("token", token);
			param.put("model", model);// 机型
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println("9dsfsdfsdfsdf3");
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			System.out.println(param.toString() + "res" + res);

			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					arg2 = 1;

					// 保存token
					SharedPreferences autologin = getSharedPreferences(
							"logindata", MODE_PRIVATE);
					Editor dataedt = autologin.edit();
					dataedt.putString("token", token);
					dataedt.commit();

					/*
					 * msg.what = what; msg.arg1 = 500; msg.arg2 = arg2;
					 * handel.sendMessage(msg);
					 */
					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);
					System.out.println("成功：    data:" + data);
					// 取出服务端返回的userid、phone、havingbaby、authlist、portrait
					String userid = datajson.getString("userid");
					String phone = datajson.getString("adminphone");
					havingbaby = datajson.getInt("havingbaby"); // 0表示没有宝贝，1表示有宝贝

					MbApplication.getGlobalData().getNowuser().setUserid(userid);
					MbApplication.getGlobalData().getNowuser().setHavingbaby(havingbaby);
					MbApplication.getGlobalData().getNowuser().setAdminphone(phone);
					// 判断该用户是否有宝贝，如果有就跳到宝贝列表，没有就跳到扫描页面;
					if (havingbaby == 1) {
						isadmin();
						if (isadmin) {

							// 将是否为管理员储存在全局变量中
							MbApplication.getGlobalData().getNowuser().setIsadmin(true);
							// 用户为管理员
							JSONArray authlist = datajson
									.getJSONArray("authlist");
							if (authlist.length() != 0) {
								// 将待审核的列表传到NowUser里面

								ArrayList<Authlist> authlistarray = new ArrayList<Authlist>();// 待审核列表

								for (int i = 0; i < authlist.length(); i++) {
									Authlist empt = new Authlist();
									JSONObject emptjson = authlist
											.getJSONObject(i);
									empt.setImei(emptjson.getString("imei"));
									empt.setUserid(emptjson.getString("userid"));
									empt.setPhone(emptjson.getString("phone"));
									empt.setRelate(emptjson.getString("relate"));
									empt.setTime(emptjson.getString("time"));
									authlistarray.add(empt);
								}

								MbApplication.getGlobalData().getNowuser().setAuthlistarray(
										authlistarray);
								
								// 发送消息处理待审核列表
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 510;
								msg.arg2 = arg2;
								handel.sendMessage(msg);

							} else {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 500;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
								
								Intent intent = new Intent(LoginActivity.this,
										BabyFragmentActivity.class);
								startActivity(intent);
								finish();
							}

						} else {

							// 将是否为管理员储存在全局变量中
							MbApplication.getGlobalData().getNowuser().setIsadmin(false);
							// 不是管理员直接登陆的则登陆成功
							
							Message msg = Message.obtain();
							msg.what = what;
							msg.arg1 = 500;
							msg.arg2 = arg2;
							handel.sendMessage(msg);
							
							Intent intent = new Intent(LoginActivity.this,
									BabyFragmentActivity.class);
							startActivity(intent);
							finish();
						}

						final ArrayList<Baby> babyList = BabyLocateServer
								.getBabyList(userid);
						if (babyList != null) {

							if (babyList.size() == 0) {
								startActivity(new Intent(LoginActivity.this,
										ActiveWatchActivity.class));
								return;
							}
							Baby baby = babyList.get(0);
							imei = baby.getBabyimei();
							getlocalinterval();
						}

					} else {

						JSONArray authresult = datajson
								.getJSONArray("authresult");
						if (authresult.length() > 0) {
							if (authresult.getJSONObject(0).getString("result")
									.equals("failed")) {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 502;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
							} else {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 500;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
							}

						} else {
							JSONArray authing = datajson
									.getJSONArray("authing");
							if (authing.length() == 0) {
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 480;
								msg.arg2 = arg2;
								handel.sendMessage(msg);

							} else {
								adminphone = authing.getJSONObject(0)
										.getString("phone");
								Message msg = Message.obtain();
								msg.what = what;
								msg.arg1 = 501;
								msg.arg2 = arg2;
								handel.sendMessage(msg);
							}

						}

						// 扫描页面也需要userid，故传递过去
						/*
						 * Intent intent = new Intent(LoginActivity.this,
						 * ActiveWatchActivity.class); Bundle bundle = new
						 * Bundle(); bundle.putString("userid", userid);
						 * intent.putExtras(bundle); startActivity(intent);
						 */
					}

				} else {
					arg2 = -1;
					erro = result.getString("msg");
					if (erro.equals("10013")) {
						System.out.println("token:" + token);

						MbApplication.getGlobalData().setToken(token);
						Message msg = Message.obtain();
						msg.what = what;
						msg.arg1 = 444;
						msg.arg2 = arg2;
						handel.sendMessage(msg);
						return;
					}
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 500;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 500;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
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

	protected void getlocalinterval() {
		// TODO 获取定位间隔
		HttpPost request = new HttpPost(URL_GETLOCALINTERVAL);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", MbApplication.getGlobalData().getNowuser().getImei());

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {

				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 成功后摇干啥这里搞

					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);

					// 取出服务端返回的间隔
					int intervalminute = datajson.getInt("minute");
					MbApplication.getGlobalData().setIntervalminute(intervalminute);

				} else {
					// 失败后摇干啥这里搞

					/*
					 * Toast.makeText(context, "获取步数失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {

				// 发送广播

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

	@Override
	public void onError(Platform platform, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		// 清除QQ登陆信息
		platform.removeAccount();

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

				if (key == 498 && arg2 == 1) {
					dismissDialog();
					btnLogIn.setClickable(true);
					Toast.makeText(LoginActivity.this,
							getResources().getString(R.string.login_success),
							Toast.LENGTH_LONG).show();
				}
				if (key == 444 && arg2 == -1) {

					btnLogIn.setClickable(true);
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.token_notban), 3000).show();
					Intent intent = new Intent(LoginActivity.this,
							LogInfoActivity.class);
					startActivity(intent);
					mainlayout.setVisibility(View.VISIBLE);

				}

				if (key == 500 && arg2 == 1) {
					dismissDialog();
					btnLogIn.setClickable(true);
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.loginsuccess), 2000).show();
				}
				if (key == 480 && arg2 == 1) {
					dismissDialog();
					btnLogIn.setClickable(true);
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.login_nobaby), 3000).show();
					Intent intent = new Intent(LoginActivity.this,
							ActiveWatchActivity.class);
					startActivity(intent);
					mainlayout.setVisibility(View.VISIBLE);
				}

				if (key == 501 && arg2 == 1) {
					dismissDialog();
					btnLogIn.setClickable(true);
					mainlayout.setVisibility(View.VISIBLE);
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.login_shenhe1)
									+ adminphone
									+ LoginActivity.this.getResources()
											.getString(R.string.login_shenhe2),
							2000).show();
				}

				if (key == 502 && arg2 == 1) {
					dismissDialog();
					btnLogIn.setClickable(true);
					mainlayout.setVisibility(View.VISIBLE);
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.login_disavow), 3000).show();
					Intent intent = new Intent(LoginActivity.this,
							ActiveWatchActivity.class);
					startActivity(intent);
				}

				if (key == 500 && arg2 == -1) {
					dismissDialog();
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(LoginActivity.this);
					erromsg=change.chang(erro);
					btnLogIn.setClickable(true);
					mainlayout.setVisibility(View.VISIBLE);
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.login_fail)
									+ erromsg, 3000).show();
				}

				if (key == 510 && arg2 == 1) {
					dismissDialog();
					btnLogIn.setClickable(true);

					// 弹出列表

					// 要显示的用户号码
					ArrayList<Authlist> arraylist = MbApplication.getGlobalData().getNowuser()
							.getAuthlistarray();
					for (int i = 0; i < arraylist.size(); i++) {
						final SetStepGoalDialog authdialog = new SetStepGoalDialog(
								LoginActivity.this);
						String phone = arraylist.get(i).getPhone();
						String userid = arraylist.get(i).getUserid();
						String imei = arraylist.get(i).getImei();
						boolean islast = false;
						if (i == arraylist.size() - 1) {
							islast = true;
						}
						authdialog.ChangeToSetauthlist(phone, userid, imei,
								islast);
						authdialog
								.setbtnconfirmauthclickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub

										new Thread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												confirmauth(
														authdialog.getUserid(),
														authdialog.getImei());
												if (authdialog.isIslast()) {
													Intent intent = new Intent(
															LoginActivity.this,
															BabyFragmentActivity.class);
													intent.putExtra("ifadmin",isadmin);
													startActivity(intent);
													finish();
												}
											}
										}).start();
										authdialog.dismiss();
									}
								});

						authdialog
								.setbtndisavowclickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										new Thread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												disavow(authdialog.getUserid(),
														authdialog.getImei());
												if (authdialog.isIslast()) {

													Intent intent = new Intent(
															LoginActivity.this,
															BabyFragmentActivity.class);
													intent.putExtra("ifadmin",isadmin);
													startActivity(intent);
													finish();
												}
											}
										}).start();
										authdialog.dismiss();
									}
								});

						authdialog.show();

					}

				}

			} else {
				dismissDialog();
				btnLogIn.setClickable(true);
				mainlayout.setVisibility(View.VISIBLE);
				Toast.makeText(LoginActivity.this,
						getString(R.string.serverbusy), 3000).show();
			}

		}

	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getString(R.string.logining));
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityContainer.getInstance().finshActivity("LoginActivity");
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 解析域名，获取ip
	public void GetIp() {

		SharedPreferences settings = getSharedPreferences("saveip", 0);
		final SharedPreferences.Editor editor = settings.edit();

		// 开启新线程，防止数据过大卡死
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpGet request = new HttpGet(
						"http://119.29.29.29/d?dn=hedy.ios16.com.");
				// 发送请求
				HttpResponse Response;
				try {
					Response = new DefaultHttpClient().execute(request);
					int res = Response.getStatusLine().getStatusCode();

					if (res == 200) {
						// 得到应答的字符串，这也是一个 JSON 格式保存的数据
						String result = EntityUtils.toString(Response
								.getEntity());

						Log.i("keis", "d+解析的ip：" + result);

						dnspodIp = result;

					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();

	}

}
