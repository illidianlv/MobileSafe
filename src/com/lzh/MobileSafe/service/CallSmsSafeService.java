package com.lzh.MobileSafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.lzh.MobileSafe.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "内部广播接收者，短信到来了");
			//检查发件人是否为黑名单，短信拦截，全部拦截。
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if ("2".equals(result)||"3".equals(result)) {
					Log.i(TAG, "拦截短信");
					abortBroadcast();
				}
				
			}
			
			
		}
		
	}
	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver= null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	
	private class MyListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
		    switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://响铃的时候
				String result = dao.findMode(incomingNumber);
				if ("1".equals(result)||"3".equals(result)) {
					Log.i(TAG, "挂断电话");
					Uri uri = Uri.parse("content://call_log/calls");
					//内容观察者 观察呼叫记录数据库内容的变化
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(incomingNumber, new Handler()));

					endCall();//另外一个进程里面运行的远程服务方法。方法调用后，呼叫记录方法可能还没生成
					//删除呼叫记录
//					deleteCallLog(incomingNumber);
					
				}
				
				break;

			}
			super.onCallStateChanged(state, incomingNumber);
		}
		private class CallLogObserver extends ContentObserver{
            private String incomingNumber;
			public CallLogObserver(String incomingNumber,Handler handler) {
				super(handler);
				this.incomingNumber = incomingNumber;
				
			}

			@Override
			public void onChange(boolean selfChange) {
				Log.i(TAG, "数据库内容发生变化了，产生了呼叫记录");
				//取消注册内容观察者
				getContentResolver().unregisterContentObserver(this);
				//删除呼叫记录
				deleteCallLog(incomingNumber);
				super.onChange(selfChange);
			}
			
		}

		private void endCall() {
//			ServiceManager.
			try {
				//加载ServiceManager的字节码
				Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = clazz.getDeclaredMethod("getService", String.class);
				IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
				ITelephony.Stub.asInterface(iBinder).endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	/**
	 * 利用内容提供者删除呼叫记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//呼叫记录uri路径
		Uri uri = Uri.parse("content://call_log/calls");
	//	CallLog.CONTENT_URI;
		
		resolver.delete(uri, "number=?" ,new String[]{incomingNumber});
		
	}

}
