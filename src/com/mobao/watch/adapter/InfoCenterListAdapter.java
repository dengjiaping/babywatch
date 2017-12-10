package com.mobao.watch.adapter;

import java.util.ArrayList;


import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BasicMapActivity;
import com.mobao.watch.bean.InformationCenter;
import com.mobao.watch.bean.SosRecordInfo;

public class InfoCenterListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<InformationCenter> infocenter_info;

	public InfoCenterListAdapter(Context context,
			ArrayList<InformationCenter> infocenter_info) {
		this.context = context;
		this.infocenter_info=infocenter_info;
	}

	@Override
	public int getCount() {
		if (infocenter_info == null) {
			return 0;
		}
		return infocenter_info.size();
	}

	@Override
	public Object getItem(int index) {
		if (infocenter_info != null && index < infocenter_info.size()) {
			return infocenter_info.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		if (infocenter_info != null && index < infocenter_info.size()) {
			return index;
		}
		return 0;
	}

	@Override
	public View getView(int index, View contextView, ViewGroup parent) {
		if (contextView == null) {
			contextView = LayoutInflater.from(context).inflate(
					R.layout.information_center_list_item, parent, false);
		}
//		int len = sos_info.size() - 1;
		/*TextView text_time = (TextView) contextView
				.findViewById(R.id.text_time);
		TextView text_date = (TextView) contextView
				.findViewById(R.id.text_month);
		TextView text_detial_address = (TextView) contextView
				.findViewById(R.id.text_detial_address);
		TextView text_am_or_pm = (TextView) contextView
				.findViewById(R.id.text_am_or_pm);*/

		// 获取对应的实体类(将最后一个放在第一个位置)
		// final SosRecordInfo sos=sos_info.get(len-index);
		final InformationCenter info = infocenter_info.get(index);
		
		
		
		TextView text_info_address = (TextView) contextView
				.findViewById(R.id.info_text_address);
		TextView text_date = (TextView) contextView
				.findViewById(R.id.info_text_date);
		TextView text_state = (TextView) contextView
				.findViewById(R.id.info_text_state);
		TextView text_hour = (TextView) contextView
				.findViewById(R.id.info_text_time);
		TextView text_am = (TextView) contextView
				.findViewById(R.id.info_text_am_or_pm);
		//设置点击跳转到地图页面
		if(info.getType().equals("safe")){
			RelativeLayout rel_right=(RelativeLayout) contextView.findViewById(R.id.rel_right);
			rel_right.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(context,BasicMapActivity.class);
					intent.putExtra("lat", info.getLat());
					intent.putExtra("lon", info.getLon());
					context.startActivity(intent);
				}
			});
			//设置状态
			String condition=info.getInfo_state();
			//安全区域状态
			if(info.getInfo_state().equals("leave")){
				text_state.setText(context.getResources().getString(R.string.levaed));
			}else{

				text_state.setText(context.getResources().getString(R.string.reached));
			}
		}else{
			text_state.setText("");
		}
		
		
		
		
		//设置值
		text_info_address.setText(info.getInfo_name());
		text_date.setText(info.getInfo_date());
			
		String minute=info.getInfo_minute();
		if(Integer.parseInt(minute)<10){
			minute="0"+minute;
		}
		// 设置参数值（地址，上下午，时间）
		text_am.setText("am");// 上下午设置
		// 12小时格式设置(设置时间)
		int time_hour = Integer.valueOf(info.getInfo_hour());
		if (time_hour > 12) {
			time_hour -= 12;
			text_am.setText("pm");
			text_hour.setText(time_hour + ":" + minute);
		} else {
			text_hour.setText(time_hour + ":" + minute);
		}
		
		
		
		

//		//连接线的高度显示
//		TextView line = (TextView) contextView
//				.findViewById(R.id.line);
//
//		Log.i("keis", index+"h="+info.getInfo_address().length());
//		
//		int g=info.getInfo_address().length();
//		
//		if(g>50){
//			line.setHeight(info.getInfo_address().length()*4);
//		}else{
//			line.setHeight(160);
//		}
		
		return contextView;
	}

}
