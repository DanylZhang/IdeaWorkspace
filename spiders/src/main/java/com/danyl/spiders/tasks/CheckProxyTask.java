package com.danyl.spiders.tasks;

import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import com.danyl.spiders.service.ProxyService;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.HttpProtocolConstants.HTTPS;
import static com.danyl.spiders.constants.TimeConstants.MINUTES;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Component
public class CheckProxyTask {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    // 校验可用的代理
    @Scheduled(fixedDelay = MINUTES * 30)
    public void validateProxy() {
        ImmutableMap<String, String> validateUrlMap = ImmutableMap.<String, String>builder()
                .put("http://category.dangdang.com/cid4002389.html", "帆布鞋")
                .put("https://www.vip.com/", "ADS\\w{5}")
                .build();
        checkProxy(validateUrlMap);
    }

    private void checkProxy(Map<String, String> map) {
        log.info("check proxy start {}", new Date());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(256);
        proxy.selectFrom(PROXY)
                .fetch()
                .stream()
                .map(proxyRecord -> CompletableFuture.runAsync(() -> {
                    Pair<Boolean, Integer> validateResultPair = ImmutablePair.of(false, TIMEOUT);
                    // 经测试发现高质量的代理极其稀少
                    // 对每个校验Url进行测试，有一个校验成功就算该代理可用
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String url = entry.getKey();
                        String regex = entry.getValue();
                        Pair<Boolean, Integer> tmpPair = doCheckProxy(proxyRecord.getIp(), proxyRecord.getPort(), url, regex, TIMEOUT);
                        if (tmpPair.getLeft()) {
                            // 如何tmpPair是校验成功的则赋值给validateResultPair,以便更新代理的speed
                            validateResultPair = tmpPair;
                            // 如果当前校验的Url是https协议，则更新代理的type为https
                            String urlProtocol = ProxyService.getUrlProtocol(url);
                            if (HTTPS.equals(urlProtocol)) {
                                proxyRecord.setType(urlProtocol);
                            }
                        }
                    }
                    if (validateResultPair.getLeft()) {
                        proxyRecord.setIsValid(true);
                        proxyRecord.setSpeed(validateResultPair.getRight());
                        proxyRecord.setComment(getProxyComment(proxyRecord));
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
     * @param ip    要检查的代理 ip
     * @param port  要检查的代理 port
     * @param url   通过此Url校验代理连通性
     * @param regex 校验正则表达式
     */
    public static Pair<Boolean, Integer> doCheckProxy(String ip, Integer port, String url, String regex, Integer timeout) {
        long start = System.currentTimeMillis();
        try {
            Connection connection = Jsoup.connect(url)
                    .referrer(url)
                    .timeout(timeout)
                    .proxy(ip, port)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            Connection.Response response = connection.execute();
            long end = System.currentTimeMillis();
            int costTime = (int) (end - start);
            // 超过 timeout就算超时
            if (costTime > timeout) {
                return Pair.of(false, costTime);
            }

            String res = response.body();
            if (Pattern.compile(regex).matcher(res).find()) {
                return Pair.of(true, costTime);
            }
        } catch (Exception ignored) {
        }
        return Pair.of(false, Integer.MAX_VALUE);
    }

    /**
     * @param proxy 要检查的代理
     * @param url   通过此url测试连通性
     * @param regex 校验正则表达式
     */
    @Deprecated
    private static Pair<Boolean, Integer> doCheckProxyOld(Proxy proxy, String url, String regex) {
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
            if (costTime > TIMEOUT) {
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

    /**
     * 对有效代理更新地域信息
     *
     * @param proxyRecord ProxyRecord
     * @return proxy's comment
     */
    private static String getProxyComment(ProxyRecord proxyRecord) {
        String result = proxyRecord.getComment();
        try {
            String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + proxyRecord.getIp();
            String regex = "\"ip\":\"" + proxyRecord.getIp() + "\"";
            // 如果是返回json字符串，不能用jsoup parse解析，会自动带上html标签
            String ipJson = ProxyService.jsoupExecute(url, regex).body();

            DocumentContext parse = JsonPath.parse(ipJson);
            String country = parse.read("$.data.country");
            String region = parse.read("$.data.region");
            String city = parse.read("$.data.city");
            String isp = parse.read("$.data.isp");

            String comment = "".concat(country.equals("XX") ? "" : country)
                    .concat(region.equals("XX") ? "" : region)
                    .concat(city.equals("XX") ? "" : city)
                    .concat(isp.equals("XX") ? "" : isp);
            if (Strings.isNotBlank(comment)) {
                result = comment;
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}