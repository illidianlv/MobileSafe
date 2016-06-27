package com.lzh.MobileSafe.receiver;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.lzh.MobileSafe.MyAdmin;
import com.lzh.MobileSafe.R;
import com.lzh.MobileSafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;
	/**
	 * �豸���Է���
	 */
	private DevicePolicyManager dpm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// д���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);

		for (Object b : objs) {
			//�����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			//������ 
			String sender = sms.getOriginatingAddress();
			Log.i(TAG, "====sender====="+sender);
			//��ȫ����
			String safenumber = sp.getString("safenumber", "");
			//����
			String body = sms.getMessageBody();
			if (sender.contains(safenumber)) {
				if ("#*location*#".equals(body)) {
					//�õ��ֻ�GPS
					Log.i(TAG, "�õ��ֻ�GPS");
					//��������
					Intent intent2  = new Intent(context, GPSService.class);
					context.startService(intent2);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastlocation)) {
						//λ��û�еõ�
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
						
					}else {
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					//������㲥��ֹ��
					abortBroadcast();
					
					
				}else if ("#*alarm*#".equals(body)) {
					//���ű�������
					Log.i(TAG, "���ű�������");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.start();
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					//������㲥��ֹ��
					abortBroadcast();
					
				}else if ("#*wipedata*#".equals(body)) {
					//�������
					Log.i(TAG, "�������");
					//������㲥��ֹ��
					abortBroadcast();
					
				}else if ("#*lockscreen*#".equals(body)) {
					//Զ������
					Log.i(TAG, "Զ������");
					//��Ҫ����˭		
					ComponentName who = new ComponentName(context,MyAdmin.class);
					if (dpm.isAdminActive(who)) {
						//���豸����ԱȨ�ޣ�ֱ������
						Log.i(TAG, "���豸����ԱȨ�ޣ�ֱ������");
						dpm.lockNow();
						abortBroadcast();
					} else {
						//û���豸����ԱȨ�ޣ�Ҫ���Ȩ��
						Log.i(TAG, "û���豸����ԱȨ�ޣ�Ҫ���Ȩ��");
						Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		
				        i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
				       //Ȱ˵�û���������ԱȨ��
				        i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				               "���ǿ����ҿ���һ����������İ�ť�Ͳ��ᾭ��ʧ��");
				        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        context.startActivity(i);

				        abortBroadcast();


					}
//					//������㲥��ֹ��
//					abortBroadcast();
					
				}
				
			}
			

		
			
		}

	}

}
