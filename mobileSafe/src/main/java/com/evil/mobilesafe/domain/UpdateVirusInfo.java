package com.evil.mobilesafe.domain;

/**
 * @version 病毒库版本
 * @downloadUrl 病毒库下载地址
 * @md5 更新的病毒的md5值
 * @type 病毒的类型
 * @name 病毒名
 * @desc 病毒的描述信息
 */
public class UpdateVirusInfo {
	
	public int version;
	public String downloadUrl;
	public String md5;
	public int type;
	public String name;
	public String desc;
	@Override
	public String toString() {
		return "UpdateVirusInfo [version=" + version + ", downloadUrl="
				+ downloadUrl + ", md5=" + md5 + ", type=" + type + ", name="
				+ name + ", desc=" + desc + "]";
	}
}
