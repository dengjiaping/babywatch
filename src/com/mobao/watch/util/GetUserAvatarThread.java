package com.mobao.watch.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.fragment.ChatFragment;

public class GetUserAvatarThread extends Thread {

	private static String baseUrl = CommonUtil.baseUrl;

	private static List<String> getingOrHasGotWithIds = new ArrayList<String>();

	private String GET_URL = "";

	private String getParam = "";

	private Context context;
	private String userId;

	private Handler handler = null;
	
	public GetUserAvatarThread(Context context, String userId, UserType userType, Handler handler) {
		this.context = context;
		this.userId = userId;
		this.handler = handler;
		
		if (userType == UserType.USER_TYPE_APP_USER) {
			GET_URL = "userportrait";
			getParam = "userid";
		} else if (userType == UserType.USER_TYPE_BABY) {
			GET_URL = "babyportrait";
			getParam = "imei";
		}

	}
	
	public GetUserAvatarThread(Context context, String userId, UserType userType){
		this.context = context;
		this.userId = userId;
		this.handler = new Handler(new Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case BabyFragmentActivity.WHAT_UPDATA_CHAT_LIST:
					ChatFragment.reshowListView();
					break;
				}
				return false;
			}
		});
		
		if (userType == UserType.USER_TYPE_APP_USER) {
			GET_URL = "userportrait";
			getParam = "userid";
		} else if (userType == UserType.USER_TYPE_BABY) {
			GET_URL = "babyportrait";
			getParam = "imei";
		}
	}

	@Override
	public void run() {
		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			baseUrl = "http://" + ip + ":8088/api/";
		}
		myrun();
	};

	public void myrun() {

		if (checkIsGetingOrHasGotWithId()) {
			return;
		}

		HttpPost post = new HttpPost(baseUrl.concat(GET_URL));

		JSONObject params = new JSONObject();

		try {
			params.put(getParam, userId);

			post.setEntity(new StringEntity(params.toString()));

			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				String resultStr = EntityUtils.toString(response.getEntity());
				JSONObject result = new JSONObject(resultStr);

				if (result.getInt("status") == 200) {
					String avatarStr = result.getJSONObject("data").getString(
							"portrait");
					byte[] bitmapArray = Base64.decode(avatarStr,
							Base64.DEFAULT);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bitmap = ChatUtil.toRoundBitmap(BitmapFactory
							.decodeByteArray(bitmapArray, 0,
									bitmapArray.length, options));
					ChatUtil.getImageCache().putBitmap(userId, bitmap);
					handler.sendEmptyMessage(BabyFragmentActivity.WHAT_UPDATA_CHAT_LIST);
				}
			}

		} catch (Exception e) {
		}

	}
	
	/** 检查此线程的userid的头像是否已经正在获取或者已经获取
	 * @return
	 */
	private synchronized boolean checkIsGetingOrHasGotWithId() {
		Log.w("getAvatar", "要开线程的userid = " + this.userId);
		for (int i = 0; i < getingOrHasGotWithIds.size(); i++) {
			Log.w("getAvatar", "已经开启了线程的userid = " + this.userId);
			if (getingOrHasGotWithIds.get(i).equals(this.userId)) {
				return true;
			}
		}
		getingOrHasGotWithIds.add(this.userId);
		return false;
	}
}
