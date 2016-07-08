package com.evil.mobilesafe.activity;

import android.os.Bundle;
import android.view.View;

import com.evil.mobilesafe.R;

/**
 * 设置向导界面1
 */
public class SetGuide1Activity extends BaseSetGuideActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setguide1);

	}
	
	public void next(View v){
		next();
	}

	@Override
	public void prev() {
		// 没有上一个界面
	}

	@Override
	public void next() {
		nextActivity(SetGuide2Activity.class);
	}
}
