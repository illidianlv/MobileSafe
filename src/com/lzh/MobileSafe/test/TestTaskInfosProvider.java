package com.lzh.MobileSafe.test;

import java.util.List;

import com.lzh.MobileSafe.domain.TaskInfo;
import com.lzh.MobileSafe.engine.TaskInfoProvider;

import android.test.AndroidTestCase;

public class TestTaskInfosProvider extends AndroidTestCase {
	public void testGetTaskInfos() throws Exception{
		List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfos(getContext());
		for (TaskInfo taskInfo : taskInfos) {
			System.out.println(taskInfo.toString());
		}
		
	}

}
