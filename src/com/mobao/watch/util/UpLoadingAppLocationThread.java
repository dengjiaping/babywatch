package com.mobao.watch.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.NowUser;

public class UpLoadingAppLocationThread extends Thread {

	private static String baseUrl = CommonUtil.baseUrl;

	private static final String UPLOAD_LOCATE = "uploadlocate";

	private double lat;
	private double lon;
	private String babyImei;
	private String address;
	private Context context;

	public UpLoadingAppLocationThread(double lat, double lon, String babyImei,
			String address, Context context) {
		this.lat = lat;
		this.lon = lon;
		this.babyImei = babyImei;
		this.address = address;
		this.context = context;
	}

	@Override
	public void run() {

		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			baseUrl = "http://" + ip + ":8088/api/";
		}

		HttpPost post = new HttpPost(baseUrl.concat(UPLOAD_LOCATE));

		JSONObject json = new JSONObject();

		try {
			json.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());
			json.put("imei", babyImei);
			json.put("address", address);
			json.put("lon", lon);
			json.put("lat", lat);

			post.setEntity(new StringEntity(json.toString(), "utf-8"));

			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {

				String resSrc = EntityUtils.toString(response.getEntity());

				JSONObject result = new JSONObject(resSrc);

				if (result.getInt("status") == 200) {
					Log.w("uuu", "发送地址成功");
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
