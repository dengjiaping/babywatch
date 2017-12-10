package com.mobao.watch.util;

import io.yunba.android.manager.YunBaManager;


import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import android.content.Context;
import android.util.Log;

public class YunBaStart{
	
	//启动推送
	public static void yunBaStart(Context context){
		YunBaManager.start(context);

      //取消接收频道
		YunBaManager.unsubscribe(context, "keis",
				  new IMqttActionListener() {

				    @Override
				    public void onSuccess(IMqttToken asyncActionToken) {
				      
				    }

				    @Override
				    public void onFailure(IMqttToken asyncActionToken,Throwable exception) {
				       
				    }
				  }
				);
        
	}

	/**
	 * 用于绑定频道，接收推送消息
	 * @param context 上下文
	 * @param topicId 频道
	 */
	public static void BindTopic(Context context,String[] topicId){
		 //目前接受频道为：imei
	      YunBaManager.subscribe(context, topicId, new IMqttActionListener() {

	            @Override
	            public void onSuccess(IMqttToken arg0) {
	                Log.i("keis", "Subscribe topic succeed");
	            }

	            @Override
	            public void onFailure(IMqttToken arg0, Throwable arg1) {
	                Log.i("keis", "Subscribe topic failed") ;
	            }
	        });
	      
	}
	
	/**
	 * 用解除绑定频道，接收推送消息
	 * @param context 上下文
	 * @param topicId 频道
	 */
	public static void UnBindTopic(Context context,String[] topicId){
		 //取消目前接受频道：imei
		YunBaManager.unsubscribe(context, topicId,
				  new IMqttActionListener() {

				    @Override
				    public void onSuccess(IMqttToken asyncActionToken) {
				    	Log.i("keis", "unSubscribe topic succeed");
				    }

				    @Override
				    public void onFailure(IMqttToken asyncActionToken,Throwable exception) {
				    	Log.i("keis", "unSubscribe topic failed");
				    }
				  }
				);
	}
	
}
