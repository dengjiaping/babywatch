package com.mobao.watch.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.activity.PhoneNumberManagerActivity;
import com.mobao.watch.activity.WhiteohoneActivity;
import com.mobao.watch.util.AddWhitephoneDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SOSphoneAdapter extends BaseAdapter {
	private String[] jsonarray;
	private Context context;

	public SOSphoneAdapter(Context context, String[] jsonarray) {
		this.jsonarray = jsonarray;
		this.context = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (jsonarray != null) {
			return jsonarray.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		if (jsonarray != null && index < jsonarray.length) {
			return jsonarray[index];
		}
		return null;
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int index, View contextview, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (contextview == null) {
			contextview = LayoutInflater.from(context).inflate(
					R.layout.phone_number_listview_item, parent, false);
		}
		TextView phone = (TextView) contextview.findViewById(R.id.tv_number_item_number);
		phone.setText(jsonarray[index]);
		
		final int i=index;
		
		ImageView iv_number_item_delete=(ImageView) contextview.findViewById(R.id.iv_number_item_delete);
		iv_number_item_delete.setVisibility(View.GONE);
		return contextview;
	}
	
}
