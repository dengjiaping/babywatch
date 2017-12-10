package com.mobao.watch.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.AlertSafetyAreaActivity;
import com.mobao.watch.activity.LocationHistoryActivity;
import com.mobao.watch.activity.SafetyAreaActivity;
import com.mobao.watch.bean.SafetyAreaInfo;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.CustomDialog;

public class SafetyAreaListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<SafetyAreaInfo> safety_area_list;

	private boolean deleteComplete = false;

	public SafetyAreaListAdapter(Context context,
			ArrayList<SafetyAreaInfo> safety_area_list) {
		super();
		this.context = context;
		this.safety_area_list = safety_area_list;
	}

	@Override
	public int getCount() {
		if (safety_area_list == null) {
			return 0;
		}
		return safety_area_list.size();
	}

	@Override
	public Object getItem(int index) {
		if (safety_area_list != null && index < safety_area_list.size()) {
			return safety_area_list.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		if (safety_area_list != null && index < safety_area_list.size()) {
			return index;
		}
		return 0;
	}

	@Override
	public View getView(final int index, View contextView, ViewGroup parent) {
		if (contextView == null) {
			contextView = LayoutInflater.from(context).inflate(
					R.layout.safety_area_list_item, parent, false);
		}
		TextView text_safety_name = (TextView) contextView
				.findViewById(R.id.text_safety_name);
		TextView text_safety_address = (TextView) contextView
				.findViewById(R.id.text_safety_address);

		final SafetyAreaInfo info = safety_area_list.get(index);

		text_safety_name.setText(info.getSafety_name());
		String address = info.getSafety_address();
		if(address.length()>20){
			address = address.substring(0, 20)+"......";
			text_safety_address.setText(address);
		}else{
		    text_safety_address.setText(address);
		}
		// 进入修改安全区域Activity
		contextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, AlertSafetyAreaActivity.class);
				intent.putExtra("index", index);
				intent.putExtra("id", info.getId());
				context.startActivity(intent);
				((Activity) context).finish();
			}
		});

		// 设置长按删除安全区域事件
		contextView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// 创建自定义的提示框
				CustomDialog.Builder builder = new CustomDialog.Builder(context);
				builder.setMessage(context.getString(R.string.suredeletesafearea));
				builder.setTitle(context.getResources().getString(R.string.dialog_body_text));
				builder.setPositiveButton(context.getString(R.string.sure),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 删除操作
								new Thread() {
									public void run() {
										final boolean result = BabyLocateServer
												.deleteSafetyArea(info.getId(),LocationHistoryActivity.now_babyimei);
										if (result == true) {
											((Activity) context)
													.runOnUiThread(new Runnable() {
														@Override
														public void run() {
															Toast.makeText(
																	context,
																	context.getString(R.string.deletesuccess),
																	3000)
																	.show();
															safety_area_list
																	.remove(index);
															SafetyAreaActivity.adapter
																	.notifyDataSetChanged();
														}
													});
										} else {
											((Activity) context)
													.runOnUiThread(new Runnable() {
														@Override
														public void run() {
															Toast.makeText(
																	context,
																	context.getString(R.string.deletefail),
																	3000)
																	.show();
														}
													});
										}
									}
								}.start();
							}
						});

				builder.setNegativeButton(context.getString(R.string.cancel),
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
				return false;
			}
		});
		
		if (deleteComplete) {
			SafetyAreaActivity.adapter.notifyDataSetChanged();
			deleteComplete = false;
		}
		return contextView;
	}

}
