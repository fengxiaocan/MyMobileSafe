package com.evil.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evil.mobilesafe.R;

public class SpaceInfoProgress extends RelativeLayout {

	private Context mContext;
	private TextView mTvTitle;
	private TextView mTvUse;
	private TextView mTvFree;
	private View mView;
	private ProgressBar mProgress;

	public SpaceInfoProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		// 查找控件.....注意,一定要加载到this父控件中
		mView = View.inflate(mContext, R.layout.item_spaceinfo_progress, this);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();
	}

	public SpaceInfoProgress(Context context) {
		this(context, null);
	}

	private void initView() {

		mTvUse = (TextView) mView.findViewById(R.id.tv_progress_item_used);
		mTvFree = (TextView) mView.findViewById(R.id.tv_progress_item_free);
		mProgress = (ProgressBar) mView.findViewById(R.id.progress_state);

	}

	/**
	 * 设置进度条
	 */
	public void setProgress(int progress) {
		mProgress.setProgress(progress);

		// 设置默认最大值
		mProgress.setMax(100);
	}

	/**
	 * 设置已使用的空间
	 */
	public void setUseMessage(String message) {
		mTvUse.setText(message);
	}

	/**
	 * 设置可用的空间
	 */
	public void setFreeMessage(String message) {
		mTvFree.setText(message);
	}

	private void initListener() {
	}

	private void initData() {

	}

}
