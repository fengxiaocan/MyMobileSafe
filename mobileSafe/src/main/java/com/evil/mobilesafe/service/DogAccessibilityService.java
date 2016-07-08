package com.evil.mobilesafe.service;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;

import com.evil.mobilesafe.activity.AppLockPasswordActivity;
import com.evil.mobilesafe.dao.AppLockDao;
import com.evil.mobilesafe.service.ApplockDogService.FriendReceiver;
import com.evil.mobilesafe.service.ApplockDogService.LockScreenReceiver;
import com.evil.mobilesafe.utils.PrintLog;

/**
 * 
 * 程序锁看门狗
 * 
 * @author 风小灿
 * 
 */
public class DogAccessibilityService extends AccessibilityService {

	private AppLockDao mDao;
	private List<String> mLockAppInfo;
	private boolean isOpen = true; // 是否开始监听软件是否打开
	private ActivityManager mAm;
	private PackageManager mPm;
	private String mFriend = ""; // 熟人不拦截
	private FriendReceiver mFriendReceiver;
	private LockScreenReceiver mLockReceiver;

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

		// 注册组件
		registerModule();

	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// 核心监控事件的回调

		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			// 窗口状态发生变化
			PrintLog.log("窗口发生变化");
			// 看门狗监控
			startLockDoor();
		}
	}

	@Override
	public void onInterrupt() {
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

		List<RunningTaskInfo> tasks = mAm.getRunningTasks(1);
		RunningTaskInfo taskInfo = tasks.get(0);
		// 获取到包名
		String packageName = taskInfo.topActivity.getPackageName();

		if (mLockAppInfo.contains(packageName)) {
			// 加锁的应用
			// 判断是否是熟人
			if (mFriend.equals(packageName)) { // 不包含熟人则看门狗口令界面
				return;
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
			// 未加锁的应用跳过
		}

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
