package com.mobao.watch.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLongClickListener;
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
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mb.zjwb1.R;
import com.mobao.watch.bean.SafetyAreaInfo;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.AMapUtil;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 修改安全区域Activity
 */
public class AlertSafetyAreaActivity extends Activity implements
		LocationSource, AMapLocationListener, InfoWindowAdapter,
		OnGeocodeSearchListener, OnMapLongClickListener,
		OnInfoWindowClickListener {

	// 返回箭头
	private RelativeLayout rel_backto_safety_area;
	private ImageButton btn_backto_safety_area;
	// 地图必要类
	private AMap aMap;
	private MapView mapView;
	private LatLng latlng = new LatLng(23.135007, 113.328819);// 假设安全区域的位置点
	private int index;// 列表下标

	private int id;// 列表id
	private SafetyAreaInfo info = new SafetyAreaInfo();// 安全区域信息

	private String location_name;// 安全位置名称
	private String location_address;// 安全位置地址
	private int safetyRadius = 300;// 安全区域半径
	private EditText safety_location_name, safety_location_address;// 安全位置名称,安全位置地址
																	// 编辑框
	private Circle circle;// 安全区域
	private int showArea = 16;// 显示范围
	private UiSettings mUiSettings;// 地图ui设置类
	private TextView text_safety_range;// infoWindow文本框
	private String String_text_safety_range;// infoWindow文本框内容
	private Marker marker;// 安全区域地点
	private TextView complete_txt;// 完成修改进行保存
	private RelativeLayout rel_complete;

	private GeocodeSearch geocoderSearch;
	private boolean changeLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.alter_safety_area_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		// 获取位置和名称等信息
		getInfo();
		// 初始化地图
		initMap();
		// 初始化组件
		initView();
		// 添加监听事件
		setOnClickListener();
		// 注册修改安全区域半径监听广播
		IntentFilter filter = new IntentFilter(
				SelectSafetyRangePopupWindowActivity.CHANGE_SAFETY_RADIUS_ACTION);
		registerReceiver(broadcastReceiver, filter);
		if (!CheckNetworkConnectionUtil.isNetworkConnected(this)) {
			ToastUtil.show(this,
					getResources().getString(R.string.networkunusable));
			return;
		}

	}

	private void getInfo() {
		// 获取安全区域信息
		index = this.getIntent().getIntExtra("index", 0);
		id = this.getIntent().getIntExtra("id", 1);
		info = SafetyAreaActivity.safety_area_list.get(index);
		latlng = new LatLng(Double.valueOf(info.getLat()), Double.valueOf(info
				.getLon()));
		// 获取位置点名称
		location_name = info.getSafety_name();
		// 获取位置点地址
		location_address = info.getSafety_address();
		// 获取安全区域半径
		safetyRadius = Integer.valueOf(info.getSafety_range());
		String_text_safety_range = safetyRadius
				+ getResources().getString(R.string.saferadiu);
	}

	private void setOnClickListener() {
		rel_backto_safety_area.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 返回上一个Activity
				Intent intent = new Intent();
				intent.setClass(AlertSafetyAreaActivity.this,
						SafetyAreaActivity.class);
				startActivity(intent);
				AlertSafetyAreaActivity.this.finish();
			}
		});
		// btn_backto_safety_area.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // 返回上一个Activity
		// Intent intent = new Intent();
		// intent.setClass(AlertSafetyAreaActivity.this,
		// SafetyAreaActivity.class);
		// startActivity(intent);
		// AlertSafetyAreaActivity.this.finish();
		// }
		// });
		// 完成保存
		rel_complete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String new_location_name = safety_location_name.getText()
						.toString();
				String new_location_address = safety_location_address.getText()
						.toString();
				String new_safety_range = safetyRadius + "";

				final SafetyAreaInfo new_info = new SafetyAreaInfo(
						info.getId(), new_location_name, new_location_address,
						null, null, new_safety_range);
				if(changeLocation){
					new_info.setLat(latlng.latitude+"");
					new_info.setLon(latlng.longitude+"");
				}
				new Thread() {
					public void run() {
						final boolean result = BabyLocateServer
								.alertSafetyArea(new_info,changeLocation);
						if (result == true) {
							runOnUiThread(new Runnable() {
								public void run() {
									SafetyAreaActivity.adapter
											.notifyDataSetChanged();
									Intent intent = new Intent();
									intent.setClass(
											AlertSafetyAreaActivity.this,
											SafetyAreaActivity.class);
									Toast.makeText(
											AlertSafetyAreaActivity.this,
											getString(R.string.alertsuccess),
											3000).show();
									startActivity(intent);
									finish();
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(
											AlertSafetyAreaActivity.this,
											getString(R.string.alerterror),
											3000).show();
								}
							});
						}
					}
				}.start();
			}
		});
		complete_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String new_location_name = safety_location_name.getText()
						.toString();
				String new_location_address = safety_location_address.getText()
						.toString();
				String new_safety_range = safetyRadius + "";

				final SafetyAreaInfo new_info = new SafetyAreaInfo(
						info.getId(), new_location_name, new_location_address,
						null, null, new_safety_range);
				if(changeLocation){
					new_info.setLat(latlng.latitude+"");
					new_info.setLon(latlng.longitude+"");
				}
				new Thread() {
					public void run() {
						final boolean result = BabyLocateServer
								.alertSafetyArea(new_info,changeLocation);
						if (result == true) {
							runOnUiThread(new Runnable() {
								public void run() {
									SafetyAreaActivity.adapter
											.notifyDataSetChanged();
									Intent intent = new Intent();
									intent.setClass(
											AlertSafetyAreaActivity.this,
											SafetyAreaActivity.class);
									Toast.makeText(
											AlertSafetyAreaActivity.this,
											R.string.alertsuccess, 3000).show();
									startActivity(intent);
									finish();
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(
											AlertSafetyAreaActivity.this,
											R.string.alerterror, 3000).show();
								}
							});
						}
					}
				}.start();
			}
		});
	}

	private void initView() {
		rel_backto_safety_area = (RelativeLayout) findViewById(R.id.rel_backto_safety_area);
		btn_backto_safety_area = (ImageButton) findViewById(R.id.btn_backto_safety_area);
		safety_location_name = (EditText) findViewById(R.id.etext_safety_location);
		safety_location_address = (EditText) findViewById(R.id.etext_safety_address);
		safety_location_name.setText(location_name);
		safety_location_address.setText(location_address);
		complete_txt = (TextView) findViewById(R.id.complete_txt);
		rel_complete = (RelativeLayout) findViewById(R.id.rel_complete);
	}

	private void initMap() {
		aMap = null;
		aMap = mapView.getMap();
		setUpMap();
	}

	private void setUpMap() {
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		UiSettings mUiSettings = aMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);
		mUiSettings.setScaleControlsEnabled(true);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		aMap.setOnMapLongClickListener(this);// 对amap添加长按地图事件监听器

		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, showArea));
		// 绘制圆形区域
		circle = aMap.addCircle(new CircleOptions().center(latlng)
				.radius(safetyRadius).strokeColor(Color.argb(50, 49, 152, 253))
				.fillColor(Color.argb(50, 186, 218, 249)).strokeWidth(2));
		marker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(getString(R.string.safearea))
				.snippet("")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.cur_safety_location))
				.draggable(true));
		marker.setAnchor(0.5f, 0.5f);
		marker.showInfoWindow();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public View getInfoContents(Marker marker) {
		View infoContent = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);
		render(marker, infoContent);
		return infoContent;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// 自定义infoWindow
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);
		text_safety_range = (TextView) infoWindow
				.findViewById(R.id.text_safety_range);
		text_safety_range.setText(String_text_safety_range);
		// render(marker, infoWindow);
		return infoWindow;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		ImageView imageView = (ImageView) view
				.findViewById(R.id.range_select_ico);
		String title = marker.getTitle();
		String snippet = marker.getSnippet();
		TextView titleUi = ((TextView) view
				.findViewById(R.id.text_safety_range));
		ImageView imageView2 = (ImageView) view.findViewById(R.id.drow_arrows);

	}

	@Override
	public void onInfoWindowClick(Marker mark) {
		// 弹出选择框
		startActivity(new Intent(AlertSafetyAreaActivity.this,
				SelectSafetyRangePopupWindowActivity.class));
	}

	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			safetyRadius = intent.getExtras().getInt("safetyRadius");
			showArea = intent.getExtras().getInt("showArea");
			String_text_safety_range = safetyRadius
					+ getResources().getString(R.string.saferadiu);
			text_safety_range.setText(String_text_safety_range);
			aMap.clear();
			initMap();
		}
	};

	@Override
	public void onMapLongClick(LatLng point) {
		LatLonPoint latlonPoint = new LatLonPoint(point.latitude,
				point.longitude);
		getAddress(latlonPoint);
		latlng = point;
		changeLocation = true;
		aMap.clear();
		addMarker(point);
	}

	// 添加安全区域
	private void addMarker(LatLng latlng) {
		// 绘制圆形区域
		circle = aMap.addCircle(new CircleOptions().center(latlng)
				.radius(safetyRadius).strokeColor(Color.argb(50, 49, 152, 253))
				.fillColor(Color.argb(50, 186, 218, 249)).strokeWidth(2));
		marker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(getString(R.string.safearea))
				.snippet("")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.cur_safety_location))
				.draggable(true));
		marker.setAnchor(0.5f, 0.5f);
		marker.showInfoWindow();
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
			} else {
				ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
						.getString(R.string.gaode_nodata));
			}

		} else if (rCode == 27) {
			ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
					.getString(R.string.gaode_networkproblem));
		} else if (rCode == 32) {
			ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
					.getString(R.string.gaode_keyunusable));
		} else {
			ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
					.getString(R.string.gaode_unknow) + rCode);
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getFormatAddress() + getString(R.string.gaode_near);
				// aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				// AMapUtil.convertToLatLng(latLonPoint), 15));
				safety_location_address.setText(addressName);
			} else {
				ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
						.getString(R.string.gaode_nodata));
			}

		} else if (rCode == 27) {
			ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
					.getString(R.string.gaode_networkproblem));
		} else if (rCode == 32) {
			ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
					.getString(R.string.gaode_keyunusable));
		} else {
			ToastUtil.show(AlertSafetyAreaActivity.this, getResources()
					.getString(R.string.gaode_unknow) + rCode);
		}
	}
}
