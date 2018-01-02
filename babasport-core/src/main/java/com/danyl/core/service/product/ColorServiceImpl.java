package com.danyl.core.service.product;

import com.danyl.core.bean.product.Color;
import com.danyl.core.bean.product.ColorQuery;
import com.danyl.core.dao.product.ColorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {
    @Autowired
    private ColorDao colorDao;

    public List<Color> selectColorsByQuery(ColorQuery colorQuery){
        return colorDao.selectByExample(colorQuery);
    }
}