package com.mobao.watch.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.mb.zjwb1.R;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.activity.chatfragmentSelectCallWayActivity;
import com.mobao.watch.adapter.ChatMsgViewAdapter;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.ChatAudioEntity;
import com.mobao.watch.bean.ChatAudioPlayer;
import com.mobao.watch.bean.MqttConnection;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.service.ChatNewMsgService;
import com.mobao.watch.util.AudioRecorder;
import com.mobao.watch.util.ChatGetMsgs;
import com.mobao.watch.util.ChatSendAudioThread;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.NewMsgComeUtil;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.UserType;
import com.mobao.watch.util.WaitDialog;

/**
 * @author 坤 聊天Fragment
 * 
 */
public class ChatFragment extends Fragment implements LocationSource,
		AMapLocationListener, OnClickListener, OnTouchListener {
	private ProgressDialog progDialog = null; // 圆形进度条
	private PopupWindow window; // popupWindow
	private ListView lv_group; // popupWindow子view
	public static List<Baby> groups;// 宝贝列表
	private RelativeLayout rel_center; // 中间布局
	private RelativeLayout rel;// 顶部布局
	public static TextView text_select_baby;
	public static RelativeLayout rel_select_baby;
	private static String now_babyimei = null;// 当前宝贝id
	public static Baby now_baby = null;// 当前宝贝
	private GroupAdapter groupAdapter;

	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	private String babyPhone = "";
	public static long refreshAminTime = 0;

	/* 伍建鹏/////////////////////////////////////////////////////////////// */
	private static int RECORD_NO = 0; // 不在录音
	public static int RECORD_ING = 1; // 正在录音
	public static int RECODE_END = 2; // 完成录音
	public static int RECODE_STATE = RECORD_NO; // 录音的状态
	public static int MAX_TIME = 20; // 最长录制时间，单位秒，0为无时间限制
	private static float MIX_TIME = 1.5f; // 最短录制时间，单位秒，0为无时间限制，建议设为1
	private static float recordTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	public static boolean isOnListDown = true;

	private static String audioId = "audioId";

	private static final int recordingInterfaceShow = 0; // 录音时的界面可见时的Visibility值
	private static final int recordingInterfaceNotShow = 8; // 录音时的界面不可见且不占布局时的Visibility值

	public static final int defaultListNumPerPage = 10;
	public static final String LATEST_AUDIO_TIME = "latest_audio_time"
			+ MbApplication.getGlobalData().getNowuser().getUserid();
	public static final String EXTRA_NOW_BABYPHONE = "now_baby_phone";
	public static final String EXTRA_NOW_BABYIMEI = "now_babyimei";

	// 组件
	private static ListView mListView; // 显示聊天内容的View
	private LinearLayout llRecordingBtn; // 录音按钮
	private RelativeLayout rlRecordingInterface; // 隐藏的录音界面
	private ImageView ivRecordRotateRound; // 录音界面中的旋转圈
	private TextView tvRecordBtnText; // 录音按钮中的文字
	private RelativeLayout rlCall; // 拨打电话按钮
	private ImageView ivCall; // 拨打电话按钮
	private RelativeLayout rlRefresh; // 拨打电话按钮
	private static ImageView ivRefresh; // 拨打电话按钮
	private String URL_GETBABYALLINFO = "http://115.28.62.126:8088/api/babyallinfo";// 获得宝贝信息的url
	// 其他
	private static ChatMsgViewAdapter mAdapter;// 语音视图的Adapter
	private static List<ChatAudioEntity> mDataArrays = new ArrayList<ChatAudioEntity>();// 语音对象数组
	private AudioRecorder mr; // 录音自定义类
	private Thread recordThread; // 录音线程
	private static Context context;
	public static String LAST_TIME = "lasttime";
	public static String PREFS_NAME = "lasttimeprefs";
	private View view;
	String sendTime = ChatUtil.getDate(); // 发送时间，主要作用是用于生成andioId
	private long recordDownTime = 0;

	private ImageButton backlocation;// 切换到定位界面的按钮

	private Activity activity;

	private static boolean isFristGetMsg = true;

	/* /////////////////////////////////////////////////////////////////////// */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		view = inflater.inflate(R.layout.chat_fragment, container, false);

		context = inflater.getContext();
		activity = getActivity();
		progDialog = new ProgressDialog(context);
		rel_select_baby = (RelativeLayout) view
				.findViewById(R.id.rel_select_baby);
		text_select_baby = (TextView) view.findViewById(R.id.text_select_baby);
		rel_center = (RelativeLayout) view.findViewById(R.id.rel_center);
		rel = (RelativeLayout) view.findViewById(R.id.rel_top);

		// 初始化宝贝下拉框
		initBabyPopuWindow();

		/* 伍建鹏/////////////////////////////////////////////////////////////// */

		// 注册宝贝信息改变的广播
		// IntentFilter filter = new IntentFilter(
		// MqttConnection.ACTION_BABY_INFO_CHANGE);
		// context.registerReceiver(babyInfoChangeRec, filter);
		IntentFilter myIntentFilter = new IntentFilter();
		// 注册宝贝信息改变的广播
		myIntentFilter.addAction(MqttConnection.ACTION_BABY_INFO_CHANGE);
		// 注册切换宝贝广播
		myIntentFilter.addAction("CHANGE_BABY_ACTION");
		activity.registerReceiver(babyInfoChangeRec, myIntentFilter);

		initView();// 初始化view

		try {
			initData();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化数据
		mListView.setSelection(mAdapter.getCount() - 1);

		rlCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						chatfragmentSelectCallWayActivity.class);
				intent.putExtra(EXTRA_NOW_BABYIMEI, now_babyimei);
				intent.putExtra(EXTRA_NOW_BABYPHONE, babyPhone);
				startActivity(intent);

				rlCall.setClickable(false);
				ivCall.setClickable(false);
			}
		});

		ivCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						chatfragmentSelectCallWayActivity.class);
				intent.putExtra(EXTRA_NOW_BABYIMEI, now_babyimei);
				intent.putExtra(EXTRA_NOW_BABYPHONE, babyPhone);
				startActivity(intent);

				rlCall.setClickable(false);
				ivCall.setClickable(false);
			}
		});

		rlRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断网络
				ivRefresh.callOnClick();

			}
		});

		ivRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断网络
				if (!CheckNetworkConnectionUtil.isNetworkConnected(context)) {
					ToastUtil.show(context,
							context.getResources()
									.getString(R.string.nonetwork));
					return;
				}
				startRefreshAmin();
				ChatGetMsgs thread = new ChatGetMsgs(activity, 20,
						BabyFragmentActivity.handler);
				thread.start();

				ChatNewMsgService.actionPing(activity);
			}
		});

		/* /////////////////////////////////////////////////////////////////// */

		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		ChatFragment.updateListView();
		if (isVisibleToUser) {
			// 判断网络
			BabyFragmentActivity.rlChatTip.setVisibility(8);

			ChatUtil.removeNotify(context, MqttConnection.NOTIFY_ID);
			NewMsgComeUtil.enterChatInterface();
		} else {
			NewMsgComeUtil.outChatInterface();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		rlCall.setClickable(true);
		ivCall.setClickable(true);
	}

	public static void startRefreshAmin() {
		RotateAnimation animation = new RotateAnimation(360f, 0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(500);
		animation.setRepeatCount(Animation.INFINITE);
		refreshAminTime = System.currentTimeMillis();
		ivRefresh.startAnimation(animation);
	}

	public static void stopRefreshAmin() {
		if (ivRefresh != null) {
			ivRefresh.clearAnimation();
		}
	}

	// 初始化宝贝下拉框
	public void initBabyPopuWindow() {
		groups = new ArrayList<Baby>();
		if (!CheckNetworkConnectionUtil.isNetworkConnected(activity)) {
			ToastUtil.show(activity,
					context.getResources().getString(R.string.no_internet));
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
							groups = babyList;
							text_select_baby.setText(groups.get(0)
									.getBabyname());
							now_baby = groups.get(0);
							now_babyimei = now_baby.getBabyimei();
							getbabyallinfo(now_babyimei);
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
									activity,
									context.getResources().getString(
											R.string.server_busy), 1000).show();
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
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
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
					ToastUtil.show(activity, activity.getString(R.string.nonetwork));
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
				// 发送广播
				activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));
				// showDialog();
				// new Thread(new Runnable() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				// getbabyallinfo(now_babyimei);
				// }
				// }).start();
			}
		});
	}

	private void getbabyallinfo(String imei) {
		// TODO 获得宝贝信息
		HttpPost request = new HttpPost(URL_GETBABYALLINFO);
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", imei);
			param.put("userid", MbApplication.getGlobalData().getNowuser()
					.getUserid());

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
					String phone = datajson.getString("phone");
					babyPhone = phone;

					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 666;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 666;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

					/*
					 * Toast.makeText(context, "获取卡路里失败!原因：" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */

				}

			} else {
				what = -1;
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 666;
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

	/* 伍建鹏//////////////////////////////////////////////////////////////// */

	/**
	 * 初始化view
	 */
	public void initView() {
		// 返回定位
		backlocation = (ImageButton) view.findViewById(R.id.but_backlocation);
		backlocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BabyFragmentActivity.mPager.setCurrentItem(0,false);
			}
		});
		mListView = (ListView) view.findViewById(R.id.chatfragment_listview);
		llRecordingBtn = (LinearLayout) view
				.findViewById(R.id.chatfragment_ll_bottom);
		llRecordingBtn.setOnTouchListener(this);
		rlRecordingInterface = (RelativeLayout) view
				.findViewById(R.id.chatfragment_rl_record_part);
		ivRecordRotateRound = (ImageView) view
				.findViewById(R.id.chatfragment_iv_record_rotate_round);
		tvRecordBtnText = (TextView) view
				.findViewById(R.id.chatfragment_tv_bottom_text);
		rlCall = (RelativeLayout) view.findViewById(R.id.rel_btn_call);
		ivCall = (ImageView) view.findViewById(R.id.btn_call);
		rlRefresh = (RelativeLayout) view.findViewById(R.id.rel_btn_map);
		ivRefresh = (ImageView) view.findViewById(R.id.but_map);
	}

	/**
	 * 模拟加载语音历史，实际开发可以从数据库中读出
	 * 
	 * @param string
	 * 
	 * @throws InterruptedException
	 */
	public void initData() throws InterruptedException {

		/* 在这里向服务器获取聊天记录 */
		// mDataArrays = getChatMsgs(now_babyimei);

		mAdapter = new ChatMsgViewAdapter(context, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	public static List<ChatAudioEntity> getAudiosList() {
		return mDataArrays;
	}

	/**
	 * 移除对应audio的语音气泡并刷新界面
	 * 
	 * @param audioId
	 */
	public static void removeAudioAndUpdate(String audioId) {
		for (int i = 0; i < mDataArrays.size(); i++) {
			ChatAudioEntity audio = mDataArrays.get(i);
			if (audioId.equals(audio.getAudioId())) {
				mDataArrays.remove(i);
				mAdapter = new ChatMsgViewAdapter(context, mDataArrays);
				mListView.setAdapter(mAdapter);
				// mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
				mListView.setSelection(mListView.getCount() - 1);// 发送一条语音时，ListView显示选择最后一项
				return;
			}
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param audio
	 *            要发送的语音实体
	 * @return 发送成功返回true,否则返回false
	 * @throws InterruptedException
	 */
	private void sendAudio(ChatAudioEntity audio) throws InterruptedException {

		if (!CheckNetworkConnectionUtil.isNetworkConnected(context)) {
			ToastUtil.show(context,
					context.getResources().getString(R.string.nonetwork));
			audio.setSendState(ChatAudioEntity.SEND_FAIL);
			updateListData(audio);
			ChatFragment.updateListView();
			return;
		}

		// 显示风火轮
		WaitDialog.getIntence(activity)
				.setContent(activity.getString(R.string.sending)).show();

		ChatSendAudioThread audioThread = new ChatSendAudioThread(context,
				audio);
		audioThread.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			long now = System.currentTimeMillis();
			if (now - recordDownTime <= 1000) {
				ToastUtil.show(
						activity,
						context.getResources().getString(
								R.string.time_is_too_short));
				recordDownTime = now;
				return true;
			}
			
			sendTime = ChatUtil.getDate();

			// 如果正在播放语音，把语音播放停止
			stopPlayAudio();

			if (RECODE_STATE != RECORD_ING) {
				try {
					audioId = ChatUtil.buildAudioId(sendTime);
					mr = new AudioRecorder(audioId, false, MbApplication
							.getGlobalData().getNowuser().getUserid()); // 把audioId作为文件名

					RECODE_STATE = RECORD_ING;
					tvRecordBtnText.setText(context.getResources().getString(
							R.string.loosen_and_send));
					showRecordingInterface();

					mr.start();
					StartRecordTimethread(); // 开机录音计时线程
				} catch (IOException e) {
					ToastUtil.show(
							context,
							context.getResources().getString(
									R.string.sd_storage_not_enough));
				} catch (ParseException e) {
					ToastUtil.show(
							context,
							context.getResources().getString(
									R.string.record_error));
				}

			}

			break;

		case MotionEvent.ACTION_UP:
			if (RECODE_STATE == RECORD_ING) {

				recordDownTime = System.currentTimeMillis();

				rlRecordingInterface.clearAnimation();
				rlRecordingInterface.setVisibility(recordingInterfaceNotShow); // 隐藏已经显示的录音时的界面
				try {
					mr.stop(); // 停止录音
					RECODE_STATE = RECODE_END;
					voiceValue = 0.0; // 初始化录音时间
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (recordTime < MIX_TIME) {
					showWarnToast();
					tvRecordBtnText.setText(context.getResources().getString(
							R.string.hold_press_on_and_say));
					RECODE_STATE = RECORD_NO;
					ChatUtil.deleteAudioFile(audioId, false, MbApplication
							.getGlobalData().getNowuser().getUserid());
				} else {
					tvRecordBtnText.setText(context.getResources().getString(
							R.string.hold_press_on_and_say));

					try {
						ChatAudioEntity audio = new ChatAudioEntity(audioId,
								UserType.USER_TYPE_APP_USER);
						audio.setDuration(getDuration(recordTime));
						audio.setDate(sendTime);
						audio.setUserId(MbApplication.getGlobalData()
								.getNowuser().getUserid());
						audio.setComMsg(false);

						/* 发送语音并保存录音信息到服务器 */
						// 发送录完的音频文件
						sendAudio(audio);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
			break;
		}

		return true;
	}

	private void stopPlayAudio() {
		sendTime = ChatUtil.getDate();
		if (mAdapter != null) {
			ChatAudioPlayer player = mAdapter.getPlayer();
			if (player != null) {
				player.stopPlay();
			}
		}
	}

	private void showRecordingInterface() {
		rlRecordingInterface.setVisibility(recordingInterfaceShow); // 显示隐藏的录音时的界面
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(1500);
		rlRecordingInterface.setAnimation(animation);
		SetRecordRotateRound(); // 设置并开始录音时背景圈的旋转
	}

	/**
	 * @param msgsList
	 * @return 如果msgsList为null时返回-1
	 */
	public static int updateListData(List<ChatAudioEntity> msgsList) {

		if (msgsList == null) {
			return -1;
		}

		if (msgsList.size() == 0) {
			return 0;
		}

		Log.w("getmessage", "updateListData()方法的参数：msgsList.size = " + msgsList.size());

		int count = 0; // 记录新消息个数

		for (int i = msgsList.size() - 1; i >= 0; i--) {
			boolean isExsit = updateListData(msgsList.get(i));
			if (isExsit == false) {
				count ++;
			}
		}

		if (isFristGetMsg) {
			count = 0;
			isFristGetMsg = false;
		}

		return count;
	}

	/**
	 * 判断该audio是否在audio中，如果不存在就就加进列表中，如果存在就不加进列表中
	 * 
	 * @param audio
	 *            要添加audio
	 * @return 如果audio已存在返回true，不存在返回false
	 */
	public static boolean updateListData(ChatAudioEntity audio) {

		Log.w("getmessage", "获取下来的：" + audio.getAudioId());

		if (audio != null && mDataArrays != null) {
			for (int i = 0; i < mDataArrays.size(); i++) {
				if (mDataArrays.get(i).getAudioId().equals(audio.getAudioId())) {
					return true;
				}
			}
			Log.w("getmessage", "添加到mDataArrays中的：" + audio.getAudioId());
			mDataArrays.add(0, audio);
		}
		return false;
	}

	public static void updateListView() {
		if (mAdapter == null) {
			return;
		}

		mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
		mAdapter.analyzeData();

//		mAdapter = new ChatMsgViewAdapter(context, mDataArrays);
//		mListView.setAdapter(mAdapter);

		mListView.setSelection(mListView.getCount() - 1);// 发送一条语音时，ListView显示选择最后一项

		if (mDataArrays != null && mDataArrays.size() > 0) {
			SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME,
					Context.MODE_PRIVATE);
			mPrefs.edit().putString(LAST_TIME, mDataArrays.get(0).getDate())
					.commit();
		}

	}

	public static void reshowListView() {
		mAdapter = new ChatMsgViewAdapter(context, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 设置并开始录音时背景圈的旋转
	 */
	private void SetRecordRotateRound() {
		RotateAnimation rotateAnimation = new RotateAnimation(450, 90,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setInterpolator(new LinearInterpolator()); // 旋转不停顿
		rotateAnimation.setDuration(2000); // 设置旋转一次持续的时间
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE); // 设置旋转次数
		ivRecordRotateRound.startAnimation(rotateAnimation);
	}

	/**
	 * 开启录音计时线程
	 */
	void StartRecordTimethread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// 录音时间太短时Toast显示
	void showWarnToast() {
		Toast toast = Toast.makeText(context,
				context.getResources().getString(R.string.timeshort), 3000);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setMargin(0f, 0f);
		toast.show();
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(context.getResources().getString(
				R.string.get_date_ing));
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

	public static void removeAllData() {
		mDataArrays.clear();
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 录音计时线程
	 */
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recordTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recordTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0); // 录音停止
				} else {
					try {
						Thread.sleep(200);
						recordTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue = mr.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					// 录音超过20秒自动停止
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_END;

						// 关闭录音界面
						rlRecordingInterface
								.setVisibility(recordingInterfaceNotShow);
						try {
							mr.stop(); // 停止录音
							voiceValue = 0.0; // 初始化音量值
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (recordTime < MIX_TIME) {
							showWarnToast();
							tvRecordBtnText.setText(context.getResources().getString(
									R.string.hold_press_on_and_say));
							RECODE_STATE = RECORD_NO;
							ChatUtil.deleteAudioFile(audioId, false,
									MbApplication.getGlobalData().getNowuser()
											.getUserid());
						} else {
							tvRecordBtnText.setText(context.getResources().getString(
									R.string.hold_press_on_and_say));

							try {
								String audioId = ChatUtil
										.buildAudioId(sendTime);
								ChatAudioEntity audio = new ChatAudioEntity(
										audioId, UserType.USER_TYPE_APP_USER);
								audio.setUserId(MbApplication.getGlobalData()
										.getNowuser().getUserid());
								audio.setDate(sendTime);
								audio.setDuration(getDuration(recordTime));
								audio.setComMsg(false);

								/* 发送语音并保存录音信息到服务器 */
								// 发送录完的音频文件
								sendAudio(audio);

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					break;
				case 1:
					/* 录音过程中要进行的操作 */
					break;
				default:
					break;
				}

			}

		};
	};

	private int getDuration(float recordTime) {
		if (recordTime == (int) recordTime) {
			return (int) recordTime;
		}
		return (int) recordTime + 1;
	}

	@Override
	public void onClick(View arg0) {

	}

	/* /////////////////////////////////////////////////////////////////////// */

	public static void setAudiosList(List<ChatAudioEntity> serverAudios) {
		mDataArrays = serverAudios;
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

				if (key == 666 && g2 == 1) {
					dismissDialog();

				}

				if (key == 666 && g2 == -1) {
					dismissDialog();
					String mesg = "";
					ErroNumberChange changge = new ErroNumberChange(context);
					mesg = changge.chang(erro);
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.getbabyphoneerror)
									+ mesg, 3000).show();
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
	public void onDestroy() {
		activity.unregisterReceiver(babyInfoChangeRec);
		super.onDestroy();
	}

	private BroadcastReceiver babyInfoChangeRec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction() == null ? "" : intent
					.getAction();
			if (action.equals("CHANGE_BABY_ACTION")) {
				groups = MbApplication.getGlobalData().getGroups();
				now_baby = MbApplication.getGlobalData().getNowBaby();
				now_babyimei = MbApplication.getGlobalData().getNowBaby()
						.getBabyimei();
				text_select_baby.setText(now_baby.getBabyname());
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						getbabyallinfo(now_babyimei);
					}
				}).start();
			} else {
				Log.w("babyInfoChange", "收到baby信息改变的广播：" + intent);
				if (action.equals(MqttConnection.ACTION_BABY_INFO_CHANGE)) {
					ChatFragment.this.initBabyPopuWindow();
				}
			}
		}
	};

}
