package com.danyl.spiders.tasks;

import com.danyl.spiders.downloader.JsoupDownloader;
import com.danyl.spiders.jooq.gen.xiaomi.tables.pojos.ItemCategory;
import com.danyl.spiders.jooq.gen.xiaomi.tables.records.ItemCategoryRecord;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.danyl.spiders.constants.Constants.XIAOMI_PAGESIZE;
import static com.danyl.spiders.constants.TimeConstants.DAYS;
import static com.danyl.spiders.jooq.gen.xiaomi.Tables.ITEM_CATEGORY;

@Slf4j
@Component
public class XiaoMiCidTask {

    @Resource(name = "DSLContextXiaoMi")
    private DSLContext xm;

    // 测试节流用
    private int limit = Integer.MAX_VALUE; // 3;

    // 符合这个模式的都会被挑选出来 ^https://list.mi.com/1$
    private Pattern pattern = Pattern.compile("^https?://list\\.mi\\.com/([1-9]\\d*)$");

    @Scheduled(fixedDelay = DAYS * 7)
    public void crawlXiaoMiCid() {
        log.info("crawl xiaomi cid start {}", new Date());
        lv1Cid();
        lv2Cid();
        lv3Cid();
        lv4Cid();
        lv5Cid();
        log.info("crawl xiaomi cid end {}", new Date());
    }

    private void lv1Cid() {
        String startUrl = "https://list.mi.com/0";
        Document document = JsoupDownloader.jsoupGet(startUrl, "所有商品");

        if (document == null) {
            log.error("lv1Cid document is null!");
            return;
        }

        getCidATag(document)
                .map(a -> a.attr("abs:href"))
                .distinct()
                .limit(limit)
                .forEach((lv1link) -> {
                    ItemCategory itemCategory = new ItemCategory();
                    // lv1cid
                    Matcher matcher = pattern.matcher(lv1link);
                    if (matcher.find()) {
                        int lv1cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv1cid);
                        itemCategory.setParentCid(0);
                        itemCategory.setTopParentCid(lv1cid);
                        itemCategory.setLv1cid(lv1cid);
                    }

                    log.info("crawl url: {}", lv1link);
                    Document document1 = JsoupDownloader.jsoupGet(lv1link, "所有商品");
                    Element span = document1.select("div.breadcrumbs > div.container > span:last-child").first();
                    if (Objects.isNull(span)) {
                        return;
                    }

                    // lv1name
                    String name = span.text();
                    itemCategory.setName(name);
                    itemCategory.setFullName(name);
                    itemCategory.setLv1name(name);

                    // item_count
                    boolean morePage = document1.select("div.xm-pagenavi").size() > 0;
                    if (morePage) {
                        int page = document1.select("div.xm-pagenavi > a").size();
                        itemCategory.setItemCount(page * XIAOMI_PAGESIZE);
                    } else {
                        Integer itemCount = document1.select("div.container > div.goods-list-box > div.goods-list > div.goods-item").size();
                        itemCategory.setItemCount(itemCount);
                    }

                    // is_parent
                    Elements dt = document1.select("div.container > div.filter-box > div.filter-list-wrap > dl.filter-list > dt");
                    if ((dt.size() > 0 && dt.text().contains("分类"))) {
                        itemCategory.setIsParent(1);
                    } else {
                        itemCategory.setIsParent(0);
                    }
                    // level
                    itemCategory.setLevel(1);

                    ItemCategoryRecord itemCategoryRecord = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        xm.executeUpdate(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        xm.executeInsert(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                });
    }

    private void lv2Cid() {
        final List<ItemCategory> lv1Categories = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(1)).fetch().into(ItemCategory.class);
        lv1Categories.stream()
                .limit(limit)
                .flatMap(lv1Category -> {
                    Integer lv1CategoryCid = lv1Category.getCid();
                    String url = "https://list.mi.com/{}".replace("{}", lv1CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "所有商品");
                    if (hasChild(document)) {
                        return getCidATag(document)
                                .map(a -> new MutablePair<>(a.attr("abs:href"), lv1Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv2link_lv1Category) -> {
                    String lv2link = lv2link_lv1Category.getLeft();
                    ItemCategory lv1Category = lv2link_lv1Category.getRight();

                    if (!pattern.matcher(lv2link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();
                    // lv2cid
                    Matcher matcher = pattern.matcher(lv2link);
                    if (matcher.find()) {
                        int lv2cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv2cid);
                        itemCategory.setParentCid(lv1Category.getCid());
                        itemCategory.setTopParentCid(lv1Category.getTopParentCid());
                        itemCategory.setLv1cid(lv1Category.getLv1cid());
                        itemCategory.setLv2cid(lv2cid);
                    }

                    log.info("crawl url: {}", lv2link);
                    Document document1 = JsoupDownloader.jsoupGet(lv2link, "所有商品");
                    Element span = document1.select("div.breadcrumbs > div.container > span:last-child").first();
                    if (Objects.isNull(span)) {
                        return;
                    }

                    // lv2name
                    String name = span.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv1Category.getLv1name());
                    itemCategory.setLv2name(name);
                    itemCategory.setFullName(lv1Category.getFullName() + ">" + name);

                    // item_count
                    boolean morePage = document1.select("div.xm-pagenavi").size() > 0;
                    if (morePage) {
                        int page = document1.select("div.xm-pagenavi > a").size();
                        itemCategory.setItemCount(page * XIAOMI_PAGESIZE);
                    } else {
                        Integer itemCount = document1.select("div.container > div.goods-list-box > div.goods-list > div.goods-item").size();
                        itemCategory.setItemCount(itemCount);
                    }

                    // is_parent
                    if (hasChild(document1)) {
                        itemCategory.setIsParent(1);
                    } else {
                        itemCategory.setIsParent(0);
                    }

                    // level
                    itemCategory.setLevel(2);

                    ItemCategoryRecord itemCategoryRecord = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        xm.executeUpdate(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        xm.executeInsert(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                });
    }

    private void lv3Cid() {
        final List<ItemCategory> lv2Categories = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(2)).fetch().into(ItemCategory.class);
        lv2Categories.stream()
                .limit(limit)
                .flatMap(lv2Category -> {
                    Integer lv2CategoryCid = lv2Category.getCid();
                    String url = "https://list.mi.com/{}".replace("{}", lv2CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "所有商品");
                    if (hasChild(document)) {
                        return getCidATag(document)
                                .map(a -> new MutablePair<>(a.attr("abs:href"), lv2Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv3link_lv2Category) -> {
                    String lv3link = lv3link_lv2Category.getLeft();
                    ItemCategory lv2Category = lv3link_lv2Category.getRight();

                    if (!pattern.matcher(lv3link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();
                    // lv3cid
                    Matcher matcher = pattern.matcher(lv3link);
                    if (matcher.find()) {
                        int lv3cid = Integer.parseInt(matcher.group(1));
                        itemCategory.setCid(lv3cid);
                        itemCategory.setParentCid(lv2Category.getCid());
                        itemCategory.setTopParentCid(lv2Category.getTopParentCid());
                        itemCategory.setLv1cid(lv2Category.getLv1cid());
                        itemCategory.setLv2cid(lv2Category.getLv2cid());
                        itemCategory.setLv3cid(lv3cid);
                    }

                    log.info("crawl url: {}", lv3link);
                    Document document1 = JsoupDownloader.jsoupGet(lv3link, "所有商品");
                    Element span = document1.select("div.breadcrumbs > div.container > span:last-child").first();
                    if (Objects.isNull(span)) {
                        return;
                    }

                    // lv3name
                    String name = span.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv2Category.getLv1name());
                    itemCategory.setLv2name(lv2Category.getLv2name());
                    itemCategory.setLv3name(name);
                    itemCategory.setFullName(lv2Category.getFullName() + ">" + name);

                    // item_count
                    boolean morePage = document1.select("div.xm-pagenavi").size() > 0;
                    if (morePage) {
                        int page = document1.select("div.xm-pagenavi > a").size();
                        itemCategory.setItemCount(page * XIAOMI_PAGESIZE);
                    } else {
                        Integer itemCount = document1.select("div.container > div.goods-list-box > div.goods-list > div.goods-item").size();
                        itemCategory.setItemCount(itemCount);
                    }

                    // is_parent
                    if (hasChild(document1)) {
                        itemCategory.setIsParent(1);
                    } else {
                        itemCategory.setIsParent(0);
                    }

                    // level
                    itemCategory.setLevel(3);

                    ItemCategoryRecord itemCategoryRecord = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        xm.executeUpdate(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        xm.executeInsert(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                });
    }

    private void lv4Cid() {
        final List<ItemCategory> lv3Categories = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(3)).fetch().into(ItemCategory.class);
        lv3Categories.stream()
                .limit(limit)
                .flatMap(lv3Category -> {
                    Integer lv3CategoryCid = lv3Category.getCid();
                    String url = "https://list.mi.com/{}".replace("{}", lv3CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "所有商品");
                    if (hasChild(document)) {
                        return getCidATag(document)
                                .map(a -> new MutablePair<>(a.attr("abs:href"), lv3Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv4link_lv3Category) -> {
                    String lv4link = lv4link_lv3Category.getLeft();
                    ItemCategory lv3Category = lv4link_lv3Category.getRight();

                    if (!pattern.matcher(lv4link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();
                    // lv4cid
                    Matcher matcher = pattern.matcher(lv4link);
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

                    log.info("crawl url: {}", lv4link);
                    Document document1 = JsoupDownloader.jsoupGet(lv4link, "所有商品");
                    Element span = document1.select("div.breadcrumbs > div.container > span:last-child").first();
                    if (Objects.isNull(span)) {
                        return;
                    }

                    // lv4name
                    String name = span.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv3Category.getLv1name());
                    itemCategory.setLv2name(lv3Category.getLv2name());
                    itemCategory.setLv3name(lv3Category.getLv3name());
                    itemCategory.setLv4name(name);
                    itemCategory.setFullName(lv3Category.getFullName() + ">" + name);

                    // item_count
                    boolean morePage = document1.select("div.xm-pagenavi").size() > 0;
                    if (morePage) {
                        int page = document1.select("div.xm-pagenavi > a").size();
                        itemCategory.setItemCount(page * XIAOMI_PAGESIZE);
                    } else {
                        Integer itemCount = document1.select("div.container > div.goods-list-box > div.goods-list > div.goods-item").size();
                        itemCategory.setItemCount(itemCount);
                    }

                    // is_parent
                    if (hasChild(document1)) {
                        itemCategory.setIsParent(1);
                    } else {
                        itemCategory.setIsParent(0);
                    }

                    // level
                    itemCategory.setLevel(4);

                    ItemCategoryRecord itemCategoryRecord = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        xm.executeUpdate(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        xm.executeInsert(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                });
    }

    private void lv5Cid() {
        final List<ItemCategory> lv4Categories = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(4)).fetch().into(ItemCategory.class);
        lv4Categories.stream()
                .limit(limit)
                .flatMap(lv4Category -> {
                    Integer lv4CategoryCid = lv4Category.getCid();
                    String url = "https://list.mi.com/{}".replace("{}", lv4CategoryCid.toString());
                    Document document = JsoupDownloader.jsoupGet(url, "所有商品");
                    if (hasChild(document)) {
                        return getCidATag(document)
                                .map(a -> new MutablePair<>(a.attr("abs:href"), lv4Category));
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv5link_lv4Category) -> {
                    String lv5link = lv5link_lv4Category.getLeft();
                    ItemCategory lv4Category = lv5link_lv4Category.getRight();

                    if (!pattern.matcher(lv5link).find()) {
                        return;
                    }

                    ItemCategory itemCategory = new ItemCategory();
                    // lv5cid
                    Matcher matcher = pattern.matcher(lv5link);
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

                    log.info("crawl url: {}", lv5link);
                    Document document1 = JsoupDownloader.jsoupGet(lv5link, "所有商品");
                    Element span = document1.select("div.breadcrumbs > div.container > span:last-child").first();
                    if (Objects.isNull(span)) {
                        return;
                    }

                    // lv5name
                    String name = span.text();
                    itemCategory.setName(name);
                    itemCategory.setLv1name(lv4Category.getLv1name());
                    itemCategory.setLv2name(lv4Category.getLv2name());
                    itemCategory.setLv3name(lv4Category.getLv3name());
                    itemCategory.setLv4name(lv4Category.getLv4name());
                    itemCategory.setLv5name(name);
                    itemCategory.setFullName(lv4Category.getFullName() + ">" + name);

                    // item_count
                    boolean morePage = document1.select("div.xm-pagenavi").size() > 0;
                    if (morePage) {
                        int page = document1.select("div.xm-pagenavi > a").size();
                        itemCategory.setItemCount(page * XIAOMI_PAGESIZE);
                    } else {
                        Integer itemCount = document1.select("div.container > div.goods-list-box > div.goods-list > div.goods-item").size();
                        itemCategory.setItemCount(itemCount);
                    }

                    // is_parent
                    if (hasChild(document1)) {
                        itemCategory.setIsParent(1);
                    } else {
                        itemCategory.setIsParent(0);
                    }

                    // level
                    itemCategory.setLevel(5);

                    ItemCategoryRecord itemCategoryRecord = xm.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.CID.eq(itemCategory.getCid())).fetchOne();
                    if (itemCategoryRecord != null) {
                        xm.executeUpdate(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    } else {
                        xm.executeInsert(xm.newRecord(ITEM_CATEGORY, itemCategory));
                    }
                });
    }

    private Boolean hasChild(Document document) {
        Elements dt = document.select("div.container > div.filter-box > div.filter-list-wrap > dl.filter-list > dt");
        if ((dt.size() > 0 && dt.text().contains("分类"))) {
            String categoryName = document.select("body > div.breadcrumbs > div > span:last-child").text();
            // 小米官网二级类目到底，如果下方分类中包含面包屑中的最后一个类目，则认为没有子类目
            return !document.select("div.container > div.filter-box > div.filter-list-wrap > dl.filter-list > dd").text().contains(categoryName);
        }
        return false;
    }

    private Stream<Element> getCidATag(Document document) {
        return document.select("div.filter-box dl.filter-list > dd > a")
                .stream()
                .filter(a -> {
                    String text = a.text();
                    String href = a.attr("abs:href");
                    href = href.trim();
                    // 小米分类里总有"全部"在捣乱
                    if (text.equals("全部")) {
                        return false;
                    }
                    return pattern.matcher(href).find();
                });
    }
}