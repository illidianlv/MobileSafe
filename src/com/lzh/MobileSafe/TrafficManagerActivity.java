package com.lzh.MobileSafe;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.widget.SlidingDrawer;

public class TrafficManagerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//1.获得一个包管理器
		PackageManager pm = getPackageManager();
		//2.遍历手机操作系统获取所有应用程序的uid
		List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : applicationInfos) {
			int uid = applicationInfo.uid;
			long tx = TrafficStats.getUidTxBytes(uid);//上传的流量byte
			long rx = TrafficStats.getUidRxBytes(uid);//下载的流量byte
		}
		setContentView(R.layout.activity_traffic_manager);
		SlidingDrawer sd;
	}

}
