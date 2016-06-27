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
 * 黑名单数据库增删改查业务类
 * @author Administrator
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;
   /**
    * 构造方法 上下文
    */
	public BlackNumberDao(Context context) {
		super();
		helper = new BlackNumberDBOpenHelper(context);
	}
	/**
	 * 查询黑名单号码拦截模式
	 * @param number
	 * @return 返回号码的拦截模式，如果不是黑名单号码，返回null
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
	 * 查询全部黑名单号码
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
	 * 查询部分黑名单号码
	 * @param offset 从哪个位置获取数据
	 * @param maxnumber 一次获取最多
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
	 * 添加黑名单号码
	 * @param number 黑名单号码
	 * @param mode 拦截模式 1电话拦截 2短信拦截 3全部拦截
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
	 * 修改黑名单号码的拦截模式
	 * @param number 要修改黑名单号码
	 * @param mode 新拦截模式
	 */
	public void update(String number,String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
        values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	/**
	 * 删除黑名单号码
	 * @param number 要修改黑名单号码
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}
}
