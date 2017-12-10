package com.mobao.watch.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.mb.zjwb1.R;
import com.mobao.watch.util.NotificationUtil;

import io.yunba.android.manager.YunBaManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class YunBaReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

			String info = null;
			String title = null;
			String imei=null;
			String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
			String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);

			// 在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
			StringBuilder showMsg = new StringBuilder();
			showMsg.append("Received message from server: ")
					.append(YunBaManager.MQTT_TOPIC).append(" = ")
					.append(topic).append(" ").append(YunBaManager.MQTT_MSG)
					.append(" = ").append(msg);

			Log.i("keis", "推送消息：msg=" + msg);

			try {
				// 解析json对象
				JSONObject Result = new JSONObject(msg.toString());
				Log.i("keis", "推送消息：Result=" + Result);

				String address = Result.getString("address");
				String condition = Result.getString("condition");
				imei = Result.getString("imei");
				String name = Result.getString("name");

				String status = null;
				if (condition.equals("enter")) {
					status = (context.getResources()
							.getString(R.string.reached));
				} else {
					status = (context.getResources().getString(R.string.levaed));
				}

				Log.i("keis", "宝贝：" + imei + " 离开：" + condition + " 地址："
						+ address);

				title = (context.getResources().getString(R.string.app_name));
				info = name + " " + status + " " + address;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 通知栏显示
			NotificationUtil.getYunBaNotification(context, title, info,imei);
		}

	}

}
