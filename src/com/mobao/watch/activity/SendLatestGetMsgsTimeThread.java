package com.mobao.watch.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.NowUser;
import com.mobao.watch.util.ChatUtil;

import android.content.Context;
import android.util.Log;

public class SendLatestGetMsgsTimeThread extends Thread {
	
	private Context context;
	private String date;
	
	public SendLatestGetMsgsTimeThread(Context context, String date){
		this.context = context;
		this.date = date;
	}
	
	@Override
	public void run() {
		HttpPost post = new HttpPost(ChatUtil.baseUrl.concat("setmessagereadtime"));
		JSONObject params = new JSONObject();
		try {
			params.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());
			params.put("time", date);
			
			post.setEntity(new StringEntity(params.toString()));
			
			Log.w("yyy", "上传到服务器的最后时间params = " + params.toString());
			
			HttpResponse response = new DefaultHttpClient().execute(post);
			
			Log.w("yyy", "发送最后时间连接  = " + response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() == 200){
				String resultStr = EntityUtils.toString(response.getEntity());
				
				JSONObject result = new JSONObject(resultStr);
				
				Log.w("yyy", "发送最后时间操作  = " + result.getInt("status"));
				if(result.getInt("status") != 200){
				}
				
			}
		} catch (Exception e) {
		}
		
	}
	
}
