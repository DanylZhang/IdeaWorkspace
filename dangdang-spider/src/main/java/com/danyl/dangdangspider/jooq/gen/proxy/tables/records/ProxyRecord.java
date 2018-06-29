/*
 * This file is generated by jOOQ.
*/
package com.danyl.dangdangspider.jooq.gen.proxy.tables.records;


import com.danyl.dangdangspider.jooq.gen.proxy.tables.Proxy;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record7;
import org.jooq.Row7;
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
public class ProxyRecord extends UpdatableRecordImpl<ProxyRecord> implements Record7<String, Integer, Integer, String, Boolean, String, LocalDateTime> {

    private static final long serialVersionUID = -538916076;

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
     * Setter for <code>proxy.speed</code>.
     */
    public ProxyRecord setSpeed(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>proxy.speed</code>.
     */
    public Integer getSpeed() {
        return (Integer) get(2);
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
     * Setter for <code>proxy.is_valid</code>.
     */
    public ProxyRecord setIsValid(Boolean value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>proxy.is_valid</code>.
     */
    public Boolean getIsValid() {
        return (Boolean) get(4);
    }

    /**
     * Setter for <code>proxy.comment</code>.
     */
    public ProxyRecord setComment(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>proxy.comment</code>.
     */
    public String getComment() {
        return (String) get(5);
    }

    /**
     * Setter for <code>proxy.create_time</code>.
     */
    public ProxyRecord setCreateTime(LocalDateTime value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>proxy.create_time</code>.
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<String, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, Integer, Integer, String, Boolean, String, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, Integer, Integer, String, Boolean, String, LocalDateTime> valuesRow() {
        return (Row7) super.valuesRow();
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
    public Field<Integer> field3() {
        return Proxy.PROXY.SPEED;
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
    public Field<Boolean> field5() {
        return Proxy.PROXY.IS_VALID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Proxy.PROXY.COMMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field7() {
        return Proxy.PROXY.CREATE_TIME;
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
    public Integer component3() {
        return getSpeed();
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
    public Boolean component5() {
        return getIsValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getComment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component7() {
        return getCreateTime();
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
    public Integer value3() {
        return getSpeed();
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
    public Boolean value5() {
        return getIsValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getComment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value7() {
        return getCreateTime();
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
    public ProxyRecord value3(Integer value) {
        setSpeed(value);
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
    public ProxyRecord value5(Boolean value) {
        setIsValid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value6(String value) {
        setComment(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord value7(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProxyRecord values(String value1, Integer value2, Integer value3, String value4, Boolean value5, String value6, LocalDateTime value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
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
    public ProxyRecord(String ip, Integer port, Integer speed, String type, Boolean isValid, String comment, LocalDateTime createTime) {
        super(Proxy.PROXY);

        set(0, ip);
        set(1, port);
        set(2, speed);
        set(3, type);
        set(4, isValid);
        set(5, comment);
        set(6, createTime);
    }
}
