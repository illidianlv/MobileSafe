package com.lzh.MobileSafe.ui;

import com.lzh.MobileSafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 自定义组合控件，它里面两个textview，一个checkbox，还有个一view
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
	 * 带有两个参数的构造方法，布局文件使用的时候调用
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
 *初始化布局文件 
 * @param context
 */
	private void iniView(Context context) {
		//把一个布局文件转换成view，并加载在SettingItemView
		View.inflate(context, R.layout.setting_item_view,this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
		
	}
	/*
	 * 校验组合控件是否选中
	 */
     public boolean isChecked(){
    	 return cb_status.isChecked();
     }
 	/*
 	 * 设置组合控件状态
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
       * 设置组合控件描述信息
       */
      public void setDesc(String text){
    	  tv_desc.setText(text);
      }
}
