package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.service.LockScreenClearService;
import com.evil.mobilesafe.utils.ActivityUtils;
import com.evil.mobilesafe.utils.SpUtils;
import com.evil.mobilesafe.utils.TaskManagerUtils;
import com.evil.mobilesafe.view.SettingItemView;
import com.evil.mobilesafe.view.SpaceInfoProgress;

/**
 * 进程管理界面
 */
public class TaskManagerActivity extends Activity {
	private SpaceInfoProgress mSipNumber;
	private SpaceInfoProgress mSipMemory;
	private Context thisContext = TaskManagerActivity.this;
	private List<AppInfo> mAppInfo;
	private long mAvailMemorySize;
	private long mTotalMemorySize;
	private long mUseMemorySize;
	private int maxTaskNumber;
	private StickyListHeadersListView mLv;
	private TaskAdapter mTaskAdapter;
	private ProgressBar mPb;
	private ImageView mIvArrows1;
	private ImageView mIvArrows2;
	private AlphaAnimation mAlpha1;
	private AlphaAnimation mAlpha2;
	private SlidingDrawer mSlidingDrawer;
	private Button mBtAll;
	private Button mBtReverse;
	private ImageView mIvClear;
	private SettingItemView mSivLockscreenClear;
	private SettingItemView mSivShowSystemTask;
	private boolean mIsShowSystemTask;
	private boolean mRunningLockScreenClear;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

	}

	@Override
	protected void onStart() {
		// 获取活动管理器
		super.onStart();

		// 初始化数据
		initData();

		// 开始动画
		startAnimation();
	}

	/**
	 * 抽屉关闭开启动画
	 */
	private void startAnimation() {
		mAlpha1 = new AlphaAnimation(1.0f, 0.4f);
		mAlpha1.setDuration(400);
		mAlpha1.setRepeatCount(Animation.INFINITE);
		// 开启动画
		mIvArrows1.startAnimation(mAlpha1);

		mAlpha2 = new AlphaAnimation(0.4f, 1.0f);
		mAlpha2.setDuration(400);
		mAlpha2.setRepeatCount(Animation.INFINITE);
		// 开启动画
		mIvArrows2.startAnimation(mAlpha2);

		// 箭头图标向上
		mIvArrows1.setImageResource(R.drawable.drawer_arrow_up);
		mIvArrows2.setImageResource(R.drawable.drawer_arrow_up);
	}

	/**
	 * 抽屉打开时关闭动画
	 */
	private void stopAnimation() {
		mIvArrows1.clearAnimation();
		mIvArrows2.clearAnimation();

		// 箭头图标向下
		mIvArrows1.setImageResource(R.drawable.drawer_arrow_down);
		mIvArrows2.setImageResource(R.drawable.drawer_arrow_down);
	}

	private void initView() {
		setContentView(R.layout.activity_taskmanager);
		// 获取进程个数的控件
		mSipNumber = (SpaceInfoProgress) findViewById(R.id.sip_taskmanager_tasknumber);
		// 获取进程内存大小的控件
		mSipMemory = (SpaceInfoProgress) findViewById(R.id.sip_taskmanager_memory);

		// 查找listView
		mLv = (StickyListHeadersListView) findViewById(R.id.lv_appmanager_content);

		// 加载进度圈
		mPb = (ProgressBar) findViewById(R.id.pb_taskmanager);

		// 向上的箭头图标
		mIvArrows1 = (ImageView) findViewById(R.id.iv_taskmanager_arrows1);
		mIvArrows2 = (ImageView) findViewById(R.id.iv_taskmanager_arrows2);

		mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sd_taskmanager);

		// 全选按钮
		mBtAll = (Button) findViewById(R.id.bt_taskmanager_all);
		// 反选按钮
		mBtReverse = (Button) findViewById(R.id.bt_taskmanager_reverse);

		// 清理按钮
		mIvClear = (ImageView) findViewById(R.id.iv_taskmanager_clear);

		// 显示系统进程按钮
		mSivShowSystemTask = (SettingItemView) findViewById(R.id.siv_taskmanager_show_systemtask);
		// 锁屏自动清理按钮
		mSivLockscreenClear = (SettingItemView) findViewById(R.id.siv_taskmanager_automatic);

	}

	private void initListener() {
		// 当上拉菜单上拉时
		mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				stopAnimation();
			}
		});
		// 当上拉菜单下拉时
		mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				startAnimation();
			}
		});

		// ListView条目点击事件
		mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo appInfo = mAppInfo.get(position);

				appInfo.isCheck = !appInfo.isCheck;

				// 通知界面更新
				mTaskAdapter.notifyDataSetChanged();
			}
		});

		// 点击事件的监听器
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_taskmanager_all: // 全选按钮
					for (AppInfo info : mAppInfo) {
						info.isCheck = true; // 全选选择
					}
					mTaskAdapter.notifyDataSetChanged(); // 更新数据
					break;
				case R.id.bt_taskmanager_reverse: // 反选按钮
					for (AppInfo info : mAppInfo) {
						info.isCheck = !info.isCheck; // 反选操作
					}
					mTaskAdapter.notifyDataSetChanged(); // 更新数据
					break;
				case R.id.iv_taskmanager_clear: 		// 清扫按钮
					// 清理进程
					clearTask();
					break;
				case R.id.siv_taskmanager_show_systemtask: // 显示系统进程
					// 显示系统进程
					showSystemTask();
					break;
				case R.id.siv_taskmanager_automatic: // 锁屏自动清理
					// 锁屏自动清理
					lockScreenClear();
					break;

				default:
					break;
				}
			}
		};
		mBtAll.setOnClickListener(listener);
		mBtReverse.setOnClickListener(listener);

		mIvClear.setOnClickListener(listener);
		// 显示系统进程
		mSivShowSystemTask.setOnClickListener(listener);

		// 锁屏自动清理
		mSivLockscreenClear.setOnClickListener(listener);
	}

	/**
	 * 锁屏自动清理
	 */
	protected void lockScreenClear() {
		mRunningLockScreenClear = !mRunningLockScreenClear;
		if (mRunningLockScreenClear) {
			// 更改锁屏自动清理按钮的状态
			mSivLockscreenClear.setSettingOnButton(mRunningLockScreenClear);
			// 开启服务
			Intent intent = new Intent(this, LockScreenClearService.class);
			startService(intent);
		} else {
			// 更改锁屏自动清理按钮的状态
			mSivLockscreenClear.setSettingOnButton(mRunningLockScreenClear);
			// 停止服务
			Intent intent = new Intent(this, LockScreenClearService.class);
			stopService(intent);
		}
	}

	/**
	 * 显示系统进程的方法
	 */
	protected void showSystemTask() {
		mIsShowSystemTask = !mIsShowSystemTask;
		// 更改图标的状态
		mSivShowSystemTask.setSettingOnButton(mIsShowSystemTask);

		// 保存信息
		SpUtils.save(thisContext, Constants.SHOW_SYSTEM_TASK, mIsShowSystemTask);
		// 通知适配器更新
		mTaskAdapter.notifyDataSetChanged();
	}

	/**
	 * 清理进程
	 */
	protected void clearTask() {
		new Thread() {
			public void run() {
				List<AppInfo> list = new ArrayList<AppInfo>();
				
				for (AppInfo info : mAppInfo) {
					if (info.appPackageName
							.equals(thisContext.getPackageName())) {
						list.add(info);
						continue; // 假如是自己的程序,跳过不执行
					}
					if (info.isCheck) {
						// 假如勾选了,则给清理进程
						ActivityUtils.killApp(thisContext, info.appPackageName);
						count++;
					} else {
						list.add(info);
					}
				}
				// 欺骗用户
				mAppInfo.clear();
				mAppInfo.addAll(list);
				// 重新获取进程数据
				getTaskMemorySize();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 更新进程数据的显示
						setTaskInfo();
						// 通知更新数据
						mTaskAdapter.notifyDataSetChanged();
						
						Toast.makeText(getApplicationContext(), "清理完成!共清理"+count+"个进程!",
								Toast.LENGTH_SHORT).show();
						count = 0;
					}
				});
			}
		}.start();

	}

	private void initData() {
		mTaskAdapter = new TaskAdapter();
		mLv.setAdapter(mTaskAdapter);

		// 是否显示系统进程
		mIsShowSystemTask = SpUtils.getInfo(thisContext,
				Constants.SHOW_SYSTEM_TASK, true);
		mSivShowSystemTask.setSettingOnButton(mIsShowSystemTask);

		// 获取进程信息
		getTaskInfo();

		// 获取锁屏进程清理服务的状态
		mRunningLockScreenClear = ActivityUtils.isRunningService(this,
				LockScreenClearService.class);
		// 更改锁屏自动清理按钮的状态
		mSivLockscreenClear.setSettingOnButton(mRunningLockScreenClear);
	}

	/**
	 * 获取进程信息
	 */
	private void getTaskInfo() {
		new Thread() {
			public void run() {
				mAppInfo = TaskManagerUtils.getProgress(thisContext);

				Collections.sort(mAppInfo, new Comparator<AppInfo>() {
					@Override
					public int compare(AppInfo lhs, AppInfo rhs) {
						if (rhs.isSystem) {
							return -1;
						}
						return 1;
					}
				});
				/*
				 * // 使用CopyOnWriteArrayList数组,可以在迭代的时候删除数据而不造成并发异常修改
				 * CopyOnWriteArrayList<AppInfo> copyList = new
				 * CopyOnWriteArrayList<AppInfo>( mAppInfo); mAppInfo =
				 * copyList;
				 */
				// 获取进程内存信息
				getTaskMemorySize();

				// 获取进程总个数
				maxTaskNumber = TaskManagerUtils.getMaxTaskNumber(thisContext);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTaskInfo();

						// mLv.setEmptyView(mPb);
						mLv.setVisibility(View.VISIBLE); // 设置第三方ListView可见
						mPb.setVisibility(View.GONE); // 设置加载圈不可见

						// 通知适配器更新数据
						mTaskAdapter.notifyDataSetChanged();
					}
				});
			}
		}.start();
	}

	/**
	 * 获取进程内存的大小
	 */
	public void getTaskMemorySize() {
		// 获取总内存大小
		mTotalMemorySize = TaskManagerUtils.getTotalMemorySize(thisContext);
		// 获取可用内存大小
		mAvailMemorySize = TaskManagerUtils.getAvailMemorySize(thisContext);
		// 获取占用内存大小
		mUseMemorySize = mTotalMemorySize - mAvailMemorySize;
	};

	/**
	 * 设置进程数以及内存信息的显示
	 */
	public void setTaskInfo() {
		mSipNumber.setUseMessage("正在运行" + mAppInfo.size() + "个");
		mSipNumber.setFreeMessage("可有进程" + maxTaskNumber + "个");
		// 设置进度条
		int progress = (int) (mAppInfo.size() * 100.0f / maxTaskNumber);
		mSipNumber.setProgress(progress);

		mSipMemory.setUseMessage("占用内存:"
				+ Formatter.formatFileSize(thisContext, mUseMemorySize));
		mSipMemory.setFreeMessage("可用内存:"
				+ Formatter.formatFileSize(thisContext, mAvailMemorySize));

		// 设置进度条
		int progress2 = (int) ((mUseMemorySize * 1.0f / mTotalMemorySize) * 100.0f);
		mSipMemory.setProgress(progress2);
	}

	class TaskAdapter extends BaseAdapter implements StickyListHeadersAdapter {

		@Override
		public int getCount() {

			if (mAppInfo == null) {
				return 0;
			}

			if (mIsShowSystemTask) { // 显示系统进程
				return mAppInfo.size();
			} else {
				// 不显示系统进程
				AppInfo info = new AppInfo();
				info.isSystem = true;
				int indexOf = mAppInfo.indexOf(info);
				return indexOf;
			}

		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null || convertView instanceof TextView) {
				holder = new ViewHolder();

				convertView = View.inflate(getApplicationContext(),
						R.layout.item_taskmanager_listview_info, null);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_taskmanager_listview_appicon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_taskmanager_listview_item_appname);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.tv_taskmanager_listview_item_issd);
				holder.cb = (CheckBox) convertView
						.findViewById(R.id.cb_taskmanager);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo info = mAppInfo.get(position);

			holder.tvName.setText(info.appName);
			holder.ivIcon.setImageDrawable(info.appIcon);
			holder.tvSize.setText("占用内存"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.memSize));
			holder.cb.setChecked(info.isCheck);

			if (info.appPackageName.equals(thisContext.getPackageName())) {
				// 假如是自己的软件,则给隐藏勾选状态
				holder.cb.setVisibility(View.GONE);
			} else {
				// 假如不是,则给显示
				holder.cb.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView ivIcon;
			TextView tvName;
			TextView tvSize; // 占用内存
			CheckBox cb; // 选择按钮
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView tv = new TextView(thisContext);
			tv.setBackgroundColor(Color.GRAY); // 灰色背景
			tv.setTextSize(18); // 字体大小
			tv.setTextColor(Color.WHITE); // 字体颜色
			tv.setPadding(5, 5, 5, 5);
			int count = 0;
			for (AppInfo info : mAppInfo) {
				if (!info.isSystem) {
					count++;
				}
			}

			if (mAppInfo.get(position).isSystem) {
				tv.setText("系统进程(" + (mAppInfo.size() - count) + "个)");
			} else {
				tv.setText("用户进程(" + count + "个)");
			}
			return tv;
		}

		@Override
		public long getHeaderId(int position) {
			AppInfo appInfo = mAppInfo.get(position);
			if (appInfo.isSystem) {
				return 2; // 假如是系统应用,则返回同一个HeaderId
			} else {
				return 1;
			}
		}
	}
}
