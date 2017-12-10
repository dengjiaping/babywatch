/**
 * 
 */
package com.mobao.watch.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static Toast toast=null;	//	用以显示内容的Toast

	public static void show(Context context, String info) {
		if(toast==null){
			toast.makeText(context, info, 3000).show();
		}else{  
			  toast.cancel();
			  toast=null;
			  toast.makeText(context, info, 3000).show();
		}
		
	}

	public static void show(Context context, int info) {
		
		if(toast==null){
			toast.makeText(context, info, 3000).show();
		}else{  
			  toast.cancel();
			  toast=null;
			  toast.makeText(context, info, 3000).show();
		}

	}
	
	
}
