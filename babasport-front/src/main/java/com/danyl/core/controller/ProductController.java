package com.danyl.core.controller;

import com.danyl.common.pagination.Pagination;
import com.danyl.common.util.Utils;
import com.danyl.core.bean.product.*;
import com.danyl.core.service.product.ProductService;
import com.danyl.core.service.product.SkuService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.Hash;
import org.ehcache.core.spi.service.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    private SolrServer solrServer;
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private ProductService productService;
    @Autowired
    private SkuService skuService;

    @RequestMapping(value = "list.html")
    public String list(Integer pageNo, String keyword, Integer brandId, String price, Model model) throws SolrServerException {
        //获取品牌列表
        List<Brand> brands = new ArrayList<>();
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys("brand:*");
        for (String key : keys) {
            List<String> hmget = jedis.hmget(key, "id", "name");
            Brand brand = new Brand();
            brand.setId(Integer.parseInt(hmget.get(0)));
            brand.setName(hmget.get(1));
            brands.add(brand);
        }
        model.addAttribute("brands", brands);
        //创建商品查询对象
        ProductQuery productQuery = new ProductQuery();
        //设置分页
        productQuery.setPageNo(Pagination.cpn(pageNo));
        productQuery.setPageSize(4);

        StringBuilder params = new StringBuilder();
        params.append("keyword=").append(keyword);
        //创建solr查询对象
        SolrQuery solrQuery = new SolrQuery();
        //关键词
        solrQuery.set("q", "name_ik:" + keyword);
        Map<String, String> fq = new HashMap<>();
        //过滤条件
        if (null != brandId) {
            solrQuery.addFilterQuery("brandId:" + brandId);
            params.append("&brandId=").append(brandId);
            fq.put("品牌", jedis.hget("brand:" + brandId, "name"));
        }
        if (null != price) {
//            solrQuery.addFilterQuery("price:[0.0 TO 79.0]");
            String[] split = price.split("-");
            Float startP, endP;
            if (split.length == 2) {
                startP = new Float(split[0]);
                endP = new Float(split[1]);
                fq.put("价格", price);
            } else {
                startP = new Float(split[0]);
                endP = Float.MAX_VALUE;
                fq.put("价格", price + "以上");
            }
            solrQuery.addFilterQuery("price:[" + startP + " TO " + endP + "]");
            params.append("&price=").append(price);
        }
        model.addAttribute("fq", fq);

        //排序
        solrQuery.addSort("price", SolrQuery.ORDER.asc);
        //分页
        solrQuery.setStart(productQuery.getStartRow());
        solrQuery.setRows(productQuery.getPageSize());
        //高亮
        //1.打开高亮开关
        solrQuery.setHighlight(true);
        //2.设置高亮字段
        solrQuery.addHighlightField("name_ik");
        //3.高亮字段的前缀
        solrQuery.setHighlightSimplePre("<span style='color:red'>");
        //4.高亮字段的后缀
        solrQuery.setHighlightSimplePost("</span>");


        //查询solr服务器
        QueryResponse response = solrServer.query(solrQuery);
        //获取结果文档
        SolrDocumentList solrDocuments = response.getResults();
        //获取高亮结果Map
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

        //查询结果集转换
        List<Product> products = new ArrayList<>();
        for (SolrDocument solrDocument : solrDocuments) {
            Product product = new Product();
            //商品id
            product.setId(Integer.parseInt(solrDocument.get("id").toString()));
            //商品名
            product.setName(solrDocument.get("name_ik").toString());
            //关键字高亮商品名
            product.setName(highlighting.get(product.getId().toString()).get("name_ik").get(0));
            //商品价格
            product.setPrice(Double.parseDouble(solrDocument.get("price").toString()));
            //商品图片url
            Img img = new Img();
            img.setUrl(solrDocument.get("url").toString());
            product.setImg(img);
            products.add(product);
        }

        //总条数
        long numFound = solrDocuments.getNumFound();
        //分页对象
        Pagination pagination = new Pagination(
                productQuery.getPageNo(),
                productQuery.getPageSize(),
                (int) numFound
        );
        //设置结果集给分页对象
        pagination.setList(products);
        String url = "/product/list.html";
        pagination.pageView(url, params.toString());
        model.addAttribute("pagination", pagination);

        //最后不要忘了关闭jedis
        jedis.close();
        return "product/product";
    }

    @RequestMapping(value = "/detail/{id}.html")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.selectProductById(id);
        model.addAttribute("product", product);
        List<Sku> skus = skuService.selectSkuListByProductIdWithStock(id);
        model.addAttribute("skus", skus);
        Set<Color> colorSet = new LinkedHashSet<>();
        for (Sku sku : skus) {
            colorSet.add(sku.getColor());
        }
        model.addAttribute("colorSet", colorSet);
        return "product/productDetail";
    }
}