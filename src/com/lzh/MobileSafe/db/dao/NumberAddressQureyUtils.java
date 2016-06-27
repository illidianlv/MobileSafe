package com.lzh.MobileSafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQureyUtils {
	private static String path ="data/data/com.lzh.MobileSafe/files/address.db";
	/**
	 * 传一个号码进来，返回一个归属地
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number){
		String address = number;
		SQLiteDatabase datebase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

		//path 在splash页面把address.db数据库拷贝到data/data/<包名>/files/address.db
		
		//手机号码正则表达式    手机号码：13 14 15 16 18
		if (number.matches("^1[34568]\\d{9}$")) {
			//手机号码
			Cursor cursor = datebase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)",
					new String[]{number.substring(0, 7)} );
			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location;
				
			}
			cursor.close();
			
		}else{
			//其他号码
			switch (number.length()) {
			case 3:
				//110
				address = "特殊号码";
				break;
			case 4:
				//5554
				address = "模拟器";
				break;
			case 5:
				//10086
				address = "客服电话";
				break;
			case 7:
				address = "本地号码";
				break;
			case 8:
				address = "本地号码";
				break;

			default:
				//长途电话
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
