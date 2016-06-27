package com.lzh.MobileSafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzh.MobileSafe.db.BlackNumberDBOpenHelper;
import com.lzh.MobileSafe.db.domain.BlackNumberInfo;

/**
 * ���������ݿ���ɾ�Ĳ�ҵ����
 * @author Administrator
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;
   /**
    * ���췽�� ������
    */
	public BlackNumberDao(Context context) {
		super();
		helper = new BlackNumberDBOpenHelper(context);
	}
	/**
	 * ��ѯ��������������ģʽ
	 * @param number
	 * @return ���غ��������ģʽ��������Ǻ��������룬����null
	 */
	
	public String findMode(String number){
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
			
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * ��ѯȫ������������
	 * @param number
	 * @return
	 */
	
	public List<BlackNumberInfo> findAll(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
			
		}
		cursor.close();
		db.close();
		return result;

	}
	/**
	 * ��ѯ���ֺ���������
	 * @param offset ���ĸ�λ�û�ȡ����
	 * @param maxnumber һ�λ�ȡ���
	 * @return
	 */
	public List<BlackNumberInfo> findPart(int offset,int maxnumber){
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",
				new String[]{String.valueOf(maxnumber),String.valueOf(offset)});
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
			
		}
		cursor.close();
		db.close();
		return result;

	}
	/**
	 * ���Ӻ���������
	 * @param number ����������
	 * @param mode ����ģʽ 1�绰���� 2�������� 3ȫ������
	 */
	public void add(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	/**
	 * �޸ĺ��������������ģʽ
	 * @param number Ҫ�޸ĺ���������
	 * @param mode ������ģʽ
	 */
	public void update(String number,String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
        values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	/**
	 * ɾ������������
	 * @param number Ҫ�޸ĺ���������
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}
}