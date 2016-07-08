/**
 * 
 */
package com.evil.mobilesafe.activity;

import java.util.List;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.ContactsInfo;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.QueryContactsUtils;
import com.evil.mobilesafe.utils.TimeTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 通话记录界面
 * 
 * @author 风小灿
 * @date 2016-6-19
 */
public class CallLogActivity extends Activity {
	private ListView mLv;
	private List<ContactsInfo> mCallLog; // 通话记录
	private MyAdapter mMyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化控件
		initView();
		// 初始化数据
		initData();
		// 初始化监听器
		initListener();

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		setContentView(R.layout.activity_calllog);

		mLv = (ListView) findViewById(R.id.lv_calllog);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 获取通话记录日志
		new Thread() {
			public void run() {
				mCallLog = QueryContactsUtils.getCallLog(CallLogActivity.this);
				runOnUiThread(new Runnable() {
					public void run() {
						// 通知适配器更新
						mMyAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();

	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		mMyAdapter = new MyAdapter();
		mLv.setAdapter(mMyAdapter);
		
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(Constants.CALL_LOG, mCallLog.get(position).phone);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if (mCallLog != null) {
				return mCallLog.size();
			}
			return 0;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_calllog_listview_info, null);

				holder = new ViewHolder();
				holder.number = (TextView) convertView
						.findViewById(R.id.tv_calllog_number);
				holder.time = (TextView) convertView
						.findViewById(R.id.tv_calllog_time);
				holder.type = (TextView) convertView
						.findViewById(R.id.tv_calllog_type);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContactsInfo info = mCallLog.get(position);
			holder.number.setText(info.phone);
			holder.time.setText(TimeTool.formatTime(info.date,
					TimeTool.DATE_TYPE1));
			if (info.type == 1) {
				holder.type.setText("呼入");
			} else {
				holder.type.setText("拨出");
			}

			return convertView;
		}

		class ViewHolder {
			TextView number;
			TextView time;
			TextView type;
		}
	}
}
