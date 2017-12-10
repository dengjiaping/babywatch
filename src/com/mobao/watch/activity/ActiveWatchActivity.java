package com.mobao.watch.activity;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mb.zjwb1.R;
import com.mobao.watch.bean.MqttConnection;
import com.mobao.watch.bean.RelactionShip;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.service.ChatNewMsgService;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.BabyNameEditTextWatcher;
import com.mobao.watch.util.CheckCanAddBabyOrNotThread;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.SubmitBabyPartInfoThread;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.WaitDialog;
import com.testin.agent.TestinAgent;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

public class ActiveWatchActivity extends Activity implements Callback {
	private ImageButton ibLast = null;
	private RelativeLayout rel_back = null;// 上一步按钮
	private ViewfinderView viewfinderView; // 扫描二维码组件
	private TextView text_activate;// 顶部标题
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private boolean playBeep;
	private boolean vibrate;
	private CaptureActivityHandler handler;
	private MediaPlayer mediaPlayer;
	protected int RESULT_CODE_HAS_VERIFY;
	private static final float BEEP_VOLUME = 0.10f;
	private String userid = null;// 当前用户id

	private TextView textRelationship; // 显示关系文本
	private RelativeLayout rel_select_relationship;
	public static ArrayList<RelactionShip> relactionship;// 关系列表
	public static final String GET_RELACTIONSHIP_ACTION = "GET_RELACTIONSHIP_ACTION";
	private String value = null;

	/* 伍建鹏//////////////////////////// */
	private EditText etName; // 宝贝昵称
	private EditText etWatchPhone; // 腕表号码
	private TextView tvNext; // 下一步
	private TextView tvImei; // 显示imei的textView

	private String babyImei = null;
	private String babyName = null;
	private String watchPhone = null;

	private boolean isRegister = true;

	BabyNameEditTextWatcher BabyNameWtcher;
	
	// 线程
	private CheckCanAddBabyOrNotThread ifAddbabyThread;
	
	public static final String INTENT_EXTRA_IS_REGISTER = "is_register";
	
	/* /////////////////////////////// */

	private boolean isWatchAuthok = false; // 判断腕表是否确认
	private boolean isWaitingWatchAuthok = false; // 判断用户是否已经点击下一步等待腕表确认

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.activation_watch_activity);

		CameraManager.init(ActiveWatchActivity.this);

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		ibLast = (ImageButton) this.findViewById(R.id.btnBack);
		rel_back = (RelativeLayout) findViewById(R.id.rel_back);
		rel_select_relationship = (RelativeLayout) findViewById(R.id.rel_select_relationship);
		textRelationship = (TextView) findViewById(R.id.textRelationship);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		text_activate = (TextView) findViewById(R.id.text_activate);

		/* 伍建鹏/////////////////////// */
		etName = (EditText) findViewById(R.id.et_activation_name);
		etWatchPhone = (EditText) findViewById(R.id.et_activation_watch_number);
		tvNext = (TextView) findViewById(R.id.textNext);
		tvImei = (TextView) findViewById(R.id.tv_activation_watch_imei);
		addListener();
		// 开启MQTT
		ChatNewMsgService.actionStart(ActiveWatchActivity.this);
		isRegister = getIntent().getBooleanExtra(INTENT_EXTRA_IS_REGISTER, true);
		Log.w("aaa", "isRegister = " + isRegister);
		/* ////////////////////////// */

		if (getIntent().getStringExtra("addother") != null) {
			text_activate.setText(R.string.activitewacth1);
		}
		if (getIntent().getStringExtra("userid") != null) {
			userid = getIntent().getStringExtra("userid");

		} else {
			MbApplication.getGlobalData().getNowuser().getUserid();
		}

		ActivityContainer.getInstance().finshActivity("VerifyActivity");
		ActivityContainer.getInstance().finshActivity("LogInfoActivity");

		// 获取关系列表
		getRelactionShipList();

		// 注册监听广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(GET_RELACTIONSHIP_ACTION);

		/* 伍建鹏//////////////////// */
		filter.addAction(MqttConnection.ACTION_BABY_AUTH_OK);
		/* /////////////////////// */

		registerReceiver(broadcastReceiver, filter);

	}

	private void getRelactionShipList() {
		new Thread() {
			public void run() {
				final ArrayList<RelactionShip> list = BabyServer
						.getRelationShipList();
				if (list != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							relactionship = list;
							MbApplication.getGlobalData().setRelactionship(
									relactionship);
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(ActiveWatchActivity.this,
									getString(R.string.serverbusy), 3000)
									.show();
						}
					});
				}
			}
		}.start();
		rel_select_relationship.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 弹出选择框
				startActivity(new Intent(ActiveWatchActivity.this,
						SelectRelactionShipActivity.class));
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

		// quit the scan view
		ibLast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rel_back.callOnClick();
			}
		});
		rel_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		reScan();
	}

	public void reScan() {
		if (handler != null) {
			handler.restartPreviewAndDecode();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {

		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result1, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result1.getText();
		
		Log.w("scanresult", "截取前：" + resultString);
		
		if (resultString.equals("")) {
			Toast.makeText(ActiveWatchActivity.this,
					getResources().getString(R.string.nonetworknotexit), 3000)
					.show();
			finishActivity();

		} else {
			int index = resultString.indexOf('=');
			resultString = resultString.substring(index + 1, index + 16);
			Log.w("scanresult", "截取后：" + resultString);
			
			// 扫描成功
			// if (resultString.length() > 15) {
			// resultString = resultString.substring(0, 15);
			// }
			if (resultString.length() > 15 || !isNumeric(resultString)) {
				CustomDialog.Builder builder = new CustomDialog.Builder(
						ActiveWatchActivity.this);
				builder.setMessage(getResources().getString(
						R.string.nonetworknotexit));
				builder.setTitle(getResources().getString(
						R.string.dialog_body_text));
				builder.setPositiveButton(
						getResources().getString(R.string.sure),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finishActivity();
							}
						});
				builder.create().show();
				return;
			}
			if (!CheckNetworkConnectionUtil.isNetworkConnected(this)) {
				ToastUtil.show(this,
						getResources().getString(R.string.noworktoverificate));
				reScan();
				return;
			}
			final String imei = resultString;

			setImei(imei);

			MbApplication.getGlobalData().getNowuser().setImei(imei);

			WaitDialog
					.getIntence(ActiveWatchActivity.this)
					.setContent(
							getString(R.string.verfying_2d_code_please_wait))
					.show();
			ifAddbabyThread = new CheckCanAddBabyOrNotThread(
					ActiveWatchActivity.this, imei);
			ifAddbabyThread.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishActivity();
		}

		return super.onKeyDown(keyCode, event);
	}

	public void finishActivity() {
		ActivityContainer.getInstance().finshActivity("VerifyActivity");
		ActivityContainer.getInstance().finshActivity("LogInfoActivity");
		
		//判断是否在注册页面进来的
		if (isRegister == false) {
			startActivity(new Intent(ActiveWatchActivity.this,
					WatchMangerActivity.class));
		} else {
			//关闭mqtt
			ChatNewMsgService.actionStop(ActiveWatchActivity.this);
		}

		ActiveWatchActivity.this.finish();
	}

	public boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(GET_RELACTIONSHIP_ACTION)) {

				String relate = intent.getExtras().getString("relate");
				value = intent.getExtras().getString("value");
				textRelationship.setText(relate);

			} else if (action.equals(MqttConnection.ACTION_BABY_AUTH_OK)) {
				/* 伍建鹏////////////////////// */
				//接受到腕表确认的广播
				isWatchAuthok = true;
				if (isWaitingWatchAuthok == true) {
					if (ifAddbabyThread == null) {
						ToastUtil.show(ActiveWatchActivity.this, getResources()
								.getString(R.string.please_input_2d_code));
						return;
					}
					ToastUtil.show(ActiveWatchActivity.this, getResources()
							.getString(R.string.WatchAuthok));
					startSubmitBabyInfoThread();  //请求addbaby接口添加宝贝
				}
				/* ////////////////////////// */
			}
		}
	};

	/* 伍建鹏///////////////////////////////////// */
	private void addListener() {
		addTvNextListener();
		addEtBabyNameListener();
	}

	private void addEtBabyNameListener() {
		BabyNameWtcher = new BabyNameEditTextWatcher(ActiveWatchActivity.this, etName);
	}

	private void addTvNextListener() {
		// 下一步
		tvNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 判断填写的信息是否合格
				if (ifAddbabyThread == null) {
					ToastUtil.show(ActiveWatchActivity.this, getResources()
							.getString(R.string.please_input_2d_code));
					return;
				}

				if (ifAddbabyThread.isManager == true) {

					Log.w("addbaby", "value = " + value);

					babyName = etName.getText().toString();
					if (babyName == null || "".equals(babyName)) {
						ToastUtil.show(ActiveWatchActivity.this, getResources()
								.getString(R.string.input_baby_name));
						return;
					}

					watchPhone = etWatchPhone.getText().toString();
					if (watchPhone == null || "".equals(watchPhone)) {
						ToastUtil.show(ActiveWatchActivity.this, getResources()
								.getString(R.string.input_watch_number));
						return;
					}

				}

				if (value == null) {
					ToastUtil.show(ActiveWatchActivity.this, getResources()
							.getString(R.string.input_relation));
					return;
				}

				if (babyName == null) {
					babyName = "";
				}

				if (watchPhone == null) {
					watchPhone = "";
				}

				if (isWatchAuthok == true || ifAddbabyThread.isManager == false) { 
					// 腕表已经按确认

					startSubmitBabyInfoThread();  //请求addbaby接口添加宝贝

				} else { // 腕表还没按确认

					WaitDialog dialog = WaitDialog
							.getIntence(ActiveWatchActivity.this);
					dialog.setCanceledOnTouchOutside(false);
					dialog.setContent(
							ActiveWatchActivity.this.getResources().getString(
									R.string.please_to_wait_authok)).show();
					dialog.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								isWaitingWatchAuthok = false;
							}
							return false;
						}
					});
					isWaitingWatchAuthok = true;

				}
			}
		});
	}

	private void startSubmitBabyInfoThread() {

		if (!CheckNetworkConnectionUtil
				.isNetworkConnected(ActiveWatchActivity.this)) {
			//判断是否收到腕表的确认
			if (isWatchAuthok == false) {
				//此情况未知腕表是否确认，所以要重新扫描二维码
				ToastUtil.show(ActiveWatchActivity.this, getResources()
						.getString(R.string.no_network_scan_agan));
				initInterface();
			} else {
				//如果腕表已经确认，只需重新点击下一步
				ToastUtil.show(ActiveWatchActivity.this, getResources()
						.getString(R.string.no_network_try_again));
			}
			return;
		}

		SubmitBabyPartInfoThread thread = new SubmitBabyPartInfoThread(
				ActiveWatchActivity.this, babyImei, MbApplication
						.getGlobalData().getNowuser().getUserid(), value,
				babyName, watchPhone, ifAddbabyThread.isManager);
		thread.start();
	}

	public void setImei(String babyImei) {
		this.babyImei = babyImei;
		tvImei.setText(babyImei);
	}

	/**
	 * 设置宝贝昵称和腕表号码都能编辑
	 */
	public void setCanEdit() {
		etName.setEnabled(true);
		etWatchPhone.setEnabled(true);
	}

	/**
	 * 设置宝贝昵称和腕表号码都不能编辑，且设置显示的值
	 * 
	 * @param name
	 *            宝贝昵称，为空时设置成默认显示文字
	 * @param phone
	 *            腕表号码，为空设置成默认显示文字
	 */
	public void setNotCanEdit(String name, String phone) {

		Log.w("ifaddbaby", "setNotCanEdit方法中的：name = " + name + "  phone = "
				+ phone);

		if (name != null) {
			this.babyName = name;
		} else {
			this.babyName = "";
		}

		if (phone != null) {
			this.watchPhone = phone;
		} else {
			this.watchPhone = "";
		}

		etName.setText(name);
		etWatchPhone.setText(phone);
		etName.setEnabled(false);
		etWatchPhone.setEnabled(false);
	}


	private void initInterface() {

		reScan();
		tvImei.setText("");
		etName.setText("");
		etWatchPhone.setText("");

		etName.setEnabled(false);
		etWatchPhone.setEnabled(false);

		babyImei = null;
		babyName = null;
		watchPhone = null;

		BabyNameWtcher.initContentOfEtName();

		// 线程
		ifAddbabyThread = null;

		isWatchAuthok = false; // 判断腕表是否确认
		isWaitingWatchAuthok = false; // 判断用户是否已经点击下一步等待腕表确认

	}

	/* /////////////////////////////////////////// */

}
