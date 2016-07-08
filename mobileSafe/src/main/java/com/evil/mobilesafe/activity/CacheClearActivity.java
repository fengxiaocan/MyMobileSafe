package com.evil.mobilesafe.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.IPackageStatsObserver.Stub;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.utils.PrintLog;

/**
 * 缓存清理界面
 */
public class CacheClearActivity extends Activity {

	protected static final int SCANNING = 1;// 正在扫描
	protected static final int FINISH = 2;// 扫描完成
	private static final int FINISH_CLEAR = 3;// 清理完成
	private ImageView mIvScanning;
	private ImageView mIvPoints;
	private PackageManager mPm;
	private List<AppInfo> mCacheApps = new ArrayList<AppInfo>(); // 装载有缓存的app
	private int count = 0; // 记录app查找缓存进度
	protected List<AppInfo> mAppInfo; // 获取所有的app
	private TextView mTvResult;
	private ImageView mIvClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		setContentView(R.layout.activity_cacheclear);
		// 雷达的图片
		mIvScanning = (ImageView) findViewById(R.id.iv_cacheclear_scanning);
		mIvPoints = (ImageView) findViewById(R.id.iv_cacheclear_points);

		// 正在扫描的包名
		mTvScanning = (TextView) findViewById(R.id.tv_cacheclear_scannningmess);
		// 扫描结果
		mTvResult = (TextView) findViewById(R.id.tv_cacheclear_result_mess);

		// 扫描数据显示
		mLlData = (LinearLayout) findViewById(R.id.ll_cacheclear_cachedatas);
		// 进度条
		mPb = (ProgressBar) findViewById(R.id.pb_cacheclear_progress);
		// 清理按钮
		mIvClear = (ImageView) findViewById(R.id.iv_cacheclear_clear);
	}

	private void initListener() {
		mIvClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearAllCache();
			}
		});
	}

	/**
	 * 
	 */
	protected void clearAllCache() {
		// 清理所有的缓存
		class ClearObserver extends IPackageDataObserver.Stub {
			@Override
			public void onRemoveCompleted(String packageName, boolean succeeded)
					throws RemoteException {
				PrintLog.log("清理缓存完成");
				handler.sendEmptyMessage(FINISH_CLEAR);
			}
		}
	}

	private void initData() {
		// 获取包管理器
		mPm = getPackageManager();

		// 开始扫描所有的app缓存信息
		startScanAllApps();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANNING: {
				// 扫描缓存中

				AppInfo appInfo = (AppInfo) msg.obj;
				mTvScanning.setText(appInfo.appName);

				mPb.setMax(mAppInfo.size());
				mPb.setProgress(appInfo.scanProgress);

				// 添加View显示扫描中的文件
				showDataScanningView(appInfo);

				break;
			}
			case FINISH:
				// 停止动画
				endAnimation();
				// 扫描完成
				if (mCacheApps.size() == 0) {

					mTvResult.setVisibility(View.VISIBLE);// 设置可见
					mLlData.setVisibility(View.GONE);// 设置不可见
				} else {

					for (int i = 0; i < mCacheApps.size(); i++) {
						// 显示获取到的有缓存的应用
						addShowDataView(i);
					}
				}
				break;

			case FINISH_CLEAR:
				Toast.makeText(getApplicationContext(), "清理缓存完成", 0).show();

				mTvResult.setVisibility(View.VISIBLE);// 设置可见
				break;
			default:
				break;
			}
		}

		/**
		 * 显示扫描中的应用
		 * 
		 * @param appInfo
		 */
		private void showDataScanningView(AppInfo appInfo) {
			View view = View.inflate(getApplicationContext(),
					R.layout.item_cacheclear_info, null);

			ImageView ivIcon = (ImageView) view
					.findViewById(R.id.iv_cacheclear_appicon);
			TextView tvName = (TextView) view
					.findViewById(R.id.tv_cacheclear_item_appname);
			TextView tvSize = (TextView) view
					.findViewById(R.id.tv_cacheclear_item_size);

			ivIcon.setImageDrawable(appInfo.appIcon);
			tvName.setText(appInfo.appName);
			tvSize.setText(Formatter.formatFileSize(getApplicationContext(),
					appInfo.cacheSize));

			mLlData.addView(view, 0);
		}

		/**
		 * 显示获取到的有缓存的应用
		 * 
		 * @param i
		 */
		private void addShowDataView(int i) {
			// 添加View显示扫描中的文件
			View view = View.inflate(getApplicationContext(),
					R.layout.item_cacheclear_info, null);

			ImageView ivIcon = (ImageView) view
					.findViewById(R.id.iv_cacheclear_appicon);
			TextView tvName = (TextView) view
					.findViewById(R.id.tv_cacheclear_item_appname);
			TextView tvSize = (TextView) view
					.findViewById(R.id.tv_cacheclear_item_size);

			ivIcon.setImageDrawable(mCacheApps.get(i).appIcon);
			tvName.setText(mCacheApps.get(i).appName);
			tvSize.setText(Formatter.formatFileSize(getApplicationContext(),
					mCacheApps.get(i).cacheSize));

			mLlData.addView(view, 0);
		};
	};
	private TextView mTvScanning;
	private LinearLayout mLlData;
	private ProgressBar mPb;

	/**
	 * 停止动画
	 */
	private void endAnimation() {
		mIvScanning.clearAnimation();
		mIvPoints.clearAnimation();// 清除动画
	}

	/**
	 * 开始遍历所有的app
	 */
	private void startScanAllApps() {
		mTvResult.setVisibility(View.GONE);// 设置可见
		startAnimation();
		new Thread() {
			public void run() {
				int progress = 0;
				// 获取所有的app
				mAppInfo = AppManagerUtils.getAppInfo(getApplicationContext());
				PrintLog.log(mAppInfo.size());
				for (AppInfo appInfo : mAppInfo) {
					appInfo.scanProgress = ++progress;// 设置进度信息
					// 获取缓存信息
					getCacheSize(appInfo);
					SystemClock.sleep(200); // 缓冲300毫秒

					// 发送获取到的消息
					Message msg = Message.obtain();
					msg.obj = appInfo;
					msg.what = SCANNING;
					handler.sendMessage(msg);
				}

				while (count < mAppInfo.size()) {
					// 缓存获取未结束
				}

				handler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}

	private class MyPackageStas extends IPackageStatsObserver.Stub {
		private AppInfo appInfo;

		public MyPackageStas(AppInfo appInfo) {
			this.appInfo = appInfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// 在子线程中完成的操作

			// 获取缓存信息的代码
			long cacheSize = pStats.cacheSize;
			// 封装有app缓存的app信息,包名,图标
			if (cacheSize != 0) {
				// 获取缓存
				appInfo.cacheSize = cacheSize;
				PrintLog.log("获取到了应用的缓存信息:" + cacheSize);
				mCacheApps.add(appInfo);
			}
			// end结束
			count++;
			PrintLog.log(count);
		}
	}

	/**
	 * 
	 * @param appInfo
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getCacheSize(AppInfo appInfo) {
		// 通过反射的方式来地调用mPm.getPackageSozeInfo
		try {
			// 获取class
			Class clazz = mPm.getClass();
			// 获取方法
			Method method = clazz.getDeclaredMethod("getPackageSizeInfo",
					new Class[] { String.class, IPackageStatsObserver.class });
			method.setAccessible(true);
			// 获取对象

			// 调用方法
			method.invoke(mPm, new Object[] { appInfo.appPackageName,
					new MyPackageStas(appInfo) });

		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
	}

	// 初始化动画
	private void startAnimation() {
		// 扫描的动画
		RotateAnimation scanAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		scanAnimation.setRepeatCount(Animation.INFINITE); // 无限次循环
		scanAnimation.setDuration(1000); // 设置动画时间
		// 设置匀速旋转
		scanAnimation.setInterpolator(new Interpolator() {

			@Override
			public float getInterpolation(float x) {

				return x;
			}
		});
		mIvScanning.startAnimation(scanAnimation);

		// 点的动画
		AlphaAnimation pointsAnimation = new AlphaAnimation(1.0f, 0.0f);
		pointsAnimation.setDuration(500); // 动画时间
		pointsAnimation.setRepeatCount(Animation.INFINITE); // 无限循环
		pointsAnimation.setRepeatMode(Animation.REVERSE); // 倒退动画模式
		mIvPoints.startAnimation(pointsAnimation);
	}
}
