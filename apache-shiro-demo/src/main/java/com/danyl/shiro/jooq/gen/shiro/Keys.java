/*
 * This file is generated by jOOQ.
*/
package com.danyl.shiro.jooq.gen.shiro;


import com.danyl.shiro.jooq.gen.shiro.tables.Permission;
import com.danyl.shiro.jooq.gen.shiro.tables.Role;
import com.danyl.shiro.jooq.gen.shiro.tables.User;
import com.danyl.shiro.jooq.gen.shiro.tables.records.PermissionRecord;
import com.danyl.shiro.jooq.gen.shiro.tables.records.RoleRecord;
import com.danyl.shiro.jooq.gen.shiro.tables.records.UserRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<PermissionRecord, Integer> IDENTITY_PERMISSION = Identities0.IDENTITY_PERMISSION;
    public static final Identity<RoleRecord, Integer> IDENTITY_ROLE = Identities0.IDENTITY_ROLE;
    public static final Identity<UserRecord, Integer> IDENTITY_USER = Identities0.IDENTITY_USER;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<PermissionRecord> PK_PERMISSION = UniqueKeys0.PK_PERMISSION;
    public static final UniqueKey<RoleRecord> PK_ROLE = UniqueKeys0.PK_ROLE;
    public static final UniqueKey<UserRecord> PK_USER = UniqueKeys0.PK_USER;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<PermissionRecord, Integer> IDENTITY_PERMISSION = Internal.createIdentity(Permission.PERMISSION, Permission.PERMISSION.PID);
        public static Identity<RoleRecord, Integer> IDENTITY_ROLE = Internal.createIdentity(Role.ROLE, Role.ROLE.RID);
        public static Identity<UserRecord, Integer> IDENTITY_USER = Internal.createIdentity(User.USER, User.USER.UID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<PermissionRecord> PK_PERMISSION = Internal.createUniqueKey(Permission.PERMISSION, "pk_permission", Permission.PERMISSION.PID);
        public static final UniqueKey<RoleRecord> PK_ROLE = Internal.createUniqueKey(Role.ROLE, "pk_role", Role.ROLE.RID);
        public static final UniqueKey<UserRecord> PK_USER = Internal.createUniqueKey(User.USER, "pk_user", User.USER.UID);
    }
}