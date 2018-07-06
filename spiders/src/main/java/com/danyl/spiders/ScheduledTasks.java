package com.danyl.spiders;

import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.tasks.DangDangCategoryTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.TimeConstants.*;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;
import static java.util.stream.Collectors.toList;

@Slf4j
@EnableScheduling
@EnableAsync
@Component
public class ScheduledTasks {

    @Autowired
    @Qualifier("DSLContextProxy")
    private DSLContext proxy;

    @Autowired
    private DangDangCategoryTask dangDangCategoryTask;

    @Scheduled(fixedDelay = DAYS)
    public void crawlCid() {
        log.info("crawl cid start {}", new Date());

        // 解放测试数量限制
        dangDangCategoryTask.setLimit(Integer.MAX_VALUE);
        dangDangCategoryTask.cid();
    }

    @Scheduled(fixedDelay = HOURS * 3)
    public void crawlProxy() {
        log.info("crawl proxy start {}", new Date());

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(this::get66ip);
        executorService.execute(this::getip3366);
        executorService.execute(this::getkuaidaili);
        executorService.execute(this::getxicidaili);
    }

    // 默认校验代理
    @Scheduled(fixedDelay = HOURS)
    public void defaultCheckProxy() {
        log.info("check proxy start {}", new Date());

        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(500, 1000, MINUTES, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000000, true), (r, executor) -> log.error("too many proxy validate,drop it!"));
        Result<ProxyRecord> proxyRecords = proxy.selectFrom(PROXY).fetch();
        List<ProxyRecord> collect = proxyRecords.parallelStream()
                .map(proxyRecord -> CompletableFuture.supplyAsync(() -> {
                    Pair<Boolean, Integer> validateResult = defaultCheckProxy(proxyRecord.getIp(), proxyRecord.getPort());
                    if (validateResult.getLeft()) {
                        proxyRecord.setIsValid(true);
                        proxyRecord.setSpeed(validateResult.getRight());
                        try {
                            // 对有效代理更新地域信息
                            String ipJson = Jsoup.connect("http://ip.taobao.com/service/getIpInfo.php?ip=" + proxyRecord.getIp())
                                    .proxy(proxyRecord.getIp(), proxyRecord.getPort())
                                    .get().text();
                            JSONObject jsonObject = new JSONObject(ipJson);
                            JSONObject data = jsonObject.getJSONObject("data");
                            String country = data.getString("country");
                            String region = data.getString("region");
                            String city = data.getString("city");
                            String isp = data.getString("isp");
                            String comment = country.concat(region.equals("XX") ? "" : region)
                                    .concat(city.equals("XX") ? "" : city)
                                    .concat(isp.equals("XX") ? "" : isp);
                            proxyRecord.setComment(comment);
                        } catch (Exception e) {
                            log.error("get proxy comment error, ip: {}, msg:{}", proxyRecord.getIp(), e.getMessage());
                        }
                    } else {
                        proxyRecord.setIsValid(false);
                    }
                    return proxyRecord;
                }, customExecutor))
                .collect(Collectors.toList())
                .parallelStream()
                .map(CompletableFuture::join)
                .collect(toList());
        proxy.batchUpdate(collect).execute();
        proxy.batchDelete(collect.parallelStream().filter(proxyRecord -> !proxyRecord.getIsValid()).collect(Collectors.toList())).execute();
    }

    // 校验当当网可用的代理
    @Scheduled(fixedDelay = HOURS)
    public void ddCheckProxy() {
        log.info("check proxy start {}", new Date());

        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(500, 1000, MINUTES, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000000, true), (r, executor) -> log.error("too many proxy validate,drop it!"));
        Result<ProxyRecord> proxyRecords = proxy.selectFrom(PROXY).fetch();
        List<ProxyRecord> collect = proxyRecords.parallelStream()
                .map(proxyRecord -> CompletableFuture.supplyAsync(() -> {
                    String url = "http://category.dangdang.com/cid4002389.html";
                    final Proxy proxy1 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyRecord.getIp(), proxyRecord.getPort()));
                    Pair<Boolean, Integer> validateResult = doCheckProxy(proxy1, url, "帆布鞋");
                    if (validateResult.getLeft()) {
                        proxyRecord.setIsValid(true);
                        proxyRecord.setSpeed(validateResult.getRight());
                    } else {
                        proxyRecord.setIsValid(false);
                    }
                    return proxyRecord;
                }, customExecutor))
                .collect(Collectors.toList())
                .parallelStream()
                .map(CompletableFuture::join)
                .collect(toList());
        proxy.batchUpdate(collect).execute();
        proxy.batchDelete(collect.parallelStream().filter(proxyRecord -> !proxyRecord.getIsValid()).collect(Collectors.toList())).execute();
    }

    // 小六代理
    public void get66ip() {
        for (int i = 0; i < 100; i++) {
            // 1. 一次100个 https代理
            String url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=4&start=&ports=&export=&ipaddress=&area=0&proxytype=1&api=66ip";
            String html = ProxyService.getJsoup(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").text();
            Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(html);
            while (matcher.find()) {
                String ip = matcher.group(1);
                int port = Integer.parseInt(matcher.group(2));
                String type = "https";
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE)
                        .values(ip, port, false, type)
                        .onDuplicateKeyIgnore()
                        .execute();
            }

            // 2. 一次100个 http代理
            url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=4&start=&ports=&export=&ipaddress=&area=0&proxytype=0&api=66ip";
            html = ProxyService.getJsoup(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").text();
            matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(html);
            while (matcher.find()) {
                String ip = matcher.group(1);
                int port = Integer.parseInt(matcher.group(2));
                String type = "http";
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE)
                        .values(ip, port, false, type)
                        .onDuplicateKeyIgnore()
                        .execute();
            }
        }
    }

    // 云代理
    public void getip3366() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 10; j++) {
                String url = "http://www.ip3366.net/?stype=" + i + "&page=" + j;
                Document document = ProxyService.getJsoup(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
                document.select("#list > table > tbody > tr")
                        .parallelStream()
                        .skip(1)
                        .forEach(element -> {
                            String ip = element.select("td:nth-child(1)").text().trim();
                            int port = Integer.parseInt(element.select("td:nth-child(2)").text());
                            String type = element.select("td:nth-child(4)").text().toLowerCase();
                            String comment = element.select("td:nth-child(6)").text();

                            proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE, PROXY.COMMENT)
                                    .values(ip, port, false, type, comment)
                                    .onDuplicateKeyIgnore()
                                    .execute();
                        });
            }
        }
    }

    // 快代理
    public void getkuaidaili() {
        for (int i = 1; i < 25; i++) {
            String url = "https://www.kuaidaili.com/free/inha/" + i + "/";
            Document document = ProxyService.getJsoup(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
            document.select("#list > table > tbody > tr")
                    .parallelStream()
                    .forEach(element -> {
                        String ip = element.select("td:nth-child(1)").text();
                        int port = Integer.parseInt(element.select("td:nth-child(2)").text());
                        String type = element.select("td:nth-child(4)").text().toLowerCase();
                        String comment = element.select("td:nth-child(5)").text();
                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE, PROXY.COMMENT)
                                .values(ip, port, false, type, comment)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }
    }

    // 西刺代理
    public void getxicidaili() {
        for (int i = 1; i <= 300; i++) {
            String url = "http://www.xicidaili.com/nn/" + i;
            Document document = ProxyService.getJsoup(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
            document.select("#ip_list > tbody > tr")
                    .parallelStream()
                    .skip(1)
                    .forEach(element -> {
                        String ip = element.select("td:nth-child(2)").text();
                        int port = Integer.parseInt(element.select("td:nth-child(3)").text());
                        String type = element.select("td:nth-child(6)").text().toLowerCase();
                        String comment = element.select("td:nth-child(4)").text();
                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE, PROXY.COMMENT)
                                .values(ip, port, false, type, comment)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }
    }

    /**
     * @param proxy 要检查的代理
     * @param url   通过此url测试连通性
     * @param regex 校验正则表达式
     */
    private static Pair<Boolean, Integer> doCheckProxy(Proxy proxy, String url, String regex) {
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(MINUTES, TimeUnit.MILLISECONDS)
                .readTimeout(MINUTES, TimeUnit.MILLISECONDS)
                .writeTimeout(MINUTES, TimeUnit.MILLISECONDS)
                .proxy(proxy)
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
                .build();
        Request request = new Request.Builder().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            long start = System.currentTimeMillis();
            response = call.execute();
            long end = System.currentTimeMillis();
            int costTime = (int) (end - start);
            if (costTime > MINUTES) {
                return Pair.of(false, costTime);
            }

            String res = response.body().string();
            if (Pattern.compile(regex).matcher(res).find()) {
                return Pair.of(true, costTime);
            } else {
                log.error("validate proxy response failed, proxy: {}, response: {}", proxy.toString(), res);
            }
        } catch (Exception e) {
            log.error("validate proxy failed: {}", e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return Pair.of(false, Integer.MAX_VALUE);
    }

    public static Pair<Boolean, Integer> defaultCheckProxy(String ip, int port) {
        String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
        return doCheckProxy(proxy, url, ip);
    }
}
