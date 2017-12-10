package com.mobao.watch.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.content.Context;
import android.content.Intent;

import com.amap.api.maps2d.model.LatLng;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.LocationHistoryActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.BabyInfo;
import com.mobao.watch.bean.BabyNowLocate;
import com.mobao.watch.bean.BabyNowLocationInfo;
import com.mobao.watch.bean.BabyState;
import com.mobao.watch.bean.LocateInfo;
import com.mobao.watch.bean.LocationLog;
import com.mobao.watch.bean.NowUser;
import com.mobao.watch.bean.SafetyArea;
import com.mobao.watch.bean.SafetyAreaInfo;
import com.mobao.watch.fragment.LocationFragment;

public class BabyLocateServer {

	public static String BASIC_URL = "http://115.28.62.126:8088/api/";

	// 获取宝贝列表
	public static ArrayList<Baby> getBabyList(String userid) {

		ArrayList<Baby> babyList = new ArrayList<Baby>();
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "baby";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", userid));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);

			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray dataArray = jo.getJSONArray("data");
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject data = (JSONObject) dataArray.opt(i);
					Baby baby = new Baby();
					baby.setBabyimei(data.getString("imei"));
					baby.setBabyname(data.getString("name"));
					baby.setPortrait(data.getString("portrait"));
					baby.setBabyphone(data.getString("phone"));
					babyList.add(baby);
				}
				return babyList;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getBabyImmediate(String imei, int num, Context context){
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babyImmediate";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("num",String.valueOf(num)));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				String msg = jo.getString("msg");
				 if(BabyFragmentActivity.progDialog.isShowing()){
						BabyFragmentActivity.progDialog.dismiss();
					    }
				if(msg.equals("20000")){
					return "操作成功";
				}
				if(msg.equals("10090")){
					return "请求定位失败";
				}
				if(msg.equals("10091")){
					return "定位请求过于频繁";
				}
				if(msg.equals("10100")){
					return "宝贝不在线";
				}
				if(msg.equals("10101")){
					if(num<3){
						System.out.println("num:"+num);
						Intent intent=new Intent();
						intent.setAction("opendialog");
						intent.putExtra("num", num);
						context.sendBroadcast(intent);
						num++;
						getBabyImmediate(imei,num,context);		
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	// 获取宝贝目前位置
	public static BabyNowLocationInfo getBabyNowLocation(String imei) {
		LatLng latlng = new LatLng(0, 0);
		String language = "";
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babynowlocate";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			if(LoginActivity.isCN){
				language = "china";
			}else{
				language = "english";
			}
			param.add(new BasicNameValuePair("language", language));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONObject data = (JSONObject) jo.get("data");
				double lat = Double.valueOf(data.getString("lat"));
				double lon = Double.valueOf(data.getString("lon"));
				String time = String.valueOf(data.getString("ultratime"));
				String address = String.valueOf(data.getString("address"));
				String state = String.valueOf(data.getString("state"));
				latlng = new LatLng(lat, lon);
				return new BabyNowLocationInfo(latlng,time,"","yes");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	//获取所有宝贝当前位置
	public static ArrayList<BabyNowLocate> getAllBabyNowLocate(String userid){
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babynowlocate";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", userid));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				ArrayList<BabyNowLocate> list = new ArrayList<BabyNowLocate>();
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray locateArray = jo.getJSONArray("data");
				if (locateArray.length() == 0) {
					return null;
				}
				for (int i = 0; i < locateArray.length(); i++) {
					JSONObject locate = (JSONObject) locateArray.opt(i);
					BabyNowLocate bl = new BabyNowLocate();
					bl.setImei(locate.getString("imei"));
					bl.setName(locate.getString("name"));
					bl.setLon(locate.getString("lon"));
					bl.setLat(locate.getString("lat"));
					bl.setAddress(locate.getString("address"));
					list.add(bl);
				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	// 获取宝贝信息
	public BabyInfo getBabyInfo(String imei) {
		BabyInfo babyInfo = new BabyInfo();
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babyinfo";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONObject data = (JSONObject) jo.get("data");
				String name = data.getString("name");
				String star = data.getString("star");
				String step = data.getString("step");
				String distance = data.getString("distance");
				JSONArray locateArray = data.getJSONArray("locate");
				ArrayList<LocateInfo> locateInfoList = new ArrayList<LocateInfo>();
				for (int i = 0; i < locateArray.length(); i++) {
					// JSONObject locate = (JSONObject)locateArray.opt(i);
					// LocateInfo li = new LocateInfo();
					// li.setX(locate.getString("x"));
					// li.setY(locate.getString("y"));
					// li.setTime(locate.getString("time"));
					// li.setAddress(locate.getString("address"));
					// locateInfoList.add(li);
				}
				babyInfo = new BabyInfo(name, star, step, distance,
						locateInfoList);
				return babyInfo;
			} else {
				return babyInfo;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return babyInfo;
		}
	}

	// 获取宝贝历史位置信息
	public static ArrayList<LocateInfo> getBabyHistoryInfo(String imei,
			String starttime, String endtime) {
		ArrayList<LocateInfo> locateInfoList = null;
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babytimelocate";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("starttime", starttime));
			param.add(new BasicNameValuePair("endtime", endtime));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {
				locateInfoList = new ArrayList<LocateInfo>();
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray locateArray = jo.getJSONArray("data");
				if (locateArray.length() == 0) {
					return null;
				}
				for (int i = 0; i < locateArray.length(); i++) {
					JSONObject locate = (JSONObject) locateArray.opt(i);
					LocateInfo li = new LocateInfo();
					li.setLat(locate.getString("lat"));
					li.setLon(locate.getString("lon"));
					li.setAddress(locate.getString("address"));
//					li.setSaferegionname(locate.getString("saferegionname"));
					li.setSaferegionname("");
					li.setHour(locate.getString("hour"));
					li.setMintue(locate.getString("minute"));
					li.setStaytime(locate.getString("staytime"));
					locateInfoList.add(li);
				}
			}
			return locateInfoList;
		} catch (Exception e) {
			e.printStackTrace();
			return locateInfoList;
		}
	}

	// 根据宝贝imei获取安全列表
	public static ArrayList<SafetyAreaInfo> getSafetyAreaList(String imei) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "saferegion";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray dataArray = jo.getJSONArray("data");
				if (dataArray.length() == 0) {
					return null;
				}
				ArrayList<SafetyAreaInfo> safety_area_list = new ArrayList<SafetyAreaInfo>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject data = (JSONObject) dataArray.opt(i);
					SafetyAreaInfo sai = new SafetyAreaInfo();
					sai.setId(data.getString("id"));
					sai.setSafety_name(data.getString("name"));
					sai.setSafety_address(data.getString("address"));
					sai.setSafety_range(data.getString("radius"));
					sai.setLat(data.getString("lat"));
					sai.setLon(data.getString("lon"));
					safety_area_list.add(sai);
				}
				return safety_area_list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	//根据用户id获取安全列表
	public static ArrayList<SafetyArea> getSafetyAreaListByUserid(String userid) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babysaferegion";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", userid));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray dataArray = jo.getJSONArray("data");
				if (dataArray.length() == 0) {
					return null;
				}
				ArrayList<SafetyArea> safety_area_list = new ArrayList<SafetyArea>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject data = (JSONObject) dataArray.opt(i);
					SafetyArea sai = new SafetyArea();
					sai.setId(data.getString("id"));
					sai.setImei(data.getString("imei"));
					sai.setBabyname(data.getString("babyname"));
					sai.setAddress(data.getString("address"));
					sai.setRadius(data.getString("radius"));
					sai.setLon(data.getString("lon"));
					sai.setLat(data.getString("lat"));
				}
				return safety_area_list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	// 添加安全区域
	public static boolean addSafetyArea(SafetyArea safetyArea) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "addsaferegion";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", safetyArea.getImei()));
			param.add(new BasicNameValuePair("name", safetyArea.getName()));
			param.add(new BasicNameValuePair("address", safetyArea.getAddress()));
			param.add(new BasicNameValuePair("radius", safetyArea.getRadius()));
			param.add(new BasicNameValuePair("lat", safetyArea.getLat()));
			param.add(new BasicNameValuePair("lon", safetyArea.getLon()));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 修改安全区域
	public static boolean alertSafetyArea(SafetyAreaInfo safetyArea,boolean isChangeLocation) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "modifysaferegion";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", LocationFragment.now_babyimei));
			param.add(new BasicNameValuePair("saferegionid", safetyArea.getId()));
			param.add(new BasicNameValuePair("name", safetyArea
					.getSafety_name()));
			param.add(new BasicNameValuePair("address", safetyArea
					.getSafety_address()));
			param.add(new BasicNameValuePair("radius", safetyArea
					.getSafety_range()));
			if(isChangeLocation){
				param.add(new BasicNameValuePair("lat", safetyArea
						.getLat()));
				param.add(new BasicNameValuePair("lon", safetyArea
						.getLon()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 删除安全区域
	public static boolean deleteSafetyArea(String saferegionid,String imei) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "deletesaferegion";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("saferegionid", saferegionid));
			param.add(new BasicNameValuePair("imei", imei));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//用户宝贝位置状态提醒
	public static ArrayList<BabyState> babyLocateState(String userid){
		String language = "";
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL.concat("babylocalremind");
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", userid));
			if(LoginActivity.isCN){
				language = "china";
			}else{
				language = "english";
			}
			param.add(new BasicNameValuePair("language", language));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray dataArray = jo.getJSONArray("data");
				if (dataArray.length() == 0) {
					return null;
				}
				ArrayList<BabyState> list = new ArrayList<BabyState>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject data = (JSONObject) dataArray.opt(i);
					BabyState sai = new BabyState();
					sai.setImei(data.getString("imei"));
					sai.setName(data.getString("babyname"));
					sai.setAddress(data.getString("name"));
					sai.setTime(data.getString("time"));
					sai.setLat(data.getString("lat"));
					sai.setLon(data.getString("lon"));
					sai.setCondition(data.getString("condition"));
					list.add(sai);
				}
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	//获取宝贝最后位置
	public static JSONObject getbabylastlocate(String imei,String language){
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babylastlocate";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("language",language));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONObject lastloact = jo.getJSONObject("data");
				return lastloact;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	
	// 获取宝贝历史位置信息
		public static ArrayList<LocationLog> getBabyHistoryLog(String imei,
				String starttime, String endtime) {
			ArrayList<LocationLog> locateInfoList = null;
			try {
				HttpClient client = new DefaultHttpClient();
				String url = BASIC_URL + "babyalllocate";
				HttpPost httpPost = new HttpPost(url);
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("imei", imei));
				param.add(new BasicNameValuePair("starttime", starttime));
				param.add(new BasicNameValuePair("endtime", endtime));
				httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
				HttpResponse response = client.execute(httpPost);
				int code = response.getStatusLine().getStatusCode();

				if (code == 200) {
					locateInfoList = new ArrayList<LocationLog>();
					InputStream is = response.getEntity().getContent();
					String str = read(is);
					JSONObject jo = new JSONObject(str);
					JSONArray locateArray = jo.getJSONArray("data");
					if (locateArray.length() == 0) {
						return null;
					}
					for (int i = 0; i < locateArray.length(); i++) {
						JSONObject locate = (JSONObject) locateArray.opt(i);
						LocationLog li = new LocationLog();
						li.setAddress(locate.getString("address"));
						li.setHour(locate.getString("hour"));
						li.setMinute(locate.getString("minute"));
						li.setType(locate.getString("type"));
						locateInfoList.add(li);
					}
				}
				return locateInfoList;
			} catch (Exception e) {
				e.printStackTrace();
				return locateInfoList;
			}
		}

	// 将流转化成字符串
	public static String read(InputStream is) throws IOException {
		byte[] data;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) != -1) {
			bout.write(buf, 0, len);
		}
		data = bout.toByteArray();
		return new String(data, "UTF-8");
	}

	// new Thread(){
	// public void run(){
	// final LatLng getLatlng=
	// BabyLocateServer.getBabyNowLocation(now_babyimei);
	// if(getLatlng!=null){
	// runOnUiThread(new Runnable() {
	// public void run() {
	//
	// }
	// });
	// }else{
	// runOnUiThread(new Runnable() {
	// public void run() {
	// Toast.makeText(this, "宝贝位置获取失败", 1000).show();
	// }
	// });
	// }
	// }
	// }.start();
}
