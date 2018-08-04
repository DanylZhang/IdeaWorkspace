/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.xiaomi.tables.records;


import com.danyl.spiders.jooq.gen.xiaomi.tables.ItemCategory;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Record1;
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
public class ItemCategoryRecord extends UpdatableRecordImpl<ItemCategoryRecord> {

    private static final long serialVersionUID = 1408920948;

    /**
     * Setter for <code>xiaomi.item_category.cid</code>.
     */
    public ItemCategoryRecord setCid(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.cid</code>.
     */
    public Integer getCid() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>xiaomi.item_category.parent_cid</code>.
     */
    public ItemCategoryRecord setParentCid(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.parent_cid</code>.
     */
    public Integer getParentCid() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>xiaomi.item_category.name</code>.
     */
    public ItemCategoryRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>xiaomi.item_category.full_name</code>.
     */
    public ItemCategoryRecord setFullName(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.full_name</code>.
     */
    public String getFullName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>xiaomi.item_category.item_count</code>.
     */
    public ItemCategoryRecord setItemCount(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.item_count</code>.
     */
    public Integer getItemCount() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>xiaomi.item_category.history</code>.
     */
    public ItemCategoryRecord setHistory(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.history</code>.
     */
    public String getHistory() {
        return (String) get(5);
    }

    /**
     * Setter for <code>xiaomi.item_category.is_parent</code>.
     */
    public ItemCategoryRecord setIsParent(Integer value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.is_parent</code>.
     */
    public Integer getIsParent() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>xiaomi.item_category.del_flag</code>.
     */
    public ItemCategoryRecord setDelFlag(Integer value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.del_flag</code>.
     */
    public Integer getDelFlag() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>xiaomi.item_category.del_flag_bak</code>.
     */
    public ItemCategoryRecord setDelFlagBak(Integer value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.del_flag_bak</code>.
     */
    public Integer getDelFlagBak() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>xiaomi.item_category.level</code>.
     */
    public ItemCategoryRecord setLevel(Integer value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.level</code>.
     */
    public Integer getLevel() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>xiaomi.item_category.top_parent_cid</code>.
     */
    public ItemCategoryRecord setTopParentCid(Integer value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.top_parent_cid</code>.
     */
    public Integer getTopParentCid() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv1cid</code>.
     */
    public ItemCategoryRecord setLv1cid(Integer value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv1cid</code>.
     */
    public Integer getLv1cid() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv2cid</code>.
     */
    public ItemCategoryRecord setLv2cid(Integer value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv2cid</code>.
     */
    public Integer getLv2cid() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv3cid</code>.
     */
    public ItemCategoryRecord setLv3cid(Integer value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv3cid</code>.
     */
    public Integer getLv3cid() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv4cid</code>.
     */
    public ItemCategoryRecord setLv4cid(Integer value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv4cid</code>.
     */
    public Integer getLv4cid() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv5cid</code>.
     */
    public ItemCategoryRecord setLv5cid(Integer value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv5cid</code>.
     */
    public Integer getLv5cid() {
        return (Integer) get(15);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv1name</code>.
     */
    public ItemCategoryRecord setLv1name(String value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv1name</code>.
     */
    public String getLv1name() {
        return (String) get(16);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv2name</code>.
     */
    public ItemCategoryRecord setLv2name(String value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv2name</code>.
     */
    public String getLv2name() {
        return (String) get(17);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv3name</code>.
     */
    public ItemCategoryRecord setLv3name(String value) {
        set(18, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv3name</code>.
     */
    public String getLv3name() {
        return (String) get(18);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv4name</code>.
     */
    public ItemCategoryRecord setLv4name(String value) {
        set(19, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv4name</code>.
     */
    public String getLv4name() {
        return (String) get(19);
    }

    /**
     * Setter for <code>xiaomi.item_category.lv5name</code>.
     */
    public ItemCategoryRecord setLv5name(String value) {
        set(20, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.lv5name</code>.
     */
    public String getLv5name() {
        return (String) get(20);
    }

    /**
     * Setter for <code>xiaomi.item_category.modified</code>.
     */
    public ItemCategoryRecord setModified(LocalDateTime value) {
        set(21, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.modified</code>.
     */
    public LocalDateTime getModified() {
        return (LocalDateTime) get(21);
    }

    /**
     * Setter for <code>xiaomi.item_category.created</code>.
     */
    public ItemCategoryRecord setCreated(LocalDateTime value) {
        set(22, value);
        return this;
    }

    /**
     * Getter for <code>xiaomi.item_category.created</code>.
     */
    public LocalDateTime getCreated() {
        return (LocalDateTime) get(22);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ItemCategoryRecord
     */
    public ItemCategoryRecord() {
        super(ItemCategory.ITEM_CATEGORY);
    }

    /**
     * Create a detached, initialised ItemCategoryRecord
     */
    public ItemCategoryRecord(Integer cid, Integer parentCid, String name, String fullName, Integer itemCount, String history, Integer isParent, Integer delFlag, Integer delFlagBak, Integer level, Integer topParentCid, Integer lv1cid, Integer lv2cid, Integer lv3cid, Integer lv4cid, Integer lv5cid, String lv1name, String lv2name, String lv3name, String lv4name, String lv5name, LocalDateTime modified, LocalDateTime created) {
        super(ItemCategory.ITEM_CATEGORY);

        set(0, cid);
        set(1, parentCid);
        set(2, name);
        set(3, fullName);
        set(4, itemCount);
        set(5, history);
        set(6, isParent);
        set(7, delFlag);
        set(8, delFlagBak);
        set(9, level);
        set(10, topParentCid);
        set(11, lv1cid);
        set(12, lv2cid);
        set(13, lv3cid);
        set(14, lv4cid);
        set(15, lv5cid);
        set(16, lv1name);
        set(17, lv2name);
        set(18, lv3name);
        set(19, lv4name);
        set(20, lv5name);
        set(21, modified);
        set(22, created);
    }
}
