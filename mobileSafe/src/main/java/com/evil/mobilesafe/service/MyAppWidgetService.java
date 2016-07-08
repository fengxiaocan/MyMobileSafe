package com.evil.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.provider.MyAppWidgetProvider;
import com.evil.mobilesafe.utils.TaskManagerUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.text.format.Formatter;
import android.view.View;
import android.widget.RemoteViews;

public class MyAppWidgetService extends Service {

	private Timer mTimer;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		mTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 更新数据
				// 获取可用的内存大小
				long availMemorySize = TaskManagerUtils
						.getAvailMemorySize(MyAppWidgetService.this);
				// 获取正在运行的进程
				List<AppInfo> progress = TaskManagerUtils
						.getProgress(MyAppWidgetService.this);
				int runningProgress = progress.size();

				// 获取appWidget管理器
				AppWidgetManager awm = AppWidgetManager
						.getInstance(MyAppWidgetService.this);
				// 指向widget
				ComponentName provider = new ComponentName(
						MyAppWidgetService.this, MyAppWidgetProvider.class);
				// 获取布局文件
				RemoteViews views = new RemoteViews(
						MyAppWidgetService.this.getPackageName(),
						R.layout.process_widget);

				// 给指定控件设置数据
				views.setTextViewText(R.id.tv_widget_process_count, "正在运行的进程:"
						+ runningProgress + "个");

				// 设置内存信息
				views.setTextViewText(
						R.id.tv_widget_process_memory,
						"可用内存:"
								+ Formatter.formatFileSize(
										getApplicationContext(),
										availMemorySize));
				// 必须要是在清单文件中注册的广播
				Intent intent = new Intent("com.evil.cleartask");

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						MyAppWidgetService.this, 110, intent, 0);
				// 设置按钮点击事件,把点击事件发送给广播,接受到广播之后清理进程
				views.setOnClickPendingIntent(R.id.btn_widget_clear,
						pendingIntent);
				// 更新数据
				awm.updateAppWidget(provider, views);
			}
		};
		/**
		 * 第一个参数,定时任务 第二个参数,任务开启时间 第三个参数,任务每隔多长时间就执行一次
		 */
		mTimer.schedule(task, 0, 5000);

	}

	@Override
	public void onDestroy() {
		// 关闭任务定时器
		mTimer.cancel();
		mTimer = null;
		super.onDestroy();
	}
}
