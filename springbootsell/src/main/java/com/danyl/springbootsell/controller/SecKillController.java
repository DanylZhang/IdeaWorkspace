package com.danyl.springbootsell.controller;

import com.danyl.springbootsell.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skill")
@Slf4j
public class SecKillController {

    @Autowired
    private SecKillService seckillService;

    /**
     * 查询秒杀活动特价商品的信息
     *
     * @param productId
     * @return
     */
    @GetMapping("/query/{productId}")
    public String query(@PathVariable String productId) throws Exception {
        log.info("@skill query request,productId={}", productId);

        return seckillService.querySecKillProductInfo(productId);
    }

    /**
     * 秒杀，没有抢到返回"很抱歉,****",抢到了会返回剩余的库存量
     *
     * @param productId
     * @return
     * @throws Exception
     */
    @GetMapping("/order/{productId}")
    public String skill(@PathVariable String productId) throws Exception {
        log.info("@skill order request,productId={}", productId);
        seckillService.orderProductMockDiffUser(productId);
        return seckillService.querySecKillProductInfo(productId);
    }
}