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

import com.mb.zjwb1.R;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.adapter.SosRecordListAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.SosRecordInfo;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.view.SosRecordListView;
import com.mobao.watch.view.SosRecordListView.IXListViewListener;
import com.testin.agent.TestinAgent;

/**
 * @author 斌丰 sos求救记录界面
 */
public class SosRecordActivity extends Activity implements IXListViewListener {

	// 根据第一个界面的宝贝值查找记录
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
	private SosRecordListAdapter soslistadapter;
	// 获取sos数据集
	private ArrayList<SosRecordInfo> sos_info = new ArrayList<SosRecordInfo>();
	private SosRecordInfo info;

	// 连接地址
	private String uri = CommonUtil.baseUrl + "getsos";

	// 传递参数月份
	private String getdate;
	// 用于判断月份
	private int monthid = 0;

	// 尝试自己构建ListView
	private SosRecordListView mListView;
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
		setContentView(R.layout.sos_record_activity);

		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			uri = "http://" + ip + ":8088/api/getsos";
		}

		// 设置缓存
		progDialog = new ProgressDialog(getLayoutInflater().getContext());
		
/*		//<宝贝切换的组件>
		activity = SosRecordActivity.this;
		rel_select_baby = (RelativeLayout)findViewById(R.id.rel_select_baby);
		text_select_baby = (TextView) findViewById(R.id.text_select_baby);
		rel_center = (RelativeLayout)findViewById(R.id.rel_center);
		rel = (RelativeLayout)findViewById(R.id.rel_top);
	    initBabyPopuWindow();	
		//<\宝贝切换的组件>
*/		
		// 初始化布局
		TestinitList();
				
		// 获取当前 月份
		GetMonth(monthid);

		// Handler取线程的数据
		initHandler();

		// 测试云崩
		// Test();

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
					//切换宝贝时刷新数据
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							TestNet();
						}
					}).start();
					//发送广播
					activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));



				}
			});
		}
	
	// 测试云崩
	private void Test() {
		int[] a = { 0, 1, 2 };
		for (int i = 0; i < 10; i++) {
			System.out.println(a[i]);
		}

	}

	private void TestinitList() {
		geneItems();
		mListView = (SosRecordListView) findViewById(R.id.list_sos);
		mListView.setPullLoadEnable(true);
		soslistadapter = new SosRecordListAdapter(this, sos_info);
		mListView.setAdapter(soslistadapter);
		// 设置上拉加载不可用
		// mListView.setPullLoadEnable(false);
		// 设置下拉刷新不可用
		// mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mHandler = new Handler();

	}

	// 获取数据显示
	private void geneItems() {
		// 连接服务端
		TestNet();

	}

	// 获取月份
	private void GetMonth(int month) {

		Calendar calendar = Calendar.getInstance();
		String mon = "";
		// month = month + 1;
		month = month + (calendar.get(Calendar.MONTH) + 1);
		int year = calendar.get(Calendar.YEAR);

		if (month >= (calendar.get(Calendar.MONTH) + 1)) {
			monthid = 0;
			// 设置下拉刷新不可用
			mListView.setPullRefreshEnable(false);
			
		}

		switch (month) {
		case 0:
			month = 12;
			year--;
			break;
		case -1:
			month = 11;
			year--;
			break;
		case -2:
			month = 10;
			year--;
			break;
		case -3:
			month = 9;
			year--;
			break;
		case -4:
			month = 8;
			year--;
			break;
		case -5:
			month = 7;
			year--;
			break;
		case -6:
			month = 6;
			year--;
			break;
		case -7:
			month = 5;
			year--;
			break;
		case -8:
			month = 4;
			year--;
			break;
		case -9:
			month = 3;
			year--;
			break;
		case -10:
			month = 2;
			year--;
			break;
		case -11:
			month = 1;
			year--;
			break;
		case -12:
			month = 12;
			year -= 2;
			break;
		case -13:
			// 设置上拉加载不可用
			mListView.setPullLoadEnable(false);
			monthid += 1;
			return;
		default:
			break;
		}

		Log.i("keis", "当前月份是：" + month);

		if (month < 10) {
			mon = "0" + month;
		} else {
			mon = mon + month;
		}

		getdate = year + "-" + mon;
		System.out.println(getdate);
		Log.e("keis", "当前月份是：" + getdate);
	}

	// handler
	private void initHandler() {
		myHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				switch (msg.what) {
				case 1001:
					info = (SosRecordInfo) msg.obj;
					// 加载数据
					sos_info.add(info);
					// 刷新列表
					dismissDialog();// 销毁缓存圆
					soslistadapter.notifyDataSetChanged();
					break;
				case 1002:
					 Toast.makeText(SosRecordActivity.this, getString(R.string.nodata),
					 Toast.LENGTH_SHORT)
					 .show();
					break;
				case 2001:
					Toast.makeText(SosRecordActivity.this,
							getString(R.string.nonetwork), 3000).show();
					break;
				case 2002:
					Toast.makeText(SosRecordActivity.this,
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
				HttpPost request = new HttpPost(uri);
				// 封装一个json
				JSONObject jsonsos = new JSONObject();

				try {
					// 传递参数
					jsonsos.put("imei", MbApplication.getGlobalData().getNowBaby().getBabyimei());
					jsonsos.put("month", getdate);
					// jsonsos.put("date", "2015-03-19");
					// jsonsos.put("page", "0");
					// jsonsos.put("num", "0");

					// 绑定到请求 Entry
					StringEntity se = new StringEntity(jsonsos.toString());
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
								Message msg = Message.obtain();
								msg.what = MSG_FROM_NODATA;
								msg.obj = datajson.length();
								myHandler.sendMessage(msg);
								dismissDialog();// 没有数据，直接销毁缓冲图标
							}
							for (int i = 0; i < datajson.length(); i++) {

								JSONObject sos = datajson.getJSONObject(i);

								String address = sos.getString("address");
								String minute = sos.getString("minute");
								String hour = sos.getString("hour");
								String date = sos.getString("date");
								String month = sos.getString("month");
								String lon=sos.getString("lon");
								String lat=sos.getString("lat");
								

								// 封装数据，发送
								info = new SosRecordInfo(date, month, hour,
										minute, address,lon,lat);

								Log.i("keis", "日期:" + date + "地址：" + address
										+ "    时间：" + hour + ":" + minute);

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

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 清除之前数据
				sos_info.clear();
				// 传上一个月
				monthid++;
				GetMonth(monthid);
				// 连接服务端拿的数据
				TestNet();
				// geneItems();
				soslistadapter.notifyDataSetChanged();
				// 设置上拉加载可用
				mListView.setPullLoadEnable(true);
				onLoad();
			}
		}, 0);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				// 清除之前数据
				sos_info.clear();
				// 传上一个月
				monthid--;
				GetMonth(monthid);
				// 连接服务端拿的数据
				TestNet();
				// geneItems();
				soslistadapter.notifyDataSetChanged();
				// 恢复可以下拉刷新
				mListView.setPullRefreshEnable(true);
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
