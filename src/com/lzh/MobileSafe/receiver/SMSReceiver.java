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
	 * 设备策略服务
	 */
	private DevicePolicyManager dpm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 写接收短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);

		for (Object b : objs) {
			//具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			//发送者 
			String sender = sms.getOriginatingAddress();
			Log.i(TAG, "====sender====="+sender);
			//安全号码
			String safenumber = sp.getString("safenumber", "");
			//内容
			String body = sms.getMessageBody();
			if (sender.contains(safenumber)) {
				if ("#*location*#".equals(body)) {
					//得到手机GPS
					Log.i(TAG, "得到手机GPS");
					//启动服务
					Intent intent2  = new Intent(context, GPSService.class);
					context.startService(intent2);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastlocation)) {
						//位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
						
					}else {
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					//把这个广播终止掉
					abortBroadcast();
					
					
				}else if ("#*alarm*#".equals(body)) {
					//播放报警音乐
					Log.i(TAG, "播放报警音乐");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.start();
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					//把这个广播终止掉
					abortBroadcast();
					
				}else if ("#*wipedata*#".equals(body)) {
					//清除数据
					Log.i(TAG, "清除数据");
					//把这个广播终止掉
					abortBroadcast();
					
				}else if ("#*lockscreen*#".equals(body)) {
					//远程锁屏
					Log.i(TAG, "远程锁屏");
					//我要激活谁		
					ComponentName who = new ComponentName(context,MyAdmin.class);
					if (dpm.isAdminActive(who)) {
						//有设备管理员权限，直接锁屏
						Log.i(TAG, "有设备管理员权限，直接锁屏");
						dpm.lockNow();
						abortBroadcast();
					} else {
						//没有设备管理员权限，要求打开权限
						Log.i(TAG, "没有设备管理员权限，要求打开权限");
						Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		
				        i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
				       //劝说用户开启管理员权限
				        i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				               "哥们开启我可以一键锁屏，你的按钮就不会经常失灵");
				        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        context.startActivity(i);

				        abortBroadcast();


					}
//					//把这个广播终止掉
//					abortBroadcast();
					
				}
				
			}
			

		
			
		}

	}

}
