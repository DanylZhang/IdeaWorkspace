package com.danyl.core.service.product;

import com.danyl.core.bean.product.Sku;

import java.util.List;

public interface SkuService {
    List<Sku> selectSkuListByProductId(Integer productId);

    List<Sku> selectSkuListByProductIdWithStock(Integer productId);

    void updateSkuById(Sku sku);
}