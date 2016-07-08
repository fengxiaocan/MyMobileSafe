package com.evil.mobilesafe.receiver;

import java.util.List;
import com.evil.mobilesafe.dao.FluxDao;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.utils.PrintLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 关机监听广播
 * @author 风小灿
 * @date 2016-6-20
 */
public class FluxBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PrintLog.log("收到关机广播");
		FluxDao dao = new FluxDao();
//		关机之前,先把流量数据统计一下
		List<AppInfo> list = AppManagerUtils.getAppInfo(context);
		for (AppInfo info : list) {
			if (info.fluxSize > 0) {
				long flux = dao.query(info.appPackageName);
				PrintLog.log(flux);
				if (flux == -1) {
					dao.add(info.appPackageName, info.fluxSize);
				}else {
					dao.update(info.appPackageName, info.fluxSize + flux);
				}
			}
		}
		
	}

}
