package com.evil.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;
import com.evil.mobilesafe.domain.CommonNumberInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	private Context mContext;
	private SQLiteDatabase mDb;

	public CommonNumberDao(Context context) {
		mContext = context;
	}

	/**
	 * 查询组的标题
	 * 
	 * @return
	 */
	public List<CommonNumberInfo> queryGroup() {
		List<CommonNumberInfo> groupList = new ArrayList<CommonNumberInfo>();

		mDb = SQLiteDatabase.openDatabase(mContext.getFilesDir()
				+ "/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

		String[] columns = { "idx", "name" };

		Cursor cursor = mDb.query("classlist", columns, null, null, null, null,
				null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				CommonNumberInfo groupInfo = new CommonNumberInfo();

				String idx = cursor.getString(0);
				String name = cursor.getString(1);

				groupInfo.idx = idx;
				groupInfo.name = name;

				groupList.add(groupInfo);
			}
		}

		return groupList;
	}

	/**
	 * 查询组内的成员 子标签
	 * 
	 * @return
	 */
	public List<CommonNumberInfo> queryChild(String idx) {
		List<CommonNumberInfo> childList = new ArrayList<CommonNumberInfo>();

		mDb = SQLiteDatabase.openDatabase(mContext.getFilesDir()
				+ "/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

		String[] columns = { "number", "name" };

		Cursor cursor = mDb.query("table"+idx, columns, null, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				CommonNumberInfo childInfo = new CommonNumberInfo();

				String number = cursor.getString(0);
				String name = cursor.getString(1);

				childInfo.number = number;
				childInfo.name = name;

				childList.add(childInfo);
			}
		}

		return childList;
	}
}
