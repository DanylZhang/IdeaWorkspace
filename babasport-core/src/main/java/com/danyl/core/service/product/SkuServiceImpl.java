package com.danyl.core.service.product;

import com.danyl.core.bean.product.*;
import com.danyl.core.dao.product.ColorDao;
import com.danyl.core.dao.product.ImgDao;
import com.danyl.core.dao.product.ProductDao;
import com.danyl.core.dao.product.SkuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// Spring整合Mybatis一级缓存失效，
// 调查后发现Spring的ThreadLocal里持有的sqlSession每次与数据库交互时都会重新打开，
// 故无法使用一级缓存。
// 但加了Transactional注解后，由于事务失败会回滚，需要保持同一个sqlSession，
// 所以Spring会使用SqlSessionHolder来保持sqlSession
// 从而保证了sqlSession不会关闭，自然也就保留了Mybatis的一级缓存
// 由于Mybatis一级缓存由PerpetualCache里的HashMap来缓存查询结果
// 相同的查询将从HashMap直接获得对象的引用，Set能对其去重，否则要重写对象的equals方法
@Transactional
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private ColorDao colorDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ImgDao imgDao;

    @Override
    public List<Sku> selectSkuListByProductId(Integer productId) {
        SkuQuery skuQuery = new SkuQuery();
        skuQuery.createCriteria().andProductIdEqualTo(productId);
        List<Sku> skus = skuDao.selectByExample(skuQuery);
        for (Sku sku : skus) {
            sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
        }
        return skus;
    }

    @Override
    public List<Sku> selectSkuListByProductIdWithStock(Integer productId) {
        SkuQuery skuQuery = new SkuQuery();
        skuQuery.createCriteria().andProductIdEqualTo(productId).andStockInventoryGreaterThan(0);
        List<Sku> skus = skuDao.selectByExample(skuQuery);
        for (Sku sku : skus) {
            sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
        }
        return skus;
    }

    @Override
    public void updateSkuById(Sku sku) {
        skuDao.updateByPrimaryKeySelective(sku);
    }

    @Override
    public Sku selectSkuById(Integer id) {
        Sku sku = skuDao.selectByPrimaryKey(id);
        //查询颜色
        Color color = colorDao.selectByPrimaryKey(sku.getColorId());
        //查询商品
        Product product = productDao.selectByPrimaryKey(sku.getProductId());
        //查询图片
        ImgQuery imgQuery = new ImgQuery();
        imgQuery.createCriteria().andProductIdEqualTo(product.getId()).andIsDefEqualTo(true);
        List<Img> imgs = imgDao.selectByExample(imgQuery);
        product.setImg(imgs.get(0));

        sku.setColor(color);
        sku.setProduct(product);
        return sku;
    }
}