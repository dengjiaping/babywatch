package com.mobao.watch.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.activity.InformationCenterActivity;
import com.mobao.watch.activity.MainActivity;
import com.mobao.watch.bean.InformationCenter;

/**
 * @author 坤
 * 通知栏工具类
 *
 */
public class NotificationUtil {
	
     public static void getNotification(Context context,String them,String title,String message){
    	//获得通知管理器
         NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
         //构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
         Notification notification = new Notification(R.drawable.not_qidong_logo,them,System.currentTimeMillis());
         Intent intent = new Intent(context,BabyFragmentActivity.class);
         intent.putExtra("notification", "notification");
         intent.putExtra("message", message);
         PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
         notification.setLatestEventInfo(context.getApplicationContext(), title, message, pendingIntent);
         notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
         notification.defaults = Notification.DEFAULT_SOUND;//声音默认
         manager.notify(1, notification);//发动通知,id由自己指定，每一个Notification对应的唯一标志
         //其实这里的id没有必要设置,只是为了下面要用到它才进行了设置
     }
     
     
     
    /**
     * @param context 上下文
     * @param topic  标题
     * @param msg  内容
     * 点击后跳转 消息中心
     */
    public static void getYunBaNotification(Context context,String topic,String msg,String imei){
    	//获得通知管理器
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
        Notification notification = new Notification(R.drawable.not_qidong_logo,topic,System.currentTimeMillis());
        Intent intent = new Intent(context,InformationCenterActivity.class);
        intent.putExtra("notification", "notification");
        intent.putExtra("message", msg);
        intent.putExtra("imei", imei);
        intent.setAction(""+System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1001,intent,0);
        notification.setLatestEventInfo(context, topic, msg, pendingIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
        notification.defaults = Notification.DEFAULT_SOUND;//声音默认
        manager.notify(1001, notification);//发动通知,id由自己指定，每一个Notification对应的唯一标志
        
      }
}
