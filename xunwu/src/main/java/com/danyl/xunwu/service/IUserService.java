package com.danyl.xunwu.service;

import com.danyl.xunwu.entity.User;

public interface IUserService {

    User findUserByName(String username);
}