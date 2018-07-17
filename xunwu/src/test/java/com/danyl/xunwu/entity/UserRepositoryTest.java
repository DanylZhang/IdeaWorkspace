package com.danyl.xunwu.entity;

import com.danyl.xunwu.ApplicationTests;
import com.danyl.xunwu.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserRepositoryTest extends ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne() {
        System.out.println(this.userRepository.count());
        System.out.println(this.userRepository.findAll());
        Optional<User> byId = this.userRepository.findById(1L);
        Assert.assertEquals("wali",byId.get().getName());
    }
}
