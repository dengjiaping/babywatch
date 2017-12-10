package com.mobao.watch.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.datespinner.JudgeDate;
import com.mobao.watch.datespinner.ScreenInfo;
import com.mobao.watch.datespinner.WheelMain;

public class SetTimePickerDialog extends Dialog {

	private View view;
	private Activity nowactivity;
	WheelMain wheelMain;
	TextView txttime;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public SetTimePickerDialog(Context context, int dialogStyle,Activity activity,String texttime) {
		super(context, dialogStyle);
		view = LayoutInflater.from(context).inflate(
				R.layout.dialog_set_timepicker, null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(view);
		this.setCanceledOnTouchOutside(false);
		this.nowactivity=activity;
		wheelMain = new WheelMain(view);
		ScreenInfo screenInfo = new ScreenInfo(nowactivity);
		wheelMain.screenheight = screenInfo.getHeight();
		String time =texttime;
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
	}
	
	
	public void setBtnOkonclik(View.OnClickListener onclickListener){
		Button ok=(Button) view.findViewById(R.id.btn_dialog_ok);
		ok.setOnClickListener(onclickListener);
		
	}
	
	public void setBtnNoonclik(View.OnClickListener onclickListener){
		Button no=(Button) view.findViewById(R.id.btn_dialog_no);
		no.setOnClickListener(onclickListener);
	}


	public CharSequence getTime() {
		// TODO Auto-generated method stub
		return wheelMain.getTime();
	}


}
