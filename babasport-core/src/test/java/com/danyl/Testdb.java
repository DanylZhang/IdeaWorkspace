package com.danyl;

import com.danyl.core.bean.User;
import com.danyl.core.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class Testdb {
    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testInsert(){
        Arrays.stream(ctx.getBeanDefinitionNames()).parallel().forEach(System.out::println);
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        User user = new User();
        user.setName("光头强");
        user.setAge(33);
        user.setEmail("123123@qq.com");
        user.setBornDate(new Date());
        Set<ConstraintViolation<@Valid User>> validate = validator.validate(user);
        System.out.println(validate);
        userDao.insert(user);
    }
}