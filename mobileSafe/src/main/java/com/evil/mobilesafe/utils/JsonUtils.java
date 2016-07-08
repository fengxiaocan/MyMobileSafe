package com.evil.mobilesafe.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.domain.UpdateInfo;
import com.evil.mobilesafe.domain.UpdateVirusInfo;

public class JsonUtils {
	
	/**
	 * 解析更新app地址json文件
	 * @return 
	 * @throws JSONException 
	 */
	public static UpdateInfo jsonAnalysis(String json) throws JSONException{
		//使用java的JSONObject来解析json
		JSONObject obj = new JSONObject(json);
		int versionCode = obj.getInt("versionCode");
		String versionDesc = obj.getString("versionDesc");
		String downloadUrl = obj.getString("downloadUrl");
		
		//把解析的json存储在UpdateInfo中
		UpdateInfo updateInfo = new UpdateInfo();
		updateInfo.versionCode = versionCode;
		updateInfo.versionDesc = versionDesc;
		updateInfo.downloadUrl =downloadUrl;
		
		return updateInfo;
	}
	
	/**
	 * 解析病毒库更新json地址
	 */
	public static UpdateVirusInfo jsonAnalysisVirus(String json) throws JSONException{
		//使用java的JSONObject来解析json
		JSONObject obj = new JSONObject(json);
		int version = obj.getInt("version");
		int type = obj.getInt("type");
		String downloadUrl = obj.getString("downloadUrl");
		String md5 = obj.getString("md5");
		String name = obj.getString("name");
		String desc = obj.getString("desc");
		
		//把解析的json存储在UpdateInfo中
		UpdateVirusInfo updateInfo = new UpdateVirusInfo();
		updateInfo.version = version;
		updateInfo.desc = desc;
		updateInfo.downloadUrl = downloadUrl;
		updateInfo.md5 = md5;
		updateInfo.type = type;
		updateInfo.name = name;
		
		return updateInfo;
	}
	
	/**
	 * 把数组转换成字符串
	 */
}
