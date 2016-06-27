package com.lzh.MobileSafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzh.MobileSafe.db.AppLockDBOpenHelper;

/**
 * ��������dao
 * 
 * @author Administrator
 * 
 */
public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;

	/**
	 * ���췽�� ������
	 */
	public AppLockDao(Context context) {
		super();
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * ���һ��Ҫ������Ӧ�ð���
	 * 
	 * @param packname
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.lzh.mobilesafe.applockchange");
		context.sendBroadcast(intent);
	}
	/**
	 * ɾ��һ��Ҫ������Ӧ�ð���
	 * @param packname
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.lzh.mobilesafe.applockchange");
		context.sendBroadcast(intent);
	}
	/**
	 * ��ѯһ�������������Ƿ����
	 * @param packname
	 * @return
	 */
	public boolean find(String packname){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
		if (cursor.moveToNext()) {
			result= true;
		}
		cursor.close();
		db.close();
		return result;
		
	}
	/**
	 * ��ѯȫ������
	 * @return
	 */
	public List<String> findAll(){
		List<String> protectPacknames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			protectPacknames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return protectPacknames;
		
	}

}
