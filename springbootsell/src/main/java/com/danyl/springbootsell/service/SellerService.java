package com.danyl.springbootsell.service;

import com.danyl.springbootsell.entity.SellerInfo;

public interface SellerService {

    /**
     * 通过卖家openid查询卖家信息
     *
     * @param openid
     * @return
     */
    SellerInfo findSellerInfoByOpenid(String openid);
}
