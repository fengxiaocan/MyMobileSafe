package com.evil.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

/**
 * 进程类管理工具
 */
public class ActivityUtils {
	/**
	 * 判断服务是否运行
	 */
	public static boolean isRunningService(Context context,
			Class<? extends Service> clazz) {
		// 获取活动管理器
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取运行中的服务
		List<RunningServiceInfo> list = manager
				.getRunningServices(Integer.MAX_VALUE);
		// 遍历集合
		for (RunningServiceInfo runningServiceInfo : list) {
			ComponentName service = runningServiceInfo.service;

			String className = service.getClassName(); // 获取类名的全路径
			String packageName = service.getPackageName(); // 获取包名

			// System.out.println("className:  "+className);
			// System.out.println("packageName:   "+packageName);

			// 判断运行中的服务类名是否一样
			if (className.equals(clazz.getCanonicalName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 杀死一个app进程
	 * @param context	上下文
	 * @param packageName	要杀死的程序的包名
	 */
	public static void killApp(Context context,String packageName) {
		// 获取活动管理器
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		manager.killBackgroundProcesses(packageName); //杀死一个app进程
	}
}
