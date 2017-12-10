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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.fragment.MotionFragment.myhandel;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

public class PowerSaveActivity extends Activity {
	private ImageButton btn_back;
	private CheckBox checkbox_locationinterval;
	private RadioGroup radiogroup_time;
	private RadioButton radio_2minute;
	private RadioButton radio_10minute;
	private RadioButton radio_30minute;
	private RelativeLayout layout_locationinterval;
	private ProgressDialog progDialog = null; // 圆形进度条
	private String URL_GETLOCALINTERVAL = CommonUtil.baseUrl + "getlocalinterval";// 获取定位间隔
	private String URL_SETLOCALINTERVAL = CommonUtil.baseUrl + "setlocalinterval";// 修改定位间隔
	private int intervalminute = 0;

	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	
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
		setContentView(R.layout.powersaveactivity);

		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_GETLOCALINTERVAL = "http://" + ip
					+ ":8088/api/getlocalinterval";// 获取定位间隔
			URL_SETLOCALINTERVAL = "http://" + ip
					+ ":8088/api/setlocalinterval";// 修改定位间隔
		}

		progDialog = new ProgressDialog(PowerSaveActivity.this);
		showDialog();
		if (!CheckNetworkConnectionUtil.isNetworkConnected(this)) {
			ToastUtil.show(this,
					getResources().getString(R.string.networkunusable));
			dismissDialog();
			;// 没网络直接销毁风火轮
			return;
		} else {
/*			//<宝贝切换的组件>
			activity = PowerSaveActivity.this;
			rel_select_baby = (RelativeLayout)findViewById(R.id.rel_select_baby);
			text_select_baby = (TextView) findViewById(R.id.text_select_baby);
			rel_center = (LinearLayout)findViewById(R.id.rel_center);
			rel = (RelativeLayout)findViewById(R.id.rel_top);
		    initBabyPopuWindow();	
			//<\宝贝切换的组件>	
*/		    
			//获取从腕表管理传过来的IMEI，如果没空就用当前宝贝的（可能会隐藏错误）
			if(getIntent().getStringExtra("nowimei")==null||getIntent().getStringExtra("nowimei").equals("")){
				now_babyimei= MbApplication.getGlobalData().getNowBaby().getBabyimei();
				System.out.println("传过来的IMEI有问题！参数为："+now_babyimei);
			}else{
				now_babyimei=getIntent().getStringExtra("nowimei");
			}
			
		    //获取定位
			getdata();
		}

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
							//刷新数据
							getdata();
							//发送广播
							activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));



						}
					});
				}
	
	private void getdata() {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getlocalinterval();
			}
		}).start();

	}

	protected void getlocalinterval() {
		// TODO 获取定位间隔
		HttpPost request = new HttpPost(URL_GETLOCALINTERVAL);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei",now_babyimei);
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
					JSONObject datajson = new JSONObject(data);

					// 取出服务端返回的间隔
					intervalminute = datajson.getInt("minute");
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

					intervalminute = 0;
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
			dismissDialog();
			e.printStackTrace();
		}

	}

	private void setinterval() {
		// TODO 修改定位间隔
		HttpPost request = new HttpPost(URL_SETLOCALINTERVAL);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			param.put("minute", String.valueOf(intervalminute));
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
					msg.arg1 = 700;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 700;
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
				msg.arg1 = 700;
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
			dismissDialog();
			e.printStackTrace();
		}
	}

	private void initview() {
		// TODO Auto-generated method stub
		radiogroup_time = (RadioGroup) findViewById(R.id.radiogroup_time);
		radio_2minute = (RadioButton) findViewById(R.id.radio_2minute);
		radio_2minute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				intervalminute = 2;
				MbApplication.getGlobalData().setIntervalminute(intervalminute);
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(PowerSaveActivity.this)) {
					ToastUtil.show(PowerSaveActivity.this, getResources()
							.getString(R.string.networkunusable));
					return;
				}

				showDialog();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						setinterval();
					}
				}).start();

			}
		});

		radio_10minute = (RadioButton) findViewById(R.id.radio_10minute);
		radio_10minute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				intervalminute = 10;
				MbApplication.getGlobalData().setIntervalminute(intervalminute);
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(PowerSaveActivity.this)) {
					ToastUtil.show(PowerSaveActivity.this, getResources()
							.getString(R.string.networkunusable));
					return;
				}
				showDialog();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						setinterval();
					}
				}).start();
			}
		});
		radio_30minute = (RadioButton) findViewById(R.id.radio_30minute);
		radio_30minute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				intervalminute = 30;
				MbApplication.getGlobalData().setIntervalminute(intervalminute);
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(PowerSaveActivity.this)) {
					ToastUtil.show(PowerSaveActivity.this, getResources()
							.getString(R.string.networkunusable));
					return;
				}
				showDialog();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						setinterval();
					}
				}).start();
			}
		});

		layout_locationinterval = (RelativeLayout) findViewById(R.id.layout_locationinterval);
		layout_locationinterval.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changecheckbox();
			}
		});

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	protected void changecheckbox() {
		// TODO Auto-generated method stub
		if(checkbox_locationinterval == null){
			return;
		}

		if (checkbox_locationinterval.isChecked()) {
			checkbox_locationinterval.setChecked(false);
		} else {
			checkbox_locationinterval.setChecked(true);
		}
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(PowerSaveActivity.this.getResources().getString(
				R.string.pleasewait));
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
				if (key == 600 && arg2 == 1) {
					initview();
					dismissDialog();
					Toast.makeText(
							PowerSaveActivity.this,
							PowerSaveActivity.this.getResources().getString(
									R.string.getintervalminutesuccess), 3000)
							.show();
					System.out.println("jiange:" + intervalminute);
					if (intervalminute != 0) {

						if (intervalminute == 2) {
							radiogroup_time.check(R.id.radio_2minute);
						}
						if (intervalminute == 10) {
							radiogroup_time.check(R.id.radio_10minute);
						}
						if (intervalminute == 30) {
							radiogroup_time.check(R.id.radio_30minute);
						}

					} else {
						checkbox_locationinterval.setChecked(false);
					}

				}
				if (key == 600 && arg2 == -1) {
					dismissDialog();
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(PowerSaveActivity.this);
					erromsg=change.chang(erro);
					Toast.makeText(
							PowerSaveActivity.this,
							PowerSaveActivity.this.getResources().getString(
									R.string.getintervalminutefail)
									+ erromsg, 3000).show();

				}

				if (key == 700 && arg2 == 1) {
					dismissDialog();
					Toast.makeText(
							PowerSaveActivity.this,
							PowerSaveActivity.this.getResources().getString(
									R.string.setintervalminutesuccess), 3000)
							.show();

				}
				if (key == 700 && arg2 == -1) {
					dismissDialog();
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(PowerSaveActivity.this);
					erromsg=change.chang(erro);
					Toast.makeText(
							PowerSaveActivity.this,
							PowerSaveActivity.this.getResources().getString(
									R.string.setintervalminutefail)
									+ erromsg, Toast.LENGTH_LONG).show();

				}

			} else {
				dismissDialog();
				Toast.makeText(
						PowerSaveActivity.this,
						PowerSaveActivity.this.getResources().getString(
								R.string.serverfail), 3000).show();
			}

		}

	}

	protected void radiobutton_onclicke(View view) {
		// TODO 单选框点击时间
		RadioButton radio = (RadioButton) view;
		System.out.println(radio.getText().toString());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

}
