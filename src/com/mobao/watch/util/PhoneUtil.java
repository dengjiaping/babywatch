package com.mobao.watch.util;

public class PhoneUtil {

	/**
	 * 分割手机号码
	 * 
	 * @param phone
	 *            手机号码
	 * @return
	 */
	public static String splitPhoneNum(String phone) {
		StringBuilder builder = new StringBuilder(phone);
		builder.reverse();
		for (int i = 4, len = builder.length(); i < len; i += 5) {
			builder.insert(i, ' ');
		}
		builder.reverse();
		return builder.toString();
	}

}
