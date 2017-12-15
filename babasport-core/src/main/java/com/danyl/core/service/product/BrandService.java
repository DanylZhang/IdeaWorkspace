package com.danyl.core.service.product;

import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;

import java.util.List;

public interface BrandService {
    public List<Brand> selectBrandList(BrandQuery brandQuery);
}