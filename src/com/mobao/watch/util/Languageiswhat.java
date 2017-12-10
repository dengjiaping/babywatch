package com.mobao.watch.util;

import java.util.Locale;

import android.content.Context;

public class Languageiswhat {
	private Context con;
	
	public Languageiswhat(Context con){
		this.con=con;
		
	}
	public boolean isZh() {
        Locale locale = con.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
