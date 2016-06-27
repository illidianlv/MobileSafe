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
		"�ֻ�����","ͨѶ��ʿ","�������",
		"���̹���","����ͳ��","�ֻ�ɱ��",
		"��������","�߼�����","��������"
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
				case 0://�����ֻ�����ҳ��
                    showLostFindDialog();
					break;
				case 1://���������ҳ��
					intent = new Intent(getApplicationContext(), CallSmsActivity.class);
					startActivity(intent);
					break;
				case 2://�����������ҳ��
					intent = new Intent(getApplicationContext(), AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3://������̹���
					intent = new Intent(getApplicationContext(), TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4://������������
					intent = new Intent(getApplicationContext(), TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5://�����ֻ�ɱ��
					intent = new Intent(getApplicationContext(), AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6://���뻺������
					intent = new Intent(getApplicationContext(), CleanCacheActivity.class);
					startActivity(intent);
					break;
				case 7://����߼�����
					intent = new Intent(getApplicationContext(),AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8://��������ҳ��
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
			//�Ѿ��������룬��������Ի���
			showEnterDialog();
		}else {
			//��û�������룬�������öԻ���
			showSetupPwdDialog();
			
		}
		
	}
	private EditText et_setup_pwd,et_setup_confirm;
	private Button bt_ok,bt_cancel;
	private AlertDialog dialog;
	/*
	 * ��������Ի���
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		//�Զ���һ�������ļ���ϵͳ�Դ���û�п�������ĶԻ���
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_passwrod, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �ѶԻ���ȡ����
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ȡ������
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(HomeActivity.this, "����Ϊ��", 0).show();
					return;
				}
				//�ж������Ƿ�һ��
				if (password.equals(password_confirm)) {
					//��������,�ѶԻ���ȡ���������ֻ�����ҳ��
					Editor editor = sp.edit();
					editor.putString("password", MD5Util.md5password(password));//������ܺ������
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "����һ�£���������,�ѶԻ���ȡ���������ֻ�����ҳ��");

				}else {
					Toast.makeText(HomeActivity.this, "���벻һ��", 0).show();
					return;
				}
				
				
			}
		});
		
		builder.setView(view);
		dialog = builder.show();
		
	}
	/*
	 * ��������Ի���
	 */

	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(this);
		//�Զ���һ�������ļ���ϵͳ�Դ���û�п�������ĶԻ���
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_passwrod, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �ѶԻ���ȡ����
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ȡ������
				String password = et_setup_pwd.getText().toString().trim();
				String savePassword = sp.getString("password", "");
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "����Ϊ��", 0).show();
					return;
				}
				if (MD5Util.md5password(password).equals(savePassword)) {
					//��������֮ǰ���õĵ����룬�ѶԻ���ȡ����������ҳ��
					dialog.dismiss();
					Log.i(TAG, "�ѶԻ���ȡ���������ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
					
				} else {
					
					Toast.makeText(HomeActivity.this, "�������", 0).show();
					return;
				}
				
			}
		});

		builder.setView(view);
		dialog = builder.show();
		
	}

	/*
	 * �Д��Ƿ��O���^�ܴa
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
