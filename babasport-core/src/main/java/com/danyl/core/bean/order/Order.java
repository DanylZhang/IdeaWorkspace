package com.danyl.core.bean.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Order implements Serializable {
    /**
     * ID
     */
    private Integer id;

    /**
     * 订单号
     */
    private String oid;

    /**
     * 运费
     */
    private BigDecimal deliverFee;

    /**
     * 应付金额
     */
    private Double payableFee;

    /**
     * 订单金额
     */
    private Double totalPrice;

    /**
     * 支付方式 0:到付 1:在线 2:邮局 3:公司转帐
     */
    private Integer paymentWay;

    /**
     * 货到付款方式.1现金,2POS刷卡
     */
    private Integer paymentCash;

    /**
     * 送货时间
     */
    private Integer delivery;

    /**
     * 是否电话确认 1:是  0: 否
     */
    private Boolean isconfirm;

    /**
     * 支付状态 :0到付1待付款,2已付款,3待退款,4退款成功,5退款失败
     */
    private Integer isPaiy;

    /**
     * 订单状态 0:提交订单 1:仓库配货 2:商品出库 3:等待收货 4:完成 5待退货 6已退货
     */
    private Integer state;

    /**
     * 订单生成时间
     */
    private Date createDate;

    /**
     * 附言
     */
    private String note;

    /**
     * 用户名
     */
    private String buyerId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid == null ? null : oid.trim();
    }

    public BigDecimal getDeliverFee() {
        return deliverFee;
    }

    public void setDeliverFee(BigDecimal deliverFee) {
        this.deliverFee = deliverFee;
    }

    public Double getPayableFee() {
        return payableFee;
    }

    public void setPayableFee(Double payableFee) {
        this.payableFee = payableFee;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(Integer paymentWay) {
        this.paymentWay = paymentWay;
    }

    public Integer getPaymentCash() {
        return paymentCash;
    }

    public void setPaymentCash(Integer paymentCash) {
        this.paymentCash = paymentCash;
    }

    public Integer getDelivery() {
        return delivery;
    }

    public void setDelivery(Integer delivery) {
        this.delivery = delivery;
    }

    public Boolean getIsconfirm() {
        return isconfirm;
    }

    public void setIsconfirm(Boolean isconfirm) {
        this.isconfirm = isconfirm;
    }

    public Integer getIsPaiy() {
        return isPaiy;
    }

    public void setIsPaiy(Integer isPaiy) {
        this.isPaiy = isPaiy;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId == null ? null : buyerId.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", oid=").append(oid);
        sb.append(", deliverFee=").append(deliverFee);
        sb.append(", payableFee=").append(payableFee);
        sb.append(", totalPrice=").append(totalPrice);
        sb.append(", paymentWay=").append(paymentWay);
        sb.append(", paymentCash=").append(paymentCash);
        sb.append(", delivery=").append(delivery);
        sb.append(", isconfirm=").append(isconfirm);
        sb.append(", isPaiy=").append(isPaiy);
        sb.append(", state=").append(state);
        sb.append(", createDate=").append(createDate);
        sb.append(", note=").append(note);
        sb.append(", buyerId=").append(buyerId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}