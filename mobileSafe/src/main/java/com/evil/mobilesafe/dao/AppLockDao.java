package com.evil.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.evil.mobilesafe.db.AppLockDB;

public class AppLockDao {
	private Context mContext;
	private AppLockDB mDb;

	public AppLockDao(Context context) {
		mContext = context;
		if (mDb == null) {
			mDb = new AppLockDB(context);
		}
	}

	/**
	 * 获取添加了程序锁的应用的包名
	 * 
	 * @return 要加锁的应用app包名的集合
	 */
	public List<String> getLockAppInfo() {
		List<String> appPackage = new ArrayList<String>();
		// 打开数据库
		SQLiteDatabase db = mDb.getReadableDatabase();

		String[] columns = { mDb.T_APP_PACKAGENAME };
		Cursor cursor = db.query(AppLockDB.T_TABLE, columns, null, null, null,
				null, null);
		if (cursor != null) {

			while (cursor.moveToNext()) {
				String appPackageName = cursor.getString(0);
				appPackage.add(appPackageName);
			}
		}
		cursor.close();
		db.close();

		return appPackage;
	}

	/**
	 * 添加要加锁的app
	 * 
	 * @param appPackageName
	 *            添加到数据库中的要加锁的app包名
	 */
	public void addLockApp(String appPackageName) {
		// 打开数据库
		SQLiteDatabase db = mDb.getWritableDatabase();

		String[] columns = { mDb.T_APP_PACKAGENAME };

		ContentValues values = new ContentValues();
		values.put(AppLockDB.T_APP_PACKAGENAME, appPackageName);

		db.insert(AppLockDB.T_TABLE, null, values);

		db.close();

		// 内容观察者通知
		mContext.getContentResolver().notifyChange(AppLockDB.LOCK_URI, null);
	}

	/**
	 * 删除加锁的app
	 * 
	 * @param appPackageName
	 *            要解锁的app包名
	 */
	public void deleteLockApp(String appPackageName) {
		// 打开数据库
		SQLiteDatabase db = mDb.getWritableDatabase();

		String whereClause = AppLockDB.T_APP_PACKAGENAME + "=?";
		String[] whereArgs = { appPackageName };
		db.delete(AppLockDB.T_TABLE, whereClause, whereArgs);

		db.close();
		// 内容观察者通知
		mContext.getContentResolver().notifyChange(AppLockDB.LOCK_URI, null);
	}

}
