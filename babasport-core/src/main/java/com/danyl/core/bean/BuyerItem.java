package com.danyl.core.bean;

import com.danyl.core.bean.product.Sku;

import java.util.Objects;

public class BuyerItem {
    //购买的单品
    private Sku sku;
    //购买数量
    private Integer amount = 1;
    //是否有货
    private Boolean haveInventory = true;

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getHaveInventory() {
        return haveInventory;
    }

    public void setHaveInventory(Boolean haveInventory) {
        this.haveInventory = haveInventory;
    }

    @Override
    public String toString() {
        return "BuyerItem{" +
                "sku=" + sku +
                ", amount=" + amount +
                ", haveInventory=" + haveInventory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuyerItem buyerItem = (BuyerItem) o;
        return Objects.equals(sku.getId(), buyerItem.sku.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}