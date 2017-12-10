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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.bean.AddBaby;
import com.mobao.watch.bean.AddBabyResult;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.CanAddBaby;
import com.mobao.watch.bean.FamilyMember;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.RelactionShip;
import com.mobao.watch.util.ToastUtil;

public class BabyServer {

	public static String BASIC_URL = "http://"+LoginActivity.dnspodIp+":8088/api/";
	// 判断是否能添加宝贝
	public static CanAddBaby canAddBaby(String imei) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "ifaddbaby";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", MbApplication
					.getGlobalData().getNowuser().getUserid()));
			param.add(new BasicNameValuePair("imei", imei));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			InputStream is = response.getEntity().getContent();
			String str = read(is);
			JSONObject jo = new JSONObject(str);
			JSONObject data = jo.getJSONObject("data");
			String msg = jo.getString("msg");
			CanAddBaby result = new CanAddBaby();
			if (code == 200) {
				if (msg.equals("20000")) {
					result.setResult("success");
					return result;
				} else if (msg.equals("20003")) {
					result.setResult("need authorize");
					result.setAdmin(data.getString("admin"));
					result.setPhone(data.getString("phone"));
					return result;
				} else if (msg.equals("10043")) {
					result.setResult("already has admin");
					result.setAdmin(data.getString("admin"));
					result.setPhone(data.getString("phone"));
					return result;
				} else if (msg.equals("10041")) {
					result.setResult("need a admin");
					return result;
				} else if (msg.equals("10042")) {
					result.setResult("already own baby");
					return result;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 判断当前用户是否为管理员
	public static boolean isAdmin(String userid) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "ifadmin";
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
				JSONObject data = jo.getJSONObject("data");
				String status = data.getString("status");
				if (status.equals("yes")) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 获取关系列表
	public static ArrayList<RelactionShip> getRelationShipList() {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babyrelatelist";
			HttpPost httpPost = new HttpPost(url);
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				ArrayList<RelactionShip> list = new ArrayList<RelactionShip>();
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONArray relactionArray = jo.getJSONArray("data");
				for (int i = 0; i < relactionArray.length(); i++) {
					JSONObject data = (JSONObject) relactionArray.opt(i);
					RelactionShip rs = new RelactionShip();
					rs.setRelate(data.getString("relate"));
					rs.setValue(data.getString("value"));
					list.add(rs);
				}
				return list;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 添加宝贝
	public static AddBabyResult addBaby(AddBaby addbaby, String userid) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "addbaby";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", userid));
			param.add(new BasicNameValuePair("imei", addbaby.getImei()));
			param.add(new BasicNameValuePair("relate", addbaby.getRelate()));
			param.add(new BasicNameValuePair("portrait", addbaby.getPortrait()));
			param.add(new BasicNameValuePair("name", addbaby.getName()));
			param.add(new BasicNameValuePair("age", addbaby.getAge()));
			param.add(new BasicNameValuePair("babyphone", addbaby
					.getBabyphone()));
			param.add(new BasicNameValuePair("height", addbaby.getHeight()));
			param.add(new BasicNameValuePair("sex", addbaby.getSex()));
			param.add(new BasicNameValuePair("weight", addbaby.getWeight()));
			param.add(new BasicNameValuePair("steplen", addbaby.getSteplen()));
			param.add(new BasicNameValuePair("birthday", addbaby.getBirthday()));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				String msg = jo.getString("msg");
				if (msg.equals("20003")) {
					JSONObject data = jo.getJSONObject("data");
					String admin = data.getString("admin");
					String phone = data.getString("phone");
					AddBabyResult addBabyResult = new AddBabyResult(
							"need authorize", phone, admin);
					return addBabyResult;
				} else if (msg.equals("20000")) {
					return new AddBabyResult("success", "", "");
				} else if (msg.equals("10040")) {
					return new AddBabyResult("chongtu", "", "");
				}
			} else if (code == 300) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				String msg = jo.getString("msg");
				if (msg.equals("10043")) {
					JSONObject data = jo.getJSONObject("data");
					String admin = data.getString("admin");
					String phone = data.getString("phone");
					return new AddBabyResult("already has admin", phone, admin);
				} else if (msg.endsWith("10041")) {
					return new AddBabyResult("need a admin", "", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	// 获取单个宝贝
	public static Baby getBaby(String userid, String imei) {

		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babyallinfo";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("userid", userid));
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(httpPost);

			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String str = read(is);
				JSONObject jo = new JSONObject(str);
				JSONObject data = jo.getJSONObject("data");
				Baby baby = new Baby();
				baby.setBabyname(data.getString("name"));
				baby.setPortrait(data.getString("portrait"));
				baby.setRelate(data.getString("relate"));
				baby.setAge(data.getString("age"));
				if (LoginActivity.isCN) {
					baby.setValue(data.getString("value"));
				} else {
					baby.setValue(data.getString("relate"));
				}
				baby.setHeight(data.getString("height"));
				baby.setBabyphone(data.getString("phone"));
				baby.setSteepLong(data.getString("steplen"));
				baby.setWeight(data.getString("weight"));
				baby.setSex(data.getString("sex"));
				baby.setBirthday(data.getString("birthday"));
				return baby;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// 获取宝贝列表
	public static ArrayList<Baby> getBabyList() {

		ArrayList<Baby> babyList = new ArrayList<Baby>();
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "baby";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("userid", MbApplication
					.getGlobalData().getNowuser().getUserid()));
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
					baby.setAddtiem(data.getString("addtime"));
					baby.setPortrait(data.getString("portrait"));
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

	//删除宝贝
	public static boolean deleteBaby(String userid, String imei) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "deldevice";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("userid", userid));
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

	// 获取家庭成员
	public static ArrayList<FamilyMember> getFamilyMember(String userid) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "family";
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
				ArrayList<FamilyMember> familyList = new ArrayList<FamilyMember>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject data = (JSONObject) dataArray.opt(i);
					FamilyMember member = new FamilyMember();
					member.setUserid(data.getString("userid"));
					member.setPhone(data.getString("phone"));
					member.setImei(data.getString("imei"));
					member.setRelate(data.getString("relate"));
					member.setAdmin(data.getString("admin"));
					member.setValue(data.getString("value"));
					familyList.add(member);
				}
				return familyList;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean deleteFamilyMember(String imei, String userid) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = BASIC_URL + "babydeleterelate";
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("imei", imei));
			param.add(new BasicNameValuePair("userid", userid));
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

}
