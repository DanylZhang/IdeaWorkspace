package com.danyl.dubboprovider;

import com.danyl.dubboapi.entity.Proxy;
import com.danyl.dubboprovider.repository.ProxyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DubboProviderApplicationTests {

    @Autowired
    private ProxyRepository repository;

    @Test
    public void test(){
        List<Proxy> all = repository.findAll();
        System.out.println(all);
    }
}