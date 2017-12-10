package com.mobao.watch.customview;

import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.fragment.ChatFragment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomViewPager extends ViewPager {
	private boolean canscroll=false;

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		     if(!canscroll){
				return true;
		           	}
				
		/*
			if ((v.getClass().getName().equals("com.amap.api.maps2d.MapView")
				&& BabyFragmentActivity.getCurrIndex() == 0 )
				|| ChatFragment.RECODE_STATE == ChatFragment.RECORD_ING) {
			return true;
		}*/
		return super.canScroll(v, checkV, dx, x, y);
	}
	
	
	//根据canscroll觉得是否可以被滑动
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
	    if(!canscroll){
		return true;
           	}
		
		
		return super.onTouchEvent(arg0);
	}

	//设置是否可以滑动的boolean
	private void setcanscroll(boolean canscroll){
		this.canscroll=canscroll;
		
	}
}
