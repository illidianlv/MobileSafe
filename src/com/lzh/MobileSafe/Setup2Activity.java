package com.lzh.MobileSafe;


import com.lzh.MobileSafe.ui.SettingItemView;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_setup2_sim;
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String sim = sp.getString("sim", null);

		siv_setup2_sim.setCheck(!TextUtils.isEmpty(sim));
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 保存sim系列号
				Editor editor = sp.edit();
				if (siv_setup2_sim.isChecked()) {
					siv_setup2_sim.setCheck(false);
					editor.putString("sim", null);
				} else {
					siv_setup2_sim.setCheck(true);
					String sim =  tm.getSimSerialNumber();
					editor.putString("sim", sim);
				}
				editor.commit();

			}
		});
	}

	@Override
	public void showNext() {
		//是否绑定sim卡
		String simString = sp.getString("sim", null);
		if (TextUtils.isEmpty(simString)) {
			//没有绑定
			Toast.makeText(this, "sim卡没有绑定", 1).show();
			return;
			
		}
		
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}
	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		
	}

}
