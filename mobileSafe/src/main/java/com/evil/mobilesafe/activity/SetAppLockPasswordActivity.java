/**
 * 
 */
package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.PrintLog;
import com.evil.mobilesafe.utils.SpUtils;
import com.zhy.zhy_gesturelockview.view.GestureLockViewGroup;
import com.zhy.zhy_gesturelockview.view.GestureLockViewGroup.OnGestureLockViewListener;

/**
 * @author 风小灿 设置看门狗口令界面
 */
public class SetAppLockPasswordActivity extends Activity {
	private GestureLockViewGroup mGestureLock;
	private TextView mTvTitle;
	private List<Integer> passList = new ArrayList<Integer>(); // 手势密码
	private boolean isFirst = true; // 手势密码输入的次数
	private List<Integer> mPassList2; // 验证二次输入的密码是否正确

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setpassword_applogdog);

		// 初始化控件
		initView();
		// 初始化数据
		initData();
		// 初始化监听器
		initListener();
	}

	/**
	 * 
	 */
	private void initView() {
		mGestureLock = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
		// 标题提示
		mTvTitle = (TextView) findViewById(R.id.tv_applogdog_setpassword_title);
	}

	/**
	 * 
	 */
	private void initData() {

	}

	/**
	 * 获取输入的手势密码
	 */
	private void initListener() {
		mGestureLock
				.setOnGestureLockViewListener(new OnGestureLockViewListener() {

					@Override
					public void onUnmatchedExceedBoundary() {
					}

					@Override
					public void onGestureEvent(boolean matched) {
					}

					@Override
					public void onBlockSelected(int cId) {
						PrintLog.log(cId);
						passList.add(cId); // 记录密码
					}
				});
		mGestureLock.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 手指按下

					break;
				case MotionEvent.ACTION_UP: // 手指松开

					if (!isFirst) { // 输入第二次

						if (passList.size() != mPassList2.size()) {
							Toast.makeText(getApplicationContext(),
									"两次密码不正确密码", 0).show();
							mTvTitle.setText("请滑动手指,输入手势密码");

							isFirst = true;//设置为第一次扫描

							return false;
						} else {
							StringBuffer sb = new StringBuffer();

							// 比较两次手势密码是否想等
							for (int i = 0; i < passList.size(); i++) {
								if (passList.get(i) != mPassList2.get(i)) {
									// 密码不匹配
									Toast.makeText(getApplicationContext(),
											"两次手势密码不一致", 0).show();
									mTvTitle.setText("请滑动手指,输入手势密码");

									isFirst = true;//设置为第一次扫描
									sb = null;

									return false;
								}

								sb.append(passList.get(i) + "--");
							}
							String password = sb.toString();
							PrintLog.log(password);

							// 记录手势密码
							SpUtils.save(getApplicationContext(),
									Constants.APP_LOCK_PASSWORD, password);

							Toast.makeText(getApplicationContext(), "手势密码设置成功",
									0).show();

							isFirst = true;//设置为第一次扫描

							//跳转到程序锁管理界面
							Intent intent = new Intent(getApplicationContext(),AppLockActivity.class);
							startActivity(intent);
							
							finish();// 关闭自身界面
						}
					} else {
						// 输入第一次手势密码
						if (passList != null && passList.size() == 0) {
							Toast.makeText(getApplicationContext(),
									"请滑动手势输入密码", 0).show();
							return false;
						}
						mPassList2 = new ArrayList<Integer>(); // 手势密码
						mPassList2.addAll(passList); // 记录第一次手势密码
						passList.clear();

						mTvTitle.setText("请再次输入手势密码");
						
						isFirst = false;//设置为第二次扫描
					}
					break;

				default:
					break;
				}

				return false; // 自己能处理
			}
		});
	}

}
