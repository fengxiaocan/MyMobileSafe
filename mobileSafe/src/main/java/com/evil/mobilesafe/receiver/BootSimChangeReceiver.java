package com.evil.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootSimChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// 监听sim卡是否改变
		SharedPreferences sp = context.getSharedPreferences("mobileSafe.xml",
				Context.MODE_PRIVATE);
		// 是否开启防盗设置
		boolean openRetrieve = sp.getBoolean("setRetrieve", false);
		if (openRetrieve) {
			// 获取绑定的sim卡
			String bindNumber = sp.getString("simNumber", "");
			// 获取更换的sim卡
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String changeSimNumber = manager.getSimSerialNumber();

			System.out.println("手机号码?:  " + manager.getLine1Number());

			if (!changeSimNumber.equals(bindNumber)) {
				// 获取安全号码
				String phone = sp.getString("safePhone", "");

				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(phone, null, "我是小偷,我偷了手机,换了SIM卡,防盗短信:\n"
						+ "GPS追踪:#*location*#" + "\n播放报警音乐:#*alarm*#"
						+ "\n远程数据删除:#*wipedata*#" + "\n远程锁屏:#*lockscreen*#",
						null, null);

			}
		} else {
			return;
		}

	}

}
