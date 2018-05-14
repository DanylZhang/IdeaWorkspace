package com.danyl.springbootsell.service.impl;

import com.danyl.springbootsell.entity.SellerInfo;
import com.danyl.springbootsell.repository.SellerInfoRepository;
import com.danyl.springbootsell.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerInfoRepository repository;

    @Override
    public SellerInfo findSellerInfoByOpenid(String openid) {
        return repository.findByOpenid(openid);
    }
}
