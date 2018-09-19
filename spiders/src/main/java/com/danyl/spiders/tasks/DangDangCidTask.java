package com.danyl.spiders.tasks;

import com.danyl.spiders.downloader.JsoupDownloader;
import com.danyl.spiders.jooq.gen.dangdang.tables.pojos.ItemCategory;
import com.danyl.spiders.jooq.gen.dangdang.tables.records.ItemCategoryRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jooq.DSLContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.danyl.spiders.constants.TimeConstants.DAYS;
import static com.danyl.spiders.jooq.gen.dangdang.Tables.ITEM_CATEGORY;

@Slf4j
@Component
public class DangDangCidTask {

    @Resource(name = "DSLContextDangDang")
    private DSLContext dd;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);

    // 测试节流用
    private int limit = Integer.MAX_VALUE; // 3;

    // 符合这个模式的都会被挑选出来 http://category.dangdang.com/cid4003471.html
    private Pattern pattern = Pattern.compile("https?://category\\.dangdang\\.com/cid(\\d+)\\.html");

    @Scheduled(fixedDelay = DAYS * 3)
    public void crawlDangDangCid() {
        log.info("crawl dangdang cid start {}", new Date());
        lv1Cid();
        lv2Cid();
        lv3Cid();
        lv4Cid();
        lv5Cid();
        log.info("crawl dangdang cid end {}", new Date());
    }

    private void lv1Cid() {
        String startUrl = "http://category.dangdang.com/?ref=www-0-C";
        Document document = JsoupDownloader.jsoupGet(startUrl, "全部商品分类");
        if (document == null) {
            log.error("lv1Cid document is null!");
            return;
        }

        document.select("a")
                .eachAttr("abs:href")
                .stream()
                .filter(href -> {
                    href = href.trim();
                    return pattern.matcher(href).find();
                })
                .distinct()
                .limit(limit)
                .flatMap(href -> {
                    Document document1 = JsoupDownloader.jsoupGet(href, "id=\"breadcrumb\"");
                    if (document1 == null) {
                        log.error("lv1Cid foreach jsoupGet document is null, url: {}", href);
                        return Stream.empty();
                    } else {
                        Elements elements = document1.select("#breadcrumb > div > a.a.diff");
                        if (elements.size() > 0) {
                            return Stream.of(elements.first().attr("abs:href"));
                        } else {
                            log.error("lv1Cid #breadcrumb get null, url: {}, html: {}", href, document1.html());
                            return Stream.empty();
                        }
                    }
                })
                .distinct()
                .map(lv1link -> CompletableFuture.runAsync(() -> {
                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = JsoupDownloader.jsoupGet(lv1link, "全部商品分类");
                    if (document2 == null) {
                        return;
                    }
                    Element a = document2.select("#breadcrumb > div > a.a.diff").first();
                    if (Objects.isNull(a)) {
                        return;
                    }

                    // lv1cid
                    String href = a.attr("abs:href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        int lv1cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv1cid);
                        itemCategory.setParentCid(0);
                        itemCategory.setTopParentCid(lv1cid);
                        itemCategory.setLv1cid(lv1cid);
                    }

                    // lv1name
                    String name = a.text();
                    itemCategory.setName(name);
                    itemCategory.setFullName(name);
                    itemCategory.setLv1name(name);

                    // item_count
                    Integer itemCount = Integer.parseInt(document2.select("#breadcrumb > div > span.sp.total > em").first().text());
                    itemCategory.setItemCount(itemCount);

                    // is_parent
                    int isParent = document2.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? 1 : 0;
                    itemCategory.setIsParent(isParent);

                    // level
                    itemCategory.setLevel(1);

                    ItemCategoryRecord itemCategoryRecord = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        dd.executeUpdate(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        dd.executeInsert(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });
    }

    private void lv2Cid() {
        final List<ItemCategory> lv1Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(1)).fetch().into(ItemCategory.class);
        lv1Categories.stream()
                .limit(limit)
                .flatMap(lv1Category -> {
                    Integer lv1CategoryCid = lv1Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv1CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "全部商品分类");
                    if (hasChild(document)) {
                        return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv2link -> new MutablePair<>(lv2link, lv1Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .map(lv2link_lv1Category -> CompletableFuture.runAsync(() -> {
                    String lv2link = lv2link_lv1Category.getLeft();
                    ItemCategory lv1Category = lv2link_lv1Category.getRight();

                    if (!pattern.matcher(lv2link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = JsoupDownloader.jsoupGet(lv2link, "全部商品分类");
                    if (document2 == null) {
                        return;
                    }
                    Element a = document2.select("#breadcrumb > div > div > a").first();
                    if (Objects.isNull(a)) {
                        return;
                    }

                    // lv2cid
                    String href = a.attr("abs:href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        int lv2cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv2cid);
                        itemCategory.setParentCid(lv1Category.getCid());
                        itemCategory.setTopParentCid(lv1Category.getTopParentCid());
                        itemCategory.setLv1cid(lv1Category.getLv1cid());
                        itemCategory.setLv2cid(lv2cid);
                    }

                    // lv2name
                    String name = a.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv1Category.getLv1name());
                    itemCategory.setLv2name(name);
                    itemCategory.setFullName(lv1Category.getFullName() + ">" + name);

                    // item_count
                    Integer itemCount = Integer.parseInt(document2.select("#breadcrumb > div > span.sp.total > em").first().text());
                    itemCategory.setItemCount(itemCount);

                    // is_parent
                    int isParent = document2.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? 1 : 0;
                    itemCategory.setIsParent(isParent);

                    // level
                    itemCategory.setLevel(2);

                    ItemCategoryRecord itemCategoryRecord = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        dd.executeUpdate(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        dd.executeInsert(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });
    }

    private void lv3Cid() {
        final List<ItemCategory> lv2Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(2)).fetch().into(ItemCategory.class);
        lv2Categories.stream()
                .limit(limit)
                .flatMap(lv2Category -> {
                    Integer lv2CategoryCid = lv2Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv2CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "全部商品分类");
                    if (hasChild(document)) {
                        return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv3link -> new MutablePair<>(lv3link, lv2Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .map(lv3link_lv2Category -> CompletableFuture.runAsync(() -> {
                    String lv3link = lv3link_lv2Category.getLeft();
                    ItemCategory lv2Category = lv3link_lv2Category.getRight();

                    if (!pattern.matcher(lv3link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = JsoupDownloader.jsoupGet(lv3link, "全部商品分类");
                    if (document2 == null) {
                        return;
                    }
                    Element a = document2.select("#breadcrumb > div > div:nth-child(7) > a").first();
                    if (Objects.isNull(a)) {
                        return;
                    }

                    // lv3cid
                    String href = a.attr("abs:href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        int lv3cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv3cid);
                        itemCategory.setParentCid(lv2Category.getCid());
                        itemCategory.setTopParentCid(lv2Category.getTopParentCid());
                        itemCategory.setLv1cid(lv2Category.getLv1cid());
                        itemCategory.setLv2cid(lv2Category.getLv2cid());
                        itemCategory.setLv3cid(lv3cid);
                    }

                    // lv3name
                    String name = a.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv2Category.getLv1name());
                    itemCategory.setLv2name(lv2Category.getLv2name());
                    itemCategory.setLv3name(name);
                    itemCategory.setFullName(lv2Category.getFullName() + ">" + name);

                    // item_count
                    Integer itemCount = Integer.parseInt(document2.select("#breadcrumb > div > span.sp.total > em").first().text());
                    itemCategory.setItemCount(itemCount);

                    // is_parent
                    int isParent = document2.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? 1 : 0;
                    itemCategory.setIsParent(isParent);

                    // level
                    itemCategory.setLevel(3);

                    ItemCategoryRecord itemCategoryRecord = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        dd.executeUpdate(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        dd.executeInsert(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });
    }

    private void lv4Cid() {
        final List<ItemCategory> lv3Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(3)).fetch().into(ItemCategory.class);
        lv3Categories.stream()
                .limit(limit)
                .flatMap(lv3Category -> {
                    Integer lv3CategoryCid = lv3Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv3CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "全部商品分类");
                    if (hasChild(document)) {
                        return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv4link -> new MutablePair<>(lv4link, lv3Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .map(lv4link_lv3Category -> CompletableFuture.runAsync(() -> {
                    String lv4link = lv4link_lv3Category.getLeft();
                    ItemCategory lv3Category = lv4link_lv3Category.getRight();

                    // 对伪分类进行过滤 http://category.dangdang.com/cid4010390-a1000367%3A2.html
                    if (!pattern.matcher(lv4link).find()) {
                        return;
                    }
                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = JsoupDownloader.jsoupGet(lv4link, "全部商品分类");
                    if (document2 == null) {
                        return;
                    }
                    Element a = document2.select("#breadcrumb > div > div:nth-child(9) > a").first();
                    if (Objects.isNull(a)) {
                        return;
                    }

                    // lv4cid
                    String href = a.attr("abs:href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        int lv4cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv4cid);
                        itemCategory.setParentCid(lv3Category.getCid());
                        itemCategory.setTopParentCid(lv3Category.getTopParentCid());
                        itemCategory.setLv1cid(lv3Category.getLv1cid());
                        itemCategory.setLv2cid(lv3Category.getLv2cid());
                        itemCategory.setLv3cid(lv3Category.getLv3cid());
                        itemCategory.setLv4cid(lv4cid);
                    }

                    // lv4name
                    String name = a.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv3Category.getLv1name());
                    itemCategory.setLv2name(lv3Category.getLv2name());
                    itemCategory.setLv3name(lv3Category.getLv3name());
                    itemCategory.setLv4name(name);
                    itemCategory.setFullName(lv3Category.getFullName() + ">" + name);

                    // item_count
                    Integer itemCount = Integer.parseInt(document2.select("#breadcrumb > div > span.sp.total > em").first().text());
                    itemCategory.setItemCount(itemCount);

                    // is_parent
                    int isParent = document2.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? 1 : 0;
                    itemCategory.setIsParent(isParent);

                    // level
                    itemCategory.setLevel(4);

                    ItemCategoryRecord itemCategoryRecord = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        dd.executeUpdate(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        dd.executeInsert(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });
    }

    private void lv5Cid() {
        final List<ItemCategory> lv4Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(4)).fetch().into(ItemCategory.class);
        lv4Categories.stream()
                .limit(limit)
                .flatMap(lv4Category -> {
                    Integer lv4CategoryCid = lv4Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv4CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "全部商品分类");
                    if (hasChild(document)) {
                        return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv5link -> new MutablePair<>(lv5link, lv4Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .map(lv5link_lv4Category -> CompletableFuture.runAsync(() -> {
                    String lv5link = lv5link_lv4Category.getLeft();
                    ItemCategory lv4Category = lv5link_lv4Category.getRight();

                    if (!pattern.matcher(lv5link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = JsoupDownloader.jsoupGet(lv5link, "全部商品分类");
                    if (document2 == null) {
                        return;
                    }
                    Element a = document2.select("#breadcrumb > div > div:nth-child(11) > a").first();
                    if (Objects.isNull(a)) {
                        return;
                    }

                    // lv5cid
                    String href = a.attr("abs:href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        int lv5cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv5cid);
                        itemCategory.setParentCid(lv4Category.getCid());
                        itemCategory.setTopParentCid(lv4Category.getTopParentCid());
                        itemCategory.setLv1cid(lv4Category.getLv1cid());
                        itemCategory.setLv2cid(lv4Category.getLv2cid());
                        itemCategory.setLv3cid(lv4Category.getLv3cid());
                        itemCategory.setLv4cid(lv4Category.getLv4cid());
                        itemCategory.setLv5cid(lv5cid);
                    }

                    // lv5name
                    String name = a.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv4Category.getLv1name());
                    itemCategory.setLv2name(lv4Category.getLv2name());
                    itemCategory.setLv3name(lv4Category.getLv3name());
                    itemCategory.setLv4name(lv4Category.getLv4name());
                    itemCategory.setLv5name(name);
                    itemCategory.setFullName(lv4Category.getFullName() + ">" + name);

                    // item_count
                    Integer itemCount = Integer.parseInt(document2.select("#breadcrumb > div > span.sp.total > em").first().text());
                    itemCategory.setItemCount(itemCount);

                    // is_parent
                    int isParent = document2.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? 1 : 0;
                    itemCategory.setIsParent(isParent);

                    // level
                    itemCategory.setLevel(5);

                    ItemCategoryRecord itemCategoryRecord = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        dd.executeUpdate(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        dd.executeInsert(dd.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });
    }

    private Boolean hasChild(Document document) {
        Elements elements = document.select("#navigation > ul > li:nth-child(1) > div.list_left");
        return elements.size() > 0 && elements.attr("title").equals("分类");
    }
}