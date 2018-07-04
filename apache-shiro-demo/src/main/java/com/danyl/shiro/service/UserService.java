package com.danyl.shiro.service;

import com.danyl.shiro.model.User;

public interface UserService {
    User findByUsername(String username);
}
