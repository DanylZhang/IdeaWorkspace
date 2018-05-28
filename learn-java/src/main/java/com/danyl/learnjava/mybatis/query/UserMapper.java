package com.danyl.learnjava.mybatis.query;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-6-22.
 */
public interface UserMapper {
    User get(Long id);

    List<User> list();

    void delete(Long id);

    void update(User user);

    @Insert({"INSERT INTO user(name,email,age,bornDate)", "VALUES (#{name},#{email},#{age},#{bornDate})"})
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    void add(User user);

    void batchAdd(List<User> users);

    int queryForCount(UserQueryObject qo);

    List<User> query(UserQueryObject qo);

    User login(Map<String, Object> root);

    User login2(@Param("username") String username, @Param("email") String email);
}
