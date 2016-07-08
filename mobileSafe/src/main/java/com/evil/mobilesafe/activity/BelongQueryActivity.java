package com.evil.mobilesafe.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.BelongQueryDao;

//号码归属地查询
public class BelongQueryActivity extends Activity {

	private EditText mEtNumber;
	private TextView mTvShow;
	private Button mBtQuery;
	private BelongQueryDao mBelongDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_belong_query);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();
	}

	private void initView() {
		// 输入框
		mEtNumber = (EditText) findViewById(R.id.et_belong_query_number);
		// 查询按钮
		mBtQuery = (Button) findViewById(R.id.bt_belong_query);
		// 归属地显示
		mTvShow = (TextView) findViewById(R.id.tv_belong_query_show);
	}

	private void initListener() {

		// 查询按钮点击事件
		mBtQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = mEtNumber.getText().toString().trim();

				if (TextUtils.isEmpty(number)) {
					// 震动
					Vibrator vibrator = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(1000);

					// 抖动文本框
					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);
					findViewById(R.id.et_belong_query_number).startAnimation(
							shake);

					Toast.makeText(getApplicationContext(), "号码不能为空",
							Toast.LENGTH_SHORT).show();

					return;
				}

				String queryArea = mBelongDao.queryArea(number);

				mTvShow.setText("归属地:" + queryArea);
			}
		});

		// 输入框变化回调事件
		TextWatcher watcher = new TextWatcher() {
			// 文本发生变化的时候
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			// 文本发生变化之前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			// 文本发生变化之后
			@Override
			public void afterTextChanged(Editable s) {
				String phone = mEtNumber.getText().toString().trim();

				String area = mBelongDao.queryArea(phone);

				mTvShow.setText("归属地:" + area);
			}
		};
		mEtNumber.addTextChangedListener(watcher);
	}

	private void initData() {
		mBelongDao = new BelongQueryDao(this);
		
	}
}
