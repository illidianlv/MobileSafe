package com.lzh.MobileSafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * 短信工具类
 * 
 * @author Administrator
 * 
 */
public class SmsUtils {
	private static final String TAG = "SmsUtils";

	/**
	 * 备份短信回调接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface SmsBackupCallBack {
		/**
		 * 开始备份前 设置进度最大值
		 * 
		 * @param max
		 *            总进度
		 */
		public void beforeBackup(int max);

		/**
		 * 备份过程中，增加进度
		 * 
		 * @param process
		 *            当前进度
		 */
		public void onSmsBackup(int process);

	}
	/**
	 * 还原短信回调接口
	 * @author Administrator
	 *
	 */
	
	public interface SmsRestoreCallBack{
		public void beforeRestore(int max);
		public void onRestore(int process);
	}
	


	/**
	 * 备份用户短信
	 * 
	 * @param context
	 *            上下文
	 * @param SmsBackupCallBack
	 *            备份短信的回调接口
	 * @throws Exception
	 */
	public static void backupSms(Context context, SmsBackupCallBack callBack)
			throws Exception {
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// 把用户短信一条一条读出来，按照一定格式写到文件里
		XmlSerializer serializer = Xml.newSerializer();// 获取xml文件生成器（系列化器）
		// 初始化生成器
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");

		Uri uri = Uri.parse("content://sms/");// null 所有短信
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		// 备份开始 设置进度条最大值
		int max = cursor.getCount();
		serializer.attribute(null, "max", max + "");
		// pb.setMax(max);
		callBack.beforeBackup(max);
		int process = 0;
		while (cursor.moveToNext()) {
			// Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			serializer.startTag(null, "sms");

			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");

			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");

			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");

			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			serializer.endTag(null, "sms");
			// 备份过程中，增加进度
			process++;
			// pb.setProgress(process);
			callBack.onSmsBackup(process);

		}
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();

	}

	/**
	 * 还原短信
	 * 
	 * @param context
	 * @param flag
	 *            是否清理原来短信
	 * @param callBack
	 * @throws Exception
	 */

	public static void restoreSms(Context context, boolean flag,SmsRestoreCallBack callBack)
			throws Exception {
		Uri uri = Uri.parse("content://sms/");
		ContentResolver resolver = context.getContentResolver();
		if (flag) {
			resolver.delete(uri, null, null);
		}

		// 1.读取sd卡上的xml文件
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		// File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		ContentValues values = null;
		// 2.读取max

		// 3.读取每一条短信信息 body address type date
		int type = parser.getEventType();
		Log.i(TAG, "type = " + type);
		int currentcount = 0;
		while (type != XmlPullParser.END_DOCUMENT) {
//			Thread.sleep(500);
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("smss".equals(parser.getName())) {
                    callBack.beforeRestore(Integer.parseInt(parser.getAttributeValue(0)));

					Log.i(TAG,
							"getAttributeValue = "
									+ parser.getAttributeValue(0));

				} else if ("sms".equals(parser.getName())) {
					values = new ContentValues();
					Log.i(TAG, "sms = " + parser.getName());

				} else if ("body".equals(parser.getName())) {
					 values.put("body", parser.getText());
					Log.i(TAG, "getText = " + parser.nextText());
				} else if ("address".equals(parser.getName())) {
					 values.put("address", parser.getText());
					Log.i(TAG, "getText = " + parser.nextText());
				} else if ("type".equals(parser.getName())) {
					 values.put("type", parser.getText());
					Log.i(TAG, "getText = " + parser.nextText());
				} else if ("date".equals(parser.getName())) {
					 values.put("date", parser.getText());
					Log.i(TAG, "getText = " + parser.nextText());
				}
				break;

			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					currentcount++;
					callBack.onRestore(currentcount);
					Log.i(TAG, "currentcount = " + currentcount);
					resolver.insert(uri, values);
					values = null;
				}
				break;

			default:
				break;
			}

			type = parser.next();
		}

		// 4.把短信插入系统的短信应用
		//
		// ContentValues values = new ContentValues();
		// values.put("body", "body");
		// values.put("address", "112");
		// values.put("type", "1");
		// values.put("date", "1395045035573");
		// resolver.insert(uri, values);
		//

	}

}
