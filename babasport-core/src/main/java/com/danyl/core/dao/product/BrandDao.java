package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;

import java.util.List;

public interface BrandDao {
    //查询所有品牌
    public List<Brand> selectBrandList(BrandQuery brandQuery);

    //查询总条数
    public Integer countBrand(BrandQuery brandQuery);
}