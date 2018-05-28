package com.danyl.learnjava.springmybatis.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.danyl.learnjava.springmybatis.domain.User;
import com.danyl.learnjava.springmybatis.service.IUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/UserServiceTest-context.xml")
public class UserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    public void testSave() {
        User user = new User();
        user.setName("spring-mybatis");
        userService.add(user);
    }
}