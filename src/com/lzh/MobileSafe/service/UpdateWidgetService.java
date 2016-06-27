package com.lzh.MobileSafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.lzh.MobileSafe.R;
import com.lzh.MobileSafe.receiver.MyWidget;
import com.lzh.MobileSafe.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	protected static final String TAG = "UpdateWidgetService";
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "锁屏啦...");
            stopTimer();

		}
	}
	private class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "解屏啦...");
            startTimer();

		}
	}

	@Override
	public void onCreate() {
		offReceiver = new ScreenOffReceiver();
		onReceiver = new ScreenOnReceiver();
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		awm = AppWidgetManager.getInstance(this);
		startTimer();
		super.onCreate();
	}

	private void startTimer() {
		if (timer==null&&task==null) {
			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
//					Log.i(TAG, "更新widget");
					// 设置更新的组件
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);
					views.setTextViewText(
							R.id.process_count,
							"正在运行的进程:"
									+ SystemInfoUtils
											.getRunningProcessCount(getApplicationContext())
									+ "个");
					long size = SystemInfoUtils
							.getAvailMem(getApplicationContext());
					views.setTextViewText(
							R.id.process_memory,
							"可用内存:"
									+ Formatter.formatFileSize(
											getApplicationContext(), size));
					// 描述一个动作，这个动作是由另一个应用执行
					// 自定义一个广播事件，杀死后台进程的事件
					Intent intent = new Intent();
					intent.setAction("com.lzh.MobileSafe.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(provider, views);

				}
			};
			timer.schedule(task, 0, 3000);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
		stopTimer();
	}

	private void stopTimer() {
		if (timer!=null&&task!=null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;			
		}
	}
	
}
