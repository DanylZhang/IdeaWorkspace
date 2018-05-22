package com.danyl.springbootsell.service;

public interface SecKillService {

    /**
     * 查询特价商品
     *
     * @param productId
     * @return
     */
    String querySecKillProductInfo(String productId);

    /**
     * 秒杀的逻辑方法
     *
     * @param productId
     */
    void orderProductMockDiffUser(String productId);
}