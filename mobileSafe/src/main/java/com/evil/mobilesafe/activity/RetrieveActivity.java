package com.evil.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

/**
 * 手机防盗界面
 */
public class RetrieveActivity extends Activity {

	private TextView mTv_anew_set;
	private RelativeLayout mLayout;
	private ImageView mIvLock;
	private TextView mTvNumber;
	private boolean mIsOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_retrieve);
		// 初始化/查找控件
		initView();
		// 初始化数据
		initData();
		// 初始化listener
		initListener();

	}

	private void initView() {
		// 重新设置向导
		mTv_anew_set = (TextView) findViewById(R.id.anew_retrieve_setduide1);
		// 防盗保护开启
		mLayout = (RelativeLayout) findViewById(R.id.rl_retrieve_open_protect);
		// 防盗保护锁的状态
		mIvLock = (ImageView) findViewById(R.id.iv_retrieve_lock_icon);
		//安全号码
		mTvNumber = (TextView) findViewById(R.id.tv_retrieve_safephone);
	}

	private void initData() {
		//安全号码回显
		String safePhone = SpUtils.getInfo(getApplicationContext(), Constants.SAFE_PHONE, "");
		
		mTvNumber.setText(safePhone);
		
		mIsOpen = SpUtils.getInfo(getApplicationContext(), Constants.SET_RETRIEVE, false);
		
		showLock();		//回显状态
	}

	private void initListener() {

		// 重新设置向导
		mTv_anew_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到设置向导1
				Intent intent = new Intent(getApplicationContext(),
						SetGuide1Activity.class);
				startActivity(intent);
				
				
				// 关闭自身
				finish();
			}
		});

		mLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsOpen) {
					Toast.makeText(getApplicationContext(), "关闭手机防盗功能将不能保护您的手机安全",
							Toast.LENGTH_SHORT).show();
					mIsOpen = ! mIsOpen;
					showLock();
					SpUtils.save(getApplicationContext(), Constants.SET_RETRIEVE, mIsOpen);
				}else {
					mIsOpen = ! mIsOpen;
					SpUtils.save(getApplicationContext(), Constants.SET_RETRIEVE, mIsOpen);
					showLock();
				}
			}
		});
	}
	
	/**
	 * 防盗锁显示状态设置
	 */
	public void showLock(){
		if (mIsOpen) {
			mIvLock.setImageResource(R.drawable.lock);
		}else {
			mIvLock.setImageResource(R.drawable.unlock);
		}
	}
}
