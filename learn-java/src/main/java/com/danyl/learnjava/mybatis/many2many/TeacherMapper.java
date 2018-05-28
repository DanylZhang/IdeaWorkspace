package com.danyl.learnjava.mybatis.many2many;

import org.apache.ibatis.annotations.Param;

public interface TeacherMapper {
    void add(Teacher teacher);
    Teacher get(Long id);
    void resovleRelation(@Param("teaId") Long teaId, @Param("stuId") Long stuId);
}