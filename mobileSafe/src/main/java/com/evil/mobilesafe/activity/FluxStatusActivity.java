package com.evil.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import com.evil.mobilesafe.R;
import com.evil.mobilesafe.dao.FluxDao;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.interfaces.MyContext;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.utils.PrintLog;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 流量统计界面
 */
public class FluxStatusActivity extends Activity {

	private ListView mLv;
	private List<AppInfo> mAppInfo;
	private List<AppInfo> mFluxAppInfo = new ArrayList<AppInfo>();		//有流量信息的app
	private AppAdapter mAdapter;
	private ProgressBar mPb;
	private FluxDao mDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fluxstatus);
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
		mLv = (ListView) findViewById(R.id.lv_fluxstatus);
		
		mPb = (ProgressBar) findViewById(R.id.pb_fluxstatusS);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mDao = new FluxDao();
		getNetworkAppInfo();
		
		mAdapter = new AppAdapter();
		mLv.setAdapter(mAdapter);
	}

	/**
	 * 获取联网的的app信息
	 */
	private void getNetworkAppInfo() {
		new Thread() {
			public void run() {
				mAppInfo = AppManagerUtils.getAppInfo(MyContext.mContext);
				for (AppInfo info : mAppInfo) {
					if (info.fluxSize > 0) {
						long flux = mDao.query(info.appPackageName);
						PrintLog.log(flux);
						if (flux == -1) {
							mFluxAppInfo.add(info);
						}else {
							info.fluxSize += flux;
						}
					}
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						mLv.setEmptyView(mPb);
						mAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();
	}

	/**
	 * 初始化事件
	 */
	private void initListener() {
		
	}

	private class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if (mFluxAppInfo != null){
				return mFluxAppInfo.size();
			}
			
			return 0;
		}

		@Override
		public AppInfo getItem(int position) {
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
				holder = new ViewHolder();

				convertView = View.inflate(getApplicationContext(),
						R.layout.item_flux_listview_info, null);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_flux_listview_appicon);
				holder.tvAppName = (TextView) convertView
						.findViewById(R.id.tv_flux_listview_item_appname);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.tv_flux_listview_item_size);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo info = mFluxAppInfo.get(position);

			holder.tvAppName.setText(info.appName);
			holder.ivIcon.setImageDrawable(info.appIcon);
			holder.tvSize.setText(Formatter.formatFileSize(
					getApplicationContext(), info.fluxSize));

			return convertView;
		}

		class ViewHolder {
			ImageView ivIcon;
			TextView tvAppName;
			TextView tvSize;
		}
	}

}
