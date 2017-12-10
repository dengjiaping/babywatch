package com.mobao.watch.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
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
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mb.zjwb1.R;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.LoactionHistoryInfo;
import com.mobao.watch.bean.SafetyArea;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.util.AMapUtil;
import com.mobao.watch.util.CharUtil;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

/**
 * @author 坤 添加安全区域Activity
 */
public class AddSafetyAreaActivity extends Activity implements LocationSource,
		AMapLocationListener, OnGeocodeSearchListener, OnMapLongClickListener,
		InfoWindowAdapter, OnInfoWindowClickListener {

	// 返回箭头
	private RelativeLayout rel_backto_safety_area;
	private ImageButton btn_backto_safety_area;
	private TextView complete_txt;// 完成修改进行保存
	// 编辑框
	private EditText safety_location_name, safety_location_address;// 安全位置名称,安全位置地址
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private UiSettings mUiSettings;// 地图ui设置类
	private TextView etext_safety_address;// 安全区域名称
	private boolean changeSafetyLocation = true;// 是否获取安全区域名称
	private Marker marker;// 安全区域地点
	private LatLonPoint latLonPoint;// 安全区域坐标
	private Circle circle;// 安全区域范围
	private int safetyRadius = 300;// 安全区域半径
	private int showArea = 16;// 显示范围
	private TextView text_safety_range;// infoWindow文本框
	private String String_text_safety_range = null;// infoWindow文本框内容
	private LatLng cur_point;// 当前选择的安全位置
	private Baby now_baby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		
		setContentView(R.layout.add_safety_area_activity);
		String_text_safety_range = getString(R.string.saferadiu300);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();
		initView();
		setOnClickListener();
		// 注册修改安全区域半径监听广播
		IntentFilter filter = new IntentFilter(
				SelectSafetyRangePopupWindowActivity.CHANGE_SAFETY_RADIUS_ACTION);
		registerReceiver(broadcastReceiver, filter);
		if(!CheckNetworkConnectionUtil.isNetworkConnected(this)){
			ToastUtil.show(this, getResources().getString(R.string.networkunusable));
			return;
		}
	}

	private void setOnClickListener() {
		rel_backto_safety_area.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 返回上一个Activity
				Intent intent = new Intent();
				intent.setClass(AddSafetyAreaActivity.this,
						SafetyAreaActivity.class);
				startActivity(intent);
				AddSafetyAreaActivity.this.finish();
			}
		});
		// 完成保存
		complete_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String location_name = safety_location_name.getText()
						.toString();
				String location_address = safety_location_address.getText()
						.toString();
				if (TextUtils.isEmpty(location_name)) {
					Toast.makeText(AddSafetyAreaActivity.this, getString(R.string.editsafeareaname),
							3000).show();
					return;
				}
				if(CharUtil.isChinese(location_name)&&location_name.length()>8){
					Toast.makeText(AddSafetyAreaActivity.this, getString(R.string.locationnametolong),
							3000).show();
					return;
				}
				if (TextUtils.isEmpty(location_address)) {
					Toast.makeText(AddSafetyAreaActivity.this,  getString(R.string.editsafeareaaddress),
							3000).show();
					return;
				}

				now_baby = LocationFragment.now_baby;
				final SafetyArea safetyArea = new SafetyArea(now_baby
						.getBabyimei(), location_name, location_address,
						safetyRadius + "", cur_point.latitude + "",
						cur_point.longitude + "");
				new Thread() {
					public void run() {
						final boolean result = BabyLocateServer
								.addSafetyArea(safetyArea);
						if (result == true) {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(AddSafetyAreaActivity.this,
											 getString(R.string.savesuccess), 3000).show();
									Intent intent = new Intent();
									intent.setClass(AddSafetyAreaActivity.this,
											SafetyAreaActivity.class);
									startActivity(intent);
									finish();
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(AddSafetyAreaActivity.this,
											 getString(R.string.savefail), 3000).show();
								}
							});
						}
					}
				}.start();
			}
		});
	}

	private void initView() {
		etext_safety_address = (TextView) findViewById(R.id.etext_safety_address);
		rel_backto_safety_area = (RelativeLayout) findViewById(R.id.rel_backto_safety_area);
		btn_backto_safety_area = (ImageButton) findViewById(R.id.btn_backto_safety_area);
		complete_txt = (TextView) findViewById(R.id.complete_txt);
		safety_location_name = (EditText) findViewById(R.id.etext_safety_location);
		safety_location_address = (EditText) findViewById(R.id.etext_safety_address);
	}

	// 添加安全区域
	private void addMarker(LatLng latlng) {
		// 绘制圆形区域
		circle = aMap.addCircle(new CircleOptions().center(latlng)
				.radius(safetyRadius).strokeColor(Color.argb(50, 49, 152, 253))
				.fillColor(Color.argb(50, 186, 218, 249)).strokeWidth(2));
		marker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title( getString(R.string.safearea))
				.snippet("")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.cur_safety_location))
				.draggable(true));
		marker.setAnchor(0.5f, 0.5f);
		marker.showInfoWindow();
	}

	private void getLocation() {
		Location myLocation = aMap.getMyLocation();
		double lat = myLocation.getLatitude();
		double lon = myLocation.getLongitude();
		LatLng latlng = new LatLng(lat, lon);
		cur_point = latlng;
		latLonPoint = new LatLonPoint(lat, lon);
		getAddress(latLonPoint);
		addMarker(latlng);
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		aMap = null;
		// 初始化地图
		aMap = mapView.getMap();
		setUpMap();
		mUiSettings = aMap.getUiSettings();
		mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
		mUiSettings.setZoomControlsEnabled(false);

	}

	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.cur_safety_location));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 180));// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(0);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setOnMapLongClickListener(this);// 对amap添加长按地图事件监听器
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		unregisterReceiver(broadcastReceiver);
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

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			if (changeSafetyLocation) {
				getLocation();
				changeSafetyLocation = false;
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getString(R.string.gettingaddress));
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
				// geoMarker.setPosition(AMapUtil.convertToLatLng(address
				// .getLatLonPoint()));
//				String addressName = "经纬度值:" + address.getLatLonPoint()
//						+ "\n位置描述:" + address.getFormatAddress();
//				ToastUtil.show(AddSafetyAreaActivity.this, addressName);
			} else {
				ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_nodata));
			}

		} else if (rCode == 27) {
			ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_networkproblem));
		} else if (rCode == 32) {
			ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_keyunusable));
		} else {
			ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_unknow) + rCode);
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
						.getFormatAddress() +  getString(R.string.gaode_near);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(latLonPoint), 15));
				etext_safety_address.setText(addressName);
			} else {
				ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_nodata));
			}

		} else if (rCode == 27) {
			ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_networkproblem));
		} else if (rCode == 32) {
			ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_keyunusable));
		} else {
			ToastUtil.show(AddSafetyAreaActivity.this, getResources().getString(R.string.gaode_unknow) + rCode);
		}
	}

	// 长按地图获取地点
	@Override
	public void onMapLongClick(LatLng point) {
		LatLonPoint latlonPoint = new LatLonPoint(point.latitude,
				point.longitude);
		getAddress(latlonPoint);
		cur_point = point;
		aMap.clear();
		addMarker(point);
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// 弹出选择框
		startActivity(new Intent(AddSafetyAreaActivity.this,
				SelectSafetyRangePopupWindowActivity.class));
	}

	@Override
	public View getInfoContents(Marker marker) {
		View infoContent = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);
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
		return infoWindow;
	}

	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			safetyRadius = intent.getExtras().getInt("safetyRadius");
			showArea = intent.getExtras().getInt("showArea");
			String_text_safety_range = safetyRadius +""+ getResources().getString(R.string.saferadiu);
			text_safety_range.setText(String_text_safety_range);
			aMap.clear();
			addMarker(cur_point);
			;
		}
	};

}
