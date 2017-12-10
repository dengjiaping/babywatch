package com.mobao.watch.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.Mutetime_Data_SetActivity;

public class MutetimeListviewAdapter extends BaseAdapter {
	private String[] name = { "时间段1", "时间段2", "时间段3", "时间段4", "时间段5", "时间段6", };
	private Context con;

	public MutetimeListviewAdapter(Context context) {
		// TODO Auto-generated constructor stub
		con = context;
		// System.out.println("size:"+list.size());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return name.length;
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return name[index];
	}

	@Override
	public long getItemId(int Id) {
		// TODO Auto-generated method stub
		return Id;
	}

	@Override
	public View getView(int index, View contentView, ViewGroup parent) {
		// TODO Auto-generated method stub
		contentView = null;
		if (contentView == null) {
			contentView = LayoutInflater.from(con).inflate(
					R.layout.mutetime_list_item, parent, false);
		}

		TextView mutetime_text = (TextView) contentView
				.findViewById(R.id.text_mutetime);
		mutetime_text.setText(name[index]);

		TextView mutetime_data_time = (TextView) contentView
				.findViewById(R.id.mutetime_manage_data_time);
		final LinearLayout datalayout = (LinearLayout) mutetime_data_time
				.getParent();
		final LinearLayout separatelayout = (LinearLayout) contentView
				.findViewById(R.id.mutetime_data_separate);

		final CheckBox checkbox_mutetime = (CheckBox) contentView
				.findViewById(R.id.checkbox_mutetime);
		checkbox_mutetime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (checkbox_mutetime.isChecked()) {
					datalayout.setVisibility(View.VISIBLE);
					separatelayout.setVisibility(View.VISIBLE);
					Intent i = new Intent(con, Mutetime_Data_SetActivity.class);
					con.startActivity(i);

				} else {
					datalayout.setVisibility(View.GONE);
					separatelayout.setVisibility(View.GONE);
				}

			}
		});

		return contentView;
	}

}
