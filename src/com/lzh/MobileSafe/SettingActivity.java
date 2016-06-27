package com.lzh.MobileSafe;

import com.lzh.MobileSafe.service.AddressService;
import com.lzh.MobileSafe.service.CallSmsSafeService;
import com.lzh.MobileSafe.service.WatchDogService;
import com.lzh.MobileSafe.ui.SettingClickView;
import com.lzh.MobileSafe.ui.SettingItemView;
import com.lzh.MobileSafe.utils.ServiceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	// 设置是否自动更新
	private SettingItemView siv_update;
	// 设置是否显示归属地
	private SettingItemView siv_address;
	private Intent showAddressIntent;
	// 设置归属地显示背景
	private SettingClickView scv_changebg;
	private SharedPreferences sp;
	// 设置黑名单拦截
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	// 设置程序锁看门狗
	private SettingItemView siv_watchdog;
	private Intent watchDogIntent;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showAddressIntent = new Intent(this, AddressService.class);
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.lzh.MobileSafe.service.AddressService");
		siv_address.setCheck(isRunning);

		boolean isCallSmsSafeRunning = ServiceUtils.isServiceRunning(this,
				"com.lzh.MobileSafe.service.CallSmsSafeService");
		siv_callsms_safe.setCheck(isCallSmsSafeRunning);
		
		boolean isWatchDogRunning = ServiceUtils.isServiceRunning(this,
				"com.lzh.MobileSafe.service.WatchDogService");
		siv_watchdog.setCheck(isWatchDogRunning);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 设置是否开启自动升级
		siv_update = (SettingItemView) findViewById(R.id.siv_update);

		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 自动升级已开启
			siv_update.setCheck(true);
		} else {
			// 自动升级已关闭
			siv_update.setCheck(false);

		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断是否有选中
				Editor editor = sp.edit();
				if (siv_update.isChecked()) {
					// 已经打开自动升级
					siv_update.setCheck(false);
					editor.putBoolean("update", false);

				} else {
					// 没有打开自动升级
					siv_update.setCheck(true);
					editor.putBoolean("update", true);
				}
				editor.commit();

			}
		});

		// 是否开启来电归属地显示

		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		showAddressIntent = new Intent(this, AddressService.class);
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.lzh.MobileSafe.service.AddressService");
		if (isRunning) {
			// 监听来电的服务运行着
			siv_address.setCheck(true);
		} else {
			// 监听来电的服务关闭着
			siv_address.setCheck(false);
		}
		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 监听来电显示归属地服务开启
				if (siv_address.isChecked()) {
					// 关闭
					stopService(showAddressIntent);
					siv_address.setCheck(false);

				} else {
					// 开启
					startService(showAddressIntent);
					siv_address.setCheck(true);
				}

			}
		});
		// 黑名单拦截设置
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 监听来电显示归属地服务开启
				if (siv_callsms_safe.isChecked()) {
					// 关闭
					stopService(callSmsSafeIntent);
					siv_callsms_safe.setCheck(false);

				} else {
					// 开启
					startService(callSmsSafeIntent);
					siv_callsms_safe.setCheck(true);
				}

			}
		});

		// 设置号码归属地背景
		final String[] items = { "半透明", "荷兰", "阿根廷", "天灰灰", "尼日利亚" };
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				int dd = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, dd,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 保存选中参数
								Editor editor = sp.edit();
								editor.putInt("which", which);
								editor.commit();
								scv_changebg.setDesc(items[which]);

								// 取消对话框
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();

			}
		});
		// 程序锁设置
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
		watchDogIntent = new Intent(this, WatchDogService.class);
		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 监听来电显示归属地服务开启
				if (siv_watchdog.isChecked()) {
					// 关闭
					stopService(watchDogIntent);
					siv_watchdog.setCheck(false);

				} else {
					// 开启
					startService(watchDogIntent);
					siv_watchdog.setCheck(true);
				}

			}
		});

	}

}
