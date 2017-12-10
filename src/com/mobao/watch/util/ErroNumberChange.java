package com.mobao.watch.util;

import com.mb.zjwb1.R;

import android.content.Context;


public class ErroNumberChange {
	private Context context;
	//初始化获取context
	public ErroNumberChange(Context context){
		this.context=context;
	}
	
	//对错误码进行转换
	public String chang(String erronum){
		String erromsg="";
		try {
			switch (Integer.parseInt(erronum)) {
			case 10010:
				erromsg =context.getString(R.string.for10010);
				break;

			case 10011:
				erromsg = context.getString(R.string.for10011);
				break;

			case 10012:
				erromsg = context.getString(R.string.for10012);
				break;

			case 10013:
				erromsg = context.getString(R.string.for10013);
				break;

			case 10021:
				erromsg = context.getString(R.string.for10021);
				break;

			case 10022:
				erromsg = context.getString(R.string.for10022);
				break;

			case 10023:
				erromsg = context.getString(R.string.for10023);
				break;

			case 10024:
				erromsg = context.getString(R.string.for10024);
				break;

			case 10030:
				erromsg = context.getString(R.string.for10030);
				break;

			case 10031:
				erromsg =context.getString(R.string.for10031);
				break;

			case 10040:
				erromsg =context.getString(R.string.for10040);
				break;

			case 10041:
				erromsg = context.getString(R.string.for10041);
				break;

			case 10042:
				erromsg = context.getString(R.string.for10042);
				break;

			case 10043:
				erromsg = context.getString(R.string.for10043);
				break;

			case 10050:
				erromsg = context.getString(R.string.for10050);
				break;

			case 10051:
				erromsg = context.getString(R.string.for10051);
				break;

			case 10060:
				erromsg = context.getString(R.string.for10060);
				break;

			case 10061:
				erromsg = context.getString(R.string.for10061);
				break;
			case 20000:
				erromsg = context.getString(R.string.for20000);
				break;
			case 20001:
				erromsg = context.getString(R.string.for20001);
				break;
			case 20002:
				erromsg =context.getString(R.string.for20002);
				break;

			case 20003:
				erromsg = context.getString(R.string.for20003);
				break;
				
			case 10020:
				erromsg = context.getString(R.string.for10020);
				break;
				
			case 10070:
				erromsg = context.getString(R.string.for10070);
				break;
			case 10071:
				erromsg = context.getString(R.string.for10071);
				break;
			case 10080:
				erromsg = context.getString(R.string.for10080);
				break;
			case 10081:
				erromsg = context.getString(R.string.for10081);
				break;
			case 10082:
				erromsg = context.getString(R.string.for10082);
				break;
			case 10090:
				erromsg = context.getString(R.string.requestpositioningfailure);
				break;
			case 10091:
				erromsg = context.getString(R.string.time_is_too_short);
				break;
			case 10100:
				erromsg = context.getString(R.string.codeiunusable);
				break;
			case 10101:
				erromsg = context.getString(R.string.request_watch_authok_outtime);
				break;
			case 20004:
				erromsg =context.getString(R.string.for20004);
				break;

			default:
				break;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return context.getString(R.string.for10031);
		}
		
		return erromsg;
		
	}

}