/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.xiaomi;


import com.danyl.spiders.jooq.gen.xiaomi.tables.ItemCategory;
import com.danyl.spiders.jooq.gen.xiaomi.tables.records.ItemCategoryRecord;

import javax.annotation.Generated;

import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>xiaomi</code> schema.
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


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<ItemCategoryRecord> KEY_ITEM_CATEGORY_PRIMARY = UniqueKeys0.KEY_ITEM_CATEGORY_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<ItemCategoryRecord> KEY_ITEM_CATEGORY_PRIMARY = Internal.createUniqueKey(ItemCategory.ITEM_CATEGORY, "KEY_item_category_PRIMARY", ItemCategory.ITEM_CATEGORY.CID);
    }
}
