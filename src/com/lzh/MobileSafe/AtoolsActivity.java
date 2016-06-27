package com.lzh.MobileSafe;

import com.lzh.MobileSafe.utils.SmsUtils;
import com.lzh.MobileSafe.utils.SmsUtils.SmsBackupCallBack;
import com.lzh.MobileSafe.utils.SmsUtils.SmsRestoreCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
//	private ProgressBar pb_sms_backup;
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
//		pb_sms_backup = (ProgressBar) findViewById(R.id.pb_sms_backup);
	}
	/**
	 * ��������������ز�ѯҳ��
	 * @param view
	 */
	public void numberQuery(View view){
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
		
	}
	/**
	 * ����¼������ű���
	 * @param view
	 */
	public void smsBackup(View view){
		pd = new ProgressDialog(this);
		pd.setMessage("���ڱ���");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
         new Thread(){
        	 public void run() {
                 try {
         			SmsUtils.backupSms(AtoolsActivity.this,new SmsBackupCallBack() {
						
						@Override
						public void onSmsBackup(int process) {
							pd.setProgress(process);
						}
						
						@Override
						public void beforeBackup(int max) {
							pd.setMax(max);
						}
					});
         			runOnUiThread(new Runnable() {
						public void run() {
		        			Toast.makeText(AtoolsActivity.this, "���ݳɹ�", 0).show();
						}
					});
 
         		} catch (Exception e) {
         			// TODO Auto-generated catch block
         			e.printStackTrace();
         			runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "����ʧ��", 0).show();
						}
					});
         			
         		}finally{
         			pd.dismiss();
         		}
        		 
        	 };
         }.start();
        
         

		
	}
	
	/**
	 * ����¼������Ż�ԭ
	 * @param view
	 */
	public void smsRestore(View view){
		pd = new ProgressDialog(this);
		pd.setMessage("���ڻ�ԭ");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			public void run() {
				try {
					SmsUtils.restoreSms(AtoolsActivity.this,false,new SmsRestoreCallBack() {
						
						@Override
						public void onRestore(int process) {
							pd.setProgress(process);
							
						}
						
						@Override
						public void beforeRestore(int max) {
							pd.setMax(max);
							
						}
					});
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "��ԭ�ɹ�", 0).show();	
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "��ԭʧ��", 0).show();
						}
					});
					
				}finally{
					pd.dismiss();
				}
			};
		}.start();

		
//		
	}
	

}
