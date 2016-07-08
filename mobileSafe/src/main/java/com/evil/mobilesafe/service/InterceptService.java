package com.evil.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.evil.mobilesafe.dao.InterceptDao;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.InterceptRecordSaveUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class InterceptService extends Service {

	private SmsInterceptReceiver mSmsReceiver;
	private InterceptDao mDao;
	private TelephonyManager mTm;
	private PhoneListener mListener;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		System.out.println("开始拦截");

		mDao = new InterceptDao(this);

		mSmsReceiver = new SmsInterceptReceiver();
		// 过滤器
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		// 设置优先级
		filter.setPriority(1000);
		// 注册广播
		registerReceiver(mSmsReceiver, filter);

		// 注册电话监听
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mListener = new PhoneListener();
		// 监听的状态
		int events = PhoneStateListener.LISTEN_CALL_STATE;
		// 监听电话使用listen
		mTm.listen(mListener, events);
	}

	/**
	 * 短信拦截
	 */
	class SmsInterceptReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("收到一条短信");

			Object[] object = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : object) {

				SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
				// 获取发短信过来的号码
				String address = message.getDisplayOriginatingAddress();

				String body = message.getDisplayMessageBody();

				// 查询数据库,是否有该拦截号码存在
				int type = mDao.queryType(address);
				if (type == Constants.INTRECEPT_TYPE_SMS
						|| type == Constants.INTRECEPT_TYPE_ALL) {
					// 记录一下拦截到的短信
					InterceptRecordSaveUtils.saveSms(context, address, body,
							System.currentTimeMillis());
					// 拦截短信
					abortBroadcast();
				}
			}
		}
	}

	/**
	 * 电话拦截
	 */
	class PhoneListener extends PhoneStateListener {

		// 监听电话状态发生改变
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			/**
			 * TelephonyManager.CALL_STATE_IDLE 闲置状态
			 * TelephonyManager.CALL_STATE_RINGING 响铃状态
			 * TelephonyManager.CALL_STATE_OFFHOOK 接听状态 incomingNumber 来电号码
			 */

			//当响铃的时候,拦截该号码
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				// 查询数据库是否有该号码存在于黑名单中,以及拦截类型
				int queryType = mDao.queryType(incomingNumber);
				if (queryType == Constants.INTRECEPT_TYPE_PHONE
						|| queryType == Constants.INTRECEPT_TYPE_ALL) {
					//				System.out.println("拦截号码");
					
					try {
						//利用反射获取serviceManager
						Class clazz = Class.forName("android.os.ServiceManager");
						
						//反射获取方法
						Method method = clazz.getMethod("getService", String.class);

						//回调的IBinder
						IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
						
						// TODO 把拦截的电话用通知显示出来,并记录下来
						
						//获取ITelephony对象
						ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
						
						//挂断电话
						itelephony.endCall();
					} catch (ClassNotFoundException e) {
						
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						
						e.printStackTrace();
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
				}
			}
		}

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		System.out.println("结束拦截");
		// 注销短信监听广播
		unregisterReceiver(mSmsReceiver);

		// 反注册电话监听
		mTm.listen(mListener, PhoneStateListener.LISTEN_CELL_INFO);
	}

}
