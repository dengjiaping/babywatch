package com.mobao.watch.util;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mb.zjwb1.R;

public class AddWhitephoneDialog extends Dialog {

	private View view;
	private Context context;

	public AddWhitephoneDialog(Context context, int dialogStyle) {
		super(context, dialogStyle);
		this.context = context;
		view = LayoutInflater.from(context).inflate(
				R.layout.dialog_whitelist, null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(view);
		this.setCanceledOnTouchOutside(false);
	}

	public void setAddBtOnclickListener(View.OnClickListener onclickListener) {
		Button add = (Button) view.findViewById(R.id.btn_dialog_add);
		add.setOnClickListener(onclickListener);
	}
	
	public void setSureBtOnclickListener(View.OnClickListener onclickListener) {
		Button sure = (Button) view.findViewById(R.id.btn_dialog_sure);
		sure.setOnClickListener(onclickListener);
	}
	
	public void setCancelBtOnclickListener(View.OnClickListener onclickListener) {
		Button cancel= (Button) view.findViewById(R.id.btn_dialogcancel);
		cancel.setOnClickListener(onclickListener);
	}

	public String getInputText() {
		EditText et = (EditText) view.findViewById(R.id.dialog_et_whitephone);
		return et.getText().toString().trim();
	}

	public void ChangeToAddWhitephone() {
		// 修改标题
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		tvTitle.setText(context.getResources().getString(R.string.addnowwhitephone));
		
		

	}
	
	public void ChangeToDelWhitephone(String phone) {
		// 修改标题
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		tvTitle.setText(context.getResources().getString(R.string.delwhitephone));

		// 隐藏输入框和添加按钮
		EditText etInput = (EditText) view.findViewById(R.id.dialog_et_whitephone);
		etInput.setVisibility(View.GONE);
		Button add=(Button) view.findViewById(R.id.btn_dialog_add);
		add.setVisibility(View.GONE);
		//显示提示TEXTVIEW和按钮
		TextView delphone=(TextView) view.findViewById(R.id.dialog_text_del);
		delphone.setText(context.getResources().getString(R.string.isdelwhitephone)+phone+"  ?");
		delphone.setVisibility(View.VISIBLE);
		Button sure=(Button) view.findViewById(R.id.btn_dialog_sure);
		sure.setVisibility(View.VISIBLE);
		Button cancel=(Button) view.findViewById(R.id.btn_dialogcancel);
		cancel.setVisibility(View.VISIBLE);
		

	}

	public void setmaxlength(int length) {

		EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
		etInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				length) });

	}

}
