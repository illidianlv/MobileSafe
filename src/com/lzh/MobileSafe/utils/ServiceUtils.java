package com.lzh.MobileSafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	/**
	 * 校验某个服务是否还在运行
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName) {
		//ActivityManager不光可以管理Activity还能管理service
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo>  infos = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : infos) {
			//得到正在运行服务的名字
			String name = runningServiceInfo.service.getClassName();
			if (serviceName.equals(name)) {
				return true;
			}
			
			
		}
		return false;
		
	}

}
