package com.danyl.core.controller;

import com.danyl.common.pagination.Pagination;
import com.danyl.common.util.Utils;
import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;
import com.danyl.core.service.product.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    // 品牌列表页面
    @RequestMapping(value = "list.html")
    public String list(Integer pageNo, String name, Boolean isDisplay, Model model) {
        BrandQuery brandQuery = new BrandQuery();
        //当前页
        brandQuery.setPageNo(Pagination.cpn(pageNo));

        StringBuilder params = new StringBuilder();

        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        if (null != name) {
            criteria.andNameEqualTo(name);
            model.addAttribute("name", name);
            params.append("name=").append(name);
        }
        if (null != isDisplay) {
            criteria.andIsDisplayEqualTo(isDisplay);
            model.addAttribute("isDisplay", isDisplay);
            params.append("&isDisplay=").append(isDisplay);
        } else {
            criteria.andIsDisplayEqualTo(true);
            model.addAttribute("isDisplay", true);
            params.append("&isDisplay=").append(true);
        }

        Pagination pagination = brandService.selectPaginationByQuery(brandQuery);

        //分页展示 <a href="../brand/list.html?name=金&isDisplay=1&pageNo=2"/>
        String url = "../brand/list.html";
        pagination.pageView(url, params.toString());

        model.addAttribute("pagination", pagination);
        return "brand/list";
    }

    //添加品牌
    @RequestMapping(value = "add.html")
    public String add(Brand brand, HttpServletRequest request) {
        if (request.getMethod().equals("GET")) {
            return "brand/add";
        }

        brandService.insertBrand(brand);
        return "redirect:/control/brand/list.html";
    }

    //去修改页面
    @GetMapping(value = "edit.html")
    public String getEdit(Long id, Model model) {
        Brand brand = brandService.selectBrandById(id);
        model.addAttribute("brand", brand);
        return "brand/edit";
    }

    //修改
    @PostMapping(value = "edit.html")
    public String postEdit(Brand brand, Model model) {
        brandService.updateBrandById(brand);
        return "redirect:/control/brand/list.html";
    }

    //删除
    @RequestMapping(value = "delete.html")
    public String delete(Integer[] ids, String name, Boolean isDisplay, Integer pageNo, Model model) {
        Utils.var_dump(ids);
        brandService.deleteBrands(ids);

        if (null != name) {
            model.addAttribute("name", name);
        }
        if (null != isDisplay) {
            model.addAttribute("isDisplay", isDisplay);
        }
        if (null != pageNo) {
            model.addAttribute("pageNo", pageNo);
        }
        return "redirect:/control/brand/list.html";
    }
}