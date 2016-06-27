package com.lzh.MobileSafe;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.MobileSafe.db.dao.AppLockDao;
import com.lzh.MobileSafe.domain.AppInfo;
import com.lzh.MobileSafe.engine.AppInfoProvider;
import com.lzh.MobileSafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private TextView tv_avial_rom;
	private TextView tv_avial_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private TextView tv_status;
	private AppManagerAdapter adapter;
	private AppLockDao dao;
	/**
	 * 所有应用程序包信息
	 */
	private List<AppInfo> appInfos;
	/**
	 * 用户应用程序包信息
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * 系统应用程序包信息
	 */
	private List<AppInfo> systemAppInfos;
	/**
	 * 弹出的悬浮窗体
	 */
	private PopupWindow popupWindow;
	/**
	 * 卸载 开启 分享
	 */
	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	/**
	 * 被点击的条目
	 */
	private AppInfo appInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao = new AppLockDao(this);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_avial_rom = (TextView) findViewById(R.id.tv_avial_rom);
		tv_avial_sd = (TextView) findViewById(R.id.tv_avial_sd);
		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		long romsize = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());

		tv_avial_sd
				.setText("SD卡可用空间：" + Formatter.formatFileSize(this, sdsize));
		tv_avial_rom.setText("内存可用空间："
				+ Formatter.formatFileSize(this, romsize));

		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

		fillData();
		// 给listview注册一个滚动监听器
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// 滚动时候调用的方法
			// firstVisibleItem 第一个可见条目在listview里面的位置
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统程序(" + systemAppInfos.size() + ")");
					} else {
						tv_status.setText("用户程序(" + userAppInfos.size() + ")");
					}
				}

			}
		});
		/**
		 * 设置listview item的点击事件
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				} else if (position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {// 用户程序
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// 系统程序
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				dismissPopupWindow();

				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_app_item, null);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);

				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				int[] location = new int[2];
				view.getLocationInWindow(location);
				popupWindow = new PopupWindow(contentView, -2, -2);
				// 动画效果播放必须要求窗体有背景颜色！！！
				// 透明颜色也是颜色
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				// 在代码里设置的宽高值都是像素 ---->dip
				int dip = 50;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AlphaAnimation aa = new AlphaAnimation(0.4f, 0.9f);
				aa.setDuration(300);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);
			}
		});
		//程序锁 设置条目长点击事件监听器
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return true;
				} else if (position == userAppInfos.size() + 1) {
					return true;
				} else if (position <= userAppInfos.size()) {// 用户程序
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// 系统程序
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				//判断条目是否存在程序锁数据库里
				if(dao.find(appInfo.getPackname())){
					//如果锁定，解除锁定，更新界面为解锁图标
					dao.delete(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.unlock);
				}else {
					//锁定程序，更新界面为锁定图标
					dao.add(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.lock);
				}
				return true;
			}
		});

	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}

				}
				// 加载listview的数据适配器
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (adapter == null) {
							adapter = new AppManagerAdapter();
							lv_app_manager.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						tv_status.setVisibility(View.VISIBLE);
						ll_loading.setVisibility(View.INVISIBLE);

					}
				});
			};
		}.start();
	}

	private TextView tv_name;

	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {// 控制listview有多少个条目
			// return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if (position == 0) {// 用来显示应用程序多少个的小标签
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户程序(" + userAppInfos.size() + ")");
				return tv;
			} else if (position == userAppInfos.size() + 1) {// 第二个小标签
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序(" + systemAppInfos.size() + ")");
				return tv;
			} else if (position <= userAppInfos.size()) {// 用户程序
				int newposition = position - 1;// 多了一个textvi占用位置
				appInfo = userAppInfos.get(newposition);
			} else {// 系统程序
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 不仅检查是否为空，还要判断是否是合适的类型去复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.iv_status = (ImageView) view.findViewById(R.id.iv_status);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("手机内存"+"uid:"+appInfo.getUid());
			} else {
				holder.tv_location.setText("外部存储"+"uid:"+appInfo.getUid());
			}
			if(dao.find(appInfo.getPackname())){
				holder.iv_status.setImageResource(R.drawable.lock);
			}else {
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
		ImageView iv_status;
	}

	/**
	 * 获取某个目录的可用空间
	 * 
	 * @param path
	 * @return
	 */
	@SuppressLint("NewApi")
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		statFs.getBlockCountLong();
		long size = statFs.getBlockSizeLong();
		long count = statFs.getAvailableBlocksLong();
		return size * count;
	}

	private void dismissPopupWindow() {
		// 把旧的弹出窗体关掉
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dismissPopupWindow();
		super.onDestroy();

	}

	/**
	 * 布局对应的点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_start:
			Log.i(TAG, "启动" + appInfo.getName());
			startApplication();
			break;
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				Log.i(TAG, "卸载" + appInfo.getName());
				uninstallApplication();
			} else {
				Toast.makeText(this, "系统应用只有root权限才能卸载", 0).show();
			}
			break;
		case R.id.ll_share:
			Log.i(TAG, "分享" + appInfo.getName());
			shareApplication();
			break;
		default:
			break;
		}
	}
   /**
    * 分享应用
    */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名称叫"+appInfo.getName());
	    startActivity(intent);
	}

	/**
	 * 卸载应用
	 */
	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 刷新界面
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 启动一个应用程序
	 */
	private void startApplication() {
		// 查询这个应用程序的入口Activity。把它开启起来
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER" );
		// //查询出来手机上具有启动能力的所有activity
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "不能启动当前应用", 0).show();
		}
	}
}
