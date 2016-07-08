package com.evil.mobilesafe.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.utils.PrintLog;
import com.evil.mobilesafe.view.SpaceInfoProgress;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

public class AppManagerActivity extends Activity {
	private SpaceInfoProgress mSipRom;
	private SpaceInfoProgress mSipSD;
	private List<AppInfo> mAppInfo;
	private ArrayList<AppInfo> mUseAppInfo;
	private ArrayList<AppInfo> mSystemAppInfo;
	private ListView mLvApp;
	private AppAdapter mAdapter;
	private ProgressBar mPb;
	private TextView mTvTitle;
	private PopupWindow mPw;
	private View mContentView;
	private TextView mTvUninstall;
	private TextView mTvOpen;
	private TextView mTvShare;
	private TextView mTvInfo;
	private AppInfo mInfo;
	private Context thisContext = AppManagerActivity.this; // 当前的上下文
	private AppUninstallReceiver mAppRemoveReceiver;

	/**
	 * 注册监听软件卸载的广播
	 */
	class AppUninstallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新app应用的数据
			mAppInfo.clear();
			mUseAppInfo.clear();
			mSystemAppInfo.clear();
			getAppInfo();

			getRomInfo(); // 获取手机内存信息
			getSDInfo(); // 获取sd卡内存信息

			PrintLog.log("卸载了软件了");
		}
	}

	/**
	 * 注册广播
	 */
	private void registerReceiver() {
		// 卸载app广播
		mAppRemoveReceiver = new AppUninstallReceiver();
		// 卸载过滤器
		IntentFilter appRemoveFilter = new IntentFilter(
				Intent.ACTION_PACKAGE_REMOVED);

		// 指定数据类型
		appRemoveFilter.addDataScheme("package");
		// 注册广播
		registerReceiver(mAppRemoveReceiver, appRemoveFilter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_appmanager);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();

		// 注册广播
		registerReceiver();
	}

	@Override
	protected void onDestroy() {
		// 注销广播
		unregisterReceiver(mAppRemoveReceiver);
		super.onDestroy();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mSipRom = (SpaceInfoProgress) findViewById(R.id.sip_appmanager_rom);

		mSipSD = (SpaceInfoProgress) findViewById(R.id.sip_appmanager_sd);

		mLvApp = (ListView) findViewById(R.id.lv_appmanager_appinfo);

		mPb = (ProgressBar) findViewById(R.id.pb_appmanager);

		mTvTitle = (TextView) findViewById(R.id.tv_appmanager_lv_title);
		mTvTitle.setClickable(true); // 设置点击可用

		mContentView = View.inflate(getBaseContext(),
				R.layout.popup_appmanager, null);
		mPw = new PopupWindow(mContentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		// 查找弹出窗体的控件
		mTvUninstall = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_uninstall);
		mTvOpen = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_open);
		mTvShare = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_share);
		mTvInfo = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_info);
	}

	/**
	 * 监听器
	 */
	private void initListener() {

		// 滚动事件
		mLvApp.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mPw.isShowing()) {
					mPw.dismiss();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mUseAppInfo == null) {
					return;
				}
				if (firstVisibleItem <= mUseAppInfo.size()) {
					mTvTitle.setText("用户程序(" + mUseAppInfo.size() + "个)");
				} else {
					mTvTitle.setText("系统程序(" + mSystemAppInfo.size() + "个)");
				}
			}
		});

		// 点击事件
		mLvApp.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == mUseAppInfo.size() + 1) {
					return;
				}

				mInfo = mAdapter.getItem(position);

				// 弹出窗体
				mPw.showAsDropDown(view, 100, -view.getHeight());
			}
		});

		// 弹出窗体的点击事件
		OnClickListener popupListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_popupwindow_uninstall: // 卸载
					// 卸载软件

					// 判断是否是系统应用
					if (mInfo.isSystem) {
						// 关闭弹出窗体
						mPw.dismiss();
						// 第一个参数是命令,第二个参数是响应时间

						AlertDialog.Builder builder = new AlertDialog.Builder(
								thisContext);
						builder.setTitle("卸载提示");
						builder.setMessage("该应用是系统应用,卸载之后会造成系统不可恢复的损坏,是否要继续卸载?");
						builder.setNeutralButton("取消卸载",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										return;
									}
								});
						builder.setPositiveButton("确定卸载",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										try {
											// RootTools.isRootAvailable();//root刷机
											// RootTools.isAccessGiven();//root权限是否赋予

											// 修改系统只读的权限
											RootTools
													.sendShell(
															"mount -o remount rw /system",
															5000);
											// 执行删除卸载命令
											RootTools.sendShell("rm "
													+ mInfo.appPath, 5000);
										} catch (IOException e) {
											e.printStackTrace();
										} catch (RootToolsException e) {
											e.printStackTrace();
										} catch (TimeoutException e) {
											e.printStackTrace();
										}
									}
								});
						builder.show();

						return;
					}

					Intent intent = new Intent("android.intent.action.VIEW");
					intent.setAction("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:" + mInfo.appPackageName));
					startActivity(intent);

					// 关闭弹出窗体
					mPw.dismiss();
					break;
				case R.id.tv_popupwindow_open: // 打开
					try {
						// 把包名转化为activity
						Intent openIntent = getPackageManager()
								.getLaunchIntentForPackage(mInfo.appPackageName);

						// 打开另一个程序
						startActivity(openIntent);
						// 关闭弹出窗体
					} catch (Exception e) {
						Toast.makeText(thisContext, "该程序没有界面,无法打开",
								Toast.LENGTH_SHORT).show();
					}
					mPw.dismiss();
					break;
				case R.id.tv_popupwindow_share: // 分享
					// 分享app
					shareSDK();

					// 关闭弹出窗体
					mPw.dismiss();
					break;
				case R.id.tv_popupwindow_info: // 信息

					/*
					 * START {act=android.settings.APPLICATION_DETAILS_SETTINGS
					 * dat=package:org.itheima10.safe flg=0x10800000
					 * cmp=com.android
					 * .settings/.applications.InstalledAppDetails u=0} from pid
					 * 515
					 */

					Intent infoIntent = new Intent(
							"android.settings.APPLICATION_DETAILS_SETTINGS");
					infoIntent.setData(Uri.parse("package:"
							+ mInfo.appPackageName));

					startActivity(infoIntent);
					// 关闭弹出窗体
					mPw.dismiss();
					break;

				default:
					break;
				}
			}
		};
		// 弹出窗体的点击事件
		mTvUninstall.setOnClickListener(popupListener);
		mTvOpen.setOnClickListener(popupListener);
		mTvShare.setOnClickListener(popupListener);
		mTvInfo.setOnClickListener(popupListener);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 设置适配器
		mAdapter = new AppAdapter();
		mLvApp.setAdapter(mAdapter);

		// 系统应用和用户应用bean
		mSystemAppInfo = new ArrayList<AppInfo>();
		mUseAppInfo = new ArrayList<AppInfo>();

		// 获取app信息
		getAppInfo();
		// 弹出窗体
		showPopupWindow();
	}

	public void getAppInfo() {
		// 获取安装的软件的数据信息
		new Thread() {
			public void run() {
				mAppInfo = AppManagerUtils.getAppInfo(getApplicationContext());
				for (AppInfo info : mAppInfo) {
					if (info.isSystem) {
						if (mSystemAppInfo == null) {
							mSystemAppInfo = new ArrayList<AppInfo>();
						}
						mSystemAppInfo.add(info);
					} else {
						if (mUseAppInfo == null) {
							mUseAppInfo = new ArrayList<AppInfo>();
						}
						mUseAppInfo.add(info);
					}
				}

				SystemClock.sleep(1000);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLvApp.setEmptyView(mPb);
						mAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();
	}

	private void showPopupWindow() {

		// 设置焦点可以获取
		mPw.setFocusable(true);
		// 设置背景透明
		mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 设置外部点击可以取消
		mPw.setOutsideTouchable(true);

	}

	@Override
	protected void onStart() {
		super.onStart();

		getRomInfo(); // 获取手机内存信息
		getSDInfo(); // 获取sd卡内存信息
	}

	/**
	 * 初始化获取手机存储信息
	 */
	public void getRomInfo() {
		long romUsedSpace = AppManagerUtils.getRomUsedSpace(); // 手机内存已用空间
		long romFreeSpace = AppManagerUtils.getRomFreeSpace(); // 手机内存可用空间
		long romtotalSpace = AppManagerUtils.getRomTotalSpace(); // 手机内存总空间

		mSipRom.setUseMessage(Formatter.formatFileSize(this, romUsedSpace)
				+ "已用");
		mSipRom.setFreeMessage(Formatter.formatFileSize(this, romFreeSpace)
				+ "可用");
		int progress = (int) (((float) (romUsedSpace) / romtotalSpace) * 100f);
		// System.out.println(progress);
		mSipRom.setProgress(progress);
	}

	/**
	 * 获取sd卡内存信息
	 */
	private void getSDInfo() {
		long sdFreeSpace = AppManagerUtils.getSDFreeSpace(); // 获取sd卡可用空间
		long sdUsedSpace = AppManagerUtils.getSDUsedSpace(); // 获取sd卡已用空间
		long sdTotalSpace = AppManagerUtils.getSDTotalSpace(); // 获取sd卡总空间

		mSipSD.setUseMessage(Formatter.formatFileSize(this, sdUsedSpace));
		mSipSD.setFreeMessage(Formatter.formatFileSize(this, sdFreeSpace));
		int progress = (int) (((float) (sdUsedSpace) / sdTotalSpace) * 100f);
		mSipSD.setProgress(progress);
	}

	private class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if (mSystemAppInfo != null && mUseAppInfo != null) {
				return mSystemAppInfo.size() + mUseAppInfo.size() + 2;
			} else if (mSystemAppInfo != null && mUseAppInfo == null) {
				return mSystemAppInfo.size() + 1;
			} else if (mUseAppInfo != null && mSystemAppInfo == null) {
				return mUseAppInfo.size() + 1;
			}
			return 0;
		}

		@Override
		public AppInfo getItem(int position) {
			AppInfo info = null;
			if (position <= mUseAppInfo.size()) { // 用户程序
				info = mUseAppInfo.get(position - 1);
			} else {
				info = mSystemAppInfo.get(position - mUseAppInfo.size() - 2);
			}
			return info;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		public TextView getView(String msg) {
			TextView tv = new TextView(getApplicationContext());
			tv.setBackgroundColor(Color.GRAY);
			tv.setPadding(7, 7, 7, 7);
			tv.setText(msg);
			tv.setTextColor(Color.WHITE);
			tv.setTextSize(18);
			return tv;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView useView = getView("用户程序 (" + mUseAppInfo.size() + "个)");

				return useView;
			}

			if (position == mUseAppInfo.size() + 1) {
				TextView systemView = getView("系统程序 (" + mSystemAppInfo.size()
						+ "个)");
				return systemView;
			}

			ViewHolder holder = null;
			if (convertView == null || convertView instanceof TextView) {
				holder = new ViewHolder();

				convertView = View.inflate(getApplicationContext(),
						R.layout.item_appmanager_listview_info, null);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_appmanager_listview_appicon);
				holder.tvAppName = (TextView) convertView
						.findViewById(R.id.tv_appmanager_listview_item_appname);
				holder.tvIsSD = (TextView) convertView
						.findViewById(R.id.tv_appmanager_listview_item_issd);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.tv_appmanager_listview_item_size);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo info = getItem(position);

			holder.tvAppName.setText(info.appName);
			holder.ivIcon.setImageDrawable(info.appIcon);
			holder.tvSize.setText(Formatter.formatFileSize(
					getApplicationContext(), info.appSize));

			if (info.isSD) {
				holder.tvIsSD.setText("SD卡");
			} else {
				holder.tvIsSD.setText("手机内存");
			}

			return convertView;
		}

		class ViewHolder {
			ImageView ivIcon;
			TextView tvAppName;
			TextView tvIsSD;
			TextView tvSize;
		}
	}

	/**
	 * 分享app应用 的方法
	 */
	protected void shareSDK() {

		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("小灿手机卫士期分享");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("https://www.google.com/ncr");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("小灿手机卫士,全面守护您的手机安全!");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("https://www.google.com/ncr");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("小灿手机卫士,谁用谁知道,用过的人都说好!");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("https://www.google.com/ncr");

		// 启动分享GUI
		oks.show(this);

	}
}
