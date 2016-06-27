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
 * ҵ�񷽷� �ṩ�ֻ��������а�װ�������Ϣ
 * @author Administrator
 *
 */
public class AppInfoProvider {
/**
 * ��ȡ���а�װӦ�ó�����Ϣ
 * @param context ������
 * @return
 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		//���а�װ��Ӧ�ó������Ϣ
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			//packageInfo �൱��һ��Ӧ�ó���apk�����嵥�ļ�
			String packname = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			int flag = packageInfo.applicationInfo.flags;//Ӧ�ó�����Ϣ��� �൱���û��ύ�Ĵ��
			int uid = packageInfo.applicationInfo.uid;//����ϵͳ�����Ӧ�ó����һ���̶�id��һ��Ӧ�ó���װ���ֻ���͹̶����䡣
//			File rcvFile = new File("proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndFile = new File("proc/uid_stat/"+uid+"/tcp_snd");
			appInfo.setUid(uid);
            if ((flag&ApplicationInfo.FLAG_SYSTEM)==0) {
            	//�û�����
				appInfo.setUserApp(true);
			} else {
				//ϵͳ����
				appInfo.setUserApp(false);
			}
            if ((flag&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {
            	//�ֻ��ڴ�
            	appInfo.setInRom(true);
			} else {
				//�ֻ���洢�豸
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
