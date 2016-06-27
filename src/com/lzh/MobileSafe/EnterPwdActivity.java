package com.lzh.MobileSafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends Activity {
	private EditText et_password;
	private String packname;
	private TextView tv_name;
	private ImageView iv_icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		Intent intent = getIntent();
		// 当前要保护应用程序的包名
		packname = intent.getStringExtra("packname");
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		// 回桌面
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		//所有程序最小化，不会执行ondestroy 只执行onstop方法
		
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}

	public void click(View view) {
		String password = et_password.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, "密码不能为空", 1).show();
			return;
		}
		if ("123".equals(password)) {
			// 告诉看门狗密码正确，可以临时停止保护
			// 自定义广播,临时停止保护
			Intent intent = new Intent();
			intent.setAction("com.lzh.mobilesafe.tempstop");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			finish();
		} else {
			Toast.makeText(this, "密码错误", 1).show();
			return;
		}
	}

}
