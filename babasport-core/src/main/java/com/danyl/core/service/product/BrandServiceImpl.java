package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.Brand;
import com.danyl.core.bean.product.BrandQuery;
import com.danyl.core.dao.product.BrandDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;

    @Transactional(readOnly = true)
    public List<Brand> selectByExample(BrandQuery brandQuery) {
        return brandDao.selectByExample(brandQuery);
    }

    //构建分页对象
    public Pagination selectPaginationByQuery(BrandQuery brandQuery) {
        Pagination pagination = new Pagination(
                brandQuery.getPageNo(),
                brandQuery.getPageSize(),
                (int) brandDao.countByExample(brandQuery));
        //设置结果集
        pagination.setList(brandDao.selectByExample(brandQuery));
        return pagination;
    }

    @Override
    public void insertBrand(Brand brand) {
        brandDao.insert(brand);
    }

    @Override
    public void deleteBrands(Integer[] ids) {
        for (int id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public void updateBrandById(Brand brand) {
        brandDao.updateByPrimaryKey(brand);
    }

    @Override
    public Brand selectBrandById(Long id) {
        return brandDao.selectByPrimaryKey(Integer.valueOf(id.toString()));
    }
}