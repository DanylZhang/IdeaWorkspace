package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Color;
import com.danyl.core.bean.product.ColorQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ColorDao {
    long countByExample(ColorQuery example);

    int deleteByExample(ColorQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Color record);

    int insertSelective(Color record);

    List<Color> selectByExample(ColorQuery example);

    Color selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Color record, @Param("example") ColorQuery example);

    int updateByExample(@Param("record") Color record, @Param("example") ColorQuery example);

    int updateByPrimaryKeySelective(Color record);

    int updateByPrimaryKey(Color record);
}