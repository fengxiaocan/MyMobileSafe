package com.evil.mobilesafe.provider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.RemoteViews;

import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.service.MyAppWidgetService;
import com.evil.mobilesafe.utils.PrintLog;
import com.evil.mobilesafe.utils.TaskManagerUtils;

public class MyAppWidgetProvider extends AppWidgetProvider {

	
	private Timer mTimer;

	//只在第一次创建的时候才调用
	@Override
	public void onEnabled(final Context context) {
		Intent intent = new Intent(context,MyAppWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	//只在最后一次销毁的时候调用
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context,MyAppWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

}
