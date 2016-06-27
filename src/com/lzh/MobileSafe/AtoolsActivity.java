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
	 * 点击进入号码归属地查询页面
	 * @param view
	 */
	public void numberQuery(View view){
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
		
	}
	/**
	 * 点击事件，短信备份
	 * @param view
	 */
	public void smsBackup(View view){
		pd = new ProgressDialog(this);
		pd.setMessage("正在备份");
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
		        			Toast.makeText(AtoolsActivity.this, "备份成功", 0).show();
						}
					});
 
         		} catch (Exception e) {
         			// TODO Auto-generated catch block
         			e.printStackTrace();
         			runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败", 0).show();
						}
					});
         			
         		}finally{
         			pd.dismiss();
         		}
        		 
        	 };
         }.start();
        
         

		
	}
	
	/**
	 * 点击事件，短信还原
	 * @param view
	 */
	public void smsRestore(View view){
		pd = new ProgressDialog(this);
		pd.setMessage("正在还原");
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
							Toast.makeText(AtoolsActivity.this, "还原成功", 0).show();	
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原失败", 0).show();
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
