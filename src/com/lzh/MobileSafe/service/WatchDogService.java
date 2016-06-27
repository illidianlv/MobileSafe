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
 * ���Ź����� ����ϵͳ��������״̬
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
		// ��ǰӦ����Ҫ����������һ�������������
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		// ������û������ջ��Ϣ�ģ��ڷ������activityҪָ�����activity���е�����ջ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					// System.out.println("��ǰ�û�������Ӧ�ó���=" + packname);// ��ѵ����
					// if (dao.find(packname)) {//��ѯ���ݿ�̫����������Դ���ĳɲ�ѯ�ڴ�
					if (protectPacknames.contains(packname)) { // ��ѯ�ڴ�Ч�ʸߺܶ�
						// �ж���������Ƿ���Ҫ��ʱֹͣ����
						if (packname.equals(tempStopProtectpackname)) {

						} else {
							// ����Ҫ����Ӧ�ó���İ���
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
