package com.mobao.watch.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.AddBabyActivity;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.activity.PhoneNumberManagerActivity;
import com.mobao.watch.activity.PowerSaveActivity;
import com.mobao.watch.activity.WatchMangerActivity;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.DialogUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.Utility;
import com.mobao.watch.util.WaitDialog;

public class WatchMangerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Baby> babyList;
	myhandel handel=new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	private ProgressDialog progDialog = null; // 圆形进度条
	public WatchMangerListAdapter(Context context, ArrayList<Baby> babyList) {
		super();
		this.context = context;
		this.babyList = babyList;
		progDialog = new ProgressDialog(context);
		
	}

	@Override
	public int getCount() {
		if (babyList != null) {
			return babyList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if (babyList != null && index < babyList.size()) {
			return babyList.get(index);
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
					R.layout.watch_manger_list_item, parent, false);
		}

		final Baby baby = babyList.get(position);

		TextView nameView = (TextView) contentView.findViewById(R.id.textName);
		nameView.setText(baby.getBabyname());

		TextView serialNoView = (TextView) contentView
				.findViewById(R.id.textSerialNo);
		serialNoView.setText(baby.getBabyimei());

		TextView bindingTimeView = (TextView) contentView
				.findViewById(R.id.textBindingTime);
		bindingTimeView.setText(baby.getAddtiem());
		//跳到定位间隔
		RelativeLayout layout_locationinterval=(RelativeLayout) contentView.findViewById(R.id.layout_locationinterval);
		layout_locationinterval.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, PowerSaveActivity.class);
				intent.putExtra("nowimei", baby.getBabyimei());
				context.startActivity(intent);
			}
		});
		//跳转到腕表管理
		RelativeLayout layout_whitelist=(RelativeLayout) contentView.findViewById(R.id.layout_whitelist);
		layout_whitelist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent(context,PhoneNumberManagerActivity.class);
				context.startActivity(i);
			}
		});

		// 获取头像
		ImageView rel_babyhead = (ImageView) contentView
				.findViewById(R.id.rel_babyhead);
		String portrait = baby.getPortrait();
		String str_photo = portrait;
		if (TextUtils.isEmpty(str_photo)) {
			rel_babyhead.setBackgroundResource(R.drawable.babymark);
		} else {
			int len = str_photo.length();
			String head_of_str_photp = str_photo.substring(len-20, len);
			Log.i("AAA", "66head_of_str_photp = " + head_of_str_photp);
			Bitmap bitmap = ChatUtil.getImageCache().getBitmap(head_of_str_photp);
			
			if(bitmap == null){
				byte[] bitmapArray;
				bitmapArray = Base64.decode(portrait,
						Base64.DEFAULT);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bitmap = ChatUtil.toRoundBitmap(BitmapFactory
						.decodeByteArray(bitmapArray,
								0, bitmapArray.length,
								options));
				
//				ChatUtil.getImageCache().putBitmap(head_of_str_photp, bitmap);
			}

			Drawable babyDrawable = new BitmapDrawable(bitmap);
			rel_babyhead.setBackgroundDrawable(babyDrawable);
		}
		RelativeLayout rel_baby_info = (RelativeLayout) contentView.findViewById(R.id.rel_baby_info);
		//点击编辑操作
		rel_baby_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, AddBabyActivity.class);
				intent.putExtra("edit", "edit");
				intent.putExtra("imei", baby.getBabyimei());
				context.startActivity(intent);
				((Activity) context).finish();
			}
		});
		
		//设置静音设置开关
		final CheckBox checkmutetime=(CheckBox) contentView.findViewById(R.id.btn_mutetime);
		if(getmutetime(baby.getBabyimei())){
			checkmutetime.setChecked(true);
		}else{
			checkmutetime.setChecked(false);
		}
		checkmutetime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    showDialog();
				if(checkmutetime.isChecked()){
					String operate="on";
					setmutetime(baby.getBabyimei(), operate,checkmutetime);
			        
				}else{
					String operate="off";
					setmutetime(baby.getBabyimei(), operate,checkmutetime);
				}

			}
		});
		/*checkmutetime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				// TODO Auto-generated method stub
		    if(ismutetime){
			String operate="on";
			if(checked){
				operate="on";
			}else{
				operate="off";
			}
         if(setmutetime(baby.getBabyimei(), operate)){
        	 if(operate.equals("on")){
        		 checkmutetime.setChecked(true);
        	 }else{
        		 checkmutetime.setChecked(false);
        	 }
         }else{
        	 if(operate.equals("on")){
        		 checkmutetime.setChecked(false);
        	 }else{
        		 checkmutetime.setChecked(true);
        	 }
         }
		}
			}
		});
		if(position==babyList.size()-1){
			ismutetime=true;
		}*/
		return contentView;
	}
	
	private boolean setmutetime(String nowimei,String operate, CheckBox checkmutetime) {
		// TODO 设置静音开关
		boolean isscuess=false;
		HttpPost request = new HttpPost("http://"+LoginActivity.dnspodIp+":8088/api/silent");
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", nowimei);
			param.put("operate", operate);
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
                what=1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					arg2=1;
					// 将data再封装成一个json（data为一个json）
					System.out.println("scuess");
					isscuess=true;
					if(operate.equals("off")){
						checkmutetime.setChecked(false);
					}else{
						checkmutetime.setChecked(true);	
					}
					Message msg=new Message();
					msg.what=what;
					msg.arg1=555;
					msg.arg2=arg2;
					handel.sendMessage(msg);
				
				} else {
					arg2=-1;
					isscuess=false;
					if(operate.equals("off")){	
						checkmutetime.setChecked(true);	
					}else{
						checkmutetime.setChecked(false);
					}
					System.out.println("fail:"+result.getString("msg"));
					erro=result.getString("msg");
					Message msg=new Message();
					msg.what=what;
					msg.arg1=555;
					msg.arg2=arg2;
					handel.sendMessage(msg);
					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				 what=-1;
				if(operate.equals("off")){	
					checkmutetime.setChecked(true);	
				}else{
					checkmutetime.setChecked(false);
				}
				isscuess=false;
				Message msg=new Message();
				msg.what=what;
				msg.arg1=555;
				msg.arg2=arg2;
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
		return isscuess;

	}
	
	private boolean getmutetime(String nowimei) {
		// TODO 获取静音开关状态
		boolean on_and_off=false;
		
		HttpPost request = new HttpPost("http://"+LoginActivity.dnspodIp+":8088/api/getsilentstatus");
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", nowimei);
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {

				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 将data再封装成一个json（data为一个json）
					JSONObject data=result.getJSONObject("data");	
					String statu=data.getString("status");
					if(statu.equals("on")){
						on_and_off=true;
					}else{
						on_and_off=false;
					}
					System.out.println("huoquchenggong:"+statu);
				} else {
					System.out.println("fail:"+result.getString("msg"));
					// 失败后摇干啥这里搞
					/*
					 * Toast.makeText(LoginActivity.this, "登陆失败！原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {

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
       return on_and_off;
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
				if (key == 555 && g2 == 1) {		
					
				}

				if (key == 555 && g2 == -1) {		
					String erromsg = "";
					ErroNumberChange change = new ErroNumberChange(context);
					erromsg = change.chang(erro);
				    Toast.makeText(context,context.getResources().getString(R.string.setsilentfail)+erromsg,3000).show();
					
				}

		

			} else {
				Toast.makeText(context,context.getResources().getString(R.string.networkunusable), 3000).show();
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
		progDialog.setMessage(context.getResources().getString(R.string.pleasewait));
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
