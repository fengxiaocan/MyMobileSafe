package com.evil.mobilesafe.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;

import com.evil.mobilesafe.domain.ContactsInfo;

public class QueryContactsUtils {
	public static List<ContactsInfo> queryContacts(Context context) {
		List<ContactsInfo> list = new ArrayList<ContactsInfo>();

		// 可以根据Android提供的api来获取联系人信息的数据库的uri
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		// 获取联系人的内容提供者
		ContentResolver resolver = context.getContentResolver();
		String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER,// 电话号码
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, // 姓名
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID // 联系人id
		};
		// 查询联系人数据库
		Cursor cursor = resolver.query(uri, projection, null, null, null);

		while (cursor.moveToNext()) {
			ContactsInfo bean = new ContactsInfo();
			bean.phone = cursor.getString(0);
			bean.name = cursor.getString(1);
			long id = cursor.getLong(2);

			bean.bitmap = getIcon(context, id);
			list.add(bean);
		}

		return list;
	}

	/**
	 * 根据联系人id来获取头像
	 */
	public static Bitmap getIcon(Context context, long id) {
		// 获取内容提供者
		ContentResolver resolver = context.getContentResolver();
		// 拼接联系人URi地址
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
				id + "");
		// 利用api来获取图片的输入流
		InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(
				resolver, uri);

		return BitmapFactory.decodeStream(in);
	}

	/**
	 * 获取通话记录
	 * @return 
	 */
	public static List<ContactsInfo> getCallLog(Context context) {
		List<ContactsInfo> list = new ArrayList<ContactsInfo>();
		
		// 获取内容提供者
		ContentResolver resolver = context.getContentResolver();
		// 可以根据Android提供的api来获取联系人信息的数据库的uri
		Uri uri = Uri.parse("content://call_log/calls");
		
		Cursor cursor = resolver.query(uri, new String[]{"number","date","type"}, null, null, null);
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				ContactsInfo info = new ContactsInfo();
				info.phone = cursor.getString(0);
				info.date = cursor.getLong(1);
				info.type = cursor.getInt(2);
				
				list.add(info);
			}
		}
		cursor.close();
		
		return list;
	}
}
