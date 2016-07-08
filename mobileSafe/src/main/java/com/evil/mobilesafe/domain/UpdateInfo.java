package com.evil.mobilesafe.domain;

public class UpdateInfo {
	
	public int versionCode;
	public String versionDesc;
	public String downloadUrl;
	
	@Override
	public String toString() {
		return "UpdateInfo [versionCode=" + versionCode + ", versionDesc="
				+ versionDesc + ", downloadUrl=" + downloadUrl + "]";
	}
}
