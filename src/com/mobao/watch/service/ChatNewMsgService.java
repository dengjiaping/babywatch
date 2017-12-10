package com.mobao.watch.service;

import java.net.URISyntaxException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.MqttConnection;
import com.mobao.watch.bean.NowUser;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.GetAtuthlist;
import com.mobao.watch.util.NewMsgComeUtil;
import com.mobao.watch.util.ToastUtil;

/* 
 * PushService that does all of the work.
 * Most of the logic is borrowed from KeepAliveService.
 * http://code.google.com/p/android-random/source/browse/trunk/TestKeepAlive/src/org/devtcg/demo/keepalive/KeepAliveService.java?r=219
 */
public class ChatNewMsgService extends Service {

	// this is the log tag
	public static final String TAG = "DemoPushService";

	// the IP address, where your MQTT broker is running.
	private static final String MQTT_HOST = "115.29.191.125";

	// MQTT client ID, which is given the broker. In this example, I also use
	// this for the topic header.
	// You can use this to run push notifications for multiple apps with one
	// MQTT broker.
	public final static String MQTT_CLIENT_ID = "w06";

	// These are the actions for the service (name are descriptive enough)
	public static final String ACTION_START = MQTT_CLIENT_ID + ".START";
	public static final String ACTION_STOP = MQTT_CLIENT_ID + ".STOP";
	public static final String ACTION_KEEPALIVE = MQTT_CLIENT_ID
			+ ".KEEP_ALIVE";
	public static final String ACTION_RECONNECT = MQTT_CLIENT_ID + ".RECONNECT";

	// Connectivity manager to determining, when the phone loses connection
	private ConnectivityManager mConnMan;

	// Whether or not the service has been started.
	public static boolean mStarted = false;

	// This the application level keep-alive interval, that is used by the
	// AlarmManager
	// to keep the connection active, even when the device goes to sleep.
	public static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 28;

	// Retry intervals, when the connection is lost.
	private static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
	private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;

	// Preferences instance
	private SharedPreferences mPrefs;
	// We store in the preferences, whether or not the service has been started
	public static final String PREF_STARTED = "isStarted";
	// We also store the deviceID (target)
	public static final String PREF_DEVICE_ID = "deviceID";
	// We store the last retry interval
	public static final String PREF_RETRY = "retryInterval";
	public static final String START_TIME = "startTime";

	private MqttConnection mConnection;

	private String topic;

	public static boolean mqttIsStop = false;

	public static boolean hasNewFamily = false;

	// Static method to start the service
	public static void actionStart(Context ctx) {
		Intent i = new Intent(ctx, ChatNewMsgService.class);
		i.setAction(ACTION_START);
		Log.w("mythread", "ACTION_START：" + i);
		ctx.startService(i);
	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, ChatNewMsgService.class);
		i.setAction(ACTION_STOP);
		Log.w("mythread", "ACTION_STOP：" + i);
		ctx.startService(i);
	}

	// Static method to send a keep alive message
	public static void actionPing(Context ctx) {
		Intent i = new Intent(ctx, ChatNewMsgService.class);
		i.setAction(ACTION_KEEPALIVE);
		Log.w("mythread", "ACTION_KEEPALIVE：" + i);
		ctx.startService(i);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		/* 初始化频道 */
		topic = "w06/user/"
				+ MbApplication.getGlobalData().getNowuser().getUserid() + "/#";

		// Get instances of preferences, connectivity manager and notification
		// manager
		mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);

		log("Creating service");
		mPrefs.edit().putLong(START_TIME, System.currentTimeMillis()).commit();

		mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		/*
		 * If our process was reaped by the system for any reason we need to
		 * restore our state with merely a call to onCreate. We record the last
		 * "started" value and restore it here if necessary.
		 */
		handleCrashedService();
	}

	// This method does any necessary clean-up need in case the server has been
	// destroyed by the system
	// and then restarted
	private void handleCrashedService() {
		if (wasStarted() == true) {
			log("Handling crashed service...");
			// stop the keep alives
			stopKeepAlives();

			// Do a clean start
			start();
		}
	}

	@Override
	public void onDestroy() {
		log("Service destroyed (started=" + mStarted + ")");

		// Stop the services, if it has been started
		if (mStarted == true) {
			stop();
		}

	}

	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);

		new Thread() {
			@Override
			public void run() {

				if (intent == null) {
					return;
				}

				double random = Math.random() * 10;
				Log.w("mythread", intent.getAction() + "  " + random + "开启线程");

				if (intent.getAction().equals(ACTION_STOP) == true) {
					ChatNewMsgService.this.stop();
					stopSelf();
				} else if (intent.getAction().equals(ACTION_START) == true) {
					mqttIsStop = false;
					ChatNewMsgService.this.start();

					// // 保持服务service是活动状态
					// while (!mqttIsStop) {
					// }
				} else if (intent.getAction().equals(ACTION_KEEPALIVE) == true) {
					keepAlive();
				} else if (intent.getAction().equals(ACTION_RECONNECT) == true) {
					if (isNetworkAvailable()) {
						reconnectIfNecessary();
					}
				}
				Log.w("mythread", intent.getAction() + "  " + random + "结束线程");
			}
		}.start();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// log helper function
	private void log(String message) {
		log(message, null);
	}

	private void log(String message, Throwable e) {
		if (e != null) {
			Log.e(TAG, message, e);
		} else {
			Log.w(TAG, message);
		}
	}

	// Reads whether or not the service has been started from the preferences
	private boolean wasStarted() {
		return mPrefs.getBoolean(PREF_STARTED, false);
	}

	// Sets whether or not the services has been started in the preferences.
	private void setStarted(boolean started) {
		mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
		mStarted = started;
	}

	private synchronized void start() {
		log("Starting service...");

		// Do nothing, if the service is already running.
		if (mStarted == true) {
			Log.w("yyy", "已经开启了service");
			return;
		}

		// Establish an MQTT connection
		connect();

		// Register a connectivity listener
		registerReceiver(mConnectivityChanged, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private synchronized void stop() {
		// Do nothing, if the service is not running.
		if (mStarted == false) {
			Log.w(TAG, "Attempt to stop connection not active.");
			return;
		}

		// Save stopped state in the preferences
		setStarted(false);

		// Remove the connectivity receiver
		unregisterReceiver(mConnectivityChanged);
		// Any existing reconnect timers should be removed, since we explicitly
		// stopping the service.
		cancelReconnect();

		// Destroy the MQTT connection if there is one
		if (mConnection != null) {
			mConnection.disconnect();
			mConnection = null;
		}
	}

	//
	private synchronized void connect() {
		log("Connecting...");
		// fetch the device ID from the preferences.
		String deviceID = mPrefs.getString(PREF_DEVICE_ID, null);

		// Create a new connection only if the device id is not NULL
		if (deviceID == null) {
			log("Device ID not found.");
		} else {
			try {
				mConnection = new MqttConnection(MQTT_HOST, topic,
						ChatNewMsgService.this, handler);
			} catch (URISyntaxException e) {
				log("MqttException: "
						+ (e.getMessage() != null ? e.getMessage() : "NULL"));
				if (isNetworkAvailable()) {
					scheduleReconnect(mPrefs.getLong(START_TIME, 0l));
				}
			}
			setStarted(true);
		}
	}

	private synchronized void keepAlive() {
		// Send a keep alive, if there is a connection.
		if (mStarted == true && mConnection != null) {
			mConnection.sendKeepAlive();
			return;
		}
	}

	// Schedule application level keep-alives using the AlarmManager
	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, ChatNewMsgService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + KEEP_ALIVE_INTERVAL,
				KEEP_ALIVE_INTERVAL, pi);
	}

	// Remove all scheduled keep alives
	private void stopKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, ChatNewMsgService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	// We schedule a reconnect based on the starttime of the service
	public void scheduleReconnect(long startTime) {
		// the last keep-alive interval
		long interval = mPrefs.getLong(PREF_RETRY, INITIAL_RETRY_INTERVAL);

		// Calculate the elapsed time since the start
		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		// Set an appropriate interval based on the elapsed time since start
		if (elapsed < interval) {
			interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
		} else {
			interval = INITIAL_RETRY_INTERVAL;
		}

		log("Rescheduling connection in " + interval + "ms.");

		// Save the new internval
		mPrefs.edit().putLong(PREF_RETRY, interval).commit();

		// Schedule a reconnect using the alarm manager.
		Intent i = new Intent();
		i.setClass(this, ChatNewMsgService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
	}

	// Remove the scheduled reconnect
	public void cancelReconnect() {
		Intent i = new Intent();
		i.setClass(this, ChatNewMsgService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void reconnectIfNecessary() {
		if (mStarted == true && mConnection == null) {
			log("Reconnecting...");
			connect();
		}
	}

	// This receiver listeners for network changes and updates the MQTT
	// connection
	// accordingly
	private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get network info
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			// Is there connectivity?
			boolean hasConnectivity = (info != null && info.isConnected()) ? true
					: false;

			log("Connectivity changed: connected=" + hasConnectivity);

			if (hasConnectivity) {
				reconnectIfNecessary();
			} else if (mConnection != null) {
				// if there no connectivity, make sure MQTT connection is
				// destroyed
				mConnection.disconnect();
				cancelReconnect();
				mConnection = null;
			}
		}
	};

	// Check if we are online
	private boolean isNetworkAvailable() {
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MqttConnection.MSG_WHAT_UPDATA_LIST:
				ChatFragment.updateListView();
				break;

			case MqttConnection.MSG_WHAT_NEW_MSG:
				NewMsgComeUtil.addNewMsgNotification(ChatNewMsgService.this);
				NewMsgComeUtil.ShowBottomTip();
				break;

			case MqttConnection.MSG_WHAT_NEW_FAMILY:
				if (BabyFragmentActivity.isEnterMainInterface) {
					GetAtuthlist gal = new GetAtuthlist(ChatNewMsgService.this);
					gal.get();
					hasNewFamily = false;
				} else {
					hasNewFamily = true;
				}
				break;
			}
			return false;
		}
	});

}