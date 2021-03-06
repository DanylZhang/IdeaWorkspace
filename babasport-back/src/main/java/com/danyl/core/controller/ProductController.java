package com.danyl.core.controller;

import com.danyl.common.pagination.Pagination;
import com.danyl.common.util.Utils;
import com.danyl.core.bean.product.*;
import com.danyl.core.bean.product.ProductQuery.Criteria;
import com.danyl.core.service.product.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.rmi.CORBA.Util;
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
        productQuery.setPageSize(5);

        StringBuilder params = new StringBuilder();
        Criteria productQueryCriteria = productQuery.createCriteria();
        productQueryCriteria.andIsDelEqualTo(true);
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

    //添加商品
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

        if (null != product.getName()) {
            productService.insertProduct(product);
        }

        if (request.getMethod().equals("GET")) {
            return "product/add";
        }

        return "redirect:/control/product/list.html";
    }

    //去修改页面
    @GetMapping(value = "edit/{id}.html")
    public String Edit(@PathVariable("id") Integer id, Model model) {
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

        Product product = productService.selectProductById(id);
        model.addAttribute("product", product);
        return "product/edit";
    }

    //修改
    @PostMapping(value = "edit/{id}.html")
    public String Edit(@PathVariable("id") Integer id, Product product) {
        product.setId(id);
        productService.updateByProduct(product);
        return "redirect:/control/product/list.html";
    }

    //删除
    @RequestMapping(value = "delete.html")
    public String delete(Integer[] ids, String name, Integer brandId, Integer pageNo, Boolean isShow, Model model) {
        // step 1: delete product by ids
        productService.deleteProductByIds(ids);

        BrandQuery brandQuery = new BrandQuery();
        brandQuery.createCriteria().andIsDisplayEqualTo(true);
        List<Brand> brands = brandService.selectByExample(brandQuery);
        model.addAttribute("brands", brands);

        //创建商品查询对象
        ProductQuery productQuery = new ProductQuery();
        productQuery.setOrderByClause("id desc");
        //分页
        productQuery.setPageNo(Pagination.cpn(pageNo));
        productQuery.setPageSize(5);

        StringBuilder params = new StringBuilder();
        Criteria productQueryCriteria = productQuery.createCriteria();
        productQueryCriteria.andIsDelEqualTo(true);
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
        if (null == isShow) {
            productQueryCriteria.andIsShowEqualTo(false);
            model.addAttribute("isShow", false);
            params.append("&isShow=").append(false);
        } else {
            productQueryCriteria.andIsShowEqualTo(isShow);
            model.addAttribute("isShow", isShow);
            params.append("&isShow=").append(isShow);
        }
        Pagination pagination = productService.selectPaginationByQuery(productQuery);

        //分页展示 <a href="../product/list.html?name=金&isDisplay=1&pageNo=2"/>
        String url = "../product/list.html";
        pagination.pageView(url, params.toString());

        model.addAttribute("pagination", pagination);

        return "redirect:/control/product/list.html";
    }

    //上架
    @RequestMapping(value = "onSale.html")
    public String onSale(Integer[] ids) {
        productService.onSale(ids);
        return "redirect:/control/product/list.html";
    }
}