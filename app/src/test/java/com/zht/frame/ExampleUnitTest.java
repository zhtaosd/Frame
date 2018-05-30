package com.zht.frame;


import com.zht.frame.simplefctory.Api;
import com.zht.frame.simplefctory.ImpA;
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

}