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
	//�õ�λ�÷���
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
		//����λ�÷���
		//��λ���ṩ����������
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, listener);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//ȡ������λ�÷���
		lm.removeUpdates(listener);
		listener = null;
	}
    class MyLocationListener implements LocationListener{
   	 /**
   	  * ��λ�øı�ʱ��ص�
   	  */

		@Override
		public void onLocationChanged(Location location) {
           String longitude = "longitude"+location.getLongitude()+"\n";
           String latitude = "latitude"+location.getLatitude()+"\n";
           String accuracy = "accuracy"+location.getAccuracy()+"\n";
           //�����Ÿ���ȫ����
           
           //�ѱ�׼gps����ת����������
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
		 * ��״̬�ı�ʱ��ص�������---�رգ��ر�---����
		 */

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
       /**
        * ĳһλ���ṩ�߿�ʹ��
        */

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
       /**
        * ĳһλ���ṩ�߲���ʹ��
        */

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
   	 
    }
	

}
