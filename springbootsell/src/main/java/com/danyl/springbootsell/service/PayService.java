package com.danyl.springbootsell.service;

import com.danyl.springbootsell.dto.OrderDTO;

public interface PayService {

    void create(OrderDTO orderDTO);
}
