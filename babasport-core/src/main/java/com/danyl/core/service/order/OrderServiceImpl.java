package com.danyl.core.service.order;

import com.danyl.core.bean.BuyerCart;
import com.danyl.core.bean.BuyerItem;
import com.danyl.core.bean.order.Detail;
import com.danyl.core.bean.order.Order;
import com.danyl.core.bean.product.Sku;
import com.danyl.core.dao.order.DetailDao;
import com.danyl.core.dao.order.OrderDao;
import com.danyl.core.dao.product.SkuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private DetailDao detailDao;
    @Autowired
    private SkuDao skuDao;

    @Override
    public Long insertOrder(Order order, BuyerCart buyerCart) {
        Jedis jedis = jedisPool.getResource();
        Long oid = jedis.incr("oid");
        order.setOid(String.valueOf(oid));
        //运费
        order.setDeliverFee(new BigDecimal(buyerCart.getDeliveFee()));
        //商品金额
        order.setPayableFee(buyerCart.getProductPrice().doubleValue());
        //订单总金额
        order.setTotalPrice(buyerCart.getDeliveFee().doubleValue());

        //支付方式
        if (order.getPaymentWay() == 0) {
            order.setIsPaiy(0);
        } else {
            order.setIsPaiy(1);
        }

        //订单状态
        order.setState(0);
        //创建时间
        order.setCreateDate(new Date());
        orderDao.insert(order);

        for (BuyerItem buyerItem : buyerCart.getBuyerItems()) {
            //订单包含的商品详细快照
            Detail detail = new Detail();
            detail.setOrderId(order.getId());
            detail.setProductNo(buyerItem.getSku().getProduct().getNo());
            detail.setProductName(buyerItem.getSku().getProduct().getName());
            detail.setColor(buyerItem.getSku().getColor().getName());
            detail.setSize(buyerItem.getSku().getSize());
            detail.setSkuPrice(buyerItem.getSku().getSkuPrice());
            detail.setAmount(buyerItem.getAmount());
            //保存详细快照
            detailDao.insert(detail);
            Sku sku = new Sku();
            sku.setId(buyerItem.getSku().getId());
            sku.setStockInventory(buyerItem.getSku().getStockInventory() - buyerItem.getAmount());
            //减库存
            skuDao.updateByPrimaryKeySelective(sku);
        }
        return oid;
    }
}