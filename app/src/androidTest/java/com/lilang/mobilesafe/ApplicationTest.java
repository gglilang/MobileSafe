package com.lilang.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.lilang.mobilesafe.db.BlackNumberDBOpenHelper;
import com.lilang.mobilesafe.db.dao.BlackNumberDao;
import com.lilang.mobilesafe.domain.BlackNumberInfo;
import com.lilang.mobilesafe.domain.TaskInfo;
import com.lilang.mobilesafe.engine.TaskInfoProvider;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testCreateDB() throws Exception{
        BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        long basenumber = 1340000000;
        Random random = new Random();
        for(int i = 0; i < 100; i++) {
            dao.add(String.valueOf(basenumber + i), String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testFindAll() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        List<BlackNumberInfo> infos = dao.findAll();
        for(BlackNumberInfo info : infos){
            System.out.println(info.toString());
        }
    }
    public void testDelete() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.delete("1340000001");
    }
    public void testUpdate() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.update("110", "2");
    }
    public void testFind() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        boolean result = dao.find("110");
        assertEquals(true, result);
    }

    public void testGetTaskInfos() throws Exception{
        List<TaskInfo> infos = TaskInfoProvider.getTaskInfos(getContext());
        for(TaskInfo info : infos){
            System.out.println(info.toString());
        }
    }

}