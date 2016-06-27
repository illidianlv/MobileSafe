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
			//��������������ִ������ط�
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//��ȡ֮ǰ�����sim����Ϣ
		String saveSim = sp.getString("sim", "");
		
		//��ȡ��ǰsim����Ϣ
		String realSim = tm.getSimSerialNumber();
		//�Ƚ������Ƿ�һ��
		if (saveSim.equals(realSim)) {
			//һ��
			
		} else {
			//��������Ͷ��Ÿ���ȫ����
			System.out.println("sim �Ѿ����");
			Toast.makeText(context, "sim���ѱ��", 1).show();
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing....", null, null);

		}
	}
	}

}
