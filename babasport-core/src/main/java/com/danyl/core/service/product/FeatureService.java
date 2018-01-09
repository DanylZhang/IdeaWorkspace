package com.danyl.core.service.product;

import com.danyl.core.bean.product.Feature;
import com.danyl.core.bean.product.FeatureQuery;

import java.util.List;

public interface FeatureService {
    public List<Feature> selectByExample(FeatureQuery featureQuery);
}