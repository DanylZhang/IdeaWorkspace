package com.danyl.shiro.service.impl;

import com.danyl.shiro.model.Permission;
import com.danyl.shiro.model.Role;
import com.danyl.shiro.model.User;
import com.danyl.shiro.service.UserService;
import org.apache.shiro.authz.permission.InvalidPermissionStringException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.danyl.shiro.jooq.gen.shiro.Tables.*;

@Service
public class UserServiceImpl implements UserService {

    @Resource(name = "DSLContextShiro")
    private DSLContext create;

    @Override
    public User findByUsername(String username) {
        Result<Record> records = create.select()
                .from(USER)
                .join(USER_ROLE)
                .on(USER.UID.eq(USER_ROLE.UID))
                .join(ROLE)
                .on(USER_ROLE.RID.eq(ROLE.RID))
                .join(PERMISSION_ROLE)
                .on(PERMISSION_ROLE.RID.eq(ROLE.RID))
                .join(PERMISSION)
                .on(PERMISSION_ROLE.PID.eq(PERMISSION.PID))
                .where(USER.USERNAME.eq(username))
                .fetch();

        // Produces Map<User, List<Role>> with User.roles being empty
        HashMap<User, HashMap<Role, Set<Permission>>> map = records
                .stream()
                .collect(Collectors.groupingBy(
                        (record) -> {
                            User user = new User();
                            user.setUid(record.getValue(USER.UID));
                            user.setUsername(record.getValue(USER.USERNAME));
                            user.setPassword(record.getValue(USER.PASSWORD));
                            return user;
                        }
                        , HashMap::new
                        , Collectors.groupingBy(
                                (record) -> {
                                    Role role = new Role();
                                    role.setRid(record.getValue(ROLE.RID));
                                    role.setName(record.getValue(ROLE.RNAME));
                                    return role;
                                }, HashMap::new,
                                Collectors.mapping((record) -> {
                                    Permission permission = new Permission();
                                    permission.setPid(record.getValue(PERMISSION.PID));
                                    permission.setName(record.getValue(PERMISSION.NAME));
                                    permission.setUrl(record.getValue(PERMISSION.URL));
                                    return permission;
                                }, Collectors.toSet()))));
        // Moves the List<Role> Entry.value into each User.roles key
        if (map.size() == 0) {
            return new User();
        }
        return map.entrySet()
                .stream()
                .map((e) -> {
                    User user1 = e.getKey();
                    HashMap<Role, Set<Permission>> roleSetHashMap = e.getValue();
                    for (Map.Entry<Role, Set<Permission>> roleSetEntry : roleSetHashMap.entrySet()) {
                        Role role = roleSetEntry.getKey();
                        Set<Permission> value1 = roleSetEntry.getValue();
                        role.setPermissions(value1);
                        user1.getRoles().add(role);
                    }
                    return user1;
                })
                .collect(Collectors.toList()).get(0);
    }
}
