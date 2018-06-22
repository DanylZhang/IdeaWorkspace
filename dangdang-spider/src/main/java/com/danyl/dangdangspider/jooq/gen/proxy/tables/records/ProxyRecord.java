/*
 * This file is generated by jOOQ.
*/
package com.danyl.dangdangspider.jooq.gen.proxy.tables.records;


import com.danyl.dangdangspider.jooq.gen.proxy.tables.Proxy;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


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
public class ProxyRecord extends UpdatableRecordImpl<ProxyRecord> implements Record5<String, Integer, Boolean, String, String> {

    private static final long serialVersionUID = 976582505;

    /**
     * Setter for <code>proxy.ip</code>.
     */
    public ProxyRecord setIp(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>proxy.ip</code>.
     */
    public String getIp() {
        return (String) get(0);
    }

    /**
     * Setter for <code>proxy.port</code>.
     */
    public ProxyRecord setPort(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>proxy.port</code>.
     */
    public Integer getPort() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>proxy.is_valid</code>.
     */
    public ProxyRecord setIsValid(Boolean value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>proxy.is_valid</code>.
     */
    public Boolean getIsValid() {
        return (Boolean) get(2);
    }

    /**
     * Setter for <code>proxy.type</code>.
     */
    public ProxyRecord setType(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>proxy.type</code>.
     */
    public String getType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>proxy.comment</code>.
     */
    public ProxyRecord setComment(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>proxy.comment</code>.
     */
    public String getComment() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, Integer, Boolean, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, Integer, Boolean, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Proxy.PROXY.IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Proxy.PROXY.PORT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field3() {
        return Proxy.PROXY.IS_VALID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Proxy.PROXY.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Proxy.PROXY.COMMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component3() {
        return getIsValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getComment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value3() {
        return getIsValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getComment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value1(String value) {
        setIp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value2(Integer value) {
        setPort(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value3(Boolean value) {
        setIsValid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value4(String value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value5(String value) {
        setComment(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord values(String value1, Integer value2, Boolean value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ProxyRecord
     */
    public ProxyRecord() {
        super(Proxy.PROXY);
    }

    /**
     * Create a detached, initialised ProxyRecord
     */
    public ProxyRecord(String ip, Integer port, Boolean isValid, String type, String comment) {
        super(Proxy.PROXY);

        set(0, ip);
        set(1, port);
        set(2, isValid);
        set(3, type);
        set(4, comment);
    }
}
