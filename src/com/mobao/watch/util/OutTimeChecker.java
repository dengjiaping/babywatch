package com.mobao.watch.util;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

/** 所在线程超时停止器,不能在主线程使用
 * @author yolin
 *
 */
public class OutTimeChecker {

	private Timer timer;
	private MyTimerTask task;
	private long outTime;
	private AfterTimeOutListener listener;
	
	/** 所在线程超时停止器，当到达指定时间后，线程会停止并调用listener的回调方法onTimeIsOunt
	 * @param outTime 要设定的超时时间，以毫秒为单位
	 * @param listener 回调方法
	 */
	public OutTimeChecker(long outTime, AfterTimeOutListener listener){
		this.outTime = outTime;
		this.listener = listener;
		timer = new Timer();
		task = new MyTimerTask();
	}

	
	public void startTimeOutCheck() {
		try {
			timer.schedule(task, outTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  停止超时器
	 */
	public void cancel(){
		timer.cancel();
	}
	
	public void addAfterTimeOutListener(AfterTimeOutListener listener){
		this.listener = listener;
	}
	
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			// 关掉timer
			timer.cancel();
			listener.onTimeIsOunt();
		}

	}
	
}
