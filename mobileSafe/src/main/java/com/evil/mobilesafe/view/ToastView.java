package com.evil.mobilesafe.view;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class ToastView implements OnTouchListener {
	private Context mContext;
	private WindowManager mWm;
	private WindowManager.LayoutParams mParams;
	private View mView;
	private float mDownX;
	private float mDownY;

	// 第一步,创建构造函数,传递进上下文
	public ToastView(Context context) {
		mContext = context;

		// 2.获取窗体管理器
		mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		mParams = new WindowManager.LayoutParams(); 					// 数据设置
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT; 		// 控件高度
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT; 		// 控件宽度
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 	// 不能获取焦点
				/*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE */		// 不能点击
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; 		// 显示在窗体的前面
		// 去除了动画
		mParams.format = PixelFormat.TRANSLUCENT;
		/*
		 * mParams.type = WindowManager.LayoutParams.TYPE_TOAST; //默认是吐司的类型
		 */
		// 修改吐司类型,在电话的前面显示,加上权限,SYSTEM_ALERT_WINDOW
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		
		//设置位置值
		mParams.gravity = Gravity.LEFT | Gravity.TOP;	//设置初始位置的值
		mParams.x = SpUtils.getInfo(context, Constants.TOAST_X, 100);
		mParams.y = SpUtils.getInfo(context, Constants.TOAST_Y, 100);
		
	}

	/**
	 * 显示自定义吐司
	 */
	public void show(String message) {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWm.removeView(mView);
			}
			mView = null;
		}

		// 4.把自定义布局文件转换为View
		mView = View.inflate(mContext, R.layout.belong_address_toast, null);

		// 给View添加点击事件
		mView.setOnTouchListener(this);
		
		//给View设置颜射
		int style = SpUtils.getInfo(mContext, Constants.DIALOG_STYLE_COLOR, 0);
		mView.setBackgroundResource(BelongShowDialog.STYLE_ICON[style]);
		
		// 查找TextView文本显示控件
		TextView tv = (TextView) mView.findViewById(R.id.tv_belong_toast_text);
		// 显示文本
		tv.setText(message);

		// 5.把view加载进窗体中
		mWm.addView(mView, mParams);
	}

	/**
	 * 移除掉Toast
	 */
	public void hide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWm.removeView(mView);
			}
			mView = null;
		}
	}

	// 给自定义吐司加上点击事件
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下的动作
			
			mDownX = event.getRawX();		//获取相对屏幕的x坐标
			mDownY = event.getRawY();		//获取相对屏幕的Y坐标
			
			break;
		case MotionEvent.ACTION_MOVE: // 移动的动作
			
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			
			//求出移动的值
			float dX = moveX - mDownX;
			float dY = moveY - mDownY;
			//设置toast的移动坐标
			mParams.x += dX;
			mParams.y += dY;
			
			//判断是否越界
			if (mParams.x < 0) {
				mParams.x = 0;
			}else if (mParams.x > mWm.getDefaultDisplay().getWidth() - mView.getWidth()) {
				mParams.x = mWm.getDefaultDisplay().getWidth() - mView.getWidth();
			}
			
			if (mParams.y < 0) {
				mParams.y = 0;
			}else if (mParams.y > mWm.getDefaultDisplay().getHeight() - mView.getHeight()) {
				mParams.y = mWm.getDefaultDisplay().getHeight() - mView.getHeight();
			}
			
			mWm.updateViewLayout(mView, mParams);
			
			//重置down值
			mDownX = moveX;
			mDownY = moveY;
			break;
		case MotionEvent.ACTION_UP: // 弹起的动作
			//记录toast的位置
			SpUtils.save(mContext, Constants.TOAST_X, mParams.x);
			SpUtils.save(mContext, Constants.TOAST_X, mParams.y);
			
			break;

		default:
			break;
		}

		return true; // 自己处理
	}
}
