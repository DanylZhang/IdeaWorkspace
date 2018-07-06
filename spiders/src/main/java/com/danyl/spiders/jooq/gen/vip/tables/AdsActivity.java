/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.vip.tables;


import com.danyl.spiders.jooq.gen.vip.Indexes;
import com.danyl.spiders.jooq.gen.vip.Keys;
import com.danyl.spiders.jooq.gen.vip.NewVip;
import com.danyl.spiders.jooq.gen.vip.tables.records.AdsActivityRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Index;
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
public class AdsActivity extends TableImpl<AdsActivityRecord> {

    private static final long serialVersionUID = 309586589;

    /**
     * The reference instance of <code>new_vip.ads_activity</code>
     */
    public static final AdsActivity ADS_ACTIVITY = new AdsActivity();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AdsActivityRecord> getRecordType() {
        return AdsActivityRecord.class;
    }

    /**
     * The column <code>new_vip.ads_activity.id</code>.
     */
    public final TableField<AdsActivityRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>new_vip.ads_activity.name</code>.
     */
    public final TableField<AdsActivityRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>new_vip.ads_activity.url</code>.
     */
    public final TableField<AdsActivityRecord, String> URL = createField("url", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>new_vip.ads_activity.type</code>.
     */
    public final TableField<AdsActivityRecord, Integer> TYPE = createField("type", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>new_vip.ads_activity.last_update_time</code>.
     */
    public final TableField<AdsActivityRecord, LocalDateTime> LAST_UPDATE_TIME = createField("last_update_time", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * Create a <code>new_vip.ads_activity</code> table reference
     */
    public AdsActivity() {
        this(DSL.name("ads_activity"), null);
    }

    /**
     * Create an aliased <code>new_vip.ads_activity</code> table reference
     */
    public AdsActivity(String alias) {
        this(DSL.name(alias), ADS_ACTIVITY);
    }

    /**
     * Create an aliased <code>new_vip.ads_activity</code> table reference
     */
    public AdsActivity(Name alias) {
        this(alias, ADS_ACTIVITY);
    }

    private AdsActivity(Name alias, Table<AdsActivityRecord> aliased) {
        this(alias, aliased, null);
    }

    private AdsActivity(Name alias, Table<AdsActivityRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return NewVip.NEW_VIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ADS_ACTIVITY_IN_URL, Indexes.ADS_ACTIVITY_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<AdsActivityRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ADS_ACTIVITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AdsActivityRecord> getPrimaryKey() {
        return Keys.KEY_ADS_ACTIVITY_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AdsActivityRecord>> getKeys() {
        return Arrays.<UniqueKey<AdsActivityRecord>>asList(Keys.KEY_ADS_ACTIVITY_PRIMARY, Keys.KEY_ADS_ACTIVITY_IN_URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsActivity as(String alias) {
        return new AdsActivity(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsActivity as(Name alias) {
        return new AdsActivity(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AdsActivity rename(String name) {
        return new AdsActivity(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AdsActivity rename(Name name) {
        return new AdsActivity(name, null);
    }
}
