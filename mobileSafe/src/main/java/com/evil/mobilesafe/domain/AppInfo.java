package com.evil.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	public long memSize;			//占用的内存大小
	public String appPackageName;	//app包名
	public String appPath;			//app的路径
	public String appName;			//app名字
	public long appSize;			//app大小
	public Drawable appIcon;		//图标
	public boolean isSD;			//是否安装在SD卡上 
	public boolean isSystem;		//是否是系统应用
	public boolean isCheck = false;	//是否被勾选
	public boolean isVirus = false;	//是否是病毒
	public String virusDesc = "安全";//如果是病毒,显示病毒的描述信息
	public int scanProgress = 0;	//查杀病毒的进度条
	public long cacheSize = 0;		//获取缓存的信息
	public long fluxSize = -1;		//获取使用的流量信息
	
	
	@Override
	public String toString() {
		return "AppInfo [memSize=" + memSize + ", appName=" + appName + "]";
	}
	@Override
	public int hashCode() {
		if (isSystem) {
			return 1;
		}
		return -1;
	}
	@Override
	public boolean equals(Object obj) {
		AppInfo info = (AppInfo)obj;
		if (info.isSystem) {
			return true;
		}
		return false;
	}
	
	
	
}
