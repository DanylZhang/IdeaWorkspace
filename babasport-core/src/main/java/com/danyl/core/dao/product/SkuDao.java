package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Sku;
import com.danyl.core.bean.product.SkuQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkuDao {
    long countByExample(SkuQuery example);

    int deleteByExample(SkuQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sku record);

    int insertSelective(Sku record);

    List<Sku> selectByExample(SkuQuery example);

    Sku selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Sku record, @Param("example") SkuQuery example);

    int updateByExample(@Param("record") Sku record, @Param("example") SkuQuery example);

    int updateByPrimaryKeySelective(Sku record);

    int updateByPrimaryKey(Sku record);
}