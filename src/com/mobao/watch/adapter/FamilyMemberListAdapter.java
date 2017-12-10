package com.mobao.watch.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.FamilyMemberActivity;
import com.mobao.watch.activity.SafetyAreaActivity;
import com.mobao.watch.bean.FamilyMember;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.Languageiswhat;

public class FamilyMemberListAdapter extends BaseAdapter {

	String[] fanilyShipNames;
	String[] fanilyShipPhones;
	private ArrayList<FamilyMember> familyList = null;
	private Context context;

	public FamilyMemberListAdapter(Context context, String[] fanilyShipNames,
			String[] fanilyShipPhones) {
		this.context = context;
		this.fanilyShipNames = fanilyShipNames;
		this.fanilyShipPhones = fanilyShipPhones;
	}

	public FamilyMemberListAdapter(Context context,
			ArrayList<FamilyMember> familyList) {
		super();
		this.familyList = familyList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (familyList != null) {
			return familyList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if (familyList != null && index < familyList.size()) {
			return familyList.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup parent) {

		if (contentView == null) {
			contentView = LayoutInflater.from(context).inflate(
					R.layout.family_member_list_item, parent, false);
		}

		final FamilyMember member = familyList.get(position);

		TextView nameView = (TextView) contentView
				.findViewById(R.id.textRelationshipName);
		Languageiswhat liw = new Languageiswhat(context);
		if (liw.isZh()) {
			nameView.setText(member.getValue());
		} else {
			nameView.setText(member.getRelate());
		}

		TextView phoneView = (TextView) contentView
				.findViewById(R.id.textRelationshipPhone);
		phoneView.setText(member.getPhone());

//		contentView.setOnLongClickListener(new OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				// 创建自定义的提示框
//				CustomDialog.Builder builder = new CustomDialog.Builder(context);
//				builder.setMessage("您确定删除此家庭成员吗？");
//				builder.setTitle("提示");
//				builder.setPositiveButton("确定",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.dismiss();
//								new Thread() {
//									public void run() {
//										final boolean result = BabyServer
//												.deleteFamilyMember(member.getImei(),
//														GlobalData.getInstance().getNowuser().getUserid());
//										if (result == true) {
//											((Activity) context)
//													.runOnUiThread(new Runnable() {
//														public void run() {
//															Toast.makeText(context,"删除成功",2000).show();
//															context.startActivity(new Intent(context,FamilyMemberActivity.class));
//															((Activity) context).finish();
//														}
//													});
//										} else {
//											((Activity) context)
//													.runOnUiThread(new Runnable() {
//														public void run() {
//															Toast.makeText(context,"网络繁忙，请稍后重试",2000).show();
//														}
//													});
//										}
//									}
//								}.start();
//
//							}
//						});
//
//				builder.setNegativeButton("取消",
//						new android.content.DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.dismiss();
//							}
//						});
//
//				builder.create().show();
//				return false;
//			}
//		});
		return contentView;
	}

}
