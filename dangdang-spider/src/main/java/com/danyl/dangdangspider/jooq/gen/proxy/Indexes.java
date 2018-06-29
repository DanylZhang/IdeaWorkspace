/*
 * This file is generated by jOOQ.
*/
package com.danyl.dangdangspider.jooq.gen.proxy;


import com.danyl.dangdangspider.jooq.gen.proxy.tables.Proxy;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index SQLITE_AUTOINDEX_PROXY_1 = Indexes0.SQLITE_AUTOINDEX_PROXY_1;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index SQLITE_AUTOINDEX_PROXY_1 = Internal.createIndex("sqlite_autoindex_proxy_1", Proxy.PROXY, new OrderField[] { Proxy.PROXY.IP, Proxy.PROXY.PORT }, true);
    }
}
