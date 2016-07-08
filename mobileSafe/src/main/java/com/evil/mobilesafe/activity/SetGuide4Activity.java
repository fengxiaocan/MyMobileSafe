package com.evil.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.receiver.DeviceAdminSample;
import com.evil.mobilesafe.utils.SpUtils;

/**
 * 设置向导界面4
 */
public class SetGuide4Activity extends BaseSetGuideActivity {
	private static final int REQUEST_CODE_ENABLE_ADMIN = 100;
	private RelativeLayout mLayout;
	private ImageView mIvAdmin;
	private DevicePolicyManager mMDPM;
	private ComponentName mWho;
	private boolean mOpenAdmin; // 是否开启设备管理器

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setguide4);

		// 初始化控件
		initView();
		// 设置监听器
		initListener();
		// 初始化数据
		initData();

	}

	private void initView() {
		mLayout = (RelativeLayout) findViewById(R.id.rl_retrieve_open_admin);

		mIvAdmin = (ImageView) findViewById(R.id.iv_retrieve_admin_inactivated_icon);

	}

	private void initListener() {
		mLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOpenAdmin) {

					mOpenAdmin = !mOpenAdmin;

					// 移除设备管理器
					if (mMDPM != null) {

						mMDPM.removeActiveAdmin(mWho);

					}

					setIcon();
				} else {
					mOpenAdmin = !mOpenAdmin;
					// 开启设备管理器

					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mWho);

					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"开启手机防盗模式,手机丢失好找回");

					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				}
			}
		});

	}

	private void initData() {

		mMDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		mWho = new ComponentName(this, DeviceAdminSample.class);
		mOpenAdmin = mMDPM.isAdminActive(mWho);

		// 设置图片
		setIcon();
	}

	public void setIcon() {
		if (mOpenAdmin) {
			mIvAdmin.setImageResource(R.drawable.admin_activated);
		} else {
			mIvAdmin.setImageResource(R.drawable.admin_inactivated);

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			if (resultCode == RESULT_OK) {
				setIcon();
			}
		}
	}

	public void pre(View v) {
		prev();
	}

	public void next(View v) {
		if (mOpenAdmin) {
			next();
		} else {
			Toast.makeText(getApplicationContext(), "要开启手机防盗,必须要开启设备管理器",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void prev() {
		prevActivity(SetGuide3Activity.class);
	}

	@Override
	public void next() {
		nextActivity(SetGuide5Activity.class);
	}

	@Override
	protected void onPause() {
		if (!mOpenAdmin) {
			// 把防盗设置设置为不开启
			SpUtils.save(getApplicationContext(), Constants.SET_RETRIEVE, false);
		}
		super.onPause();
	}
}
