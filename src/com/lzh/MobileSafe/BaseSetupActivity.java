package com.lzh.MobileSafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public abstract class BaseSetupActivity extends Activity {
	//1定义一个手势识别器
	private GestureDetector detector ;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//2实例化这个手势识别器
		detector = new GestureDetector(this, new SimpleOnGestureListener(){

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//屏蔽斜着滑动
				if (Math.abs((e2.getRawY()- e1.getRawY())) > 150) {
					return true;
				}
				//屏蔽滑动很慢
				if (Math.abs(velocityX)<100) {
					return true;
				}
				
				if ((e2.getRawX()- e1.getRawX()) > 200) {
					//显示上一个页面，从左往右划
					showPre() ;
					return true;
				}
				if ((e1.getRawX()- e2.getRawX()) > 200) {
					//显示下一个页面
					showNext();
					return true;
					
				}
				return super.onFling(e1, e2, velocityX, velocityY);



			}
			
		});


	}
	//3 使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	public abstract void showNext() ;
	public abstract void showPre() ;
	
	/**
	 * 下一步点击事件
	 * @param view
	 */
	
	public void next(View view){
		showNext();
		
	}
	/**
	 * 上一步点击事件
	 * @param view
	 */
	
	public void pre(View view){
		showPre() ;
		
	}

}
