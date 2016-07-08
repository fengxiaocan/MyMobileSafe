package com.evil.mobilesafe.dao;

import java.util.List;
import com.evil.mobilesafe.db.FluxDB;
import com.evil.mobilesafe.domain.AppInfo;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 流量数据库操作Dao
 * 
 * @author 风小灿
 * @date 2016-6-20
 */
public class FluxDao {

	private FluxDB fluxDb;
	private ContentValues mValues = new ContentValues();;

	public FluxDao() {
		if (fluxDb == null) {
			fluxDb = new FluxDB();
		}
	}

	/**
	 * 添加一个应用的流量数据
	 * 
	 * @param packageName
	 *            应用包名
	 * @param flux
	 *            应用流量数据
	 */
	public void add(String packageName, long flux) {
		SQLiteDatabase db = fluxDb.getWritableDatabase();
		mValues.clear();
		mValues.put(FluxDB.DB_APP_NAME, packageName);
		mValues.put(FluxDB.DB_APP_FLUX, flux);
		db.insert(FluxDB.DB_TATLE, null, mValues);
		db.close();
	}

	/**
	 * 删除数据流量
	 * 
	 * @param packageName
	 *            应用包名
	 */
	public void delete(String packageName) {
		SQLiteDatabase db = fluxDb.getWritableDatabase();
		String whereClause = FluxDB.DB_APP_NAME + "=?";
		String[] whereArgs = new String[] { packageName };
		db.delete(FluxDB.DB_TATLE, whereClause, whereArgs);
		db.close();
	}

	/**
	 * 清空所有流量数据
	 */
	public void deleteAll() {
		SQLiteDatabase db = fluxDb.getWritableDatabase();
		db.delete(FluxDB.DB_TATLE, null, null);
		db.close();
	}

	/**
	 * 更新流量数据
	 * 
	 * @param packageName
	 * @param flux
	 */
	public void update(String packageName, long flux) {
		SQLiteDatabase db = fluxDb.getWritableDatabase();
		mValues.clear();
		mValues.put(FluxDB.DB_APP_FLUX, flux);
		String whereClause = FluxDB.DB_APP_NAME + "=?";
		String[] whereArgs = new String[] { packageName };
		db.update(FluxDB.DB_TATLE, mValues, whereClause, whereArgs);
		db.close();
	}

	/**
	 * 更新流量数据
	 * 
	 * @param info
	 *            所有流量数据的集合
	 */
	public void update(List<AppInfo> info) {
		SQLiteDatabase db = fluxDb.getWritableDatabase();
		for (AppInfo appInfo : info) {
			mValues.clear();
			mValues = new ContentValues();
			mValues.put(FluxDB.DB_APP_FLUX, appInfo.fluxSize);
			String whereClause = FluxDB.DB_APP_NAME + "=?";
			String[] whereArgs = new String[] { appInfo.appPackageName };
			db.update(FluxDB.DB_TATLE, mValues, whereClause, whereArgs);
		}
		db.close();
	}

	/**
	 * 获取流量信息
	 * 
	 * @return 流量数据,-1代表数据库没有数据
	 */
	public long query(String packageName) {
		SQLiteDatabase db = fluxDb.getWritableDatabase();
		String selection = FluxDB.DB_APP_NAME + "=?";
		String[] selectionArgs = new String[] { packageName };
		Cursor cursor = db.query(FluxDB.DB_TATLE,
				new String[] { FluxDB.DB_APP_FLUX }, selection, selectionArgs,
				null, null, null);

		long flux = -1;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				flux = cursor.getLong(0);
			}
		}
		cursor.close();
		db.close();

		return flux;
	}


}
