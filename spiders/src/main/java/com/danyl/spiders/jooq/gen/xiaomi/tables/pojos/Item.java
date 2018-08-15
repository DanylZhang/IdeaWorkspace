/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.xiaomi.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Generated;


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
public class Item implements Serializable {

    private static final long serialVersionUID = 121050289;

    private String        itemId;
    private Long          commodityId;
    private Long          goodsId;
    private Long          productId;
    private String        name;
    private Integer       cid;
    private Integer       sid;
    private Integer       brand;
    private Integer       price;
    private Integer       overSea;
    private String        region;
    private Integer       hot;
    private Integer       commentNum;
    private String        props;
    private String        img;
    private LocalDateTime modified;
    private LocalDateTime created;

    public Item() {}

    public Item(Item value) {
        this.itemId = value.itemId;
        this.commodityId = value.commodityId;
        this.goodsId = value.goodsId;
        this.productId = value.productId;
        this.name = value.name;
        this.cid = value.cid;
        this.sid = value.sid;
        this.brand = value.brand;
        this.price = value.price;
        this.overSea = value.overSea;
        this.region = value.region;
        this.hot = value.hot;
        this.commentNum = value.commentNum;
        this.props = value.props;
        this.img = value.img;
        this.modified = value.modified;
        this.created = value.created;
    }

    public Item(
        String        itemId,
        Long          commodityId,
        Long          goodsId,
        Long          productId,
        String        name,
        Integer       cid,
        Integer       sid,
        Integer       brand,
        Integer       price,
        Integer       overSea,
        String        region,
        Integer       hot,
        Integer       commentNum,
        String        props,
        String        img,
        LocalDateTime modified,
        LocalDateTime created
    ) {
        this.itemId = itemId;
        this.commodityId = commodityId;
        this.goodsId = goodsId;
        this.productId = productId;
        this.name = name;
        this.cid = cid;
        this.sid = sid;
        this.brand = brand;
        this.price = price;
        this.overSea = overSea;
        this.region = region;
        this.hot = hot;
        this.commentNum = commentNum;
        this.props = props;
        this.img = img;
        this.modified = modified;
        this.created = created;
    }

    public String getItemId() {
        return this.itemId;
    }

    public Item setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public Long getCommodityId() {
        return this.commodityId;
    }

    public Item setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
        return this;
    }

    public Long getGoodsId() {
        return this.goodsId;
    }

    public Item setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
        return this;
    }

    public Long getProductId() {
        return this.productId;
    }

    public Item setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getCid() {
        return this.cid;
    }

    public Item setCid(Integer cid) {
        this.cid = cid;
        return this;
    }

    public Integer getSid() {
        return this.sid;
    }

    public Item setSid(Integer sid) {
        this.sid = sid;
        return this;
    }

    public Integer getBrand() {
        return this.brand;
    }

    public Item setBrand(Integer brand) {
        this.brand = brand;
        return this;
    }

    public Integer getPrice() {
        return this.price;
    }

    public Item setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public Integer getOverSea() {
        return this.overSea;
    }

    public Item setOverSea(Integer overSea) {
        this.overSea = overSea;
        return this;
    }

    public String getRegion() {
        return this.region;
    }

    public Item setRegion(String region) {
        this.region = region;
        return this;
    }

    public Integer getHot() {
        return this.hot;
    }

    public Item setHot(Integer hot) {
        this.hot = hot;
        return this;
    }

    public Integer getCommentNum() {
        return this.commentNum;
    }

    public Item setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
        return this;
    }

    public String getProps() {
        return this.props;
    }

    public Item setProps(String props) {
        this.props = props;
        return this;
    }

    public String getImg() {
        return this.img;
    }

    public Item setImg(String img) {
        this.img = img;
        return this;
    }

    public LocalDateTime getModified() {
        return this.modified;
    }

    public Item setModified(LocalDateTime modified) {
        this.modified = modified;
        return this;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public Item setCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Item other = (Item) obj;
        if (itemId == null) {
            if (other.itemId != null)
                return false;
        }
        else if (!itemId.equals(other.itemId))
            return false;
        if (commodityId == null) {
            if (other.commodityId != null)
                return false;
        }
        else if (!commodityId.equals(other.commodityId))
            return false;
        if (goodsId == null) {
            if (other.goodsId != null)
                return false;
        }
        else if (!goodsId.equals(other.goodsId))
            return false;
        if (productId == null) {
            if (other.productId != null)
                return false;
        }
        else if (!productId.equals(other.productId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (cid == null) {
            if (other.cid != null)
                return false;
        }
        else if (!cid.equals(other.cid))
            return false;
        if (sid == null) {
            if (other.sid != null)
                return false;
        }
        else if (!sid.equals(other.sid))
            return false;
        if (brand == null) {
            if (other.brand != null)
                return false;
        }
        else if (!brand.equals(other.brand))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        }
        else if (!price.equals(other.price))
            return false;
        if (overSea == null) {
            if (other.overSea != null)
                return false;
        }
        else if (!overSea.equals(other.overSea))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        }
        else if (!region.equals(other.region))
            return false;
        if (hot == null) {
            if (other.hot != null)
                return false;
        }
        else if (!hot.equals(other.hot))
            return false;
        if (commentNum == null) {
            if (other.commentNum != null)
                return false;
        }
        else if (!commentNum.equals(other.commentNum))
            return false;
        if (props == null) {
            if (other.props != null)
                return false;
        }
        else if (!props.equals(other.props))
            return false;
        if (img == null) {
            if (other.img != null)
                return false;
        }
        else if (!img.equals(other.img))
            return false;
        if (modified == null) {
            if (other.modified != null)
                return false;
        }
        else if (!modified.equals(other.modified))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        }
        else if (!created.equals(other.created))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.itemId == null) ? 0 : this.itemId.hashCode());
        result = prime * result + ((this.commodityId == null) ? 0 : this.commodityId.hashCode());
        result = prime * result + ((this.goodsId == null) ? 0 : this.goodsId.hashCode());
        result = prime * result + ((this.productId == null) ? 0 : this.productId.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.cid == null) ? 0 : this.cid.hashCode());
        result = prime * result + ((this.sid == null) ? 0 : this.sid.hashCode());
        result = prime * result + ((this.brand == null) ? 0 : this.brand.hashCode());
        result = prime * result + ((this.price == null) ? 0 : this.price.hashCode());
        result = prime * result + ((this.overSea == null) ? 0 : this.overSea.hashCode());
        result = prime * result + ((this.region == null) ? 0 : this.region.hashCode());
        result = prime * result + ((this.hot == null) ? 0 : this.hot.hashCode());
        result = prime * result + ((this.commentNum == null) ? 0 : this.commentNum.hashCode());
        result = prime * result + ((this.props == null) ? 0 : this.props.hashCode());
        result = prime * result + ((this.img == null) ? 0 : this.img.hashCode());
        result = prime * result + ((this.modified == null) ? 0 : this.modified.hashCode());
        result = prime * result + ((this.created == null) ? 0 : this.created.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Item (");

        sb.append(itemId);
        sb.append(", ").append(commodityId);
        sb.append(", ").append(goodsId);
        sb.append(", ").append(productId);
        sb.append(", ").append(name);
        sb.append(", ").append(cid);
        sb.append(", ").append(sid);
        sb.append(", ").append(brand);
        sb.append(", ").append(price);
        sb.append(", ").append(overSea);
        sb.append(", ").append(region);
        sb.append(", ").append(hot);
        sb.append(", ").append(commentNum);
        sb.append(", ").append(props);
        sb.append(", ").append(img);
        sb.append(", ").append(modified);
        sb.append(", ").append(created);

        sb.append(")");
        return sb.toString();
    }
}