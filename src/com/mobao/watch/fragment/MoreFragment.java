package com.mobao.watch.fragment;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.mb.zjwb1.R;
import com.mobao.watch.activity.AboutUsActivity;
import com.mobao.watch.activity.FamilyMemberActivity;
import com.mobao.watch.activity.HelpActivity;
import com.mobao.watch.activity.InformationCenterActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.Mutetime_ManageActivity;
import com.mobao.watch.activity.PowerSaveActivity;
import com.mobao.watch.activity.SosRecordActivity;
import com.mobao.watch.activity.WatchMangerActivity;
import com.mobao.watch.activity.WhiteohoneActivity;
import com.mobao.watch.util.SetStepGoalDialog;
import com.mobao.watch.util.YunBaStart;

/**
 * @author �� �������Fragment
 */
public class MoreFragment extends Fragment implements LocationSource,
		AMapLocationListener {
	private TextView familyMemberLayout;
	private TextView watchManagerLayout;
	private TextView feadBackLayout;
	private TextView powersaveLayout;
	private TextView mutetimeLayout;
	private TextView sosRecordLayout;
	private TextView InformationLayout;
	private TextView TextHelpLayout;
	private TextView escnowuser;
	private TextView whitephone;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.more_fragment, container, false);
        context=view.getContext();
		familyMemberLayout = (TextView) view
				.findViewById(R.id.family_member);
		familyMemberLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						FamilyMemberActivity.class);
				startActivity(intent);
			}
		});

		watchManagerLayout = (TextView) view
				.findViewById(R.id.watch_manager);
		watchManagerLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						WatchMangerActivity.class);
				startActivity(intent);
			}
		});

		powersaveLayout = (TextView) view
				.findViewById(R.id.poweraving_Layout);
		powersaveLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						PowerSaveActivity.class);
				startActivity(intent);
			}
		});

		feadBackLayout = (TextView) view
				.findViewById(R.id.feedback_layout);
		feadBackLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), AboutUsActivity.class);
				startActivity(intent);
			}
		});

		mutetimeLayout = (TextView) view
				.findViewById(R.id.Layout_SetWatchMute);
		mutetimeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						Mutetime_ManageActivity.class);
				startActivity(intent);
			}
		});

		// sos界面跳转
		sosRecordLayout = (TextView) view.findViewById(R.id.textSOSHelpNote);
		sosRecordLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						SosRecordActivity.class);
				startActivity(intent);
			}
		});

		// 信息中心页面跳转
		InformationLayout = (TextView) view
				.findViewById(R.id.InformationCenter);
		InformationLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						InformationCenterActivity.class);
				startActivity(intent);
			}
		});

		// 帮助页面跳转
		TextHelpLayout = (TextView) view
				.findViewById(R.id.TextHelpLayout);
		TextHelpLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						HelpActivity.class);
				startActivity(intent);
			}
		});
		//白名单管理页面跳转
		whitephone = (TextView) view
				.findViewById(R.id.whitephone_manager);
		whitephone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						WhiteohoneActivity.class);
				startActivity(intent);
			}
		});
		escnowuser = (TextView) view
				.findViewById(R.id.escnowuser_layout);
		escnowuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
			  final SetStepGoalDialog dialog=new SetStepGoalDialog(context);
			  dialog.ChangeToEscnowuser();
			  dialog.setbtnconfirmauthclickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//反注册云巴，推送
					//解除绑定频道
					Log.i("keis", Arrays.toString(LocationFragment.imeiTopic));
					YunBaStart.UnBindTopic(getActivity(), LocationFragment.imeiTopic);
					LocationFragment.imeiTopic=null;
			
					SharedPreferences autologin =context.getSharedPreferences(
							"logindata", context.MODE_PRIVATE);
					Editor dataedt = autologin.edit();
					dataedt.clear();
					dataedt.commit();
                 
					Intent intent = new Intent(
							context,
							LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					getActivity().finish();
					
				    dialog.dismiss();
				}
			});
			  
			  dialog.setbtndisavowclickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			  
			  dialog.show();
			  
			}
		});

		return view;

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate(OnLocationChangedListener arg0) {

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}
}
