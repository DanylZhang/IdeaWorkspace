package com.danyl.spiders.tasks;

import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.TimeConstants.HOURS;
import static com.danyl.spiders.constants.TimeConstants.MINUTES;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;
import static java.util.stream.Collectors.toList;

@Slf4j
@EnableScheduling
@Component
public class CheckProxyTask {

    @Autowired
    @Qualifier("DSLContextProxy")
    private DSLContext proxy;

    // 校验当当网可用的代理
    @Scheduled(fixedDelay = MINUTES * 30)
    public void ddCheckProxy() {
        String url = "http://category.dangdang.com/cid4002389.html";
        String regex = "帆布鞋";
        checkProxy(url, regex);
    }

    // 校验唯品会可用的代理
    @Scheduled(fixedDelay = MINUTES * 30)
    public void vipCheckProxy() {
        String url = "https://www.vip.com/";
        String regex = "ADS\\w{5}";
        checkProxy(url, regex);
    }

    // 更新可用代理的comment位置信息
    @Scheduled(fixedDelay = HOURS)
    public void updateProxyComment() {
        log.info("update proxy's comment start {}", new Date());

        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(1000, 3000, MINUTES, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000000, true), (r, executor) -> log.error("too many proxy,drop it!"));
        Result<ProxyRecord> proxyRecords = proxy.selectFrom(PROXY)
                .where(PROXY.IS_VALID.eq(true)).fetch();
        List<ProxyRecord> collect = proxyRecords.stream()
                .map(proxyRecord -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // 对有效代理更新地域信息
                        String ipJson = Jsoup.connect("http://ip.taobao.com/service/getIpInfo.php?ip=" + proxyRecord.getIp())
                                .proxy(proxyRecord.getIp(), proxyRecord.getPort())
                                .ignoreContentType(true)
                                .execute().body();
                        DocumentContext parse = JsonPath.parse(ipJson);
                        String country = parse.read("$.data.country");
                        String region = parse.read("$.data.region");
                        String city = parse.read("$.data.city");
                        String isp = parse.read("$.data.isp");
                        String comment = country.concat(region.equals("XX") ? "" : region)
                                .concat(city.equals("XX") ? "" : city)
                                .concat(isp.equals("XX") ? "" : isp);
                        proxyRecord.setComment(comment);
                    } catch (Exception e) {
                        log.error("get proxy comment error, ip: {}, msg:{}", proxyRecord.getIp(), e.getMessage());
                    }
                    return proxyRecord;
                }, customExecutor))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(toList());
        proxy.batchUpdate(collect).execute();
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
                log.error("validate proxy failed: Proxy have unexpected response");
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

    public void checkProxy(String url, String regex) {
        log.info("check proxy start {}", new Date());

        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(1000, 3000, MINUTES, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000000, true), (r, executor) -> log.error("too many proxy validate,drop it!"));
        Result<ProxyRecord> proxyRecords = proxy.selectFrom(PROXY).fetch();
        List<ProxyRecord> collect = proxyRecords.parallelStream()
                .map(proxyRecord -> CompletableFuture.supplyAsync(() -> {
                    final Proxy proxy1 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyRecord.getIp(), proxyRecord.getPort()));
                    Pair<Boolean, Integer> validateResult = doCheckProxy(proxy1, url, regex);
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
}