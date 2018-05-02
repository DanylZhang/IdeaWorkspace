package com.danyl.springbootsell.service;

import com.danyl.springbootsell.dto.OrderDTO;
import org.springframework.util.StringUtils;

public interface BuyerService {

    OrderDTO findOrderOne(String openid, String orderId);

    OrderDTO cancelOrder(String openid,String orderId);
}
