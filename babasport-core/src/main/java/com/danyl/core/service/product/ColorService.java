package com.danyl.core.service.product;

import com.danyl.core.bean.product.Color;
import com.danyl.core.bean.product.ColorQuery;

import java.util.List;

public interface ColorService {
    public List<Color> selectColorsByQuery(ColorQuery colorQuery);
}