package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Type;
import com.danyl.core.bean.product.TypeQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TypeDao {
    long countByExample(TypeQuery example);

    int deleteByExample(TypeQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Type record);

    int insertSelective(Type record);

    List<Type> selectByExample(TypeQuery example);

    Type selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Type record, @Param("example") TypeQuery example);

    int updateByExample(@Param("record") Type record, @Param("example") TypeQuery example);

    int updateByPrimaryKeySelective(Type record);

    int updateByPrimaryKey(Type record);
}