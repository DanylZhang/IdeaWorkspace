package com.danyl.xunwu.repository;

import com.danyl.xunwu.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String username);
}