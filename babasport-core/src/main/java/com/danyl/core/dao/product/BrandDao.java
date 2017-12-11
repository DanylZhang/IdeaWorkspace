package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Brand;

import java.util.List;

public interface BrandDao {
    public List<Brand> selectBrandList();
}