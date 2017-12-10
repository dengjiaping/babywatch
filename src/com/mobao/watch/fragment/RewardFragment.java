package com.mobao.watch.fragment;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.SetRewardGoalDialog;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.UniformDialog;
import com.mobao.watch.view.RoundProgressView;

/**
 * @author �� ����Fragment
 */
public class RewardFragment extends Fragment implements LocationSource,
		AMapLocationListener {

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
	private String modifyiimei = ""; // 待修改的imei
	private ImageView ivPlusReward;
	private RoundProgressView round;
	private TextView tvRewardCompleteness; // 当前星星
	private ImageView ivMinusReward;
	private LinearLayout SetRewardGoalLayout;
	private TextView tvRewardGoalText; // 目标星星
	private LinearLayout SetRewardThingLayout;
	private TextView tvRewardThing; // 奖励
	private TextView tvtotalStar;
	private TextView tvtotalStar_lv;
	private int star = 0;
	private int target = 0;
	private String award = "";
	private boolean ismodify = false; // 判断是否编辑过数据
	private ProgressDialog progDialog = null; // 圆形进度条
	private ImageButton btn_share;
	private Activity activity;
	private static float totalRoundProgress = 0f;
	private static float nowRoundProgress = 0f;
	private static String rewardThing;
	private int totalStar = 0;
	private Context context;
	private View view;
	private String URL_GETBABYTARGET = CommonUtil.baseUrl + "babystartarget";// 获得宝贝目标星星的url
	private String URL_GETBABYSTAR = CommonUtil.baseUrl + "babystar";// 获得宝贝星星的url
	private String URL_GETBABYtotalStar = CommonUtil.baseUrl + "babytotalstar";// 获得累计星星的url
	private String URL_BABYMODIFYSTARTARGET = CommonUtil.baseUrl + "babymodifystartarget";// 修改宝贝目标星星和奖励url
	private String URL_BABYMODIFYSTAR = CommonUtil.baseUrl + "babymodifystar";// 修改宝贝当前星星url
	Message msg = new Message();
	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	// 分享到各个平台所需要的数据(test为测试时用的数据)
	private String SHARE_TITLE = "test";
	private String SHARE_TITLEURL = "test";
	private String SHARE_TEXT = "test";
	private String SHARE_IMGPATH = "test";
	private String SHARE_URL = "test";
	private String SHARE_COMMENT = "test";
	private String SHARE_SITE = "test";
	private String SHARE_SITEURL = "test";

	private final int DIALOG_TYPE_JUST_REACH_GOAL = 0;
	private final int DIALOG_TYPE_HAS_REACHED_GOAL = 1;
	private final int DIALOG_TYPE_ONCLICK_SET_GOAL = 2;

	private ImageButton but_step;// 计步数据
	private ImageButton but_backlocation;// 跳回定位界面

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_GETBABYTARGET = "http://" + ip + ":8088/api/babystartarget";// 获得宝贝目标星星的url
			URL_GETBABYSTAR = "http://" + ip + ":8088/api/babystar";// 获得宝贝星星的url
			URL_GETBABYtotalStar = "http://" + ip + ":8088/api/babytotalstar";// 获得累计星星的url
			URL_BABYMODIFYSTARTARGET = "http://" + ip
					+ ":8088/api/babymodifystartarget";// 修改宝贝目标星星和奖励url
			URL_BABYMODIFYSTAR = "http://" + ip + ":8088/api/babymodifystar";// 修改宝贝当前星星url
		}

		Bundle args = getArguments();
		view = inflater.inflate(R.layout.reward_fragment, container, false);
		context = inflater.getContext();
		activity = getActivity();
		progDialog = new ProgressDialog(context);

		rewardThing = "";

		initView();

		text_select_baby = (TextView) view.findViewById(R.id.text_select_baby);
		rel_select_baby = (RelativeLayout) view
				.findViewById(R.id.rel_select_baby);
		rel_center = (RelativeLayout) view.findViewById(R.id.rel_center);
		rel = (RelativeLayout) view.findViewById(R.id.rel_top);
		// 初始化宝贝下拉框
		initBabyPopuWindow();

		addPlusRewardListener();
		addMinusRewardListener();
		addSetRewardGoalLayoutListener();
		addSetRewardThingLayoutListener();
		// 初始化ShareSDK
		ShareSDK.initSDK(context);
		// 注册切换宝贝广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("CHANGE_BABY_ACTION");
		getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);

		return view;
	}

	private void getbabystar() {
		// TODO 获得宝贝星星

		HttpPost request = new HttpPost(URL_GETBABYSTAR);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println("getbabystar()" + param.toString());
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

					// 取出服务端返回的宝贝星星
					star = datajson.getInt("star");
					System.out.println("star:" + star);

					nowRoundProgress = (float) star;

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					System.out.println(result.getString("msg"));
					/*
					 * Toast.makeText(context, "获取宝贝星星失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
					star = 0;
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

	private void getbabytarget() {
		// TODO 获得宝贝目标星星

		HttpPost request = new HttpPost(URL_GETBABYTARGET);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString(), "UTF-8");

			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println("getbabytarget()" + param.toString());
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

					// 取出服务端返回的目标星星
					target = datajson.getInt("target");
					totalRoundProgress = (float) target;
					award = datajson.getString("award");

				} else {
					arg2 = -1;
					erro = result.getString("msg");
					// 失败后摇干啥这里搞
					/*
					 * System.out.println(result.getString("msg"));
					 * Toast.makeText(context, "获取目标星星失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
					target = 0;
					award = "";
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

	private void getbabytotalStar() {
		// TODO 获取累计星星
		dismissDialog();
		HttpPost request = new HttpPost(URL_GETBABYtotalStar);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", now_babyimei);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println("getbabytotalStar：" + param.toString());
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

					// 取出服务端返回的累计星星
					String totalstar = datajson.getString("totalstar");
					int a = Integer.parseInt(totalstar);
					totalStar = a;
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 200;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					arg2 = -1;
					erro = result.getString("msg");
					// 失败后摇干啥这里搞
					/*
					 * System.out.println("累计星星" + result.getString("msg"));
					 * Toast.makeText(context, "获取累计星星失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
					msg = new Message();
					msg.what = what;
					msg.arg1 = 200;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				}

			} else {
				what = -1;
				msg = new Message();
				msg.what = what;
				msg.arg1 = 200;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				/*
				 * dismissDialog(); Toast.makeText(context, "糟糕！服务器大姨妈来了，请见谅！",
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

	private void initView() {
		but_backlocation = (ImageButton) view
				.findViewById(R.id.rewar_but_backlocation);
		but_backlocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BabyFragmentActivity.mPager.setCurrentItem(0,false);

			}
		});

		but_step = (ImageButton) view.findViewById(R.id.but_step);
		but_step.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BabyFragmentActivity.mPager.setCurrentItem(2,false);

			}
		});
		tvtotalStar_lv = (TextView) view.findViewById(R.id.tv_total_star_LV);
		btn_share = (ImageButton) view.findViewById(R.id.but_share);
		btn_share.setOnClickListener(new OnClickListener() {

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

		ivPlusReward = (ImageView) view.findViewById(R.id.iv_plus_reward);
		ivMinusReward = (ImageView) view.findViewById(R.id.iv_minus_reward);
		round = (RoundProgressView) view
				.findViewById(R.id.reward_round_progress);
		round.setmPercent(nowRoundProgress / totalRoundProgress);// 设置圆形进度条的完成度
		tvRewardCompleteness = (TextView) view
				.findViewById(R.id.tv_reward_completeness);
		tvRewardCompleteness.setText((int) nowRoundProgress + "");

		if (nowRoundProgress > -1 && nowRoundProgress < 100) {
			tvRewardCompleteness.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					activity.getResources().getDimension(R.dimen.dimen_70sp));
		} else {
			tvRewardCompleteness.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					activity.getResources().getDimension(R.dimen.dimen_50sp));
		}

		tvRewardGoalText = (TextView) view
				.findViewById(R.id.set_reward_goal_text);
		tvRewardGoalText.setText((int) totalRoundProgress + "");
		SetRewardGoalLayout = (LinearLayout) view
				.findViewById(R.id.set_reward_goal);
		SetRewardThingLayout = (LinearLayout) view
				.findViewById(R.id.set_reward_thing);
		tvRewardThing = (TextView) view.findViewById(R.id.reward_thing);
		tvRewardThing.setText(rewardThing);
		tvtotalStar = (TextView) view.findViewById(R.id.tv_total_star);
		tvtotalStar.setText(totalStar + "");
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
							modifyiimei = now_babyimei;
							// 将当前宝贝的imei存到全局变量里
							MbApplication.getGlobalData().getNowuser()
									.setImei(now_babyimei);
							groups.remove(0);
							// 获取上次未完成的奖励数据
							showDialog();
							getbabytarget();
							getbabystar();
							getbabytotalStar();
							round.setmPercent((float) star / target);

							// 初始化PopupWindow
							initPopupWindow();
						}

					});
				} else {
					dismissDialog();
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
				// showPopwindow();
			}
		});
	}

	private void showPopwindow() {
		// 利用layoutInflater获得View
		modifyiimei = now_babyimei;
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
				showDialog();
				if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							context.getResources()
									.getString(R.string.nonetwork));
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

				getActivity().sendBroadcast(new Intent("CHANGE_BABY_ACTION"));

				// 切换宝贝时刷新数据
				if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							context.getResources()
									.getString(R.string.nonetwork));
					return;
				}
				showDialog();
				msg = new Message();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						// modify
						if (ismodify) {
							String goal = tvRewardGoalText.getText().toString();
							String thing = tvRewardThing.getText().toString();
							String completeness = tvRewardCompleteness
									.getText().toString();
							babymodifystartarget(goal, thing);
							babymodifystar(completeness);
							ismodify = false;
						}
						modifyiimei = now_babyimei;
						// getdata of nowimei
						getbabytarget();
						getbabystar();
						getbabytotalStar();

					}
				}).start();
			}
		});
	}

	private void addSetRewardGoalLayoutListener() {
		SetRewardGoalLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ismodify = true;

				// 判断星星数是否为新用户的初始状态，即星星为0，目标为0
				if (totalRoundProgress == 0) {
					resetRoundProgressAndGoal();
					return;
				}

				// 判断现在的星星是否已经达到了目标
				if (Math.abs(nowRoundProgress - totalRoundProgress) < 0.001) {
					showIsReSetNewGoalDiaolg(DIALOG_TYPE_ONCLICK_SET_GOAL);
				} else {
					resetRoundProgressAndGoal();
				}
			}
		});
	}

	private void addSetRewardThingLayoutListener() {
		SetRewardThingLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ismodify = true;
				final SetRewardGoalDialog srgDialog = new SetRewardGoalDialog(
						context, R.style.chatfragment_call_dialog_style);
				srgDialog.ChangeToSetRewardThingDialog();
				srgDialog.setmaxlength(16);
				srgDialog.setBtOnclickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String thing = srgDialog.getInputText();
						tvRewardThing.setText(thing);
						srgDialog.dismiss();
					}
				});
				srgDialog.show();
			}
		});
	}

	private void addPlusRewardListener() {

		ivPlusReward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ismodify = true;
				addAStar();
			}
		});

		ivPlusReward.setOnTouchListener(new OnTouchListener() {

			private boolean isStop = false;
			private long currentTimeMillis;

			@Override
			public boolean onTouch(final View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isStop = false;

					currentTimeMillis = System.currentTimeMillis();

					new Thread() {
						public void run() {
							while (!isStop) {
								if ((System.currentTimeMillis() - currentTimeMillis) < 300) {
									continue;
								}

								v.post(new Runnable() {
									@Override
									public void run() {
										if (!addAStar()) {
											isStop = true;
										}
									}
								});

								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						};
					}.start();
					break;
				case MotionEvent.ACTION_UP:
					isStop = true;
					break;
				}
				return false;
			}
		});
	}

	private void addMinusRewardListener() {
		ivMinusReward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ismodify = true;
				removeAStar();
			}
		});

		ivMinusReward.setOnTouchListener(new OnTouchListener() {

			private boolean isStop = false;
			private long currentTimeMillis;

			@Override
			public boolean onTouch(final View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isStop = false;

					currentTimeMillis = System.currentTimeMillis();

					new Thread() {

						public void run() {

							while (!isStop) {
								if ((System.currentTimeMillis() - currentTimeMillis) < 300) {
									continue;
								}
								v.post(new Runnable() {
									@Override
									public void run() {
										if (!removeAStar()) {
											isStop = true;
										}
									}
								});

								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							}
						};
					}.start();
					break;
				case MotionEvent.ACTION_UP:
					isStop = true;
					break;
				}
				return false;
			}
		});
	}

	private boolean addAStar() {
		ismodify = true;
		// 判断星星数是否为新用户的初始状态，即星星为0，目标为0
		if (totalRoundProgress == 0) {
			resetRoundProgressAndGoal();
			return false;
		}

		// 判断是否可以添加星星
		if (Math.abs(nowRoundProgress - totalRoundProgress) < 0.001) {
			if (UniformDialog.getInstance().isShowing() == false) {
				showIsReSetNewGoalDiaolg(DIALOG_TYPE_HAS_REACHED_GOAL);
			}
			return false;
		}

		nowRoundProgress = nowRoundProgress + 1f;
		star++;
		float percent = nowRoundProgress / totalRoundProgress;
		round.setmPercent(percent);

		tvRewardCompleteness.setText((int) nowRoundProgress + "");

		/* 秋旭///////////////////////////////// */
		totalStar = totalStar + 1;
		tvtotalStar.setText(totalStar + "");
		int lv = totalStar / 1000;
		if (lv >= 0) {
			if (lv >= 9) {
				tvtotalStar_lv.setText("9"
						+ context.getResources().getString(R.string.lv));
			} else {
				tvtotalStar_lv.setText(lv
						+ context.getResources().getString(R.string.lv));
			}
		} else {
			tvtotalStar_lv.setText("0"
					+ context.getResources().getString(R.string.lv));
		}
		/* //////////////////////////////////////// */

		if (Math.abs(nowRoundProgress - totalRoundProgress) < 0.001) {	
				showIsReSetNewGoalDiaolg(DIALOG_TYPE_HAS_REACHED_GOAL);
		}
		return true;
	}

	/**
	 * 显示询问是否设定新目标的dialog
	 * 
	 * @param type
	 */
	private void showIsReSetNewGoalDiaolg(int type) {

		UniformDialog.initDialog(context, true);
		final UniformDialog unidialog = UniformDialog.getInstance();
		unidialog.getTvTitle().setText(
				context.getResources().getString(R.string.setnowtotal));

		// 根据不同情况设定不同提示框内容
		if (type == DIALOG_TYPE_HAS_REACHED_GOAL) {
			unidialog.getTvBody().setText(
					context.getResources().getString(R.string.has_into_total));
		} else if (type == DIALOG_TYPE_JUST_REACH_GOAL) {
			unidialog.getTvBody().setText(
					context.getResources().getString(R.string.just_into_total));
		} else if (type == DIALOG_TYPE_ONCLICK_SET_GOAL) {
			unidialog.getTvBody().setText(
					context.getResources().getString(
							R.string.onclick_into_total));
		}

		unidialog.getBtOk().setText(
				context.getResources().getString(R.string.cancel));
		unidialog.getBtCancel().setText(
				context.getResources().getString(R.string.sure));
		unidialog.getBtOk().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unidialog.dismiss();

			}
		});
		unidialog.getBtCancel().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unidialog.dismiss();

			}
		});
		unidialog.getBtCancel().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				unidialog.dismiss();
				/* 秋旭///////////////////////////////// */
				// 修改
				final String goal = tvRewardGoalText.getText().toString();
				final String thing = tvRewardThing.getText().toString();
				final String completeness = tvRewardCompleteness.getText()
						.toString();

				// 判断是否有网络，用于上传目标、奖励等数据到服务器
				// if (!CheckNetworkConnectionUtil
				// .isNetworkConnected(getActivity())) {
				// ToastUtil.show(getActivity(), context.getResources()
				// .getString(R.string.nonetwork));
				// return;
				// }

				modifyiimei = now_babyimei;
				showDialog();
				msg = new Message();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						// modify
						String completeness = tvRewardCompleteness.getText()
								.toString();
						babymodifystartarget(goal, thing);
						babymodifystar(completeness);
						getbabytotalStar();
					}
				}).start();
				/* //////////////////////////////////////// */
				resetRoundProgressAndGoal();
			}
		});
		unidialog.show();
	}

	protected void babymodifystar(String completeness) {
		// TODO 修改当前星星
		HttpPost request = new HttpPost(URL_BABYMODIFYSTAR);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", modifyiimei);
			param.put("star", completeness);

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println("babymodifystar()" + param.toString());
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
					/*
					 * Toast.makeText(context, "修改当前星星成功", Toast.LENGTH_LONG)
					 * .show();
					 */
					msg = new Message();
					msg.what = what;
					msg.arg1 = 400;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
					System.out.println("json:" + param.toString()
							+ "----baocun");

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					msg = new Message();
					msg.what = what;
					msg.arg1 = 400;
					msg.arg2 = arg2;
					handel.sendMessage(msg);
					/*
					 * System.out.println(result.getString("msg"));
					 * Toast.makeText(context, "修改当前星星失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */

				}

			} else {
				what = -1;
				msg = new Message();
				msg.what = what;
				msg.arg1 = 400;
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

	protected void babymodifystartarget(String goal, String thing) {
		// TODO 修改目标奖励和目标星星

		HttpPost request = new HttpPost(URL_BABYMODIFYSTARTARGET);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", modifyiimei);
			param.put("award", thing);
			param.put("target", goal);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString(), "UTF-8");
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println("babymodifystartarget()" + param.toString());
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
					/*
					 * Toast.makeText(context, "目标和奖励修改成功", Toast.LENGTH_LONG)
					 * .show();
					 */

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					/*
					 * System.out.println(result.getString("msg"));
					 * Toast.makeText(context, "目标和奖励修改失败!原因：" +
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

	private boolean removeAStar() {
		ismodify = true;
		if (nowRoundProgress == 0f) {
			Toast.makeText(context,
					context.getResources().getString(R.string.startsiszero),
					3000).show();
			return false;
		}
		nowRoundProgress = nowRoundProgress - 1f;
		star--;
		float percent = nowRoundProgress / totalRoundProgress;
		round.setmPercent(percent);
		tvRewardCompleteness.setText((int) nowRoundProgress + "");

		if (nowRoundProgress > -1 && nowRoundProgress < 100) {
			tvRewardCompleteness.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					activity.getResources().getDimension(R.dimen.dimen_70sp));
		} else {
			tvRewardCompleteness.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					activity.getResources().getDimension(R.dimen.dimen_50sp));

		}

		totalStar = totalStar - 1;
		tvtotalStar.setText(totalStar + "");
		int lv = totalStar / 1000;
		if (lv >= 0) {
			if (lv >= 9) {
				tvtotalStar_lv.setText("9"
						+ context.getResources().getString(R.string.lv));
			} else {
				tvtotalStar_lv.setText(lv
						+ context.getResources().getString(R.string.lv));
			}
		} else {
			tvtotalStar_lv.setText("0"
					+ context.getResources().getString(R.string.lv));
		}
		return true;
	}

	private void resetRoundProgressAndGoal() {
		final SetRewardGoalDialog srgDialog = new SetRewardGoalDialog(context,
				R.style.chatfragment_call_dialog_style);

		srgDialog.setBtOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String num = srgDialog.getInputText();
				if (num == null || "".equals(num)) {
					Toast.makeText(context,
							context.getResources().getString(R.string.noempty),
							3000).show();
					return;
				}

				try {
					int result = Integer.parseInt(num);

					if (result < 1 || result > 10) {
						Toast.makeText(
								context,
								context.getResources().getString(
										R.string.editstart), 3000).show();
						return;
					}

				} catch (NumberFormatException e) {
					Toast.makeText(
							context,
							context.getResources()
									.getString(R.string.editstart), 3000)
							.show();
				}

				nowRoundProgress = 0f;
				tvRewardCompleteness.setText(0 + "");

				round.setmPercent(0f);
				try {
					totalRoundProgress = Float.parseFloat(num);
					tvRewardGoalText.setText(Integer.parseInt(num) + "");
				} catch (Exception e) {
					// TODO: handle exception
					totalRoundProgress = 0f;
					tvRewardGoalText.setText(num);
				}

				srgDialog.dismiss();
			}
		});
		srgDialog.show();
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

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(context.getResources().getString(
				R.string.pleasewait));

	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
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
	public void onStop() {
		// TODO 当activity到了onStop时保存数据
		if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
			ToastUtil.show(activity,
					context.getResources().getString(R.string.nonetwork));

		} else {

			showDialog();
			msg = new Message();
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// modify
					if (ismodify) {
						System.out.println("rewardonStop()");
						String goal = tvRewardGoalText.getText().toString();
						String thing = tvRewardThing.getText().toString();
						String completeness = tvRewardCompleteness.getText()
								.toString();
						babymodifystartarget(goal, thing);
						babymodifystar(completeness);
						ismodify = false;
					}
				}
			}).start();
		}
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
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
				if (key == 200 && arg2 == 1) {
					dismissDialog();
					tvRewardGoalText.setText(String.valueOf(target));
					tvRewardThing.setText(award);
					tvRewardCompleteness.setText(String.valueOf(star));

					if (star > -1 && star < 100) {
						tvRewardCompleteness.setTextSize(
								TypedValue.COMPLEX_UNIT_PX,
								activity.getResources().getDimension(
										R.dimen.dimen_70sp));
					} else {
						tvRewardCompleteness.setTextSize(
								TypedValue.COMPLEX_UNIT_PX,
								activity.getResources().getDimension(
										R.dimen.dimen_50sp));
					}

					tvtotalStar.setText(String.valueOf(totalStar));
					int lv = totalStar / 1000;
					if (lv >= 0) {
						if (lv >= 9) {
							tvtotalStar_lv.setText("9"
									+ context.getResources().getString(
											R.string.lv));
						} else {
							tvtotalStar_lv.setText(lv
									+ context.getResources().getString(
											R.string.lv));
						}
					} else {
						tvtotalStar_lv
								.setText("0"
										+ context.getResources().getString(
												R.string.lv));
					}

					round.setmPercent((float) star / target);
				}
				if (key == 200 && arg2 == -1) {
					dismissDialog();
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.getdataerror)
									+ erro, 3000).show();
				}

				if (key == 400 && arg2 == 1) {
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.datasavesuccess), 3000).show();

				}
				if (key == 400 && arg2 == -1) {
					String erromsg = "";
					ErroNumberChange change = new ErroNumberChange(context);
					erromsg = change.chang(erro);

					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.datasaveerror)
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

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 接收宝贝切换的广播
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
				showDialog();
				msg = new Message();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						// modify
						if (ismodify) {
							String goal = tvRewardGoalText.getText().toString();
							String thing = tvRewardThing.getText().toString();
							String completeness = tvRewardCompleteness
									.getText().toString();
							babymodifystartarget(goal, thing);
							babymodifystar(completeness);
							ismodify = false;
						}
						modifyiimei = now_babyimei;
						// getdata of nowimei
						getbabytarget();
						getbabystar();
						getbabytotalStar();

					}
				}).start();
			}
		}
	};
}
