package com.mobao.watch.util;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class CubeTransformer implements PageTransformer {
	private static final float MIN_SCALE = 0.75f;  
	private int i=0;
	@Override
	public void transformPage(View view, float position) {
		// TODO Auto-generated method stub
		 int pageHeight = view.getHeight();  
		 System.out.println("89888888888888888888888888888888888888888888888");
		  

		 
		 
	        if (position < -1) { // [-Infinity,-1)  
	            // This page is way off-screen to the left.  
	            view.setAlpha(0);  
	  
	        } else if (position <= 0) { // [-1,0]  
	            // Use the default slide transition when moving to the left page  
	            view.setAlpha(1);  
	            view.setTranslationY(0);  
	            view.setScaleX(1);  
	            view.setScaleY(1);  
	  
	        } else if (position <= 1) { // (0,1]  
	            // Fade the page out.  
	            view.setAlpha(1 - position);  
	  
	            // Counteract the default slide transition  
	            view.setTranslationY(pageHeight * -position);  
	  
	            // Scale the page down (between MIN_SCALE and 1)  
	            float scaleFactor = MIN_SCALE  
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));  
	           // view.setScaleX(scaleFactor);  
	            view.setScaleY(scaleFactor);  
	         //   ViewHelper.setRotation(view, 0.2f);
	  
	        } else { // (1,+Infinity]  
	            // This page is way off-screen to the right.  
	            view.setAlpha(0);  
	        }  
	}

}
