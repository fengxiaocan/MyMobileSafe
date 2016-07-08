package com.evil.mobilesafe.service;

import java.util.List;

import com.evil.mobilesafe.activity.AppLockPasswordActivity;
import com.evil.mobilesafe.dao.AppLockDao;
import com.evil.mobilesafe.utils.PrintLog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * 电子狗服务1线程版
 */
public class ApplockDogService extends Service {

	private AppLockDao mDao;
	private List<String> mLockAppInfo;
	private boolean isOpen = true; // 是否开始监听软件是否打开
	private ActivityManager mAm;
	private PackageManager mPm;
	private String mFriend = ""; // 熟人不拦截
	private FriendReceiver mFriendReceiver;
	private LockScreenReceiver mLockReceiver;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		PrintLog.log("电子狗服务开启");
		// 监听打开的每一个窗体

		mDao = new AppLockDao(this);
		// 获取加锁了的程序包
		mLockAppInfo = mDao.getLockAppInfo();
		// 获取活动管理器
		mAm = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		// 获取包管理器
		mPm = getPackageManager();

		//注册组件
		registerModule();

		// 开启看门狗
		startLockDoor();
	}

	/**
	 * 注册组件
	 */
	private void registerModule() {
		// 注册熟人广播
		mFriendReceiver = new FriendReceiver();
		IntentFilter filter = new IntentFilter("evil.lockdog");
		registerReceiver(mFriendReceiver, filter);

		// 注册锁屏监听
		mLockReceiver = new LockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mLockReceiver, lockFilter);

		// 内容观察者监听数据库是否发生变化
		ContentObserver observer = new ContentObserver(new Handler()) {

			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				// 数据库发生改变
				// 重新获取加锁了的程序包
				mLockAppInfo = mDao.getLockAppInfo();
			}
		};

		Uri LOCK_URI = Uri.parse("content://com.evil.lock/t_lock");

		// 注册内容观察者
		getContentResolver().registerContentObserver(LOCK_URI, false, observer);
	}

	private void startLockDoor() {
		new Thread() {
			public void run() {
				while (isOpen) {
					// 每隔200毫秒监听是否有打开的软件
					SystemClock.sleep(200);

					List<RunningTaskInfo> tasks = mAm.getRunningTasks(1);// 获取一个最顶端的任务栈
					RunningTaskInfo taskInfo = tasks.get(0);
					// 获取到包名
					String packageName = taskInfo.topActivity.getPackageName();

					if (mLockAppInfo.contains(packageName)) {
						// 加锁的应用
						// 判断是否是熟人
						if (mFriend.equals(packageName)) { // 不包含熟人则看门狗口令界面
							continue;
						}

						// 假如是熟人不拦截
						// 开启关门狗界面
						Intent intent = new Intent(getApplicationContext(),
								AppLockPasswordActivity.class);
						// 在服务中开启另外一个界面,必须要添加到一个新的任务栈中
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("packageName", packageName); // 把包名传递过去
						startActivity(intent);
					} else {
						// 未加锁的应用
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		PrintLog.log("电子狗服务关闭");
		isOpen = false; // 关闭监控
		// 注销熟人广播
		unregisterReceiver(mFriendReceiver);
		// 注销锁屏监听
		unregisterReceiver(mLockReceiver);
		super.onDestroy();
	}

	// 接收熟人广播
	class FriendReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			mFriend = intent.getStringExtra("friend"); // 接收熟人数据
			PrintLog.log("friend:" + mFriend);
		}
	}

	// 锁屏监听广播
	class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 锁屏时,清空熟人
			mFriend = "";
			PrintLog.log("锁屏执行了");
		}
	}

}
