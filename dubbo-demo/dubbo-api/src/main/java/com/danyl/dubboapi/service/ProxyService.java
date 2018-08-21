package com.danyl.dubboapi.service;

import com.danyl.dubboapi.entity.Proxy;

import java.util.List;

public interface ProxyService {

    String sayHello();

    List<Proxy> findAll();

    List<Proxy> findAllByIP(String ip);
}
