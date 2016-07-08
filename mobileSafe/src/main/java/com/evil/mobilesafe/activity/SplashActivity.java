package com.evil.mobilesafe.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import android.R.animator;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.UpdateInfo;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.GZipUtils;
import com.evil.mobilesafe.utils.JsonUtils;
import com.evil.mobilesafe.utils.SpUtils;
import com.evil.mobilesafe.utils.Stream2String;
import com.evil.mobilesafe.utils.VersionUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int WHAT_SUCCEED = 0;
	protected static final int WHAT_FAIL = 1;
	protected static final int WHAT_ERROR = 2;
	protected int requestCode = 100; // 请求码
	private Context mContext = SplashActivity.this;
	private TextView mTvVersionName;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_SUCCEED:
				UpdateInfo mUpdateInfo = (UpdateInfo) msg.obj;

				// 校验版本号是否一致
				if (mUpdateInfo.versionCode == VersionUtils
						.getVersionCode(mContext)) {
					loadMain();
				} else {
					showDialog(mUpdateInfo);
				}
				break;
			case WHAT_FAIL:
				Toast.makeText(mContext, "联网更新版本失败", Toast.LENGTH_SHORT).show();
				// 去主界面
				loadMain();
				break;
			case WHAT_ERROR:
				if (msg.arg1 == 10000) {
					Toast.makeText(mContext, "MalformedURLException",
							Toast.LENGTH_SHORT).show();
				} else if (msg.arg1 == 10001) {
					Toast.makeText(mContext, "IOException", Toast.LENGTH_SHORT)
							.show();
				} else if (msg.arg1 == 10002) {
					Toast.makeText(mContext, "JSONException",
							Toast.LENGTH_SHORT).show();
				}
				// 去主界面
				loadMain();
				break;
			}
		};
	};
	private ImageView mIv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		mTvVersionName = (TextView) findViewById(R.id.tv_splash_version);
		// 查找控件
		initView();
		// 设置数据
		initData();

		// 获取版本信息
		mTvVersionName.setText(VersionUtils.getVersionName(this));
		// 获取设置是否联网更新
		boolean isUpdateOpen = SpUtils.getInfo(this, Constants.IS_UPDATE_OPEN,
				true);
		if (isUpdateOpen) {
			// 联网更新数据
			updateVersionInfo();
		} else {
			loadMain();
		}

	}

	// 查找控件
	private void initView() {
		mIv = (ImageView) findViewById(R.id.iv_splash_icon);

	}

	/**
	 * 设置数据
	 */
	private void initData() {
		// 加载手机归属地数据库
		loadBelongDB();

		// 加载常用号码数据库
		loadDB("commonnum.db");
		// 加载病毒数据库
		loadDB("antivirus.db");

		// 设置logo动画
		RotateAnimation rotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(2000);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setInterpolator(new Interpolator() {
			
			@Override
			public float getInterpolation(float input) {
				
				return input;
			}
		});

		mIv.startAnimation(rotate);
	}

	/**
	 * 联网获取更新json文件
	 */
	private void updateVersionInfo() {
		new Thread() {

			public void run() {
				Message msg = Message.obtain();
				try {
					// 指定url地址
					URL url = new URL(Constants.UPDATEURI);
					// 打开链接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置响应超时链接
					conn.setConnectTimeout(3000);
					// 设置请求方式
					conn.setRequestMethod("GET");
					// 获取响应码
					int responseCode = conn.getResponseCode();
					// 判断响应码
					if (responseCode == 200) {
						// 假如响应码=200则获取输入流
						InputStream inputStream = conn.getInputStream();
						// 把输入流转换成String
						String json = Stream2String.stream2String(inputStream);
						UpdateInfo mUpdateInfo = JsonUtils.jsonAnalysis(json);
						
						msg.obj = mUpdateInfo;
						msg.what = WHAT_SUCCEED;
						handler.sendMessage(msg);
					} else {
						// 获取响应不成功
						handler.sendEmptyMessage(WHAT_FAIL);
					}
				} catch (MalformedURLException e) {
					msg.what = WHAT_ERROR;
					msg.arg1 = 10000;
					handler.sendEmptyMessage(msg.what);
				} catch (IOException e) {
					msg.what = WHAT_ERROR;
					msg.arg1 = 10001;
					handler.sendEmptyMessage(msg.what);
				} catch (JSONException e) {
					msg.what = WHAT_ERROR;
					msg.arg1 = 10002;
					handler.sendEmptyMessage(msg.what);
				}

			};
		}.start();
	}

	/**
	 * 跳转到主界面
	 */
	public void loadMain() {
		new Thread(new Runnable() {
			public void run() {
				SystemClock.sleep(1500);
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}).start();
	}

	/**
	 * 联网下载apk
	 */
	public void downloadApkDialog(String downloadUrl) {
		final File file = new File(Environment.getExternalStorageDirectory(),
				"mobileSafe.apk");

		// 弹出下载进度条
		final ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setTitle("正在下载中");
		// 设置横条
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.show();
		// 设置不能够取消下载进度条
		dialog.setCancelable(false);
		// 参数是请求响应时间
		HttpUtils utils = new HttpUtils(3000);
		// 开始下载
		utils.download(downloadUrl, file.getAbsolutePath(),
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// 下载成功跳转到安装界面
						Intent intent = new Intent("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(file),
								"application/vnd.android.package-archive");
						dialog.dismiss();
						// 等待安装返回一个结果
						startActivityForResult(intent, requestCode);
					}

					/**
					 * 下载进度
					 */
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
						dialog.setMax(100);
						// 设置进度
						dialog.setProgress((int)(current * 100f /total));
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						

						Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT)
								.show();
						dialog.dismiss();

						// 去主界面
						loadMain();
					}
				});
	}

	/**
	 * 等待安装界面返回结果
	 */
	@Override
	protected void onActivityResult(int requestCodes, int resultCode,
			Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		// 等待安装界面传回结果
		if (requestCodes == requestCode) {
			// Log.d(TAG, ""+resultCode);
			// 假如传回的结果是0,则代表取消安装
			if (resultCode == 0) {
				Toast.makeText(mContext, "温馨提示， 避免下次重新下载，请选择更新安装",
						Toast.LENGTH_SHORT).show();
				// 去往主界面
				loadMain();
			}
		}

	}

	/**
	 * 弹一个dialog对话框提示下载新版本
	 */
	public void showDialog(final UpdateInfo updateInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("有最新版本: v" + updateInfo.versionCode + ".0");
		builder.setMessage(updateInfo.versionDesc);
		builder.setPositiveButton("马上下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载
				downloadApkDialog(updateInfo.downloadUrl);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("稍后下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadMain();
				dialog.dismiss();
			}
		});
		// 用户取消对话框的回调
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				loadMain();
			}
		});

		builder.show();
	}

	// 加载电话号码归属地数据库
	public void loadBelongDB() {
		final File file = new File(this.getFilesDir(), "address.db");
		// 判断数据库是否存在,不存在则加载
		if (!file.exists()) {
			new Thread() {
				public void run() {
					try {
						FileOutputStream fos = new FileOutputStream(file);
						InputStream inputStream = SplashActivity.this
								.getAssets().open("address.zip");
						GZipUtils.unCompressFile(inputStream, fos);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	// 加载数据库
	public void loadDB(final String sqlDB) {
		// 加载常用号码数据库
		final File file = new File(this.getFilesDir(), sqlDB);
		// 判断数据库是否存在,假如不存在,则拷贝
		if (!file.exists()) {
			new Thread() {
				public void run() {
					try {
						FileOutputStream fos = new FileOutputStream(file);
						AssetManager assets = getAssets();
						InputStream inputStream = assets.open(sqlDB);
						byte[] arr = new byte[8192];
						int len;
						while ((len = inputStream.read(arr)) != -1) {
							fos.write(arr, 0, len);
						}
						fos.close();
						inputStream.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}
}
