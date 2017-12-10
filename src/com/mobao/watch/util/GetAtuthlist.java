package com.mobao.watch.util;

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

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.bean.Authlist;

public class GetAtuthlist {
	private Context context;
	private String URL_AUTHLIST = CommonUtil.baseUrl + "authlist";// 获取待审核列表的url
	private String URL_CONFIRMAUTH = CommonUtil.baseUrl + "confirmauth";// 同意审核的的url
	private String URL_DISAVOW = CommonUtil.baseUrl + "disavow";// 拒绝审核的的url
	
	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	public GetAtuthlist(Context context) {
		this.context = context;
		// 通过D+重组URL
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_AUTHLIST = "http://" + ip + ":8088/api/authlist";// 获取待审核列表的url
			URL_CONFIRMAUTH = "http://" + ip + ":8088/api/confirmauth";// 同意审核的的url
			URL_DISAVOW = "http://" + ip + ":8088/api/disavow";// 拒绝审核的的url
		}

	}

	public void get() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getatuthlist();
			}
		}).start();

	}
	
	private void getatuthlist() {
		// TODO 待审核列表
		HttpPost request = new HttpPost(URL_AUTHLIST);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
                what=1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					arg2=1;
					// 成功后摇干啥这里搞

					// 将data再封装成一个json（data为一个json）
					JSONArray data = result.getJSONArray("data");
					
					
					if (data.length() != 0) {
						// 将待审核的列表传到NowUser里面

						ArrayList<Authlist> authlistarray = new ArrayList<Authlist>();// 待审核列表

						for (int i = 0; i < data.length(); i++) {
							Authlist empt = new Authlist();
							JSONObject emptjson = data
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
						msg.arg1 = 520;
						msg.arg2 = arg2;
						handel.sendMessage(msg);
					}else{
						return;
					}
					
					
					
				

				} else {
					// 失败后摇干啥这里搞
					arg2=-1;
					Message msg = Message.obtain();
					    msg.what = what;
						msg.arg1 = 520;
						msg.arg2 = arg2;
						handel.sendMessage(msg);
					/*
					 * Toast.makeText(context, "获取步数失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				   what=-1;
				   Message msg = Message.obtain();
				   msg.what = what;
					msg.arg1 = 520;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
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
	

	
	private void confirmauth(String userid, String imei) {
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

	private void disavow(String userid, String imei) {
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
	
	
	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;

			if (lj == 1) {
				
				
				if (key == 520 && arg2 == 1) {
					
					// 弹出列表
					ArrayList<Authlist> arraylist = MbApplication.getGlobalData().getNowuser()
							.getAuthlistarray();
					for (int i = 0; i < arraylist.size(); i++) {
						final SetStepGoalDialog authdialog = new SetStepGoalDialog(
								context);
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
													return;
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
													return;
												}
											}
										}).start();
										authdialog.dismiss();
									}
								});
						
						/*伍建鹏/////////////////////////////*/
						//使此dialog能在service中显示
						authdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						/*////////////////////////////////*/
						
						authdialog.show();

					}

				}

			} else {
				
				Toast.makeText(context,context.getString(R.string.serverbusy), 3000).show();
			}

		}

	}
	
}
