package com.lzh.MobileSafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �������ݿ�ҵ����
 * @author Administrator
 *
 */
public class AntiVirusDao {
	/**
	 * ��ѯһ��md5�Ƿ��ڲ������ݿ���
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5){
		String path = "/data/data/com.lzh.MobileSafe/files/antivirus.db";
		boolean result = false;
		//�򿪲������ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
		
	}
	

}
