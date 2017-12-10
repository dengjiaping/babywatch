package com.mobao.watch.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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

import com.mb.zjwb1.R;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.adapter.MyFragmentPagerAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.BabyState;
import com.mobao.watch.bean.MqttConnection;
import com.mobao.watch.customview.CustomViewPager;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.fragment.MoreFragment;
import com.mobao.watch.fragment.MotionFragment;
import com.mobao.watch.fragment.RewardFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.service.ChatNewMsgService;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.ChatGetMsgs;
import com.mobao.watch.util.ChatOpenMonitorThread;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.GetAtuthlist;
import com.mobao.watch.util.NewMsgComeUtil;
import com.mobao.watch.util.NotificationUtil;
import com.mobao.watch.util.SPUtil;
import com.mobao.watch.util.SetStepGoalDialog;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.YunBaStart;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class BabyFragmentActivity extends FragmentActivity {
	public static ProgressDialog progDialog = null; // 圆形进度条
	public static CustomViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private RelativeLayout rel_location, rel_sport, rel_rewards, rel_tell,
			rel_more;
	private RelativeLayout layout_location, layout_cellphone, layout_monitor,
			layout_chat;
	private ImageButton but_1, but_2, but_3, but_4, but_5;
	private TextView txt1, txt2, txt3, txt4, txt5;

	private ImageButton but_location, but_cellphone, but_monitor, but_chat;
	private TextView text_location, text_cellphone, text_monitor, text_chat;

	public static int currIndex;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���
	private int offset;// ͼƬ�ƶ���ƫ����

	public boolean isAdmin ;// 判断是否为管理员
	/* 伍建鹏////////////////////////////////////// */
	public static RelativeLayout rlChatTip;
	public static TextView tvChatTipNum;
	public static int metricWidth;
	public static boolean isEnterMainInterface = false;

	public static boolean isEnterFromNotifytion = false;

	public static final int WHAT_STOP_REFRESH_ANMIN = 100;
	public static final int WHAT_UPDATA_CHAT_LIST = 105;
	public static final int WHAT_GET_MSG_SUCCESS = 103;
	public static final int WHAT_NETWORK_OUT_TIME = 104;
	public static final int WHAT_GET_MSG_SERVER_BUY = 102;
	public static final int WHAT_GET_MSG_NETWORK_EXCPTION = 101;

	public boolean isExitApp = false;
	public long lastBackTime = 0;
	
	public TextView tvWatchManager;
	public TextView tvLine;
	
	/* ///////////////////////////////////////// */

	public static DrawerLayout leftmenu;// 侧滑菜单
	private RelativeLayout sos;// sos记录
	private LinearLayout motionandreward;// 运动和奖励数据
	private LinearLayout watchmanger;// 手表管理
	private LinearLayout messagecenter;// 消息中心
	private LinearLayout family_info;// 家庭圈信息
	private LinearLayout playhelp;// 使用帮助
	private LinearLayout aboutandfeedback;// 关于和反馈
	private LinearLayout watchloactioninfo;// 手表详细定位记录
	private RelativeLayout layout_share;// 分享
	private ImageView image_escnowuser;// 退出当前账号
	private ImageView imag_down;// 宝贝名字下拉箭头
	private ImageView image_head_bg_bg;// 宝贝头像
	private static LinearLayout left_layout_main;// 侧滑菜单的整体布局
	// 宝贝切换
	private PopupWindow window; // popupWindow
	private static Activity activity;
	public static RelativeLayout rel_select_baby;
	public static TextView text_select_baby;
	private LinearLayout rel_center; // 中间布局
	private LinearLayout rel;// 顶部布局
	public static List<Baby> groups;// 宝贝列表
	private String now_babyimei = null;// 当前宝贝id
	public static Baby now_baby = null;// 当前宝贝
	private ListView lv_group; // popupWindow子view
	private GroupAdapter groupAdapter;
	private Bitmap bitmap;
	private ImageView image_head_bg;// 头像背景圆圈
	private String str_photp = null;// Base64Coder字符串形式的头像
	public static LinearLayout lin_bottom;// 底部按钮栏
	private static boolean isCanLocate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* 伍建鹏//////////////////////////// */

		// 获取屏幕宽度
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		metricWidth = metric.widthPixels; // 屏幕宽度（像素）

		/* ////////////////////////////// */

		// 友盟统计
		MobclickAgent.updateOnlineConfig(BabyFragmentActivity.this);

		// 云测崩溃分析
		TestinAgent.init(this);

		setContentView(R.layout.baby_fragment_activity);
		progDialog = new ProgressDialog(BabyFragmentActivity.this);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		MbApplication.getGlobalData().setIsinto(true);

		// 判断是否为管理员
try {
	 isAdmin=getIntent().getBooleanExtra("ifadmin", false);
     } catch (Exception e) {
	// TODO: handle exception
    	 ToastUtil.show(BabyFragmentActivity.this, "qubudao");
    	 boolean admin=SPUtil.getIsAdmin(BabyFragmentActivity.this);
 		isAdmin = admin; 
    }
 //  ToastUtil.show(BabyFragmentActivity.this, "主界面是否管理员："+isAdmin);

        System.out.println(isAdmin);
		// 监控宝贝位置
		LocatedBabyLocation();

		initRelativeLayout();
		initViewPager();

		/* 伍建鹏/////////////////////////////// */

		rlChatTip = (RelativeLayout) findViewById(R.id.rl_chat_tip);
		tvChatTipNum = (TextView) findViewById(R.id.tv_chat_tip_num);
		tvWatchManager = (TextView) findViewById(R.id.tv_watch_manager);
		tvLine = (TextView) findViewById(R.id.tv_line);
		startChatService();
		getMsgs();
		
		// 注册广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("baby_info_change");
		myIntentFilter.addAction("ADD_BABY_ACTION");
		myIntentFilter.addAction(MqttConnection.ACTION_BABYHASDELETE);
		registerReceiver(mBroadcastReceiver, myIntentFilter);

		checkSdCard(); // 检查sd卡状态
		/* /////////////////////////////////// */

		initleftmune();  //初始化右滑菜单
		
		// 将这个页面加入容器，方便后续干掉
		ActivityContainer.getInstance().addActivity(this,
				"BabyFragmentActivity");

	}

	private void initleftmune() {
		// TODO Auto-generated method stub
		ShareSDK.initSDK(BabyFragmentActivity.this);

		leftmenu = (DrawerLayout) findViewById(R.id.leftmenu);
		image_head_bg = (ImageView) findViewById(R.id.image_head_bg);
		imag_down = (ImageView) findViewById(R.id.imag_down);

		if(isAdmin){
			// 跳到宝贝信息页面
			image_head_bg_bg = (ImageView) findViewById(R.id.image_head_bg_bg);
			image_head_bg_bg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(BabyFragmentActivity.this,
							AddBabyActivity.class);
					intent.putExtra("edit", "edit");
					intent.putExtra("imei", now_babyimei);
					startActivity(intent);

				}
			});
			
			// 跳转到手表管理
			watchmanger = (LinearLayout) findViewById(R.id.watchmanger);
			watchmanger.setVisibility(View.VISIBLE);
			tvWatchManager.setVisibility(View.VISIBLE);
			tvLine.setVisibility(View.VISIBLE);
			watchmanger.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					leftmenu.closeDrawers();
					Intent intent = new Intent(BabyFragmentActivity.this,
							WatchMangerActivity.class);
					startActivity(intent);
				}
			});
		}else{
			//普通用户屏蔽掉腕表管理
			watchmanger = (LinearLayout) findViewById(R.id.watchmanger);
			watchmanger.setVisibility(View.GONE);
			tvWatchManager.setVisibility(View.GONE);
			tvLine.setVisibility(View.GONE);
		}
	
		// 跳转到奖励数据页面
		motionandreward = (LinearLayout) findViewById(R.id.motionandreward);
		motionandreward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(1);
				leftmenu.closeDrawers();

			}
		});

		

		// 跳转到消息中心
		messagecenter = (LinearLayout) findViewById(R.id.messagecenter);
		messagecenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						InformationCenterActivity.class);
				startActivity(intent);
			}
		});

		// 跳转到消息中心
		messagecenter = (LinearLayout) findViewById(R.id.messagecenter);
		messagecenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						InformationCenterActivity.class);
				startActivity(intent);
			}
		});

		// 跳转到家庭圈信息
		family_info = (LinearLayout) findViewById(R.id.family_info);
		family_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						FamilyMemberActivity.class);
				startActivity(intent);
			}
		});

		// 跳转到使用帮助
		playhelp = (LinearLayout) findViewById(R.id.playhelp);
		playhelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						HelpActivity.class);
				startActivity(intent);
			}
		});

		// 跳转到关于和反馈
		aboutandfeedback = (LinearLayout) findViewById(R.id.aboutandfeedback);
		aboutandfeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						AboutUsActivity.class);
				startActivity(intent);
			}
		});
		sos = (RelativeLayout) findViewById(R.id.layout_sos);
		sos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						SosRecordActivity.class);
				startActivity(intent);
			}
		});

		// 启动分享
		layout_share = (RelativeLayout) findViewById(R.id.layout_share);
		layout_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				showshare();

			}

		});
		// 退出当前账号
		image_escnowuser = (ImageView) findViewById(R.id.image_escnowuser);
		image_escnowuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final SetStepGoalDialog dialog = new SetStepGoalDialog(
						BabyFragmentActivity.this);
				dialog.ChangeToEscnowuser();
				dialog.setbtnconfirmauthclickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// 反注册云巴，推送
						// 解除绑定频道
						Log.i("keis",
								Arrays.toString(LocationFragment.imeiTopic));
						YunBaStart.UnBindTopic(BabyFragmentActivity.this,
								LocationFragment.imeiTopic);
						LocationFragment.imeiTopic = null;

						SharedPreferences autologin = getSharedPreferences(
								"logindata", MODE_PRIVATE);
						Editor dataedt = autologin.edit();
						dataedt.clear();
						dataedt.commit();

						Intent intent = new Intent(BabyFragmentActivity.this,
								LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();

						dialog.dismiss();
					}
				});

				dialog.setbtndisavowclickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		// 跳转到详细定位界面
		watchloactioninfo = (LinearLayout) findViewById(R.id.watchloactioninfo);
		watchloactioninfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				leftmenu.closeDrawers();
				Intent intent = new Intent(BabyFragmentActivity.this,
						GoogleLocationLogActivity.class);
				startActivity(intent);
			}
		});

		// <宝贝切换的组件>
		activity = BabyFragmentActivity.this;
		rel_select_baby = (RelativeLayout) findViewById(R.id.rel_select_baby);
		text_select_baby = (TextView) findViewById(R.id.select_baby_txt);
		rel_center = (LinearLayout) findViewById(R.id.motionandreward);
		rel = (LinearLayout) findViewById(R.id.rel_top);
		initBabyPopuWindow();
		// <\宝贝切换的组件>

	}

	// 初始化宝贝下拉框
	private void initBabyPopuWindow() {
		groups = new ArrayList<Baby>();
		if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
			ToastUtil.show(activity,
					getResources().getString(R.string.nonetwork));
			return;
		}
		new Thread() {
			public void run() {
				final ArrayList<Baby> babyList = BabyLocateServer
						.getBabyList(MbApplication.getGlobalData().getNowuser()
								.getUserid());
				if (babyList != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (babyList.size() < 2) {
								imag_down.setVisibility(View.INVISIBLE);
							} else {
								imag_down.setVisibility(View.VISIBLE);
							}

							groups = babyList;
							text_select_baby.setText(groups.get(0)
									.getBabyname());
							now_baby = groups.get(0);
							now_babyimei = now_baby.getBabyimei();

							// 获取宝贝头像
							getPortrait();
							groups.remove(0);
							// 将当前宝贝的imei存到全局变量里
							MbApplication.getGlobalData().getNowuser()
									.setImei(now_babyimei);
							MbApplication.getGlobalData().setNowBaby(now_baby);
							MbApplication.getGlobalData().setGroups(groups);
							
							// 初始化PopupWindow
							initPopupWindow();
						}

					});
				} else {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(
									activity,
									getResources().getString(
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
				showPopwindow();
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
		window.showAtLocation(rel_center, Gravity.TOP, -65, y);

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
				// 获取宝贝头像
				getPortrait();
				// 将当前宝贝的imei存到全局变量里
				MbApplication.getGlobalData().getNowuser()
						.setImei(now_babyimei);
				MbApplication.getGlobalData().setNowBaby(now_baby);
				MbApplication.getGlobalData().setGroups(groups);
				// 发送广播
				activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));
			}
		});
	}

	private void getPortrait() {
		// TODO 获取宝贝头像
		// 头像转换
		str_photp = now_baby.getPortrait();
		if (TextUtils.isEmpty(str_photp)) {
			image_head_bg.setBackgroundResource(R.drawable.babymark);
		} else {
			int len = str_photp.length();
			String head_of_str_photp = str_photp.substring(len - 20, len);
			Log.i("AAA", "22head_of_str_photp = " + head_of_str_photp);
			bitmap = ChatUtil.getImageCache().getBitmap(head_of_str_photp);

			if (bitmap == null) {
				byte[] bitmapArray;
				bitmapArray = Base64.decode(str_photp, Base64.DEFAULT);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bitmap = ChatUtil.toRoundBitmap(BitmapFactory.decodeByteArray(
						bitmapArray, 0, bitmapArray.length, options));

				// ChatUtil.getImageCache().putBitmap(head_of_str_photp,
				// bitmap);
			}

			Drawable babyDrawable = new BitmapDrawable(bitmap);
			image_head_bg.setBackgroundDrawable(babyDrawable);
		}

	}

	private void showshare() {
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
		oks.show(BabyFragmentActivity.this);
	}

	private void getMsgs() {
		ChatGetMsgs thread = new ChatGetMsgs(BabyFragmentActivity.this, 20,
				handler);
		thread.start();
	}

	private void ifAdmin() {
		new Thread() {
			public void run() {
				isAdmin = BabyServer.isAdmin(MbApplication.getGlobalData()
						.getNowuser().getUserid());
				MbApplication.getGlobalData().setAdmin(isAdmin);
			}
		}.start();
	}

	/* 伍建鹏/////////////////////////////// */
	private void checkSdCard() {
		// 检查sd卡存储状态是否可以接受新语音
		int res = ChatUtil.CheckSdCardForAudio(BabyFragmentActivity.this);
		if (res == ChatUtil.SD_CARD_CAN_T_USER) {
			ToastUtil.show(BabyFragmentActivity.this,
					getResources().getString(R.string.sd_card_can_t_user_affect_chat_use));
		} else if (res == ChatUtil.SD_CARD_FULL) {
			ToastUtil.show(BabyFragmentActivity.this,
					getResources().getString(R.string.sd_card_full_affect_chat_use));
		}
	}

	private void startChatService() {
		ChatNewMsgService.actionStart(BabyFragmentActivity.this);
	}

	/**
	 * 聊天底部栏提示
	 * 
	 * @param newCount
	 *            要提示的数目
	 * @return
	 */
	public static void showChatTip(int newCount) {

		rlChatTip.setVisibility(View.VISIBLE);
		if (newCount > 99) {
			tvChatTipNum.setText(newCount + "+");
		} else {
			tvChatTipNum.setText(newCount + "");
		}

	}

	@Override
	protected void onDestroy() {
		ChatNewMsgService.actionStop(BabyFragmentActivity.this);
		ChatFragment.removeAllData();
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		ChatFragment.isOnListDown = false;
		NewMsgComeUtil.outChatInterface();
		super.onPause();
		MobclickAgent.onPause(this);// 友盟统计时长
		isEnterMainInterface = false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		isEnterMainInterface = true;

		ChatUtil.removeNotify(BabyFragmentActivity.this,
				MqttConnection.NOTIFY_ID);

		// 是否是在notifytion中的跳过来的
		if (isEnterFromNotifytion) {
			isEnterFromNotifytion = false;
			mPager.setCurrentItem(3);
		}

		// 判断是否有新成员加入列表
		if (ChatNewMsgService.hasNewFamily) {
			GetAtuthlist gal = new GetAtuthlist(BabyFragmentActivity.this);
			gal.get();
			ChatNewMsgService.hasNewFamily = false;
		}

		if (currIndex == 3) {
			ChatFragment.updateListView();
			rlChatTip.setVisibility(8);
			NewMsgComeUtil.enterChatInterface();
		}

		MobclickAgent.onResume(this); // 友盟统计时长

	}

	/* /////////////////////////////////// */

	private void initRelativeLayout() {

		lin_bottom = (LinearLayout) findViewById(R.id.lin_bottom);
		left_layout_main = (LinearLayout) findViewById(R.id.left_layout_main);
		layout_location = (RelativeLayout) findViewById(R.id.layout_location);
		layout_cellphone = (RelativeLayout) findViewById(R.id.layout_cellphone);
		layout_monitor = (RelativeLayout) findViewById(R.id.layout_monitor);
		layout_chat = (RelativeLayout) findViewById(R.id.layout_chat);
         
		
		but_location = (ImageButton) findViewById(R.id.btn_location);
		but_cellphone = (ImageButton) findViewById(R.id.btn_cellphone);
		but_monitor = (ImageButton) findViewById(R.id.btn_monitor);
		but_chat = (ImageButton) findViewById(R.id.btn_chat);
        
		text_location = (TextView) findViewById(R.id.text__location);
		text_cellphone = (TextView) findViewById(R.id.text_cellphone);
		text_monitor = (TextView) findViewById(R.id.text_monitor);
		text_chat = (TextView) findViewById(R.id.text_chat);

		layout_cellphone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (leftmenu.isDrawerOpen(left_layout_main)) {
					return;
				}
				if (MbApplication.getGlobalData().getNowBaby().getBabyimei() == null) {
					ToastUtil.show(BabyFragmentActivity.this, getResources()
							.getString(R.string.call_baby_fail));
					return;
				}
				Intent intent2 = new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:"
								+ MbApplication.getGlobalData().getNowBaby()
										.getBabyphone()));
				startActivity(intent2);

			}
		});
		layout_monitor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (leftmenu.isDrawerOpen(left_layout_main)) {
					return;
				}
				if (MbApplication.getGlobalData().getNowBaby().getBabyimei() == null) {
					ToastUtil.show(BabyFragmentActivity.this, getResources()
							.getString(R.string.call_baby_fail));
					return;
				}
				ChatOpenMonitorThread openMonitor = new ChatOpenMonitorThread(
						BabyFragmentActivity.this, MbApplication
								.getGlobalData().getNowBaby().getBabyimei(),
						MbApplication.getGlobalData().getNowBaby()
								.getBabyphone());
				openMonitor.start();
			}
		});

		but_cellphone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (leftmenu.isDrawerOpen(left_layout_main)) {
					return;
				}
				if (MbApplication.getGlobalData().getNowBaby().getBabyimei() == null) {
					ToastUtil.show(BabyFragmentActivity.this, getResources()
							.getString(R.string.call_baby_fail));
					return;
				}
				Intent intent2 = new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:"
								+ MbApplication.getGlobalData().getNowBaby()
										.getBabyphone()));
				startActivity(intent2);

			}
		});
		but_monitor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (leftmenu.isDrawerOpen(left_layout_main)) {
					return;
				}
				if (MbApplication.getGlobalData().getNowBaby().getBabyimei() == null) {
					ToastUtil.show(BabyFragmentActivity.this, getResources()
							.getString(R.string.call_baby_fail));
					return;
				}
				ChatOpenMonitorThread openMonitor = new ChatOpenMonitorThread(
						BabyFragmentActivity.this, MbApplication
								.getGlobalData().getNowBaby().getBabyimei(),
						MbApplication.getGlobalData().getNowBaby()
								.getBabyphone());
				openMonitor.start();
			}
		});

		
		layout_cellphone.setOnTouchListener(new OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                            //更改为按下时的背景图片
                    	    layout_cellphone.setBackgroundColor(0xFFFF4F03);
                            but_cellphone.setImageResource(R.drawable.cellphone1);
                            but_cellphone.setBackgroundColor(0xFFFF4F03);
                            but_monitor.setImageResource(R.drawable.monitor_off);
                            but_chat.setImageResource(R.drawable.tell);
                            but_location.setImageResource(R.drawable.location);
                            text_location.setTextColor(0xFFAFAFAF);
                            text_cellphone.setTextColor(0xFFFFFFFF);
                            text_monitor.setTextColor(0xFFAFAFAF);
                            text_chat.setTextColor(0xFFAFAFAF);
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                            //改为抬起时的图片
                    	    layout_cellphone.setBackgroundColor(0xFFFFFFFF);
                    	    but_cellphone.setBackgroundColor(0xFFFFFFFF);
                    	    but_cellphone.setImageResource(R.drawable.cellphone_on);
                    	    text_cellphone.setTextColor(0xFFFF4F03);
                    }
                    return false;
            }
            
    });
		but_cellphone.setOnTouchListener(new OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                    	   //更改为按下时的背景图片
                	    layout_cellphone.setBackgroundColor(0xFFFF4F03);
                        but_cellphone.setImageResource(R.drawable.cellphone1);
                        but_cellphone.setBackgroundColor(0xFFFF4F03);
                        but_monitor.setImageResource(R.drawable.monitor_off);
                        but_chat.setImageResource(R.drawable.tell);
                        text_cellphone.setTextColor(0xFFFFFFFF);
                        text_monitor.setTextColor(0xFFAFAFAF);
                        text_chat.setTextColor(0xFFAFAFAF);
                        but_location.setImageResource(R.drawable.location);
                        text_location.setTextColor(0xFFAFAFAF);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                        //改为抬起时的图片
                	    layout_cellphone.setBackgroundColor(0xFFFFFFFF);
                	    but_cellphone.setBackgroundColor(0xFFFFFFFF);
                	    but_cellphone.setImageResource(R.drawable.cellphone_on);
                	    text_cellphone.setTextColor(0xFFFF4F03);
                }
                return false;
        }
        
});
		
		layout_monitor.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
             	   //更改为按下时的背景图片
                 layout_monitor.setBackgroundColor(0xFFFF4F03);
                 but_monitor.setImageResource(R.drawable.monitor_off1);
                 but_monitor.setBackgroundColor(0xFFFF4F03);
                 but_cellphone.setImageResource(R.drawable.cellphone_off);
                 but_chat.setImageResource(R.drawable.tell);
                 text_monitor.setTextColor(0xFFFFFFFF);
                 text_cellphone.setTextColor(0xFFAFAFAF);
                 text_chat.setTextColor(0xFFAFAFAF);
                 layout_location.setBackgroundColor(0xFFFFFFFF);
                 but_location.setImageResource(R.drawable.location);
                 text_location.setTextColor(0xFFAFAFAF);
         }else if(event.getAction() == MotionEvent.ACTION_UP){
                 //改为抬起时的图片
        	    layout_monitor.setBackgroundColor(0xFFFFFFFF);
        	    but_monitor.setBackgroundColor(0xFFFFFFFF);
        	    but_monitor.setImageResource(R.drawable.monitor_on);
         	    text_monitor.setTextColor(0xFFFF4F03);
         }
         return false;
 }
 
});
		
        but_monitor.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
             	   //更改为按下时的背景图片
                 layout_monitor.setBackgroundColor(0xFFFF4F03);
                 but_monitor.setImageResource(R.drawable.monitor_off1);
                 but_monitor.setBackgroundColor(0xFFFF4F03);
                 but_cellphone.setImageResource(R.drawable.cellphone_off);
                 but_chat.setImageResource(R.drawable.tell);
                 text_monitor.setTextColor(0xFFFFFFFF);
                 text_cellphone.setTextColor(0xFFAFAFAF);
                 text_chat.setTextColor(0xFFAFAFAF);
                 but_location.setImageResource(R.drawable.location);
                 text_location.setTextColor(0xFFAFAFAF);
         }else if(event.getAction() == MotionEvent.ACTION_UP){
                 //改为抬起时的图片
        	    layout_monitor.setBackgroundColor(0xFFFFFFFF);
        	    but_monitor.setBackgroundColor(0xFFFFFFFF);
        	    but_monitor.setImageResource(R.drawable.monitor_on);
         	    text_monitor.setTextColor(0xFFFF4F03);
         }
         return false;
 }
 
});
		
        layout_chat.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
             	   //更改为按下时的背景图片
                 layout_chat.setBackgroundColor(0xFFFF4F03);
                 but_chat.setImageResource(R.drawable.chat1);
                 but_chat.setBackgroundColor(0xFFFF4F03);
                 but_cellphone.setImageResource(R.drawable.cellphone_off);
                 but_monitor.setImageResource(R.drawable.monitor_off);
                 text_chat.setTextColor(0xFFFFFFFF);
                 text_cellphone.setTextColor(0xFFAFAFAF);
                 text_monitor.setTextColor(0xFFAFAFAF);
                 but_location.setImageResource(R.drawable.location);
                 text_location.setTextColor(0xFFAFAFAF);
         }else if(event.getAction() == MotionEvent.ACTION_UP){
                 //改为抬起时的图片
        	    layout_chat.setBackgroundColor(0xFFFFFFFF);
        	    but_chat.setBackgroundColor(0xFFFFFFFF);
        	    but_chat.setImageResource(R.drawable.tell_checked);
         	    text_chat.setTextColor(0xFFFF4F03);
         }
         return false;
 }
 
});
        but_chat.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
             	   //更改为按下时的背景图片
                 layout_chat.setBackgroundColor(0xFFFF4F03);
                 but_chat.setImageResource(R.drawable.chat1);
                 but_chat.setBackgroundColor(0xFFFF4F03);
                 but_cellphone.setImageResource(R.drawable.cellphone_off);
                 but_monitor.setImageResource(R.drawable.monitor_off);
                 text_chat.setTextColor(0xFFFFFFFF);
                 text_cellphone.setTextColor(0xFFAFAFAF);
                 text_monitor.setTextColor(0xFFAFAFAF);
                 but_location.setImageResource(R.drawable.location);
                 text_location.setTextColor(0xFFAFAFAF);
         }else if(event.getAction() == MotionEvent.ACTION_UP){
                 //改为抬起时的图片
        	    layout_chat.setBackgroundColor(0xFFFFFFFF);
        	    but_chat.setBackgroundColor(0xFFFFFFFF);
        	    but_chat.setImageResource(R.drawable.tell_checked);
         	    text_chat.setTextColor(0xFFFF4F03);
         }
         return false;
 }
 
});
        layout_location.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
             	   //更改为按下时的背景图片
                 layout_location.setBackgroundColor(0xFFFF4F03);
                 but_location.setImageResource(R.drawable.location);
                 but_location.setBackgroundColor(0xFFFF4F03);
                 but_cellphone.setImageResource(R.drawable.cellphone_off);
                 but_monitor.setImageResource(R.drawable.monitor_off);
                 but_chat.setImageResource(R.drawable.tell);
                 text_chat.setTextColor(0xFFAFAFAF);
                 text_location.setTextColor(0xFFFFFFFF);
                 text_cellphone.setTextColor(0xFFAFAFAF);
                 text_monitor.setTextColor(0xFFAFAFAF);
         }else if(event.getAction() == MotionEvent.ACTION_UP){
                 //改为抬起时的图片
        	    layout_location.setBackgroundColor(0xFFFFFFFF);
        	    but_location.setBackgroundColor(0xFFFFFFFF);
        	    but_location.setImageResource(R.drawable.location_checked);
         	    text_location.setTextColor(0xFFFF4F03);
         }
         return false;
 }
 
});
        but_location.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
             	   //更改为按下时的背景图片
                 layout_location.setBackgroundColor(0xFFFF4F03);
                 but_location.setImageResource(R.drawable.location);
                 but_location.setBackgroundColor(0xFFFF4F03);
                 but_cellphone.setImageResource(R.drawable.cellphone_off);
                 but_monitor.setImageResource(R.drawable.monitor_off);
                 but_chat.setImageResource(R.drawable.tell);
                 text_chat.setTextColor(0xFFAFAFAF);
                 text_location.setTextColor(0xFFFFFFFF);
                 text_cellphone.setTextColor(0xFFAFAFAF);
                 text_monitor.setTextColor(0xFFAFAFAF);
         }else if(event.getAction() == MotionEvent.ACTION_UP){
                 //改为抬起时的图片
        	    layout_location.setBackgroundColor(0xFFFFFFFF);
        	    but_location.setBackgroundColor(0xFFFFFFFF);
        	    but_location.setImageResource(R.drawable.location_checked);
         	    text_location.setTextColor(0xFFFF4F03);
         }
         return false;
 }
 
});
		layout_location.setOnClickListener(new txListener(0));
		layout_chat.setOnClickListener(new txListener(3));
		but_location.setOnClickListener(new txListener(0));
		but_chat.setOnClickListener(new txListener(3));

		/*
		 * rel_location = (RelativeLayout) findViewById(R.id.rel_location);
		 * rel_sport = (RelativeLayout) findViewById(R.id.rel_sport);
		 * rel_rewards = (RelativeLayout) findViewById(R.id.rel_rewards);
		 * rel_tell = (RelativeLayout) findViewById(R.id.rel_tell); rel_more =
		 * (RelativeLayout) findViewById(R.id.rel_more);
		 * 
		 * rel_location.setOnClickListener(new txListener(0));
		 * rel_sport.setOnClickListener(new txListener(1));
		 * rel_rewards.setOnClickListener(new txListener(2));
		 * rel_tell.setOnClickListener(new txListener(3));
		 * rel_more.setOnClickListener(new txListener(4)); //
		 * ֻ��RelativeLayout�����¼����Ǻ������������ټ�������ķ������м��� but_1 =
		 * (ImageButton) findViewById(R.id.btn_location); but_2 = (ImageButton)
		 * findViewById(R.id.btn_sport); but_3 = (ImageButton)
		 * findViewById(R.id.btn_reward); but_4 = (ImageButton)
		 * findViewById(R.id.btn_tell); but_5 = (ImageButton)
		 * findViewById(R.id.but_more);
		 * 
		 * but_1.setOnClickListener(new txListener(0));
		 * but_2.setOnClickListener(new txListener(1));
		 * but_3.setOnClickListener(new txListener(2));
		 * but_4.setOnClickListener(new txListener(3));
		 * but_5.setOnClickListener(new txListener(4));
		 * 
		 * // ��ȡTextView ��������ɫ txt1 = (TextView) findViewById(R.id.text1);
		 * txt2 = (TextView) findViewById(R.id.text2); txt3 = (TextView)
		 * findViewById(R.id.text3); txt4 = (TextView) findViewById(R.id.text4);
		 * txt5 = (TextView) findViewById(R.id.text5);
		 */

	}

	public  class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;

		}

		@Override
		public void onClick(View v) {
			// mPager.setScanScroll(true)
			if (leftmenu.isDrawerOpen(left_layout_main)) {
				return;
			}
			if (index == 0) {
//				if (isCanLocate == true) {
				
				if(!progDialog.isShowing()){
					System.out.println("弹出");
					showDialog();
					Intent intent = new Intent("LOCATE_BABY_ACTION");
					activity.sendBroadcast(intent);
				}
				
//					isCanLocate = false;
//					handler.sendEmptyMessageDelayed(10, 60000);
//				} else {
//					ToastUtil.show(activity,
//							activity.getString(R.string.waitingonemin));
//				}
			}
			mPager.setCurrentItem(index,false);
			mPager.setClickable(false);
		}
	}

	private void initViewPager() {
		mPager = (CustomViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();

		LocationFragment locationfragment = new LocationFragment();
		MotionFragment motionfragment = new MotionFragment();
		RewardFragment rewardfragment = new RewardFragment();
		ChatFragment chatfragment = new ChatFragment();
		MoreFragment morefragment = new MoreFragment();
		fragmentList.add(locationfragment);
		fragmentList.add(rewardfragment);
		fragmentList.add(motionfragment);
		fragmentList.add(chatfragment);
		fragmentList.add(morefragment);
		// ��ViewPager����������
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setOffscreenPageLimit(5);// ���û���ҳ����
		mPager.setCurrentItem(0,false);		// mPager.setPageTransformer(true, new CubeTransformer());
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// ҳ��仯ʱ�ļ�����

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpW;// ��������ҳ���ƫ����

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

			/* 伍建鹏////////////////////////////////////////////////////////////// */
			// if (arg0 == 3 && currIndex == 3) {
			// // // 判断网络
			// // ChatFragment.updateListView();
			// // rlChatTip.setVisibility(8);
			// //
			// // ChatUtil.removeNotify(BabyFragmentActivity.this,
			// // MqttConnection.NOTIFY_ID);
			// // NewMsgComeUtil.enterChatInterface();
			// } else {
			// NewMsgComeUtil.outChatInterface();
			// }

			/* ////////////////////////////////////////////////////////////// */
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageSelected(int arg0) {

			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// ƽ�ƶ���
		      but_cellphone.setImageResource(R.drawable.cellphone_off);
              but_monitor.setImageResource(R.drawable.monitor_off);
              text_cellphone.setTextColor(0xFFAFAFAF);
              text_monitor.setTextColor(0xFFAFAFAF);
			currIndex = arg0;
			animation.setFillAfter(true);// ������ֹʱͣ�������һ֡����Ȼ��ص�û��ִ��ǰ��״̬
			animation.setDuration(400);// ��������ʱ��0.2��
			int i = currIndex + 1;
			changeColor(currIndex);

		}
	}

	/**
	 * ��ͼ��
	 */
	public void changeColor(int i) {
		switch (i) {
		case 0:
			but_location.setImageResource(R.drawable.location_checked);
			text_location.setTextColor(0xFFFC4709);
			/*
			 * but_cellphone.setImageResource(R.drawable.cellphone_off);
			 * text_cellphone.setTextColor(0xFFAFAFAF);
			 * but_monitor.setImageResource(R.drawable.monitor_off);
			 * text_monitor.setTextColor(0xFFAFAFAF);
			 */
			but_chat.setImageResource(R.drawable.tell);
			text_chat.setTextColor(0xFFAFAFAF);
			break;

		case 3:
			but_location.setImageResource(R.drawable.location);
			text_location.setTextColor(0xFFAFAFAF);

			but_chat.setImageResource(R.drawable.tell_checked);
			text_chat.setTextColor(0xFFFC4709);
			break;

		default:
			break;
		}

		/*
		 * switch (i) { case 0:
		 * but_1.setImageResource(R.drawable.location_checked);
		 * but_2.setImageResource(R.drawable.sport);
		 * but_3.setImageResource(R.drawable.rewards);
		 * but_4.setImageResource(R.drawable.tell);
		 * but_5.setImageResource(R.drawable.more);
		 * txt1.setTextColor(Color.parseColor("#FC4709"));
		 * txt2.setTextColor(Color.parseColor("#8C8D90"));
		 * txt3.setTextColor(Color.parseColor("#8C8D90"));
		 * txt4.setTextColor(Color.parseColor("#8C8D90"));
		 * txt5.setTextColor(Color.parseColor("#8C8D90")); break;
		 * 
		 * case 1: but_1.setImageResource(R.drawable.location);
		 * but_2.setImageResource(R.drawable.sport_checked);
		 * but_3.setImageResource(R.drawable.rewards);
		 * but_4.setImageResource(R.drawable.tell);
		 * but_5.setImageResource(R.drawable.more);
		 * txt1.setTextColor(Color.parseColor("#8C8D90"));
		 * txt2.setTextColor(Color.parseColor("#FC4709"));
		 * txt3.setTextColor(Color.parseColor("#8C8D90"));
		 * txt4.setTextColor(Color.parseColor("#8C8D90"));
		 * txt5.setTextColor(Color.parseColor("#8C8D90")); break;
		 * 
		 * case 2: but_1.setImageResource(R.drawable.location);
		 * but_2.setImageResource(R.drawable.sport);
		 * but_3.setImageResource(R.drawable.rewards_checked);
		 * but_4.setImageResource(R.drawable.tell);
		 * but_5.setImageResource(R.drawable.more);
		 * txt1.setTextColor(Color.parseColor("#8C8D90"));
		 * txt2.setTextColor(Color.parseColor("#8C8D90"));
		 * txt3.setTextColor(Color.parseColor("#FC4709"));
		 * txt4.setTextColor(Color.parseColor("#8C8D90"));
		 * txt5.setTextColor(Color.parseColor("#8C8D90")); break;
		 * 
		 * case 3: but_1.setImageResource(R.drawable.location);
		 * but_2.setImageResource(R.drawable.sport);
		 * but_3.setImageResource(R.drawable.rewards);
		 * but_4.setImageResource(R.drawable.tell_checked);
		 * but_5.setImageResource(R.drawable.more);
		 * txt1.setTextColor(Color.parseColor("#8C8D90"));
		 * txt2.setTextColor(Color.parseColor("#8C8D90"));
		 * txt3.setTextColor(Color.parseColor("#8C8D90"));
		 * txt4.setTextColor(Color.parseColor("#FC4709"));
		 * txt5.setTextColor(Color.parseColor("#8C8D90")); break;
		 * 
		 * case 4: but_1.setImageResource(R.drawable.location);
		 * but_2.setImageResource(R.drawable.sport);
		 * but_3.setImageResource(R.drawable.rewards);
		 * but_4.setImageResource(R.drawable.tell);
		 * but_5.setImageResource(R.drawable.more_checked);
		 * txt1.setTextColor(Color.parseColor("#8C8D90"));
		 * txt2.setTextColor(Color.parseColor("#8C8D90"));
		 * txt3.setTextColor(Color.parseColor("#8C8D90"));
		 * txt4.setTextColor(Color.parseColor("#8C8D90"));
		 * txt5.setTextColor(Color.parseColor("#FC4709")); break;
		 * 
		 * default: break; }
		 */

	}

	public static int getCurrIndex() {
		return currIndex;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (NewMsgComeUtil.isAtChatInterface) {
				mPager.setCurrentItem(0,false);
				return true;
			}

			showIsExitDialog();

			// if (isExitApp == true) {
			// if ((System.currentTimeMillis() - lastBackTime) < 3 * 1000) {
			// ActivityContainer.getInstance().finshActivity(
			// "VerifyActivity");
			// ActivityContainer.getInstance().finshActivity(
			// "LogInfoActivity");
			// finishActivity();
			// return super.onKeyDown(keyCode, event);
			// } else {
			// isExitApp = true;
			// }
			// }
			//
			// Toast.makeText(this,
			// getResources().getString(R.string.click_again_exit_app),
			// 2 * 1000).show();
			//
			// lastBackTime = System.currentTimeMillis();
			//
			// isExitApp = true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showIsExitDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(
				BabyFragmentActivity.this);
		builder.setMessage(getResources().getString(R.string.is_exit_app));
		builder.setTitle(getResources().getString(R.string.dialog_body_text));
		builder.setPositiveButton(getResources().getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finishActivity();
					}
				});
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void finishActivity() {
		ChatNewMsgService.actionStop(BabyFragmentActivity.this);
		ChatFragment.removeAllData();
		BabyFragmentActivity.this.finish();
	}

	public void LocatedBabyLocation() {

		if (getIntent().getStringExtra("notification") != null) {
			ToastUtil.show(this, getIntent().getStringExtra("message"));
			return;
		} else {
			int time = MbApplication.getGlobalData().getIntervalminute() * 60 * 1000;
			Timer timer = new Timer();
			NewTimerTask timerTask = new NewTimerTask();
			// 程序运行后立刻执行任务，每隔10000ms执行一次
			timer.schedule(timerTask, 0, time);
		}
	}
	/**
	 * 显示进度条对话框
	 */
	public  void showDialog() {
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(false);
			progDialog.setMessage(BabyFragmentActivity.this.getResources().getString(R.string.babylocationing));
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
	class NewTimerTask extends TimerTask {
		@Override
		public void run() {

			new Thread() {
				public void run() {
					final ArrayList<BabyState> list = BabyLocateServer
							.babyLocateState(MbApplication.getGlobalData()
									.getNowuser().getUserid());
					if (list != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								String msg = "";
								for (int i = 0; i < list.size(); i++) {
									BabyState bs = list.get(i);
									msg = msg + bs.getTime() + ":"
											+ bs.getName() + bs.getCondition()
											+ bs.getAddress() + ";";
								}
								NotificationUtil
										.getNotification(
												getBaseContext(),
												getResources()
														.getString(
																R.string.babylocatechange)
														+ "",
												getResources()
														.getString(
																R.string.babylocatechange),
												msg);
							}
						});
					}
				}
			}.start();

		}
	}

	// 接收宝贝信息发送改变的信息
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 接收修改宝贝信息广播
			if (action.equals("baby_info_change")) {
				showDialog();
				initBabyPopuWindow();
				dismissDialog();
			}
			if (action.equals("ADD_BABY_ACTION")) {
				showDialog();
				initBabyPopuWindow();
				dismissDialog();
			}

			if (action.equals(MqttConnection.ACTION_BABYHASDELETE)) {
				Intent i = new Intent();
				i.setClass(BabyFragmentActivity.this, LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				ToastUtil.show(BabyFragmentActivity.this, getResources()
						.getString(R.string.nowbabyhasdelet));
				finish();
			}
		}
	};

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case 10:
			// if (isCanLocate == false) {
			// isCanLocate = true;
			// }
			// break;

			case WHAT_GET_MSG_NETWORK_EXCPTION:
				ToastUtil.show(
						activity,
						activity.getResources().getString(
								R.string.network_excption_fail_to_get_msg));
				break;
			case WHAT_GET_MSG_SERVER_BUY:
				ToastUtil.show(
						activity,
						activity.getResources().getString(
								R.string.server_buy_fail_to_get_msg));
				break;
			case WHAT_NETWORK_OUT_TIME:
				ToastUtil.show(
						activity,
						activity.getResources().getString(
								R.string.network_out_time_fail_to_get_msg));
				break;
			case WHAT_GET_MSG_SUCCESS:
				ChatFragment.updateListData(ChatGetMsgs.getMsgsList());
				ChatFragment.updateListView();
				break;

			case WHAT_UPDATA_CHAT_LIST:
				Log.w("chat", "到了babFragmentActivity的handler中了");
				ChatFragment.reshowListView();
				break;

			case WHAT_STOP_REFRESH_ANMIN:
				ChatFragment.stopRefreshAmin();
				break;

			}
		};
	};

}
