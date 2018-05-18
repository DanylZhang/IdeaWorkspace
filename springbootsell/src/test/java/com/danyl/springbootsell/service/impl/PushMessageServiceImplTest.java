package com.danyl.springbootsell.service.impl;

import com.danyl.springbootsell.dto.OrderDTO;
import com.danyl.springbootsell.service.PushMessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PushMessageServiceImplTest {

    @Autowired
    private PushMessageService pushMessageService;

    @Test
    public void orderStatus() {
        OrderDTO orderDTO = new OrderDTO();
        pushMessageService.orderStatus(orderDTO);
    }
}