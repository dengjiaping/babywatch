package com.mobao.watch.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
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
import com.mobao.watch.activity.BabyFragmentActivity.txListener;
import com.mobao.watch.activity.*;
import com.mobao.watch.adapter.GroupAdapter;
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.BabyNowLocationInfo;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.MqttConnection;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.AMapUtil;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CheckNetworkConnectionUtil;
import com.mobao.watch.util.DialogUtil;
import com.mobao.watch.util.ToastUtil;
import com.mobao.watch.util.UpLoadingAppLocationThread;
import com.mobao.watch.util.YunBaStart;

/**
 * @author 坤 定位Fragment
 */
public class LocationFragment extends Fragment implements LocationSource,
		AMapLocationListener {

	private AMap aMap;
	public static MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	public static RelativeLayout rel_select_baby;
	public static TextView selectBabyText;// 选择Baby
	private Context context;
	private PopupWindow window; // popupWindow
	private ListView lv_group; // popupWindow子view
	public static List<Baby> groups;// 宝贝列表
	public static GroupAdapter groupAdapter;
	private RelativeLayout rel; // 顶部操作栏
	// private ImageButton img_Locationbaby;// 宝贝头像显示层

	private RelativeLayout relat_bule;
	private ImageButton gotoRoute;// 查看轨迹按钮
	private RelativeLayout relat_gotoRoute;
	private ImageView image;// 三角形图标
	// private ImageButton locationBaby;// 定位宝宝位置按钮
	// private ImageButton locationMySelf;// 定位手机位置按钮
	private LatLng latlng = null;// 假设孩子的位置点
	MyLocationStyle myLocationStyle;
	private Circle circle;// 孩子位置包围圈
	private int radius = 0;
	private UiSettings mUiSettings;// 地图ui设置类
	public static List<Baby> public_groups;// 宝贝列表
	private ProgressDialog progDialog = null; // 圆形进度条
	public static String now_babyimei = null;// 当前宝贝id
	public static Baby now_baby = null;

	private LinearLayout lin_btn_location;// 定位按钮
	private ImageView img_btn_state;// 在线状态显示按钮
	private TextView text_GPS = null;
	// 当前baby头像
	public static Drawable babyDrawable;
	public static Bitmap bitmap;
	public static String portrait = null;
	private GeocodeSearch geocoderSearch;
	private String address = null;

	private RelativeLayout rel_btn_goto_log;
	private ImageButton but_goto_log;

	private TextView txt_linestate;
	private static double lon = 0;
	private static double lat = 0;
	// 一分钟内不可获取
	private static boolean canClick = true;

	// 宝贝imei数组
	public static String[] imeiTopic = null;
	public DialogUtil dialogUtil = null;

	// 画出左侧菜单的按钮
	private RelativeLayout layout_open_leftmenu;
	private ImageButton btn_open_leftmenu;
	private Activity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		View view = inflater.inflate(R.layout.location_fragment, container,
				false);
		context = inflater.getContext();
		activity=getActivity();
		mapView = (MapView) view.findViewById(R.id.map);
		progDialog = new ProgressDialog(context);
		selectBabyText = (TextView) view.findViewById(R.id.select_baby_txt);
		rel_select_baby = (RelativeLayout) view
				.findViewById(R.id.rel_select_baby);
		rel = (RelativeLayout) view.findViewById(R.id.top_rel);
		relat_gotoRoute = (RelativeLayout) view
				.findViewById(R.id.rel_btn_goto_route);
		txt_linestate = (TextView) view.findViewById(R.id.txt_linestate);
		image = (ImageView) view.findViewById(R.id.image);
		text_GPS = (TextView) view.findViewById(R.id.text_GPS);
		lin_btn_location = (LinearLayout) view
				.findViewById(R.id.lin_btn_location);
		// locationBaby = (ImageButton)
		// view.findViewById(R.id.img_Locationbaby);
		// img_rewardstart = (ImageButton)
		// view.findViewById(R.id.img_rewardstart);
		// locationMySelf = (ImageButton) view
		// .findViewById(R.id.img_LoactionMyself);
		gotoRoute = (ImageButton) view.findViewById(R.id.but_goto_route);
		// img_Locationbaby = (ImageButton) view
		// .findViewById(R.id.img_Locationbaby);
		/*
		 * rel_btn_goto_log = (RelativeLayout) view
		 * .findViewById(R.id.rel_btn_goto_log); but_goto_log = (ImageButton)
		 * view.findViewById(R.id.but_goto_log);
		 */

		layout_open_leftmenu = (RelativeLayout) view
				.findViewById(R.id.layout_open_leftmenu);
		btn_open_leftmenu = (ImageButton) view
				.findViewById(R.id.btn_open_leftmenu);
		layout_open_leftmenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BabyFragmentActivity.leftmenu.openDrawer(Gravity.START);
			}
		});
		btn_open_leftmenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BabyFragmentActivity.leftmenu.openDrawer(Gravity.START);
			}
		});

		img_btn_state = (ImageView) view.findViewById(R.id.img_btn_state);
		handler.sendEmptyMessageDelayed(2, 1000);

		if (!CheckNetworkConnectionUtil.isNetworkConnected(getActivity())) {
			ToastUtil.show(getActivity(),
					getResources().getString(R.string.networkunusable));
			dismissDialog();
			return view;
		}
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		showDialog();
		// 获取宝贝列表
		// 给按钮设置监听事件
		setImageButtonClickListener();
		getBabyList();

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("baby_info_change");
		myIntentFilter.addAction("CHANGE_IMAGE_ACTION");
		myIntentFilter.addAction("CHANGE_BABY_ACTION");
		myIntentFilter.addAction(MqttConnection.ACTION_NOW_LOCATE);
		myIntentFilter.addAction("LOCATE_BABY_ACTION");
		myIntentFilter.addAction(MqttConnection.ACTION_BABY_IS_ONLINE);
		myIntentFilter.addAction("opendialog");
		// 注册广播
		getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		// 切换到地图界面时显示底部按钮栏
		if (isVisibleToUser) {
			BabyFragmentActivity.lin_bottom.setVisibility(View.VISIBLE);
		} else {
			BabyFragmentActivity.lin_bottom.setVisibility(View.GONE);
		}

		super.setUserVisibleHint(isVisibleToUser);

	}

	private void getBabyNowLocate() {
		
		// showDialog();
		if (aMap == null) {
			// 初始化地图
			aMap = mapView.getMap();
			UiSettings mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(true);
			mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
			mUiSettings.setMyLocationButtonEnabled(false);
			setUpMap();
			aMap.getUiSettings().setMyLocationButtonEnabled(false);
		}
		if (!CheckNetworkConnectionUtil.isNetworkConnected(getActivity())) {
			dismissDialog();
			ToastUtil.show(activity,
					getResources().getString(R.string.networkunusable));
			return;
		}
		
		new Thread() {
			public void run() {
				System.out.println("请求定位的imei：" + now_babyimei);
				final String result = BabyLocateServer
						.getBabyImmediate(now_babyimei,1,context);
				if (result != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							initPopupWindow();
							if (result.equals("操作成功")) {
								dialogUtil.dismissDialog();
								dialogUtil = new DialogUtil(activity,
										getString(R.string.babypositioning));
								dialogUtil.showDialog();
								return;
							}
							if (result.equals("定位请求过于频繁")) {
								dialogUtil.dismissDialog();
								ToastUtil
										.show(activity,
												getString(R.string.locaterequesttoooften));
								return;
							}
							if (result.equals("请求定位失败")) {
								dialogUtil.dismissDialog();
								ToastUtil
										.show(activity,
												getString(R.string.requestpositioningfailure));
								return;
							}
							if (result.equals("宝贝不在线")) {
								dialogUtil.dismissDialog();
								img_btn_state
										.setImageResource(R.drawable.location_btn_red);
								ToastUtil.show(activity,
										getString(R.string.watchoffline));
								if (aMap != null) {
									aMap.clear();
								}
								return;
							}
							if (result.equals("请求腕表的操作超时")) {
								dialogUtil.dismissDialog();
								ToastUtil.show(activity,
										getString(R.string.requesttimedout));
								return;
							}
							initAMap();
						}
					});
				} else {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// dismissDialog();
							if(BabyFragmentActivity.progDialog.isShowing()){
								BabyFragmentActivity.progDialog.dismiss();
							}
							dialogUtil.dismissDialog();
							ToastUtil.show(activity,
									getString(R.string.watchoffline));
							// Toast.makeText(
							// getActivity(),
							// getResources().getString(
							// R.string.babynotnoline), 3000)
							// .show();
							img_btn_state
									.setBackgroundResource(R.drawable.location_btn_red);
						}
					});
				}
			}
		}.start();
		initPopupWindow();

	}

	private void getBabyList() {
		groups = new ArrayList<Baby>();
		new Thread() {
			public void run() {
				final ArrayList<Baby> babyList = BabyLocateServer
						.getBabyList(MbApplication.getGlobalData().getNowuser()
								.getUserid());
				if (babyList != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (babyList.size() == 0) {
								startActivity(new Intent(activity,
										ActiveWatchActivity.class));
								return;
							}
							// 获取宝贝Imei列表
							imeiTopic = new String[babyList.size()];
							for (int i = 0; i < babyList.size(); i++) {
								imeiTopic[i] = babyList.get(i).getBabyimei();
								Log.i("keis", "imeiTopic[" + i + "]="
										+ imeiTopic[i]);
							}
							// 绑定push频道
							YunBaStart.BindTopic(activity, imeiTopic);

							groups = babyList;
							public_groups = groups;
							selectBabyText.setText(groups.get(0).getBabyname());
							now_baby = groups.get(0);
							now_babyimei = now_baby.getBabyimei();
							// 将当前宝贝的imei存到全局变量里
							MbApplication.getGlobalData().getNowuser()
									.setImei(now_babyimei);
							// 将当前宝贝存到全局变量里
							MbApplication.getGlobalData().setNowBaby(now_baby);
							// 获取宝贝头像
							portrait = now_baby.getPortrait();
							// 将当前宝贝头像存到全局变量里
							MbApplication.getGlobalData().setBabyhead(portrait);
							if (TextUtils.isEmpty(portrait)) {
								// img_Locationbaby
								// .setBackgroundResource(R.drawable.babymark);
								bitmap = null;
							} else {
								byte[] bitmapArray;
								bitmapArray = Base64.decode(portrait,
										Base64.DEFAULT);
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inSampleSize = 2;
								bitmap = ChatUtil.toRoundBitmap(BitmapFactory
										.decodeByteArray(bitmapArray, 0,
												bitmapArray.length, options));

								// }

								babyDrawable = new BitmapDrawable(bitmap);
								// img_Locationbaby
								// .setBackgroundDrawable(babyDrawable);
							}
							groups.remove(0);
							if (aMap == null) {
								// 初始化地图
								aMap = mapView.getMap();
								UiSettings mUiSettings = aMap.getUiSettings();
								mUiSettings.setZoomControlsEnabled(true);
								mUiSettings
										.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
								mUiSettings.setMyLocationButtonEnabled(false);
								setUpMap();
								aMap.getUiSettings()
										.setMyLocationButtonEnabled(false);
							} else {
								aMap.clear();
							}
							dismissDialog();
							canClick = true;
							// 获取宝贝最后一次的位置
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (getResources().getConfiguration().locale
											.getCountry().equals("CN")) {
										JSONObject lastloact = BabyLocateServer
												.getbabylastlocate(
														now_babyimei, "china");
										String str_lat;
										String str_lon;
										String str_address;
										String str_time;
										try {
											str_lon = lastloact
													.getString("lon");
											str_lat = lastloact
													.getString("lat");
											str_address = lastloact
													.getString("address");
											str_time = lastloact
													.getString("time");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											// e.printStackTrace();
											str_lon = "0";
											str_lat = "0";
											str_address = "";
											str_time = "";

										}
										if (str_lon.equals("0")
												&& str_lat.equals("0")) {
											ToastUtil
													.show(context,
															context.getResources()
																	.getString(
																			R.string.watchoffline)
																	+ str_address
																	+ "");

										} else {
											Double lat = Double
													.parseDouble(str_lat);
											Double lon = Double
													.parseDouble(str_lon);
											// 初始化地图
											if (aMap != null) {
												aMap.clear();
											} else {
												initAMap();
											}

											latlng = new LatLng(lat, lon);
											addMarkersToMap();
											aMap.moveCamera(CameraUpdateFactory
													.newLatLngZoom(latlng, 17));
											ToastUtil
													.show(context,
															context.getResources()
																	.getString(
																			R.string.babylastloaction)
																	+ str_address
																	+ "   "
																	+ str_time);
										}

									} else {
										JSONObject lastloact = BabyLocateServer
												.getbabylastlocate(
														now_babyimei, "english");
										String str_lat;
										String str_lon;
										String str_address;
										String str_time;
										try {
											str_lon = lastloact
													.getString("lon");
											str_lat = lastloact
													.getString("lat");
											str_address = lastloact
													.getString("address");
											str_time = lastloact
													.getString("time");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											// e.printStackTrace();
											str_lon = "0";
											str_lat = "0";
											str_address = "";
											str_time = "";

										}
										if (str_lon.equals("0")
												&& str_lat.equals("0")) {
											ToastUtil
													.show(context,
															context.getResources()
																	.getString(
																			R.string.watchoffline)
																	+ str_address
																	+ "");

										} else {
											Double lat = Double
													.parseDouble(str_lat);
											Double lon = Double
													.parseDouble(str_lon);
											// 初始化地图
											if (aMap != null) {
												aMap.clear();
											} else {
												initAMap();
											}
											latlng = new LatLng(lat, lon);
											addMarkersToMap();
											aMap.moveCamera(CameraUpdateFactory
													.newLatLngZoom(latlng, 17));
											ToastUtil
													.show(context,
															context.getResources()
																	.getString(
																			R.string.babylastloaction)
																	+ str_address
																	+ "   "
																	+ str_time);
										}
									}

								}
							});
							// 获取宝贝当前位置
							getBabyNowLocate();
						}
					});
				} else {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog();
							Toast.makeText(
									activity,
									getResources().getString(
											R.string.serverbusy), 1000).show();
						}
					});
				}
			}
		}.start();

	}

	private void setImageButtonClickListener() {
		dismissDialog();

		// 定位宝贝事件
		lin_btn_location.setOnClickListener(new LinearLayout.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							getResources().getString(R.string.networkunusable));
					return;
				}
				if (canClick) {
					canClick = false;
					handler.sendEmptyMessageDelayed(1, 3000);
					getBabyNowLocate();
				} else {
					return;
				}
			}
		});
		/*
		 * rel_btn_goto_log.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // Intent intent = new
		 * Intent(); // intent.setClass(getActivity(),
		 * GoogleLocationLogActivity.class); // startActivity(intent); Intent
		 * intent = new Intent(); intent.setClass(getActivity(),
		 * BasicMapActivity.class); startActivity(intent); } });
		 * but_goto_log.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // Intent intent = new
		 * Intent(); // intent.setClass(getActivity(),
		 * GoogleLocationLogActivity.class); // startActivity(intent); Intent
		 * intent = new Intent(); intent.setClass(getActivity(),
		 * BasicMapActivity.class); startActivity(intent); } });
		 */

		// 定位宝贝事件
		// locationBaby.setOnClickListener(new ImageButton.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// if (!CheckNetworkConnectionUtil
		// .isNetworkConnected(getActivity())) {
		// ToastUtil.show(getActivity(),
		// getResources().getString(R.string.networkunusable));
		// return;
		// }
		//
		// if (canClick == false) {
		// ToastUtil.show(getActivity(),
		// getResources().getString(R.string.waitingonemin));
		// return;
		// }
		// handler.sendEmptyMessageDelayed(1, 60000);
		//
		// progDialog = new ProgressDialog(context);
		// showDialog();
		// new Thread() {
		// public void run() {
		// final BabyNowLocationInfo babyNowLocationInfo = BabyLocateServer
		// .getBabyNowLocation(now_babyimei);
		// if (babyNowLocationInfo != null) {
		// getActivity().runOnUiThread(new Runnable() {
		// public void run() {
		// latlng = babyNowLocationInfo.getLatlng();
		// if (latlng != null && aMap != null) {
		// aMap.clear();
		// addMarkersToMap();
		//
		// aMap.moveCamera(CameraUpdateFactory
		// .newLatLngZoom(latlng, 19));
		// if (babyNowLocationInfo.getState().equals("no")) {
		// txt_linestate
		// .setText(getString(R.string.babynotline)
		// + " "
		// + babyNowLocationInfo.getTime());
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_red);
		// } else {
		// txt_linestate
		// .setText(getString(R.string.babyonline)
		// + " "
		// + babyNowLocationInfo.getTime());
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_green);
		// }
		// } else {
		// txt_linestate
		// .setText(getString(R.string.babynotline));
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_red);
		//
		// }
		// dismissDialog();
		// }
		// });
		// } else {
		// getActivity().runOnUiThread(new Runnable() {
		// public void run() {
		// dismissDialog();
		// txt_linestate
		// .setText(getString(R.string.babynotline));
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_red);
		//
		// }
		// });
		// }
		// }
		// }.start();
		// canClick = false;
		// }
		//
		// });
		// 跳入轨迹页面
		gotoRoute.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(activity, LocationHistoryActivity.class);
				startActivity(intent);
			}
		});
		relat_gotoRoute
				.setOnClickListener(new RelativeLayout.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(activity,
								LocationHistoryActivity.class);
						startActivity(intent);
					}
				});

	}

	private void initPopupWindow() {

		// rel_select_baby.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// showPopwindow();
		// }
		// });
	}

	/**
	 * 初始化PopupWindow
	 */
	private void showPopwindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.group_list, null);

		lv_group = (ListView) view.findViewById(R.id.lvGroup);
		// 加载数据

		groupAdapter = new GroupAdapter(activity, groups);
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
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int y = rel.getHeight() + statusBarHeight;
		window.showAtLocation(mapView, Gravity.TOP, 0, y);

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							getResources().getString(R.string.networkunusable));
					window.dismiss();
					return;
				}
				progDialog = new ProgressDialog(context);
				showDialog();
				Baby cur_baby = groups.get(position);
				String lasttext = selectBabyText.getText().toString();
				// 获取宝贝头像
				portrait = cur_baby.getPortrait();
				if (TextUtils.isEmpty(portrait)) {
					// img_Locationbaby.setBackgroundResource(R.drawable.babymark);
					bitmap = null;
				} else {

					if (bitmap == null) {
						byte[] bitmapArray;
						bitmapArray = Base64.decode(portrait, Base64.DEFAULT);
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 2;
						bitmap = ChatUtil.toRoundBitmap(BitmapFactory
								.decodeByteArray(bitmapArray, 0,
										bitmapArray.length, options));
					}

					babyDrawable = new BitmapDrawable(bitmap);
					// img_Locationbaby.setBackground(babyDrawable);
				}
				selectBabyText.setText(groups.get(position).getBabyname());
				now_babyimei = groups.get(position).getBabyimei();

				groups.remove(position);
				groups.add(now_baby);
				now_baby = cur_baby;
				now_babyimei = now_baby.getBabyimei();

				MbApplication.getGlobalData().setNowBaby(now_baby);
				MbApplication.getGlobalData().setGroups(groups);
				// 将当前宝贝的imei存到全局变量里
				MbApplication.getGlobalData().getNowuser()
						.setImei(now_babyimei);
				groupAdapter.notifyDataSetChanged();

				if (!CheckNetworkConnectionUtil
						.isNetworkConnected(activity)) {
					ToastUtil.show(activity,
							getResources().getString(R.string.networkunusable));
					return;
				}
				// 发送广播
				activity.sendBroadcast(new Intent("CHANGE_BABY_ACTION"));
				// refreshMap(now_babyimei);
				if (window != null) {
					window.dismiss();
				}
			}
		});
	}

	public void refreshMap(final String imei) {
		now_babyimei = imei;
		getBabyNowLocate();
	}

	/**
	 * 初始化AMap对象
	 */
	private void initAMap() {
		// 设置默认定位按钮是否显示
		addMarkersToMap();// 往地图上添加Babymarker
		// new Thread() {
		// public void run() {
		// final BabyNowLocationInfo babyNowLocationInfo = BabyLocateServer
		// .getBabyNowLocation(now_babyimei);
		// if (getActivity() == null) {
		// return;
		// }
		//
		// if (babyNowLocationInfo != null) {
		// getActivity().runOnUiThread(new Runnable() {
		// public void run() {
		// latlng = babyNowLocationInfo.getLatlng();
		// if (latlng != null && aMap != null) {
		// aMap.moveCamera(CameraUpdateFactory
		// .newLatLngZoom(latlng, 19));
		// if (babyNowLocationInfo.getState().equals("no")) {
		// txt_linestate
		// .setText(getString(R.string.babynotline)
		// + " "
		// + babyNowLocationInfo.getTime());
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_red);
		// } else {
		// txt_linestate
		// .setText(getString(R.string.babyonline)
		// + " "
		// + babyNowLocationInfo.getTime());
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_green);
		// }
		// } else {
		// txt_linestate
		// .setText(getString(R.string.babynotline));
		// img_btn_state.setBackgroundResource(R.drawable.location_btn_red);
		//
		// }
		// dismissDialog();
		// }
		// });
		// }
		// }
		// }.start();
	}

	// 往地图上添加Babymarker
	private void addMarkersToMap() {

		if (latlng.latitude == 0 && latlng.longitude == 0) {
			ToastUtil.show(activity, getString(R.string.babynotnoline));
			return;
		}
		if (radius != 0) {
			circle = aMap.addCircle(new CircleOptions().center(latlng)
					.radius(radius).strokeColor(Color.argb(50, 253, 143, 85))
					.fillColor(Color.argb(50, 253, 121, 53)).strokeWidth(2));
		}
		if (portrait != null) {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(portrait, Base64.DEFAULT);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			bitmap = ChatUtil.toRoundBitmap(BitmapFactory.decodeByteArray(
					bitmapArray, 0, bitmapArray.length, options));
			Marker marker = aMap.addMarker(new MarkerOptions().position(latlng)
					.draggable(true)
					.icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
			marker.setAnchor(0.5f, 0.5f);
			marker.showInfoWindow();
		} else {
			Marker marker = aMap.addMarker(new MarkerOptions()
					.position(latlng)
					.draggable(true)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.babymark)));
			marker.setAnchor(0.5f, 0.5f);
			marker.showInfoWindow();
		}
	}

	// // 往地图上添加手机marker
	// private void addMyMarkersToMap(LatLng myLatLng) {
	//
	// // 定位到手机位置
	//
	// Marker marker = aMap.addMarker(new MarkerOptions()
	// .position(myLatLng)
	// .draggable(true)
	// .icon(BitmapDescriptorFactory
	// .fromResource(R.drawable.mylocation)));
	//
	// }

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.mylocation));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
		myLocationStyle.strokeWidth(0);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		// 修复部分机型回到单位界面定位按钮图片显示不正常
		if (bitmap != null) {
			babyDrawable = new BitmapDrawable(bitmap);
			// img_Locationbaby.setBackgroundDrawable(babyDrawable);
		}
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
		bitmap = null;
		mapView.onDestroy();
		activity.unregisterReceiver(mBroadcastReceiver);

		// 反注册云巴，推送
		// 解除绑定频道
		Log.i("keis", Arrays.toString(LocationFragment.imeiTopic));
		YunBaStart.UnBindTopic(activity, LocationFragment.imeiTopic);
		LocationFragment.imeiTopic = null;

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
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy
					.getInstance(activity);
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
		progDialog.setCanceledOnTouchOutside(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getResources().getString(R.string.gettingdata));
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

	public static MapView getMapView() {
		return mapView;
	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {
		showDialog();
		GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("baby_info_change")) {
				getBabyList();
			}
			//弹出连接超时次数的提示框
			if (action.equals("opendialog")) {
				
				dialogUtil.dismissDialog();
				int num=intent.getIntExtra("num", -1);
                if(num>=3){
                	
				return;	
				
				}else{
					if(num>1){
						dialogUtil = new DialogUtil(activity,
								"正在尝试重新连接腕表。。。。");
					}else{
						dialogUtil = new DialogUtil(activity,
								"正在连接腕表。。。。");
					}
					
					dialogUtil.showDialog();
				}

			}
			//腕表在线状态发生变化
			if (action.equals(MqttConnection.ACTION_BABY_IS_ONLINE)) {
			   boolean	babylocatstaus=intent.getBooleanExtra(MqttConnection.EXTRA_NAME_BABY_IS_ONLINE, false);
				System.out.println("babylocatstaus"+babylocatstaus);
			   String bechangedimei=intent.getStringExtra(MqttConnection.EXTRA_NAME_BABY_IMEM);
				if(!TextUtils.isEmpty(bechangedimei)&&bechangedimei.equals(now_babyimei)){
					if(babylocatstaus){
						img_btn_state
						.setImageResource(R.drawable.location_btn_green);
					}else{
						img_btn_state
						.setImageResource(R.drawable.location_btn_red);
						ToastUtil.show(activity,
								getString(R.string.watchoffline));
					}
				}
			}
			
			
			// 接收宝贝位置改变消息推送
			if (action.equals(MqttConnection.ACTION_NOW_LOCATE)) {
				String babyImei = intent
						.getStringExtra(MqttConnection.EXTRA_NAME_BABY_IMEM);
				lon = Double
						.valueOf(intent
								.getStringExtra(MqttConnection.EXTRA_NAME_NOW_LOCATE_LON));
				lat = Double
						.valueOf(intent
								.getStringExtra(MqttConnection.EXTRA_NAME_NOW_LOCATE_LAT));
				img_btn_state
				.setImageResource(R.drawable.location_btn_green);
			}
			// 接收修改宝贝头像广播
			if (action.equals("CHANGE_IMAGE_ACTION")) {
				portrait = intent.getStringExtra("str_photp");
				if (bitmap == null) {
					byte[] bitmapArray;
					bitmapArray = Base64.decode(portrait, Base64.DEFAULT);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					bitmap = ChatUtil.toRoundBitmap(BitmapFactory
							.decodeByteArray(bitmapArray, 0,
									bitmapArray.length, options));
				}

				babyDrawable = new BitmapDrawable(bitmap);
				// img_Locationbaby.setBackground(babyDrawable);

				// 初始化地图
				if (aMap != null && latlng != null) {
					aMap.clear();
					initAMap();
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,
							19));
				}
			}
			// 接收切换宝贝广播
			if (action.equals("CHANGE_BABY_ACTION")) {
				now_baby = MbApplication.getGlobalData().getNowBaby();
				now_babyimei = now_baby.getBabyimei();
				groups = MbApplication.getGlobalData().getGroups();
				selectBabyText.setText(now_baby.getBabyname());
				// refreshMap(now_baby.getBabyimei());
				portrait = now_baby.getPortrait();
				byte[] bitmapArray;
				bitmapArray = Base64.decode(portrait, Base64.DEFAULT);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bitmap = ChatUtil.toRoundBitmap(BitmapFactory.decodeByteArray(
						bitmapArray, 0, bitmapArray.length, options));
				babyDrawable = new BitmapDrawable(bitmap);
				// img_Locationbaby.setBackground(babyDrawable);
				if (aMap == null) {
					return;
				}
				aMap.clear();
				getBabyNowLocate();
				
			}

			if (action.equals("LOCATE_BABY_ACTION")) {
				getBabyNowLocate();
			}

			if (action.equals(MqttConnection.ACTION_NOW_LOCATE)) {
				String imei = intent
						.getStringExtra(MqttConnection.EXTRA_NAME_BABY_IMEM);
				if (imei.equals(MbApplication.getGlobalData().getNowBaby()
						.getBabyimei())) {
					Double new_lon = Double
							.parseDouble(intent
									.getStringExtra(MqttConnection.EXTRA_NAME_NOW_LOCATE_LON));
					Double new_lat = Double
							.parseDouble(intent
									.getStringExtra(MqttConnection.EXTRA_NAME_NOW_LOCATE_LAT));
					latlng = new LatLng(new_lat, new_lon);
					String time = intent
							.getStringExtra(MqttConnection.EXTRA_NAME_NOW_LOCATE_TIME);
					radius = Integer
							.parseInt(intent
									.getStringExtra(MqttConnection.EXTRA_NAME_NOW_LOCATE_RADIUS));

					if (latlng.latitude == 0 && latlng.longitude == 0) {
						ToastUtil.show(activity,
								getString(R.string.babynotnoline));
						img_btn_state
								.setImageResource(R.drawable.location_btn_red);
						return;
					}
					img_btn_state
							.setImageResource(R.drawable.location_btn_green);
					if (aMap != null) {
						aMap.clear();
						addMarkersToMap();
						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								latlng, 17));
					} else {
						initAMap();
					}
					if (radius == 5) {
						text_GPS.setText("GPS");
						ToastUtil.show(getActivity(),
								getString(R.string.GPSpositioningsuccess)
										+ "  " + time);
					} else {
						text_GPS.setText("LBS");
						ToastUtil.show(getActivity(),
								getString(R.string.LBSpositioningsuccess)
										+ "  " + time);
					}
					dialogUtil.dismissDialog();
				}
			}
		}

	};

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (canClick == false) {
					canClick = true;
				}
			}
			if (msg.what == 2) {
				if (img_btn_state.getVisibility() == (View.INVISIBLE)) {
					img_btn_state.setVisibility(View.VISIBLE);
				} else {
					img_btn_state.setVisibility(View.INVISIBLE);
				}
				handler.sendEmptyMessageDelayed(2, 1000);
			}
		};
	};

}
