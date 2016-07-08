package com.evil.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.evil.mobilesafe.dao.BelongQueryDao;
import com.evil.mobilesafe.view.ToastView;

/**
 * 来去电归属地显示服务
 */
public class BelongAddressService extends Service {

	private TelephonyManager mTm;
	private CallInListener mCallInListener;
	private CallOutReceiver mCallOutReceiver;
	private BelongQueryDao mDao;
	private ToastView mToastView;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 数据库操作工具
		mDao = new BelongQueryDao(this);
		// 自定义吐司
		mToastView = new ToastView(this);

		// 获取电话管理器
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 电话监听器
		mCallInListener = new CallInListener();

		int events = PhoneStateListener.LISTEN_CALL_STATE; // 监听的类型状态
		// 电话来电监听
		mTm.listen(mCallInListener, events);

		// 注册去电电话监听广播
		mCallOutReceiver = new CallOutReceiver();

		// 一定要设置优先级,同时要添加一个权限android.permission.PROCESS_OUTGOING_CALLS

		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL); // 意图过滤器

		// 设置优先级
		filter.setPriority(1000);
		// 注册广播
		registerReceiver(mCallOutReceiver, filter);
	}

	/**
	 * 电话来电广播
	 */
	class CallInListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 闲置状态
				// 关闭弹出自定义吐司
				mToastView.hide();

				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				// 弹出自定义吐司,显示归属地地址
				showLocation(incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: //通话状态
				/*// 关闭弹出自定义吐司
				mToastView.hide();*/
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 电话去电监听
	 */
	class CallOutReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 获取打出去的电话
			String number = intent.getExtras().getString(
					Intent.EXTRA_PHONE_NUMBER);

			showLocation(number);
			
			
		}
	}

	public void showLocation(String phone) {
		// 查询数据库
		String location = mDao.queryArea(phone);
		// 显示自定义吐司
		mToastView.show(location);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 注销电话来电监听
		int events = PhoneStateListener.LISTEN_NONE; // 停止监听

		mTm.listen(mCallInListener, events);

		// 注销电话去电监听
		unregisterReceiver(mCallOutReceiver);
	}

}
