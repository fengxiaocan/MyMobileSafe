package com.evil.mobilesafe.db;

import com.evil.mobilesafe.interfaces.MyContext;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 风小灿
 * @date 2016-6-20
 */
public class FluxDB extends SQLiteOpenHelper {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "flux.db";
	/**
	 * 数据库表名
	 */
	public static final String DB_TATLE = "fluxTatle";
	/**
	 * 应用程序的包名
	 */
	public static final String DB_APP_NAME = "appPackageName";
	/**
	 * 应用数据流量
	 */
	public static final String DB_APP_FLUX = "flux";

	public FluxDB() {
		super(MyContext.mContext, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库
		String sql = "create table " + DB_TATLE
				+ " (_id integer primary key autoincrement, " + DB_APP_NAME
				+ " text, " + DB_APP_FLUX + " text )";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
