package com.mobao.watch.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.mb.zjwb1.R;
import com.mobao.watch.util.ChatUtil;

public class BasicMapActivity extends Activity {

	private MapView mapView;
	private AMap aMap;
	String portrait = null;
	Bitmap babyBitmap = null;
	private LatLng babyLatlng = null;
	private RelativeLayout rel_backtoloaction;
	private ImageButton btn_backtoloaction;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basicmap);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		portrait = MbApplication.getGlobalData().getBabyhead();
		rel_backtoloaction = (RelativeLayout) findViewById(R.id.rel_backtoloaction);
		btn_backtoloaction = (ImageButton) findViewById(R.id.btn_backtoloaction);
		rel_backtoloaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		btn_backtoloaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		init();
		if(getIntent().getStringExtra("lat")==null||getIntent().getStringExtra("lon")==null){
			return;
		}
		Double lat = Double.parseDouble(getIntent().getStringExtra("lat"));
		Double lon = Double.parseDouble(getIntent().getStringExtra("lon"));
		babyLatlng = new LatLng(lat, lon);
		addMarkersToMap();

		
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChangeFinish(CameraPosition cameraPosition) {

					System.out.println("zoom level is:" + cameraPosition.tilt);

				}
				@Override
				public void onCameraChange(CameraPosition arg0) {

				}
			});
		}
	}
	
	// 往地图上添加Babymarker
		private void addMarkersToMap() {
			if (portrait != null) {
				byte[] bitmapArray;
				bitmapArray = Base64.decode(portrait, Base64.DEFAULT);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				babyBitmap = ChatUtil.toRoundBitmap(BitmapFactory.decodeByteArray(
						bitmapArray, 0, bitmapArray.length, options));
				Marker marker = aMap.addMarker(new MarkerOptions().position(babyLatlng)
						.draggable(true)
						.icon(BitmapDescriptorFactory.fromBitmap(babyBitmap)));
				marker.setAnchor(0.5f, 0.5f);
				marker.showInfoWindow();
			} else {
				Marker marker = aMap.addMarker(new MarkerOptions()
						.position(babyLatlng)
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.babymark)));
				marker.setAnchor(0.5f, 0.5f);
				marker.showInfoWindow();
			}
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(babyLatlng,
					17));
		}
		
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
}
