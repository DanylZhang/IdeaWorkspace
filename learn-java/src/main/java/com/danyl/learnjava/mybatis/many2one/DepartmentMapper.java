package com.danyl.learnjava.mybatis.many2one;

/**
 * Created by Administrator on 2017-6-22.
 */
public interface DepartmentMapper {
    Department get(Long id);

    void add(Department department);
}