package com.mobao.watch.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.WhiteohoneActivity.myhandel;
import com.mobao.watch.adapter.SOSphoneAdapter;
import com.mobao.watch.adapter.WhitephoneAdapter;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.AddWhitephoneDialog;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.Utility;
import com.mobao.watch.util.WaitDialog;

public class PhoneNumberManagerActivity extends Activity {
	private ProgressDialog progDialog = null; // 圆形进度条
	private TextView text_watch_quick;// 速波号码
	private boolean isadmin;// 是否管理员
	private ListView whitephonelist;// 白名单列表
	private ListView lv_watch_number_manager_SOS_phone;//SOS列表
	private RelativeLayout addwhitelist;// 添加白名单
	private WhitephoneAdapter adapter;// 白名单列表适配器
	private SOSphoneAdapter  sosadapter;// sos电话号码适配器
	private String URL_GETALLNUMBER = CommonUtil.baseUrl + "familyphone";// 获取家庭圈号码的url
	private String URL_ADDWHITELIST = CommonUtil.baseUrl + "addwhitelist";// 添加白名单的url
	private String URL_DELWHITELIST = CommonUtil.baseUrl + "delwhitelist";// 删除白名单的url
	private String userid;// 用户ID
	private ErroNumberChange change;// 错误码转译工具类
	private String[] jsonarray = {};// 白名单号码数组
	private String[] sosphone={};//sos电话号码
	private String quick_phone;//速拨号码
	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no

	/* 伍建鹏///////////////////////// */
	private ImageView ivBack;

	/* ////////////////////////////// */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watch_number_manager_activity);

		// D+的处理
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_GETALLNUMBER = "http://" + ip + ":8088/api/familyphone";// 获取家庭圈号码的url
			URL_ADDWHITELIST = "http://" + ip + ":8088/api/addwhitelist";// 添加白名单的url
			URL_DELWHITELIST = "http://" + ip + ":8088/api/delwhitelist";// 删除白名单的url
		}

		userid = MbApplication.getGlobalData().getNowuser().getUserid();
		isadmin = BabyServer.isAdmin(userid);
		progDialog=new ProgressDialog(PhoneNumberManagerActivity.this);
		// 获取白名单
		change = new ErroNumberChange(PhoneNumberManagerActivity.this);
		initview();

		// 注册数据变化的广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("CHANGE_PhoneNumberData_ACTION");
		registerReceiver(mBroadcastReceiver, myIntentFilter);

		/* 伍建鹏////////////////////////////// */
		addListener();
		/* /////////////////////////////// */

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initview() {
		// TODO Auto-generated method stub

		/* 伍建鹏///////////////////////// */
		ivBack = (ImageView) findViewById(R.id.iv_watch_number_manager_back);
		/* ////////////////////////////// */

		whitephonelist = (ListView) findViewById(R.id.lv_watch_number_manager_write_name);
		lv_watch_number_manager_SOS_phone=(ListView) findViewById(R.id.lv_watch_number_manager_SOS_phone);
		text_watch_quick = (TextView) findViewById(R.id.text_watch_quick);
		text_watch_quick.setText(MbApplication.getGlobalData().getNowuser()
				.getAdminphone());
		showDialog();
		// 加载白名单
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getwhitelist();
			}

		}).start();

		addwhitelist = (RelativeLayout) findViewById(R.id.rl_watch_number_manager_write_name);
		if (isadmin) {
			addwhitelist.setVisibility(View.VISIBLE);
			addwhitelist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
				
					final AddWhitephoneDialog dialog = new AddWhitephoneDialog(
							PhoneNumberManagerActivity.this,
							R.style.chatfragment_call_dialog_style);
					dialog.setAddBtOnclickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							showDialog();
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									addwhitelist(dialog.getInputText());
								}
							}).start();

						}
					});
					dialog.show();

				}
			});
		} else {
			addwhitelist.setVisibility(View.GONE);
		}

	}

	protected void addwhitelist(String phone) {
		// TODO 添加白名单
		HttpPost request = new HttpPost(URL_ADDWHITELIST);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", userid);
			param.put("phone", phone);

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
				// 判断获取是否成功
				int status = result.getInt("status");
				System.out.println(result.toString());
				if (status == 200) {
					arg2 = 1;
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 556;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 556;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 556;
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

	private void getwhitelist() {
		// TODO 获取白名单
		HttpPost request = new HttpPost(URL_GETALLNUMBER);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", userid);

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
				// 判断获取是否成功
				int status = result.getInt("status");
				if (status == 200) {
					arg2 = 1;
					// 将获取到的数据存起来
					JSONObject data = result.getJSONObject("data");
					//获取白名单号码
					JSONArray whitedata=data.getJSONArray("white");
					jsonarray = new String[whitedata.length()];
					for (int i = 0; i < whitedata.length(); i++) {
						JSONObject temp=whitedata.getJSONObject(i);
						if(temp.getString("erasable").equals("0")){
							jsonarray[i] = temp.getString("phone")+"x";
						}else{
							jsonarray[i] = temp.getString("phone");
						}
						
					}
					//获取SOS电话号码
					JSONArray sosdata=data.getJSONArray("sos");
					sosphone=new String[sosdata.length()];
					for (int i = 0; i < sosdata.length(); i++) {
						sosphone[i] = sosdata.getString(i);
					}
					//获取速拨号码
					JSONArray dialdata=data.getJSONArray("dial");
					quick_phone=dialdata.getString(0);

					System.out.println("白名单："+Arrays.toString(jsonarray)+"||SOS号码："+Arrays.toString(sosphone)+"||速拨号码："+quick_phone);
					// jsonarray = String.data;
					// 发生消息将数据展现出来
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 555;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 555;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 555;
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

	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;
			dismissDialog();
			if (lj == 1) {
				if (key == 555 && g2 == 1) {
					text_watch_quick.setText(quick_phone);
					adapter = new WhitephoneAdapter(
							PhoneNumberManagerActivity.this, jsonarray);
					whitephonelist.setAdapter(adapter);
					sosadapter=new SOSphoneAdapter(PhoneNumberManagerActivity.this, sosphone);
					lv_watch_number_manager_SOS_phone.setAdapter(sosadapter);
					Utility.setListViewHeightBasedOnChildren(whitephonelist);
					Utility.setListViewHeightBasedOnChildren(lv_watch_number_manager_SOS_phone);
					Toast.makeText(
							PhoneNumberManagerActivity.this,
							PhoneNumberManagerActivity.this.getResources()
									.getString(R.string.gethitephonesuccess),
							3000).show();
				}

				if (key == 555 && g2 == -1) {
					String erromsg = change.chang(erro);
					Toast.makeText(
							PhoneNumberManagerActivity.this,
							PhoneNumberManagerActivity.this.getResources()
									.getString(R.string.login_fail) + erromsg,
							3000).show();

				}

				if (key == 556 && g2 == 1) {
					getwhitelist();
					Toast.makeText(
							PhoneNumberManagerActivity.this,
							PhoneNumberManagerActivity.this.getResources()
									.getString(R.string.addhitephonesuccess),
							3000).show();
				}

				if (key == 556 && g2 == -1) {
					String erromsg = change.chang(erro);
					Toast.makeText(
							PhoneNumberManagerActivity.this,
							PhoneNumberManagerActivity.this.getResources()
									.getString(R.string.addhitephonefail)
									+ erromsg, 3000).show();
				}

				if (key == 557 && g2 == 1) {
					getwhitelist();
					Toast.makeText(
							PhoneNumberManagerActivity.this,
							PhoneNumberManagerActivity.this.getResources()
									.getString(R.string.delhitephonesuccess),
							3000).show();
				}

				if (key == 557 && g2 == -1) {
					;
					String erromsg = change.chang(erro);
					Toast.makeText(
							PhoneNumberManagerActivity.this,
							PhoneNumberManagerActivity.this.getResources()
									.getString(R.string.delhitephonefail)
									+ erromsg, 3000).show();
				}

			} else {

				Toast.makeText(PhoneNumberManagerActivity.this,
						getString(R.string.networkunusable), 3000).show();
			}

		}

	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 接收宝贝切换的广播
			if (action.equals("CHANGE_PhoneNumberData_ACTION")) {
				getwhitelist();
			}
		}
	};

	private void addListener() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PhoneNumberManagerActivity.this.finish();
			}
		});
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getString(R.string.gettingdata));
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
}
