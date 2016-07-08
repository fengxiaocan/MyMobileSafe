package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.Collections;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.utils.ActivityUtils;
import com.evil.mobilesafe.utils.PrintLog;
import com.zhy.zhy_gesturelockview.MainActivity;
import com.zhy.zhy_gesturelockview.view.GestureLockViewGroup;
import com.zhy.zhy_gesturelockview.view.GestureLockViewGroup.OnGestureLockViewListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 看门狗口令界面
 * 
 * @author Administrator
 * 
 */
public class AppLockPasswordActivity extends Activity {
	private String mPackageName;
	private HomeKeyReceiver mHomeKeyReceiver;
	private GestureLockViewGroup mGestureLockViewGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_applogdog);

		Intent intent = getIntent();
		// 获取从服务中取出来的包名
		mPackageName = intent.getStringExtra("packageName");

		// 注册监听Home键
		mHomeKeyReceiver = new HomeKeyReceiver();
		IntentFilter filter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mHomeKeyReceiver, filter);

		initListener();
	}

	/**
	 * 监听事件,输入手势密码
	 */
	private void initListener() {
		// 查找控件
		mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);

		// 获取保存好的密码
		SharedPreferences sp = getSharedPreferences("mobileSafe.xml",
				Context.MODE_PRIVATE);
		String password = sp.getString("GesturePassword", "1234");
		
		//切割字符串
		String[] split = password.split("--");
		int[] pass = new int[split.length];
		for (int i = 0; i < pass.length; i++) {
			pass[i] = Integer.parseInt(split[i]);
			PrintLog.log(pass[i]);
		}

		mGestureLockViewGroup.setAnswer(pass);
		mGestureLockViewGroup
				.setOnGestureLockViewListener(new OnGestureLockViewListener() {

					@Override
					public void onUnmatchedExceedBoundary() {
						// Toast.makeText(AppLockPasswordActivity.this,
						// "错误5次...",
						// Toast.LENGTH_SHORT).show();
						// mGestureLockViewGroup.setUnMatchExceedBoundary(5);
					}

					@Override
					public void onGestureEvent(boolean matched) {
						if (matched) {
							//密码正确
							// 告诉看门狗这是熟人
							Intent intent = new Intent("evil.lockdog");
							intent.putExtra("friend", mPackageName);
							sendBroadcast(intent);
							
							// 反注册
							unregisterReceiver(mHomeKeyReceiver);

							finish();
						} else {
							Toast.makeText(getApplicationContext(), "密码错误", 0)
									.show();
						}
					}

					@Override
					public void onBlockSelected(int cId) {
					}
				});
	}

	/**
	 * 按返回键的时候调用,直接回到主界面
	 */
	@Override
	public void onBackPressed() {
		// 关闭打开的程序
		ActivityUtils.killApp(this, mPackageName);

		// 反注册
		unregisterReceiver(mHomeKeyReceiver);

		// 回到主界面
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);

		PrintLog.log("返回主界面,解除home键监听,关闭程序");

		super.onBackPressed();
	}

	// 监听HOme键
	private class HomeKeyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 反注册
			unregisterReceiver(mHomeKeyReceiver);

			// 关闭打开的程序
			ActivityUtils.killApp(context, mPackageName);

			// 当按下home键的时候,关闭自身窗口
			finish();

			PrintLog.log("返回主界面,解除home键监听,关闭程序");
		}
	}

	@Override
	protected void onDestroy() {
		// 反注册
		// unregisterReceiver(mHomeKeyReceiver);
		super.onDestroy();
	}
}
