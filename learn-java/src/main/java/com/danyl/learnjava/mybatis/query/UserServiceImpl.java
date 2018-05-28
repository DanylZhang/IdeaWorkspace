package com.danyl.learnjava.mybatis.query;

import com.danyl.learnjava.mybatis.util.MybatisUtil;

import java.util.List;

/**
 * Created by Administrator on 2017-6-23.
 */
public class UserServiceImpl implements IUserService {
    private UserMapper userMapper;

    public UserServiceImpl() {
        userMapper = MybatisUtil.openSession().getMapper(UserMapper.class);
    }

    public PageResult query(UserQueryObject qo){
        int totalCount=userMapper.queryForCount(qo);
        if (totalCount>0){
            List<User> list = userMapper.query(qo);
            return new PageResult(totalCount,qo.getPageSize(),qo.getCurrentPage(),list);
        }
        return new PageResult().empty(qo.getPageSize());
    }
}