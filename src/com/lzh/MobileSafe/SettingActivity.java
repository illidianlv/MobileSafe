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
	// �����Ƿ��Զ�����
	private SettingItemView siv_update;
	// �����Ƿ���ʾ������
	private SettingItemView siv_address;
	private Intent showAddressIntent;
	// ���ù�������ʾ����
	private SettingClickView scv_changebg;
	private SharedPreferences sp;
	// ���ú���������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	// ���ó��������Ź�
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
		// �����Ƿ����Զ�����
		siv_update = (SettingItemView) findViewById(R.id.siv_update);

		boolean update = sp.getBoolean("update", false);
		if (update) {
			// �Զ������ѿ���
			siv_update.setCheck(true);
		} else {
			// �Զ������ѹر�
			siv_update.setCheck(false);

		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �ж��Ƿ���ѡ��
				Editor editor = sp.edit();
				if (siv_update.isChecked()) {
					// �Ѿ����Զ�����
					siv_update.setCheck(false);
					editor.putBoolean("update", false);

				} else {
					// û�д��Զ�����
					siv_update.setCheck(true);
					editor.putBoolean("update", true);
				}
				editor.commit();

			}
		});

		// �Ƿ��������������ʾ

		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		showAddressIntent = new Intent(this, AddressService.class);
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.lzh.MobileSafe.service.AddressService");
		if (isRunning) {
			// ��������ķ���������
			siv_address.setCheck(true);
		} else {
			// ��������ķ���ر���
			siv_address.setCheck(false);
		}
		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����������ʾ�����ط�����
				if (siv_address.isChecked()) {
					// �ر�
					stopService(showAddressIntent);
					siv_address.setCheck(false);

				} else {
					// ����
					startService(showAddressIntent);
					siv_address.setCheck(true);
				}

			}
		});
		// ��������������
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����������ʾ�����ط�����
				if (siv_callsms_safe.isChecked()) {
					// �ر�
					stopService(callSmsSafeIntent);
					siv_callsms_safe.setCheck(false);

				} else {
					// ����
					startService(callSmsSafeIntent);
					siv_callsms_safe.setCheck(true);
				}

			}
		});

		// ���ú�������ر���
		final String[] items = { "��͸��", "����", "����͢", "��һ�", "��������" };
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// ����һ���Ի���
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				int dd = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, dd,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// ����ѡ�в���
								Editor editor = sp.edit();
								editor.putInt("which", which);
								editor.commit();
								scv_changebg.setDesc(items[which]);

								// ȡ���Ի���
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("ȡ��", null);
				builder.show();

			}
		});
		// ����������
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
		watchDogIntent = new Intent(this, WatchDogService.class);
		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����������ʾ�����ط�����
				if (siv_watchdog.isChecked()) {
					// �ر�
					stopService(watchDogIntent);
					siv_watchdog.setCheck(false);

				} else {
					// ����
					startService(watchDogIntent);
					siv_watchdog.setCheck(true);
				}

			}
		});

	}

}
