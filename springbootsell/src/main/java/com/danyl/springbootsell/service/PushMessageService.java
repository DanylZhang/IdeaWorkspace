package com.danyl.springbootsell.service;

import com.danyl.springbootsell.dto.OrderDTO;

/**
 * 消息推送
 */
public interface PushMessageService {

    void orderStatus(OrderDTO orderDTO);
}
