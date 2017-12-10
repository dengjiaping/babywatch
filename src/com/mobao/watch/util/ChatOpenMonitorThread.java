package com.mobao.watch.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.chatfragmentSelectCallWayActivity;

public class ChatOpenMonitorThread extends Thread {

	private static String baseUrl = CommonUtil.baseUrl;
	private static final String OPEN_MONITOR = "setmonitor";
	private String babyId;
	private Context context;
	private String babyPhone;

	private final int MSG_WHAT_SHOW_TOAST = 410;
	private final int MSG_WHAT_SHOW_WAIT_DIALOG = 141;
	private final int MSG_WHAT_CLOSE_WAIT_DIALOG = 242;
	private final int MSG_WHAT_CALL = 243;

	public ChatOpenMonitorThread(Context context, String babyId,
			String babyPhone) {
		this.babyId = babyId;
		this.context = context;
		this.babyPhone = babyPhone;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String showStr = (String) msg.obj;
			switch (msg.what) {
			case MSG_WHAT_SHOW_TOAST:
				showStr = (showStr == null) ? context
						.getString(R.string.fail_to_open_monitor) : showStr;
				Toast toast = Toast.makeText(context, showStr, 3000);
				toast.setGravity(Gravity.CENTER, 0, -10);
				toast.show();
				break;

			case MSG_WHAT_SHOW_WAIT_DIALOG:

				if (CheckNetworkConnectionUtil.isNetworkConnected(context) == false) {
					ToastUtil.show(context,
							context.getString(R.string.no_internet));
					break;
				}

				WaitDialog dialog = WaitDialog
						.getIntence(context)
						.setContent(
								context.getString(R.string.sending_monitor_request))
						.setIsCanCancel(false);
				dialog.show();

				break;

			case MSG_WHAT_CLOSE_WAIT_DIALOG:
				WaitDialog.getIntence(context).dismiss();
				break;

			case MSG_WHAT_CALL:
				Intent intent2 = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:" + babyPhone));
				context.startActivity(intent2);
				break;

			}

		};
	};

	@Override
	public void run() {

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			baseUrl = "http://" + ip + ":8088/api/";
		}

		handler.sendEmptyMessage(MSG_WHAT_SHOW_WAIT_DIALOG);

		HttpPost post = new HttpPost(baseUrl.concat(OPEN_MONITOR));

		JSONObject json = new JSONObject();

		try {
			json.put("imei", babyId);

			post.setEntity(new StringEntity(json.toString()));

			HttpResponse response = new DefaultHttpClient().execute(post);

			Log.w("openMonitor", response.getStatusLine().getStatusCode() + "");

			if (response.getStatusLine().getStatusCode() == 200) {

				String res = EntityUtils.toString(response.getEntity());

				JSONObject result = new JSONObject(res);

				int status = result.getInt("status");
				Log.w("openMonitor", status + " : " + result.getString("msg"));
				if (status == 200) {
					// 提示请求成功的toast
					Message msg = Message.obtain();
					msg.obj = context.getResources().getString(
							R.string.has_sent_motior);
					msg.what = MSG_WHAT_SHOW_TOAST;
					handler.sendMessage(msg);

					// // 弹出等待腕表打开监听的风火轮
					// msg.obj = context
					// .getString(R.string.wait_watch_monitor_open_then_call);
					// msg.what = MSG_WHAT_SHOW_WAIT_DIALOG;
					// handler.sendMessage(msg);
					//
					// try {
					// Thread.sleep(3000);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }
					//
					// // 关闭风火轮
					// handler.sendEmptyMessage(MSG_WHAT_CLOSE_WAIT_DIALOG);
					//
					// 拨打电话
					handler.sendEmptyMessage(MSG_WHAT_CALL);

				} else {
					// 提示服务器繁忙或打开腕表监听失败的toast
					Message msg = Message.obtain();
					msg.obj = result.getString("msg").equals("10070") ? context
							.getString(R.string.fail_to_open_monitor) : context
							.getString(R.string.serverbusy);
					msg.what = MSG_WHAT_SHOW_TOAST;
					handler.sendMessage(msg);
				}

			} else {
				// 提示网络异常toast
				Message msg = Message.obtain();
				msg.obj = context.getResources().getString(
						R.string.networkunusuable);
				msg.what = MSG_WHAT_SHOW_TOAST;
				handler.sendMessage(msg);
			}

		} catch (Exception e) {
			// 提示网络异常toast
			Message msg = Message.obtain();
			msg.obj = context.getString(R.string.serverbusy);
			msg.what = MSG_WHAT_SHOW_TOAST;
			handler.sendMessage(msg);

			e.printStackTrace();
		}

		handler.sendEmptyMessage(MSG_WHAT_CLOSE_WAIT_DIALOG);

	}

}
