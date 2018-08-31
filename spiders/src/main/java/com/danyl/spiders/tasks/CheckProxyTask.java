package com.danyl.spiders.tasks;

import com.danyl.spiders.downloader.JsoupDownloader;
import com.danyl.spiders.utils.ProxyUtil;
import com.google.common.collect.ImmutableMap;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.Constants.USERAGENT;
import static com.danyl.spiders.constants.ProtocolConstants.HTTPS;
import static com.danyl.spiders.constants.TimeConstants.*;
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
                .put("https://list.mi.com/0", "所有商品")
                .build();
        checkProxy(validateUrlMap);
    }

    private void checkProxy(Map<String, String> map) {
        log.info("check proxy start {}", new Date());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
        try {
            proxy.selectFrom(PROXY).fetch().stream().map(proxyRecord -> CompletableFuture.runAsync(() -> {
                Pair<Boolean, Integer> validateResultPair = Pair.of(false, TIMEOUT);
                // 经测试发现高质量的代理极其稀少
                // 对每个校验Url进行测试，有一个校验成功就算该代理可用
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String url = entry.getKey();
                    String regex = entry.getValue();
                    Pair<Boolean, Integer> tmpPair = doCheckProxy(proxyRecord.getIp(), proxyRecord.getPort(), proxyRecord.getProtocol(), url, regex, TIMEOUT);
                    if (tmpPair.getLeft()) {
                        // 如何tmpPair是校验成功的则赋值给validateResultPair,以便更新代理的speed
                        validateResultPair = tmpPair;
                        // 如果当前校验通过的Url是https协议，并且proxyRecord不是socks类型，则更新代理的protocol为https
                        String urlProtocol = ProxyUtil.getUrlProtocol(url);
                        if (HTTPS.equals(urlProtocol) && !proxyRecord.getProtocol().toLowerCase().contains("socks")) {
                            proxyRecord.setProtocol(urlProtocol);
                        }
                    }
                }
                if (validateResultPair.getLeft()) {
                    proxyRecord.setIsValid(true);
                    proxyRecord.setSpeed(validateResultPair.getRight());
                    proxyRecord.setCheckedTime(LocalDateTime.now());
                    proxyRecord.update();
                } else {
                    proxyRecord.setIsValid(false);
                    LocalDateTime checkedTime = proxyRecord.getCheckedTime();
                    // 无效代理保留三天
                    if (checkedTime.plusDays(3).isAfter(LocalDateTime.now())) {
                        proxyRecord.update();
                    } else {
                        proxyRecord.delete();
                    }
                }
            }, fixedThreadPool))
                    .collect(Collectors.toList())
                    .stream()
                    // 等待所有校验线程执行完毕
                    .map(CompletableFuture::join)
                    .forEach(aVoid -> {
                    });
        } catch (Exception e) {
            log.error("check proxy task exception: {}", e.getMessage());
        } finally {
            // 别忘了关闭局部变量的线程池
            fixedThreadPool.shutdown();
        }

        log.info("check proxy end {}", new Date());
    }

    /**
     * @param ip    要检查的代理 ip
     * @param port  要检查的代理 port
     * @param url   通过此Url校验代理连通性
     * @param regex 校验正则表达式
     */
    public static Pair<Boolean, Integer> doCheckProxy(String ip, Integer port, String protocol, String url, String regex, Integer timeout) {
        long start = System.currentTimeMillis();
        try {
            Connection connection = Jsoup.connect(url)
                    .referrer(url)
                    .timeout(timeout)
                    .proxy(ProxyUtil.getProxy(ip, port, protocol))
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .userAgent(USERAGENT);
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

    @Scheduled(fixedDelay = HOURS * 6)
    public void fillProxyFields() {
        log.info("fill proxy fields start {}", new Date());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(16);
        try {
            proxy.selectFrom(PROXY)
                    .where(PROXY.IS_VALID.eq(true).and(PROXY.CITY.eq("").or(PROXY.CITY.eq("XX"))))
                    .fetch()
                    .stream()
                    .map(proxyRecord -> CompletableFuture.supplyAsync(() -> {
                        try {
                            Document document = Jsoup.connect("http://proxydb.net/anon")
                                    .proxy(ProxyUtil.getProxy(proxyRecord))
                                    .ignoreContentType(true)
                                    .followRedirects(true)
                                    .userAgent(USERAGENT)
                                    .timeout(TIMEOUT * 2)
                                    .get();
                            String anonymity = document.select("body > div.container-fluid > dl > dd:nth-child(4)").text();
                            String country = document.select("body > div.container-fluid > dl > dd:nth-child(6) > img").attr("title");
                            String city = document.select("body > div.container-fluid > dl > dd:nth-child(8)").text();
                            String region = document.select("body > div.container-fluid > dl > dd:nth-child(10)").text();
                            String isp = document.select("body > div.container-fluid > dl > dd:nth-child(12)").text();

                            proxyRecord.setAnonymity(anonymity);
                            proxyRecord.setCountry(country);
                            proxyRecord.setCity(city);
                            proxyRecord.setRegion(region);
                            proxyRecord.setIsp(isp);
                            proxyRecord.update();
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }, fixedThreadPool).thenApplyAsync(success -> {
                        if (success) {
                            return true;
                        }
                        try {
                            String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + proxyRecord.getIp();
                            String regex = "\"ip\":\"" + proxyRecord.getIp() + "\"";
                            // 如果是返回json字符串，不能用jsoup parse解析，会自动带上html标签
                            String ipJson = JsoupDownloader.jsoupExecute(url, regex).body();

                            DocumentContext parse = JsonPath.parse(ipJson);
                            String country = parse.read("$.data.country");
                            String city = parse.read("$.data.city");
                            String region = parse.read("$.data.region");
                            String isp = parse.read("$.data.isp");

                            proxyRecord.setCountry(country);
                            proxyRecord.setCity(city);
                            proxyRecord.setRegion(region);
                            proxyRecord.setIsp(isp);
                            proxyRecord.update();
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }, fixedThreadPool).thenAcceptAsync(success -> {
                        if (success) {
                            return;
                        }
                        try {
                            String url = "http://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=" + proxyRecord.getIp() + "&co=&resource_id=6006&t=" + System.currentTimeMillis() + "&ie=utf8&oe=gbk&format=json&tn=baidu&_=" + System.currentTimeMillis();
                            String regex = "\"origip\":\"" + proxyRecord.getIp() + "\"";
                            // 如果是返回json字符串，不能用jsoup parse解析，会自动带上html标签
                            String ipJson = JsoupDownloader.jsoupExecute(url, regex).body();

                            DocumentContext parse = JsonPath.parse(ipJson);
                            String country = parse.read("$.data.location");
                            proxyRecord.setCountry(country);
                            proxyRecord.update();
                        } catch (Exception ignored) {
                        }
                    }, fixedThreadPool))
                    .collect(Collectors.toList())
                    .stream()
                    // 等待所有校验线程执行完毕
                    .map(CompletableFuture::join)
                    .forEach(aVoid -> {
                    });
        } catch (Exception e) {
            log.error("check proxy task exception: {}", e.getMessage());
        } finally {
            // 别忘了关闭局部变量的线程池
            fixedThreadPool.shutdown();
        }

        log.info("fill proxy fields end {}", new Date());
    }
}