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
			Log.i(TAG, "�ڲ��㲥�����ߣ����ŵ�����");
			//��鷢�����Ƿ�Ϊ���������������أ�ȫ�����ء�
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//�õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if ("2".equals(result)||"3".equals(result)) {
					Log.i(TAG, "���ض���");
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
			case TelephonyManager.CALL_STATE_RINGING://�����ʱ��
				String result = dao.findMode(incomingNumber);
				if ("1".equals(result)||"3".equals(result)) {
					Log.i(TAG, "�Ҷϵ绰");
					Uri uri = Uri.parse("content://call_log/calls");
					//���ݹ۲��� �۲���м�¼���ݿ����ݵı仯
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(incomingNumber, new Handler()));

					endCall();//����һ�������������е�Զ�̷��񷽷����������ú󣬺��м�¼�������ܻ�û����
					//ɾ�����м�¼
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
				Log.i(TAG, "���ݿ����ݷ����仯�ˣ������˺��м�¼");
				//ȡ��ע�����ݹ۲���
				getContentResolver().unregisterContentObserver(this);
				//ɾ�����м�¼
				deleteCallLog(incomingNumber);
				super.onChange(selfChange);
			}
			
		}

		private void endCall() {
//			ServiceManager.
			try {
				//����ServiceManager���ֽ���
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
	 * ���������ṩ��ɾ�����м�¼
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//���м�¼uri·��
		Uri uri = Uri.parse("content://call_log/calls");
	//	CallLog.CONTENT_URI;
		
		resolver.delete(uri, "number=?" ,new String[]{incomingNumber});
		
	}

}
