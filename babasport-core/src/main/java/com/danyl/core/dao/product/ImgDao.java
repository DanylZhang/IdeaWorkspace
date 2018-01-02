package com.danyl.core.dao.product;

import com.danyl.core.bean.product.Img;
import com.danyl.core.bean.product.ImgQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ImgDao {
    long countByExample(ImgQuery example);

    int deleteByExample(ImgQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Img record);

    int insertSelective(Img record);

    List<Img> selectByExample(ImgQuery example);

    Img selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Img record, @Param("example") ImgQuery example);

    int updateByExample(@Param("record") Img record, @Param("example") ImgQuery example);

    int updateByPrimaryKeySelective(Img record);

    int updateByPrimaryKey(Img record);
}