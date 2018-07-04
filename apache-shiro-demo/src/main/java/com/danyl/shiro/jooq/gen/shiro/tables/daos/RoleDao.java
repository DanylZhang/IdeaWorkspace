/*
 * This file is generated by jOOQ.
*/
package com.danyl.shiro.jooq.gen.shiro.tables.daos;


import com.danyl.shiro.jooq.gen.shiro.tables.Role;
import com.danyl.shiro.jooq.gen.shiro.tables.records.RoleRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RoleDao extends DAOImpl<RoleRecord, com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role, Integer> {

    /**
     * Create a new RoleDao without any configuration
     */
    public RoleDao() {
        super(Role.ROLE, com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role.class);
    }

    /**
     * Create a new RoleDao with an attached configuration
     */
    public RoleDao(Configuration configuration) {
        super(Role.ROLE, com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role object) {
        return object.getRid();
    }

    /**
     * Fetch records that have <code>rid IN (values)</code>
     */
    public List<com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role> fetchByRid(Integer... values) {
        return fetch(Role.ROLE.RID, values);
    }

    /**
     * Fetch a unique record that has <code>rid = value</code>
     */
    public com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role fetchOneByRid(Integer value) {
        return fetchOne(Role.ROLE.RID, value);
    }

    /**
     * Fetch records that have <code>rname IN (values)</code>
     */
    public List<com.danyl.shiro.jooq.gen.shiro.tables.pojos.Role> fetchByRname(String... values) {
        return fetch(Role.ROLE.RNAME, values);
    }
}
