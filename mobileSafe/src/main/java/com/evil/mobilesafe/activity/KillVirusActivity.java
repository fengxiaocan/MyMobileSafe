package com.evil.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.KillVirusDao;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.domain.UpdateVirusInfo;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.utils.JsonUtils;
import com.evil.mobilesafe.utils.Md5Utils;
import com.evil.mobilesafe.utils.PrintLog;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class KillVirusActivity extends Activity {
	private RelativeLayout mRlScanResult;
	private RelativeLayout mRlScaning;
	private LinearLayout mLlScanEnd;
	private TextView mTvScanResult;
	private Button mBtAnewScan;
	private TextView mTvScaning;
	private ImageView mIvLeft;
	private ImageView mIvRight;
	private List<AppInfo> mAppInfo;
	private AppInfo appinfo; // 这是扫描病毒之后的app
	private ArcProgress mProgress;
	private ListView mLv;
	private List<AppInfo> mLvList; // 用于给listView更新数据
	private static final int WHAT_SCAN = 1; // 扫描病毒过程中发送的appinfo
	protected static final int WAHT_VIRUSINFO = 3; // 获取病毒库成功
	private UpdateVirusInfo mVirusInfo; // 更新的病毒库版本
	private ScanAdapter mAdapter;
	private KillVirusDao mVirusDao;
	private int findVirusNum = 0; // 发现的病毒数
	private int findVirusPosition = 0; // 发现病毒的所在位置
	private List<String> findVirusAppInfoPackage; // 发现的病毒的应用的包名
	protected boolean isStopScan = false; // 是否停止扫描

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_killvirus);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();

	}

	private void initView() {
		// 扫描结果父控件
		mRlScanResult = (RelativeLayout) findViewById(R.id.rl_killvirus_scanresult);
		// 扫描结果的文本显示
		mTvScanResult = (TextView) findViewById(R.id.tv_killvirus_sanresult);
		// 重新扫描按钮
		mBtAnewScan = (Button) findViewById(R.id.bt_killvirus_anewscan);

		// 扫描过程中父控件
		mRlScaning = (RelativeLayout) findViewById(R.id.rl_killvirus_scaning);
		// 扫描进度条
		mProgress = (ArcProgress) findViewById(R.id.progress_killvirus_scaning);
		// 扫描中显示的包名
		mTvScaning = (TextView) findViewById(R.id.tv_killvirus_scaning_packagename);

		// 扫描结束
		mLlScanEnd = (LinearLayout) findViewById(R.id.ll_killvirus_scanend);
		// 扫描结束之后左边的图片
		mIvLeft = (ImageView) findViewById(R.id.iv_killvirus_scanend_left);
		// 扫描结束之后右边的图片
		mIvRight = (ImageView) findViewById(R.id.iv_killvirus_scanend_right);

		// listView
		mLv = (ListView) findViewById(R.id.lv_killvirus_scanresultshow);

	}

	private void initListener() {
		mBtAnewScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 重新开始扫描
				findVirusNum = 0;
				findVirusPosition = 0;
				scanStartAnimation();
			}
		});
	}

	private void initData() {

		// 联网查询病毒库是否更新
		checkNetVirus();

		// 设置适配器
		mAdapter = new ScanAdapter();
		mLv.setAdapter(mAdapter);

		// 初始化病毒查询工具
		mVirusDao = new KillVirusDao(this);
	}

	@Override
	protected void onStart() {
		// 判断是否还有病毒应用
		if (findVirusAppInfoPackage != null
				&& findVirusAppInfoPackage.size() > 0) {
			showKillVirus();
		}
		super.onStart();
	}

	/**
	 * 联网检查病毒库是否更新
	 */
	private void checkNetVirus() {
		// 获取updatejson
		HttpUtils http = new HttpUtils();

		http.configTimeout(5000);// 设置超时时间
		// 获取更新病毒库版本地址
		String url = getApplicationContext().getResources().getString(
				R.string.update_viris_version_net);

		RequestCallBack callBack = new RequestCallBack<String>() {
			// 下载失败
			@Override
			public void onFailure(HttpException arg0, String arg1) {

				Toast.makeText(getApplicationContext(), "联网失败，请检查网络",
						Toast.LENGTH_SHORT).show();
				// 开始扫描
				startScan();
			}

			// 下载成功
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String json = arg0.result;

				try {
					mVirusInfo = JsonUtils.jsonAnalysisVirus(json);
					// 打印一下bean的信息
					PrintLog.log(mVirusInfo.toString());

					// 判断是否要更新

					// 获取本地病毒库
					int version = mVirusDao.queryVersion();
					if (version == mVirusInfo.version) {
						// 病毒库版本一致
						Toast.makeText(getApplicationContext(),
								"您的病毒库已经是最新的版本", Toast.LENGTH_SHORT).show();
						// 开始扫描
						startScan();
					} else if (mVirusInfo.version - version > 1) {
						// 假如版本相差大于1,则更新整个病毒库
						// 下载整个病毒库
						downloadVirusDB();
					} else {
						// 否则,只更新一条病毒数据
						updateVirusDB();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		http.send(HttpMethod.GET, url, callBack);
	}

	/**
	 * 开始扫描病毒
	 */
	private void startScan() {
		mAppInfo = null;
		mLvList = null;

		mRlScanResult.setVisibility(View.GONE);
		mRlScaning.setVisibility(View.VISIBLE);
		mLlScanEnd.setVisibility(View.GONE);
		// 扫描应用
		scanVirus();
	}

	/**
	 * 弹窗提示删除病毒
	 */
	protected void showKillVirus() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("发现病毒");
		builder.setMessage("发现您手机上出现病毒应用，该应用经检测会严重危害您的手机安全，是否立即马上卸载?");
		builder.setCancelable(false);
		builder.setPositiveButton("马上卸载", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"
						+ findVirusAppInfoPackage.get(0)));
				findVirusAppInfoPackage.remove(0);

				dialog.dismiss();

				startActivity(intent);

			}
		});
		builder.setNegativeButton("不了，自己处理", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	/**
	 * 只更新一条病毒库
	 */
	protected void updateVirusDB() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新提示");
		builder.setCancelable(false);
		builder.setMessage("发现有最新的病毒，是否更新?(建议更新)");
		builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mVirusDao.addNewVirus(mVirusInfo.md5, mVirusInfo.type,
						mVirusInfo.name, mVirusInfo.desc);
				// 更新病毒库版本
				mVirusDao.updataVersion(mVirusInfo.version);

				Toast.makeText(getApplicationContext(), "恭喜您，您的病毒库已更新到最新的版本",
						Toast.LENGTH_SHORT).show();
				// 开始扫描
				startScan();
			}
		});
		builder.setNegativeButton("不更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 开始扫描
				startScan();
			}
		});

		builder.show();
	}

	/**
	 * 下载整个病毒库
	 */
	protected void downloadVirusDB() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新提示");
		builder.setCancelable(false);
		builder.setMessage("您的病毒库版本过低，严重影响您手机的安全，是否立即更新?(建议更新)");

		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						HttpUtils http = new HttpUtils(3000);
						RequestCallBack<File> callBack = new RequestCallBack<File>() {
							@Override
							public void onSuccess(ResponseInfo<File> arg0) {
								Toast.makeText(getApplicationContext(),
										"更新成功，您的病毒库已更新到最新版本",
										Toast.LENGTH_SHORT).show();

								// 开始扫描
								startScan();
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								Toast.makeText(getApplicationContext(),
										"下载失败，请检查网络,尽快更新到最新版本",
										Toast.LENGTH_SHORT).show();
								// 开始扫描
								startScan();
							}
						};
						http.download(mVirusInfo.downloadUrl, getFilesDir()
								+ "/antivirus.db", callBack);

					}
				});
		builder.setNegativeButton("不更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 开始扫描
				startScan();
			}
		});

		builder.show();

	}

	// 扫描结束的动画
	protected void scanEndAnimation() {
		mRlScaning.setVisibility(View.GONE); // 扫描过程中
		mLlScanEnd.setVisibility(View.VISIBLE); // 扫描完成或者开始
		mBtAnewScan.setClickable(false); // 禁止点击按钮,防止多次按按钮
		// 平移动画
		ObjectAnimator leftTrans = ObjectAnimator.ofFloat(mIvLeft,
				"translationX", 0, -20, -50, -70, -100, -mIvLeft.getWidth());
		// 透明度变化
		ObjectAnimator leftAlpha = ObjectAnimator.ofFloat(mIvLeft, "alpha",
				1.0f, 0.7f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0f);

		ObjectAnimator rightTrans = ObjectAnimator.ofFloat(mIvRight,
				"translationX", 0, 20, 40, 60, 80, 100, mIvRight.getWidth());
		// 透明度变化
		ObjectAnimator rightAlpha = ObjectAnimator.ofFloat(mIvRight, "alpha",
				1.0f, 0.7f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0f);

		mRlScanResult.setVisibility(View.VISIBLE); // 扫描结果动画淡出
		// mRlScanResult.setBackgroundColor(Color.WHITE);
		ObjectAnimator rlResultAlpha = ObjectAnimator.ofFloat(mRlScanResult,
				"alpha", 0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 1.0f);

		// 开始播放动画
		AnimatorSet set = new AnimatorSet();
		set.playTogether(leftTrans, leftAlpha, rightTrans, rightAlpha,
				rlResultAlpha);
		set.setDuration(2000);// 设置播放时间
		set.start();

		set.addListener(new MyAnimatorListener() {

			// 动画播放完成回调
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (findVirusNum != 0) {
					mTvScanResult.setText("危险!发现了" + findVirusNum + "个病毒");
					mTvScanResult.setTextColor(Color.RED);

					// 弹窗提示删除病毒
					showKillVirus();
				} else {
					mTvScanResult.setText("您的手机很安全");
					mTvScanResult.setTextColor(Color.WHITE);
				}
			}
		});

	}

	// 扫描开始的动画
	protected void scanStartAnimation() {
		mBtAnewScan.setClickable(false); // 禁止点击按钮,防止多次按按钮

		mRlScanResult.setVisibility(View.VISIBLE); // 扫描结果
		mRlScaning.setVisibility(View.GONE); // 扫描过程中
		mLlScanEnd.setVisibility(View.VISIBLE); // 扫描完成或者开始

		// 平移动画
		ObjectAnimator leftTrans = ObjectAnimator.ofFloat(mIvLeft,
				"translationX", -mIvLeft.getWidth(), -100, -80, -60, -40, -20,
				0);
		// 透明度变化
		ObjectAnimator leftAlpha = ObjectAnimator.ofFloat(mIvLeft, "alpha", 0f,
				0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 1.0f);

		ObjectAnimator rightTrans = ObjectAnimator.ofFloat(mIvRight,
				"translationX", mIvRight.getWidth(), 100, 80, 60, 40, 20, 0);
		// 透明度变化
		ObjectAnimator rightAlpha = ObjectAnimator.ofFloat(mIvRight, "alpha",
				0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 1.0f);

		// 扫描结果淡出
		ObjectAnimator rlResultAlpha = ObjectAnimator.ofFloat(mRlScanResult,
				"alpha", 1.0f, 0.7f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0f);

		// 开始播放动画
		AnimatorSet set = new AnimatorSet();
		set.playTogether(leftTrans, leftAlpha, rightTrans, rightAlpha,
				rlResultAlpha);
		set.setDuration(2000);// 设置播放时间
		set.start();

		// 播放动画结束以后的回调
		set.addListener(new MyAnimatorListener() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				// 播放动画完成以后开始扫描病毒
				mBtAnewScan.setClickable(true); // 开启按钮
				startScan();
			}

		});
	}

	/**
	 * 获取左边的图片
	 */
	protected Bitmap getLeftBitmap(Bitmap bitmap) {
		// 1.画纸
		Bitmap leftBitmap = Bitmap.createBitmap(bitmap.getWidth() / 2,
				bitmap.getHeight(), bitmap.getConfig());
		// 2.画板
		Canvas canvas = new Canvas(leftBitmap);
		// 3.画笔
		Paint paint = new Paint();

		// 4.矩阵
		Matrix matrix = new Matrix();

		// 5.绘画
		canvas.drawBitmap(bitmap, matrix, paint);

		return leftBitmap;
	}

	/**
	 * 获取右边的图片
	 */
	protected Bitmap getRightBitmap(Bitmap bitmap) {
		// 1.画纸
		Bitmap rightBitmap = Bitmap.createBitmap(bitmap.getWidth() / 2,
				bitmap.getHeight(), bitmap.getConfig());
		// 2.画板
		Canvas canvas = new Canvas(rightBitmap);
		// 3.画笔
		Paint paint = new Paint();

		// 4.矩阵
		Matrix matrix = new Matrix();
		// 偏移原图
		matrix.setTranslate(-rightBitmap.getWidth(), 0);
		// 5.绘画
		canvas.drawBitmap(bitmap, matrix, paint);

		return rightBitmap;
	}
	
	@Override
	protected void onDestroy() {
		isStopScan = true;		//停止扫描
		super.onDestroy();
	}

	/**
	 * 扫描应用
	 */
	public void scanVirus() {
		// 禁止ListView获取焦点和点击
		mLv.setClickable(false);
		mLv.setFocusable(false);

		new Thread() {
			public void run() {
				// 获取所有安装的应用
				mAppInfo = AppManagerUtils.getAppInfo(getApplicationContext());

				mLvList = new ArrayList<AppInfo>();
				// 开始查杀病毒
				int scanProgress = 0;

				for (AppInfo appInfo : mAppInfo) {

					if (isStopScan) { // 判断是否停止扫描
						PrintLog.log("停止扫描");
						return;
					}

					// 获取app文件的md5特征码
					File appFile = new File(appInfo.appPath);
					String md5 = Md5Utils.getMd5(appFile);

					// 扫描进度自增
					scanProgress++;

					// 判断是否是病毒
					String checkVirus = mVirusDao.checkVirus(md5);
					if (TextUtils.isEmpty(checkVirus)) {
						// 为空则不是病毒
						appInfo.isVirus = false;
					} else {
						findVirusNum++;
						// 是病毒
						appInfo.isVirus = true;
						appInfo.virusDesc = checkVirus; // 获取病毒的信息
						findVirusPosition = scanProgress; // 记录病毒所在的位置

						// 记录病毒的appInfo包名
						if (findVirusAppInfoPackage == null) {
							findVirusAppInfoPackage = new ArrayList<String>();
						}
						findVirusAppInfoPackage.add(appInfo.appPackageName);
					}

					mLvList.add(appInfo); // 添加到集合中

					// 改变appInfo里的扫描进度
					appInfo.scanProgress = scanProgress;

					appinfo = appInfo;

					// 把appInfo发送到主线程
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							// 显示应用程序包名
							mTvScaning.setText(appinfo.appPackageName);

							// 设置进度
							mProgress.setProgress(Math
									.round(appinfo.scanProgress * 100.0f
											/ mAppInfo.size()));

							// 通知适配器更新
							mAdapter.notifyDataSetChanged();
							// listView自动滚动到最后
							mLv.smoothScrollToPosition(mLvList.size());
						}
					});
					// 延迟一下
					SystemClock.sleep(100);
				}
				// 扫描完成
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						// 扫描完成的时候,截图
						mRlScaning.setDrawingCacheEnabled(true);// 只有设置这个属性,才能够截图
						Bitmap bitmap = mRlScaning.getDrawingCache();
						// 画出左边的图片
						Bitmap leftBitmap = getLeftBitmap(bitmap);
						// 画出右边的图片
						Bitmap rightBitmap = getRightBitmap(bitmap);

						mIvLeft.setImageBitmap(leftBitmap);
						mIvRight.setImageBitmap(rightBitmap);

						// 左边以及右边的动画淡出
						scanEndAnimation();

						mBtAnewScan.setClickable(true); // 按钮可点击

						// ListView获取焦点和点击
						mLv.setClickable(true);
						mLv.setFocusable(true);
						// listView自动滚动到病毒所在位置
						mLv.smoothScrollToPosition(findVirusPosition - 1);
					}
				});

			};
		}.start();
	}

	// 适配器
	class ScanAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mLvList != null) {
				return mLvList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = View.inflate(getApplicationContext(),
						R.layout.item_killvirus_listview_scanshow, null);

				holder.ivAppIcon = (ImageView) convertView
						.findViewById(R.id.iv_killvirus_scanshow_appicon);
				holder.ivSafeIcon = (ImageView) convertView
						.findViewById(R.id.iv_killvirus_scanshow_safe);
				holder.tvAppName = (TextView) convertView
						.findViewById(R.id.tv_killvirus_scanshow_appname);
				holder.tvSafe = (TextView) convertView
						.findViewById(R.id.tv_killvirus_scanshow_safe);

				// 设置标记
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo appInfo = mLvList.get(position);
			holder.ivAppIcon.setImageDrawable(appInfo.appIcon);
			holder.tvAppName.setText(appInfo.appName);
			if (appInfo.isVirus) {
				// 是病毒
				holder.tvSafe.setText(appInfo.virusDesc);
				holder.tvSafe.setTextColor(Color.RED);
				holder.ivSafeIcon.setImageResource(R.drawable.unsafe);
				holder.tvAppName.setTextColor(Color.RED);
			} else {
				// 不是病毒则是安全的
				holder.tvSafe.setText("安全");
				holder.tvSafe.setTextColor(Color.GREEN);
				holder.ivSafeIcon.setImageResource(R.drawable.safe);
				holder.tvAppName.setTextColor(Color.BLACK);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView ivAppIcon;
			TextView tvAppName;
			TextView tvSafe;
			ImageView ivSafeIcon;
		}

	}

	class MyAnimatorListener implements AnimatorListener {

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
