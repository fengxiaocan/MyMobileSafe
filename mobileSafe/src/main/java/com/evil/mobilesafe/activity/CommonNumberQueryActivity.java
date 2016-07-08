package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.CommonNumberDao;
import com.evil.mobilesafe.domain.CommonNumberInfo;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView mElv;
	private MyAdapter mAdapter;
	private CommonNumberDao mDao;
	private List<CommonNumberInfo> mGroup;
	private List<List<CommonNumberInfo>> mChildList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_common_number_query);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();

	}

	private void initView() {
		mElv = (ExpandableListView) findViewById(R.id.elv_common_number_query);
	}

	private void initListener() {
		//点击子标签的事件
		mElv.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				//跳转拨打电话界面
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				String number = mChildList.get(groupPosition).get(childPosition).number;
				intent.setData(Uri.parse("tel:"+number));
				
				startActivity(intent);
				return true;
			}
		});
	}

	private void initData() {
		mAdapter = new MyAdapter();
		mElv.setAdapter(mAdapter);
		// 数据库操作工具
		mDao = new CommonNumberDao(this);

		getData();
	}

	/**
	 * 查询数据库获取数据
	 */
	private void getData() {
		new Thread() {
			public void run() {
				// 获取组标签
				mGroup = mDao.queryGroup();

				// 获取所有子成员的集合组
				mChildList = new ArrayList<List<CommonNumberInfo>>();
				for (int i = 1; i <= mGroup.size(); i++) {
					List<CommonNumberInfo> child = mDao.queryChild(i + "");
					mChildList.add(child);
				}
			};
		}.start();

	}

	class MyAdapter extends BaseExpandableListAdapter {

		// 返回组的个数
		@Override
		public int getGroupCount() {
			if (mGroup != null) {
				return mGroup.size();
			}
			return 0;
		}

		// 根据组的位置获取该组下的子标签的个数
		@Override
		public int getChildrenCount(int groupPosition) {
			if (mChildList == null) {
				return 0;
			}
			// 先获取对应组的集合,再获取该集合的个数
			return mChildList.get(groupPosition).size();
		}

		// 获取组的View
		@Override
		public TextView getGroup(int groupPosition) {
			TextView tv = new TextView(getApplicationContext());
			tv.setBackgroundColor(Color.GRAY);
			tv.setTextSize(20);
			tv.setTextColor(Color.BLACK);
			tv.setPadding(10, 10, 10, 10);
			tv.setText(mGroup.get(groupPosition).name);

			return tv;
		}

		// 获取子的View
		@Override
		public LinearLayout getChild(int groupPosition, int childPosition) {
			LinearLayout ll = new LinearLayout(getApplicationContext());
			ll.setBackgroundColor(Color.WHITE); // 设置背景为白色
			ll.setOrientation(LinearLayout.VERTICAL); // 设置为垂直
			// ll.setClickable(true);
			// 创建要给textview用于显示号码名称
			TextView tv1 = new TextView(getApplicationContext());
			tv1.setBackgroundColor(Color.WHITE);
			tv1.setTextSize(17);
			tv1.setTextColor(Color.BLACK);
			tv1.setPadding(5, 5, 0, 0);
			tv1.setText(mChildList.get(groupPosition).get(childPosition).name);
			ll.addView(tv1);// 添加到容器中

			// 创建一个textView,用于显示号码
			TextView tv2 = new TextView(getApplicationContext());
			tv2.setBackgroundColor(Color.WHITE);
			tv2.setTextSize(15);
			tv2.setTextColor(Color.BLACK);
			tv2.setPadding(5, 0, 0, 5);
			tv2.setText(mChildList.get(groupPosition).get(childPosition).number);

			ll.addView(tv2);// 添加到容器中
			return ll;
		}

		// 获取组的id
		@Override
		public long getGroupId(int groupPosition) {

			return 0;
		}

		// 获取子的id
		@Override
		public long getChildId(int groupPosition, int childPosition) {

			return 0;
		}

		@Override
		public boolean hasStableIds() {

			return false;
		}

		// 设置组的布局
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			convertView = getGroup(groupPosition);

			return convertView;
		}

		// 设置子的布局
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			return getChild(groupPosition, childPosition);
		}

		//设置子标签是否开启可点击状态
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return true;
		}

	}
}
