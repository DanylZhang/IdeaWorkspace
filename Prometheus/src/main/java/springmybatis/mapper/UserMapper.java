package springmybatis.mapper;

import springmybatis.domain.User;

import java.util.List;

/**
 * Created by Administrator on 2017-6-22.
 */
public interface UserMapper {
    User get(Long id);

    List<User> list();

    void delete(Long id);

    void update(User user);

    void add(User user);

    void batchAdd(List<User> users);
}