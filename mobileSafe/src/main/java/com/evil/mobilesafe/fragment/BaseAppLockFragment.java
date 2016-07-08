package com.evil.mobilesafe.fragment;

import java.util.Iterator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.AppLockDao;
import com.evil.mobilesafe.domain.AppInfo;

public class BaseAppLockFragment extends Fragment {

	private MyAdapter mMyAdapter;
	private List<AppInfo> mAppInfo;
	private AppLockDao mDao;
	private StickyListHeadersListView mLv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMyAdapter = new MyAdapter();

		mLv = new StickyListHeadersListView(getActivity());

		mLv.setAdapter(mMyAdapter);

		// 返回一个View布局
		return mLv;
	}

	public void setData(List<AppInfo> appInfo, AppLockDao dao) {
		mAppInfo = appInfo;

		mDao = dao;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mMyAdapter.notifyDataSetChanged();
		};
	};

	private class MyAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {

			if (mAppInfo != null) {
				return mAppInfo.size();
			}
			return 0;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = View.inflate(getActivity(),
						R.layout.item_applock_listview_data, null);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_applock_item_appicon);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.iv_applock_lock);
				holder.tvAppName = (TextView) convertView
						.findViewById(R.id.tv_applock_item_appname);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final View copyView = convertView; // 使用另外一个变量来获取当前代表的 数据的容器

			AppInfo appInfo = mAppInfo.get(position);
			holder.ivIcon.setImageDrawable(appInfo.appIcon);
			holder.tvAppName.setText(appInfo.appName);

			if (BaseAppLockFragment.this instanceof LeftAppLockFragment) {
				// 未加锁
				holder.ivLock.setImageResource(R.drawable.app_lock_ic_locked);

				holder.ivLock.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 添加移除动画
						TranslateAnimation translation = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1.0f,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						translation.setDuration(1000); // 设置时间

						// 开始动画
						copyView.startAnimation(translation);

						mLv.setItemChecked(position, false);

						// 加锁
						AppInfo info = mAppInfo.get(position);
						mAppInfo.remove(position);
						mDao.addLockApp(info.appPackageName);
						// 休眠一秒,跟上动画时间
						synchronized (BaseAppLockFragment.class) {
							new Thread() {
								@Override
								public void run() {
									SystemClock.sleep(1000);
									// 通知更新listView
									handler.sendEmptyMessage(0);
								}
							}.start();
						}
					}
				});

			} else {
				// 已加锁
				holder.ivLock.setImageResource(R.drawable.app_lock_ic_unlock);

				holder.ivLock.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						// 添加移除动画
						TranslateAnimation translation = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1.0f,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);

						translation.setDuration(1000); // 设置时间
						// 开始动画
						copyView.startAnimation(translation);

						AppInfo info = mAppInfo.get(position);
						mAppInfo.remove(position);
						mDao.deleteLockApp(info.appPackageName);

						// 休眠一秒,跟上动画时间
						synchronized (BaseAppLockFragment.class) {
							new Thread() {
								@Override
								public void run() {
									SystemClock.sleep(1000);
									// 通知更新listView
									handler.sendEmptyMessage(0);
								}
							}.start();
						}
					}
				});
			}

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView tv = new TextView(getActivity());
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
				tv.setText("系统应用(" + (mAppInfo.size() - count) + "个)");
			} else {
				tv.setText("用户应用(" + count + "个)");
			}
			return tv;
		}

		@Override
		public long getHeaderId(int position) {

			AppInfo appInfo = mAppInfo.get(position);
			if (appInfo.isSystem) {
				return 12; // 系统软件
			} else {
				return 13; // 用户软件
			}
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvAppName;
		ImageView ivLock;
	}

	/**
	 * 动画监听器
	 */
	private class MyAnimatorListener implements AnimatorListener {

		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

	}

}
