package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.rtp.RtpStream;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.InterceptDao;
import com.evil.mobilesafe.domain.InterceptInfo;
import com.evil.mobilesafe.interfaces.Constants;

/**
 * 黑名单管理界面
 * 
 * @author 风小灿
 * @date 2016-6-19
 */
public class InterceptActivity extends Activity {

	protected static final int callLogRequestCode = 204; // 跳转到通话记录界面
	protected static final int smsLogRequestCode = 205;	//跳转到短信记录界面
	private final int contactsRequestCode = 201; // 跳转到联系人界面的请求码
	private final int addRequestCode = 202; // 跳转到手动添加黑名单界面的请求码
	private final int updateRequestCode = 203; // 跳转到更新黑名单界面的请求码

	private ListView mLv;
	private ImageView mIv;
	private InterceptAdapter mAdapter;
	private int mPageSize = 10;
	private List<InterceptInfo> mList;
	private ProgressBar mPb;
	private InterceptDao mDao;
	private boolean isHasData = true; // 判断是否在数据库中获取到了数据
	private PopupWindow mPw;
	private ImageView mIvAdd;
	private TextView mInputAdd;
	private TextView mPhoneAdd;
	private TextView mSmsAdd;
	private TextView mContactsAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_intercept);

		// 初始化控件
		initView();
		// 设置监听器
		initListener();
		// 初始化数据
		initData();

	}

	private void initView() {

		// 获取到listView
		mLv = (ListView) findViewById(R.id.lv_intercept);

		// 获取到背景图片
		mIv = (ImageView) findViewById(R.id.iv_intercept_empty_icon);

		mPb = (ProgressBar) findViewById(R.id.progressBar);

		mIvAdd = (ImageView) findViewById(R.id.iv_intercept_add_icon);

		View contentView = View.inflate(this,
				R.layout.menu_popupwindow_intercept, null);
		mPw = new PopupWindow(contentView, -2, -2);
		// 设置焦点可以获取
		mPw.setFocusable(true);
		// 设置背景透明
		mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 设置外部点击可以取消
		mPw.setOutsideTouchable(true);

		// 获取弹出窗体中 的控件
		mInputAdd = (TextView) contentView
				.findViewById(R.id.tv_popupwindow_input_add);
		mPhoneAdd = (TextView) contentView
				.findViewById(R.id.tv_popupwindow_phone_add);
		mSmsAdd = (TextView) contentView
				.findViewById(R.id.tv_popupwindow_sms_add);
		mContactsAdd = (TextView) contentView
				.findViewById(R.id.tv_popupwindow_contacts_add);
	}

	private void initListener() {
		// 点击条目更新黑名单拦截方式
		mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InterceptInfo info = mList.get(position);

				openActivity(AddBlackNumActivity.class, true,
						updateRequestCode, info);
			}
		});

		// 下拉滑动刷新数据
		mLv.setOnScrollListener(new OnScrollListener() {
			// 当滚动发生变化的回调
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// System.out.println("scrollState:"+scrollState);
				// 获取最后一条可见的条目
				int lastVisiblePosition = view.getLastVisiblePosition();

				if (lastVisiblePosition == mList.size() - 1) {
					getData(mPageSize, mList.size()); // 下拉的时候更新一个列表条目
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// System.out.println("visibleItemCount:"+visibleItemCount +
				// "  totalItemCount:"+totalItemCount);
			}
		});

		// 添加联系人进黑名单dialog按钮监听
		mIvAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPw.showAsDropDown(mIvAdd);
			}
		});

		mInputAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 跳转到添加黑名单
				openActivity(AddBlackNumActivity.class, false, addRequestCode);

				mPw.dismiss();
			}
		});
		mPhoneAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(CallLogActivity.class, false, callLogRequestCode);
				mPw.dismiss();
			}
		});
		mSmsAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				openActivity(SmsLogActivity.class, false, smsLogRequestCode);
				mPw.dismiss();
			}
		});
		// 添加联系人进黑名单dialog按钮监听
		mContactsAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到联系人的界面来添加数据
				openActivity(ContactsActivity.class, false, contactsRequestCode);
				mPw.dismiss();
			}
		});
	}

	// 开启一个界面等待返回结果
	protected void openActivity(Class<? extends Activity> clazz,
			boolean isUpdate, int requestCode) {
		Intent intent = new Intent(this, clazz);
		intent.putExtra("isUpdate", isUpdate);
		startActivityForResult(intent, requestCode);
	}

	// 开启一个界面传输数据等待返回结果
	protected void openActivity(Class<? extends Activity> clazz,
			boolean isUpdate, int requestCode, InterceptInfo info) {
		Intent intent = new Intent(this, clazz);
		intent.putExtra("isUpdate", isUpdate);
		intent.putExtra("interceptInfo", info);
		startActivityForResult(intent, requestCode);
	}

	private void initData() {
		// listView显示数据

		mAdapter = new InterceptAdapter();

		mLv.setAdapter(mAdapter);

		mDao = new InterceptDao(getApplicationContext());

		getData(mPageSize, 0); // 从0开始获取数据
	}

	/**
	 * 从数据库中获取数据
	 */
	public void getData(final int pageSize, final int index) {

		if (pageSize > 5) {
			mPb.setVisibility(View.VISIBLE); // 设置加载图片
		}

		new Thread() {
			public void run() {

				if (!isHasData) { // 没有数据的时候就不再加载
					// 设置加载时的图片
					if (pageSize > 5) {
						runOnUiThread(new Runnable() {
							public void run() {
								mPb.setVisibility(View.GONE);
							}
						});
					}
					return;
				}
				if (pageSize > 5) {
					SystemClock.sleep(1000);
				}

				List<InterceptInfo> list = mDao.query(pageSize, index);

				if (list.size() == 0) { // 没有数据的时候,就不再获取数据库数据
					isHasData = false;

					if (mPb.isShown()) {
						runOnUiThread(new Runnable() {
							public void run() {
								mPb.setVisibility(View.GONE);

								// 设置数据为空时的背景图片
								mLv.setEmptyView(mIv);
							}
						});
					}

					return;
				}

				if (mList == null) {
					mList = list;
				} else {
					mList.addAll(list);
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (pageSize > 5) {
							// 设置加载时的图片
							mPb.setVisibility(View.GONE);
						}

						// 设置数据为空时的背景图片
						mLv.setEmptyView(mIv);

						// 通知适配器更新
						mAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();
	}

	// 适配器
	class InterceptAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mList == null) {
				return 0;
			}

			return mList.size();
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (mList == null) {
				return null;
			}
			ViewHolder holder = null;

			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_intercept_list, null);
				holder = new ViewHolder();

				TextView tvNumber = (TextView) convertView
						.findViewById(R.id.tv_intercept_item_number);
				TextView tvMode = (TextView) convertView
						.findViewById(R.id.tv_intercept_item_mode);
				ImageView icon = (ImageView) convertView
						.findViewById(R.id.iv_intercept_item_delete_icon);

				holder.tvNumber = tvNumber;
				holder.tvMode = tvMode;
				holder.icon = icon;

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final InterceptInfo info = mList.get(position);

			holder.tvNumber.setText(info.phone);
			holder.tvMode.setText(info.desc);

			// 删除记录
			holder.icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new Thread() {
						public void run() {
							// 删除数据库中的数据
							mDao.delete(info.phone);

							// 删除集合中的数据
							mList.remove(position);

							notifyAdapter();

							// 从数据库中获取添加一条数据
							getData(1, mList.size());
						};
					}.start();
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView tvNumber;
			TextView tvMode;
			ImageView icon;
		}
	}

	/**
	 * 接收等待返回数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (mList == null) {
			mList = new ArrayList<InterceptInfo>();
		}

		if (requestCode == contactsRequestCode
				|| requestCode == callLogRequestCode
				|| requestCode == smsLogRequestCode) {

			if (resultCode == RESULT_OK) {
				final String phone;
				if (requestCode == contactsRequestCode) {
					phone = data.getStringExtra(Constants.SAFE_PHONE);
				} else if (requestCode == callLogRequestCode) {
					phone = data.getStringExtra(Constants.CALL_LOG);
				} else {
					phone = data.getStringExtra(Constants.SMS_LOG);
				}

				View view = View.inflate(this,
						R.layout.dialog_intercept_contacts_add, null);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setView(view);

				final AlertDialog dialog = builder.create();

				dialog.show();

				EditText etPhone = (EditText) view
						.findViewById(R.id.et_intercept_add_contacts);
				final RadioGroup rg = (RadioGroup) view
						.findViewById(R.id.rg_intercept_dialog);
				Button btAdd = (Button) view
						.findViewById(R.id.bt_intercept_dialog_add);
				Button btCancle = (Button) view
						.findViewById(R.id.bt_intercept_dialog_cancle);

				etPhone.setText(phone);
				etPhone.setSelection(phone.length());

				// 取消按钮
				btCancle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				// 添加按钮
				btAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int radioButtonId = rg.getCheckedRadioButtonId();

						InterceptInfo info = new InterceptInfo();
						info.phone = phone;

						switch (radioButtonId) {
						case R.id.rb_intercept_dialog_phone:
							info.type = Constants.INTRECEPT_TYPE_PHONE;
							info.desc = "电话拦截";
							break;
						case R.id.rb_intercept_dialog_sms:
							info.type = Constants.INTRECEPT_TYPE_SMS;
							info.desc = "短信拦截";
							break;
						case R.id.rb_intercept_dialog_all:
							info.type = Constants.INTRECEPT_TYPE_ALL;
							info.desc = "电话+短信拦截";
							break;
						}

						boolean add = mDao.add(info);

						if (!add) {
							Toast.makeText(InterceptActivity.this,
									"号码已经添加了,请直接修改", Toast.LENGTH_SHORT).show();
							return;
						}

						mList.add(0, info);

						dialog.dismiss();

						notifyAdapter();
					}
				});
			}

		} else if (requestCode == addRequestCode) {

			if (resultCode == RESULT_OK) {
				// 把添加界面传过来的数据添加到记录中
				InterceptInfo info = (InterceptInfo) data
						.getSerializableExtra(Constants.INTERCEPT_INFO);

				/*
				 * if (mList.contains(info)) { System.out.println("有相同的数据");
				 * mList.remove(info); // 移除掉旧的数据 }
				 */

				mList.add(0, info); // 添加新的数据

				mAdapter.notifyDataSetChanged();
			}

		} else if (requestCode == updateRequestCode) {

			if (resultCode == RESULT_OK) {
				// 把添加界面传过来的数据添加到记录中
				InterceptInfo info = (InterceptInfo) data
						.getSerializableExtra(Constants.INTERCEPT_INFO);

				if (mList.contains(info)) {
					mList.remove(info); // 移除掉旧的数据
				}

				mList.add(0, info); // 添加新的数据

				mAdapter.notifyDataSetChanged();
			}

		}
	}

	/**
	 * 通知更新适配器
	 */
	public void notifyAdapter() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

}
