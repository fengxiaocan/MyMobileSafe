package com.evil.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.evil.mobilesafe.R;

public abstract class BaseSetGuideActivity extends Activity {

	private GestureDetector mGd;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		mGd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			//快速滑动屏幕的回调方法
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				float e1X = e1.getX();
				float e2X = e2.getX();
				
//				System.out.println("e1X:"+e1X+"   e2X:"+e2X);
				
				//假如速率不够快,则不能跳转界面
				if (Math.abs(velocityX) < 100) {
					return false;
				}
				
				
				//假如由右向左滑动,则e1X-e2X为正
				if ((e1X - e2X )> 0) {
					next();				//跳转到下一个界面
				}else{
				//假如由左向右滑动,则e1X-e2X为负
					prev();				//跳转到上一个界面
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
		});

	}
	
	//调用手势滑动方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGd.onTouchEvent(event);			//调用手势滑动的方法
		return true;				//不使用父类的方法,子类自己能处理
	}
	
	public abstract void prev();
	
	public abstract void next();

	/**
	 * 跳转上一个界面
	 */
	public void prevActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(getApplicationContext(), clazz);
		startActivity(intent);
		
		//切换界面的动画
		overridePendingTransition(R.anim.setguide_prevactivity_into, R.anim.setguide_prevactivity_exit);
		
		finish();
	}
	/**
	 * 跳转到下一个界面
	 */
	public void nextActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(getApplicationContext(), clazz);
		startActivity(intent);
		//切换界面的动画
		overridePendingTransition(R.anim.setguide_nextvactivity_into, R.anim.setguide_nextvactivity_exit);
		
		finish();
	}
}
