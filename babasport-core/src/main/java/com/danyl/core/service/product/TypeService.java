package com.danyl.core.service.product;

import com.danyl.core.bean.product.Type;
import com.danyl.core.bean.product.TypeQuery;

import java.util.List;

public interface TypeService {
    public List<Type> selectTypeListByQuery(TypeQuery typeQuery);
}