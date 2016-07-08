package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import android.app.ProgressDialog;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.AppLockDao;
import com.evil.mobilesafe.db.AppLockDB;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.fragment.BaseAppLockFragment;
import com.evil.mobilesafe.fragment.LeftAppLockFragment;
import com.evil.mobilesafe.fragment.RightAppLockFragment;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.view.TabAppLockView;
import com.evil.mobilesafe.view.TabAppLockView.OnLockChangeLinstener;

//程序锁界面
public class AppLockActivity extends FragmentActivity {
	private TabAppLockView mLockView;
	private BaseAppLockFragment mRightAppLockFragment;
	private BaseAppLockFragment mLeftAppLockFragment;
	private List<AppInfo> mAppUnlockInfo; // 未加锁的app
	private List<AppInfo> mAppLockInfo = new ArrayList<AppInfo>(); // 加锁的app
	private AppLockDao mDao;
	private ProgressDialog mDialog;
	private boolean isLeftSelect = true;
	private List<String> mAppLock;
	private CopyOnWriteArrayList<AppInfo> mCopyInfo;
	private boolean isFirstGetData = true; // 是否是界面可见时第一次获取数据


	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		initFragment();
		// 初始化数据
		initData();
		// 注册内容观察者
		registerContentOb();

	}

	void initFragment() {
		mLeftAppLockFragment = new LeftAppLockFragment();
		mRightAppLockFragment = new RightAppLockFragment();

	}

	// 设置fragment
	private void setFragment(List<AppInfo> list, boolean isLeftTab) {
		// fragment的替换显示
		// 1. fragment管理器
		FragmentManager fm = this.getSupportFragmentManager();
		// 2.开始事务
		FragmentTransaction transaction = fm.beginTransaction();
		// 3.开始替换
		if (isLeftTab) {
			transaction.replace(R.id.fl_applock, mLeftAppLockFragment);
			mLeftAppLockFragment.setData(list, mDao);
		} else {
			transaction.replace(R.id.fl_applock, mRightAppLockFragment);
			mRightAppLockFragment.setData(list, mDao);
		}

		// 4.提交事务
		transaction.commit();
	}

	private void initView() {
		setContentView(R.layout.activity_applock);
		// 查找标签控件
		mLockView = (TabAppLockView) findViewById(R.id.talv_applock_tab);

		// 初始化数据库工具
		mDao = new AppLockDao(this);
	}

	private void initListener() {
		// 设置监听事件,把处理反馈给控件本身处理,自身等待处理结果
		mLockView.setOnLockChangeLinstener(new OnLockChangeLinstener() {

			@Override
			public void onChanged(boolean isLeftTab) {
				isLeftSelect = isLeftTab;
				if (isLeftSelect) {
					// 是左边的未加锁标签
					setFragment(mAppUnlockInfo, true);
				} else {
					// 是右边的以加锁标签
					setFragment(mAppLockInfo, false);
				}
			}
		});
	}

	// 注册内容观察者
	private void registerContentOb() {
		ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				new Thread() {
					public void run() {
						// 获取数据
						getData();
					};
				}.start();
			}
		};
		// 内容观察者
		getContentResolver().registerContentObserver(AppLockDB.LOCK_URI, false,
				observer);
	}

	private void initData() {
		// 显示加载进度框
		mDialog = ProgressDialog.show(this, "提示", "正在加载数据中......");

		// 获取数据
		getAppInfoData();
	}

	// 获取数据
	private void getAppInfoData() {
		new Thread() {
			public void run() {
				// 获取数据
				getData();

				SystemClock.sleep(1000);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
						// 给Fragment设置数据
						setFragment(mAppUnlockInfo, isLeftSelect); // 默认显示未加锁状态
					}
				});
			};
		}.start();
	}

	// 获取数据,必须放在子线程中
	private void getData() {
		synchronized (AppLockActivity.class) {

			// 判断界面可见时是否是第一次获取数据,防止重复获取数据,提高速度
			if (isFirstGetData || mCopyInfo == null) {
				
				
				// 获取所有已安装的应用程序
				mAppUnlockInfo = AppManagerUtils
						.getAppInfo(getApplicationContext());

				// 复制到另外一个数组中
				mCopyInfo = new CopyOnWriteArrayList<AppInfo>(mAppUnlockInfo);

				isFirstGetData = false; // 第一次获取数据之后置为false
			}

			mAppUnlockInfo.clear(); // 清空未加锁的集合
			mAppLockInfo.clear(); // 清空已加锁的集合

			// 获取已加锁的app包名
			mAppLock = mDao.getLockAppInfo();

			// 判断是否是加锁的
			for (AppInfo info : mCopyInfo) {
				if (mAppLock.contains(info.appPackageName)) {
					mAppLockInfo.add(info); // 加锁的包名
				} else {
					mAppUnlockInfo.add(info); // 添加未加锁的
				}
			}

			// 重新排序
			Collections.sort(mAppUnlockInfo, new Comparator<AppInfo>() {
				@Override
				public int compare(AppInfo lhs, AppInfo rhs) {
					if (rhs.isSystem) {
						return -1;
					} else {
						return 1;
					}
				}
			});

			// 重新排序
			Collections.sort(mAppLockInfo, new Comparator<AppInfo>() {
				@Override
				public int compare(AppInfo lhs, AppInfo rhs) {
					if (rhs.isSystem) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			mAppLock = null; // 获取完成清空数据

		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		isFirstGetData = true; // 界面重新可见时,置为true
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}
