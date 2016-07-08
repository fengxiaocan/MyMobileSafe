/**
 * 
 */
package com.evil.mobilesafe;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.Intent;
import android.os.Process;

import com.evil.mobilesafe.interfaces.MyContext;
import com.evil.mobilesafe.utils.PrintLog;

/**
 * @author 风小灿
 * @time 2016
 */
public class MyApplication extends Application {

	// 程序最先运行的
	@Override
	public void onCreate() {
		super.onCreate();

		PrintLog.log("程序运行了");
		// 初始化程序上下文
		MyContext.mContext = getApplicationContext();

		// 捕捉所有未捕获的异常
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				PrintLog.log(ex.getMessage());
				// 收集产生的异常信息
				try {
					FileWriter fw = new FileWriter(new File(
							getExternalCacheDir(), "error.log"),true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(ex.getMessage());
					bw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 重新打开我们的app
				Intent intent = getPackageManager().getLaunchIntentForPackage(
						getPackageName());
				startActivity(intent);

				// 杀掉自身进程
				Process.killProcess(Process.myPid());

			}
		});

	}
}
