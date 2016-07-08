package com.evil.mobilesafe.activity;

import java.io.File;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.GZipUtils;
import com.evil.mobilesafe.utils.SpUtils;

public class MainActivity extends Activity {

	private ImageView mIvMainLogo;
	private Context thisContext = MainActivity.this;
	private ImageView mIvSetting;
	private final static String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
			"软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private final static String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
			"管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };

	private final static int[] ICONS = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };
	private MainAdapter mAdapter;
	private GridView mGv;
	private long mTimeTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 查找控件
		initFindView();

		// 设置动画
		setImageAnimator();

		// 设置数据
		initData();

		// 监听器
		initListener();

	}

	private void initData() {
		mAdapter = new MainAdapter();
		mGv.setAdapter(mAdapter);

	}

	/**
	 * 初始化控件
	 */
	public void initFindView() {
		mIvSetting = (ImageView) findViewById(R.id.iv_main_setting);
		mIvMainLogo = (ImageView) findViewById(R.id.iv_main_logo);
		mGv = (GridView) findViewById(R.id.gv_main);
	}

	/**
	 * 监听器
	 */
	public void initListener() {
		/**
		 * 监听菜单键
		 */
		mIvSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SettingActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

		// 主界面点击GV条目
		mGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {
				case 0: // 点击手机防盗
					// 取出密码
					String pwd = SpUtils.getInfo(thisContext,
							Constants.PASSWORD, "");
					if (pwd.equals("")) {
						setPasswordDialog();
					} else {
						setInputPasswordDialog();
					}

					break;

				case 1:
					// 跳转到黑名单管理界面
					startActivity(InterceptActivity.class);
					break;
				case 2:
					// 跳转到软件管家界面
					startActivity(AppManagerActivity.class);
					break;
				case 3:
					// 跳转到进程管理界面
					startActivity(TaskManagerActivity.class);
					break;
				case 4:
					// 跳转到流量统计界面
					startActivity(FluxStatusActivity.class);
					break;
				case 5:
					// 跳转到进程管理界面
					startActivity(KillVirusActivity.class);
					break;
				case 6:
					// 跳转到缓存清理界面
					startActivity(CacheClearActivity.class);
					break;
				case 7:
					// 跳转到常用工具界面
					startActivity(CommonActivity.class);
					break;

				default:
					break;
				}
			}

		});
	}

	// 第二次进入确认密码
	private void setInputPasswordDialog() {

		// 把布局文件转换为view对线
		View view = View.inflate(this, R.layout.input_password_dialog, null);
		Button sure = (Button) view.findViewById(R.id.bt_inputpassword_sure);
		Button cancle = (Button) view
				.findViewById(R.id.bt_inputpassword_cancle);

		final EditText et_pwd = (EditText) view
				.findViewById(R.id.et_main_input_password_pwd);

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setView(view);
		final AlertDialog dialog = ab.create();

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.bt_inputpassword_sure) {
					// 取出密码来确认是否密码正确
					String pwd = et_pwd.getText().toString();

					String password = SpUtils.getInfo(thisContext,
							Constants.PASSWORD, "");
					if (TextUtils.isEmpty(pwd)) {
						Toast.makeText(thisContext, "请输入密码", Toast.LENGTH_SHORT)
								.show();
						return;
					}

					if (password.equals(pwd)) {
						dialog.dismiss();
						startRetrieve();
					} else {
						Toast.makeText(thisContext, "密码错误", Toast.LENGTH_SHORT)
								.show();
						return;
					}

				} else if (v.getId() == R.id.bt_inputpassword_cancle) {
					dialog.dismiss();
				}
			}
		};

		sure.setOnClickListener(listener);
		cancle.setOnClickListener(listener);

		// 禁止点空白取消dialog
		dialog.setCancelable(false);
		dialog.show(); // 不能用abshow(),因为就是两个不同的对象
	}

	// 初始设置防盗密码
	public void setPasswordDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		// 把布局文件转换为view对线

		View view = View.inflate(this, R.layout.set_password_dialog, null);
		ab.setView(view);

		final EditText mEt_pwd = (EditText) view
				.findViewById(R.id.et_main_setpassword_pwd);
		final EditText mEt_pwd2 = (EditText) view
				.findViewById(R.id.et_main_setpassword_pwd2);
		final AlertDialog dialog = ab.create();

		// 确定按钮
		view.findViewById(R.id.bt_setpassword_sure).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String pwd = mEt_pwd.getText().toString();
						String pwd2 = mEt_pwd2.getText().toString();
						if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd2)) {
							Toast.makeText(MainActivity.this, "密码不能为空",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (!pwd.equals(pwd2)) {
							Toast.makeText(MainActivity.this, "密码不一致",
									Toast.LENGTH_SHORT).show();
							return;
						}
						SpUtils.save(getBaseContext(), Constants.PASSWORD, pwd);
						dialog.dismiss();

						startRetrieve();
					}
				});
		// 取消按钮
		view.findViewById(R.id.bt_setpassword_cancle).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		// 禁止随便取消dialog
		dialog.setCancelable(false);
		dialog.show();
	}

	/**
	 * 设置logo动画不停的旋转
	 */
	public void setImageAnimator() {
		ObjectAnimator obj = ObjectAnimator.ofFloat(mIvMainLogo, "rotationY",
				0, 50, 120, 180, 260, 330);
		obj.setDuration(3000);
		obj.setRepeatCount(ObjectAnimator.INFINITE);
		obj.setRepeatMode(ObjectAnimator.REVERSE);
		obj.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 适配器
	public class MainAdapter extends BaseAdapter {
		public int getCount() {

			return TITLES.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(MainActivity.this,
						R.layout.item_main_gv_view, null);
			}

			ImageView iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_item_main_gv_icon);
			TextView tv_title = (TextView) convertView
					.findViewById(R.id.tv_item_main_gv_title);
			TextView tv_desc = (TextView) convertView
					.findViewById(R.id.tv_item_main_gv_desc);

			iv_icon.setImageResource(ICONS[position]);
			tv_title.setText(TITLES[position]);
			tv_desc.setText(DESCS[position]);

			return convertView;
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

	}

	// 跳转到手机防盗界面或者设置向导1
	public void startRetrieve() {
		boolean isSetREtrieve = SpUtils.getInfo(thisContext,
				Constants.SET_RETRIEVE, false);
		if (isSetREtrieve) {
			// 跳转到手机防盗界面

			startActivity(RetrieveActivity.class);

		} else {
			// 跳转到防盗设置向导界面
			startActivity(SetGuide1Activity.class);
		}

	}

	// 按返回键 的时候调用
	@Override
	public void onBackPressed() {
		if (mTimeTag > 0) {
			long timeMillis = System.currentTimeMillis();
			if (timeMillis - mTimeTag < 2000) {
				super.onBackPressed(); // 退出app
			}
		}

		Toast.makeText(thisContext, "再按一次返回键将退出", Toast.LENGTH_SHORT).show();
		mTimeTag = System.currentTimeMillis();
	}

	// 跳转界面
	public void startActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}

}
