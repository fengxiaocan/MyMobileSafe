package com.evil.mobilesafe.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 
 */

/**
 * 时间工具类
 * 
 * @author 风小灿
 * @date 2016-6-19
 */
public class TimeTool {
	/**
	 * 格式:2016-08-20 11:11:11
	 */
	public static final String DATE_TYPE1 = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 格式:2016-08-20
	 */
	public static final String DATE_TYPE2 = "yyyy-MM-dd";
	/**
	 * 格式:2016-08-20 Tuesday"
	 */
	public static final String DATE_TYPE3 = "yyyy-MM-dd E";
	/**
	 * 格式:2016年 第181天
	 */
	public static final String DATE_TYPE4 = "yyyy年 '第'D'天'";
	/**
	 * 格式:12:24:36
	 */
	public static final String DATE_TYPE5 = "HH:mm:ss";
	/**
	 * 格式:2016年6月19日 星期日
	 */
	public static final String DATE_TYPE6 = "yyyy年MM月dd日 E";
	/**
	 * 格式:12:23:34 星期日 下午
	 */
	public static final String DATE_TYPE7 = "HH:mm:ss E a";
	/**
	 * 格式:2015年12月23日 星期日 下午
	 */
	public static final String DATE_TYPE8 = "yyyy年MM月dd日 E a";

	/**
	 * 获取当前时间
	 * 
	 * @return 当前时间的毫秒值
	 */
	public static long getNowTime() {
		long nowTime = System.currentTimeMillis();
		return nowTime;
	}

	/**
	 * 获取当前时间
	 * 
	 * @param type
	 *            时间的格式
	 * @return 返回格式化后的时间
	 */
	public static String getNowTime(String type) {
		long nowTime = System.currentTimeMillis();
		return formatTime(nowTime, type);
	}

	/**
	 * 格式化时间
	 * 
	 * @param date
	 *            时间 单位:毫秒
	 * @param type
	 *            日期格式
	 * @return
	 */
	public static String formatTime(long date, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		String time = sdf.format(date);
		return time;
	}
}
