package com.danyl.core.service.product;

import com.danyl.core.bean.product.Type;
import com.danyl.core.bean.product.TypeQuery;
import com.danyl.core.dao.product.TypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeDao typeDao;

    //查询类型
    public List<Type> selectTypeListByQuery(TypeQuery typeQuery){
        return typeDao.selectByExample(typeQuery);
    }
}