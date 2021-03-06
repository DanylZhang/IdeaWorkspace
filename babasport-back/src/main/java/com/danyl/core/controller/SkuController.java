package com.danyl.core.controller;

import com.danyl.core.bean.product.Sku;
import com.danyl.core.service.product.SkuService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 库存管理
 * 列表
 * 修改
 */
@Controller
@RequestMapping(value = "/control/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;

    // 库存列表页面
    @RequestMapping(value = "list.html")
    public String list(Integer productId, Model model) {
        List<Sku> skus = skuService.selectSkuListByProductId(productId);
        model.addAttribute("skus", skus);
        return "sku/list";
    }

    // 库存修改
    @RequestMapping(value = "edit.html")
    public void edit(Sku sku, HttpServletResponse response) throws IOException {
        skuService.updateSkuById(sku);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("msg","保存成功" );
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());
    }
}