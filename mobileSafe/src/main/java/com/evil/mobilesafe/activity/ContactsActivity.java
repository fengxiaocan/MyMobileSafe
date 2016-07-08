package com.evil.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.domain.ContactsInfo;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.QueryContactsUtils;

public class ContactsActivity extends Activity {
	private ListView mLv;
	private List<ContactsInfo> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contacts);

		// 初始化控件
		initView();

		// 初始化数据
		initData();

		// 设置监听器
		initListener();
	}

	private void initView() {
		mLv = (ListView) findViewById(R.id.lv_contacts);
	}

	private void initListener() {
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactsInfo bean = mList.get(position);
				Intent intent = new Intent();
				intent.putExtra(Constants.SAFE_PHONE, bean.phone);

				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initData() {
		// 初始化数据

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("正在玩命的加载中");
		dialog.show();

		new Thread() {
			public void run() {

				mList = QueryContactsUtils
						.queryContacts(getApplicationContext());

				runOnUiThread(new Runnable() {
					public void run() {
						dialog.dismiss();
						mLv.setAdapter(new Myadapter());
					}
				});
			};
		}.start();

	}

	class Myadapter extends BaseAdapter {

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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (mList == null) {
				return null;
			}

			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_contacts_content, null);

				holder = new ViewHolder();

				holder.name = (TextView) convertView
						.findViewById(R.id.tv_item_contact_name);
				holder.phone = (TextView) convertView
						.findViewById(R.id.tv_item_contact_number);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_item_contact_icon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(mList.get(position).name);
			holder.phone.setText(mList.get(position).phone);
			holder.icon.setImageBitmap(mList.get(position).bitmap);

			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView phone;
			ImageView icon;
		}

	}
}
