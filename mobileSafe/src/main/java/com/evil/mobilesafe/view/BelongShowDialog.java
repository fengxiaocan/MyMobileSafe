package com.evil.mobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

/**
 * 归属地样式DialogView
 */
public class BelongShowDialog extends Dialog {

	private ListView mLvStyle;
	public static final String[] STYLE_NAME = { "半透明", "活力橙", "卫士蓝", "金属灰",
			"苹果绿" };
	public static final int[] STYLE_ICON = { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green };
	private StyleAdapter mAdapter;

	public BelongShowDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);

	}

	public BelongShowDialog(Context context, int theme) {
		super(context, theme);

		//获取窗口
		Window window = getWindow();
		//获取参数
		LayoutParams params = window.getAttributes();
		//设置参数
		params.gravity = Gravity.BOTTOM;		//设置底部对齐
		window.setAttributes(params);
	}

	public BelongShowDialog(Context context) {
		this(context, R.style.BelongDialogStyle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置显示的View布局
		setContentView(R.layout.dialog_belong_address_style);

		// 初始化控件
		initView();

		// 设置监听器
		initListener();

		// 初始化数据
		initData();

	}

	private void initView() {
		// 查找ListView
		mLvStyle = (ListView) findViewById(R.id.lv_belong_dialog_style);
		
	}

	private void initListener() {
		mLvStyle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.setStyleColor(position);
				mAdapter.notifyDataSetChanged();
				SpUtils.save(getContext(), Constants.DIALOG_STYLE_COLOR, position);
				//关闭自身
				dismiss();
			}
		});
		
	}

	private void initData() {
		mAdapter = new StyleAdapter();
		mLvStyle.setAdapter(mAdapter);
		
		//回显归属地样式
		int sytleColor = SpUtils.getInfo(getContext(), Constants.DIALOG_STYLE_COLOR, 0);
		mAdapter.setStyleColor(sytleColor);
		mAdapter.notifyDataSetChanged();
	}

	class StyleAdapter extends BaseAdapter {
		private int index = 0;
		
		//使用一个类的setXXX()设置数据可以在外部类或其他类中传输数据给该类
		public void setStyleColor(int position){
			index = position;
		}

		@Override
		public int getCount() {

			return STYLE_NAME.length;
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
			convertView = convertView.inflate(getContext(),
					R.layout.item_belong_dialog_style_listview, null);

			ImageView ivColor = (ImageView) convertView
					.findViewById(R.id.iv_belong_dialog_style_color_icon);

			TextView tvTitle = (TextView) convertView
					.findViewById(R.id.tv_belong_dialog_style_title);

			ImageView ivSelect = (ImageView) convertView
					.findViewById(R.id.iv_belong_dialog_style_select_icon);
			//设置颜色
			ivColor.setBackgroundResource(STYLE_ICON[position]);
			//设置标题
			tvTitle.setText(STYLE_NAME[position]);
			//设置控件的选择状况
			if (index == position) {
				ivSelect.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

	}

}
