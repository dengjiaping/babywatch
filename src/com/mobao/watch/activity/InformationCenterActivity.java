package com.mobao.watch.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.amap.api.maps2d.model.LatLng;
import com.mb.zjwb1.R;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.adapter.InfoCenterListAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.InformationCenter;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.view.InformationCenterListView;
import com.mobao.watch.view.InformationCenterListView.IXListViewListener;
import com.testin.agent.TestinAgent;

/**
 * @author 斌丰 消息中心界面
 */
public class InformationCenterActivity extends Activity implements
		IXListViewListener {

	// 根据第一个界面的宝贝值查找记录
	// String imei = LocationFragment.now_babyimei;
	@SuppressWarnings("static-access")
	private String imei = MbApplication.getGlobalData().getNowuser().getImei();

	// 圆形进度条
	private ProgressDialog progDialog = null;

	// 接受线程发送的消息
	private Handler myHandler;
	private int MSG_FROM_NET = 1001;
	private int MSG_FROM_NODATA = 1002;

	// 返回
	private ImageButton btn_back;

	// sos记录adapter
	private InfoCenterListAdapter infolistadapter;
	// 获取sos数据集
	private ArrayList<InformationCenter> infocenter_info = new ArrayList<InformationCenter>();
	private InformationCenter info;

	// 连接地址
	private String url = CommonUtil.baseUrl + "messagecenter";

	// 用户id
	String userid = MbApplication.getGlobalData().getNowuser().getUserid();

	// 查看数据页数
	private int page = 0;

	// 尝试自己构建ListView
	private InformationCenterListView mListView;
	private Handler mHandler;
	
	//宝贝切换
	private PopupWindow window; // popupWindow
	private Activity activity;
	public static RelativeLayout rel_select_baby;
	public static TextView text_select_baby;
	private RelativeLayout rel_center; // 中间布局
	private RelativeLayout rel;// 顶部布局
	public static List<Baby> groups;// 宝贝列表
	private String now_babyimei = null;// 当前宝贝id
	public static Baby now_baby = null;// 当前宝贝
	private ListView lv_group; // popupWindow子view
	private GroupAdapter groupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);

		setContentView(R.layout.information_center_activity);

		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			url = "http://" + ip + ":8088/api/messagecenter";
		}

		if(getIntent().getStringExtra("imei")==null||getIntent().getStringExtra("imei").equals("")){
			
		System.out.println("没有获取到");
		NotificationManager nm =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1001);

		}else{
			imei= getIntent().getStringExtra("imei");
			System.out.println("有获取到");
		}
		// 设置缓存
		progDialog = new ProgressDialog(getLayoutInflater().getContext());
		/*//<宝贝切换的组件>
				activity = InformationCenterActivity.this;
				rel_select_baby = (RelativeLayout)findViewById(R.id.rel_select_baby);
				text_select_baby = (TextView) findViewById(R.id.text_select_baby);
				rel_center = (RelativeLayout)findViewById(R.id.rel_center);
				rel = (RelativeLayout)findViewById(R.id.rel_top);
				initBabyPopuWindow();
				//<\宝贝切换的组件>
*/		
		
		// 连接服务端,取数据
		TestNet();

		// 显示数据
		TestinitList();

		// Handler取线程的数据
		initHandler();

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});

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
									// 将当前宝贝的imei存到全局变量里
									MbApplication.getGlobalData().getNowuser()
											.setImei(now_babyimei);
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
						TestNet();
						//发送广播
						activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));



					}
				});
			}
	private void TestinitList() {

		mListView = (InformationCenterListView) findViewById(R.id.list_info);
		mListView.setPullLoadEnable(true);
		infolistadapter = new InfoCenterListAdapter(this, infocenter_info);
		mListView.setAdapter(infolistadapter);
		// 设置上拉加载不可用
		// mListView.setPullLoadEnable(false);
		// 设置下拉刷新不可用
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mHandler = new Handler();

	}

	// 接受消息
	private void initHandler() {
		myHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				switch (msg.what) {
				case 1001:
					info = (InformationCenter) msg.obj;
					// 加载数据
					infocenter_info.add(info);
					// 刷新列表
					dismissDialog();// 销毁缓存圆
					infolistadapter.notifyDataSetChanged();
					break;
				case 1002:
					Toast.makeText(InformationCenterActivity.this,
							getString(R.string.nodata), 3000).show();
					// 设置上拉加载不可用（防止爆点）
					mListView.setPullLoadEnable(false);
					break;
				case 2001:
					Toast.makeText(InformationCenterActivity.this,
							getString(R.string.nonetwork), 3000).show();
					break;
				case 2002:
					Toast.makeText(InformationCenterActivity.this,
							getString(R.string.nonetwork), 3000).show();
					break;

				default:
					break;
				}
				return false;
			}

		});
	}

	// 连接服务端，取数据
	private void TestNet() {
		showDialog();// 缓存圆
		// 开启新线程，防止数据过大卡死
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpPost request = new HttpPost(url);
				// 封装一个json
				JSONObject jsonsos = new JSONObject();

				try {
					// 传递参数
					jsonsos.put("imei", imei);
					jsonsos.put("page", page);

					// 绑定到请求 Entry
					StringEntity se = new StringEntity(jsonsos.toString());
					System.out.println("请求消息中心："+jsonsos.toString());
					request.setEntity(se);
                
					// 发送请求
					HttpResponse Response = new DefaultHttpClient()
							.execute(request);
					// 判断服务器是否连接成功
					int res = Response.getStatusLine().getStatusCode();

					if (res == 200) {
						// 得到应答的字符串，这也是一个 JSON 格式保存的数据
						String result = EntityUtils.toString(Response
								.getEntity());
						// 生成json对象
						JSONObject sosResult = new JSONObject(result);
						// 判断登陆是否成功
						int status = sosResult.getInt("status");

						if (status == 200) {

							// 将data再封装成一个json（data为一个json）
							String data = sosResult.getString("data");

							JSONArray datajson = new JSONArray(data);

							if (datajson.length() == 0) {

								Log.i("keis", "亲没有数据");

								Message msg = Message.obtain();
								msg.what = MSG_FROM_NODATA;
								msg.obj = datajson.length();
								myHandler.sendMessage(msg);
								dismissDialog();// 没有数据，直接销毁缓冲图标
							}
							for (int i = 0; i < datajson.length(); i++) {

								JSONObject infocenter = datajson
										.getJSONObject(i);

							/*	String address = infocenter
										.getString("address");
								String condition = infocenter
										.getString("condition");
								String lon=infocenter
										.getString("lon");
								String lat=infocenter
										.getString("lat");
								*/
								String date = infocenter.getString("date");
								String hour = infocenter.getString("hour");
								String minute = infocenter.getString("minute");
								String type=infocenter.getString("type");
								System.out.println("消息中心"+infocenter.toString());
								if(type.equals("safe")){
									JSONObject value=infocenter.getJSONObject("value");
									String name=value.getString("name");
									String condition=value.getString("condition");
									String lon=value.getString("lon");
									String lat=value.getString("lat");
									// 封装数据，发送
									info = new InformationCenter(type,name, date,
											condition, hour, minute,lon,lat);
									
								}else{
									if(type.equals("voltage")){
										String value_voltage=getResources().getString(R.string.voltage)+infocenter.getString("value")+"%";
										// 封装数据，发送
										info = new InformationCenter(type,value_voltage, date,
												null, hour, minute,null,null);
									}else{
										String value_dial=getResources().getString(R.string.dial)+infocenter.getString("value");
										// 封装数据，发送
										info = new InformationCenter(type,value_dial, date,
												null, hour, minute,null,null);
									}
								}
								

								int m = Integer.parseInt(minute);
								if (m < 10) {
									minute = "0" + m;
								}

/*								// 封装数据，发送
								info = new InformationCenter(address, date,
										condition, hour, minute,lon,lat);*/

/*								Log.i("keis", "地址：" + address + " 状态:"
										+ condition + "   发生时间:" + date
										+ "  时间：" + hour + ":" + minute+"lon:"+lon+"lat:"+lat);*/

								Message msg = Message.obtain();
								msg.what = MSG_FROM_NET;
								msg.obj = info;
								myHandler.sendMessage(msg);

							}

						} else if (status == 300) {

							Message msg = Message.obtain();
							msg.what = 2002;
							myHandler.sendMessage(msg);
							String msg1 = sosResult.getString("msg");
							Log.i("keis", "异常msg=" + msg1);
						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i("keis", "1");
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					Log.i("keis", "2");
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					Log.i("keis", "3");
					e.printStackTrace();
				} catch (IOException e) {
					Log.i("keis", "4");
					Message msg = Message.obtain();
					msg.what = 2001;
					myHandler.sendMessage(msg);
					e.printStackTrace();
				} finally {
					dismissDialog();
				}

			}
		}).start();

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

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobao.watch.view.InformationCenterListView.IXListViewListener#onRefresh
	 * () 下拉刷新
	 */
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				// 加载一页数据
				page++;
				// 连接服务端拿的数据
				TestNet();

				infolistadapter.notifyDataSetChanged();

				onLoad();
			}
		}, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobao.watch.view.InformationCenterListView.IXListViewListener#onLoadMore
	 * () 上拉加载更多
	 */
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				// 加载一页数据
				page++;
				// 连接服务端拿的数据
				TestNet();

				onLoad();
			}
		}, 0);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
	
}
