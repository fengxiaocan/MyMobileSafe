package com.evil.mobilesafe.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BelongQueryDao {
	private Context context;

	public BelongQueryDao(Context context) {
		this.context = context;
	}

	public String queryArea(String number) {
		if (number == null) {
			return "请输入手机号码";
		}

		String result = "未知号码";

		String path = context.getFilesDir() + "/address.db"; // 数据库文件地址

		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				Context.MODE_PRIVATE);

		String regular = "[1][358][0-9]{5,9}";

		// 查询手机号归属地
		if (number.matches(regular)) {
			String phone = number.substring(0, 7);

			Cursor cursor = db.query("info", new String[] { "cardtype" },
					"mobileprefix=?", new String[] { phone }, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					result = cursor.getString(0);
				}
			}

			cursor.close();
		}

		// 查询电话区号3位
		if (result.equals("未知号码") && number.length() >= 3
				&& number.length() <= 11) { // 查询区号
			String area = number.substring(0, 3);
			Cursor cursor = db.query("info", new String[] { "city" }, "area=?",
					new String[] { area }, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					result = cursor.getString(0);
				}
			}

			cursor.close();
		}

		// 电话区号
		if (result.equals("未知号码") && number.length() >= 4
				&& number.length() <= 12) { // 查询区号
			String area = number.substring(0, 4);
			Cursor cursor = db.query("info", new String[] { "city" }, "area=?",
					new String[] { area }, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					result = cursor.getString(0);
				}
			}
			cursor.close();
		}
		// 其他号码
		if (result.equals("未知号码") && number.length() >= 3
				&& number.length() <= 5) { // 查询区号
			switch (number.length()) {
			case 3:
				result = "紧急号码";
				break;
			case 4:
				result = "模拟号码";
				break;
			case 5:
				result = "服务号码";
				break;
			case 10:
			case 11:
			case 12:
				result = "境外号码,请注意诈骗";
				break;
			}
		}

		db.close();
		return result;
	}

}
