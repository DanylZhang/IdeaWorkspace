/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.dangdang;


import com.danyl.spiders.jooq.gen.dangdang.tables.ItemCategory;

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
public class Dangdang extends SchemaImpl {

    private static final long serialVersionUID = -882586067;

    /**
     * The reference instance of <code>dangdang</code>
     */
    public static final Dangdang DANGDANG = new Dangdang();

    /**
     * The table <code>dangdang.item_category</code>.
     */
    public final ItemCategory ITEM_CATEGORY = com.danyl.spiders.jooq.gen.dangdang.tables.ItemCategory.ITEM_CATEGORY;

    /**
     * No further instances allowed
     */
    private Dangdang() {
        super("dangdang", null);
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
            ItemCategory.ITEM_CATEGORY);
    }
}