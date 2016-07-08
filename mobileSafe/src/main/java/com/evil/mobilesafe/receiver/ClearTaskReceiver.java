package com.evil.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.widget.Toast;

import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.utils.ActivityUtils;
import com.evil.mobilesafe.utils.TaskManagerUtils;

/**
 * 清理进程的广播
 */
public class ClearTaskReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取未清理之前的内存
		long startMemory = TaskManagerUtils.getAvailMemorySize(context);
		// 获取要清理的进程集合
		List<AppInfo> progress = TaskManagerUtils.getProgress(context);
		for (AppInfo appInfo : progress) {
			ActivityUtils.killApp(context, appInfo.appPackageName);
		}
		// 获取清理进程之后的集合
		List<AppInfo> list = TaskManagerUtils.getProgress(context);
		// 获取清理之后的内存
		long endMemory = TaskManagerUtils.getAvailMemorySize(context);

		// 清理的内存量
		String fileSize = Formatter.formatFileSize(context, endMemory
				- startMemory);

		Toast.makeText(
				context,
				"清理了" + (progress.size() - list.size()) + "个进程,节约" + fileSize
						+ "内存", Toast.LENGTH_SHORT).show();
	}

}
