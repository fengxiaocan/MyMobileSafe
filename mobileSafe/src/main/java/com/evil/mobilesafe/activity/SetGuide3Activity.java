package com.evil.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

/**
 * 设置向导界面3
 */
public class SetGuide3Activity extends BaseSetGuideActivity {
	private TextView mTvSelect;
	private final int mRequestCode = 100;
	private String mSafePhone;
	private EditText mEtPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setguide3);

		// 初始化控件
		initView(); 
		// 设置监听器
		initListener();

		// 初始化数据
		initData();

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		//选择安全号码
		mTvSelect = (TextView) findViewById(R.id.tv_setguide3_select);
		
		//输入安全号码
		mEtPhone = (EditText) findViewById(R.id.et_setguide_safephone);
	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		mTvSelect.setOnClickListener(new OnClickListener() {
			
			

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetGuide3Activity.this,ContactsActivity.class);
				
				startActivityForResult(intent, mRequestCode);
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		//安全号码回显
		String phone = SpUtils.getInfo(getApplicationContext(), Constants.SAFE_PHONE, "");
		
		mEtPhone.setText(phone);
		
		//光标后移
		mEtPhone.setSelection(phone.length());
	}

	public void pre(View v) {
		prev();
	}

	public void next(View v) {
		next();
	}

	@Override
	public void prev() {
		prevActivity(SetGuide2Activity.class);
	}

	@Override
	public void next() {
		if (TextUtils.isEmpty(mEtPhone.getText())) {
			Toast.makeText(getApplicationContext(), "请选择安全号码", Toast.LENGTH_SHORT)
					.show();
		} else {
			mSafePhone = mEtPhone.getText().toString().trim();
			SpUtils.save(getApplicationContext(), Constants.SAFE_PHONE, mSafePhone);
			nextActivity(SetGuide4Activity.class);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == mRequestCode) {
			if (resultCode == RESULT_OK) {
				mSafePhone = data.getStringExtra(Constants.SAFE_PHONE);
				
				mEtPhone.setText(mSafePhone);
				
				//把光标往后移动
				mEtPhone.setSelection(mEtPhone.length());
			}
		}
		
	}
	
	@Override
	protected void onPause() {
		if (TextUtils.isEmpty(mEtPhone.getText())) {
			//把防盗设置设置为不开启
			SpUtils.save(getApplicationContext(), Constants.SET_RETRIEVE, false);
		}
		super.onPause();
	}
}
