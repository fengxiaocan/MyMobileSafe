package com.evil.mobilesafe.test;

import java.util.List;
import java.util.Random;
import com.evil.mobilesafe.dao.InterceptDao;
import com.evil.mobilesafe.domain.AppInfo;
import com.evil.mobilesafe.domain.InterceptInfo;
import com.evil.mobilesafe.utils.AppManagerUtils;
import com.evil.mobilesafe.utils.SmsUtils;
import com.evil.mobilesafe.utils.TaskManagerUtils;
import android.test.AndroidTestCase;

public class MobileSafeTest extends AndroidTestCase {

	public void getSms() {
	}

	public void getTaskInfo() {
		List<AppInfo> list = TaskManagerUtils.getProgress(getContext());
		System.out.println(list);
	}

	// 测试获取应用工具类
	public void getAppInfo() {
		List<AppInfo> appInfo = AppManagerUtils.getAppInfo(getContext());
		for (AppInfo appInfo2 : appInfo) {
			System.out.println(appInfo2);
		}
		//
		// System.out.println("内存总空间: "
		// + Formatter.formatFileSize(getContext(),
		// AppManagerUtils.getRomTotalSpace()));
		// System.out.println("内存可用空间: "
		// + Formatter.formatFileSize(getContext(),
		// AppManagerUtils.getRomFreeSpace()));
		// System.out.println("内存已使用空间: "
		// + Formatter.formatFileSize(getContext(),
		// AppManagerUtils.getRomUsedSpace()));
		// System.out.println("sd卡总空间: "
		// + Formatter.formatFileSize(getContext(),
		// AppManagerUtils.getSDTotalSpace()));
		// System.out.println("sd卡可用空间: "
		// + Formatter.formatFileSize(getContext(),
		// AppManagerUtils.getSDFreeSpace()));
		// System.out.println("sd卡已使用空间: "
		// + Formatter.formatFileSize(getContext(),
		// AppManagerUtils.getSDUsedSpace()));
	}

	public void getService() {

	}

	public void addTest() {
		InterceptDao dao = new InterceptDao(getContext());

		Random random = new Random();
		for (int i = 0; i < 400; i++) {
			InterceptInfo info = new InterceptInfo();
			int s = 0;
			while (true) {
				s = random.nextInt(100000);
				if (s > 10000 && s < 100000) {
					break;
				}
			}

			info.phone = "134500" + s;

			switch (random.nextInt(3)) {
			case 0:
				info.type = 0;
				info.desc = "电话拦截";
				break;
			case 1:
				info.type = 1;
				info.desc = "短信拦截";
				break;
			case 2:
				info.type = 2;
				info.desc = "电话+短信拦截";
				break;
			}

			dao.add(info);
		}
	}

	public void delete() {
		InterceptDao dao = new InterceptDao(getContext());

		dao.delete("13450092427");
	}

	public void update() {
		InterceptDao dao = new InterceptDao(getContext());

		InterceptInfo info = new InterceptInfo();

		info.phone = "13450096042";
		info.type = 0;
		info.desc = "电话拦截";

		dao.update(info);
	}

	public void query() {
		InterceptDao dao = new InterceptDao(getContext());

		List<InterceptInfo> list = dao.query(10, 2);

		if (list == null) {
			System.out.println("list 为 null");
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString());
		}
	}

	public void queryType() {
		InterceptDao dao = new InterceptDao(getContext());

		int queryType = dao.queryType("13450096042");

		switch (queryType) {
		case 0:
			System.out.println("电话拦截");
			break;
		case 1:
			System.out.println("短信拦截");
			break;
		case 2:
			System.out.println("电话+短信拦截");
			break;

		default:
			break;
		}
	}
}
