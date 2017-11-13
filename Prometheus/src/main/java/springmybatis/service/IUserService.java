package springmybatis.service;

import springmybatis.domain.User;

import java.util.List;

/**
 * Created by Administrator on 2017-6-23.
 */
public interface IUserService {
    User get(Long id);

    List<User> list();

    void delete(Long id);

    void update(User user);

    void add(User user);

    void batchAdd(List<User> users);
}