package com.lzh.MobileSafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.MobileSafe.db.dao.NumberAddressQureyUtils;

public class NumberAddressQueryActivity extends Activity {
	private static final String TAG = "NumberAddressQueryActivity";
	private EditText et_phone;
	private TextView tv_result;
	//系统提供震动服务
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_phone = (EditText) findViewById(R.id.et_phone);
		tv_result = (TextView) findViewById(R.id.tv_result);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_phone.addTextChangedListener(new TextWatcher() {
			/**
			 * 当文本发生变化的时候回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s!=null &&s.length()>=3) {
					//查询数据库并显示结果
					String address = NumberAddressQureyUtils.queryNumber(s.toString());
					tv_result.setText(address);
					
				}
				
			}
			/**
			 * 当文本发生变化之前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			/**
			 * 当文本发生变化之后回调
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	/**
	 * 查询号码归属地
	 * @param view
	 */
	public void numberAddressQuery(View view){
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "号码为空", 0).show();
			//输入框抖动
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phone.startAnimation(shake);
			//震动提醒
		    vibrator.vibrate(1000);
		    
//		    long[] pattern = {100,100,200,200,1000,1000};
//		    //-1 不循环 0循环震动
//		    vibrator.vibrate(pattern, -1);
			return;
			
		} else {
			//去数据库查询号码归属地
			//写一个工具类去查询数据库
			String address = NumberAddressQureyUtils.queryNumber(phone);
			tv_result.setText(address);
			Log.i(TAG, "电话号码"+phone);

		}
		
	}

}
