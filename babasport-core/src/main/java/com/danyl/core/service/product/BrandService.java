package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;

import java.util.List;

public interface BrandService {
    public List<Brand> selectByExample(BrandQuery brandQuery);

    public Pagination selectPaginationByQuery(BrandQuery brandQuery);

    public void insertBrand(Brand brand);

    public void deleteBrands(Integer[] ids);

    public void updateBrandById(Brand brand);

    public Brand selectBrandById(Long id);
}