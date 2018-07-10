/*
 * This file is generated by jOOQ.
*/
package com.danyl.shiro.jooq.gen.shiro.tables;


import com.danyl.shiro.jooq.gen.shiro.DefaultSchema;
import com.danyl.shiro.jooq.gen.shiro.Indexes;
import com.danyl.shiro.jooq.gen.shiro.tables.records.UserRoleRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class UserRole extends TableImpl<UserRoleRecord> {

    private static final long serialVersionUID = -1001846592;

    /**
     * The reference instance of <code>user_role</code>
     */
    public static final UserRole USER_ROLE = new UserRole();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserRoleRecord> getRecordType() {
        return UserRoleRecord.class;
    }

    /**
     * The column <code>user_role.uid</code>.
     */
    public final TableField<UserRoleRecord, Integer> UID = createField("uid", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>user_role.rid</code>.
     */
    public final TableField<UserRoleRecord, Integer> RID = createField("rid", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>user_role</code> table reference
     */
    public UserRole() {
        this(DSL.name("user_role"), null);
    }

    /**
     * Create an aliased <code>user_role</code> table reference
     */
    public UserRole(String alias) {
        this(DSL.name(alias), USER_ROLE);
    }

    /**
     * Create an aliased <code>user_role</code> table reference
     */
    public UserRole(Name alias) {
        this(alias, USER_ROLE);
    }

    private UserRole(Name alias, Table<UserRoleRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserRole(Name alias, Table<UserRoleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IDX_UID, Indexes.IDX_USER_RID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRole as(String alias) {
        return new UserRole(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRole as(Name alias) {
        return new UserRole(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserRole rename(String name) {
        return new UserRole(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserRole rename(Name name) {
        return new UserRole(name, null);
    }
}