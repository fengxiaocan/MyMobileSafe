package com.evil.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class KillVirusDao {
	private Context mContext;

	public KillVirusDao(Context context) {
		mContext = context;
	}

	/**
	 * 对比是否是病毒
	 * 
	 * @param md5
	 *            查询文件的特征码是否是病毒
	 * @return 是否是病毒;假如为空,则不是病毒;假如不为空,则是病毒
	 */
	public String checkVirus(String md5) {
		String desc = "";
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(mContext.getFilesDir()
				+ "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);

		String[] columns = { "desc" };
		String selection = "md5=?";
		String[] selectionArgs = { md5 };
		// 查询数据库
		Cursor cursor = db.query("datable", columns, selection, selectionArgs,
				null, null, null);
		// 判断是数据库是否存在该类型木马的md5值
		if (cursor.moveToFirst()) {
			desc = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return desc;
	}

	/**
	 * @return 数据库版本号
	 */
	public int queryVersion() {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(mContext.getFilesDir()
				+ "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);

		String[] columns = { "subcnt" };
		// 查询数据库
		Cursor cursor = db.query("version", columns, null, null, null, null,
				null);
		// 把游标移动到下一位
		cursor.moveToFirst();
		int version = cursor.getInt(0);

		cursor.close();
		db.close();

		return version;
	}

	/**
	 * @param version
	 *            更新病毒库的版本号
	 */
	public void updataVersion(int version) {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(mContext.getFilesDir()
				+ "/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();
		values.put("subcnt", version);
		db.update("version", values, null, null);
		db.close();
	}

	/**
	 * @param md5
	 *            病毒的md5特征
	 * @param type
	 *            病毒的类型
	 * @param name
	 *            病毒的名字
	 * @param desc
	 *            病毒的描述信息
	 */
	public void addNewVirus(String md5, int type, String name, String desc) {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(mContext.getFilesDir()
				+ "/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", type);
		values.put("name", name);
		values.put("desc", desc);

		db.insert("datable", null, values);

		db.close();
	}
}
