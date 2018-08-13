package com.danyl.spiders.tasks;

import com.danyl.spiders.jooq.gen.xiaomi.tables.pojos.Item;
import com.danyl.spiders.jooq.gen.xiaomi.tables.pojos.ItemCategory;
import com.danyl.spiders.jooq.gen.xiaomi.tables.records.ItemRecord;
import com.danyl.spiders.service.ProxyService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.danyl.spiders.constants.Constants.XIAOMI_PAGESIZE;
import static com.danyl.spiders.constants.TimeConstants.DAYS;
import static com.danyl.spiders.jooq.gen.xiaomi.Tables.ITEM;
import static com.danyl.spiders.jooq.gen.xiaomi.Tables.ITEM_CATEGORY;

@Slf4j
@Component
public class XiaoMiProductTask {

    @Resource(name = "DSLContextXiaoMi")
    private DSLContext xm;

    // 测试节流用
    private int limit = Integer.MAX_VALUE;

    // li->json->price pattern
    private Pattern pattern = Pattern.compile("(\\d+(\\.\\d*)?)元(\\s*<del>.*元</del>)?");

    @Scheduled(fixedDelay = DAYS * 3)
    public void crawlXiaoMiItem() {
        log.info("crawl xiaomi product start {}", new Date());

        List<ItemCategory> itemCategories = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(2)).fetchInto(ItemCategory.class);

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
        itemCategories.stream()
                .flatMap(itemCategory -> {
                    Integer itemCount = itemCategory.getItemCount();
                    int pageNum = new Double(Math.ceil(itemCount * 1.0 / XIAOMI_PAGESIZE)).intValue();
                    return IntStream.rangeClosed(1, pageNum).mapToObj(PageIndex -> {
                        String url = String.format("https://list.mi.com/%d-0-0-0-0-0-0-0-%d", itemCategory.getCid(), PageIndex);
                        return Pair.of(itemCategory.getCid(), url);
                    });
                })
                .limit(limit)
                .map(cidUrlPair -> CompletableFuture.runAsync(() -> {
                    Integer cid = cidUrlPair.getLeft();
                    String url = cidUrlPair.getRight();
                    Document document = ProxyService.jsoupGet(url, "所有商品");
                    if (document == null) {
                        return;
                    }
                    document.select("div.content div.goods-list-box > div.goods-list > div.goods-item ul > li")
                            .forEach(li -> {
                                try {
                                    String json = li.attr("data-config");
                                    DocumentContext parse = JsonPath.parse(json);
                                    Long commodityId = Long.parseLong(parse.read("$.cid").toString());
                                    long goodsId = Long.parseLong(parse.read("$.gid").toString());
                                    Matcher matcher = pattern.matcher(parse.read("$.price").toString());
                                    int price = 0;
                                    if (matcher.find()) {
                                        price = new Double(new Double(matcher.group(1)) * 100).intValue();
                                    }

                                    String imgUrl = li.child(0).absUrl("src");
                                    String name = li.child(0).attr("alt");

                                    Item item = new Item();
                                    item.setItemId(commodityId + "-" + goodsId);
                                    item.setCommodityId(commodityId);
                                    item.setGoodsId(goodsId);
                                    item.setName(name);
                                    item.setCid(cid);
                                    item.setPrice(price);
                                    item.setImg(imgUrl);

                                    ItemRecord itemRecord = xm.selectFrom(ITEM).where(ITEM.ITEM_ID.eq(item.getItemId())).fetchOne();
                                    if (itemRecord != null) {
                                        xm.executeUpdate(xm.newRecord(ITEM, item));
                                    } else {
                                        xm.executeInsert(xm.newRecord(ITEM, item));
                                    }

                                    // 有些li中gid为0，可能确实为0，也可能有二级属性
                                    if (goodsId == 0) {
                                        String url1 = "https://item.mi.com/{}.html?cfrom=list".replace("{}", commodityId.toString());
                                        Document document1 = ProxyService.jsoupGet(url1, "小米商城");
                                        if (document1 == null) {
                                            return;
                                        }

                                        Matcher matcher1 = Pattern.compile("goodsStyleList:(.+?),\\s*//商品是否缺货").matcher(document1.html());
                                        if (matcher1.find()) {
                                            String json1 = matcher1.group(1);
                                            DocumentContext parse1 = JsonPath.parse(json1);
                                            List<Map<String, Object>> maps = parse1.read("$.*");
                                            for (Map<String, Object> map : maps) {
                                                commodityId = Long.parseLong(map.get("commodity_id").toString());
                                                goodsId = Long.parseLong(map.get("goods_id").toString());
                                                price = new Double(new Double(map.get("price").toString()) * 100).intValue();
                                                imgUrl = map.get("image").toString();
                                                name = map.get("name").toString();

                                                Item item1 = new Item();
                                                item1.setItemId(commodityId + "-" + goodsId);
                                                item1.setCommodityId(commodityId);
                                                item1.setGoodsId(goodsId);
                                                item1.setName(name);
                                                item1.setCid(cid);
                                                item1.setPrice(price);
                                                item1.setImg(imgUrl);

                                                // 进入商品详情页了，估计可以拿到productId
                                                Matcher productIdMatcher = Pattern.compile("productId:\"(\\d+)\",").matcher(document1.html());
                                                if (productIdMatcher.find()) {
                                                    Long productId = Long.parseLong(productIdMatcher.group(1));
                                                    item1.setProductId(productId);
                                                }

                                                ItemRecord itemRecord1 = xm.selectFrom(ITEM).where(ITEM.ITEM_ID.eq(item.getItemId())).fetchOne();
                                                if (itemRecord1 != null) {
                                                    xm.executeUpdate(xm.newRecord(ITEM, item));
                                                } else {
                                                    xm.executeInsert(xm.newRecord(ITEM, item));
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("crawl xiaomi product li parse error: {}", e.getMessage());
                                }
                            });
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });

        fixedThreadPool.shutdown();
        log.info("crawl xiaomi product end {}", new Date());
    }

    @Scheduled(fixedDelay = DAYS * 3)
    public void crawlXiaoMiProductId() {
        log.info("crawl xiaomi productId start {}", new Date());

        List<Long> commodityIds = xm.selectDistinct(ITEM.COMMODITY_ID)
                .from(ITEM)
                .where(ITEM.PRODUCT_ID.eq(0L))
                .fetch(ITEM.COMMODITY_ID);

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
        commodityIds.stream()
                .limit(limit)
                .map(commodityId -> CompletableFuture.supplyAsync(() -> {
                    long productId = 0L;
                    // 小米商城普通商品，无独立宣传页
                    String regex1 = "productId:\"(\\d+)\",";
                    // 小米商城推广商品，有独立宣传页
                    String regex2 = "commodity/detail/(\\d+)'";
                    // 小米商城推广商品，有独立宣传页，过时推广
                    String regex3 = "view/product_id/(\\d+)'";
                    // 小米商城推广商品，有独立宣传页，其他格式
                    String regex4 = "view\\?product_id=(\\d+)";

                    String url = "https://item.mi.com/{}.html?cfrom=list".replace("{}", commodityId.toString());
                    Document document = ProxyService.jsoupGet(url, "小米商城");

                    String html;
                    if (document == null) {
                        html = "";
                    } else {
                        html = document.html();
                    }

                    Matcher matcher = Pattern.compile(regex1).matcher(html);
                    if (matcher.find()) {
                        productId = Long.parseLong(matcher.group(1));
                    }
                    matcher = Pattern.compile(regex2).matcher(html);
                    if (matcher.find()) {
                        productId = Long.parseLong(matcher.group(1));
                    }
                    matcher = Pattern.compile(regex3).matcher(html);
                    if (matcher.find()) {
                        productId = Long.parseLong(matcher.group(1));
                    }
                    matcher = Pattern.compile(regex4).matcher(html);
                    if (matcher.find()) {
                        productId = Long.parseLong(matcher.group(1));
                    }

                    if (productId <= 0L) {
                        log.error("get xiaomi productId error, commodityId: {}, url: {}", commodityId, url);

                        String location = document.location();
                        Matcher locationMatcher = Pattern.compile("https://item\\.mi\\.com/product/(\\d+)\\.html").matcher(location);
                        if (locationMatcher.find()) {
                            productId = Long.parseLong(locationMatcher.group(1));
                        } else {
                            try {
                                URL url1 = new URL(location);
                                String targetUrl = new StringBuilder()
                                        .append(url1.getProtocol())
                                        .append("://")
                                        .append(url1.getHost())
                                        .append(url1.getPath()).toString();
                                url = new StringBuilder()
                                        .append("https://order.mi.com/product/gettabinfo?url=")
                                        .append(URLEncoder.encode(targetUrl, "UTF-8"))
                                        .append("&_=")
                                        .append(System.currentTimeMillis()).toString();
                                Connection connection = Jsoup.connect(url).header("Referer", targetUrl).ignoreContentType(true);
                                Document document1 = ProxyService.jsoupGet(connection, "\"msg\":\"ok\"");
                                if (document1 == null) {
                                    log.error("get xiaomi productId error, commodityId: {}, url: {}", commodityId, url);
                                    return productId;
                                }
                                Matcher matcher1 = Pattern.compile("\"product_id\":(\\d+),").matcher(document1.html());
                                if (matcher1.find()) {
                                    productId = Long.parseLong(matcher1.group(1));
                                } else {
                                    log.error("get xiaomi productId error, commodityId: {}, url: {}", commodityId, url);
                                }
                            } catch (Exception e) {
                                log.error("get xiaomi productId error, commodityId: {}, url: {}", commodityId, url);
                            }
                        }
                    }

                    if (productId > 0) {
                        xm.update(ITEM).set(ITEM.PRODUCT_ID, productId).where(ITEM.COMMODITY_ID.eq(commodityId)).execute();
                    }
                    return productId;
                }, fixedThreadPool).thenAcceptAsync(productId -> {
                    if (productId == 0) {
                        return;
                    }
                    String referer = "https://item.mi.com/comment/{}.html".replace("{}", "" + productId);
                    String url = new StringBuilder().append("https://comment.huodong.mi.com/comment/entry/getSummary?goods_id=")
                            .append(productId)
                            .append("&v_pid=")
                            .append(productId)
                            .append("&sstart=0&slen=10&astart=0&alen=")
                            .append(10)
                            .append("&_=")
                            .append(System.currentTimeMillis()).toString();
                    Connection connection = Jsoup.connect(url).header("Referer", referer).ignoreContentType(true);
                    Document document = ProxyService.jsoupGet(connection, "\"msg\":\"ok\"");
                    if (document == null) {
                        return;
                    }

                    Matcher matcher = Pattern.compile("\"total_count\":(\\d+),").matcher(document.html());
                    if (matcher.find()) {
                        int comment_num = Integer.parseInt(matcher.group(1));
                        xm.update(ITEM).set(ITEM.COMMENT_NUM, comment_num).where(ITEM.PRODUCT_ID.eq(productId)).execute();
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });

        fixedThreadPool.shutdown();
        log.info("crawl xiaomi productId end {}", new Date());
    }
}