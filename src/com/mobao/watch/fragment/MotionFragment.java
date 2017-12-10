package com.mobao.watch.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.mb.zjwb1.R;
import com.mobao.watch.activity.ActiveWatchActivity;
import com.mobao.watch.activity.AddBabyActivity2;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.activity.PowerSaveActivity;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.datespinner.JudgeDate;
import com.mobao.watch.datespinner.ScreenInfo;
import com.mobao.watch.datespinner.WheelMain;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.DateTimePickDialogUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.SetRewardGoalDialog;
import com.mobao.watch.util.SetStepGoalDialog;
import com.mobao.watch.util.SetTimePickerDialog;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.view.RoundProgressView;

/**
 * @author �� �˶�Fragment
 */
public class MotionFragment extends Fragment implements LocationSource,
		AMapLocationListener {
	private ProgressDialog progDialog = null; // 圆形进度条
	private PopupWindow window; // popupWindow
	private ListView lv_group; // popupWindow子view
	public static List<Baby> groups;// 宝贝列表
	private RelativeLayout rel_center; // 中间布局
	private RelativeLayout rel;// 顶部布局
	public static RelativeLayout rel_select_baby;
	public static TextView text_select_baby;
	private String now_babyimei = null;// 当前宝贝id
	public static Baby now_baby = null;// 当前宝贝
	private GroupAdapter groupAdapter;
	private TextView text_completeness;
	private Context context;
	private TextView text_step;
	private TextView text_calores;
	private ImageView img_modify;// 编辑图标
	private TextView editext_target;// 目标输入框
	private RoundProgressView rpundprogressvar;
	private Boolean havingFcusable = false;// 目标输入框是否获得焦点
	private String babystep = "0"; // 步数
	private String babycalories = "0"; // 卡路里
	private String target = "0";
	private String imei = "";
	private String URL_GETCBABYSTEPTARGET = CommonUtil.baseUrl + "babysteptarget";// 获得步数目标的url
	private String URL_GETSTEP = CommonUtil.baseUrl + "babystep";// 获得步数的url
	private String URL_GETCALORIES = CommonUtil.baseUrl + "babycalories";// 获得卡路里的url
	private String URL_MODIFYSTEPTAR = CommonUtil.baseUrl + "babymodifysteptarget";// 修改宝贝目标的url
	private ImageButton share;
	private RelativeLayout spinner;
	private Dialog dialog;
	private String fristtime; // 今天的日期
	private Activity activity;
    private ImageButton rel_btn_rewar;//跳到奖励数据页面的按钮
    private ImageButton but_backliaction;//跳到地图界面的按钮
	
	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	WheelMain wheelMain;
	TextView txttime;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	// 分享到各个平台所需要的数据(test为测试时用的数据)
	private String SHARE_TITLE = "test";
	private String SHARE_TITLEURL = "test";
	private String SHARE_TEXT = "test";
	private String SHARE_IMGPATH = "test";
	private String SHARE_URL = "test";
	private String SHARE_COMMENT = "test";
	private String SHARE_SITE = "test";
	private String SHARE_SITEURL = "test";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_GETCBABYSTEPTARGET = "http://" + ip
					+ ":8088/api/babysteptarget";// 获得步数目标的url
			URL_GETSTEP = "http://" + ip + ":8088/api/babystep";// 获得步数的url
			URL_GETCALORIES = "http://" + ip + ":8088/api/babycalories";// 获得卡路里的url
			URL_MODIFYSTEPTAR = "http://" + ip
					+ ":8088/api/babymodifysteptarget";// 修改宝贝目标的url
		}

		Bundle args = getArguments();
		View view = inflater
				.inflate(R.layout.motion_fragment, container, false);
		context = inflater.getContext();
		progDialog = new ProgressDialog(context);
		activity = getActivity();
		rel_select_baby = (RelativeLayout) view
				.findViewById(R.id.rel_select_baby);
		text_select_baby = (TextView) view.findViewById(R.id.text_select_baby);
		rel_center = (RelativeLayout) view.findViewById(R.id.rel_center);
		rel = (RelativeLayout) view.findViewById(R.id.rel_top);
		// 初始化宝贝下拉框
		initBabyPopuWindow();

		// 初始化ShareSDK
		ShareSDK.initSDK(context);
		inintview(view, inflater.getContext());

		// 注册切换宝贝广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("CHANGE_BABY_ACTION");
		getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
		
		return view;
	}

	// 初始化宝贝下拉框
	private void initBabyPopuWindow() {
		groups = new ArrayList<Baby>();
		if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
			ToastUtil.show(activity,
					context.getResources().getString(R.string.nonetwork));
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
							groups = babyList;
							text_select_baby.setText(groups.get(0)
									.getBabyname());
							now_baby = groups.get(0);
							now_babyimei = now_baby.getBabyimei();
							// 将当前宝贝的imei存到全局变量里
							MbApplication.getGlobalData().getNowuser()
									.setImei(now_babyimei);
							groups.remove(0);
							// 界面刚加载时获得当前的数据
							String a = txttime.getText().toString();
							getbabystep(a);
							getbabycalories(a);
							getbabysteptarget(a);
							float gresssvar = 0;
							if (Integer.parseInt(babystep) > Integer
									.parseInt(target)) {
								gresssvar = 1f;
							} else {
								gresssvar = (float) Integer.parseInt(babystep)
										/ Integer.parseInt(target);
							}
							rpundprogressvar.setmPercent(gresssvar);

							// 初始化PopupWindow
							initPopupWindow();
						}
					});
				} else {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(
									activity,
									context.getResources().getString(
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
				//发送广播
				getActivity().sendBroadcast(new Intent("CHANGE_BABY_ACTION"));

//				if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
//					ToastUtil.show(activity,
//							context.getResources()
//									.getString(R.string.nonetwork));
//					return;
//				}
//				// 切换宝贝时刷新数据
//				final String a = txttime.getText().toString();
//				showDialog();
//				msg = new Message();
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//
//						// 获得步数和卡路里
//						getbabystep(a);
//						getbabycalories(a);
//						getbabysteptarget(a);
//					}
//				}).start();

			}
		});
	}

	private void inintview(View view, final Context context) {
		// TODO Auto-generated method stub
		final Context con = context;
		but_backliaction=(ImageButton) view.findViewById(R.id.but_backliaction);
		but_backliaction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BabyFragmentActivity.mPager.setCurrentItem(1,false);
			}
		});
		rpundprogressvar = (RoundProgressView) view
				.findViewById(R.id.rpundprogressvar);
		text_completeness = (TextView) view
				.findViewById(R.id.text_completeness);
		text_step = (TextView) view.findViewById(R.id.text_babystep);
		text_calores = (TextView) view.findViewById(R.id.text_babycalories);
		img_modify = (ImageView) view.findViewById(R.id.imgbt_modify);
		editext_target = (TextView) view.findViewById(R.id.editext_target);
		img_modify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final SetStepGoalDialog stepdialog = new SetStepGoalDialog(arg0
						.getContext());
				stepdialog.ChangeToSetRewardThingDialog();
				stepdialog.setBtOnclickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						final String newtarget = stepdialog.getInputText();
						if(newtarget.indexOf("0")==0){
							ToastUtil.show(context, context.getResources().getString(R.string.editgoalfail));
							return;
						}
						final String a = txttime.getText().toString();
						final int aa = newtarget.length();

						try {
							int result = Integer.parseInt(newtarget);

							if (result < 1) {
								stepdialog.dismiss();
								return;

							}

						} catch (NumberFormatException e) {
							stepdialog.dismiss();
						}

						if (!CheckNetworkConnectionUtil
								.isNetworkConnected(activity)) {
							ToastUtil.show(activity, context.getResources()
									.getString(R.string.nonetwork));
							return;
						}

						showDialog();
						new Thread(new Runnable() {
							public void run() {
								if (newtarget != null && aa != 0) {

									target = newtarget;
									modifysteptarget(a, newtarget);
									stepdialog.dismiss();
								} else {
									stepdialog.dismiss();
									dismissDialog();
								}
							}
						}).start();
					}
				});
				stepdialog.show();
			}
		});

		share = (ImageButton) view.findViewById(R.id.but_share);
		spinner = (RelativeLayout) view.findViewById(R.id.layout_spinnerdate);
		txttime = (TextView) view.findViewById(R.id.text_spinnerdate);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		// int month=calendar.get(Calendar.MONTH) + 1;
		String month = null;
		if (calendar.get(Calendar.MONTH) + 1 < 10) {
			month = "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
		} else {
			month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		}
		String day = null;
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			day = "0" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		} else {
			day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		}

		txttime.setText(year + "-" + month + "-" + day);
		txttime.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				final String a = txttime.getText().toString();
				// 判断当前选择的日期是不是今天
				if (a.equals(fristtime)) {
					img_modify.setVisibility(View.VISIBLE);
				} else {
					img_modify.setVisibility(View.GONE);
				}
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(activity)) {
					ToastUtil.show(activity, context.getResources()
							.getString(R.string.nonetwork));
					return;
				}
				// 切换时间的时候刷新
				showDialog();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method
						// stub

						// 获得步数和卡路里
						getbabystep(a);
						getbabycalories(a);
						getbabysteptarget(a);
					}
				}).start();
			}
		});
		fristtime = txttime.getText().toString();

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							context.getResources()
									.getString(R.string.nonetwork));
					return;
				}
				showshare(v);

			}
		});

		spinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(getActivity(), "");
				dateTimePicKDialog.dateTimePicKDialog(txttime);
				/*dateTimePicKDialog.setBtnOkonclik(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						txttime.setText(timepicker.getTime());
						final String a = txttime.getText().toString();
						// 判断当前选择的日期是不是今天
						if (a.equals(fristtime)) {
							img_modify.setVisibility(View.VISIBLE);
						} else {
							img_modify.setVisibility(View.GONE);
						}
						timepicker.dismiss();

						if (!CheckNetworkConnectionUtil
								.isNetworkConnected(activity)) {
							ToastUtil.show(activity, context.getResources()
									.getString(R.string.nonetwork));
							return;
						}
						// 切换时间的时候刷新
						showDialog();

						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method
								// stub

								// 获得步数和卡路里
								getbabystep(a);
								getbabycalories(a);
								getbabysteptarget(a);
							}
						}).start();

					}

				});

				timepicker.setBtnNoonclik(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						timepicker.dismiss();
					}
				});
				timepicker.show();
*/
				/*
				 * new AlertDialog.Builder(con) .setTitle("选择时间")
				 * .setView(timepickerview) .setPositiveButton("确定", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { txttime.setText(wheelMain.getTime()); final String a
				 * = txttime.getText().toString(); //判断当前选择的日期是不是今天
				 * if(a.equals(fristtime)){
				 * img_modify.setVisibility(View.VISIBLE); }else{
				 * img_modify.setVisibility(View.GONE); }
				 * 
				 * // 切换时间的时候刷新 showDialog(); msg = new Message(); new
				 * Thread(new Runnable() {
				 * 
				 * @Override public void run() { // TODO Auto-generated method
				 * // stub
				 * 
				 * // 获得步数和卡路里 getbabystep(a); getbabycalories(a);
				 * getbabysteptarget(a); } }).start();
				 * 
				 * } }) .setNegativeButton("取消", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { } }).show();
				 */
			}
		});

	}

	protected void showshare(View v) {
		// TODO Auto-generated method stub

		/* 要分享的内容在这里设置 */

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		/*
		 * oks.setNotification(R.drawable.qidong_logo,
		 * getString(R.string.app_name));
		 */
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getResources().getString(R.string.shareertongwanbiao));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(getResources().getString(R.string.shareertongwanbiao));
		// text是分享文本，所有平台都需要这个字段
		oks.setText(getResources().getString(R.string.sharecontent));
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(getResources().getString(R.string.sharecontent));// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(getResources().getString(R.string.shareertongwanbiao));
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment(getResources().getString(R.string.shareertongwanbiao));
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getResources().getString(R.string.shareertongwanbiao));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(getResources().getString(R.string.shareertongwanbiao));
		// 启动分享GUI
		oks.show(context);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	private void getbabystep(String text) {
		// TODO 获得步数

		HttpPost request = new HttpPost(URL_GETSTEP);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			param.put("date", text);

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
					// 成功后摇干啥这里搞
					arg2 = 1;
					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);

					// 取出服务端返回的步数
					babystep = datajson.getString("step");
					System.out.println("babaystep:" + babystep);

				} else {
					// 失败后摇干啥这里搞
					what = 1;
					arg2 = -1;
					erro = result.getString("msg");
					System.out.println("步数" + result.getString("msg"));
					babystep = "0";
					/*
					 * Toast.makeText(context, "获取步数失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
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

	private void getbabycalories(String text) {
		// TODO 获得卡里路

		HttpPost request = new HttpPost(URL_GETCALORIES);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			param.put("date", text);

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			System.out.println("res" + res);
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

					// 取出服务端返回的卡路里
					babycalories = datajson.getString("calories");
					System.out.println("babycalories:" + babycalories);

				} else {
					// 失败后摇干啥这里搞
					what = 1;
					arg2 = -1;

					/*
					 * Toast.makeText(context, "获取卡路里失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
					babycalories = "0";
				}

			} else {
				what = -1;
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

	private void getbabysteptarget(String text) {
		// TODO 获得步数目标
		HttpPost request = new HttpPost(URL_GETCBABYSTEPTARGET);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			param.put("date", text);

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
					// 成功后摇干啥这里搞
					arg2 = 1;
					// 将data再封装成一个json（data为一个json）
					String data = result.getString("data");
					JSONObject datajson = new JSONObject(data);

					// 取出服务端返回的目标
					target = datajson.getString("target");
					System.out.println("target:" + target);
					// 发送消息
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 100;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					System.out.println("目标" + result.getString("msg"));
					/*
					 * Toast.makeText(context, "获取目标失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
					target = "0";
					// 发送消息
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 100;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				}

			} else {
				what = -1;
				target = "0";
				// 发送消息
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 100;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
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

	private void modifysteptarget(String text, String newtarget) {
		// TODO 修改步数目标
		HttpPost request = new HttpPost(URL_MODIFYSTEPTAR);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();
		try {
			param.put("imei", now_babyimei);
			param.put("date", text);
			param.put("target", newtarget);

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
				what = 1;
				// 判断修改是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 成功后摇干啥这里搞
					arg2 = 1;

					target = newtarget;

					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 300;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					Message msg = Message.obtain();
					msg.arg2 = -1;
					erro = result.getString("msg");
					// 失败后摇干啥这里搞
					/*
					 * dismissDialog(); System.out.println("设定目标" +
					 * result.getString("msg")); Toast.makeText(context,
					 * "设定目标失败!原因：" + result.getString("msg"),
					 * Toast.LENGTH_LONG).show();
					 */
					msg.what = what;
					msg.arg1 = 300;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
				}

			} else {
				what = -1;
				/*
				 * dismissDialog(); Toast.makeText(context, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG) .show();
				 */
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 300;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
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

	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;

			if (lj == 1) {

				// 100getmotion 200getreward 300setmotion 400setreward
				if (key == 100 && g2 == 1) {
					dismissDialog();
					text_step.setText(babystep
							+ context.getResources().getString(
									R.string.step_danwei));
					text_completeness.setText(babystep);
					text_calores.setText(babycalories + "Kam");
					editext_target.setText(target);
					try {
						if (Integer.parseInt(target) == 0
								&& Float.parseFloat(babycalories) <= 0
								&& Integer.parseInt(babystep) == 0) {
							Toast.makeText(
									context,
									context.getResources().getString(
											R.string.nomotiondata), 3000)
									.show();
							float gresssvar = 0;
							if (Integer.parseInt(babystep) > Integer
									.parseInt(target)) {
								gresssvar = 1f;
							} else {
								gresssvar = (float) Integer.parseInt(babystep)
										/ Integer.parseInt(target);
							}
							rpundprogressvar.setmPercent(gresssvar);
							return;
						}
						float gresssvar = 0;
						if (Integer.parseInt(babystep) > Integer
								.parseInt(target)) {
							gresssvar = 1f;
						} else {
							gresssvar = (float) Integer.parseInt(babystep)
									/ Integer.parseInt(target);
						}
						rpundprogressvar.setmPercent(gresssvar);
					} catch (Exception e) {
						// TODO: handle exception
						float gresssvar = 0;
						System.out.println("gresssvar:" + gresssvar);
						rpundprogressvar.setmPercent(gresssvar);
						Toast.makeText(
								context,
								context.getResources().getString(
										R.string.for10031), 3000).show();
					}

				}

				if (key == 100 && g2 == -1) {
					dismissDialog();
					String erromsg = "";
					ErroNumberChange change = new ErroNumberChange(context);
					erromsg = change.chang(erro);
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.getdataerror)
									+ erromsg, 3000).show();
				}

				if (key == 300 && g2 == 1) {
					dismissDialog();
					editext_target.setText(target);

					// 如果出现转换错误则直接为零
					try {
						float gresssvar = 0;
						if (Integer.parseInt(babystep) > Integer
								.parseInt(target)) {
							gresssvar = 1f;
						} else {
							gresssvar = (float) Integer.parseInt(babystep)
									/ Integer.parseInt(target);
						}
						rpundprogressvar.setmPercent(gresssvar);

					} catch (Exception e) {
						// TODO: handle exception
						float gresssvar = 0;
						rpundprogressvar.setmPercent(gresssvar);
						Toast.makeText(
								context,
								context.getResources().getString(
										R.string.for10031), 3000).show();
					}

					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.successsetgold), 3000).show();

				}

				if (key == 300 && g2 == -1) {
					String erromsg = "";
					ErroNumberChange change = new ErroNumberChange(context);
					erromsg = change.chang(erro);
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.goldalterfail)
									+ erromsg, 3000).show();
				}

			} else {
				dismissDialog();
				Toast.makeText(context,
						context.getResources().getString(R.string.serverbusy),
						3000).show();
			}

		}

	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		// 进入界面时刷新数据
		if(isVisibleToUser){
			String a = txttime.getText().toString();
			getbabystep(a);
			getbabycalories(a);
			getbabysteptarget(a);
			float gresssvar = 0;
			if (Integer.parseInt(babystep) > Integer
					.parseInt(target)) {
				gresssvar = 1f;
			} else {
				gresssvar = (float) Integer.parseInt(babystep)
						/ Integer.parseInt(target);
			}
			rpundprogressvar.setmPercent(gresssvar);	
		}
		
		super.setUserVisibleHint(isVisibleToUser);
	}
	public void onStop() {
		// TODO 当activity到了onStop时保存数据
		System.out.println("motiononStop()");

		super.onStop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}


	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 接收宝贝切换的广播z
			if (action.equals("CHANGE_BABY_ACTION")) {
				now_baby = MbApplication.getGlobalData().getNowBaby();
				groups = MbApplication.getGlobalData().getGroups();
				text_select_baby.setText(now_baby.getBabyname());
				now_babyimei = now_baby.getBabyimei();
				if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							context.getResources()
									.getString(R.string.nonetwork));
					return;
				}
				final String a = txttime.getText().toString();
				showDialog();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 获得步数和卡路里
						getbabystep(a);
						getbabycalories(a);
						getbabysteptarget(a);
					}
				}).start();
			}
		}
	};
}
