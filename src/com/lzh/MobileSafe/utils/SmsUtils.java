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
 * ���Ź�����
 * 
 * @author Administrator
 * 
 */
public class SmsUtils {
	private static final String TAG = "SmsUtils";

	/**
	 * ���ݶ��Żص��ӿ�
	 * 
	 * @author Administrator
	 * 
	 */
	public interface SmsBackupCallBack {
		/**
		 * ��ʼ����ǰ ���ý������ֵ
		 * 
		 * @param max
		 *            �ܽ���
		 */
		public void beforeBackup(int max);

		/**
		 * ���ݹ����У����ӽ���
		 * 
		 * @param process
		 *            ��ǰ����
		 */
		public void onSmsBackup(int process);

	}
	/**
	 * ��ԭ���Żص��ӿ�
	 * @author Administrator
	 *
	 */
	
	public interface SmsRestoreCallBack{
		public void beforeRestore(int max);
		public void onRestore(int process);
	}
	


	/**
	 * �����û�����
	 * 
	 * @param context
	 *            ������
	 * @param SmsBackupCallBack
	 *            ���ݶ��ŵĻص��ӿ�
	 * @throws Exception
	 */
	public static void backupSms(Context context, SmsBackupCallBack callBack)
			throws Exception {
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// ���û�����һ��һ��������������һ����ʽд���ļ���
		XmlSerializer serializer = Xml.newSerializer();// ��ȡxml�ļ���������ϵ�л�����
		// ��ʼ��������
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");

		Uri uri = Uri.parse("content://sms/");// null ���ж���
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		// ���ݿ�ʼ ���ý��������ֵ
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
			// ���ݹ����У����ӽ���
			process++;
			// pb.setProgress(process);
			callBack.onSmsBackup(process);

		}
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();

	}

	/**
	 * ��ԭ����
	 * 
	 * @param context
	 * @param flag
	 *            �Ƿ�����ԭ������
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

		// 1.��ȡsd���ϵ�xml�ļ�
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		// File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		ContentValues values = null;
		// 2.��ȡmax

		// 3.��ȡÿһ��������Ϣ body address type date
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

		// 4.�Ѷ��Ų���ϵͳ�Ķ���Ӧ��
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
