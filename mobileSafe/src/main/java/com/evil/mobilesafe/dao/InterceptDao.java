package com.evil.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import com.evil.mobilesafe.db.InterceptDB;
import com.evil.mobilesafe.domain.InterceptInfo;
import com.evil.mobilesafe.interfaces.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class InterceptDao {

	private InterceptDB mDb;
	private final Context mContext;

	public InterceptDao(Context context) {
		mContext = context;
		if (mDb == null) {
			mDb = new InterceptDB(context);
		}
	}

	/**
	 * 数据库添加数据方法
	 */
	public boolean add(InterceptInfo info) {
		SQLiteDatabase db = mDb.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(Constants.DB_PHONE, info.phone);

		values.put(Constants.DB_TYPE, info.type);

		values.put(Constants.DB_TYPE_DESC, info.desc);

		
		long insert = db.insert(Constants.DB_TABLE, null, values);
		
		if (insert == -1) {
			db.close();

			return false;
		}
		
		

		
		return true;
	}

	/**
	 * 数据库删除数据方法
	 */
	public void delete(String phone) {
		SQLiteDatabase db = mDb.getWritableDatabase();

		String whereClause = Constants.DB_PHONE + "=?";

		String[] whereArgs = { phone };

		db.delete(Constants.DB_TABLE, whereClause, whereArgs);

		db.close();
	}

	/**
	 * 数据库更新数据方法
	 */
	public void update(InterceptInfo info) {
		SQLiteDatabase db = mDb.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(Constants.DB_TYPE, info.type);

		values.put(Constants.DB_TYPE_DESC, info.desc);

		String whereClause = Constants.DB_PHONE + "=?";

		String[] whereArgs = { info.phone };

		db.update(Constants.DB_TABLE, values, whereClause, whereArgs);

		db.close();
	}

	/**
	 * @pageSize 查询的数据条数
	 * @index 查询的起始位置
	 * 查询从index开始的pageSize条数据
	 */
	public List<InterceptInfo> query(int pageSize,int index) {
		List<InterceptInfo> list = null;

		SQLiteDatabase db = mDb.getWritableDatabase();

		//根据这个表达式,可以查询从index开始的pageSize条数据
		String sql = "select * from "+Constants.DB_TABLE+" limit "+pageSize + " offset "+index;
		
		String[] selectionArgs = null;
		
		Cursor cursor = db.rawQuery(sql, selectionArgs);

		if (cursor != null) {
			list = new ArrayList<InterceptInfo>();
			while (cursor.moveToNext()) {
				
				InterceptInfo info = new InterceptInfo();

				String phone = cursor.getString(cursor
						.getColumnIndex(Constants.DB_PHONE));
				int type = cursor.getInt(cursor
						.getColumnIndex(Constants.DB_TYPE));
				String desc = cursor.getString(cursor
						.getColumnIndex(Constants.DB_TYPE_DESC));

				info.phone = phone;
				info.type = type;
				info.desc = desc;

				list.add(info); // 最近更新的数据显示在最前面
			}
			cursor.close();
		}

		db.close();

		return list;
	}
	
	/**
	 * 根据号码查询拦截方式
	 */
	public int queryType(String number){
		SQLiteDatabase db = mDb.getWritableDatabase();

		String[] columns = {Constants.DB_TYPE};
		String selection = Constants.DB_PHONE+"=?";
		String[] selectionArgs = {number};
		Cursor cursor = db.query(Constants.DB_TABLE, columns, selection, selectionArgs, null, null, null);
		int type = -1;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				type = cursor.getInt(0);
			}
			cursor.close();
		}
		
		db.close();
		
		return type;
	}
}
