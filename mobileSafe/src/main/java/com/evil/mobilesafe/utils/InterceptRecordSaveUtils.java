package com.evil.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class InterceptRecordSaveUtils {
	
	/**
	 * 保存拦截到的短信
	 */
	public static void saveSms(Context context,String address,String smsBody,long time) {
		SharedPreferences sp = context.getSharedPreferences("InterceptSms", 0);
		
		Editor edit = sp.edit();
		
		edit.putString("address", address);
		edit.putString("body", smsBody);
		edit.putLong("time", time);
		
		edit.commit();
	}
}
