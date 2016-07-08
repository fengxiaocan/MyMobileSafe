package com.evil.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.evil.mobilesafe.R;

/**
 * 远程短信指令广播操控手机
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsBroadcastReceiver";
	private String mPhone;
	private DevicePolicyManager mMDPM;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取是否开启了防盗服务

		SharedPreferences sp = context.getSharedPreferences("mobileSafe.xml",
				Context.MODE_PRIVATE);
		boolean openRetrieve = sp.getBoolean("setRetrieve", false);
		if (openRetrieve) {

			// 获取短信组
			Object[] obj = (Object[]) intent.getSerializableExtra("pdus");
			for (Object object : obj) {
				byte[] arr = (byte[]) object;

				SmsMessage sms = SmsMessage.createFromPdu(arr);

				// 获取短信内容
				String body = sms.getMessageBody();

				// 判断短信内容
				if (body.equals("#*location*#")) { // GPS追踪

					getLocation(context);

					abortBroadcast(); // 清理短信

				} else if (body.equals("#*alarm*#")) { // 播放报警音乐
					MediaPlayer player = MediaPlayer.create(context,
							R.raw.alarm);

					player.setVolume(1.0f, 1.0f);

					player.setLooping(true);

					player.start();

					abortBroadcast(); // 清理短信
				} else if (body.equals("#*wipedata*#")) { // 清除数据
					mMDPM = (DevicePolicyManager) context
							.getSystemService(Context.DEVICE_POLICY_SERVICE);

					// 清除数据
					mMDPM.wipeData(0);

					abortBroadcast(); // 清理短信

				} else if (body.equals("#*lockscreen*#")) { // 锁屏
					mMDPM = (DevicePolicyManager) context
							.getSystemService(Context.DEVICE_POLICY_SERVICE);

					mMDPM.lockNow();

					// 设置密码
					String password = sp.getString("password", "123456");
					mMDPM.resetPassword(password, 0);

					abortBroadcast(); // 清理短信
				}
			}
		} else {
			// 不开启防盗服务跳过
			return;
		}
	}

	/**
	 * 获取GPS
	 */
	public void getLocation(Context context) {

		SharedPreferences sp = context.getSharedPreferences("mobileSafe.xml",
				Context.MODE_PRIVATE);
		// 获取安全号码
		mPhone = sp.getString("safePhone", "");

		// 获取定位服务
		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		LocationListener intent = new LocationListener() {
			// 状态发生改变
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			// 激活的时候回调
			@Override
			public void onProviderEnabled(String provider) {
			}

			// 停止的时候回调
			@Override
			public void onProviderDisabled(String provider) {
			}

			// 当位置发生改变的时候回调
			@Override
			public void onLocationChanged(Location location) {
				double latitude = location.getLatitude(); // 获取维度信息
				double longitude = location.getLongitude(); // 获取经度
				// double altitude = location.getAltitude(); //海拔

				SmsManager sms = SmsManager.getDefault();
				// 发送信息给安全号码
				sms.sendTextMessage(mPhone, null, "维度:" + latitude + "\n经度:"
						+ longitude, null, null);

			}
		};

		/**
		 * 第一个参数,使用哪种定位方式 第二个参数,多长时间间隔通知一次 第三个参数,移动多少距离通知一次 第四个参数,监听回调
		 */
		manager.requestLocationUpdates(manager.GPS_PROVIDER, 0, 1000, intent);
	}

}
