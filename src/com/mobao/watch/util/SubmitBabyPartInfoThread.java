package com.mobao.watch.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.ActiveWatchActivity;
import com.mobao.watch.activity.AddBabyActivity2;
import com.mobao.watch.bean.BabyInfo;

public class SubmitBabyPartInfoThread extends Thread {

	private String imei;
	private String userid;
	private String relation;
	private ActiveWatchActivity activity;
	private String babyName;
	private String watchPhone;
	private boolean isManager;

	private final static int MSG_WHAT_SHOW_TOAST_WITH_OBJ = 221;
	private final static int MSG_WHAT_SAVE_BABY_INFO_SUCCESS = 222;
	private final static int MSG_WHAT_CLOSE_WAIT_DIALOG = 223;
	private final static int MSG_WHAT_SHOW_WAIT_DIALOG = 224;
	private final static int MSG_WHAT_SHOW_VERFY_DIALOG = 225;

	private final static String url = "addbaby";
	public static final String INTENT_EXTRA_IMEI = "imei";
	public static final String INTENT_EXTRA_NAME = "name";
	public static final String INTENT_EXTRA_PHONE = "watchPhone";
	public static final String INTENT_EXTRA_RELATION = "relation";

	public SubmitBabyPartInfoThread(ActiveWatchActivity activity, String imei,
			String userid, String relation, String babyName, String watchPhone,
			boolean isManager) {
		this.activity = activity;
		this.imei = imei;
		this.userid = userid;
		this.relation = relation;
		this.babyName = babyName;
		this.watchPhone = watchPhone;
		this.isManager = isManager;
	}

	@Override
	public void run() {

		handler.sendEmptyMessage(MSG_WHAT_SHOW_WAIT_DIALOG);

		HttpPost post = new HttpPost(CommonUtil.baseUrl + url);

		/*
		 * JSONObject json = new JSONObject();
		 * 
		 * 
		 * 
		 * json.put("userid", userid); json.put("imei", imei);
		 * json.put("relate", relation); json.put("babyphone", watchPhone);
		 * json.put("name", babyName);
		 * 
		 * Log.w("addbaby", "请求的参数：" + json.toString());
		 * 
		 * post.setEntity(new StringEntity(json.toString())); HttpResponse
		 * response = new DefaultHttpClient().execute(post)
		 */;
		try {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", userid));
			param.add(new BasicNameValuePair("name", babyName));
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("relate", relation));
			param.add(new BasicNameValuePair("babyphone", watchPhone));

			post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			System.out.println("上传的宝贝：" + param.toString());
			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {

				String resStr = EntityUtils.toString(response.getEntity());
				Log.w("addbaby", "返回的参数：" + resStr);

				JSONObject result = new JSONObject(resStr);
				
				String code = result.getString("msg");
				
				if (result.getInt("status") == 200) {

					if (result.getString("msg").equals("20003")) {
						String phone = result.getJSONObject("data").getString(
								"phone");

						Message msg = new Message();
						msg.what = MSG_WHAT_SHOW_VERFY_DIALOG;
						msg.obj = phone;
						handler.sendMessage(msg);
					} else if (result.getString("msg").equals("20000")) {
						handler.sendEmptyMessage(MSG_WHAT_SAVE_BABY_INFO_SUCCESS);
					} else if ("10040".equals(code)) { // 关系冲突
						Message msg = new Message();
						msg.what = MSG_WHAT_SHOW_TOAST_WITH_OBJ;
						msg.obj = new ErroNumberChange(activity).chang(result
								.getString("msg"));
						handler.sendMessage(msg);
					}

				} else {
					Message msg = new Message();
					msg.what = MSG_WHAT_SHOW_TOAST_WITH_OBJ;
					msg.obj = CommonUtil.getServerStr(activity,
							result.getString("msg"));
					handler.sendMessage(msg);
				}

			} else {
				Message msg = new Message();
				msg.what = MSG_WHAT_SHOW_TOAST_WITH_OBJ;
				msg.obj = activity.getResources().getString(
						R.string.network_exception_save_fail);
				handler.sendMessage(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = MSG_WHAT_SHOW_TOAST_WITH_OBJ;
			msg.obj = activity.getResources().getString(
					R.string.server_is_busy_try_again_a_moument);
			handler.sendMessage(msg);
		}
		handler.sendEmptyMessage(MSG_WHAT_CLOSE_WAIT_DIALOG);
	}

	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case MSG_WHAT_SHOW_TOAST_WITH_OBJ: // 显示toast
				if (msg.obj == null) {
					break;
				}
				String content = (String) msg.obj;
				ToastUtil.show(activity, content);
				break;

			case MSG_WHAT_SAVE_BABY_INFO_SUCCESS: // 修改成功
				if (isManager == true) {
					ToastUtil
							.show(activity,
									activity.getResources()
											.getString(
													R.string.add_baby_success_please_complete_baby_info));
					Intent intent = new Intent(activity, AddBabyActivity2.class);
					intent.putExtra(INTENT_EXTRA_IMEI, imei);
					intent.putExtra(INTENT_EXTRA_NAME, babyName);
					intent.putExtra(INTENT_EXTRA_PHONE, watchPhone);
					intent.putExtra(INTENT_EXTRA_RELATION, relation);
					activity.startActivity(intent);
					activity.finish();
				} else {
					ToastUtil.show(
							activity,
							activity.getResources().getString(
									R.string.login_after_verfy));
					activity.finish();
				}

				break;

			case MSG_WHAT_SHOW_WAIT_DIALOG: // 弹出等待dialog
				WaitDialog dialog = WaitDialog.getIntence(activity);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setContent(activity
						.getString(R.string.adding_baby_wait__));
				dialog.show();
				break;

			case MSG_WHAT_CLOSE_WAIT_DIALOG: // 关闭等待dialog
				dialog = WaitDialog.getIntence(activity);
				dialog.dismiss();
				break;

			case MSG_WHAT_SHOW_VERFY_DIALOG: // 需要管理员审核
				ToastUtil.show(
						activity,
						activity.getResources().getString(
								R.string.login_after_verfy));
				activity.finish();
				break;
			default:
				break;
			}
			return false;
		}
	});

}
