package com.evil.mobilesafe.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Environment;
import com.evil.mobilesafe.domain.AppInfo;

/**
 * app管理工具
 */
public class AppManagerUtils {

	/**
	 * 获取app的信息
	 */
	public static List<AppInfo> getAppInfo(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();

		// 获取包管理器
		PackageManager manager = context.getPackageManager();

		// 获取安装应用的包信息
		List<PackageInfo> packages = manager.getInstalledPackages(0);

		for (PackageInfo packageInfo : packages) {
			AppInfo info = getAppInfo(manager, packageInfo);
			list.add(info);
		}

		return list;
	}

	/**
	 * 获取应用程序的信息
	 */
	public static AppInfo getAppInfo(PackageManager manager,
			PackageInfo packageInfo) {
		AppInfo info = new AppInfo();
		// 获取程序的包名
		info.appPackageName = packageInfo.packageName;

		// 获取app安装详细信息
		ApplicationInfo applicationinfo = packageInfo.applicationInfo;
		// 获取应用名
		String appName = applicationinfo.loadLabel(manager).toString();
		info.appName = appName;

		// 根据uid获取应用的流量信息
		int uid = applicationinfo.uid;

		long uidRxBytes = TrafficStats.getUidRxBytes(uid);
		long uidTxBytes = TrafficStats.getUidTxBytes(uid);

		long flux = uidRxBytes + uidTxBytes;
		
		if (flux > 0) {
			info.fluxSize = flux;
		}

		// 获取app的路径
		info.appPath = applicationinfo.sourceDir;

		// 获取应用图标
		Drawable loadIcon = applicationinfo.loadIcon(manager);
		info.appIcon = loadIcon;

		// 获取应用大小
		String sourceDir = applicationinfo.sourceDir;
		File file = new File(sourceDir);
		long appSize = file.length();
		info.appSize = appSize;

		int flags = applicationinfo.flags;
		// 判断应用是否是系统应用
		if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
			info.isSystem = true;
		} else {
			info.isSystem = false;
		}

		// 判断应用是否安装在sd卡中
		if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
			info.isSD = true;
		} else {
			info.isSD = false;
		}

		return info;
	}

	/**
	 * 获取手机内存总空间大小
	 */
	public static long getRomTotalSpace() {
		File dataDirectory = Environment.getDataDirectory();

		return dataDirectory.getTotalSpace();
	}

	/**
	 * 获取手机内存可用空间大小
	 */
	public static long getRomFreeSpace() {
		File dataDirectory = Environment.getDataDirectory();

		return dataDirectory.getFreeSpace();
	}

	/**
	 * 获取手机内存已经使用的空间大小
	 */
	public static long getRomUsedSpace() {
		return getRomTotalSpace() - getRomFreeSpace();
	}

	/**
	 * 获取sd卡已经使用的空间大小
	 */
	public static long getSDUsedSpace() {
		return getSDTotalSpace() - getSDFreeSpace();
	}

	/**
	 * 获取存储卡已经使用的空间大小
	 */
	public static long getSDTotalSpace() {
		File sdDirectory = Environment.getExternalStorageDirectory();

		return sdDirectory.getTotalSpace();
	}

	/**
	 * 获取sd卡可以使用空间大小
	 */
	public static long getSDFreeSpace() {
		File sdDirectory = Environment.getExternalStorageDirectory();

		return sdDirectory.getFreeSpace();
	}

}
