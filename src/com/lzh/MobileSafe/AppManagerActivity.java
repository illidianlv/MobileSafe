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
	 * ����Ӧ�ó������Ϣ
	 */
	private List<AppInfo> appInfos;
	/**
	 * �û�Ӧ�ó������Ϣ
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * ϵͳӦ�ó������Ϣ
	 */
	private List<AppInfo> systemAppInfos;
	/**
	 * ��������������
	 */
	private PopupWindow popupWindow;
	/**
	 * ж�� ���� ����
	 */
	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	/**
	 * ���������Ŀ
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
				.setText("SD�����ÿռ䣺" + Formatter.formatFileSize(this, sdsize));
		tv_avial_rom.setText("�ڴ���ÿռ䣺"
				+ Formatter.formatFileSize(this, romsize));

		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

		fillData();
		// ��listviewע��һ������������
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// ����ʱ����õķ���
			// firstVisibleItem ��һ���ɼ���Ŀ��listview�����λ��
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����(" + systemAppInfos.size() + ")");
					} else {
						tv_status.setText("�û�����(" + userAppInfos.size() + ")");
					}
				}

			}
		});
		/**
		 * ����listview item�ĵ���¼�
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				} else if (position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {// �û�����
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// ϵͳ����
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
				// ����Ч�����ű���Ҫ�����б�����ɫ������
				// ͸����ɫҲ����ɫ
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				// �ڴ��������õĿ��ֵ�������� ---->dip
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
		//������ ������Ŀ������¼�������
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return true;
				} else if (position == userAppInfos.size() + 1) {
					return true;
				} else if (position <= userAppInfos.size()) {// �û�����
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// ϵͳ����
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				//�ж���Ŀ�Ƿ���ڳ��������ݿ���
				if(dao.find(appInfo.getPackname())){
					//���������������������½���Ϊ����ͼ��
					dao.delete(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.unlock);
				}else {
					//�������򣬸��½���Ϊ����ͼ��
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
				// ����listview������������
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
		public int getCount() {// ����listview�ж��ٸ���Ŀ
			// return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if (position == 0) {// ������ʾӦ�ó�����ٸ���С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����(" + userAppInfos.size() + ")");
				return tv;
			} else if (position == userAppInfos.size() + 1) {// �ڶ���С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����(" + systemAppInfos.size() + ")");
				return tv;
			} else if (position <= userAppInfos.size()) {// �û�����
				int newposition = position - 1;// ����һ��textviռ��λ��
				appInfo = userAppInfos.get(newposition);
			} else {// ϵͳ����
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ��������Ƿ�Ϊ�գ���Ҫ�ж��Ƿ��Ǻ��ʵ�����ȥ����
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
				holder.tv_location.setText("�ֻ��ڴ�"+"uid:"+appInfo.getUid());
			} else {
				holder.tv_location.setText("�ⲿ�洢"+"uid:"+appInfo.getUid());
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
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿռ�
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
		// �Ѿɵĵ�������ص�
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
	 * ���ֶ�Ӧ�ĵ���¼�
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_start:
			Log.i(TAG, "����" + appInfo.getName());
			startApplication();
			break;
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				Log.i(TAG, "ж��" + appInfo.getName());
				uninstallApplication();
			} else {
				Toast.makeText(this, "ϵͳӦ��ֻ��rootȨ�޲���ж��", 0).show();
			}
			break;
		case R.id.ll_share:
			Log.i(TAG, "����" + appInfo.getName());
			shareApplication();
			break;
		default:
			break;
		}
	}
   /**
    * ����Ӧ��
    */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ����������ƽ�"+appInfo.getName());
	    startActivity(intent);
	}

	/**
	 * ж��Ӧ��
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
		// ˢ�½���
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ����һ��Ӧ�ó���
	 */
	private void startApplication() {
		// ��ѯ���Ӧ�ó�������Activity��������������
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER" );
		// //��ѯ�����ֻ��Ͼ�����������������activity
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "����������ǰӦ��", 0).show();
		}
	}
}
