/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.vip.tables.records;


import com.danyl.spiders.jooq.gen.vip.tables.Ads;

import java.time.LocalDate;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record4;
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
public class AdsRecord extends UpdatableRecordImpl<AdsRecord> implements Record5<Integer, Integer, Long, LocalDate, Integer> {

    private static final long serialVersionUID = -1358086020;

    /**
     * Setter for <code>new_vip.ads.item_id</code>.
     */
    public AdsRecord setItemId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>new_vip.ads.item_id</code>.
     */
    public Integer getItemId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>new_vip.ads.list_id</code>.
     */
    public AdsRecord setListId(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>new_vip.ads.list_id</code>.
     */
    public Integer getListId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>new_vip.ads.spuId</code>.
     */
    public AdsRecord setSpuid(Long value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>new_vip.ads.spuId</code>.
     */
    public Long getSpuid() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>new_vip.ads.date</code>.
     */
    public AdsRecord setDate(LocalDate value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>new_vip.ads.date</code>.
     */
    public LocalDate getDate() {
        return (LocalDate) get(3);
    }

    /**
     * Setter for <code>new_vip.ads.act_id</code>.
     */
    public AdsRecord setActId(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>new_vip.ads.act_id</code>.
     */
    public Integer getActId() {
        return (Integer) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record4<Integer, Integer, LocalDate, Integer> key() {
        return (Record4) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, Integer, Long, LocalDate, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, Integer, Long, LocalDate, Integer> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Ads.ADS.ITEM_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Ads.ADS.LIST_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field3() {
        return Ads.ADS.SPUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field4() {
        return Ads.ADS.DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Ads.ADS.ACT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getItemId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getListId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component3() {
        return getSpuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate component4() {
        return getDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getActId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getItemId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getListId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value3() {
        return getSpuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value4() {
        return getDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getActId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsRecord value1(Integer value) {
        setItemId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsRecord value2(Integer value) {
        setListId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsRecord value3(Long value) {
        setSpuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsRecord value4(LocalDate value) {
        setDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsRecord value5(Integer value) {
        setActId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdsRecord values(Integer value1, Integer value2, Long value3, LocalDate value4, Integer value5) {
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
     * Create a detached AdsRecord
     */
    public AdsRecord() {
        super(Ads.ADS);
    }

    /**
     * Create a detached, initialised AdsRecord
     */
    public AdsRecord(Integer itemId, Integer listId, Long spuid, LocalDate date, Integer actId) {
        super(Ads.ADS);

        set(0, itemId);
        set(1, listId);
        set(2, spuid);
        set(3, date);
        set(4, actId);
    }
}
