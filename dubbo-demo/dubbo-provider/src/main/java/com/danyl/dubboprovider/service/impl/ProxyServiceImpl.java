package com.danyl.dubboprovider.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.danyl.dubboapi.entity.Proxy;
import com.danyl.dubboapi.service.ProxyService;
import com.danyl.dubboprovider.repository.ProxyRepository;

import javax.annotation.Resource;
import java.util.List;

@Service(timeout = 5000)
public class ProxyServiceImpl implements ProxyService {

    @Resource
    private ProxyRepository repository;

    public String sayHello() {
        return "hello dubbo";
    }

    public List<Proxy> findAll() {
        return repository.findAll();
    }

    public List<Proxy> findAllByIP(String ip) {
        return repository.findAllByIpLike("%" + ip + "%");
    }
}
