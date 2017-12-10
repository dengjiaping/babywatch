package com.mobao.watch.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;

import com.mb.zjwb1.R;
import com.mobao.watch.util.UniformDialog;

public class DialogCountDownReceiver extends BroadcastReceiver {

	public static int nowNum = 0;
	private  CountThread countThread;
	public static final String ACTION_START_COUNT_DOWN = "startCountDown";
	public static final int MSG_CHANGE_BODYTEXT = 100;
	private static boolean changeDialogText = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == MSG_CHANGE_BODYTEXT && changeDialogText) {
				String fromHtml = (String) msg.obj;
				UniformDialog.getInstance().getTvBody()
						.setText(Html.fromHtml(fromHtml));
			}
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION_START_COUNT_DOWN)) {
			nowNum = intent.getIntExtra("startNum", 0);
			if (countThread == null || !countThread.isAlive()) {
				countThread = new CountThread(nowNum, context);
				countThread.start();
			}

		}
	}

	private class CountThread extends Thread {
		
		private Context context;
		
		public CountThread(int startNum, Context context) {
			nowNum = startNum;
			this.context = context;
		}

		@Override
		public void run() {
			try {

				while (nowNum > 0) {
					Thread.sleep(1000);
					nowNum = nowNum - 1;
					UniformDialog uniDialog = UniformDialog.getInstance();
					if (uniDialog != null && uniDialog.isShowing()
							&& changeDialogText) {
						String fromHtml = context.getResources().getString(R.string.wait)
								+ "<b><font color='#FF0000' size='5'>" + nowNum
								+ "</font></b>" +  context.getResources().getString(R.string._retryu);
						Message msg = new Message();
						msg.arg1 = MSG_CHANGE_BODYTEXT;
						msg.obj = fromHtml;
						handler.sendMessage(msg);
					}
				}
				if (UniformDialog.getInstance() != null
						&& UniformDialog.getInstance().isShowing()
						&& changeDialogText) {
					UniformDialog dialog = UniformDialog.getInstance();
					if(dialog != null && dialog.isShowing()){
						dialog.dismiss();
					}

				}
				changeDialogText = false;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
	}

	public static void stopThread() {
		nowNum = 0;
	}

	public static void showDialog(int startNum, Context context,
			String titleText) {
		Spanned fromHtml = Html.fromHtml(context.getResources().getString(R.string.wait)
				+ "<b><font color='#FF0000' size='5'>" + startNum
				+ "</font></b>" + context.getResources().getString(R.string._retryu));

		UniformDialog.initDialog(context, false);
		UniformDialog uniDialog = UniformDialog.getInstance();
		uniDialog.getTvTitle().setText(titleText);
		uniDialog.getTvBody().setText(fromHtml);
		uniDialog.getBtOk().setText(context.getResources().getString(R.string.sure));
uniDialog.setCanceledOnTouchOutside(true); 
		uniDialog.getBtOk().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UniformDialog dialog = UniformDialog.getInstance();
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});
		changeDialogText = true;
		uniDialog.show();
	}

	public static boolean isChangeDialogText() {
		return changeDialogText;
	}

	public static void setChangeDialogText(boolean changeDialogText) {
		DialogCountDownReceiver.changeDialogText = changeDialogText;
	}

}
