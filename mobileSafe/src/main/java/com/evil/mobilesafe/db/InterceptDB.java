package com.evil.mobilesafe.db;

import com.evil.mobilesafe.interfaces.Constants;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.content.Context;

public class InterceptDB extends SQLiteOpenHelper implements BaseColumns {

	private static final int DB_VERSION = 1;

	public InterceptDB(Context context) {
		super(context, "intercept.db", null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + Constants.DB_TABLE
				+ " (_id integer primary key autoincrement,"
				+ Constants.DB_PHONE + " text unique ," + Constants.DB_TYPE
				+ " text, " + Constants.DB_TYPE_DESC + " text " + ")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 销毁老表
		db.execSQL("drop table " + Constants.DB_TABLE);

		// 创建表
		onCreate(db);
	}

}
