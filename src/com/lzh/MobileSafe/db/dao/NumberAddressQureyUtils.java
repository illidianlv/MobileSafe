package com.lzh.MobileSafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQureyUtils {
	private static String path ="data/data/com.lzh.MobileSafe/files/address.db";
	/**
	 * ��һ���������������һ��������
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number){
		String address = number;
		SQLiteDatabase datebase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

		//path ��splashҳ���address.db���ݿ⿽����data/data/<����>/files/address.db
		
		//�ֻ�����������ʽ    �ֻ����룺13 14 15 16 18
		if (number.matches("^1[34568]\\d{9}$")) {
			//�ֻ�����
			Cursor cursor = datebase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)",
					new String[]{number.substring(0, 7)} );
			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location;
				
			}
			cursor.close();
			
		}else{
			//��������
			switch (number.length()) {
			case 3:
				//110
				address = "�������";
				break;
			case 4:
				//5554
				address = "ģ����";
				break;
			case 5:
				//10086
				address = "�ͷ��绰";
				break;
			case 7:
				address = "���غ���";
				break;
			case 8:
				address = "���غ���";
				break;

			default:
				//��;�绰
				if (number.length()>10&& number.startsWith("0")) {
					//010-12345678
					Cursor  cursor  = datebase.rawQuery("select location from data2 where area = ?",
						new String[]{number.substring(1, 3)});	
					while (cursor.moveToNext()) {
						String location = cursor .getString(0);
						address = location .substring(0, location.length()-2);
						
					}
					cursor.close();
					//0596-6531666
					cursor  = datebase.rawQuery("select location from data2 where area = ?",
							new String[]{number.substring(1, 4)});	
					while (cursor.moveToNext()) {
						String location = cursor .getString(0);
						address = location .substring(0, location.length()-2);
						
					}
					cursor.close();
					
				}
				break;
			}
		}
		
		

		return address;
		
	}

}
