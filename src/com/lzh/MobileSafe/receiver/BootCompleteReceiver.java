package com.lzh.MobileSafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
    SharedPreferences sp;
    private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			//开启防盗保护才执行这个地方
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//读取之前保存的sim卡信息
		String saveSim = sp.getString("sim", "");
		
		//读取当前sim卡信息
		String realSim = tm.getSimSerialNumber();
		//比较两者是否一样
		if (saveSim.equals(realSim)) {
			//一样
			
		} else {
			//变更，发送短信给安全号码
			System.out.println("sim 已经变更");
			Toast.makeText(context, "sim卡已变更", 1).show();
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing....", null, null);

		}
	}
	}

}
