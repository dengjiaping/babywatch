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
import com.mobao.watch.activity.PhoneNumberManagerActivity.myhandel;
import com.mobao.watch.util.AddWhitephoneDialog;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

public class WhitephoneAdapter extends BaseAdapter {
	private String[] jsonarray;
	private Context context;
	private ProgressDialog progDialog = null; // 圆形进度条
	String erro = "";
	final myhandel handel = new myhandel();
	ErroNumberChange change;
	public WhitephoneAdapter(Context context, String[] jsonarray) {
		this.jsonarray = jsonarray;
		this.context = context;
		progDialog=new ProgressDialog(context);
		change = new ErroNumberChange(context);

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
	
		
		final int i=index;
		TextView phone = (TextView) contextview.findViewById(R.id.tv_number_item_number);
		ImageView iv_number_item_delete=(ImageView) contextview.findViewById(R.id.iv_number_item_delete);
		if(jsonarray[i].indexOf("x")==-1){
			phone.setText(jsonarray[index]);
			iv_number_item_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					final AddWhitephoneDialog dialog = new AddWhitephoneDialog(
							context,
							R.style.chatfragment_call_dialog_style);
					dialog.ChangeToDelWhitephone(jsonarray[i]);
					dialog.setSureBtOnclickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							showDialog();
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									delwhitelist(jsonarray[i]);
								}
							}).start();

						}
					});
					dialog.setCancelBtOnclickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					dialog.show();
					
				}
			});
	        
		}else{
			phone.setText(jsonarray[index].replace("x",""));
		  iv_number_item_delete.setVisibility(View.GONE);
		}
		
		return contextview;
	}
	private void delwhitelist(String phone) {
		// TODO 删除白名单
		HttpPost request = new HttpPost("http://" + LoginActivity.dnspodIp + ":8088/api/delwhitelist");
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());
			param.put("phone", phone);
			System.out.println("删除："+param.toString());
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			System.out.println(param.toString());
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
			
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				// 判断获取是否成功
				int status = result.getInt("status");
				System.out.println(result.toString());
				if (status == 200) {
					System.out.println("删除成功");
					//发送广播
					context.sendBroadcast(new Intent("CHANGE_PhoneNumberData_ACTION"));
					Message msg=Message.obtain();
					msg.what=1;
					msg.arg1=555;
					msg.arg2=1;
					handel.sendMessage(msg);
				} else {
					// 失败后摇干啥这里搞
					erro=result.getString("msg");
					Message msg=Message.obtain();
					msg.what=1;
					msg.arg1=555;
					msg.arg2=-1;
					handel.sendMessage(msg);
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				Message msg=Message.obtain();
				msg.what=-1;
				msg.arg1=555;
				msg.arg2=-1;
				handel.sendMessage(msg);
				/*
				 * Toast.makeText(LoginActivity.this, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG).show();
				 */
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Aut-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public class myhandel extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int key = msg.arg1;
			int g2 = msg.arg2;
			int lj = msg.what;
			dismissDialog();
			if (lj == 1) {

				if (key == 555 && g2 == -1) {
					String erromsg = change.chang(erro);
					Toast.makeText(
							context,
							context.getResources()
									.getString(R.string.login_fail) + erromsg,
							3000).show();

				}

				

			} else {

				Toast.makeText(context,
						context.getResources().getString(R.string.networkunusable), 3000).show();
			}

		}

	}
	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(context.getResources().getString(R.string.gettingdata));
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
}
