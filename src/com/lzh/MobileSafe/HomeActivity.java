package com.lzh.MobileSafe;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.MobileSafe.utils.MD5Util;

public class HomeActivity extends Activity {
	protected static final String TAG = "HomeActivity";
	private MyAdapter adapter;
	private GridView list_home;
	private SharedPreferences sp;
	private static String [] names = {
		"手机防盗","通讯卫士","软件管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"
	};
	private static int [] ids = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent;
				switch (arg2) {
				case 0://进入手机防盗页面
                    showLostFindDialog();
					break;
				case 1://进入黑名单页面
					intent = new Intent(getApplicationContext(), CallSmsActivity.class);
					startActivity(intent);
					break;
				case 2://进入软件管理页面
					intent = new Intent(getApplicationContext(), AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3://进入进程管理
					intent = new Intent(getApplicationContext(), TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4://进入流量管理
					intent = new Intent(getApplicationContext(), TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5://进入手机杀毒
					intent = new Intent(getApplicationContext(), AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6://进入缓存清理
					intent = new Intent(getApplicationContext(), CleanCacheActivity.class);
					startActivity(intent);
					break;
				case 7://进入高级工具
					intent = new Intent(getApplicationContext(),AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8://进入设置页面
					intent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
				
			}
		});
		
	}

	protected void showLostFindDialog() {
		if (isSetupPwd()) {
			//已经设置密码，弹出输入对话框
			showEnterDialog();
		}else {
			//还没设置密码，弹出设置对话框
			showSetupPwdDialog();
			
		}
		
	}
	private EditText et_setup_pwd,et_setup_confirm;
	private Button bt_ok,bt_cancel;
	private AlertDialog dialog;
	/*
	 * 设置密码对话框
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		//自定义一个布局文件，系统自带的没有可以输入的对话框。
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_passwrod, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 把对话框取消掉
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(HomeActivity.this, "密码为空", 0).show();
					return;
				}
				//判断密码是否一致
				if (password.equals(password_confirm)) {
					//保存密码,把对话框取消，进入手机防盗页面
					Editor editor = sp.edit();
					editor.putString("password", MD5Util.md5password(password));//保存加密后的密码
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "密码一致，保存密码,把对话框取消，进入手机防盗页面");

				}else {
					Toast.makeText(HomeActivity.this, "密码不一致", 0).show();
					return;
				}
				
				
			}
		});
		
		builder.setView(view);
		dialog = builder.show();
		
	}
	/*
	 * 输入密码对话框
	 */

	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(this);
		//自定义一个布局文件，系统自带的没有可以输入的对话框。
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_passwrod, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 把对话框取消掉
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String savePassword = sp.getString("password", "");
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "密码为空", 0).show();
					return;
				}
				if (MD5Util.md5password(password).equals(savePassword)) {
					//密码是我之前设置的的密码，把对话框取消，进入主页面
					dialog.dismiss();
					Log.i(TAG, "把对话框取消，进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
					
				} else {
					
					Toast.makeText(HomeActivity.this, "密码错误", 0).show();
					return;
				}
				
			}
		});

		builder.setView(view);
		dialog = builder.show();
		
	}

	/*
	 * 判斷是否設置過密碼
	 */
	private boolean isSetupPwd(){
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view =View.inflate(HomeActivity.this, R.layout.list_item, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			iv_item.setImageResource(ids[position]);
			tv_item.setText(names[position]);
			return view;

		}
		
	}

}
