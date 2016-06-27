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
		tv_splash_version.setText("版本号" + getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		installShortCut();
		// 拷贝数据库
		copyDB("address.db");
		copyDB("antivirus.db");
		if (update) {
			// 检查升级
			checkUpdate();
		} else {
			// 自动升级已关闭
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 进入主页面
					enterHome();
				}
			}, 2000);

		}

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
	}

	/**
	 * 创建快捷图标
	 */
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if (shortcut)
			return;
		Editor editor = sp.edit();
		// 发送广播的意图，告诉桌面要创建快捷图标
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式，要包含3个重要信息。1名称2图标3做什么事
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		// 桌面点击图标意图
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
	 * 把address.db数据库拷贝到data/data/<包名>/files/address.db
	 */
	private void copyDB(String filename) {
		// 只要拷贝一次，就不要再拷贝了
		try {
			File file = new File(getFilesDir(), filename);
			if (file.exists() && file.length() > 0) {
				// 有了，不需要拷贝
				Log.i(TAG, "有了，不需要拷贝");

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
			case SHOW_UPDATE_DIALOG:// 显示升级对话框
				Log.i(TAG, "显示升级对话框");
				showUpdateDialog();
				break;
			case ENTER_HOME:// 进入主页面
				enterHome();
				break;
			case URL_ERROR:// URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			case NETWORK_ERROR:// 网络错误
				enterHome();
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				break;
			case JSON_ERROR:// json解析出错
				enterHome();
				// showUpdateDialog();
				Toast.makeText(getApplicationContext(), "json解析出错", 0).show();
				break;
			default:
				break;
			}

		}

	};

	/**
	 * 检查是否有新版本，有就升级
	 */
	private void checkUpdate() {
		new Thread() {
			public void run() {
				Message mes = Message.obtain();
				long starTime = System.currentTimeMillis();
				try {

					URL url = new URL(getString(R.string.serverurl));
					// 联网
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// 联网成功
						InputStream is = conn.getInputStream();
						// 把流转成String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功" + result);
						// JSON解析
						JSONObject obj = new JSONObject(result);
						// 得到服务器的版本信息
						String version = obj.getString("version");

						desciption = obj.getString("desciption");
						apkurl = obj.getString("apkurl");
						// 校验是否有新版本
						if (getVersionName().equals(version)) {
							// 版本一致，不用升级，进入主页面
							mes.what = ENTER_HOME;

						} else {
							// 有新版本，弹出升级对话框
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
					// 花了多少时间
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
		builder.setTitle("提示升级");
		// builder.setCancelable(false);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();// 进入主页面
			}
		});
		builder.setMessage(desciption);
		builder.setPositiveButton("立即升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载apk，并替换安装
				Log.i(TAG, "下载路径" + apkurl);
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd卡存在
					// afinal
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilsafe.apk", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							Toast.makeText(getApplicationContext(), "下载失败", 1)
									.show();
							super.onFailure(t, errorNo, strMsg);
							enterHome();// 进入主页面
						}

						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							tv_update_info.setVisibility(View.VISIBLE);
							// 当前下载百分比
							int progress = (int) (current * 100 / count);
							tv_update_info.setText("下载进度" + progress + "%");
						}

						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							installAPK(t);

						}

						/*
						 * 安装apk
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
					Toast.makeText(getApplicationContext(), "没有SD卡，请安装再试", 0)
							.show();
					return;
				}

			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();// 进入主页面

			}
		});
		builder.show();
	}

	private void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 關閉當前頁面
		finish();

	}

	/**
	 * 得到应用程序的版本名称
	 */
	private String getVersionName() {
		// 用来管理手机的APK
		PackageManager pm = getPackageManager();

		try {
			// 得到APK功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
