package com.mobao.watch.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import android.content.Intent;
import android.graphics.EmbossMaskFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.PowerSaveActivity.myhandel;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.adapter.MutetimeListviewAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

public class Mutetime_ManageActivity extends Activity {
	private ProgressDialog progDialog = null; // 圆形进度条
	private ImageButton btn_back;
	private Boolean bnt_pd = false;
	private RelativeLayout layout_mutetime_1;
	private RelativeLayout layout_mutetime_2;
	private RelativeLayout layout_mutetime_3;
	private LinearLayout mutetime_data_timedata_1;
	private LinearLayout mutetime_data_timedata_2;
	private LinearLayout mutetime_data_timedata_3;
	private CheckBox checkbox_mutetime_1;
	private CheckBox checkbox_mutetime_2;
	private CheckBox checkbox_mutetime_3;
	private TextView mutetime_manage_data_time_1;
	private TextView mutetime_manage_data_time_2;
	private TextView mutetime_manage_data_time_3;
	private TextView mutetime_manage_week_1;
	private TextView mutetime_manage_week_2;
	private TextView mutetime_manage_week_3;
	private String URL_GETSILENT = CommonUtil.baseUrl + "getsilent";// 获取静音时段
	private String URL_DELSILENTT = CommonUtil.baseUrl + "delsilent";// 删除静音时段
	private Boolean isfrist = false;
	String mutetimeweek = "";// 重复
	int num = 0;// 组别
	String startime;// 开始时间
	String endtime;// 结束时间

	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	Thread duqu;
	
	
	//宝贝切换
		private PopupWindow window; // popupWindow
		private Activity activity;
		public static RelativeLayout rel_select_baby;
		public static TextView text_select_baby;
		private LinearLayout rel_center; // 中间布局
		private RelativeLayout rel;// 顶部布局
		public static List<Baby> groups;// 宝贝列表
		private String now_babyimei = null;// 当前宝贝id
		public static Baby now_baby = null;// 当前宝贝
		private ListView lv_group; // popupWindow子view
		private GroupAdapter groupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.mutetime_manage);

		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_GETSILENT = "http://" + ip + ":8088/api/getsilent";// 获取静音时段
			URL_DELSILENTT = "http://" + ip + ":8088/api/delsilent";// 删除静音时段
		}

		progDialog = new ProgressDialog(Mutetime_ManageActivity.this);
		isfrist = true;
		
		/*//<宝贝切换的组件>
				activity = Mutetime_ManageActivity.this;
				rel_select_baby = (RelativeLayout)findViewById(R.id.rel_select_baby);
				text_select_baby = (TextView) findViewById(R.id.text_select_baby);
				rel_center = (LinearLayout)findViewById(R.id.rel_center);
				rel = (RelativeLayout)findViewById(R.id.rel_top);
			    initBabyPopuWindow();	
				//<\宝贝切换的组件>
*/		
		inintview();
		if (!CheckNetworkConnectionUtil.isNetworkConnected(this)) {
			ToastUtil.show(this,
					getResources().getString(R.string.networkunusable));
			isfrist = false;
			return;
		}
		showDialog();

		duqu = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getmutetimedata();

			}
		});
		duqu.start();

	}

	// 初始化宝贝下拉框
			private void initBabyPopuWindow() {
				groups = new ArrayList<Baby>();
				if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
					ToastUtil.show(activity,getResources().getString(R.string.nonetwork));
					return;
				}
				new Thread() {
					public void run() {
						try {
							sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						final ArrayList<Baby> babyList = BabyLocateServer
								.getBabyList(MbApplication.getGlobalData().getNowuser()
										.getUserid());
						if (babyList != null) {
							activity.runOnUiThread(new Runnable() {
								public void run() {
									if(MbApplication.getGlobalData().getGroups()==null){
										groups = babyList;
									}else{
										groups = MbApplication.getGlobalData().getGroups();
										text_select_baby.setText(MbApplication.getGlobalData().getNowBaby().getBabyname());
										now_baby = MbApplication.getGlobalData().getNowBaby();
										now_babyimei = now_baby.getBabyimei();
										// 初始化PopupWindow
										initPopupWindow();
										return;
									}

								
									
									text_select_baby.setText(groups.get(0)
											.getBabyname());
									now_baby = groups.get(0);
									now_babyimei = now_baby.getBabyimei();
									// 将当前宝贝存到全局变量里
									MbApplication.getGlobalData().setNowBaby(now_baby);
									groups.remove(0);

									// 初始化PopupWindow
									initPopupWindow();
								}
							});
						} else {
							activity.runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(
											activity,getResources().getString(
													R.string.serverbusy), 1000).show();
								}
							});
						}
					}
				}.start();
			}

			private void initPopupWindow() {
				rel_select_baby.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//showPopwindow();
					}
				});
			}
			private void showPopwindow() {
				// 利用layoutInflater获得View
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.group_list, null);

				lv_group = (ListView) view.findViewById(R.id.lvGroup);
				// 加载数据

				groupAdapter = new GroupAdapter(activity, groups);
				lv_group.setAdapter(groupAdapter);

				// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
				window = new PopupWindow(view, 300,
						WindowManager.LayoutParams.WRAP_CONTENT);

				// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
				window.setFocusable(true);

				// 实例化一个ColorDrawable颜色为半透明
				ColorDrawable dw = new ColorDrawable(0xb0000000);
				window.setBackgroundDrawable(dw);

				// 设置popWindow的显示和消失动画
				window.setAnimationStyle(R.style.AnimationFade);
				// 在底部显示
				// 获取状态栏高度
				Rect frame = new Rect();
				activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
				int statusBarHeight = frame.top;
				// WindowManager windowManager = (WindowManager)
				// getActivity().getSystemService(Context.WINDOW_SERVICE);
				// int xPos = windowManager.getDefaultDisplay().getWidth() / 2
				// - window.getWidth() / 2+rel.getHeight()+statusBarHeight;
				int y = rel.getHeight() + statusBarHeight;
				// window.showAtLocation(rel,
				// Gravity.CENTER, 0, -xPos);
				window.showAtLocation(rel_center, Gravity.TOP, 0, y);

				lv_group.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView, View view,
							int position, long id) {
						if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
							ToastUtil.show(activity,
									getString(R.string.networkunusable));
							window.dismiss();
							return;
						}
						isfrist=true;
						checkbox_mutetime_1.setChecked(false);
						checkbox_mutetime_2.setChecked(false);
						checkbox_mutetime_3.setChecked(false);
						Baby cur_baby = groups.get(position);
						String lasttext = text_select_baby.getText().toString();
						text_select_baby.setText(groups.get(position).getBabyname());
						now_babyimei = groups.get(position).getBabyimei();
						groups.remove(position);
						groups.add(now_baby);
						now_baby = cur_baby;
						now_babyimei = now_baby.getBabyimei();
						groupAdapter.notifyDataSetChanged();
						if (window != null) {
							window.dismiss();
						}
						// 将当前宝贝的imei存到全局变量里
						MbApplication.getGlobalData().getNowuser()
								.setImei(now_babyimei);
						MbApplication.getGlobalData().setNowBaby(now_baby);
						MbApplication.getGlobalData().setGroups(groups);
						
						showDialog();
						if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
							ToastUtil.show(activity,
									getString(R.string.networkunusable));
							window.dismiss();
							dismissDialog();
							return;
						}
						duqu = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								
								getmutetimedata();

							}
						});
						duqu.start();
						//发送广播
						activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));



					}
				});
			}
	
	private void getmutetimedata() {
		// TODO 获取静音时段
		HttpPost request = new HttpPost(URL_GETSILENT);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", MbApplication.getGlobalData().getNowBaby().getBabyimei());

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
				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 成功后摇干啥这里搞
					arg2 = 1;
					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONArray datajsonarray = new JSONArray(data);
					System.out.println(datajsonarray.toString());

					for (int i = 0; i < datajsonarray.length(); i++) {
						JSONObject emtp = datajsonarray.getJSONObject(i);
						num = emtp.getInt("num");
						String weekarray1 = emtp.getString("weekday");
						String weekarray2 = weekarray1.replace("[\"", "");
						String weekarray3 = weekarray2.replace("\"]", "");
						String[] weekarray = weekarray3.split("\",\"");

						for (int index = 0; index < weekarray.length; index++) {
							if (weekarray[index].equals("1")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week1);
							}
							if (weekarray[index].equals("2")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week2);
							}
							if (weekarray[index].equals("3")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week3);
							}
							if (weekarray[index].equals("4")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week4);
							}
							if (weekarray[index].equals("5")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week5);
							}
							if (weekarray[index].equals("6")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week6);
							}
							if (weekarray[index].equals("7")) {
								mutetimeweek = mutetimeweek
										+ " "
										+ Mutetime_ManageActivity.this
												.getResources().getString(
														R.string.week7);
							}

						}

						startime = emtp.getString("starttime");
						endtime = emtp.getString("endtime");
						
						Message msg = Message.obtain();
						msg.what = what;
						msg.arg1 = 800;
						msg.arg2 = arg2;
						handel.sendMessage(msg);

						try {
							duqu.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// 取出服务端返回的间隔

					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 600;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 600;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

					/*
					 * Toast.makeText(context, "获取步数失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				// 发送广播
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 600;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
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

	private void delmutetimedata() {
		// TODO 删除静音时段
		HttpPost request = new HttpPost(URL_DELSILENTT);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", MbApplication.getGlobalData().getNowBaby().getBabyimei());
			param.put("num", num);
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
				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 成功后摇干啥这里搞
					arg2 = 1;

					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 900;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 900;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

					/*
					 * Toast.makeText(context, "获取步数失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				// 发送广播
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 900;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
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

	private void inintview() {
		// TODO Auto-generated method stub
		mutetime_manage_data_time_1 = (TextView) findViewById(R.id.mutetime_manage_data_time_1);
		mutetime_manage_data_time_2 = (TextView) findViewById(R.id.mutetime_manage_data_time_2);
		mutetime_manage_data_time_3 = (TextView) findViewById(R.id.mutetime_manage_data_time_3);

		mutetime_manage_week_1 = (TextView) findViewById(R.id.mutetime_manage_week_1);
		mutetime_manage_week_2 = (TextView) findViewById(R.id.mutetime_manage_week_2);
		mutetime_manage_week_3 = (TextView) findViewById(R.id.mutetime_manage_week_3);

		mutetime_data_timedata_1 = (LinearLayout) findViewById(R.id.mutetime_data_timedata_1);
		mutetime_data_timedata_2 = (LinearLayout) findViewById(R.id.mutetime_data_timedata_2);
		mutetime_data_timedata_3 = (LinearLayout) findViewById(R.id.mutetime_data_timedata_3);

		layout_mutetime_1 = (RelativeLayout) findViewById(R.id.layout_mutetime_1);
		layout_mutetime_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkbox_mutetime_1.isChecked()) {
					checkbox_mutetime_1.setChecked(false);

				} else {
					checkbox_mutetime_1.setChecked(true);
				}

			}
		});
		layout_mutetime_2 = (RelativeLayout) findViewById(R.id.layout_mutetime_2);
		layout_mutetime_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkbox_mutetime_2.isChecked()) {
					checkbox_mutetime_2.setChecked(false);
				} else {
					checkbox_mutetime_2.setChecked(true);
				}

			}
		});
		layout_mutetime_3 = (RelativeLayout) findViewById(R.id.layout_mutetime_3);
		layout_mutetime_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkbox_mutetime_3.isChecked()) {
					checkbox_mutetime_3.setChecked(false);
				} else {
					checkbox_mutetime_3.setChecked(true);
				}
			}
		});

		checkbox_mutetime_1 = (CheckBox) findViewById(R.id.checkbox_mutetime_1);
		checkbox_mutetime_1
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean ischeck) {
						// TODO Auto-generated method stub
						if (ischeck) {

							mutetime_data_timedata_1
									.setVisibility(View.VISIBLE);
							if (isfrist) {

							} else {

								num = 1;

								MbApplication.getGlobalData().setNow_silent_num(String
										.valueOf(num));
								Intent i = new Intent(
										Mutetime_ManageActivity.this,
										Mutetime_Data_SetActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}

						} else {
							if (!CheckNetworkConnectionUtil
									.isNetworkConnected(Mutetime_ManageActivity.this)) {
								ToastUtil.show(
										Mutetime_ManageActivity.this,
										getResources().getString(
												R.string.networkunusable));

							} else {
								num = 1;
								showDialog();
								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										if(!isfrist){
											delmutetimedata();
										}
									}
								}).start();
								mutetime_data_timedata_1
										.setVisibility(View.GONE);
							}

						}

					}
				});
		checkbox_mutetime_2 = (CheckBox) findViewById(R.id.checkbox_mutetime_2);
		checkbox_mutetime_2
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean ischeck) {
						// TODO Auto-generated method stub
						if (ischeck) {
							mutetime_data_timedata_2
									.setVisibility(View.VISIBLE);
							if (isfrist) {

							} else {

								num = 2;

								MbApplication.getGlobalData().setNow_silent_num(String
										.valueOf(num));
								Intent i = new Intent(
										Mutetime_ManageActivity.this,
										Mutetime_Data_SetActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}
						} else {
							if (!CheckNetworkConnectionUtil
									.isNetworkConnected(Mutetime_ManageActivity.this)) {
								ToastUtil.show(
										Mutetime_ManageActivity.this,
										getResources().getString(
												R.string.networkunusable));

							} else {
								num = 2;
								showDialog();
								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										if(!isfrist){
											delmutetimedata();
										}
										
									}
								}).start();
								mutetime_data_timedata_2
										.setVisibility(View.GONE);

							}

						}
					}
				});
		checkbox_mutetime_3 = (CheckBox) findViewById(R.id.checkbox_mutetime_3);
		checkbox_mutetime_3
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean ischeck) {
						// TODO Auto-generated method stub
						if (ischeck) {
							mutetime_data_timedata_3
									.setVisibility(View.VISIBLE);
							if (isfrist) {

							} else {

								num = 3;
								MbApplication.getGlobalData().setNow_silent_num(String
										.valueOf(num));
								Intent i = new Intent(
										Mutetime_ManageActivity.this,
										Mutetime_Data_SetActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}
						} else {
							if (!CheckNetworkConnectionUtil
									.isNetworkConnected(Mutetime_ManageActivity.this)) {
								ToastUtil.show(
										Mutetime_ManageActivity.this,
										getResources().getString(
												R.string.networkunusable));

							} else {
								num = 3;
								showDialog();
								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										if(!isfrist){
											delmutetimedata();
										}

									}
								}).start();
								mutetime_data_timedata_3
										.setVisibility(View.GONE);
							}

						}
					}
				});
		btn_back = (ImageButton) findViewById(R.id.mute_btn_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});

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
				// 修改VIEW内容
				if (key == 800 && g2 == 1) {

					if (num == 1) {

						mutetime_manage_week_1.setText(mutetimeweek);
						mutetime_manage_data_time_1.setText(startime + "-"
								+ endtime);
						checkbox_mutetime_1.setChecked(true);
						mutetimeweek = "";

					}

					if (num == 2) {

						mutetime_manage_week_2.setText(mutetimeweek);
						mutetime_manage_data_time_2.setText(startime + "-"
								+ endtime);
						checkbox_mutetime_2.setChecked(true);
						mutetimeweek = "";

					}

					if (num == 3) {

						mutetime_manage_week_3.setText(mutetimeweek);
						mutetime_manage_data_time_3.setText(startime + "-"
								+ endtime);
						checkbox_mutetime_3.setChecked(true);
						mutetimeweek = "";

					}

				}

				if (key == 900 && arg2 == 1) {
					dismissDialog();
					Toast.makeText(
							Mutetime_ManageActivity.this,
							Mutetime_ManageActivity.this.getResources()
									.getString(R.string.delmutetimesuccess),
							3000).show();

				}
				if (key == 900 && arg2 == -1) {
					dismissDialog();
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(Mutetime_ManageActivity.this);
					erromsg=change.chang(erro);
					Toast.makeText(
							Mutetime_ManageActivity.this,
							Mutetime_ManageActivity.this.getResources()
									.getString(R.string.delmutetimefail)
									+ erromsg, 3000).show();
					finish();
				}
				if (key == 600 && arg2 == 1) {
					dismissDialog();
					isfrist = false;
					Toast.makeText(
							Mutetime_ManageActivity.this,
							Mutetime_ManageActivity.this.getResources()
									.getString(R.string.getmutetimesuccess),
							3000).show();
				}
				if (key == 600 && arg2 == -1) {
					dismissDialog();
					isfrist = false;
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(Mutetime_ManageActivity.this);
					erromsg=change.chang(erro);
					Toast.makeText(
							Mutetime_ManageActivity.this,
							Mutetime_ManageActivity.this.getResources()
									.getString(R.string.getmutetimefail)
									+ erromsg, 3000).show();
				}

			} else {
				dismissDialog();
				Toast.makeText(
						Mutetime_ManageActivity.this,
						Mutetime_ManageActivity.this.getResources().getString(
								R.string.serverfail), 3000).show();
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
		progDialog.setMessage(Mutetime_ManageActivity.this.getResources()
				.getString(R.string.pleasewait));
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
