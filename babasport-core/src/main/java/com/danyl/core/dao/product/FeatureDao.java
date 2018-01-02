package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Feature;
import com.danyl.core.bean.product.FeatureQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeatureDao {
    long countByExample(FeatureQuery example);

    int deleteByExample(FeatureQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Feature record);

    int insertSelective(Feature record);

    List<Feature> selectByExample(FeatureQuery example);

    Feature selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Feature record, @Param("example") FeatureQuery example);

    int updateByExample(@Param("record") Feature record, @Param("example") FeatureQuery example);

    int updateByPrimaryKeySelective(Feature record);

    int updateByPrimaryKey(Feature record);
}