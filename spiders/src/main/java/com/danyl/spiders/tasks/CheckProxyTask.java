package com.danyl.spiders.tasks;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.TimeConstants.MINUTES;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Component
public class CheckProxyTask {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    // 校验可用的代理
    @Scheduled(fixedDelay = MINUTES * 5)
    public void checkProxy() {
        ImmutableMap<String, String> validateUrlMap = new ImmutableMap.Builder<String, String>()
                .put("http://category.dangdang.com/cid4002389.html", "帆布鞋")
                .put("https://www.vip.com/", "ADS\\w{5}")
                .build();
        checkProxy(validateUrlMap);
    }

    // 更新可用代理的comment位置信息
    @Scheduled(fixedDelay = MINUTES * 30)
    public void updateProxyComment() {
        log.info("update proxy's comment start {}", new Date());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
        proxy.selectFrom(PROXY)
                .where(PROXY.IS_VALID.eq(true))
                .fetch()
                .stream()
                .map(proxyRecord -> CompletableFuture.runAsync(() -> {
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
                        proxyRecord.update(PROXY.COMMENT);
                    } catch (Exception ignored) {
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });

        // 别忘了关闭局部变量的线程池
        fixedThreadPool.shutdownNow();
        log.info("update proxy's comment end {}", new Date());
    }

    private void checkProxy(Map<String, String> map) {
        log.info("check proxy start {}", new Date());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(256);
        proxy.selectFrom(PROXY)
                .fetch()
                .stream()
                .map(proxyRecord -> CompletableFuture.runAsync(() -> {
                    Proxy proxy1 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyRecord.getIp(), proxyRecord.getPort()));

                    Pair<Boolean, Integer> validateResult = ImmutablePair.of(false, 60000);
                    // 经测试发现高质量的代理极其稀少
                    // 对每个校验Url进行测试，有一个校验成功就算该代理可用
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String url = entry.getKey();
                        String regex = entry.getValue();
                        validateResult = doCheckProxy(proxy1, url, regex);
                        if (validateResult.getLeft()) {
                            break;
                        }
                    }
                    if (validateResult.getLeft()) {
                        proxyRecord.setIsValid(true);
                        proxyRecord.setSpeed(validateResult.getRight());
                        proxyRecord.update();
                    } else {
                        proxyRecord.setIsValid(false);
                        proxyRecord.delete();
                    }
                }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                // 等待所有校验线程执行完毕
                .map(CompletableFuture::join)
                .forEach(aVoid -> {
                });

        // 别忘了关闭局部变量的线程池
        fixedThreadPool.shutdownNow();
        log.info("check proxy end {}", new Date());
    }

    /**
     * @param proxy 要检查的代理
     * @param url   通过此url测试连通性
     * @param regex 校验正则表达式
     */
    private static Pair<Boolean, Integer> doCheckProxy(Proxy proxy, String url, String regex) {
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(proxy)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();
        Request request = new Request.Builder().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .build();
        Call call = okHttpClient.newCall(request);
        long start = System.currentTimeMillis();
        try (Response response = call.execute()) {
            long end = System.currentTimeMillis();
            int costTime = (int) (end - start);
            // 超过半分钟就算超时
            if (costTime > MINUTES / 2) {
                return Pair.of(false, costTime);
            }

            String res = response.body().string();
            if (Pattern.compile(regex).matcher(res).find()) {
                return Pair.of(true, costTime);
            }
        } catch (Exception ignored) {
        }
        return Pair.of(false, Integer.MAX_VALUE);
    }
}