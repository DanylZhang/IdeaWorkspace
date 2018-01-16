package com.danyl.core.service.product;

import com.danyl.core.bean.product.Sku;

import java.util.List;

public interface SkuService {
    List<Sku> selectSkuListByProductId(Integer productId);

    void updateSkuById(Sku sku);
}