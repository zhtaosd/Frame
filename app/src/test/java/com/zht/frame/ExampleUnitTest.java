package com.zht.frame;


import com.zht.frame.build.Build;
import com.zht.frame.build.Designer;
import com.zht.frame.build.Room;
import com.zht.frame.build.WorkBuilder;
import com.zht.frame.export.Factory;
import com.zht.frame.export.FileFactory;
import com.zht.frame.simplefctory.Api;
import com.zht.frame.simplefctory.ImpB;
import com.zht.frame.simplefctory.SimpleFactory;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testSimpleFactory() {
        Api  api = SimpleFactory.creat(1);
        api.dobusiness();

        api = SimpleFactory.creatProduct(ImpB.class);
        api.dobusiness();
    }
    @Test
    public void testExport() {
        Factory factory = new FileFactory();
        factory.export("文件工厂打印文件");
    }
    @Test
    public void testBuild() {
        Build build = new WorkBuilder();
        Designer designer = new Designer();
        Room room  = designer.build(build);
        System.out.println(room.getDoor());
        System.out.println(room.getWindow());
    }

}