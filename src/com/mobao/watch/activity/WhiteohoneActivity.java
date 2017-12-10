package com.mobao.watch.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.LoginActivity.myhandel;
import com.mobao.watch.adapter.WhitephoneAdapter;
import com.mobao.watch.bean.Authlist;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.AddWhitephoneDialog;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.SetStepGoalDialog;
import com.testin.agent.TestinAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WhiteohoneActivity extends Activity {
	private ListView phonelist;// 白名单列表
	private WhitephoneAdapter adapter;
	private RelativeLayout add;// 添加按钮
	private ImageButton back;// 返回按钮
	private String userid;// 用户ID
	private boolean isadmin;// 是否管理员
	private String URL_GETWHITELIST = CommonUtil.baseUrl + "getwhitelist";// 获取白名单的url
	private String URL_ADDWHITELIST = CommonUtil.baseUrl + "addwhitelist";// 添加白名单的url
	private String URL_DELWHITELIST = CommonUtil.baseUrl + "delwhitelist";// 删除白名单的url
	private String[] jsonarray = {};// 白名单号码数组
	private ProgressDialog progDialog = null; // 圆形进度条

	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whitephone);

		// 初始化Testin崩溃分析
		TestinAgent.init(this);

		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_GETWHITELIST = "http://" + ip + ":8088/api/getwhitelist";// 获取白名单的url
			URL_ADDWHITELIST = "http://" + ip + ":8088/api/addwhitelist";// 添加白名单的url
			URL_DELWHITELIST = "http://" + ip + ":8088/api/delwhitelist";// 删除白名单的url
		}

		progDialog = new ProgressDialog(WhiteohoneActivity.this);
		userid = MbApplication.getGlobalData().getNowuser().getUserid();
		isadmin = MbApplication.getGlobalData().getNowuser().isIsadmin();
		if (isNetworkConnected(WhiteohoneActivity.this) != true) {
			Toast.makeText(WhiteohoneActivity.this,
					getString(R.string.networkunusable), 3000).show();
			initview();
			return;
		}
		// 获取白名单
		showDialog();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getwhitelist();
			}
		}).start();

	}

	private void initview() {
		// TODO Auto-generated method stub
		phonelist = (ListView) findViewById(R.id.white_listView);
		adapter = new WhitephoneAdapter(WhiteohoneActivity.this, jsonarray);
		phonelist.setAdapter(adapter);
		// 是管理员才可以删除白名单
		if (isadmin) {
			phonelist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int index, long arg3) {
					// TODO Auto-generated method stub
					final AddWhitephoneDialog dialog = new AddWhitephoneDialog(
							WhiteohoneActivity.this,
							R.style.chatfragment_call_dialog_style);
					final String nowphone = jsonarray[index];
					dialog.ChangeToDelWhitephone(nowphone);
					dialog.setSureBtOnclickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							if (isNetworkConnected(WhiteohoneActivity.this) != true) {
								Toast.makeText(WhiteohoneActivity.this,
										getString(R.string.networkunusable),
										3000).show();
								return;
							}
							showDialog();
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									delwhitelist(nowphone);
								}
							}).start();

						}
					});
					dialog.setCancelBtOnclickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					dialog.show();

				}
			});
		}

		add = (RelativeLayout) findViewById(R.id.layout_btn_add);
		if (isadmin) {
			add.setVisibility(View.VISIBLE);
			add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					final AddWhitephoneDialog dialog = new AddWhitephoneDialog(
							WhiteohoneActivity.this,
							R.style.chatfragment_call_dialog_style);
					dialog.setAddBtOnclickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							if (isNetworkConnected(WhiteohoneActivity.this) != true) {
								Toast.makeText(WhiteohoneActivity.this,
										getString(R.string.networkunusable),
										3000).show();
								return;
							}
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
			add.setVisibility(View.GONE);
		}

		back = (ImageButton) findViewById(R.id.btnBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityContainer.getInstance().finshActivity("WhiteohoneActivity");
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	protected void delwhitelist(String phone) {
		// TODO 删除白名单
		HttpPost request = new HttpPost(URL_DELWHITELIST);
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
					msg.arg1 = 557;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 557;
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
				msg.arg1 = 557;
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

	protected void getwhitelist() {
		// TODO 获取白名单
		HttpPost request = new HttpPost(URL_GETWHITELIST);
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
				System.out.println(result.toString());
				if (status == 200) {
					arg2 = 1;
					// 将获取到的数据存起来
					JSONArray data = result.getJSONArray("data");
					jsonarray = new String[data.length()];
					for (int i = 0; i < data.length(); i++) {
						jsonarray[i] = data.getString(i);
					}
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

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getString(R.string.pleasewait));
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

	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;
			if (lj == 1) {
				if (key == 555 && g2 == 1) {
					initview();
					dismissDialog();
					Toast.makeText(
							WhiteohoneActivity.this,
							WhiteohoneActivity.this.getResources().getString(
									R.string.gethitephonesuccess), 3000).show();
				}

				if (key == 555 && g2 == -1) {
					dismissDialog();
					String erromsg = geterror(erro);
					Toast.makeText(
							WhiteohoneActivity.this,
							WhiteohoneActivity.this.getResources().getString(
									R.string.login_fail)
									+ erromsg, 3000).show();

				}

				if (key == 556 && g2 == 1) {
					getwhitelist();
					Toast.makeText(
							WhiteohoneActivity.this,
							WhiteohoneActivity.this.getResources().getString(
									R.string.addhitephonesuccess), 3000).show();
				}

				if (key == 556 && g2 == -1) {
					dismissDialog();
					String erromsg = geterror(erro);
					Toast.makeText(
							WhiteohoneActivity.this,
							WhiteohoneActivity.this.getResources().getString(
									R.string.addhitephonefail)
									+ erromsg, 3000).show();
				}

				if (key == 557 && g2 == 1) {
					getwhitelist();
					Toast.makeText(
							WhiteohoneActivity.this,
							WhiteohoneActivity.this.getResources().getString(
									R.string.delhitephonesuccess), 3000).show();
				}

				if (key == 557 && g2 == -1) {
					dismissDialog();
					String erromsg = geterror(erro);
					Toast.makeText(
							WhiteohoneActivity.this,
							WhiteohoneActivity.this.getResources().getString(
									R.string.delhitephonefail)
									+ erromsg, 3000).show();
				}

			} else {
				dismissDialog();
				Toast.makeText(WhiteohoneActivity.this,
						getString(R.string.networkunusable), 3000).show();
			}

		}

	}

	public String geterror(String erro2) {
		// TODO Auto-generated method stub
		String erromsg = "未知错误";
		switch (Integer.parseInt(erro2)) {
		case 10010:
			erromsg = getResources().getString(R.string.for10010);
			break;

		case 10011:
			erromsg = getResources().getString(R.string.for10011);
			break;

		case 10012:
			erromsg = getResources().getString(R.string.for10012);
			break;

		case 10013:
			erromsg = getResources().getString(R.string.for10013);
			break;

		case 10021:
			erromsg = getResources().getString(R.string.for10021);
			break;

		case 10022:
			erromsg = getResources().getString(R.string.for10022);
			break;

		case 10023:
			erromsg = getResources().getString(R.string.for10023);
			break;

		case 10024:
			erromsg = getResources().getString(R.string.for10024);
			break;

		case 10030:
			erromsg = getResources().getString(R.string.for10030);
			break;

		case 10031:
			erromsg = getResources().getString(R.string.for10031);
			break;

		case 10040:
			erromsg = getResources().getString(R.string.for10040);
			break;

		case 10041:
			erromsg = getResources().getString(R.string.for10041);
			break;

		case 10042:
			erromsg = getResources().getString(R.string.for10042);
			break;

		case 10043:
			erromsg = getResources().getString(R.string.for10043);
			break;

		case 10050:
			erromsg = getResources().getString(R.string.for10050);
			break;

		case 10051:
			erromsg = getResources().getString(R.string.for10051);
			break;

		case 10060:
			erromsg = getResources().getString(R.string.for10060);
			break;

		case 10061:
			erromsg = getResources().getString(R.string.for10061);
			break;
		case 20000:
			erromsg = getResources().getString(R.string.for20000);
			break;
		case 20001:
			erromsg = getResources().getString(R.string.for20001);
			break;
		case 20002:
			erromsg = getResources().getString(R.string.for20002);
			break;

		case 20003:
			erromsg = getResources().getString(R.string.for20003);
			break;

		default:
			break;
		}
		return erromsg;
	}

}
