package com.danyl.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 品牌管理
 * 列表查询
 * 带条件
 * 带分页
 * 去添加页面
 * 添加（提交）
 * 去修改页面
 * 修改（提交）
 * 删除（批量）
 * 删除（单个）
 * */
@Controller
@RequestMapping(value = "/control/brand")
public class BrandController {
    // 商品身体
    @RequestMapping(value = "list.html")
    public String product_main() {
        return "brand/list";
    }
}