package com.evil.mobilesafe.db;

import com.evil.mobilesafe.interfaces.Constants;

import android.R.string;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class AppLockDB extends SQLiteOpenHelper {

	public AppLockDB(Context context) {
		super(context, T_DBNAME, null, 1);

	}

	/**
	 * 数据库表名
	 */
	public static String T_TABLE = "applock";
	/**
	 * 数据库名
	 */
	public static String T_DBNAME = "appLock.db";
	/**
	 * 数据库中存储的加锁app包名
	 */
	public static String T_APP_PACKAGENAME = "appLockPackageName";
	
	/**
	 * 内容观察者的uri
	 */
	public static final Uri LOCK_URI = Uri.parse("content://com.evil.lock/t_lock");

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + T_TABLE
				+ " (_id integer primary key autoincrement," 
				+ T_APP_PACKAGENAME + " text " + ")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
