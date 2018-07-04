/*
 * This file is generated by jOOQ.
*/
package com.danyl.shiro.jooq.gen.shiro.tables;


import com.danyl.shiro.jooq.gen.shiro.DefaultSchema;
import com.danyl.shiro.jooq.gen.shiro.Keys;
import com.danyl.shiro.jooq.gen.shiro.tables.records.PermissionRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
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
public class Permission extends TableImpl<PermissionRecord> {

    private static final long serialVersionUID = -1991570516;

    /**
     * The reference instance of <code>permission</code>
     */
    public static final Permission PERMISSION = new Permission();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PermissionRecord> getRecordType() {
        return PermissionRecord.class;
    }

    /**
     * The column <code>permission.pid</code>.
     */
    public final TableField<PermissionRecord, Integer> PID = createField("pid", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>permission.name</code>.
     */
    public final TableField<PermissionRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false).defaultValue(org.jooq.impl.DSL.field("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>permission.url</code>.
     */
    public final TableField<PermissionRecord, String> URL = createField("url", org.jooq.impl.SQLDataType.VARCHAR(512).defaultValue(org.jooq.impl.DSL.field("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * Create a <code>permission</code> table reference
     */
    public Permission() {
        this(DSL.name("permission"), null);
    }

    /**
     * Create an aliased <code>permission</code> table reference
     */
    public Permission(String alias) {
        this(DSL.name(alias), PERMISSION);
    }

    /**
     * Create an aliased <code>permission</code> table reference
     */
    public Permission(Name alias) {
        this(alias, PERMISSION);
    }

    private Permission(Name alias, Table<PermissionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Permission(Name alias, Table<PermissionRecord> aliased, Field<?>[] parameters) {
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
    public Identity<PermissionRecord, Integer> getIdentity() {
        return Keys.IDENTITY_PERMISSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PermissionRecord> getPrimaryKey() {
        return Keys.PK_PERMISSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PermissionRecord>> getKeys() {
        return Arrays.<UniqueKey<PermissionRecord>>asList(Keys.PK_PERMISSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission as(String alias) {
        return new Permission(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission as(Name alias) {
        return new Permission(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Permission rename(String name) {
        return new Permission(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Permission rename(Name name) {
        return new Permission(name, null);
    }
}
