package com.danyl.core.service.product;

import com.danyl.core.bean.product.Feature;
import com.danyl.core.bean.product.FeatureQuery;
import com.danyl.core.dao.product.FeatureDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FeatureServiceImpl implements FeatureService {
    @Autowired
    private FeatureDao featureDao;

    @Override
    public List<Feature> selectByExample(FeatureQuery featureQuery) {
        List<Feature> features = featureDao.selectByExample(featureQuery);
        return features;
    }
}