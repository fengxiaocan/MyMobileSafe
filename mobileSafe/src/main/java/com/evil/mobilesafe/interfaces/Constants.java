package com.evil.mobilesafe.interfaces;

public interface Constants {
	/**
	 * 手机卫士缓存记录名
	 */
	String MOBILE_SAFE_SHAREP = "mobileSafe.xml";

	/**
	 * 下载获取更新地址
	 */
	String UPDATEURI = "http://188.188.3.34:8080/myd/androidmobilesafeupdate/update.json";

	/**
	 * 自动更新版本设置状态的记录名
	 */
	String IS_UPDATE_OPEN = "isupdate";

	/**
	 * 骚扰拦截设置状态的记录名
	 */
	String IS_INTERCEPT = "interceptOpen";

	/**
	 * 归属地显示是否开启记录状态名
	 */
	String PHONE_AREA_OPEN_SHOW = "isPhoneAreaShow";

	/**
	 * 手机防盗界面的密码保存名
	 */
	String PASSWORD = "password";

	/**
	 * 是否已经设置了手机防盗向导
	 * 或者是勾选了开启手机防盗功能
	 */
	String SET_RETRIEVE = "setRetrieve";

	
	/**
	 *	手机防盗安全号码
	 */
	String SAFE_PHONE = "safePhone";

	/**
	 * 绑定的手机SIM卡
	 */
	String SIM_NUMBER = "simNumber";
	
	/**
	 * 黑明单数据库 table表名
	 */
	String DB_TABLE = "intercept";
	
	/**
	 * 黑明单数据库的列表名的拦截号码
	 */
	String DB_PHONE = "blackPhone";
	
	/**
	 * 黑名单数据库列表名的拦截方式名
	 */
	String DB_TYPE = "blackType";
	
	/**
	 * 黑名单数据库列表名拦截方式的描述名
	 */
	String DB_TYPE_DESC = "blackTypeDesc";
	
	/**
	 * 黑名单拦截类型  电话拦截
	 */
	int INTRECEPT_TYPE_PHONE = 0;
	
	/**
	 * 黑名单拦截 短信拦截
	 */
	int INTRECEPT_TYPE_SMS = 1;
	
	/**
	 * 黑名单拦截 电话+短信拦截
	 */
	int INTRECEPT_TYPE_ALL = 2;

	/**
	 * 黑名单INFO信息传输名
	 */
	String INTERCEPT_INFO = "INFO";

	/**
	 * 吐司的x坐标位置
	 */
	String TOAST_X = "toastX";
	/**
	 * 吐司的y坐标位置
	 */
	String TOAST_Y = "toastY";

	/**
	 * 归属地样式
	 */
	String DIALOG_STYLE_COLOR = "belongStyleColor";

	/**
	 * 显示体统进程
	 */
	String SHOW_SYSTEM_TASK = "showSystemTask";

	/**
	 * 程序锁手势密码
	 */
	String APP_LOCK_PASSWORD = "GesturePassword";

	/**
	 * 通话日志
	 */
	String CALL_LOG = "callLog";

	/**
	 * 短信记录
	 */
	String SMS_LOG = "smsLog";
	
}
