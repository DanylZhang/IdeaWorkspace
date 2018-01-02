package com.danyl.core.dao.product;

import com.danyl.core.bean.product.City;
import com.danyl.core.bean.product.CityQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CityDao {
    long countByExample(CityQuery example);

    int deleteByExample(CityQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(City record);

    int insertSelective(City record);

    List<City> selectByExample(CityQuery example);

    City selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") City record, @Param("example") CityQuery example);

    int updateByExample(@Param("record") City record, @Param("example") CityQuery example);

    int updateByPrimaryKeySelective(City record);

    int updateByPrimaryKey(City record);
}