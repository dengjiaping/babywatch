package com.mobao.watch.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.bean.ChatAudioEntity;
import com.mobao.watch.fragment.ChatFragment;

public class ChatGetMsgs extends Thread {
	private static final String GET_MESSAGE = "getmessage";

	public static final int ARG1_IS_REFRESH = 550;
	public static final int ARG1_IS_NOT_REFRESH = 551;

	public static List<ChatAudioEntity> msgsList = new ArrayList<ChatAudioEntity>();
	private Context context;
	private String num = 20 + "";
	private String date;
	private boolean isGetNewMsg = false;
	private String UserId;
	private String babyImei;
	private OutTimeChecker out;
	private Handler handler;

	public ChatGetMsgs(Context context, int num, Handler handler) {
		this.context = context;
		this.num = num + "";
		this.UserId = MbApplication.getGlobalData().getNowuser().getUserid();
		this.babyImei = MbApplication.getGlobalData().getNowuser().getImei();
		this.handler = handler;
	}

	@Override
	public synchronized void run() {

		boolean isStartOutTime = false;

		// 检查网络超时
		out = new OutTimeChecker(20000, new AfterTimeOutListener() {

			@Override
			public void onTimeIsOunt() {
				handler.sendEmptyMessage(BabyFragmentActivity.WHAT_NETWORK_OUT_TIME);
			}
		});

		out.startTimeOutCheck();
		isStartOutTime = true;

		HttpPost post = new HttpPost(ChatUtil.baseUrl.concat(GET_MESSAGE));

		JSONObject params = new JSONObject();

		try {
			params.put("userid", UserId);
			params.put("imei", babyImei);
			params.put("num", num);
			if (isGetNewMsg) {
				params.put("time", date);
			}

			Log.w(GET_MESSAGE, "请求参数：" + params.toString());

			post.setEntity(new StringEntity(params.toString()));

			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

			HttpResponse response = defaultHttpClient.execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(response.getEntity());

				Log.w(GET_MESSAGE, "返回参数：" + retSrc);

				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断获取是否成功
				int status = result.getInt("status");

				if (status == 200) { // 获取成功

					// 取消网络超时判断
					if (isStartOutTime) {
						out.cancel();
					}

					isStartOutTime = false;

					JSONArray array = result.getJSONArray("data");

					for (int i = 0; i < array.length(); i++) {
						JSONObject json = array.getJSONObject(i);
						String userId = json.getString("id");
						String msgType = json.getString("type");
						String sendTime = json.getString("time");
						String audioId = json.getString("audioid");
						String duration = json.getString("audiolen");

						try {
							ChatAudioEntity audio = new ChatAudioEntity(
									audioId, UserType.USER_TYPE_APP_USER);

							if (msgType.equals("user")) {
								audio.setUserType(UserType.USER_TYPE_APP_USER);
							} else if (msgType.equals("baby")) {
								audio.setUserType(UserType.USER_TYPE_BABY);
							}

							audio.setUserId(userId);
							audio.setDate(sendTime);
							audio.setDuration(Integer.parseInt(duration));

							if (userId.equals(MbApplication.getGlobalData()
									.getNowuser().getUserid())) {
								audio.setComMsg(false);
								if (isGetNewMsg) {
									continue;
								}
							} else {
								audio.setComMsg(true);

								// 获取头像
								Bitmap bitmap = ChatUtil.getImageCache()
										.getBitmap(userId);

								if (bitmap == null) {
									Log.w("yyy", userId + "的头像为空");
									GetUserAvatarThread thread = new GetUserAvatarThread(
											context, userId,
											audio.getUserType(), handler);
									thread.start();
								}

							}

							msgsList.add(audio);

							Log.w("chat", "聊天记录： userId = " + userId
									+ "  audioId = " + audio.getAudioId()
									+ " i = " + i);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					Log.w("getmessage", "获取下来的条数 = " + msgsList.size());

					// 判断是否是按刷新按钮
					if (ChatFragment.refreshAminTime != 0) {
						ChatUtil.sleep1s(ChatFragment.refreshAminTime);
						handler.sendEmptyMessage(BabyFragmentActivity.WHAT_STOP_REFRESH_ANMIN);
					}

					handler.sendEmptyMessage(BabyFragmentActivity.WHAT_GET_MSG_SUCCESS);
					return;

				} else {
					handler.sendEmptyMessage(BabyFragmentActivity.WHAT_GET_MSG_SERVER_BUY);
				}

			} else {
				handler.sendEmptyMessage(BabyFragmentActivity.WHAT_GET_MSG_NETWORK_EXCPTION);
			}

		} catch (Exception e) {
			handler.sendEmptyMessage(BabyFragmentActivity.WHAT_GET_MSG_NETWORK_EXCPTION);
		}

		// 判断是否是按刷新按钮
		if (ChatFragment.refreshAminTime != 0) {
			ChatUtil.sleep1s(ChatFragment.refreshAminTime);
			handler.sendEmptyMessage(BabyFragmentActivity.WHAT_STOP_REFRESH_ANMIN);
		}

	}

	public static List<ChatAudioEntity> getMsgsList() {
		return msgsList;
	}

}
