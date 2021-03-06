/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.vip;


import com.danyl.spiders.jooq.gen.vip.tables.Ads;
import com.danyl.spiders.jooq.gen.vip.tables.AdsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class NewVip extends SchemaImpl {

    private static final long serialVersionUID = -1665032113;

    /**
     * The reference instance of <code>new_vip</code>
     */
    public static final NewVip NEW_VIP = new NewVip();

    /**
     * The table <code>new_vip.ads</code>.
     */
    public final Ads ADS = com.danyl.spiders.jooq.gen.vip.tables.Ads.ADS;

    /**
     * The table <code>new_vip.ads_activity</code>.
     */
    public final AdsActivity ADS_ACTIVITY = com.danyl.spiders.jooq.gen.vip.tables.AdsActivity.ADS_ACTIVITY;

    /**
     * No further instances allowed
     */
    private NewVip() {
        super("new_vip", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Ads.ADS,
            AdsActivity.ADS_ACTIVITY);
    }
}
