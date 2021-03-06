/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.proxy.tables.daos;


import com.danyl.spiders.jooq.gen.proxy.tables.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
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
public class ProxyDao extends DAOImpl<ProxyRecord, com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy, Record2<String, Integer>> {

    /**
     * Create a new ProxyDao without any configuration
     */
    public ProxyDao() {
        super(Proxy.PROXY, com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy.class);
    }

    /**
     * Create a new ProxyDao with an attached configuration
     */
    public ProxyDao(Configuration configuration) {
        super(Proxy.PROXY, com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Record2<String, Integer> getId(com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy object) {
        return compositeKeyRecord(object.getIp(), object.getPort());
    }

    /**
     * Fetch records that have <code>IP IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByIp(String... values) {
        return fetch(Proxy.PROXY.IP, values);
    }

    /**
     * Fetch records that have <code>PORT IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByPort(Integer... values) {
        return fetch(Proxy.PROXY.PORT, values);
    }

    /**
     * Fetch records that have <code>IS_VALID IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByIsValid(Boolean... values) {
        return fetch(Proxy.PROXY.IS_VALID, values);
    }

    /**
     * Fetch records that have <code>ANONYMITY IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByAnonymity(String... values) {
        return fetch(Proxy.PROXY.ANONYMITY, values);
    }

    /**
     * Fetch records that have <code>SPEED IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchBySpeed(Integer... values) {
        return fetch(Proxy.PROXY.SPEED, values);
    }

    /**
     * Fetch records that have <code>PROTOCOL IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByProtocol(String... values) {
        return fetch(Proxy.PROXY.PROTOCOL, values);
    }

    /**
     * Fetch records that have <code>CHECKED_TIME IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByCheckedTime(LocalDateTime... values) {
        return fetch(Proxy.PROXY.CHECKED_TIME, values);
    }

    /**
     * Fetch records that have <code>CREATED_TIME IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByCreatedTime(LocalDateTime... values) {
        return fetch(Proxy.PROXY.CREATED_TIME, values);
    }

    /**
     * Fetch records that have <code>SOURCE IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchBySource(String... values) {
        return fetch(Proxy.PROXY.SOURCE, values);
    }

    /**
     * Fetch records that have <code>COUNTRY IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByCountry(String... values) {
        return fetch(Proxy.PROXY.COUNTRY, values);
    }

    /**
     * Fetch records that have <code>CITY IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByCity(String... values) {
        return fetch(Proxy.PROXY.CITY, values);
    }

    /**
     * Fetch records that have <code>REGION IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByRegion(String... values) {
        return fetch(Proxy.PROXY.REGION, values);
    }

    /**
     * Fetch records that have <code>ISP IN (values)</code>
     */
    public List<com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy> fetchByIsp(String... values) {
        return fetch(Proxy.PROXY.ISP, values);
    }
}
