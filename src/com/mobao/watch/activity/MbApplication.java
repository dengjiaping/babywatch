package com.mobao.watch.activity;

import android.app.Application;

import com.mobao.watch.bean.GlobalData;

public class MbApplication extends Application {
	private static MbApplication instance;
	private GlobalData data;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public synchronized static GlobalData getGlobalData() {
		if (null == instance) {
			instance = new MbApplication();
		}

		return instance.getData();
	}

	private GlobalData getData() {
		if (data == null) {
			data = new GlobalData(MbApplication.this);
		}
		return data;
	}
}
