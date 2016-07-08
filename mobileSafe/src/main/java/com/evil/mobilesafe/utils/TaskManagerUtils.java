package com.evil.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.evil.mobilesafe.domain.AppInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 用户进程管理工具
 */
public class TaskManagerUtils {

	/**
	 * 获取当前所有运行的进程
	 */
	public static List<AppInfo> getProgress(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取正在运行的app进程集合
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
			// 获取进程名,即是包名
			String processName = runningAppProcessInfo.processName;

			// 获取包管理器
			PackageManager pm = context.getPackageManager();
			// 根据包名获取包信息
			PackageInfo packageInfo = null;
			try {
				packageInfo = pm.getPackageInfo(processName, 0);

				// 获取appInfo信息
				AppInfo appInfo = AppManagerUtils.getAppInfo(pm, packageInfo);

				// 根据pid获取进程的占用内存大小

				android.os.Debug.MemoryInfo[] memoryInfos = am
						.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
				// 获取到appInfo进程占用内存的大小
				android.os.Debug.MemoryInfo memoryInfo = memoryInfos[0];
				appInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

				list.add(appInfo);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		}

		return list;
	}

	/**
	 * 获取手机总内存
	 * 
	 */
	public static long getTotalMemorySize(Context context) {
		// 1.读取/proc/meminfo
		File file = new File("/proc/meminfo");
		try {
			// 读取第一行
			BufferedReader br = new BufferedReader(new FileReader(file));

			String readLine = br.readLine();
			// 切割字符串
			String line = readLine.substring(readLine.indexOf(' ') + 1,
					readLine.lastIndexOf(' ')).trim();
			PrintLog.log(line);
			return Integer.parseInt(line) * 1024;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取手机使用内存
	 */
	public static long getAvailMemorySize(Context context) {
		// 获取活动管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		// 获取内存信息
		am.getMemoryInfo(outInfo);

		return outInfo.availMem; // 获取可用内存大小
	}
	
	/**
	 * 获取最大进程个数
	 */
	public static int getMaxTaskNumber(Context context){
		List<AppInfo> appInfo = AppManagerUtils.getAppInfo(context);
		return appInfo.size();
	}
}
