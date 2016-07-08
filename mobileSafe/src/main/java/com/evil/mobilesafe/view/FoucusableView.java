package com.evil.mobilesafe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 这是一个自定义TextView,用于欺骗系统获取焦点,设置跑马灯不停的显示
 */
public class FoucusableView extends TextView{

	public FoucusableView(Context context) {
		super(context);
		
	}

	public FoucusableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//布局文件中直接使用
		//设置焦点
		setFocusable(true);
		//设置焦点mode
		setFocusableInTouchMode(true);
		//设置单行显示
		setSingleLine(true);
		//设置跑马灯效果
		setEllipsize(TruncateAt.MARQUEE);
		
	}
	
	@Override
	public boolean isFocused() {
		//欺骗系统, 我获取到了焦点
		return true;
	}
	/**
	 * 当焦点改变时的回调方法
	 */
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused) {
			//当焦点改变为true的时候才调用方法
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}else{
			//不做操作
		}
	}

	/**
	 * 当窗口焦点改变的时候回调
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}
}
