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
		//1.���һ����������
		PackageManager pm = getPackageManager();
		//2.�����ֻ�����ϵͳ��ȡ����Ӧ�ó����uid
		List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : applicationInfos) {
			int uid = applicationInfo.uid;
			long tx = TrafficStats.getUidTxBytes(uid);//�ϴ�������byte
			long rx = TrafficStats.getUidRxBytes(uid);//���ص�����byte
		}
		setContentView(R.layout.activity_traffic_manager);
		SlidingDrawer sd;
	}

}
