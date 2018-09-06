/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.proxy;


import com.danyl.spiders.jooq.gen.proxy.tables.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;

import javax.annotation.Generated;

import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>PUBLIC</code> schema.
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

    public static final UniqueKey<ProxyRecord> CONSTRAINT_48 = UniqueKeys0.CONSTRAINT_48;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<ProxyRecord> CONSTRAINT_48 = Internal.createUniqueKey(Proxy.PROXY, "CONSTRAINT_48", Proxy.PROXY.IP, Proxy.PROXY.PORT);
    }
}
