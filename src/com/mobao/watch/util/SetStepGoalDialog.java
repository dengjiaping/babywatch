package com.mobao.watch.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;


public class SetStepGoalDialog extends Dialog {

	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	private View view;
	private String userid="";
	private String imei="";
	private boolean islast=false;
	private Context context;
	
	public SetStepGoalDialog(Context context) {
		super(context);
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
		tvTitle.setText(context.getResources().getString(R.string.setgoal));
		// 修改输入框输入类型
		EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
		etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		etInput.setHint(context.getString(R.string.setgoal));
	}

	public void ChangeToSetauthlist(String phone,String userid,String imei,boolean islast) {
		// TODO 变化为审核列表
		         
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
				tvTitle.setText(context.getResources().getString(R.string.these_menber_request_join));
		
				EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
                etInput.setVisibility(View.GONE);
                
                TextView textphone=(TextView) view.findViewById(R.id.text_dialog_authlis);
                textphone.setVisibility(View.VISIBLE);
                textphone.setText(phone);
                
                Button confirmauth=(Button) view.findViewById(R.id.btn_dialog_confirmauth);
                confirmauth.setVisibility(View.VISIBLE);
                Button disavow=(Button) view.findViewById(R.id.btn_dialog_disavow);
                disavow.setVisibility(View.VISIBLE);
                Button btOk = (Button) view.findViewById(R.id.btn_dialog_ok);
                btOk.setVisibility(View.GONE);
               
                
                this.userid=userid;
                this.imei=imei;
                this.islast=islast;
                           
	}
	
	
	
	public void ChangeToEscnowuser() {
		// TODO 变化为审核列表
		         
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
				tvTitle.setText(context.getResources().getString(R.string.esc));
		
				EditText etInput = (EditText) view.findViewById(R.id.et_dialog_hint);
                etInput.setVisibility(View.GONE);
                
                TextView textphone=(TextView) view.findViewById(R.id.text_dialog_authlis);
                textphone.setVisibility(View.VISIBLE);
                textphone.setText(context.getResources().getString(R.string.isescnowuser));
                
                Button confirmauth=(Button) view.findViewById(R.id.btn_dialog_confirmauth);
                confirmauth.setText(context.getResources().getString(R.string.sure));
                confirmauth.setVisibility(View.VISIBLE);
                Button disavow=(Button) view.findViewById(R.id.btn_dialog_disavow);
                disavow.setText(context.getResources().getString(R.string.cancel));
                disavow.setVisibility(View.VISIBLE);
                Button btOk = (Button) view.findViewById(R.id.btn_dialog_ok);
                btOk.setVisibility(View.GONE);
               
  
                           
	}
	
	
	public void setbtnconfirmauthclickListener(View.OnClickListener onclickListener) {
		 Button btnconfirmauth=(Button) view.findViewById(R.id.btn_dialog_confirmauth);
		 btnconfirmauth.setOnClickListener(onclickListener);
	}
	public void setbtndisavowclickListener(View.OnClickListener onclickListener) {
		 Button btndisavow=(Button) view.findViewById(R.id.btn_dialog_disavow);
		 btndisavow.setOnClickListener(onclickListener);
	}
	
	
	
	public String getUserid() {
		return userid;
	}

	public String getImei() {
		return imei;
	}

	public boolean isIslast() {
		return islast;
	}



	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;

			if (lj == 1) {

			
			} else {
				Toast.makeText(context, "连接服务器失败，请稍候重试",
						3000).show();
			}

		}

	}

}
