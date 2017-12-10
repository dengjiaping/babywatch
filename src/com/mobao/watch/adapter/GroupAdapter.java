package com.mobao.watch.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.bean.Baby;

public class GroupAdapter extends BaseAdapter {

	private Context context;

	private List<Baby> list;

	public GroupAdapter(Context context, List<Baby> list) {

		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (list != null && position < list.size()) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		if (list != null && position < list.size()) {
			return position;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.group_item, null);
			holder = new ViewHolder();

			convertView.setTag(holder);

			holder.groupItem = (TextView) convertView
					.findViewById(R.id.groupItem);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.groupItem.setText(list.get(position).getBabyname());

		return convertView;
	}

	static class ViewHolder {
		TextView groupItem;
	}

}
