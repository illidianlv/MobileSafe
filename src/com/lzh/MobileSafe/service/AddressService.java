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
	 * 窗体管理者
	 */
	private WindowManager wm;
	/**
	 * 监听来电
	 */
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	/**
	 * 监听去电广播接收者
	 */
	private OutCallReceiver receiver;
	private SharedPreferences sp;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	//在服务里面定义一广播接受者，监听去电。内部类。
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "内部类的广播接收者");
			// 得到电话号码
			String phone = getResultData();
			//根据号码查询归属地
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
			case TelephonyManager.CALL_STATE_RINGING://电话铃声响起的时候，也是来电的时候
				//根据得到的电话号码查询它的归属地，并显示在toast里面
				String address = NumberAddressQureyUtils.queryNumber(incomingNumber);
//				Toast.makeText(getApplicationContext(), address, 1).show();
				myToast(address);
				
				break;
			case TelephonyManager.CALL_STATE_IDLE://电话空闲状态---挂断，来电拒绝
				//把textview移除
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
		//注册一个广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//监听来电
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		//实例化窗体管理者
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	/**
	 * 自定义toast
	 * @param address
	 */
	private WindowManager.LayoutParams params;
	
	private long [] mHits = new long[2];
	
	public void myToast(String address) {

        //"半透明","荷兰","阿根廷","天灰灰","尼日利亚"		
		int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		view = View.inflate(this,R.layout.address_show, null);
		//给view对象设置一个点击监听器
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if (mHits[0] > mHits[mHits.length-1] -500) {
					//双击居中
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view, params);
					//记录控件距离左上角的坐标
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.commit();
					
				}
				
				
			}
		});
		//给view对象设置一个触摸监听器
		view.setOnTouchListener(new OnTouchListener() {
			//定义手指初始化位置
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://手指按下屏幕
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "开始位置"+startX+","+startY);
					
					break;
					
				case MotionEvent.ACTION_MOVE://手指在屏幕上移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(TAG, "新的位置"+newX+","+newY);
					int dX = newX - startX;
					int dY = newY - startY;
					Log.i(TAG, "偏移量"+dX+","+dY);
					//更新view的位置
					params.x+= dX;
					params.y+= dY;
					//考虑边界问题
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
					//重新初始化手指头的位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				
				case MotionEvent.ACTION_UP://手指离开屏幕
					//记录控件距离左上角的坐标
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					
					break;

				default:
					break;
				}
				return false;//事件处理完毕，不要让父布局响应触摸事件

			}
			
		});
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textView.setText(address);
		//设置窗体参数
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //与窗体左上角对齐
        params.gravity = Gravity.TOP + Gravity.LEFT;
        //距离左边100像素，距离上面100像素 
        params.x = sp.getInt("lastX", 0);
        params.y = sp.getInt("lastY", 0);
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        //android系统里面具有电话优先级的一种窗体，记得添加权限。
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

		
        wm.addView(view, params);
		
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//代码取消注册一个广播接收者
		unregisterReceiver(receiver);
		receiver = null;
		//取消监听来电
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
}
