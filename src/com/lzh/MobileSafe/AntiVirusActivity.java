package com.lzh.MobileSafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzh.MobileSafe.db.dao.AntiVirusDao;

public class AntiVirusActivity extends Activity {
	protected static final int SCANING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan;
	private ProgressBar progressBar1;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("正在扫描"+scanInfo.name);
				TextView tv = new TextView(getApplicationContext());
				if (scanInfo.isVirus) {
					tv.setTextColor(Color.RED);
					tv.setText("发现病毒:"+scanInfo.name);
				}else {
					tv.setTextColor(Color.BLACK);
					tv.setText("扫描安全:"+scanInfo.name);
				}
				ll_container.addView(tv, 0);
				break;
			case FINISH:
				tv_scan_status.setText("扫描完毕");
				iv_scan.clearAnimation();
				iv_scan.setVisibility(View.INVISIBLE);
				break;
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		tv_scan_status=(TextView) findViewById(R.id.tv_scan_status);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.setAnimation(ra);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		scanVirus();

	}

	/**
	 * 扫描病毒
	 */
	private void scanVirus() {
		pm = getPackageManager();
		tv_scan_status.setText("正在初始化杀毒引擎...");
		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				progressBar1.setMax(infos.size());
				int progress = 0;
				for (PackageInfo packageInfo : infos) {
					// apk文件的完整路径
					String sourcedir = packageInfo.applicationInfo.sourceDir;
					String md5 = getFileMd5(sourcedir);
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
					scanInfo.packname = packageInfo.packageName;
					// 查询md5的值是否在病毒数据库里存在
					if (AntiVirusDao.isVirus(md5)) {
						// 发现病毒
						scanInfo.isVirus = true;
					} else {
						// 扫描安全
						scanInfo.isVirus = false;
					}
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what =SCANING;
					handler.sendMessage(msg);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					progress++;
					progressBar1.setProgress(progress);
				}
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}
	/**
	 * 扫描信息内部类
	 */
	class ScanInfo{
		String packname;
		String name;
		boolean isVirus;
	}

	/**
	 * 获取文件的md5值
	 * 
	 * @param path
	 *            文件的全路径名称
	 * @return
	 */
	private String getFileMd5(String path) {
		try {
			File file = new File(path);
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] resulet = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : resulet) {
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append(0);
				}
				sb.append(str);
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

}
