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
 * 商品管理
 * 列表
 * 添加
 * 库存
 * 上架
 */
@Controller
@RequestMapping(value = "/control/product")
public class ProductController {
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
    public String list(Integer pageNo, String name, Integer brandId, Boolean isShow, Model model) {
        BrandQuery brandQuery = new BrandQuery();
        brandQuery.createCriteria().andIsDisplayEqualTo(true);
        List<Brand> brands = brandService.selectByExample(brandQuery);
        model.addAttribute("brands", brands);

        //创建商品查询对象
        ProductQuery productQuery = new ProductQuery();
        productQuery.setOrderByClause("id desc");
        //分页
        productQuery.setPageNo(Pagination.cpn(pageNo));
        productQuery.setPageSize(3);

        StringBuilder params = new StringBuilder();
        Criteria productQueryCriteria = productQuery.createCriteria();
        if (null != name) {
            productQueryCriteria.andNameLike("%" + name + "%");
            model.addAttribute("name", name);
            params.append("name=").append(name);
        }
        if (null != brandId) {
            productQueryCriteria.andBrandIdEqualTo(brandId);
            model.addAttribute("brandId", brandId);
            params.append("&brandId=").append(brandId);
        }
        if (null != isShow) {
            productQueryCriteria.andIsShowEqualTo(isShow);
            model.addAttribute("isShow", isShow);
            params.append("&isShow=").append(isShow);
        } else {
            productQueryCriteria.andIsShowEqualTo(false);
            model.addAttribute("isShow", false);
            params.append("&isShow=").append(false);
        }
        Pagination pagination = productService.selectPaginationByQuery(productQuery);

        //分页展示 <a href="../product/list.html?name=金&isDisplay=1&pageNo=2"/>
        String url = "../product/list.html";
        pagination.pageView(url, params.toString());

        model.addAttribute("pagination", pagination);
        return "product/list";
    }

    //添加品牌
    @RequestMapping(value = "add.html")
    public String add(Product product, Model model, HttpServletRequest request) {
        TypeQuery typeQuery = new TypeQuery();
        typeQuery.createCriteria().andParentIdNotEqualTo(0);
        List<Type> types = typeService.selectTypeListByQuery(typeQuery);
        model.addAttribute("types", types);

        BrandQuery brandQuery = new BrandQuery();
        brandQuery.createCriteria().andIsDisplayEqualTo(true);
        List<Brand> brands = brandService.selectByExample(brandQuery);
        model.addAttribute("brands", brands);

        FeatureQuery featureQuery = new FeatureQuery();
        featureQuery.createCriteria().andIsDelEqualTo(true);
        List<Feature> features = featureService.selectByExample(featureQuery);
        model.addAttribute("features", features);

        ColorQuery colorQuery = new ColorQuery();
        colorQuery.createCriteria().andParentIdNotEqualTo(0);
        colorQuery.setFields("id,name");
        List<Color> colors = colorService.selectColorsByQuery(colorQuery);
        model.addAttribute("colors", colors);

        Utils.var_dump(request.getParameterMap());

        if (null != product.getName()) {
            productService.insertProduct(product);
        }

        if (request.getMethod().equals("GET")) {
            return "product/add";
        }

        return "redirect:/control/product/list.html";
    }

    //去修改页面
    @GetMapping(value = "edit.html")
    public String getEdit(Long id, Model model) {
        return "brand/edit";
    }

    //修改
    @PostMapping(value = "edit.html")
    public String postEdit(Brand brand, Model model) {
        return "redirect:/control/brand/list.html";
    }

    //删除
    @RequestMapping(value = "delete.html")
    public String delete(Integer[] ids, String name, Integer isDisplay, Integer pageNo, Model model) {

        return "redirect:/control/brand/list.html";
    }
}