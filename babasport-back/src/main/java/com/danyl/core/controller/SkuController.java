package com.danyl.core.controller;

import com.danyl.common.pagination.Pagination;
import com.danyl.common.util.Utils;
import com.danyl.core.bean.product.*;
import com.danyl.core.bean.product.ProductQuery.Criteria;
import com.danyl.core.service.product.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 库存管理
 * 列表
 * 添加
 * 库存
 * 上架
 */
@Controller
@RequestMapping(value = "/control/sku")
public class SkuController {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private ColorService colorService;

    // 商品列表页面
    @RequestMapping(value = "list.html")
    public String list() {
        return "sku/list";
    }
}