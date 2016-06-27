package com.lzh.MobileSafe.test;

import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.lzh.MobileSafe.db.BlackNumberDBOpenHelper;
import com.lzh.MobileSafe.db.dao.BlackNumberDao;
import com.lzh.MobileSafe.db.domain.BlackNumberInfo;

public class TestBlackNumberDB extends AndroidTestCase {
	public void testCreateDB() throws Exception{
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
		
	}
	public void add() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long basenumber = 1350000000;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(basenumber+i),String.valueOf(random.nextInt(3)+1));
			
		}
		
	}
	public void findAll() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> info = dao.findAll();
	    for (BlackNumberInfo blackNumberInfo : info) {
	    	System.out.println(blackNumberInfo.toString());
			
		}
	
	}
	public void delete() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("123");
		
	}
	public void update() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("123", "3");
		
	}
	public void find() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		String result = dao.findMode("123");
//		assertEquals(true, result);
	}

}
