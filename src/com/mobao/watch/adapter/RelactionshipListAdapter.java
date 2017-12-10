package com.mobao.watch.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.AddBabyActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.bean.RelactionShip;
import com.mobao.watch.util.Languageiswhat;

public class RelactionshipListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<RelactionShip> relactionship_list;

	public RelactionshipListAdapter(Context context,
			ArrayList<RelactionShip> relactionship_list) {
		super();
		this.context = context;
		this.relactionship_list = relactionship_list;
	}

	@Override
	public int getCount() {
		if (relactionship_list == null) {
			return 0;
		}
		return relactionship_list.size();
	}

	@Override
	public Object getItem(int index) {
		if (relactionship_list != null && index < relactionship_list.size()) {
			return relactionship_list.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		if (relactionship_list != null && index < relactionship_list.size()) {
			return index;
		}
		return 0;
	}

	@Override
	public View getView(int index, View contextView, ViewGroup parent) {
		if (contextView == null) {
			contextView = LayoutInflater.from(context).inflate(
					R.layout.selectrelactionshipadapter, parent, false);
		}
		final RelactionShip rs = relactionship_list.get(index);
		LinearLayout liner_relactionship = (LinearLayout) contextView
				.findViewById(R.id.liner_relactionship);
		TextView txt_relacationship = (TextView) contextView
				.findViewById(R.id.txt_relacationship);
		Languageiswhat lw = new Languageiswhat(context);
		if (lw.isZh()) {
			txt_relacationship.setText(rs.getRelate());
		} else {
			txt_relacationship.setText(rs.getValue());
		}

		liner_relactionship
				.setOnClickListener(new LinearLayout.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(
								AddBabyActivity.GET_RELACTIONSHIP_ACTION);
						intent.putExtra("value", rs.getValue());
						Languageiswhat lw = new Languageiswhat(context);
						if (lw.isZh()) {
							intent.putExtra("relate", rs.getRelate());
						} else {
							intent.putExtra("relate", rs.getValue());
						}
						context.sendBroadcast(intent);
						((Activity) context).finish();
					}
				});
		return contextView;
	}

}
