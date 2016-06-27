package com.lzh.MobileSafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库业务类
 * @author Administrator
 *
 */
public class AntiVirusDao {
	/**
	 * 查询一个md5是否在病毒数据库里
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5){
		String path = "/data/data/com.lzh.MobileSafe/files/antivirus.db";
		boolean result = false;
		//打开病毒数据库
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
