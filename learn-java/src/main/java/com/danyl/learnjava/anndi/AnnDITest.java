package com.danyl.learnjava.anndi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017-5-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/AnnDITest-context.xml")
public class AnnDITest {
    @Autowired
    private ApplicationContext ctx;

    @Test
    public void test() {
        SomeBean someBean = ctx.getBean("someBean", SomeBean.class);
        System.out.println(someBean);
    }
}