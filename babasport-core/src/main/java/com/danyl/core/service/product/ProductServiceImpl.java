package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.ProductQuery;
import com.danyl.core.dao.product.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Override
    public Pagination selectPaginationByQuery(ProductQuery productQuery) {
        Pagination pagination = new Pagination(productQuery.getPageNo(), productQuery.getPageSize(), (int) productDao.countByExample(productQuery));
        pagination.setList(productDao.selectByExample(productQuery));
        return pagination;
    }
}