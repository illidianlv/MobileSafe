package com.lzh.MobileSafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.MobileSafe.utils.StreamTools;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 1;
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private TextView tv_update_info;
	private SharedPreferences sp;
	private String desciption;
	private String apkurl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("�汾��" + getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		installShortCut();
		// �������ݿ�
		copyDB("address.db");
		copyDB("antivirus.db");
		if (update) {
			// �������
			checkUpdate();
		} else {
			// �Զ������ѹر�
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// ������ҳ��
					enterHome();
				}
			}, 2000);

		}

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
	}

	/**
	 * �������ͼ��
	 */
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if (shortcut)
			return;
		Editor editor = sp.edit();
		// ���͹㲥����ͼ����������Ҫ�������ͼ��
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// ��ݷ�ʽ��Ҫ����3����Ҫ��Ϣ��1����2ͼ��3��ʲô��
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ�С��ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		// ������ͼ����ͼ
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(),
				"com.lzh.MobileSafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
	}

	/**
	 * ��address.db���ݿ⿽����data/data/<����>/files/address.db
	 */
	private void copyDB(String filename) {
		// ֻҪ����һ�Σ��Ͳ�Ҫ�ٿ�����
		try {
			File file = new File(getFilesDir(), filename);
			if (file.exists() && file.length() > 0) {
				// ���ˣ�����Ҫ����
				Log.i(TAG, "���ˣ�����Ҫ����");

			} else {
				InputStream is = getAssets().open(filename);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// ��ʾ�����Ի���
				Log.i(TAG, "��ʾ�����Ի���");
				showUpdateDialog();
				break;
			case ENTER_HOME:// ������ҳ��
				enterHome();
				break;
			case URL_ERROR:// URL����
				enterHome();
				Toast.makeText(getApplicationContext(), "URL����", 0).show();
				break;
			case NETWORK_ERROR:// �������
				enterHome();
				Toast.makeText(getApplicationContext(), "�������", 0).show();
				break;
			case JSON_ERROR:// json��������
				enterHome();
				// showUpdateDialog();
				Toast.makeText(getApplicationContext(), "json��������", 0).show();
				break;
			default:
				break;
			}

		}

	};

	/**
	 * ����Ƿ����°汾���о�����
	 */
	private void checkUpdate() {
		new Thread() {
			public void run() {
				Message mes = Message.obtain();
				long starTime = System.currentTimeMillis();
				try {

					URL url = new URL(getString(R.string.serverurl));
					// ����
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// �����ɹ�
						InputStream is = conn.getInputStream();
						// ����ת��String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "�����ɹ�" + result);
						// JSON����
						JSONObject obj = new JSONObject(result);
						// �õ��������İ汾��Ϣ
						String version = obj.getString("version");

						desciption = obj.getString("desciption");
						apkurl = obj.getString("apkurl");
						// У���Ƿ����°汾
						if (getVersionName().equals(version)) {
							// �汾һ�£�����������������ҳ��
							mes.what = ENTER_HOME;

						} else {
							// ���°汾�����������Ի���
							mes.what = SHOW_UPDATE_DIALOG;
						}

					}
				} catch (MalformedURLException e) {
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					mes.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					mes.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					// ���˶���ʱ��
					long dTime = endTime - starTime;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					handler.sendMessage(mes);
				}

			};
		}.start();

	}

	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��ʾ����");
		// builder.setCancelable(false);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();// ������ҳ��
			}
		});
		builder.setMessage(desciption);
		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ����apk�����滻��װ
				Log.i(TAG, "����·��" + apkurl);
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd������
					// afinal
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilsafe.apk", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							Toast.makeText(getApplicationContext(), "����ʧ��", 1)
									.show();
							super.onFailure(t, errorNo, strMsg);
							enterHome();// ������ҳ��
						}

						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							tv_update_info.setVisibility(View.VISIBLE);
							// ��ǰ���ذٷֱ�
							int progress = (int) (current * 100 / count);
							tv_update_info.setText("���ؽ���" + progress + "%");
						}

						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							installAPK(t);

						}

						/*
						 * ��װapk
						 */

						private void installAPK(File t) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t),
									"application/vnd.android.package-archive");
							startActivity(intent);

						}
					});
				} else {
					Toast.makeText(getApplicationContext(), "û��SD�����밲װ����", 0)
							.show();
					return;
				}

			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();// ������ҳ��

			}
		});
		builder.show();
	}

	private void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// �P�]��ǰ���
		finish();

	}

	/**
	 * �õ�Ӧ�ó���İ汾����
	 */
	private String getVersionName() {
		// ���������ֻ���APK
		PackageManager pm = getPackageManager();

		try {
			// �õ�APK�����嵥�ļ�
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
