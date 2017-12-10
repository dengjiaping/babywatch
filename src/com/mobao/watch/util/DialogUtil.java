package com.mobao.watch.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mb.zjwb1.R;

public class DialogUtil {

	private static ProgressDialog progDialog = null;
	private static String message = null;
	private static Context context;

	public DialogUtil(Context context, String message) {
		this.message = message;
		this.context = context;
		progDialog = new ProgressDialog(context);
	}

	/**
	 * 显示进度条对话框
	 */
	public static void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage(message);
		progDialog.show();
		handler.sendEmptyMessageDelayed(0, 30000);
	}

	/**
	 * 隐藏进度条对话框
	 */
	public static void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
			progDialog = null;
		}
	}

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (progDialog != null) {
				progDialog.dismiss();
				//ToastUtil.show(context, R.string.conntectserverfial);
				ToastUtil.show(context, R.string.requesttimedout);
			}
		}
	};
}
