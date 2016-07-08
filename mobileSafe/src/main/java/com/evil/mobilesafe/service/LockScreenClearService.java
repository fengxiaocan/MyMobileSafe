package com.evil.mobilesafe.service;

import java.util.List;
import java.util.Random;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.utils.ActivityUtils;
import com.evil.mobilesafe.utils.PrintLog;
import com.evil.mobilesafe.utils.TaskManagerUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenClearService extends Service {

	private LockScreenClearReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 把服务的优先级提升为前台级别

		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher, "小灿手机卫士",
				System.currentTimeMillis());
		Intent intent = new Intent("com.evil.mobilesafe.home");
		// 将要跳转的意图
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 11, intent, 11);
		// 通知显示
		notification.setLatestEventInfo(getApplicationContext(), "手机卫士",
				"小灿超级手机卫士,守护您的手机安全", contentIntent);
		// 提供服务级别
		startForeground(11, notification);

		mReceiver = new LockScreenClearReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

		registerReceiver(mReceiver, filter);
	}

	// 锁屏监听广播
	class LockScreenClearReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			List<AppInfo> list = TaskManagerUtils.getProgress(context);
			for (AppInfo appInfo : list) {
				// 清理进程
				ActivityUtils.killApp(context, appInfo.appPackageName);
			}
			PrintLog.log("锁屏进程清理完成!");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 注销监听锁屏
		unregisterReceiver(mReceiver);
	}

}
