package com.danyl.core.service.order;

import com.danyl.core.bean.BuyerCart;
import com.danyl.core.bean.order.Order;

public interface OrderService {
    Long insertOrder(Order order, BuyerCart buyerCart);
}