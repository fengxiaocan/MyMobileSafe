package com.evil.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.InterceptDao;
import com.evil.mobilesafe.domain.InterceptInfo;
import com.evil.mobilesafe.interfaces.Constants;

public class AddBlackNumActivity extends Activity {

	private TextView mTitle;
	private EditText mInput;
	private RadioGroup mRg;
	private Button mBtSave;
	private Button mBtCancle;
	private boolean mIsUpdate;
	private InterceptDao dao;
	private InterceptInfo mInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_intercept_input_or_update);

		// 初始化控件
		initView();
		// 设置监听器
		initListener();
		// 初始化数据
		initData();
	}

	private void initView() {
		// 标题
		mTitle = (TextView) findViewById(R.id.tv_intercept_add_or_update_title);
		// 输入框
		mInput = (EditText) findViewById(R.id.et_intercept_add_or_update_input);
		// 单选框组
		mRg = (RadioGroup) findViewById(R.id.rg_intercept_add_or_update_rg);
		// 保存按钮
		mBtSave = (Button) findViewById(R.id.bt_intercept_add_or_update_save);
		// 取消按钮
		mBtCancle = (Button) findViewById(R.id.bt_intercept_add_or_update_cancle);

	}

	private void initListener() {
		mBtCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish(); // 关闭自己
			}
		});

		mBtSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsUpdate) { // 假如是更新黑名单界面
					int radioButtonId = mRg.getCheckedRadioButtonId();

					switch (radioButtonId) {
					case R.id.rb_intercept_add_or_update_phone:
						mInfo.type = Constants.INTRECEPT_TYPE_PHONE;
						mInfo.desc = "电话拦截";
						break;
					case R.id.rb_intercept_add_or_update_sms:
						mInfo.type = Constants.INTRECEPT_TYPE_SMS;
						mInfo.desc = "短信拦截";
						break;
					case R.id.rb_intercept_add_or_update_all:
						mInfo.type = Constants.INTRECEPT_TYPE_ALL;
						mInfo.desc = "电话+短信拦截";
						break;
					}

					dao.update(mInfo);

					Intent intent = new Intent();

					intent.putExtra(Constants.INTERCEPT_INFO, mInfo);

					setResult(RESULT_OK, intent);

					finish();

				} else {
					// 假如是添加黑名单界面
					String number = mInput.getText().toString().trim();

					if (TextUtils.isEmpty(number)) {
						Toast.makeText(getApplicationContext(), "号码不能为空",
								Toast.LENGTH_SHORT).show();
						return;
					}

					InterceptInfo info = new InterceptInfo();

					info.phone = number;

					int radioButtonId = mRg.getCheckedRadioButtonId();

					switch (radioButtonId) {
					case R.id.rb_intercept_add_or_update_phone:
						info.type = Constants.INTRECEPT_TYPE_PHONE;
						info.desc = "电话拦截";
						break;
					case R.id.rb_intercept_add_or_update_sms:
						info.type = Constants.INTRECEPT_TYPE_SMS;
						info.desc = "短信拦截";
						break;
					case R.id.rb_intercept_add_or_update_all:
						info.type = Constants.INTRECEPT_TYPE_ALL;
						info.desc = "电话+短信拦截";
						break;
					}

					boolean add = dao.add(info);

					if (!add) {
						Toast.makeText(getApplicationContext(), "添加失败,号码已经存在",
								Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent();

					intent.putExtra(Constants.INTERCEPT_INFO, info);

					setResult(RESULT_OK, intent);

					finish();

				}

			}
		});
	}

	private void initData() {
		dao = new InterceptDao(this);

		mIsUpdate = getIntent().getBooleanExtra("isUpdate", false);

		mInfo = (InterceptInfo) getIntent().getSerializableExtra(
				"interceptInfo");
		if (mIsUpdate) {
			mTitle.setText("更新黑名单");
			mBtSave.setText("更新");
			mInput.setEnabled(false);
			mInput.setBackgroundResource(R.drawable.et_intercept_bg_update);

			mInput.setText(mInfo.phone);

			int type = mInfo.type;

			switch (type) {
			case Constants.INTRECEPT_TYPE_PHONE:
				RadioButton rbPhone = (RadioButton) findViewById(R.id.rb_intercept_add_or_update_phone);
				rbPhone.setChecked(true);
				break;
			case Constants.INTRECEPT_TYPE_SMS:
				RadioButton rbSms = (RadioButton) findViewById(R.id.rb_intercept_add_or_update_sms);
				rbSms.setChecked(true);
				break;
			case Constants.INTRECEPT_TYPE_ALL:
				RadioButton rbAll = (RadioButton) findViewById(R.id.rb_intercept_add_or_update_all);
				rbAll.setChecked(true);
				break;

			default:
				break;
			}
		}

	}
}
