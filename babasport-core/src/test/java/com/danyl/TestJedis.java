package com.danyl;

import com.danyl.common.junit.SpringJunitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class TestJedis extends SpringJunitTest {
    @Test
    public void testJedis() {
        Jedis jedis = new Jedis("192.168.1.15", 6379);
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        Long pno = jedis.incr("pno");
        System.out.println(pno);
    }

    @Autowired
    private Jedis jedis;

    @Test
    public void testJedisSpring() {
        Long pno = jedis.incr("pno");
        System.out.println(pno);
    }
}