package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Product;
import com.danyl.core.bean.product.ProductQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {
    long countByExample(ProductQuery example);

    int deleteByExample(ProductQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    List<Product> selectByExample(ProductQuery example);

    Product selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Product record, @Param("example") ProductQuery example);

    int updateByExample(@Param("record") Product record, @Param("example") ProductQuery example);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
}