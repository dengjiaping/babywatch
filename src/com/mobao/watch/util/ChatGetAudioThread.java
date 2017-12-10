package com.mobao.watch.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.myInterface.AudioPlayInterface;

public class ChatGetAudioThread extends Thread {
	private static String baseUrl = CommonUtil.baseUrl;

	private static final String GET_AUDIO = "getaudio";

	private static final int MSG_ARG1_GET_AUDIO_NOT_EXIST = 1;

	private static final int MSG_ARG1_GET_AUDIO_INTERNET_Exception = 2;

	private static final int MSG_ARG1_GET_AUDIO_SUCCESS = 0;

	private static String nowAudioId;

	private Context context;
	private String audioId;
	private String path;
	private boolean isComMsg;
	private String userId;
	private AudioPlayInterface audioPlay;

	public ChatGetAudioThread(Context context, String audioId, String path,
			boolean isComMsg, String userId, AudioPlayInterface audioPlay) {
		nowAudioId = audioId;
		this.context = context;
		this.audioId = audioId;
		this.path = path;
		this.isComMsg = isComMsg;
		this.userId = userId;
		this.audioPlay = audioPlay;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == MSG_ARG1_GET_AUDIO_NOT_EXIST) {
				// 告诉用户语音不存在
				String showStr = (String) msg.obj;
				Toast toast = Toast.makeText(context, showStr, 3000);
				toast.setGravity(Gravity.CENTER, 0, -10);
				toast.show();
			} else if (msg.arg1 == MSG_ARG1_GET_AUDIO_INTERNET_Exception) {
				String showStr = (String) msg.obj;
				Toast toast = Toast.makeText(context, showStr, 3000);
				toast.setGravity(Gravity.CENTER, 0, -10);
				toast.show();
			} else if (msg.arg1 == MSG_ARG1_GET_AUDIO_SUCCESS) {
				// 播放录音
				// 保证不会有两个录音同时使用这个线程
				if (!nowAudioId.equals(audioId)) {
					return;
				}

				audioPlay.playAudio(path);

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

		// 保证不会有两个录音同时使用这个线程
		if (!nowAudioId.equals(audioId)) {
			return;
		}

		HttpPost post = new HttpPost(baseUrl.concat(GET_AUDIO));

		JSONObject params = new JSONObject();

		try {
			params.put("audioid", audioId);
			params.put("userid", userId);

			post.setEntity(new StringEntity(params.toString()));

			// 保证不会有两个录音同时使用这个线程
			if (!nowAudioId.equals(audioId)) {
				return;
			}

			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(response.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断获取是否成功
				int status = result.getInt("status");

				if (status == 200) { // 获取成功

					JSONObject data = result.getJSONObject("data");

					// 保证不会有两个录音同时使用这个线程
					if (!nowAudioId.equals(audioId)) {
						return;
					}

					ChatUtil.saveAudio(data.getString("content"), audioId,
							isComMsg, userId);

					Message msg = Message.obtain();
					msg.arg1 = MSG_ARG1_GET_AUDIO_SUCCESS;
					handler.sendMessage(msg);

				} else {
					Message msg = Message.obtain();
					msg.obj = context.getString(R.string.audiounexsit);
					msg.arg1 = MSG_ARG1_GET_AUDIO_NOT_EXIST;
					handler.sendMessage(msg);
				}

			} else {
				Message msg = Message.obtain();
				msg.obj = context.getResources().getString(
						R.string.networkunusable);
				msg.arg1 = MSG_ARG1_GET_AUDIO_INTERNET_Exception;
				handler.sendMessage(msg);
			}

		} catch (Exception e) {
			Message msg = Message.obtain();
			msg.obj = context.getString(R.string.audiounexsit);
			msg.arg1 = MSG_ARG1_GET_AUDIO_NOT_EXIST;
			handler.sendMessage(msg);
		}

	}
}
