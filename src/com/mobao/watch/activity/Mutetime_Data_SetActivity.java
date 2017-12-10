package com.mobao.watch.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.Mutetime_ManageActivity.myhandel;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.CommonUtil;
import com.mobao.watch.util.ErroNumberChange;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;
import com.zxing.view.PickerView;
import com.zxing.view.PickerView.onSelectListener;

public class Mutetime_Data_SetActivity extends Activity {
	private ProgressDialog progDialog = null; // 圆形进度条
	private LinearLayout layout_startime;
	private LinearLayout layout_endtime;
	private TextView mututime_btn_save;
	private ImageButton btn_back;
	private PickerView numberpicker_h;
	private PickerView numberpicker_m;
	private RelativeLayout layout_repeat;
	private TextView startime;// 开始时间
	private TextView endtime;// 结束时间
	private TextView zaoshang;// 早上
	private TextView xiawu;// 下午
	private TextView shangxiawu;// 时间选择器上下午标示
	private String m = "30";// 分钟
	private String h = "12";// 小时
	private String nowstartime = "12:30";// 选择的开始时间
	private String nowendtime = "19:30";// 选择的结束时间
	private int whattext = 1;// 被选中的单选框
	private TextView mutetimename;//时间段名称
	
	final myhandel handel = new myhandel();
	String erro = "";
	int what = 0; // 1:yes -1:no
	int arg2 = 0; // 1:yes -1:no
	private String URL_SETSILENT = CommonUtil.baseUrl + "setsilent";// 保存静音时段

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.mutetime_data_set);
		progDialog = new ProgressDialog(Mutetime_Data_SetActivity.this);
		// 设置进入界面动画
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);
		// 设置url
		String ip = LoginActivity.dnspodIp;
		if (ip != null) {
			URL_SETSILENT = CommonUtil.baseUrl + "setsilent";// 保存静音时段
		}

		inintview();

	}

	private void inintview() {
		// TODO Auto-generated method stub
		//显示时间段名称
		mutetimename=(TextView) findViewById(R.id.mutetimename);
	    mutetimename.setText(getResources().getString(R.string.timebacket)+MbApplication.getGlobalData().getNow_silent_num());

		

		
		mututime_btn_save = (TextView) findViewById(R.id.mututime_btn_save);
		mututime_btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String start_h = nowstartime.split(":")[0];
				String start_m = nowstartime.split(":")[1];
				String end_h = nowendtime.split(":")[0];
				String end_m = nowendtime.split(":")[1];
				if (Integer.parseInt(start_h) < Integer.parseInt(end_h)) {

					if (Integer.parseInt(end_h) - Integer.parseInt(start_h) == 1
							&& Integer.parseInt(start_m)
									- Integer.parseInt(end_m) >= 50) {
						Toast.makeText(
								Mutetime_Data_SetActivity.this,
								Mutetime_Data_SetActivity.this.getResources()
										.getString(R.string.timeminno10), 3000)
								.show();
					} else {

						if (!CheckNetworkConnectionUtil
								.isNetworkConnected(Mutetime_Data_SetActivity.this)) {
							ToastUtil.show(
									Mutetime_Data_SetActivity.this,
									getResources().getString(
											R.string.networkunusable));

							return;
						}

						showDialog();

						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub

								setmutetimedata();
							}
						}).start();
					}

				} else {
					if (Integer.parseInt(start_h) == Integer.parseInt(end_h)) {

						if (Integer.parseInt(start_m) > Integer.parseInt(end_m)) {
							Toast.makeText(
									Mutetime_Data_SetActivity.this,
									Mutetime_Data_SetActivity.this
											.getResources().getString(
													R.string.endbeforestrar),
									3000).show();
							return;
						}

						if (Integer.parseInt(end_m) - Integer.parseInt(start_m) < 10) {
							Toast.makeText(
									Mutetime_Data_SetActivity.this,
									Mutetime_Data_SetActivity.this
											.getResources().getString(
													R.string.timeminno10), 3000)
									.show();
							return;
						}
						if (!CheckNetworkConnectionUtil
								.isNetworkConnected(Mutetime_Data_SetActivity.this)) {
							ToastUtil.show(
									Mutetime_Data_SetActivity.this,
									getResources().getString(
											R.string.networkunusable));

							return;
						}
						showDialog();

						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub

								setmutetimedata();
							}
						}).start();

					} else {

						Toast.makeText(
								Mutetime_Data_SetActivity.this,
								Mutetime_Data_SetActivity.this.getResources()
										.getString(R.string.endbeforestrar),
								3000).show();
					}
				}

			}
		});
		btn_back = (ImageButton) findViewById(R.id.mute_btn_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		zaoshang = (TextView) findViewById(R.id.text_mutetime_set_zaoshang);
		xiawu = (TextView) findViewById(R.id.text_mutetime_set_xiawu);
		shangxiawu = (TextView) findViewById(R.id.text_mutetime_set_shangxiawu);
		startime = (TextView) findViewById(R.id.text_mutetime_set_starttime);
		endtime = (TextView) findViewById(R.id.text_mutetime_set_endtime);

		layout_startime = (LinearLayout) findViewById(R.id.layout_startime);
		layout_startime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				whattext = 1;
				startime.setTextColor(0xffff4f03);
				zaoshang.setTextColor(0xffff4f03);
				endtime.setTextColor(0xff000000);
				xiawu.setTextColor(0xff000000);

				/*
				 * int max_h=Integer.parseInt(nowendtime.split(":")[0]);
				 * 
				 * for(int i=06;i<=18;i++){ if(i<10){ ht.add("0" + i); }else{
				 * ht.add("" + i); } } for(int i=0;i<=59;i++){ if(i<10){
				 * mt.add("0" + i); }else{ mt.add("" + i); } }
				 * numberpicker_h.setData(ht); numberpicker_m.setData(mt);
				 */

			}
		});
		layout_endtime = (LinearLayout) findViewById(R.id.layout_endtime);
		layout_endtime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				whattext = 2;
				startime.setTextColor(0xff000000);
				zaoshang.setTextColor(0xff000000);
				endtime.setTextColor(0xffff4f03);
				xiawu.setTextColor(0xffff4f03);

				/*
				 * List<String> ht = new ArrayList<String>(); List<String> mt =
				 * new ArrayList<String>(); for(int i=00;i<=05;i++){ if(i<10){
				 * ht.add("0" + i); }else{ ht.add("" + i); } }
				 * 
				 * for(int i=19;i<=23;i++){
				 * 
				 * ht.add("" + i); }
				 * 
				 * 
				 * 
				 * 
				 * for(int i=0;i<=59;i++){ if(i<10){ mt.add("0" + i); }else{
				 * mt.add("" + i); } } numberpicker_h.setData(ht);
				 * numberpicker_m.setData(mt);
				 */
			}
		});
		layout_repeat = (RelativeLayout) findViewById(R.id.layout_repeat);
		layout_repeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Mutetime_Data_SetActivity.this,
						Mutetime_Data_Set_WeekeActivity.class);
				startActivity(i);
			}
		});

		// 设置小时的NumberPicker
		numberpicker_h = (PickerView) findViewById(R.id.numberpicker_h);
		// 设置分钟的NumberPicker
		numberpicker_m = (PickerView) findViewById(R.id.numberpicker_m);
		List<String> ht = new ArrayList<String>();
		List<String> mt = new ArrayList<String>();
		for (int i = 00; i <= 23; i++) {
			if (i < 10) {
				ht.add("0" + i);
			} else {
				ht.add("" + i);
			}
		}
		for (int i = 0; i <= 59; i++) {
			if (i < 10) {
				mt.add("0" + i);
			} else {
				mt.add("" + i);
			}
		}
		numberpicker_h.setData(ht);
		numberpicker_m.setData(mt);

		numberpicker_h.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				// TODO Auto-generated method stub
				h = text;
				System.out.println("h" + h);
				if (Integer.parseInt(h) >= 6 && Integer.parseInt(h) <= 18) {
					shangxiawu.setText(Mutetime_Data_SetActivity.this
							.getResources().getString(R.string.day));

					if (whattext == 1) {
						zaoshang.setText(Mutetime_Data_SetActivity.this
								.getResources().getString(R.string.day));
					}
					if (whattext == 2) {
						xiawu.setText(Mutetime_Data_SetActivity.this
								.getResources().getString(R.string.day));
					}
				} else {
					shangxiawu.setText(Mutetime_Data_SetActivity.this
							.getResources().getString(R.string.night));
					if (whattext == 1) {
						zaoshang.setText(Mutetime_Data_SetActivity.this
								.getResources().getString(R.string.night));
					}
					if (whattext == 2) {
						xiawu.setText(Mutetime_Data_SetActivity.this
								.getResources().getString(R.string.night));
					}

				}

				if (whattext == 1) {
					nowstartime = h + ":" + m;
					startime.setText(nowstartime);

				}
				if (whattext == 2) {
					nowendtime = h + ":" + m;
					endtime.setText(nowendtime);

				}

			}
		});

		numberpicker_m.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				// TODO Auto-generated method stub
				m = text;
				System.out.println("m" + m);
				if (whattext == 1) {
					nowstartime = h + ":" + m;
					startime.setText(nowstartime);

				}
				if (whattext == 2) {
					nowendtime = h + ":" + m;
					endtime.setText(nowendtime);

				}
			}
		});

	}

	private void setmutetimedata() {
		// TODO 保存静音时段
		HttpPost request = new HttpPost(URL_SETSILENT);
		// 先封装一个 JSON 象
		JSONObject param = new JSONObject();

		try {
			param.put("imei", MbApplication.getGlobalData().getNowBaby().getBabyimei());
			param.put("num", MbApplication.getGlobalData().getNow_silent_num());
			param.put("weekday", MbApplication.getGlobalData().getWeeks());
			param.put("starttime", nowstartime);
			param.put("endtime", nowendtime);
			System.out.println(param.toString());
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {
				what = 1;
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				// 判断登陆是否成功
				int status = result.getInt("status");
				if (status == 200) {
					// 成功后摇干啥这里搞
					arg2 = 1;
					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 1000;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

				} else {
					// 失败后摇干啥这里搞
					arg2 = -1;
					erro = result.getString("msg");
					// 发送广播
					Message msg = Message.obtain();
					msg.what = what;
					msg.arg1 = 1000;
					msg.arg2 = arg2;
					handel.sendMessage(msg);

					/*
					 * Toast.makeText(context, "获取步数失败!原因:" +
					 * result.getString("msg"), Toast.LENGTH_LONG).show();
					 */
				}

			} else {
				what = -1;
				// 发送广播
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = 1000;
				msg.arg2 = arg2;
				handel.sendMessage(msg);
				/*
				 * Toast.makeText(context, "糟糕！服务器大姨妈来了，请见谅！",
				 * Toast.LENGTH_LONG) .show();
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

			if (lj == 1) {
				if (key == 1000 && arg2 == 1) {

					dismissDialog();
					mututime_btn_save.setClickable(true);
					Toast.makeText(
							Mutetime_Data_SetActivity.this,
							Mutetime_Data_SetActivity.this.getResources()
									.getString(R.string.setmutesuccess), 3000)
							.show();
					Intent i = new Intent(Mutetime_Data_SetActivity.this,
							Mutetime_ManageActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

				}
				if (key == 1000 && arg2 == -1) {

					dismissDialog();
					mututime_btn_save.setClickable(true);
					String erromsg = "";
					ErroNumberChange change=new ErroNumberChange(Mutetime_Data_SetActivity.this);
					erromsg=change.chang(erro);
					Toast.makeText(
							Mutetime_Data_SetActivity.this,
							Mutetime_Data_SetActivity.this.getResources()
									.getString(R.string.setmutefail) + erromsg,
							3000).show();
					Intent i = new Intent(Mutetime_Data_SetActivity.this,
							Mutetime_ManageActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

				}

			} else {

				dismissDialog();
				mututime_btn_save.setClickable(true);
				Toast.makeText(
						Mutetime_Data_SetActivity.this,
						Mutetime_Data_SetActivity.this.getResources()
								.getString(R.string.serverfail), 3000).show();

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
		progDialog.setMessage(Mutetime_Data_SetActivity.this.getResources()
				.getString(R.string.pleasewait));
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
