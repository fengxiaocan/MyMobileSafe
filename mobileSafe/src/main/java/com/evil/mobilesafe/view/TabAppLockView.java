package com.evil.mobilesafe.view;

import com.evil.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabAppLockView extends LinearLayout {

	private TextView mTvLeft;
	private TextView mTvRight;
	private boolean isLeftSelect = true; // 默认模式是左边的未加锁状态

	public TabAppLockView(Context context, AttributeSet attrs) {
		// 在xml中创建
		super(context, attrs);
		// 初始化控件
		initView();
		initListener();
		settData();
	}

	// 初始化控件
	private void initView() {
		// this代表添加进TabAppLockView这个父控件中
		View view = View.inflate(getContext(), R.layout.view_applock_tab, this);

		// 找到布局文件中的各个id
		mTvLeft = (TextView) view.findViewById(R.id.tv_applock_left);
		mTvRight = (TextView) view.findViewById(R.id.tv_applock_right);
	}

	// 设置数据
	private void settData() {
		// 初始化选中状态
		if (isLeftSelect) {
			mTvLeft.setSelected(true); // 选择未加锁的状态
			mTvRight.setSelected(false);
		} else {
			mTvLeft.setSelected(false);
			mTvRight.setSelected(true);// 选择已加锁的状态
		}

		// 把处理数据 反馈给调用者
		if (mOnLockChangeLinstener != null) {
			// 把数据状态回调给调用者
			mOnLockChangeLinstener.onChanged(isLeftSelect);
		}
	}

	private OnLockChangeLinstener mOnLockChangeLinstener;

	/**
	 * 设置监听器,回调给控件本身,让控件来处理事件
	 */
	public void setOnLockChangeLinstener(OnLockChangeLinstener linstener) {
		mOnLockChangeLinstener = linstener;
	}

	// 接口回调
	/**
	 * 接口回调设置View点击事件
	 */
	public interface OnLockChangeLinstener {
		/**
		 * @param isLeftTab
		 *            true 左边的未加锁状态 false 右边的已加锁状态
		 */
		void onChanged(boolean isLeftTab);
	}

	private void initListener() {
		// 给两个标签设置点击事件
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_applock_left: // 左边的未加锁
					isLeftSelect = true;
					break;
				case R.id.tv_applock_right: // 右边的已加锁
					isLeftSelect = false;
					break;
				default:
					break;
				}
				// 初始化事件
				settData();
			}
		};
		mTvLeft.setOnClickListener(listener);
		mTvRight.setOnClickListener(listener);
	}

	public TabAppLockView(Context context) {
		// 代码创建
		this(context, null);
	}

}
