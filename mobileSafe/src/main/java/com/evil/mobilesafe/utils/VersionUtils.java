package com.evil.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


public class VersionUtils {
	private static PackageManager sPackageManager;
	private static PackageInfo sPackageInfo;
	
	private VersionUtils(Context context) {
		super();
	}
	
	/**
	 * 获取版本名字
	 */
	public static String getVersionName(Context context){
		String versionName = "未知版本";
		sPackageManager = context.getPackageManager();
		
		try {
			//获取包的信息
			sPackageInfo = sPackageManager.getPackageInfo(context.getPackageName(), 0);
			//获取版本的名字
			versionName = sPackageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	
	/**
	 * 获取版本号
	 */
	public static int getVersionCode(Context context){
		int versionCode = -1;
		//获取包管理器
		sPackageManager = context.getPackageManager();
		try {
			//获取包的信息
			sPackageInfo = sPackageManager.getPackageInfo(context.getPackageName(), 0);
			//获取包的版本号
			versionCode = sPackageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		
		return versionCode;
	}
	
}
