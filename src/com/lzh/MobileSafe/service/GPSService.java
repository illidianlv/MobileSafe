package com.lzh.MobileSafe.service;


import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {
	//用到位置服务
	private LocationManager lm;
	private MyLocationListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyLocationListener();
		//监听位置服务
		//给位置提供者设置条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, listener);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//取消监听位置服务
		lm.removeUpdates(listener);
		listener = null;
	}
    class MyLocationListener implements LocationListener{
   	 /**
   	  * 当位置改变时候回调
   	  */

		@Override
		public void onLocationChanged(Location location) {
           String longitude = "longitude"+location.getLongitude()+"\n";
           String latitude = "latitude"+location.getLatitude()+"\n";
           String accuracy = "accuracy"+location.getAccuracy()+"\n";
           //发短信给安全号码
           
           //把标准gps坐标转换火星坐标
           InputStream is;
		try {
			is = getAssets().open("axisoffset.dat");
			ModifyOffset offset = ModifyOffset.getInstance(is);
			PointDouble double1 = offset.c2s(new PointDouble(location.getLongitude(),location.getLatitude()));
			longitude = "longitude"+double1.x+"\n";
			latitude = "latitude"+double1.y+"\n";
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
           SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
           Editor editor = sp.edit();
           editor.putString("lastlocation", longitude+latitude+accuracy);
           editor.commit();
			
			
		}
		/**
		 * 当状态改变时候回调。开启---关闭，关闭---开启
		 */

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
       /**
        * 某一位置提供者可使用
        */

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
       /**
        * 某一位置提供者不可使用
        */

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
   	 
    }
	

}
