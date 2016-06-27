package com.lzh.MobileSafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	/**
	 * У��ĳ�������Ƿ�������
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName) {
		//ActivityManager������Թ���Activity���ܹ���service
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo>  infos = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : infos) {
			//�õ��������з��������
			String name = runningServiceInfo.service.getClassName();
			if (serviceName.equals(name)) {
				return true;
			}
			
			
		}
		return false;
		
	}

}
