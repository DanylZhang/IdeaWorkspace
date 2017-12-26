package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;
import com.danyl.core.dao.product.BrandDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;

    @Transactional(readOnly = true)
    public List<Brand> selectBrandList(BrandQuery brandQuery){
        return brandDao.selectBrandList(brandQuery);
    }

    //构建分页对象
    public Pagination selectPaginationByQuery(BrandQuery brandQuery){
        Pagination pagination = new Pagination(
                brandQuery.getPageNo(),
                brandQuery.getPageSize(),
                brandDao.countBrand(brandQuery));
        //设置结果集
        pagination.setList(brandDao.selectBrandList(brandQuery));
        return pagination;
    }
}