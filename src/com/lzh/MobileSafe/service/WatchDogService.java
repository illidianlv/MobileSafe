package com.lzh.MobileSafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.lzh.MobileSafe.EnterPwdActivity;
import com.lzh.MobileSafe.db.dao.AppLockDao;

/**
 * 看门狗代码 监视系统程序运行状态
 * 
 * @author Administrator
 * 
 */
public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private InnerReceiver innerReceiver;
	private DataChangeReceive datachangeReceiver;
	private String tempStopProtectpackname;
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;
	private List<String> protectPacknames;
	private Intent intent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectpackname = null;
			flag = false;
		}
	}

	private class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			startProtect();
		}
	}

	private class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectpackname = intent.getStringExtra("packname");
		}
	}
	private class DataChangeReceive extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			protectPacknames = dao.findAll();
		}
	}

	@Override
	public void onCreate() {
		offReceiver = new ScreenOffReceiver();
		onReceiver = new ScreenOnReceiver();
		registerReceiver(offReceiver,
				new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter(
				"com.lzh.mobilesafe.tempstop"));
		
		datachangeReceiver = new DataChangeReceive();
		registerReceiver(datachangeReceiver, new IntentFilter(
				"com.lzh.mobilesafe.applockchange"));
		dao = new AppLockDao(this);
		protectPacknames = dao.findAll();
		startProtect();
		super.onCreate();
	}

	private void startProtect() {
		flag = true;
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 当前应用需要保护，弹出一个输入密码界面
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		// 服务是没有任务栈信息的，在服务里打开activity要指定这个activity运行的任务栈
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					// System.out.println("当前用户操作的应用程序=" + packname);// 培训好了
					// if (dao.find(packname)) {//查询数据库太慢，消耗资源，改成查询内存
					if (protectPacknames.contains(packname)) { // 查询内存效率高很多
						// 判断这个程序是否需要临时停止保护
						if (packname.equals(tempStopProtectpackname)) {

						} else {
							// 设置要保护应用程序的包名
							intent.putExtra("packname", packname);
							startActivity(intent);
						}

					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(datachangeReceiver);
		datachangeReceiver = null;
		unregisterReceiver(offReceiver);
		offReceiver = null;
		unregisterReceiver(onReceiver);
		onReceiver = null;
		super.onDestroy();
	}

}
