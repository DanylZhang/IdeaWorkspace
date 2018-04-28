package com.danyl.core.service.product;

import com.danyl.common.pagination.Pagination;
import com.danyl.core.bean.product.*;
import com.danyl.core.dao.product.ImgDao;
import com.danyl.core.dao.product.ProductDao;
import com.danyl.core.dao.product.SkuDao;
import com.danyl.core.service.staticpage.StaticPageService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ImgDao imgDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private SolrServer solrServer;
    @Autowired
    private StaticPageService staticPageService;
    @Autowired
    private SkuService skuService;

    @Override
    public Pagination selectPaginationByQuery(ProductQuery productQuery) {
        Pagination pagination = new Pagination(productQuery.getPageNo(), productQuery.getPageSize(), (int) productDao.countByExample(productQuery));
        //结果集
        List<Product> products = productDao.selectByExample(productQuery);
        for (Product product : products) {
            ImgQuery imgQuery = new ImgQuery();
            imgQuery.createCriteria().andProductIdEqualTo(product.getId()).andIsDefEqualTo(true);
            List<Img> imgs = imgDao.selectByExample(imgQuery);
            if (imgs.size() > 0) {
                product.setImg(imgs.get(0));
            }
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

    @Override
    public void deleteProductByIds(Integer[] ids) {
        for (Integer id : ids) {
            Product product = new Product();
            product.setId(id);
            product.setIsDel(false);
            productDao.updateByPrimaryKeySelective(product);
        }
    }

    @Override
    public void onSale(Integer[] ids) {
        for (Integer id : ids) {
            //step 1: 修改商品为上架状态
            Product product = new Product();
            product.setId(id);
            product.setIsShow(true);
            productDao.updateByPrimaryKeySelective(product);

            //step 2: 保存到solr
            SolrInputDocument doc = new SolrInputDocument();
            Product p = productDao.selectByPrimaryKey(id);
            //product_id
            doc.setField("id", id);
            //name
            doc.setField("name_ik", p.getName());
            //url
            ImgQuery imgQuery = new ImgQuery();
            imgQuery.createCriteria().andProductIdEqualTo(id);
            List<Img> imgs = imgDao.selectByExample(imgQuery);
            doc.setField("url", imgs.get(0).getUrl());
            //brandId
            doc.setField("brandId", p.getBrandId());
            //price
            SkuQuery skuQuery = new SkuQuery();
            skuQuery.createCriteria().andProductIdEqualTo(id);
            skuQuery.setFields("sku_price");
            skuQuery.setOrderByClause("sku_price asc");
            skuQuery.setPageNo(1);
            skuQuery.setPageSize(1);
            List<Sku> skus = skuDao.selectByExample(skuQuery);
            doc.setField("price", skus.get(0).getSkuPrice());
            try {
                solrServer.add(doc);
                solrServer.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //静态化
            Map<String, Object> root = new HashMap<>();
            Product staticProduct = selectProductById(id);
            root.put("product", staticProduct);
            List<Sku> staticSkus = skuService.selectSkuListByProductIdWithStock(id);
            root.put("skus", staticSkus);
            Set<Color> colorSet = new HashSet<>();
            for (Sku sku : staticSkus) {
                colorSet.add(sku.getColor());
            }
            root.put("colorSet", colorSet);
            staticPageService.index(root, id);
        }
    }

    @Override
    public Product selectProductById(Integer id) {
        Product product = productDao.selectByPrimaryKey(id);
        ImgQuery imgQuery = new ImgQuery();
        imgQuery.createCriteria().andProductIdEqualTo(product.getId()).andIsDefEqualTo(true);
        List<Img> imgs = imgDao.selectByExample(imgQuery);
        if (imgs.size() > 0) {
            product.setImg(imgs.get(0));
        }
        return product;
    }

    @Override
    public void updateByProduct(Product product) {
        //从img对象给img_url赋值
        product.setImgUrl(product.getImg().getUrl());
        //保存图片
        Img img = product.getImg();
        //设置商品ID
        img.setProductId(product.getId());
        //设置默认
        img.setIsDef(true);
        imgDao.insertSelective(img);
        //更新商品
        productDao.updateByPrimaryKeySelective(product);
    }
}