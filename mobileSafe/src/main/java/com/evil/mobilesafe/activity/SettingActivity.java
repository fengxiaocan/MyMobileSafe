package com.evil.mobilesafe.activity;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.service.BelongAddressService;
import com.evil.mobilesafe.service.InterceptService;
import com.evil.mobilesafe.utils.ActivityUtils;
import com.evil.mobilesafe.utils.SpUtils;
import com.evil.mobilesafe.view.BelongShowDialog;
import com.evil.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * setting设置界面
 */
public class SettingActivity extends Activity {
	private SettingItemView mSivUpdate;
	private boolean mIsUpdate;
	private SettingItemView mSivIntercept;
	private boolean mIsIntercept;
	private Context thisContext;
	private boolean mIsShowArea;
	private SettingItemView mSivArea;
	private SettingItemView mSivStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting_main);
		// 查找控件id
		initFindView();
		// 设置监听事件
		initListener();

		// 初始化数据
		initData();

	}

	private void initData() {

	}

	/**
	 * 查找控件id
	 */
	public void initFindView() {
		// 设置当前上下文
		thisContext = SettingActivity.this;
		// 自动更新版本控件
		mSivUpdate = (SettingItemView) findViewById(R.id.siv_setting_update);
		// 骚扰拦截控件
		mSivIntercept = (SettingItemView) findViewById(R.id.siv_setting_intercept);
		// 归属地显示控件
		mSivArea = (SettingItemView) findViewById(R.id.siv_setting_showArea);
		//归属地style
		mSivStyle = (SettingItemView) findViewById(R.id.siv_setting_belong_style);
	}

	/**
	 * 监听事件
	 */
	public void initListener() {

		// 设置自动更新版本按钮点击改变状态
		mSivUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsUpdate = !mIsUpdate;
				mSivUpdate.setSettingOnButton(mIsUpdate);
				SpUtils.save(thisContext, Constants.IS_UPDATE_OPEN, mIsUpdate);
			}
		});
		// 设置骚扰拦截按钮点击改变状态
		mSivIntercept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsIntercept = !mIsIntercept;
				//设置按钮显示状态
				mSivIntercept.setSettingOnButton(mIsIntercept);
/*				//记录状态
				SpUtils.save(thisContext, Constants.IS_INTERCEPT, mIsIntercept);
*/				
				if (mIsIntercept) { // 假如开启,则开启拦截服务
					startService(new Intent(SettingActivity.this,
							InterceptService.class));
				} else { // 关闭拦截服务
					stopService(new Intent(SettingActivity.this,
							InterceptService.class));
				}

			}
		});
		// 设置归属地显示按钮点击改变状态
		mSivArea.setOnClickListener(new OnClickListener() {
			private Intent service;

			@Override
			public void onClick(View v) {
				
				mIsShowArea = !mIsShowArea;
				//设置显示按钮状态
				mSivArea.setSettingOnButton(mIsShowArea);
				
				/*
				 * //记录状态 SpUtils.save(thisContext,
				 * Constants.PHONE_AREA_OPEN_SHOW, mIsShowArea);
				 */
				service = new Intent(getApplicationContext(),
						BelongAddressService.class);

				if (mIsShowArea) {
					//开启显示服务
					startService(service);
				} else {
					//关闭显示服务
					stopService(service);
				}
			}
		});
		
		//设置归属地显示DialogView
		mSivStyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BelongShowDialog dialog = new BelongShowDialog(thisContext);
				
				dialog.show();
			}
		});
	}

	/**
	 * 初始化开关
	 */
	public void initButtonOpen() {
		// 获取自动更新开启记录的状态
		mIsUpdate = SpUtils.getInfo(this, Constants.IS_UPDATE_OPEN, true);
		// 初始化开关状态
		mSivUpdate.setSettingOnButton(mIsUpdate);
	}

	@Override
	protected void onStart() {
		// 界面可见的时候设置上次记录的开关状态
		initButtonOpen();
		// 检查服务是否运行
		checkServiece();

		super.onStart();
	}

	public void checkServiece() {
		// 判断一下骚扰拦截服务是否运行
		mIsIntercept = ActivityUtils.isRunningService(this,
				InterceptService.class);
		// 根据服务的开启与否状态来给骚扰拦截设置更新按钮状态
		mSivIntercept.setSettingOnButton(mIsIntercept);
		/*// 保存一下状态
		SpUtils.save(this, Constants.IS_INTERCEPT, mIsIntercept);*/

		// 判断归属地显示服务是否运行
		mIsShowArea = ActivityUtils.isRunningService(this,
				BelongAddressService.class);
		//根据服务的开启状态来给归属地显示设置更新按钮状态
		mSivArea.setSettingOnButton(mIsShowArea);
		/*//保存一下状态
		SpUtils.save(thisContext, Constants.PHONE_AREA_OPEN_SHOW, mIsShowArea);*/
	}
}
