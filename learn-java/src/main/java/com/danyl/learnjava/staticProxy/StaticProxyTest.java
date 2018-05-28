package com.danyl.learnjava.staticProxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017-6-3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/StaticProxyTest-context.xml")
public class StaticProxyTest {
    @Autowired
    @Qualifier("employeeTransaction")
    private IEmployeeService employeeService;
    @Test
    public void test(){
        employeeService.save(new Employee());
    }
}