package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.*;
import com.danyl.core.dao.product.ImgDao;
import com.danyl.core.dao.product.ProductDao;
import com.danyl.core.dao.product.SkuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ImgDao imgDao;
    @Autowired
    private SkuDao skuDao;

    @Override
    public Pagination selectPaginationByQuery(ProductQuery productQuery) {
        Pagination pagination = new Pagination(productQuery.getPageNo(), productQuery.getPageSize(), (int) productDao.countByExample(productQuery));
        //结果集
        List<Product> products = productDao.selectByExample(productQuery);
        for (Product product : products) {
            ImgQuery imgQuery = new ImgQuery();
            imgQuery.createCriteria().andProductIdEqualTo(product.getId()).andIsDefEqualTo(true);
            List<Img> imgs = imgDao.selectByExample(imgQuery);
            product.setImg(imgs.get(0));
        }
        pagination.setList(products);
        return pagination;
    }

    @Autowired
    private Jedis jedis;
    @Override
    public void insertProduct(Product product) {
        Long pno = jedis.incr("pno");
        product.setId(Math.toIntExact(pno));
        //设置下架
        product.setIsShow(false);
        //设置不删除
        product.setIsDel(true);
        //增加时间
        product.setCreateTime(new Date());
        //从img对象设置product imgUrl
        product.setImgUrl(product.getImg().getUrl());
        //保存 insert intot bbs_product
        productDao.insertSelective(product);
        //保存图片
        Img img = product.getImg();
        //设置商品ID
        img.setProductId(product.getId());
        //设置默认
        img.setIsDef(true);
        imgDao.insertSelective(img);

        //保存Sku
        for (String colorId : product.getColor().split(",")) {
            for (String size : product.getSize().split(",")) {
                //创建
                Sku sku = new Sku();
                //设置颜色
                sku.setColorId(Integer.parseInt(colorId));
                //设置尺码
                sku.setSize(size);
                //设置商品ID
                sku.setProductId(product.getId());
                //运费
                sku.setDeliveFee(10.0);
                //售价
                sku.setSkuPrice(0.0);
                //市场价
                sku.setMarketPrice(0.0);
                //库存
                sku.setStockInventory(0);
                //购买限制
                sku.setSkuUpperLimit(200);
                //添加时间
                sku.setCreateTime(new Date());
                //保存
                skuDao.insertSelective(sku);
            }
        }
    }
}