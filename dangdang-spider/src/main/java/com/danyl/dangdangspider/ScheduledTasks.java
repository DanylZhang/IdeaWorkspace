package com.danyl.dangdangspider;

import com.danyl.dangdangspider.jooq.gen.dangdang.tables.pojos.ItemCategory;
import com.danyl.dangdangspider.jooq.gen.dangdang.tables.records.ItemCategoryRecord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jooq.DSLContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.danyl.dangdangspider.jooq.gen.dangdang.Tables.ITEM_CATEGORY;

@Slf4j
@EnableScheduling
@EnableAsync
@Component
public class ScheduledTasks {
    public static final int SECONDS = 1000;
    public static final int DAYS = 1000 * 60 * 60 * 24;
    public static final int limit = 5; // Integer.MAX_VALUE

    @Autowired
    @Qualifier("DSLContextDangDang")
    private DSLContext dd;

    @Autowired
    @Qualifier("DSLContextProxy")
    private DSLContext proxy;

    @Scheduled(fixedDelay = 3 * DAYS)
    public void fixedRateJob() {
        log.info("category do spider time {}", new Date());

        cid();
    }

    public void cid() {
        lv1Cid();
        lv2Cid();
        lv3Cid();
        lv4Cid();
        lv5Cid();
    }

    public void lv1Cid() {
        String url = "http://category.dangdang.com/?ref=www-0-C";
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 符合这个模式的都会被挑选出来 http://category.dangdang.com/cid4003471.html
        Pattern pattern = Pattern.compile("https?://category\\.dangdang\\.com/cid\\d+\\.html");
        document.select("a")
                .eachAttr("abs:href")
                .stream()
                .filter(href -> {
                    href = href.trim();
                    return pattern.matcher(href).matches();
                })
                .distinct()
                .limit(limit)
                .map((href) -> {
                    Document document1 = null;
                    String lv1link = "";
                    try {
                        System.out.println(href);
                        document1 = Jsoup.connect(href).get();
                        lv1link = document1.select("#breadcrumb > div > a.a.diff").first().attr("abs:href");
                        checkSleep();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return lv1link;
                })
                .distinct()
                .forEach((lv1link) -> {
                    System.out.println(lv1link);

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = null;
                    try {
                        document2 = Jsoup.connect(lv1link).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Element a = document2.select("#breadcrumb > div > a.a.diff").first();

                    // lv1cid
                    String href = a.attr("abs:href");
                    Pattern cidPattern = Pattern.compile("https?://category\\.dangdang\\.com/cid(\\d+)\\.html");
                    Matcher matcher = cidPattern.matcher(href);
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
                });
    }

    public void lv2Cid() {
        final List<ItemCategory> lv1Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(1)).fetch().into(ItemCategory.class);
        lv1Categories.stream()
                .limit(limit)
                .flatMap(lv1Category -> {
                    Integer lv1CategoryCid = lv1Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv1CategoryCid.toString());
                    Document document = null;
                    try {
                        document = Jsoup.connect(url).get();
                        Boolean hasChild = document.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? true : false;
                        if (hasChild) {
                            return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv2link -> new MutablePair<String, ItemCategory>(lv2link, lv1Category));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv2link_lv1Category) -> {
                    String lv2link = lv2link_lv1Category.getLeft();
                    ItemCategory lv1Category = lv2link_lv1Category.getRight();
                    System.out.println(lv2link);

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = null;
                    try {
                        document2 = Jsoup.connect(lv2link).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Element a = document2.select("#breadcrumb > div > div > a").first();

                    // lv2cid
                    String href = a.attr("abs:href");
                    Pattern cidPattern = Pattern.compile("https?://category\\.dangdang\\.com/cid(\\d+)\\.html");
                    Matcher matcher = cidPattern.matcher(href);
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
                });
    }

    public void lv3Cid() {
        final List<ItemCategory> lv2Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(2)).fetch().into(ItemCategory.class);
        lv2Categories.stream()
                .limit(limit)
                .flatMap(lv2Category -> {
                    Integer lv2CategoryCid = lv2Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv2CategoryCid.toString());
                    Document document = null;
                    try {
                        document = Jsoup.connect(url).get();
                        Boolean hasChild = document.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? true : false;
                        if (hasChild) {
                            return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv3link -> new MutablePair<String, ItemCategory>(lv3link, lv2Category));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv3link_lv2Category) -> {
                    String lv3link = lv3link_lv2Category.getLeft();
                    ItemCategory lv2Category = lv3link_lv2Category.getRight();
                    System.out.println(lv3link);

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = null;
                    try {
                        document2 = Jsoup.connect(lv3link).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Element a = document2.select("#breadcrumb > div > div:nth-child(7) > a").first();
                    // lv3cid
                    String href = a.attr("abs:href");
                    Pattern cidPattern = Pattern.compile("https?://category\\.dangdang\\.com/cid(\\d+)\\.html");
                    Matcher matcher = cidPattern.matcher(href);
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
                });
    }

    public void lv4Cid() {
        final List<ItemCategory> lv3Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(3)).fetch().into(ItemCategory.class);
        lv3Categories.stream()
                .limit(limit)
                .flatMap(lv3Category -> {
                    Integer lv3CategoryCid = lv3Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv3CategoryCid.toString());
                    Document document = null;
                    try {
                        document = Jsoup.connect(url).get();
                        Boolean hasChild = document.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? true : false;
                        if (hasChild) {
                            return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv4link -> new MutablePair<String, ItemCategory>(lv4link, lv3Category));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv4link_lv3Category) -> {
                    String lv4link = lv4link_lv3Category.getLeft();
                    ItemCategory lv3Category = lv4link_lv3Category.getRight();
                    System.out.println(lv4link);

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = null;
                    try {
                        document2 = Jsoup.connect(lv4link).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Element a = document2.select("#breadcrumb > div > div:nth-child(9) > a").first();
                    // lv4cid
                    String href = a.attr("abs:href");
                    Pattern cidPattern = Pattern.compile("https?://category\\.dangdang\\.com/cid(\\d+)\\.html");
                    Matcher matcher = cidPattern.matcher(href);
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
                });
    }

    public void lv5Cid() {
        final List<ItemCategory> lv4Categories = dd.selectFrom(ITEM_CATEGORY).where(ITEM_CATEGORY.LEVEL.eq(4)).fetch().into(ItemCategory.class);
        lv4Categories.stream()
                .limit(limit)
                .flatMap(lv4Category -> {
                    Integer lv4CategoryCid = lv4Category.getCid();
                    String url = "http://category.dangdang.com/cid{}.html".replace("{}", lv4CategoryCid.toString());
                    Document document = null;
                    try {
                        document = Jsoup.connect(url).get();
                        Boolean hasChild = document.select("#navigation > ul > li:nth-child(1) > div.list_left").attr("title").equals("分类") ? true : false;
                        if (hasChild) {
                            return document.select("#navigation > ul > li:nth-child(1) > div.list_right > div.list_content.fix_list > div > span > a").eachAttr("abs:href").stream().map(lv5link -> new MutablePair<String, ItemCategory>(lv5link, lv4Category));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                })
                .distinct()
                .limit(limit)
                .forEach((lv5link_lv4Category) -> {
                    String lv5link = lv5link_lv4Category.getLeft();
                    ItemCategory lv4Category = lv5link_lv4Category.getRight();
                    System.out.println(lv5link);

                    ItemCategory itemCategory = new ItemCategory();

                    Document document2 = null;
                    try {
                        document2 = Jsoup.connect(lv5link).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Element a = document2.select("#breadcrumb > div > div:nth-child(11) > a").first();
                    // lv5cid
                    String href = a.attr("abs:href");
                    Pattern cidPattern = Pattern.compile("https?://category\\.dangdang\\.com/cid(\\d+)\\.html");
                    Matcher matcher = cidPattern.matcher(href);
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
                });
    }

    public void testProxy(String proxy) {
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(proxy);
        matcher.find();
        String ip = matcher.group(1);
        String port = matcher.group(2);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port))))
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        String url = "https://ip.awk.sh/api.php?type=json";
        Request request = new Request.Builder().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String res = response.body().string();
            System.out.println(res);
            Map<String, String> map = new Gson().fromJson(res, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println(map.get("ip"));
            System.out.println(map.get("addr"));
            System.out.println(port);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private static int count = 0;

    static boolean checkSleep() {
        count++;
        if (count % 10 == 0) {
            try {
                int sleep = RandomUtils.nextInt(15, 25);
                log.info("count:{}, sleep:{}", count, sleep);
                Thread.sleep(sleep * 1000);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
