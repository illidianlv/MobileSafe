package com.lzh.MobileSafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * 系统信息的工具类
 * 
 * @author Administrator
 * 
 */
public class SystemInfoUtils {
	/**
	 * 获取正在运行的进程数量
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		// PackageManager 包管理器，相当于程序管理器，静态的内容
		// ActivityManager 进程管理器 管理手机的活动信息 动态的内容
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();

	}

	/**
	 * 获取手机可用剩余内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 获取手机内存
	 * 
	 * @param context
	 * @return long byte
	 */
	public static long getTotalMem(Context context) {
//		ActivityManager am = (ActivityManager) context
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		return outInfo.totalMem;
		try {
			File file = new File("proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
			String line = bf.readLine();
			//MemTotal:  530000kB
			StringBuilder sb = new StringBuilder();
			for (char c : line.toCharArray()) {
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

		
		
	}

}
