package com.mobao.watch.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.ActiveWatchActivity;
import com.mobao.watch.activity.MbApplication;

public class CheckCanAddBabyOrNotThread extends Thread {

	private static final String URL = "tryaddbaby";
	private String babyImei;
	private ActiveWatchActivity activity;
	public boolean isManager = true; // 宝贝提示待管理员审核的提示
	public static String managerPhone = "-1";

	private String name = ""; // 宝贝昵称
	private String number = ""; // 腕表号码

	// handler的what
	private static final int WHAT_NETWORK_EXCEPTION = 100;
	private static final int WHAT_SERVER_BUSY = 101;
	private static final int WHAT_SHOW_DIALOG_WITH_OBJ = 102;
	private static final int WHAT_SHOW_TOAST_WITH_OBJ = 103;

	private static final int ARG1_RESCAN = 104;
	private static final int ARG1_CLOSE_SCAN = 105;
	private static final int ARG1_TO_SET_BABY_INFO = 106;
	private static final int ARG2_IS_ADMIN = 107;
	private static final int ARG2_NOT_IS_ADMIN = 108;

	public CheckCanAddBabyOrNotThread(ActiveWatchActivity activity,
			String babyImei) {
		this.babyImei = babyImei;
		this.activity = activity;
	}

	@Override
	public void run() {

		try {
			HttpPost post = new HttpPost(CommonUtil.baseUrl + URL);

			// 封装参数
			JSONObject json = new JSONObject();
			json.put("userid", MbApplication.getGlobalData().getNowuser()
					.getUserid());
			json.put("imei", babyImei);

			Log.w("ifaddbaby", "请求参数json = " + json.toString());
			post.setEntity(new StringEntity(json.toString()));
			HttpResponse response = new DefaultHttpClient().execute(post);
			Log.w("ifaddbaby", "status："
					+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {

				JSONObject result = new JSONObject(
						EntityUtils.toString(response.getEntity()));

				Log.w("ifaddbaby", "返回的参数：" + result.toString());

				String code = result.getString("msg");

				if ("20000".equals(code)) { // 扫描成功，可以添加并成为管理员
					Message msg = new Message();
					msg.what = WHAT_SHOW_TOAST_WITH_OBJ;
					msg.obj = activity.getResources().getString(
							R.string.scansuccess);
					msg.arg1 = ARG1_TO_SET_BABY_INFO;
					msg.arg2 = ARG2_IS_ADMIN;
					handler.sendMessage(msg);
				} else if ("10042".equals(code)) { // 不可以重复添加宝贝
					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = activity.getResources().getString(
							R.string.add_baby_fail_because_you_has);
					msg.arg1 = ARG1_CLOSE_SCAN;
					handler.sendMessage(msg);
				} else if ("10041".equals(code)) { // 普通成员不能添加宝贝
					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = activity.getResources().getString(
							R.string.add_baby_fail_because_not_jurisdiction);
					msg.arg1 = ARG1_CLOSE_SCAN;
					handler.sendMessage(msg);
				} else if ("20003".equals(code)) { // 需要管理员审核的成功提示

					// 获取管理员手机号码
					JSONObject data = result.getJSONObject("data");
					managerPhone = data.getString("phone");

					number = data.getString("babyphone");
					name = data.getString("babyname");

					isManager = false;

					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = activity.getString(R.string.need_to_verify)
							+ managerPhone;
					msg.arg1 = ARG1_TO_SET_BABY_INFO;
					msg.arg2 = ARG2_NOT_IS_ADMIN;
					handler.sendMessage(msg);
				} else if ("10043".equals(code)) { // 不能被其他管理员激活

					// 获取管理员手机号码
					JSONObject data = result.getJSONObject("data");
					String phone = data.getString("phone");

					// 组件提示内容
					String head = activity.getResources().getString(
							R.string.add_baby_fail_because_you_is_admin1);
					String tail = activity.getResources().getString(
							R.string.add_baby_fail_because_you_is_admin2);

					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = head + phone + tail;
					msg.arg1 = ARG1_CLOSE_SCAN;
					handler.sendMessage(msg);

				} else if ("10100".equals(code)) { // 宝贝不在线
					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = activity.getResources().getString(
							R.string.codeiunusable);
					msg.arg1 = ARG1_CLOSE_SCAN;
					handler.sendMessage(msg);

				} else if ("10101".equals(code)) { // 请求超时
					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = activity.getResources().getString(
							R.string.request_watch_authok_outtime);
					msg.arg1 = ARG1_RESCAN;
					handler.sendMessage(msg);
				} else {  //其他
					Message msg = new Message();
					msg.what = WHAT_SHOW_DIALOG_WITH_OBJ;
					msg.obj = activity.getResources().getString(
							R.string.nonetworknotexit);
					msg.arg1 = ARG1_CLOSE_SCAN;
					handler.sendMessage(msg);
				}

			} else {
				handler.sendEmptyMessage(WHAT_NETWORK_EXCEPTION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(WHAT_NETWORK_EXCEPTION);
		}

	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;

			WaitDialog.getIntence(activity).dismiss();

			switch (what) {
			case WHAT_NETWORK_EXCEPTION:
				ToastUtil.show(
						activity,
						activity.getResources().getString(
								R.string.network_excelption_rescan));
				activity.reScan();
				break;

			case WHAT_SERVER_BUSY:
				if (msg.obj == null) {
					break;
				}
				ToastUtil.show(activity,
						CommonUtil.getServerStr(activity, (String) msg.obj));
				activity.reScan();
				break;

			case WHAT_SHOW_DIALOG_WITH_OBJ: // 弹dialog
				if (msg.obj != null) {
					showDialog(msg);
				}
				break;

			case WHAT_SHOW_TOAST_WITH_OBJ: // 弹toast
				if (msg.obj != null) {
					ToastUtil.show(activity, (String) msg.obj);
					doByArg1(msg.arg1, msg.arg2);
				}
				break;
			}
			return false;
		}

	});

	private void showDialog(Message msg) {

		final int arg1 = msg.arg1;
		final int arg2 = msg.arg2;

		CustomDialog.Builder builder = new CustomDialog.Builder(activity);
		builder.setMessage((String) msg.obj);
		builder.setTitle(activity.getResources().getString(
				R.string.dialog_body_text));
		builder.setPositiveButton(
				activity.getResources().getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doByArg1(arg1, arg2);
					}
				});
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private void doByArg1(int arg1, int arg2) {
		switch (arg1) {
		case ARG1_CLOSE_SCAN: // 结束扫描
			activity.finishActivity();
			break;

		case ARG1_RESCAN: // 重新扫描
			activity.reScan();
			break;

		case ARG1_TO_SET_BABY_INFO: // 设置
			if (arg2 == ARG2_IS_ADMIN) {
				activity.setCanEdit();
			} else if (arg2 == ARG2_NOT_IS_ADMIN) {
				activity.setNotCanEdit(name, number);
				activity.setImei(babyImei);
			}
			break;
		}
	}

}
