package com.evil.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

/**
 * 设置向导界面2
 */
public class SetGuide2Activity extends BaseSetGuideActivity{
	protected static final String TAG = "SetGuide2Activity";
	private RelativeLayout mLayout;
	private ImageView mIvLock;
	private boolean mBindSim;			//是否绑定了sim卡
	private String mSimNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setguide2);
		
		//初始化控件
		initView();
		//设置监听器
		initListener();
		
		//初始化数据
		initData();
		
	}

	/**
	 *	初始化控件
	 */
	private void initView() {
		mLayout = (RelativeLayout) findViewById(R.id.rl_retrieve_bind_sim);
		//锁的图标
		mIvLock = (ImageView) findViewById(R.id.iv_retrieve_bind_lock_icon);
	}
	
	private void initListener() {
		//绑定解绑SIM卡
		mLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBindSim) {				//假如已经绑定手机SIM卡
					mBindSim = !mBindSim;	//解除绑定手机SIM卡
					setIcon(mBindSim);		//设置图标
					//置空sim卡
					SpUtils.save(getApplicationContext(), Constants.SIM_NUMBER, "");
				}else {
					//获取手机管理器
					TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
					//获取sim卡号
					String number = manager.getSimSerialNumber();
					//判断是否能获取SIM卡
					if (number == null) {
						Toast.makeText(getApplicationContext(), "绑定SIM卡失败,请检查手机SIM卡是否插入",
								Toast.LENGTH_SHORT).show();
						return;
					}
					//保存sim卡
					SpUtils.save(getApplicationContext(), Constants.SIM_NUMBER, number);
					mBindSim = !mBindSim;	//解除绑定手机SIM卡
					setIcon(mBindSim);		//设置图标
				}
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		mSimNumber = SpUtils.getInfo(getApplicationContext(), Constants.SIM_NUMBER, "");
		if (TextUtils.isEmpty(mSimNumber)) {
			mBindSim = false;
		}else {
			mBindSim = true;
		}
		setIcon(mBindSim);
	}
	
	/**
	 * 设置锁的图标状态
	 */
	public void setIcon(boolean isBind){
		if (isBind) {
			mIvLock.setImageResource(R.drawable.lock);
		}else {
			mIvLock.setImageResource(R.drawable.unlock);
		}
	}
	
	

	public void pre(View v){
		prev();
	}
	public void next(View v){
		next();
	}

	//跳转到上一个界面
	@Override
	public void prev() {
		prevActivity(SetGuide1Activity.class);
	}

	//跳转到下一个界面
	@Override
	public void next() {
		//判断是否绑定SIM卡
		String simNumber = SpUtils.getInfo(getBaseContext(),Constants.SIM_NUMBER, "");
		if (TextUtils.isEmpty(simNumber)) {
			Toast.makeText(getApplicationContext(), "如果要开启手机防盗,必须要绑定SIM卡", Toast.LENGTH_SHORT)
			.show();
		}else {
			nextActivity(SetGuide3Activity.class);
		}
	}
	
	@Override
	protected void onPause() {
		if (!mBindSim) {
			//把防盗设置设置为不开启
			SpUtils.save(getApplicationContext(), Constants.SET_RETRIEVE, false);
		}
		super.onPause();
	}
}
