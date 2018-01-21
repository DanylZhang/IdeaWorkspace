package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.Product;
import com.danyl.core.bean.product.ProductQuery;

public interface ProductService{
    Pagination selectPaginationByQuery(ProductQuery productQuery);

    void insertProduct(Product product);

    void deleteProductByIds(Integer[] ids);

    void onSale(Integer[] ids);

    Product selectProductById(Integer id);

    void updateByProduct(Product product);
}