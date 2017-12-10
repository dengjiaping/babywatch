package com.mobao.watch.util;

import android.app.ProgressDialog;
import android.content.Context;

public class WaitDialog extends ProgressDialog {

	private WaitDialog(Context context) {
		super(context);
	}

	private static WaitDialog progDialog;

	public synchronized static WaitDialog getIntence(Context context) {
		if (progDialog == null) {
			progDialog = new WaitDialog(context);
		}
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		return progDialog;

	}

	@Override
	public void dismiss() {
		if (progDialog.isShowing()) {
			super.dismiss();
		}
		setDialogToNull();
	}

	private static void setDialogToNull() {
		progDialog = null;
	}

	public WaitDialog setContent(String content) {
		progDialog.setMessage(content);
		return progDialog;
	}

	public WaitDialog setIsCanCancel(boolean cancelable) {
		progDialog.setCancelable(cancelable);
		return progDialog;
	}
}
