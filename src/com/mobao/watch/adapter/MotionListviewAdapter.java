package com.mobao.watch.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mb.zjwb1.R;

public class MotionListviewAdapter extends BaseAdapter {
	private ArrayList<String> list;
	private Context con;

	public MotionListviewAdapter(Context context, ArrayList<String> list) {
		// TODO Auto-generated constructor stub
		con = context;
		this.list = list;
		// System.out.println("size:"+list.size());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return list.get(index);
	}

	@Override
	public long getItemId(int Id) {
		// TODO Auto-generated method stub
		return Id;
	}

	@Override
	public View getView(int index, View contentView, ViewGroup parent) {
		// TODO Auto-generated method stub

		contentView = LayoutInflater.from(con).inflate(
				R.layout.date_spinner_item, parent, false);

		TextView name = (TextView) contentView
				.findViewById(R.id.Spinner_Item_text);
		name.setText(list.get(index).toString());

		return contentView;
	}

}
