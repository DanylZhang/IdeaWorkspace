package com.danyl.core.bean;

import com.danyl.core.bean.product.Sku;
import org.apache.regexp.REUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class BuyerCart {
    //商品集
    private List<BuyerItem> buyerItems = new ArrayList<BuyerItem>();

    public List<BuyerItem> getBuyerItems() {
        return buyerItems;
    }

    public void setBuyerItems(List<BuyerItem> buyerItems) {
        this.buyerItems = buyerItems;
    }

    //添加商品
    public void addItem(BuyerItem buyerItem) {
        //判断是否同款已加入购物车
        if (buyerItems.contains(buyerItem)) {
            //拿到已加入购物车的同款并追加数量
            int i = buyerItems.indexOf(buyerItem);
            BuyerItem buyerItem1 = buyerItems.get(i);
            buyerItem1.setAmount(buyerItem1.getAmount() + buyerItem.getAmount());
        } else {
            buyerItems.add(buyerItem);
        }
    }

    //删除商品
    public void removeItem(Integer skuId){
        Sku sku = new Sku();
        sku.setId(skuId);
        BuyerItem buyerItem = new BuyerItem();
        buyerItem.setSku(sku);
        //能指定remove的对象前提是，重写了buyerItem的equals方法，内部以skuId为标准
        this.buyerItems.remove(buyerItem);
    }

    //清空购物车
    public void clear(){
        this.buyerItems.clear();
    }

    //购物车数量
    public Integer getProductAmount() {
        Integer result = 0;
        for (BuyerItem buyerItem : this.getBuyerItems()) {
            result += buyerItem.getAmount();
        }
        return result;
    }

    //商品金额
    public Float getProductPrice() {
        Float result = 0.0f;
        for (BuyerItem buyerItem : this.getBuyerItems()) {
            result += buyerItem.getSku().getSkuPrice().floatValue() * buyerItem.getAmount();
        }
        return result;
    }

    //运费
    public Float getDeliveFee() {
        Float result = 0.0f;
        if (getProductPrice() < 79) {
            result += 5;
        }
        return result;
    }

    //总金额
    public Float getTotalPrice() {
        return getProductPrice() + getDeliveFee();
    }
}