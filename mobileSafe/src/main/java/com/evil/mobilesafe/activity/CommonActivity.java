package com.evil.mobilesafe.activity;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.interfaces.MyContext;
import com.evil.mobilesafe.service.ApplockDogService;
import com.evil.mobilesafe.service.DogAccessibilityService;
import com.evil.mobilesafe.utils.ActivityUtils;
import com.evil.mobilesafe.utils.SmsUtils;
import com.evil.mobilesafe.utils.SmsUtils.OnProgressListener;
import com.evil.mobilesafe.utils.SpUtils;
import com.evil.mobilesafe.view.SettingItemView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

//常用工具界面
public class CommonActivity extends Activity {
	private SettingItemView mSivPhoneQuery;
	private SettingItemView mSivCommonNumberQuery;
	private SettingItemView mSivApplock;
	private SettingItemView mSivApplockDog1;
	private SettingItemView mSivApplockDog2;
	private boolean mRunningService;
	private boolean mOpenLockDog2 = false;
	private SettingItemView mSivSmsCopy;
	private SettingItemView mSivSmsRestore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_common);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

	}

	@Override
	protected void onStart() {
		// 初始化数据
		initData();
		super.onStart();
	}

	private void initView() {
		// 号码归属地查询
		mSivPhoneQuery = (SettingItemView) findViewById(R.id.siv_common_belong_query);
		// 常用号码查询
		mSivCommonNumberQuery = (SettingItemView) findViewById(R.id.siv_common_number_query);
		// 短信备份
		mSivSmsCopy = (SettingItemView) findViewById(R.id.siv_common_sms_copy);
		// 短信还原
		mSivSmsRestore = (SettingItemView) findViewById(R.id.siv_common_sms_restore);
		// 程序锁界面
		mSivApplock = (SettingItemView) findViewById(R.id.siv_common_app_lock);
		// 电子狗服务1
		mSivApplockDog1 = (SettingItemView) findViewById(R.id.siv_common_open_dog1);
		// 电子狗服务2
		mSivApplockDog2 = (SettingItemView) findViewById(R.id.siv_common_open_dog2);

	}

	private void initListener() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 短信备份还原进度显示
				final ProgressDialog dialog = new ProgressDialog(
						CommonActivity.this);
				dialog.setMax(0);
				dialog.setProgress(0);
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				OnProgressListener listener = new SmsUtils.OnProgressListener() {
					@Override
					public void show() {
						dialog.show();
					}

					@Override
					public void setProgress(int progress) {
						dialog.setProgress(progress);
					}

					@Override
					public void setMax(int max) {
						dialog.setMax(max);
					}

					@Override
					public void setClose() {
						dialog.dismiss();
					}

				};

				switch (v.getId()) {
				case R.id.siv_common_belong_query:
					// 跳转到号码归属地查询
					openActivity(BelongQueryActivity.class);
					break;
				case R.id.siv_common_number_query:
					// 跳转到常用号码查询
					openActivity(CommonNumberQueryActivity.class);
					break;
				case R.id.siv_common_sms_copy:
					// 短信备份
					SmsUtils.smsBackups(CommonActivity.this, listener);
					break;
				case R.id.siv_common_sms_restore:
					// 短信还原
					SmsUtils.smsRese(CommonActivity.this, listener);
					break;

				case R.id.siv_common_open_dog1:
					// 开启电子狗服务2
					Intent intent = new Intent(CommonActivity.this,
							ApplockDogService.class);
					if (mRunningService) {
						// 注销电子狗服务
						stopService(intent);
					} else {
						// 开启电子狗服务
						startService(intent);
					}
					mRunningService = !mRunningService;
					mSivApplockDog1.setSettingOnButton(mRunningService);

					break;

				case R.id.siv_common_open_dog2:

					/*
					 * <activity android:name="AccessibilitySettings"
					 * android:label="@string/accessibility_settings_title">
					 * <intent-filter> <action
					 * android:name="android.intent.action.MAIN" /> <action
					 * android:name="android.settings.ACCESSIBILITY_SETTINGS" />
					 * <!-- Wtf... this action is bogus! Can we remove it? -->
					 * <action android:name="ACCESSIBILITY_FEEDBACK_SETTINGS" />
					 * <category android:name="android.intent.category.DEFAULT"
					 * /> <category
					 * android:name="android.intent.category.VOICE_LAUNCH" />
					 * </intent-filter> </activity>
					 */
					// 跳转到辅助设置界面
					Intent intents = new Intent(
							"android.settings.ACCESSIBILITY_SETTINGS");
					/* intents.setAction("ACCESSIBILITY_FEEDBACK_SETTINGS"); */
					intents.addCategory("android.intent.category.DEFAULT");
					intents.addCategory("android.intent.category.VOICE_LAUNCH");
					startActivity(intents);
					break;
				case R.id.siv_common_app_lock:
					String info = SpUtils.getInfo(getApplicationContext(),
							Constants.APP_LOCK_PASSWORD, "");

					if (TextUtils.isEmpty(info)) {// 没有密码
						openActivity(SetAppLockPasswordActivity.class); // 跳转到设置程序锁密码的界面
					} else {
						// 跳转到程序锁界面
						openActivity(AppLockActivity.class);
					}
					break;

				default:
					break;
				}
			}
		};
		// 号码归属地查询
		mSivPhoneQuery.setOnClickListener(clickListener);
		// 常用号码查询
		mSivCommonNumberQuery.setOnClickListener(clickListener);
		// 短信备份
		mSivSmsCopy.setOnClickListener(clickListener);
		// 短信还原
		mSivSmsRestore.setOnClickListener(clickListener);
		// 程序锁
		mSivApplock.setOnClickListener(clickListener);
		// 电子狗服务1
		mSivApplockDog1.setOnClickListener(clickListener);
		// 电子狗服务2
		mSivApplockDog2.setOnClickListener(clickListener);
	}

	private void initData() {
		// 判断服务是否运行
		mOpenLockDog2 = ActivityUtils.isRunningService(this,
				DogAccessibilityService.class);

		mSivApplockDog2.setSettingOnButton(mOpenLockDog2);

		// 判断电子狗服务是否运行
		mRunningService = ActivityUtils.isRunningService(
				getApplicationContext(), ApplockDogService.class);
		// 根据是否开启来设置电子狗按钮
		if (mRunningService) {
			mSivApplockDog1.setSettingOnButton(true);
		} else {
			mSivApplockDog1.setSettingOnButton(false);
		}

	}

	/**
	 * 开启别的界面
	 */
	public void openActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(this, clazz);

		startActivity(intent);
	}
}
