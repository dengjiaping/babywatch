package com.mobao.watch.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mb.zjwb1.R;
import com.mobao.watch.adapter.AddressListAdapter;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.LoactionHistoryInfo;
import com.mobao.watch.bean.LocateInfo;
import com.mobao.watch.datespinner.JudgeDate;
import com.mobao.watch.datespinner.ScreenInfo;
import com.mobao.watch.datespinner.WheelMain;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.AMapUtil;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.DateTimePickDialogUtil;
import com.mobao.watch.util.DialogUtil;
import com.mobao.watch.util.HistoryDrambleUtil;
import com.mobao.watch.util.SetTimePickerDialog;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 Baby轨迹Activity
 */
public class LocationHistoryActivity extends Activity implements
		LocationSource, AMapLocationListener, OnGeocodeSearchListener {
	// 组件
	private TextView text_baby_address;// baby目前的位置

	private TextView text_baby_name;
	private TextView text_baby_time;
	private RelativeLayout rel_backtoloact;
	private TextView selectBabyText;// 选择Baby
	private ImageView image_baby_head;// 中间位置宝贝头像
	// 时间下拉选择框
	private RelativeLayout spinner;
	private Dialog dialog;
	WheelMain wheelMain;
	TextView txttime;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	// 当前时间
	private String date;

	// 轨迹listView
	private ListView listView;
	public static AddressListAdapter historyadapter;// 轨迹Adapter
	private ArrayList<LatLng> location_latlng = new ArrayList<LatLng>();// 地点坐标
	private ArrayList<LoactionHistoryInfo> location_info = new ArrayList<LoactionHistoryInfo>();

	private PopupWindow window; // popupWindow
	private ListView lv_group; // popupWindow子view
	private List<Baby> babyGroups;// 宝贝列表
	private RelativeLayout rel; // 顶部操作栏
	private RelativeLayout rel_center;// 中间操作栏
	// 进入安全区域
	private RelativeLayout rel_btn_set;
	private ImageButton btn_set;

	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private LatLng latlng = null;// 假设孩子的位置点
	private LatLonPoint latLonPoint;// 安全区域坐标
	private Circle circle;// 孩子位置包围圈
	private UiSettings mUiSettings;// 地图ui设置类
	private GroupAdapter groupAdapter;// 顶部选择框adapter
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;

	public static String now_babyimei = null;// 当前宝贝id
	private Baby now_baby = null;
	private Drawable babyDrawable = null;// 当前宝贝头像
	private Bitmap babyBitmap = null;// 当前宝贝头像
	private LatLng first_latlng = null;// 当天最后位置点
	// 当前选择的时间
	private String selected_starttiem = null;
	private String selected_endtiem = null;
	private DialogUtil dialogUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		dialogUtil = new DialogUtil(this, getString(R.string.gettingdata));
		dialogUtil.showDialog();
		setContentView(R.layout.location_history_activity);

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		babyDrawable = LocationFragment.babyDrawable;
		babyBitmap = LocationFragment.bitmap;
		initView();

		// 获取当前日期
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		date = sDateFormat.format(new java.util.Date());
		selected_starttiem = date + " 00:00:00";
		selected_endtiem = date + " 23:59:59";

		// 获取全部宝贝
		babyGroups = LocationFragment.public_groups;
		now_baby = LocationFragment.now_baby;
		if (babyBitmap == null) {
			image_baby_head.setBackgroundResource(R.drawable.babymark);
		} else {
			image_baby_head.setBackgroundDrawable(babyDrawable);
			babyBitmap = LocationFragment.bitmap;
		}
		now_babyimei = now_baby.getBabyimei();
		text_baby_name.setText(now_baby.getBabyname());
		// 设置selectBabyText
		selectBabyText.setText(now_baby.getBabyname());
		babyGroups.remove(now_baby);
		// 获取数据
		getLocationInfo(selected_starttiem, selected_endtiem);
		// 注册监听广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(AddressListAdapter.MOVE_TO_HISTORY_LOCATE_ACTION);
		filter.addAction("new_locate");
		filter.addAction(DateTimePickDialogUtil.SELECTION_TIME_ACTION);
		registerReceiver(broadcastReceiver, filter);

	}

	private void getLocationInfo(final String starttime, final String endtime) {
		if (!CheckNetworkConnectionUtil.isNetworkConnected(this)) {
			ToastUtil.show(this,
					getResources().getString(R.string.networkunusable));
			// 设置按钮监听事件
			setOnClickLinstener();
			return;
		}
		// 获取位置信息
		new Thread() {
			public void run() {
				final ArrayList<LocateInfo> locateInfoList = BabyLocateServer
						.getBabyHistoryInfo(now_babyimei, starttime, endtime);
				if (locateInfoList != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							// 获取当天最后一个位置点
							int len = 0;
							if (locateInfoList.size() < 1) {
								ToastUtil.show(
										LocationHistoryActivity.this,
										getResources().getString(
												R.string.nowdaynodata));
							} else {
								len = locateInfoList.size() - 1;
								LocateInfo endli = locateInfoList.get(len);
								double lat = Double.valueOf(endli.getLat());
								double lon = Double.valueOf(endli.getLon());
								latlng = new LatLng(lat, lon);
								latLonPoint = new LatLonPoint(lat, lon);
								first_latlng = latlng;
								text_baby_address.setText(endli.getAddress());
								String stime = "";
								int ihour = Integer.valueOf(endli.getHour());
								if (ihour >= 12) {
									ihour -= 12;
									// 判断是否需要再前面加0,不满10便加前面加0
									if (ihour >= 0 && ihour < 10) {
										stime = "0" + ihour;
									} else {
										stime = ihour + "";
									}
									stime = stime + ":" + endli.getMintue()
											+ " pm";
								} else {
									stime = ihour + ":" + endli.getMintue()
											+ " am";
								}
								text_baby_time.setText(stime);
								// 设置text_baby_address内容
								text_baby_address.setText(endli.getAddress());
								// getAddress(latLonPoint);
								for (int i = 0; i < len; i++) {
									LocateInfo li = locateInfoList.get(i);
									double lat_ = Double.valueOf(li.getLat());
									double lon_ = Double.valueOf(li.getLon());
									String address = li.getAddress();
									String saferegionname = li
											.getSaferegionname();
									String hour = li.getHour();
									String minute = li.getMintue();
									String time = hour + ":" + minute;
									LatLng ll = new LatLng(lat_, lon_);

									location_latlng.add(ll);
									LoactionHistoryInfo lhi = new LoactionHistoryInfo(
											time, saferegionname, address,
											hour, minute, lat_, lon_,
											li.getStaytime());
									location_info.add(lhi);

								}
							}
							// 初始化历史轨迹ListView
							initHistoryListView();
							// 初始化地图
							initMap();
							// 设置按钮监听事件
							setOnClickLinstener();
							dialogUtil.dismissDialog();

						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							ToastUtil.show(
									LocationHistoryActivity.this,
									getResources().getString(
											R.string.nowdaynodata));
							latLonPoint = null;
							latlng = null;
							location_latlng = new ArrayList<LatLng>();// 地点坐标
							location_info = new ArrayList<LoactionHistoryInfo>();
							text_baby_address.setText("");
							text_baby_time.setText("");
							if (aMap != null) {
								aMap.clear();
							}
							// 初始化历史轨迹ListView
							initHistoryListView();
							// 初始化地图
							initMap();
							// 设置按钮监听事件
							setOnClickLinstener();
							dialogUtil.dismissDialog();
						}
					});
				}
			}
		}.start();
		// 获取坐标
		// latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
	}

	private void initView() {
		text_baby_name = (TextView) findViewById(R.id.text_baby_name);
		rel_backtoloact = (RelativeLayout) findViewById(R.id.rel_backtoloaction);

		selectBabyText = (TextView) findViewById(R.id.select_baby_txt);
		rel = (RelativeLayout) findViewById(R.id.top_rel);
		rel_btn_set = (RelativeLayout) findViewById(R.id.rel_btn_set);
		btn_set = (ImageButton) findViewById(R.id.btn_save_set);
		text_baby_address = (TextView) findViewById(R.id.text_baby_address);
		image_baby_head = (ImageView) findViewById(R.id.image_baby_head);
		rel_center = (RelativeLayout) findViewById(R.id.rel_center);
		text_baby_time = (TextView) findViewById(R.id.text_baby_time);
		spinner = (RelativeLayout) findViewById(R.id.layout_spinnerdate);
		txttime = (TextView) findViewById(R.id.text_spinnerdate);

		Calendar calendar = Calendar.getInstance();
		txttime.setText(calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "");

		geocoderSearch = new GeocodeSearch(LocationHistoryActivity.this);
		geocoderSearch.setOnGeocodeSearchListener(LocationHistoryActivity.this);
		progDialog = new ProgressDialog(LocationHistoryActivity.this);
	}

	private void initHistoryListView() {
		listView = (ListView) findViewById(R.id.list_address);

		listView.setDividerHeight(0);
		historyadapter = new AddressListAdapter(this, location_info,true);
		listView.setAdapter(historyadapter);
		historyadapter.notifyDataSetChanged();

	}

	private void getLocationHistoryInfo() {
		// TODO Auto-generated method stub

	}

	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		} else {
			aMap.clear();
		}
		setUpMap();
	}

	private void setUpMap() {
		UiSettings mUiSettings = aMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);
		// 添加轨迹marker
		addHistoryMarker();
		if (latlng == null) {
			return;
		}
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
		// 绘制圆形区域
		circle = aMap.addCircle(new CircleOptions().center(latlng).radius(300)
				.strokeColor(Color.argb(50, 253, 143, 85))
				.fillColor(Color.argb(50, 253, 121, 53)).strokeWidth(2));
		if (babyBitmap == null) {
			Marker marker = aMap.addMarker(new MarkerOptions()
					.position(latlng)
					.draggable(true)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.babymark)));
			marker.setAnchor(0.5f, 0.5f);
			marker.showInfoWindow();
		} else {
			Marker marker = aMap.addMarker(new MarkerOptions().position(latlng)
					.draggable(true)
					.icon(BitmapDescriptorFactory.fromBitmap(babyBitmap)));

			marker.setAnchor(0.5f, 0.5f);
			marker.showInfoWindow();
		}
	}

	// 添加轨迹marker
	private void addHistoryMarker() {
		for (int i = 0; i < location_latlng.size(); i++) {
			LatLng lat = location_latlng.get(i);
			// Marker marker = aMap.addMarker(new MarkerOptions()
			// .position(lat)
			// .icon(BitmapDescriptorFactory
			// .fromResource(HistoryDrambleUtil
			// .getHistotyDramble(i))).draggable(true));
			Marker marker = aMap.addMarker(new MarkerOptions()
					.position(lat)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.history)).draggable(true));
			marker.setAnchor(0.5f, 0.5f);
			marker.showInfoWindow();
		}

	}

	private void setOnClickLinstener() {

		rel_backtoloact
				.setOnClickListener(new RelativeLayout.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						LocationHistoryActivity.this.finish();
					}
				});

		// selectBabyText.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // 初始化PopupWindow
		// initPopupWindow();
		// }
		// });

		rel_btn_set.setOnClickListener(new RelativeLayout.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 进入安全区域Activity
				Intent intent = new Intent(LocationHistoryActivity.this,
						SafetyAreaActivity.class);
				startActivity(intent);
				finish();
			}
		});

		btn_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 进入安全区域Activity
				Intent intent = new Intent(LocationHistoryActivity.this,
						SafetyAreaActivity.class);
				startActivity(intent);
			}
		});
		// 定位到最后一个位置
		rel_center.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (aMap == null || first_latlng == null) {
					ToastUtil.show(LocationHistoryActivity.this, getResources()
							.getString(R.string.nowdaynodata));
					return;
				}
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first_latlng,
						16));
			}
		});

		spinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						LocationHistoryActivity.this, "");
				dateTimePicKDialog.dateTimePicKDialog(txttime);
				// TODO Auto-generated method stub
				// String time = txttime.getText().toString();
				// final SetTimePickerDialog timepicker = new
				// SetTimePickerDialog(
				// LocationHistoryActivity.this,
				// R.style.chatfragment_call_dialog_style,
				// LocationHistoryActivity.this, time);
				// timepicker.setBtnOkonclik(new OnClickListener() {
				//
				// @Override
				// public void onClick(View arg0) {
				// // TODO Auto-generated method stub
				// txttime.setText(timepicker.getTime());
				// selected_starttiem = timepicker.getTime() + " 00:00:00";
				// selected_endtiem = timepicker.getTime() + " 23:59:59";
				// location_latlng = new ArrayList<LatLng>();// 地点坐标
				// location_info = new ArrayList<LoactionHistoryInfo>();
				// if (!CheckNetworkConnectionUtil
				// .isNetworkConnected(LocationHistoryActivity.this)) {
				// ToastUtil.show(
				// LocationHistoryActivity.this,
				// getResources().getString(
				// R.string.networkunusable));
				// return;
				// }
				// dialogUtil = new DialogUtil(
				// LocationHistoryActivity.this,
				// getString(R.string.gettingdata));
				// dialogUtil.showDialog();
				// aMap.clear();
				// getLocationInfo(selected_starttiem, selected_endtiem);
				// historyadapter.notifyDataSetChanged();
				// timepicker.dismiss();
				//
				// }
				// });
				//
				// timepicker.setBtnNoonclik(new OnClickListener() {
				//
				// @Override
				// public void onClick(View arg0) {
				// // TODO Auto-generated method stub
				// timepicker.dismiss();
				// }
				// });
				// timepicker.show();

				/*
				 * LayoutInflater inflater = LayoutInflater
				 * .from(LocationHistoryActivity.this); final View
				 * timepickerview = inflater.inflate( R.layout.timepicker,
				 * null); ScreenInfo screenInfo = new ScreenInfo(
				 * LocationHistoryActivity.this); wheelMain = new
				 * WheelMain(timepickerview); wheelMain.screenheight =
				 * screenInfo.getHeight(); String time =
				 * txttime.getText().toString(); Calendar calendar =
				 * Calendar.getInstance(); if (JudgeDate.isDate(time,
				 * "yyyy-MM-dd")) { try {
				 * calendar.setTime(dateFormat.parse(time)); } catch
				 * (ParseException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } } int year =
				 * calendar.get(Calendar.YEAR); int month =
				 * calendar.get(Calendar.MONTH); int day =
				 * calendar.get(Calendar.DAY_OF_MONTH);
				 * wheelMain.initDateTimePicker(year, month, day); new
				 * AlertDialog.Builder(LocationHistoryActivity.this)
				 * .setTitle("选择时间") .setView(timepickerview)
				 * .setPositiveButton("确定", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { txttime.setText(wheelMain.getTime());
				 * selected_starttiem = wheelMain .getTime() + " 00:00:00";
				 * selected_endtiem = wheelMain.getTime() + " 23:59:59";
				 * location_latlng = new ArrayList<LatLng>();// 地点坐标
				 * location_info = new ArrayList<LoactionHistoryInfo>();
				 * aMap.clear(); getLocationInfo(selected_starttiem,
				 * selected_endtiem); historyadapter.notifyDataSetChanged(); }
				 * }) .setNegativeButton("取消", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { } }).show();
				 */
			}
		});
	}

	/**
	 * 初始化PopupWindow
	 */
	protected void initPopupWindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.group_list, null);

		lv_group = (ListView) view.findViewById(R.id.lvGroup);
		// 加载数据

		groupAdapter = new GroupAdapter(this, babyGroups);
		lv_group.setAdapter(groupAdapter);

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		window = new PopupWindow(view, 300,
				WindowManager.LayoutParams.WRAP_CONTENT);

		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);

		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.AnimationFade);
		// 在底部显示
		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int y = rel.getHeight() + statusBarHeight;
		window.showAtLocation(mapView, Gravity.TOP, 0, y);

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(LocationHistoryActivity.this)) {
					ToastUtil.show(LocationHistoryActivity.this, getResources()
							.getString(R.string.networkunusable));
					window.dismiss();
					return;
				}
				dialogUtil = new DialogUtil(LocationHistoryActivity.this,
						getString(R.string.gettingdata));
				dialogUtil.showDialog();
				Baby cur_baby = babyGroups.get(position);
				String lasttext = (String) selectBabyText.getText();
				selectBabyText.setText(babyGroups.get(position).getBabyname());
				now_babyimei = babyGroups.get(position).getBabyimei();
				LocationFragment.now_baby = cur_baby;
				LocationFragment.now_babyimei = now_babyimei;
				// 获取头像
				String str_photo = cur_baby.getPortrait();
				if (TextUtils.isEmpty(str_photo)) {
					image_baby_head.setBackgroundResource(R.drawable.babymark);
					babyBitmap = null;
				} else {
					int len = str_photo.length();
					String head_of_str_photp = str_photo.substring(len - 20,
							len);
					Log.i("AAA", "55head_of_str_photp = " + head_of_str_photp);
					babyBitmap = ChatUtil.getImageCache().getBitmap(
							head_of_str_photp);

					if (LocationFragment.bitmap == null) {
						byte[] bitmapArray;
						bitmapArray = Base64.decode(str_photo, Base64.DEFAULT);
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 2;
						babyBitmap = ChatUtil.toRoundBitmap(BitmapFactory
								.decodeByteArray(bitmapArray, 0,
										bitmapArray.length, options));

						// ChatUtil.getImageCache().putBitmap(head_of_str_photp,
						// babyBitmap);
					}

					LocationFragment.bitmap = babyBitmap;
					LocationFragment.babyDrawable = new BitmapDrawable(
							babyBitmap);
					image_baby_head
							.setBackgroundDrawable(LocationFragment.babyDrawable);
				}
				text_baby_name.setText(cur_baby.getBabyname());
				babyGroups.remove(position);
				babyGroups.add(now_baby);
				now_baby = cur_baby;
				groupAdapter.notifyDataSetChanged();
				// 获取数据
				getLocationInfo(selected_starttiem, selected_endtiem);
				if (window != null) {
					window.dismiss();
				}
			}
		});
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getResources().getString(R.string.positioning));
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

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {
		showDialog();
		GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		showDialog();
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));

				// String addressName = "经纬度值:" + address.getLatLonPoint()
				// + "\n位置描述:" + address.getFormatAddress();
				// ToastUtil.show(LocationHistoryActivity.this, addressName);
			} else {
				ToastUtil.show(LocationHistoryActivity.this, getResources()
						.getString(R.string.gaode_nodata));
			}

		} else if (rCode == 27) {
			ToastUtil.show(LocationHistoryActivity.this, getResources()
					.getString(R.string.gaode_networkproblem));
		} else if (rCode == 32) {
			ToastUtil.show(LocationHistoryActivity.this, getResources()
					.getString(R.string.gaode_keyunusable));
		} else {
			ToastUtil.show(LocationHistoryActivity.this, getResources()
					.getString(R.string.gaode_unknow) + rCode);
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getFormatAddress()
						+ getResources().getString(R.string.gaode_near);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(latLonPoint), 15));
				text_baby_address.setText(addressName);
			} else {
				ToastUtil.show(LocationHistoryActivity.this, getResources()
						.getString(R.string.gaode_nodata));
			}

		} else if (rCode == 27) {
			ToastUtil.show(LocationHistoryActivity.this, getResources()
					.getString(R.string.gaode_networkproblem));
		} else if (rCode == 32) {
			ToastUtil.show(LocationHistoryActivity.this, getResources()
					.getString(R.string.gaode_keyunusable));
		} else {
			ToastUtil.show(LocationHistoryActivity.this, getResources()
					.getString(R.string.gaode_unknow) + rCode);
		}
	}

	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					DateTimePickDialogUtil.SELECTION_TIME_ACTION)) {
				selected_starttiem = intent.getStringExtra("dateTime")
						+ " 00:00:00";
				selected_endtiem = intent.getStringExtra("dateTime")
						+ " 23:59:59";
				location_latlng = new ArrayList<LatLng>();// 地点坐标
				location_info = new ArrayList<LoactionHistoryInfo>();
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(LocationHistoryActivity.this)) {
					ToastUtil.show(LocationHistoryActivity.this, getResources()
							.getString(R.string.networkunusable));
					return;
				}
				dialogUtil = new DialogUtil(LocationHistoryActivity.this,
						getString(R.string.gettingdata));
				dialogUtil.showDialog();
				aMap.clear();
				getLocationInfo(selected_starttiem, selected_endtiem);
				historyadapter.notifyDataSetChanged();
			}
			if (intent.getAction().equals(
					AddressListAdapter.MOVE_TO_HISTORY_LOCATE_ACTION)) {
				double lat = intent.getExtras().getDouble("lat");
				double lon = intent.getExtras().getDouble("lon");
				LatLng history_latlng = new LatLng(lat, lon);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						history_latlng, 16));
			}
			if (intent.getAction().equals("new_locate")) {
				getLocationInfo(selected_starttiem, selected_endtiem);
			}
		}
	};
}
