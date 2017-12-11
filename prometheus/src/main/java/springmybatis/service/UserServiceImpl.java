package springmybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springmybatis.domain.User;
import springmybatis.mapper.UserMapper;

import java.util.List;

/**
 * Created by Administrator on 2017-6-23.
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User get(Long id) {
        return userMapper.get(id);
    }

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public void delete(Long id) {
        userMapper.delete(id);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void add(User user) {
        userMapper.add(user);
    }

    @Override
    public void batchAdd(List<User> users) {
        userMapper.batchAdd(users);
    }
}