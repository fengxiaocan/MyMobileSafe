package com.evil.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.evil.mobilesafe.domain.SmsInfo;
import com.evil.mobilesafe.interfaces.MyContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * @author 风小灿
 */
public class SmsUtils {
	private static Uri smsUri = Uri.parse("content://sms");
	private final static int mSeed = 12; // 加密密码
	private static int mProgress;

	/**
	 * 进度条监听器 所有的进程都在主线程中完成
	 * 
	 * @author 风小灿
	 * @date 2016-6-19
	 */
	public interface OnProgressListener {
		/**
		 * 设置进度条显示
		 */
		void show();

		/**
		 * 设置进度条最大值
		 * 
		 * @param max
		 *            最大值
		 */
		void setMax(int max);

		/**
		 * 设置进度条
		 * 
		 * @param progress
		 *            进度
		 */
		void setProgress(int progress);

		/**
		 * 设置进度条关闭
		 */
		void setClose();

	}

	/**
	 * 短信备份 只能在子线程中运行
	 */
	private static void smsBackupsThread(final Activity activity,
			final OnProgressListener onProgressListener) {

		// 获取所有的信息
		List<SmsInfo> allSms = getAllSms(activity, mSeed);

		if (allSms == null || allSms.size() == 0) {
			return;
		}

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 进度条的显示
				onProgressListener.show();
			}
		});
		// 设置最大进度
		onProgressListener.setMax(allSms.size());

		// 转换成json
		Gson gson = new Gson();
		String json = gson.toJson(allSms);

		// 加密短信
		String encode = encode(json, 3);
		for (int i = 0; i < allSms.size(); i++) {
			mProgress = i;
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onProgressListener.setProgress(mProgress);
				}
			});
			// 休眠300毫秒
			SystemClock.sleep(300);
		}

		// 把加密的信息保存
		File filesDir = activity.getFilesDir();
		File sms = new File(filesDir, "mms");
		try {
			// 保存
			BufferedWriter bw = new BufferedWriter(new FileWriter(sms));
			bw.write(encode);
			bw.close();

			// 关闭进度条
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onProgressListener.setClose();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 短信备份
	 * 
	 * @param activity
	 * @param listener
	 *            实现进度条的监听器
	 */
	public static void smsBackups(final Activity activity,
			final OnProgressListener listener) {
		new Thread() {
			public void run() {
				smsBackupsThread(activity, listener);
			};
		}.start();
	}

	/**
	 * 短信还原 只能在子线程中跑
	 * 
	 * @param activity
	 * @param listener
	 */
	private static void smsReseThread(Activity activity,
			final OnProgressListener listener) {
		File filesDir = activity.getFilesDir();
		File sms = new File(filesDir, "mms");
		if (!sms.exists() || sms.length() == 0) {
			return;
		}

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 显示进度条
				listener.show();
				// 设置进度的标题
			}
		});
		try {
			BufferedReader br = new BufferedReader(new FileReader(sms));
			String line = br.readLine();
			// 解密信息
			String json = encode(line, 3);

			// 把json还原成集合
			Gson gson = new Gson();
			List<SmsInfo> allSms = gson.fromJson(json,
					new TypeToken<List<SmsInfo>>() {
					}.getType());
			// 设置最大进度
			listener.setMax(allSms.size());

			insertSms(activity, allSms, mSeed);

			for (int i = 0; i < allSms.size(); i++) {
				mProgress = i + 1;

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.setProgress(mProgress);
					}
				});
				SystemClock.sleep(300);
			}

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.setClose();
				}
			});

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void smsRese(final Activity activity,
			final OnProgressListener listener) {
		new Thread() {
			public void run() {
				smsReseThread(activity, listener);
			};
		}.start();
	}

	/**
	 * 短信备份
	 */
	public static void smsBackups(Context context) {
		PrintLog.log("开始备份");
		// 获取所有的信息
		List<SmsInfo> allSms = getAllSms(context, mSeed);

		if (allSms == null || allSms.size() == 0) {
			return;
		}

		// 转换成json
		Gson gson = new Gson();
		String json = gson.toJson(allSms);

		String encode = encode(json, 3);
		// 把加密的信息保存
		File filesDir = context.getFilesDir();
		File sms = new File(filesDir, "mms");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(sms));
			bw.write(encode);
			bw.close();
			PrintLog.log("备份成功");
			Toast.makeText(MyContext.mContext, "短信备份成功", 0).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 短信还原
	 */
	public static void smsRese(Context context) {
		File filesDir = context.getFilesDir();
		File sms = new File(filesDir, "mms");
		if (!sms.exists() || sms.length() == 0) {
			return;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(sms));
			String line = br.readLine();
			// 还原信息
			String json = encode(line, 3);
			PrintLog.log(json);
			// 把json还原成集合
			Gson gson = new Gson();
			List<SmsInfo> allSms = gson.fromJson(json,
					new TypeToken<List<SmsInfo>>() {
					}.getType());
			insertSms(context, allSms, mSeed);
			PrintLog.log("还原成功");
			Toast.makeText(MyContext.mContext, "短信还原成功", 0).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有的短信
	 * 
	 * @return smsInfo 信息的bean
	 * @seed 加密的信息的密码,防止有特殊字符
	 */
	public static List<SmsInfo> getAllSms(Context context, int seed) {
		List<SmsInfo> list = null;

		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(smsUri, new String[] { "address",
				"date", "date_sent", "type", "body" }, null, null, null);
		if (cursor != null) {
			list = new ArrayList<SmsInfo>();
			while (cursor.moveToNext()) {
				SmsInfo info = new SmsInfo();
				info.address = cursor.getString(0);
				info.date = cursor.getLong(1);
				info.date_sent = cursor.getString(2);
				info.type = cursor.getInt(3);
				info.body = encode(cursor.getString(4), seed);

				list.add(info);
			}
		}

		cursor.close();
		return list;
	}

	/**
	 * 对外提供的获取所有信息
	 * 
	 * @return
	 */
	public static List<SmsInfo> getAllSms(Context context) {
		return getAllSms(context, 0);
	}

	/**
	 * 删除所有的信息
	 */
	public static void deleteAllSms() {
		ContentResolver resolver = MyContext.mContext.getContentResolver();
		int delete = resolver.delete(smsUri, null, null);
	}

	/**
	 * 插入单条信息
	 * 
	 * @param smsInfo
	 *            要插入的信息的封装类
	 * @seed 加密的信息的解密密码
	 * 
	 */
	private static void insertSms(Context context, SmsInfo smsInfo, int seed) {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();

		values.put("address", smsInfo.address);
		values.put("date", smsInfo.date);
		values.put("date_sent", smsInfo.date_sent);
		values.put("type", smsInfo.type);
		values.put("body", encode(smsInfo.body, seed));

		resolver.insert(smsUri, values);
	}

	/**
	 * 编辑系统信息 插入单条信息 没有加密的信息
	 * 
	 * @param smsInfo
	 *            要插入的信息的封装类
	 */
	public static void insertSms(Context context, SmsInfo smsInfo) {
		insertSms(context, smsInfo, 0);
	}

	/**
	 * 对外提供的 插入多条信息
	 * 
	 * @param info要插入的信息封装类的list集合
	 */
	public static void insertSms(Context context, List<SmsInfo> info) {
		ContentResolver resolver = MyContext.mContext.getContentResolver();
		for (SmsInfo smsInfo : info) {
			insertSms(context, smsInfo);
		}
	}

	/**
	 * 插入多条信息
	 * 
	 * @param info
	 *            要插入的信息封装类的list集合
	 * @param seed
	 *            解密密码
	 */
	private static void insertSms(Context context, List<SmsInfo> info, int seed) {
		ContentResolver resolver = context.getContentResolver();
		for (SmsInfo smsInfo : info) {
			insertSms(context, smsInfo, seed);
		}
	}

	/**
	 * 把字符串加密
	 * 
	 * @param code
	 *            要加密的信息
	 * @return 还原后的信息
	 */
	public static String encode(String code) {
		int seed = 3; // 加密密码
		byte[] bytes = code.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] ^= seed;
		}
		String string = new String(bytes);
		return string;
	}

	/**
	 * 解密加密的字符串
	 * 
	 * @param code
	 *            要加密的信息
	 * @return 还原后的信息
	 */
	public static String decode(String code) {
		return encode(code);
	}

	/**
	 * 把字符串加密
	 * 
	 * @param code
	 *            要加密的信息
	 * @return 还原后的信息
	 * @param seed
	 *            加密的密码
	 * 
	 */
	public static String encode(String code, int seed) {
		// 加密密码
		byte[] bytes = code.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] ^= seed;
		}
		String string = new String(bytes);
		return string;
	}

	/**
	 * 解密加密的字符串
	 * 
	 * @param code
	 *            要加密的信息
	 * @param seed
	 *            解密的密码
	 * @return 还原后的信息
	 */
	public static String decode(String code, int seed) {
		return encode(code, seed);
	}
}
