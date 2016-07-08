package com.evil.mobilesafe.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

/**
 * 设置向导界面5
 */
public class SetGuide5Activity extends BaseSetGuideActivity {
	private CheckBox mCb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setguide5);

		initView();
		initData();
	}
	//初始化控件
	private void initView() {
		mCb = (CheckBox) findViewById(R.id.cb_setguide5_openfind);
	}
	//初始化数据
	private void initData() {
		boolean isCheck = SpUtils.getInfo(getApplicationContext(), Constants.SET_RETRIEVE, false);
		//回显勾选状态
		mCb.setChecked(isCheck);
	}

	public void pre(View v) {
		prev();
	}

	public void next(View v) {
		next();
	}

	@Override
	public void prev() {
		prevActivity(SetGuide4Activity.class);
	}

	@Override
	public void next() {
		//假如选择开启
		if (mCb.isChecked()) {
			SpUtils.save(getApplicationContext(), Constants.SET_RETRIEVE, mCb.isChecked());
			nextActivity(RetrieveActivity.class);
		}else {
			Toast.makeText(getApplicationContext(), "要开启防盗功能必须勾选", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
