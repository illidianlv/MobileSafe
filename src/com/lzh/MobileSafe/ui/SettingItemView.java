package com.lzh.MobileSafe.ui;

import com.lzh.MobileSafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * �Զ�����Ͽؼ�������������textview��һ��checkbox�����и�һview
 * @author Administrator
 *
 */

public class SettingItemView extends RelativeLayout {
    
	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	private String desc_on;
	private String desc_off;
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}
	/*
	 * �������������Ĺ��췽���������ļ�ʹ�õ�ʱ�����
	 */

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.lzh.MobileSafe", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.lzh.MobileSafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.lzh.MobileSafe", "desc_off");
		tv_title.setText(title);
	}

	public SettingItemView(Context context) {
		super(context);
		iniView(context);
	}
/**
 *��ʼ�������ļ� 
 * @param context
 */
	private void iniView(Context context) {
		//��һ�������ļ�ת����view����������SettingItemView
		View.inflate(context, R.layout.setting_item_view,this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
		
	}
	/*
	 * У����Ͽؼ��Ƿ�ѡ��
	 */
     public boolean isChecked(){
    	 return cb_status.isChecked();
     }
 	/*
 	 * ������Ͽؼ�״̬
 	 */
      public void setCheck(boolean checked){
    	  if (checked) {
    		 setDesc(desc_on);
		} else {
			 setDesc(desc_off);
		}
     	  cb_status.setChecked(checked);
      }
      /*
       * ������Ͽؼ�������Ϣ
       */
      public void setDesc(String text){
    	  tv_desc.setText(text);
      }
}
