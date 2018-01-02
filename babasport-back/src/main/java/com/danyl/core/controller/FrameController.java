package com.danyl.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/control")
public class FrameController {
    // 商品身体
    @RequestMapping(value = "frame/product_main.html")
    public String product_main() {
        return "frame/product_main";
    }

    // 商品左
    @RequestMapping(value = "frame/product_left.html")
    public String product_left() {
        return "frame/product_left";
    }

    // 订单身体
    @RequestMapping(value = "frame/order_main.html")
    public String order_main() {
        return "frame/order_main";
    }

    // 订单左
    @RequestMapping(value = "frame/order_left.html")
    public String order_left() {
        return "frame/order_left";
    }

    // 订单列表页面
    @RequestMapping(value = "order/list.html")
    public String order_list() {
        return "order/list";
    }
}