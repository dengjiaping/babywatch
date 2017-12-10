package com.mobao.watch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.mb.zjwb1.R;
import com.mobao.watch.bean.GlobalData;
import com.testin.agent.TestinAgent;

public class Mutetime_Data_Set_WeekeActivity extends Activity {
	private RelativeLayout week1_layout;
	private RelativeLayout week2_layout;
	private RelativeLayout week3_layout;
	private RelativeLayout week4_layout;
	private RelativeLayout week5_layout;
	private RelativeLayout week6_layout;
	private RelativeLayout week7_layout;
	private CheckBox week1_checbox;
	private CheckBox week2_checbox;
	private CheckBox week3_checbox;
	private CheckBox week4_checbox;
	private CheckBox week5_checbox;
	private CheckBox week6_checbox;
	private CheckBox week7_checbox;
	private ImageButton btn_back;
	private String weeks;
	private boolean week1=false;//星期一是否被选择
	private boolean week2=false;//星期二是否被选中，一下同理
	private boolean week3=false;
	private boolean week4=false;
	private boolean week5=false;
	private boolean week6=false;
	private boolean week7=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.mutetime_data_set_weeke);
		//设置进入界面动画
				overridePendingTransition(R.anim.base_slide_right_in,
								R.anim.base_slide_remain);
		inintview();

	}

	private void inintview() {
		// TODO Auto-generated method stub
		btn_back = (ImageButton) findViewById(R.id.mute_set_weeke_btn_back);
		week1_layout = (RelativeLayout) findViewById(R.id.week1_layout);
		week2_layout = (RelativeLayout) findViewById(R.id.week2_layout);
		week3_layout = (RelativeLayout) findViewById(R.id.week3_layout);
		week4_layout = (RelativeLayout) findViewById(R.id.week4_layout);
		week5_layout = (RelativeLayout) findViewById(R.id.week5_layout);
		week6_layout = (RelativeLayout) findViewById(R.id.week6_layout);
		week7_layout = (RelativeLayout) findViewById(R.id.week7_layout);
		week1_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_1);
		week1_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week1=true;
				}else{
					week1=false;
				}
			}
		});
		week2_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_2);
		week2_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week2=true;
				}else{
					week2=false;
				}
			}
		});
		week3_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_3);
		week3_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week3=true;
				}else{
					week3=false;
				}
			}
		});
		week4_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_4);
		week4_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week4=true;
				}else{
					week4=false;
				}
			}
		});
		week5_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_5);
		week5_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week5=true;
				}else{
					week5=false;
				}
			}
		});
		week6_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_6);
		week6_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week6=true;
				}else{
					week6=false;
				}
			}
		});
		week7_checbox = (CheckBox) findViewById(R.id.weeke_checkbox_7);
		week7_checbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				if(ischecked){
					week7=true;
				}else{
					week7=false;
				}
			}
		});

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		week1_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week1_checbox.isChecked()) {
					week1_checbox.setChecked(false);
				} else {
					week1_checbox.setChecked(true);
				}
			}
		});

		week2_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week2_checbox.isChecked()) {
					week2_checbox.setChecked(false);
				} else {
					week2_checbox.setChecked(true);
				}
			}
		});

		week3_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week3_checbox.isChecked()) {
					week3_checbox.setChecked(false);
				} else {
					week3_checbox.setChecked(true);
				}
			}
		});

		week4_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week4_checbox.isChecked()) {
					week4_checbox.setChecked(false);
				} else {
					week4_checbox.setChecked(true);
				}
			}
		});
		week5_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week5_checbox.isChecked()) {
					week5_checbox.setChecked(false);
				} else {
					week5_checbox.setChecked(true);
				}
			}
		});

		week6_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week6_checbox.isChecked()) {
					week6_checbox.setChecked(false);
				} else {
					week6_checbox.setChecked(true);
				}
			}
		});
		week7_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (week7_checbox.isChecked()) {
					week7_checbox.setChecked(false);
				} else {
					week7_checbox.setChecked(true);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		String week="";
		if(week1){
			week=week+"1,";
		}
		if(week2){
			week=week+"2,";
		}
		if(week3){
			week=week+"3,";
		}
		if(week4){
			week=week+"4,";
		}
		if(week5){
			week=week+"5,";
		}
		if(week6){
			week=week+"6,";
		}
		if(week7){
			week=week+"7,";
		}
		weeks=week;
		MbApplication.getGlobalData().setWeeks(weeks);
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

}
