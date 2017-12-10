package com.mobao.watch.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mb.zjwb1.R;

/**
 * 提示框
 * 
 * @author yoine
 * 
 */
public class DialogManager {

	private Dialog dialog = null;
	private TextView tvTitle = null;
	private TextView tvBody = null;
	private Button btOk = null;
	private Button btCancel = null;

	public DialogManager(Context context) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_translucent, null);
		dialog.setContentView(view);
	}

	/**
	 * 获取提示框
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题
	 * @param BodyText
	 *            提示框要显示的内容
	 * @param OkBtnText
	 *            按钮1显示的文字
	 * @param CancelBtnText
	 *            按钮2显示的文字
	 */
	public DialogManager(Context context, String title, String BodyText,
			String OkBtnText, String CancelBtnText) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_translucent, null);

		// 设置dialog标题
		tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		tvTitle.setText(title);

		// 设置dialog显示内容
		tvBody = (TextView) view.findViewById(R.id.et_dialog_hint);
		tvBody.setText(BodyText);

		// 初始化dialog中的两个按钮
		btOk = (Button) view.findViewById(R.id.btn_dialog_ok);
		btCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);

		// 设置按钮文字
		btOk.setText(OkBtnText);
		btCancel.setText(CancelBtnText);

		dialog.setContentView(view);
	}

	public void DialogShow() {
		if (dialog != null) {
			dialog.show();
		}
	}

	public void DialogDismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setBody(String text) {
		tvBody.setText(text);
	}

	public void setOkBtnText(String text) {
		btOk.setText(text);
	}

	public void setCancelBtnText(String text) {
		btCancel.setText(text);
	}

	public void setOkBtnOclick(View.OnClickListener onclick) {
		btOk.setOnClickListener(onclick);
	}

	public void setCancelBtnOclick(View.OnClickListener onclick) {
		btCancel.setOnClickListener(onclick);
	}

}
