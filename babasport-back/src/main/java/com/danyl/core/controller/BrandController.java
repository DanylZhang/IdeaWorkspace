package com.danyl.core.controller;

import com.danyl.core.bean.product.Brand;
import com.danyl.core.service.product.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
 */
@Controller
@RequestMapping(value = "/control/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    // 商品身体
    @RequestMapping(value = "list.html")
    public String list(Model model) {
        List<Brand> brands = brandService.selectBrandList();
        model.addAttribute("brands", brands);
        return "brand/list";
    }
}