package com.mobao.watch.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.mb.zjwb1.R;

public class BabyNameEditTextWatcher implements TextWatcher {

	private String contentOfEtName = "";
	private EditText etName;
	private Context context;

	public BabyNameEditTextWatcher(Context context, EditText etName) {
		this.etName = etName;
		this.context = context;
		etName.addTextChangedListener(this);
		contentOfEtName = etName.getText().toString();
	}

	public void initContentOfEtName() {
		contentOfEtName = "";
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.w("babyName", "s = " + s + " start = " + start + " before = "
				+ before + " count = " + count);
		
		if (checkIsHasSapce(s.toString())) {
			ToastUtil.show(context,
					context.getString(R.string.name_can_t_input_space));
			etName.setText(contentOfEtName);
			etName.setSelection(start);
			return;
		}
		contentOfEtName = s.toString();
//		if (s.length() > contentOfEtName.length()) { // 保证是添加字符
//			String change = s.toString().substring(start, start + count); // 输入的字符串
//			if (checkIsHasSapce(change)) {
//				ToastUtil.show(context,
//						context.getString(R.string.name_can_t_input_space));
//				etName.setText(contentOfEtName);
//				etName.setSelection(start);
//				return;
//			} else {
//				int CNCount = checkHowManyCN(s.toString()); // 判断中文字符的个数
//				if (s.length() + CNCount > 12) {
//					ToastUtil.show(context,
//							context.getString(R.string.name_can_not_more_16));
//					etName.setText(contentOfEtName);
//					etName.setSelection(start); // 设置光标位置
//					return;
//				}
//			}
//		}
//
//		contentOfEtName = s.toString();
	}

	private boolean checkIsHasSapce(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') {
				return true;
			}
		}
		return false;
	}

	private int checkHowManyCN(String s) {
		int result = 0;
		for (int i = 0; i < s.length(); i++) {
			if (CharUtil.isChinese(s.charAt(i))) {
				result++;
			}
		}
		return result;
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

}
