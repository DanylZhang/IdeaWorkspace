package com.danyl.xunwu.service.impl;

import com.danyl.xunwu.entity.Role;
import com.danyl.xunwu.entity.User;
import com.danyl.xunwu.repository.RoleRepository;
import com.danyl.xunwu.repository.UserRepository;
import com.danyl.xunwu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User findUserByName(String username) {
        User user = userRepository.findByName(username);
        if (user == null) {
            return null;
        }
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorityList = new ArrayList<>();
        roles.forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorityList(authorityList);
        return user;
    }
}