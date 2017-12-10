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

public class SetRewardGoalDialog extends Dialog {

	private View view;
	private Context context;

	public SetRewardGoalDialog(Context context, int dialogStyle) {
		super(context, dialogStyle);
		this.context = context;
		view = LayoutInflater.from(context).inflate(
				R.layout.dialog_set_reward_goal, null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(view);
		this.setCanceledOnTouchOutside(false);
	}

	public void setBtOnclickListener(View.OnClickListener onclickListener) {
		Button btOk = (Button) view.findViewById(R.id.btn_dialog_ok);
		btOk.setOnClickListener(onclickListener);
	}

	public String getInputText() {
		EditText et = (EditText) view.findViewById(R.id.et_dialog_hint);
		return et.getText().toString().trim();
	}

	public void ChangeToSetRewardThingDialog() {
		// 修改标题
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		tvTitle.setText(context.getResources().getString(R.string.set_reward));

		// 修改输入框输入类型
		EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
		etInput.setInputType(InputType.TYPE_CLASS_TEXT);
		etInput.setHint(context.getResources().getString(R.string.please_input_reward_thing));
		etInput.addTextChangedListener(textWatcher);
	}

	public void setmaxlength(int length) {

		EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
		etInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				length) });

	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		if(CharUtil.isChinese(s.toString())){
			if(count>=8){
				EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
				String now=etInput.getText().toString().replace(s, "");
				etInput.setText(now);
			}
			setmaxlength(8);
		}else{
			setmaxlength(16);
		}
		
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			//		if(CharUtil.isChinese(s.toString())){

		}
		

		

		@Override
		public void afterTextChanged(Editable s) {
			//
			if(CharUtil.isChinese(s.toString())){
				setmaxlength(8);
				
			}else{
				setmaxlength(16);
			}

		}
	};

}
