package com.lzh.MobileSafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.lzh.MobileSafe.domain.AppInfo;

/**
 * 业务方法 提供手机里面所有安装程序的信息
 * @author Administrator
 *
 */
public class AppInfoProvider {
/**
 * 获取所有安装应用程序信息
 * @param context 上下文
 * @return
 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		//所有安装的应用程序包信息
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			//packageInfo 相当于一个应用程序apk包的清单文件
			String packname = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			int flag = packageInfo.applicationInfo.flags;//应用程序信息标记 相当于用户提交的答卷
			int uid = packageInfo.applicationInfo.uid;//操作系统分配给应用程序的一个固定id。一旦应用程序被装到手机里就固定不变。
//			File rcvFile = new File("proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndFile = new File("proc/uid_stat/"+uid+"/tcp_snd");
			appInfo.setUid(uid);
            if ((flag&ApplicationInfo.FLAG_SYSTEM)==0) {
            	//用户程序
				appInfo.setUserApp(true);
			} else {
				//系统程序
				appInfo.setUserApp(false);
			}
            if ((flag&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {
            	//手机内存
            	appInfo.setInRom(true);
			} else {
				//手机外存储设备
				appInfo.setInRom(false);
			}
			
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
		}
		
		return appInfos;
		
	}

}
