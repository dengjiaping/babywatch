package com.mobao.watch.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.bean.LoactionHistoryInfo;
import com.mobao.watch.bean.LocationLog;

public class HistoryLogListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<LocationLog> location_log;
	public static final String MOVE_TO_HISTORY_LOCATE_ACTION= "MOVE_TO_HISTORY_LOCATE_ACTION";
    private boolean isAdmin = true;
	
	public HistoryLogListAdapter(Context context,
			ArrayList<LocationLog> location_log) {
		this.context = context;
		this.location_log = location_log;
	}

	@Override
	public int getCount() {
		if (location_log == null) {
			return 0;
		}
		return location_log.size();
	}

	@Override
	public Object getItem(int index) {
		if (location_log != null && index < location_log.size()) {
			return location_log.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		if (location_log != null && index < location_log.size()) {
			return index;
		}
		return 0;
	}

	@Override
	public View getView(int index, View contextView, ViewGroup parent) {
		if (contextView == null) {
			contextView = LayoutInflater.from(context).inflate(
					R.layout.address_list_item, parent, false);
		}
		TextView text_time = (TextView) contextView
				.findViewById(R.id.text_time);
		TextView text_num = (TextView) contextView.findViewById(R.id.text_num);
		TextView text_address_name = (TextView) contextView
				.findViewById(R.id.text_address_name);
		TextView text_detial_address = (TextView) contextView
				.findViewById(R.id.text_detial_address);
		TextView text_am_or_pm = (TextView) contextView
				.findViewById(R.id.text_am_or_pm);
		
		final LocationLog log = location_log.get(index);
		int hour_int = Integer.valueOf(log.getHour());//用int格式获取时
		String hour="00";
		int minute_int=Integer.valueOf(log.getMinute());//用int格式获取分钟
		String minute="00";
		//判断是否需要再前面加0,不满10便加前面加0
		if(minute_int>=0 && minute_int<10){
			minute="0"+minute_int;
		}else{
			minute=minute_int+"";
		}
		
		if (hour_int > 12) {
			hour_int -= 12;
			//判断是否需要再前面加0,不满10便加前面加0
			if(hour_int>=0 && hour_int<10){
				hour="0"+String.valueOf(hour_int);
			}else{
				hour=String.valueOf(hour_int);
			}
			
			text_am_or_pm.setText("pm");
			text_time.setText(hour + ":" + minute);
		} else {
			//text_time.setText(info.getHistory_time());
			//判断是否需要再前面加0,不满10便加前面加0
			if(hour_int>=0 && hour_int<10){
				hour="0"+String.valueOf(hour_int);
			}else{
				hour=String.valueOf(hour_int);
			}
			text_am_or_pm.setText("am");
			text_time.setText(hour + ":" + minute);
		}
		text_num.setText(log.getType());
		text_detial_address.setText(log.getAddress());
		return contextView;
	}

}
