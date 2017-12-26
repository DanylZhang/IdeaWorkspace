package com.danyl.core.controller;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;
import com.danyl.core.service.product.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
    public String list(Integer pageNo, String name, Integer isDisplay, Model model) {
        BrandQuery brandQuery = new BrandQuery();
        //当前页
        brandQuery.setPageNo(Pagination.cpn(pageNo));

        StringBuilder params = new StringBuilder();

        if (null != name) {
            brandQuery.setName(name);
            model.addAttribute("name", name);
            params.append("name=").append(name);
        }
        if (null != isDisplay) {
            brandQuery.setIsDisplay(isDisplay);
            model.addAttribute("isDisplay", isDisplay);
            params.append("&isDisplay=").append(isDisplay);
        } else {
            brandQuery.setIsDisplay(1);
            model.addAttribute("isDisplay", 1);
            params.append("&isDisplay=").append(1);
        }

        Pagination pagination = brandService.selectPaginationByQuery(brandQuery);

        //分页展示 <a href="../brand/list.html?name=金&isDisplay=1&pageNo=2"/>
        String url = "../brand/list.html";
        pagination.pageView(url, params.toString());

        model.addAttribute("pagination", pagination);
        return "brand/list";
    }

    //去添加页面
    @RequestMapping(value = "add.html")
    public String add(Brand brand, HttpServletRequest request) {
        if (request.getMethod().matches("get")) {
            return "brand/add";
        }

        return "brand/add";
//        return "redirect:/control/brand/list.html";
    }
}