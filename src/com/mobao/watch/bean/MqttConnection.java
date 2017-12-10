package com.mobao.watch.bean;

import java.net.URISyntaxException;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.client.Tracer;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.InformationCenterActivity;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.service.ChatNewMsgService;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.NewMsgComeUtil;
import com.mobao.watch.util.UserType;

public class MqttConnection {

	private Context context;

	// the port at which the broker is running.
	private static int MQTT_BROKER_PORT_NUM = 1883;

	// Let's set the internal keep alive for MQTT to 15 mins. I haven't tested
	// this value much. It could probably be increased.
	private static short MQTT_KEEP_ALIVE = 60 * 15;

	// The broker should not retain any messages.
	private static boolean MQTT_RETAINED_PUBLISH = false;

	// Notification manager to displaying arrived push notifications
	private NotificationManager mNotificationManager;

	private int countConnFailAtFrist = 0;
	private int countConn = 0;

	public final static int MSG_WHAT_UPDATA_LIST = 501;
	public final static int MSG_WHAT_NEW_MSG = 502;
	public final static int MSG_WHAT_NEW_FAMILY = 504;

	// Notification id
	private static final int NOTIF_CONNECTED = 0;

	public static final String ACTION_NEW_BABY_TIME_LOCATE = "newbabytimelocate";
	public static final String ACTION_NEW_FAMILY = "newfamily";
	public static final String ACTION_BABY_INFO_CHANGE = "baby_info_change";
	public static final String ACTION_NOW_LOCATE = "now_locate";
	public static final String ACTION_BABY_AUTH_OK = "baby_auth_ok";
	public static final String ACTION_BABYHASDELETE = "now_babyhasdelete";
	public static final String ACTION_BABY_IS_ONLINE = "isonline";

	public static final String EXTRA_NAME_BABY_IMEM = "imei";
	public static final String EXTRA_NAME_NOW_LOCATE_ADDRESS = "address";
	public static final String EXTRA_NAME_NOW_LOCATE_NAME = "name";
	public static final String EXTRA_NAME_NOW_LOCATE_LON = "lon";
	public static final String EXTRA_NAME_NOW_LOCATE_LAT = "lat";
	public static final String EXTRA_NAME_NOW_LOCATE_TIME = "time";
	public static final String EXTRA_NAME_NOW_LOCATE_RADIUS = "radius";
	public static final String EXTRA_NAME_BABY_IS_ONLINE = "babyisonline";


	private static final String USER_NAME = "w06";
	private static final String PASSWORD = "w06app";

	private SharedPreferences mPrefs;

	private CallbackConnection callbackConnection;

	private Handler handler;

	private MQTT mqtt;
	private String topic;

	public final static int NOTIFY_ID = 1;

	public MqttConnection(String brokerHostName, String topic, Context context,
			Handler handler) throws URISyntaxException {

		this.context = context;
		this.topic = topic;
		this.handler = handler;

		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		initMqtt(brokerHostName);

		// 连接
		connect();

		mPrefs.edit()
				.putLong(ChatNewMsgService.START_TIME,
						System.currentTimeMillis()).commit();

		// Start the keep-alives
		startKeepAlives();
	}

	private void initMqtt(String brokerHostName) throws URISyntaxException {
		/* 初始化Preferences实例 */
		mPrefs = context.getSharedPreferences(ChatNewMsgService.TAG,
				Context.MODE_PRIVATE);

		// Create connection spec
		String mqttConnSpec = "tcp://" + brokerHostName + ":"
				+ MQTT_BROKER_PORT_NUM;
		String clientID = ChatNewMsgService.MQTT_CLIENT_ID + "/"
				+ mPrefs.getString(ChatNewMsgService.PREF_DEVICE_ID, "");
		Log.w("mqtt", "clientID = " + clientID);

		// Create the mqtt and connect
		mqtt = new MQTT();
		mqtt.setHost(mqttConnSpec);
		mqtt.setClientId(clientID);
		mqtt.setUserName(USER_NAME);
		mqtt.setPassword(PASSWORD);
		mqtt.setCleanSession(true);
		mqtt.setKeepAlive(MQTT_KEEP_ALIVE);

		// 失败重连接
		mqtt.setConnectAttemptsMax(10L);// 客户端首次连接到服务器时，连接的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
		mqtt.setReconnectAttemptsMax(3L);// 客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
		mqtt.setReconnectDelay(1000L);// 首次重连接间隔毫秒数，默认为10ms
		mqtt.setReconnectDelayMax(30000L);// 重连接间隔毫秒数，默认为30000ms

		// 选择消息分发队列
		mqtt.setDispatchQueue(Dispatch.createQueue(topic));// 若没有调用方法setDispatchQueue，客户端将为连接新建一个队列。如果想实现多个连接使用公用的队列，显式地指定队列是一个非常方便的实现方法

		// 使用回调式API
		callbackConnection = mqtt.callbackConnection();
	}

	private void connect() {

		setTracer(callbackConnection);
		setlistener(callbackConnection);
		startConn(callbackConnection);
	}

	private void setTracer(CallbackConnection callbackConnection) {
		mqtt.setTracer(new Tracer() {
			@Override
			public void onReceive(MQTTFrame frame) {
				Log.w("mqtt", "Tracer中的receive：frame = " + frame.toString());
			}

			@Override
			public void onSend(MQTTFrame frame) {
				Log.w("mqtt", "Tracer中的send：frame = " + frame.toString());
			}

			@Override
			public void debug(String message, Object... args) {
				Log.w("mqtt",
						"Tracer中的receive：frame = "
								+ String.format("debug: " + message, args));
			}

		});
	}

	// Disconnect
	public void disconnect() {
		Log.w("mqtt", "discontent");
		stopKeepAlives();
		if (callbackConnection != null) {
			ChatNewMsgService.mqttIsStop = true;
			callbackConnection.disconnect(null);
		}
	}

	public void sendKeepAlive() {
		Log.w("mqtt", "Sending keep alive");
		// publish to a keep-alive topic
		if (!connIsNull()) {
			publishToTopic(ChatNewMsgService.MQTT_CLIENT_ID + "/keepalive",
					mPrefs.getString(ChatNewMsgService.PREF_DEVICE_ID, ""));
			return;
		}
	}

	/*
	 * Sends a message to the message broker, requesting that it be published to
	 * the specified topic.
	 */
	private void publishToTopic(String topicName, String message) {
		if (callbackConnection == null) {
			// quick sanity check - don't try and publish if we don't have
			// a connection
			Log.w("mqtt", "No connection to public to");
		} else {
			callbackConnection.publish(topicName, message.getBytes(),
					QoS.AT_LEAST_ONCE, MQTT_RETAINED_PUBLISH, null);
		}
	}

	// Schedule application level keep-alives using the AlarmManager
	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(context, ChatNewMsgService.class);
		i.setAction(ChatNewMsgService.ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(ChatNewMsgService.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis()
						+ ChatNewMsgService.KEEP_ALIVE_INTERVAL,
				ChatNewMsgService.KEEP_ALIVE_INTERVAL, pi);
	}

	// Remove all scheduled keep alives
	private void stopKeepAlives() {

		Intent i = new Intent();
		i.setClass(context, ChatNewMsgService.class);
		i.setAction(ChatNewMsgService.ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(ChatNewMsgService.ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private void setlistener(CallbackConnection callbackConnection) {
		callbackConnection.listener(new Listener() {

			// 接收订阅话题发布的消息
			@Override
			public void onPublish(UTF8Buffer topic, Buffer payload,
					Runnable onComplete) {
				Log.w("mqtt", "接受到的推送：topic = " + topic.toString());

				String strResult = payload.toString();
				strResult = strResult.substring(strResult.indexOf("{")); // 去掉字符串非json的头字符
				Log.w("mqtt", "接受的推送：" + strResult);

				onComplete.run(); // 告诉服务器已接受了信息

				try {
					JSONObject result = new JSONObject(strResult);
                    System.out.println("MQTT的内容："+result.toString());
					String msgType = result.getString("type");

					if (msgType.equals("newbabytimelocate")) {

						String imei = result.getString("imei");

						Log.w("mqtt", "是newbabytimelocate信息");
						Intent intent = new Intent();
						intent.setAction(ACTION_NEW_BABY_TIME_LOCATE);

						if (imei != null) {
							intent.putExtra(EXTRA_NAME_BABY_IMEM, imei);
						}
						context.sendBroadcast(intent);
					} else if (msgType.equals("lowvoltage")) {
						String name = result.getString("babyname");
						String imei = result.getString("imei");

						String title = context
								.getString(R.string.low_charge_tip);
						String content = context.getString(R.string.your_baby)
								+ name
								+ context.getString(R.string.watch_low_charge);

						CommonUtil.addNotificaction(context, title, content,
								InformationCenterActivity.class, NOTIFY_ID,imei);

						// 发广播
						context.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));

					} else if (msgType.equals("babyauthok")) {
						System.out.println("宝贝确定了");
						Intent intent = new Intent();
						intent.setAction(ACTION_BABY_AUTH_OK);
						context.sendBroadcast(intent);
					} else if (msgType.equals("newfamily")) {
						handler.sendEmptyMessage(MSG_WHAT_NEW_FAMILY);
					} else if (msgType.equals("babyinfomodify")) {
						Intent intent = new Intent();
						intent.setAction(ACTION_BABY_INFO_CHANGE);
						context.sendBroadcast(intent);
					} else if (msgType.equals("nowlocate")) {

						String imei = result.getString("imei");
						String address = result.getString("address");
						String name = result.getString("name");
						String lon = result.getString("lon");
						String lat = result.getString("lat");
						String time = result.getString("time");
						String radius = result.getString("radius");

						Intent intent = new Intent();
						intent.setAction(ACTION_NOW_LOCATE);
						intent.putExtra(EXTRA_NAME_BABY_IMEM, imei);
						intent.putExtra(EXTRA_NAME_NOW_LOCATE_ADDRESS, address);
						intent.putExtra(EXTRA_NAME_NOW_LOCATE_NAME, name);
						intent.putExtra(EXTRA_NAME_NOW_LOCATE_LON, lon);
						intent.putExtra(EXTRA_NAME_NOW_LOCATE_LAT, lat);
						intent.putExtra(EXTRA_NAME_NOW_LOCATE_TIME, time);
						intent.putExtra(EXTRA_NAME_NOW_LOCATE_RADIUS, radius);
						context.sendBroadcast(intent);

					} else if (msgType.equals("newmessage")) {

						JSONObject datas = result.getJSONObject("data");

						String id = datas.getString("id");
						String type = datas.getString("type");
						String audioid = datas.getString("audioid");
						int audiolen = datas.getInt("audiolen");
						String time = datas.getString("time");

						UserType userType = type.equals("user") ? UserType.USER_TYPE_APP_USER
								: UserType.USER_TYPE_BABY;
						ChatAudioEntity audio = new ChatAudioEntity(audioid,
								userType);
						audio.setComMsg(true);
						audio.setDate(time);
						audio.setDuration(audiolen);
						audio.setUserId(id);

						Log.w("mqtt", "audioid = " + audio.getAudioId());

						boolean isExist = ChatFragment.updateListData(audio);

						if (isExist == true) {
							// 语音不存在列表中
							return;
						}

						handler.sendEmptyMessage(MSG_WHAT_UPDATA_LIST);

						NewMsgComeUtil.addaNewCount();

						// 新消息到来提示
						handler.sendEmptyMessage(MSG_WHAT_NEW_MSG);
					} else if (msgType.equals("babyhasdelete")) {
						Intent intent = new Intent();
						intent.setAction(ACTION_BABYHASDELETE);
						context.sendBroadcast(intent);
					} else if (msgType.equals("babystatus")){
						String imei = result.getString("imei");
						String status = result.getString("status");
						boolean isOnline = true;
						if(status.equals("online")){
							isOnline = true;
						}else if(status.equals("offline")){
							isOnline = false;
						}
						
						Intent intent = new Intent();
						intent.setAction(ACTION_BABY_IS_ONLINE);
						intent.putExtra(EXTRA_NAME_BABY_IMEM, imei);
						intent.putExtra(EXTRA_NAME_BABY_IS_ONLINE, isOnline);
						context.sendBroadcast(intent);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// 连接失败
			@Override
			public void onFailure(Throwable value) {
				Log.w("mqtt", "监听到连接失败时：" + value.getMessage());
			}

			// 连接断开
			@Override
			public void onDisconnected() {
				Log.w("mqtt", "监听到连接断开了");
				if (!ChatNewMsgService.mqttIsStop) {
					Log.w("mqtt", "异常连接断开，准备手动重连");
					connect();
				}
			}

			@Override
			public void onConnected() {
				Log.w("mqtt", "监听到连接成功了");
			}
		});
	}

	private void startConn(final CallbackConnection callbackConnection) {
		callbackConnection.connect(new Callback<Void>() {

			@Override
			public void onSuccess(Void v) {
				Log.w("mqtt", "connect的连接成功了：");
				Log.w("mqtt", "订阅主题：" + topic);
				Topic[] topics = { new Topic(topic, QoS.AT_LEAST_ONCE) };
				callbackConnection.subscribe(topics, new Callback<byte[]>() {

					@Override
					public void onFailure(Throwable value) {
						Log.w("mqtt", "订阅主题失败：" + value.toString());
					}

					@Override
					public void onSuccess(byte[] qoses) {
						Log.w("mqtt", "订阅主题成功：" + qoses.toString());
					}
				});
			}

			@Override
			public void onFailure(Throwable value) {
				countConnFailAtFrist = countConnFailAtFrist + 1;
				Log.w("mqtt", "connect的连接失败第" + countConnFailAtFrist
						+ "次：value = " + value.toString());
				if (countConnFailAtFrist >= 10) {
					Log.w("mqtt", "10次了");
					disconnect();
				}

			}
		});
	}

	public boolean connIsNull() {
		if (callbackConnection == null) {
			return true;
		}
		return false;
	}

}
