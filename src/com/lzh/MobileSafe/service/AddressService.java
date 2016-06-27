package com.lzh.MobileSafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.MobileSafe.R;
import com.lzh.MobileSafe.db.dao.NumberAddressQureyUtils;

public class AddressService extends Service {
	public static final String TAG = "AddressService";
	private View view;
	/**
	 * ���������
	 */
	private WindowManager wm;
	/**
	 * ��������
	 */
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	/**
	 * ����ȥ��㲥������
	 */
	private OutCallReceiver receiver;
	private SharedPreferences sp;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	//�ڷ������涨��һ�㲥�����ߣ�����ȥ�硣�ڲ��ࡣ
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "�ڲ���Ĺ㲥������");
			// �õ��绰����
			String phone = getResultData();
			//���ݺ����ѯ������
			String address = NumberAddressQureyUtils.queryNumber(phone);
//			Toast.makeText(context, address, 1).show();
			myToast(address);
			

		}

	}
	private class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://�绰���������ʱ��Ҳ�������ʱ��
				//���ݵõ��ĵ绰�����ѯ���Ĺ����أ�����ʾ��toast����
				String address = NumberAddressQureyUtils.queryNumber(incomingNumber);
//				Toast.makeText(getApplicationContext(), address, 1).show();
				myToast(address);
				
				break;
			case TelephonyManager.CALL_STATE_IDLE://�绰����״̬---�Ҷϣ�����ܾ�
				//��textview�Ƴ�
				if (view != null) {
					wm.removeView(view);
     			}

				break;

			default:
				break;
			}
		}
		
		
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//ע��һ���㲥������
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//��������
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		//ʵ�������������
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	/**
	 * �Զ���toast
	 * @param address
	 */
	private WindowManager.LayoutParams params;
	
	private long [] mHits = new long[2];
	
	public void myToast(String address) {

        //"��͸��","����","����͢","��һ�","��������"		
		int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		view = View.inflate(this,R.layout.address_show, null);
		//��view��������һ�����������
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if (mHits[0] > mHits[mHits.length-1] -500) {
					//˫������
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view, params);
					//��¼�ؼ��������Ͻǵ�����
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.commit();
					
				}
				
				
			}
		});
		//��view��������һ������������
		view.setOnTouchListener(new OnTouchListener() {
			//������ָ��ʼ��λ��
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://��ָ������Ļ
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "��ʼλ��"+startX+","+startY);
					
					break;
					
				case MotionEvent.ACTION_MOVE://��ָ����Ļ���ƶ�
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(TAG, "�µ�λ��"+newX+","+newY);
					int dX = newX - startX;
					int dY = newY - startY;
					Log.i(TAG, "ƫ����"+dX+","+dY);
					//����view��λ��
					params.x+= dX;
					params.y+= dY;
					//���Ǳ߽�����
					if (params.x<0) {
						params.x = 0;
					}
					if (params.y<0) {
						params.y = 0;
					}
					if (params.x>(wm.getDefaultDisplay().getWidth() - view.getWidth())) {
						params.x =wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if (params.y>(wm.getDefaultDisplay().getHeight() - view.getHeight())) {
						params.y =wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					wm.updateViewLayout(view, params);
					//���³�ʼ����ָͷ��λ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				
				case MotionEvent.ACTION_UP://��ָ�뿪��Ļ
					//��¼�ؼ��������Ͻǵ�����
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					
					break;

				default:
					break;
				}
				return false;//�¼�������ϣ���Ҫ�ø�������Ӧ�����¼�

			}
			
		});
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textView.setText(address);
		//���ô������
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //�봰�����ϽǶ���
        params.gravity = Gravity.TOP + Gravity.LEFT;
        //�������100���أ���������100���� 
        params.x = sp.getInt("lastX", 0);
        params.y = sp.getInt("lastY", 0);
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        //androidϵͳ������е绰���ȼ���һ�ִ��壬�ǵ����Ȩ�ޡ�
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

		
        wm.addView(view, params);
		
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//����ȡ��ע��һ���㲥������
		unregisterReceiver(receiver);
		receiver = null;
		//ȡ����������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
}
