package com.mobao.watch.adapter;

import java.util.ArrayList;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BasicMapActivity;
import com.mobao.watch.bean.SosRecordInfo;

public class SosRecordListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<SosRecordInfo> sos_info;

	public SosRecordListAdapter(Context context,
			ArrayList<SosRecordInfo> sos_info) {
		this.context = context;
		this.sos_info = sos_info;
	}

	@Override
	public int getCount() {
		if (sos_info == null) {
			return 0;
		}
		return sos_info.size();
	}

	@Override
	public Object getItem(int index) {
		if (sos_info != null && index < sos_info.size()) {
			return sos_info.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		if (sos_info != null && index < sos_info.size()) {
			return index;
		}
		return 0;
	}

	@Override
	public View getView(int index, View contextView, ViewGroup parent) {
		if (contextView == null) {
			contextView = LayoutInflater.from(context).inflate(
					R.layout.sos_record_list_item, parent, false);
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
		final SosRecordInfo sos = sos_info.get(index);
		
		TextView text_month = (TextView) contextView
				.findViewById(R.id.text_month);
		TextView text_sos_address = (TextView) contextView
				.findViewById(R.id.text_sos_address);
		TextView text_date = (TextView) contextView
				.findViewById(R.id.text_sos_date);

		String hour = sos.getSos_hour();
		String minute = sos.getSos_minute();
		String address = sos.getSos_address();
		String date = sos.getSos_date();
		String month=sos.getSos_month();
		
		text_date.setText(date);
		text_sos_address.setText(address);
		text_month.setText(month+context.getResources().getString(R.string.month));
		
		LinearLayout rel_right=(LinearLayout) contextView.findViewById(R.id.rel_right);
		rel_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent inten=new Intent(context,BasicMapActivity.class);
				inten.putExtra("lat", sos.getSos_lat());
				inten.putExtra("lon", sos.getSos_lon());
				context.startActivity(inten);
			}
		});
		// 设置参数值（地址，上下午，时间）
		/*text_am_or_pm.setText("am");// 上下午设置
		text_detial_address.setText(address);
		text_date.setText(date);*/

		/*// 12小时格式设置(设置时间)
		int time_hour = Integer.valueOf(hour);
		if (time_hour > 12) {
			time_hour -= 12;
			text_am_or_pm.setText("pm");
			text_time.setText(time_hour + ":" + minute);
		} else {
			text_time.setText(hour + ":" + minute);
		}
*/
		return contextView;
	}

}
