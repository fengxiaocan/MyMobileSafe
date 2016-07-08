/**
 * 
 */
package com.evil.mobilesafe.domain;

/**
 * @author 风小灿
 * @date 2016-6-18
 */
public class SmsInfo {
	public String address;
	public long date;
	public String date_sent;
	public int type;
	public String body;
	@Override
	public String toString() {
		return "SmsInfo [address=" + address + ", date=" + date
				+ ", date_sent=" + date_sent + ", type=" + type + ", body="
				+ body + "]";
	}
}
