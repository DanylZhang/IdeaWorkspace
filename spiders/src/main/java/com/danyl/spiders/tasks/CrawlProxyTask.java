package com.danyl.spiders.tasks;

import com.danyl.spiders.downloader.JsoupDownloader;
import com.danyl.spiders.downloader.PhantomJSDownloader;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.danyl.spiders.constants.ProtocolConstants.*;
import static com.danyl.spiders.constants.TimeConstants.HOURS;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Component
public class CrawlProxyTask {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);

    // 66免费代理网
    @Scheduled(fixedDelay = HOURS)
    private void get66ip() {
        for (int i = 0; i < 100; i++) {
            String url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=0&start=&ports=&export=&ipaddress=&area=0&proxytype=2&api=66ip";
            Document document = JsoupDownloader.jsoupGet(url, "www\\.66daili\\.cn");
            if (document == null) {
                continue;
            }
            Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(document.html());
            while (matcher.find()) {
                String ip = matcher.group(1);
                int port = Integer.parseInt(matcher.group(2));
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE)
                        .values(ip, port, false, TIMEOUT, HTTP, "http://www.66ip.cn")
                        .onDuplicateKeyIgnore()
                        .execute();
            }
        }
        log.info("crawl 66免费代理网 proxy end {}", new Date());
    }

    // 云代理
    @Scheduled(fixedDelay = HOURS)
    private void getip3366() {
        IntStream.of(1, 3).boxed().flatMap(i -> IntStream.rangeClosed(1, 7).boxed().map(j -> CompletableFuture.runAsync(() -> {
            String url = "http://www.ip3366.net/?stype=" + i + "&page=" + j;
            Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
            if (document == null) {
                return;
            }
            document.select("#list > table > tbody > tr")
                    .forEach(element -> {
                        String ip = element.select("td:nth-child(1)").text().trim();
                        int port = Integer.parseInt(element.select("td:nth-child(2)").text());
                        String anonymity = element.select("td:nth-child(3)").text();
                        String protocol = element.select("td:nth-child(4)").text().toLowerCase();
                        String country = element.select("td:nth-child(5)").text();

                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY)
                                .values(ip, port, false, anonymity, TIMEOUT, protocol, "http://www.ip3366.net", country)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }, fixedThreadPool)))
                .collect(Collectors.toList())
                .stream()
                .map((Function<CompletableFuture<Void>, Object>) CompletableFuture::join)
                .forEach(aVoid -> {
                });
        log.info("crawl 云代理 proxy end {}", new Date());
    }

    // 快代理
    @Scheduled(fixedDelay = HOURS)
    private void getkuaidaili() {
        IntStream.rangeClosed(1, 25).boxed().map(i -> CompletableFuture.runAsync(() -> {
            String url = "https://www.kuaidaili.com/free/inha/" + i + "/";
            Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
            if (document == null) {
                return;
            }
            document.select("#list > table > tbody > tr")
                    .forEach(element -> {
                        String ip = element.select("td:nth-child(1)").text().trim();
                        int port = Integer.parseInt(element.select("td:nth-child(2)").text());
                        String anonymity = element.select("td:nth-child(3)").text();
                        String protocol = element.select("td:nth-child(4)").text().toLowerCase();
                        String country = element.select("td:nth-child(5)").text();

                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY)
                                .values(ip, port, false, anonymity, TIMEOUT, protocol, "https://www.kuaidaili.com", country)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map((Function<CompletableFuture<Void>, Object>) CompletableFuture::join)
                .forEach(aVoid -> {
                });
        log.info("crawl 快代理 proxy end {}", new Date());
    }

    // 西刺代理
    @Scheduled(fixedDelay = HOURS)
    private void getxicidaili() {
        IntStream.rangeClosed(1, 300).boxed().map(i -> CompletableFuture.runAsync(() -> {
            String url = "http://www.xicidaili.com/nn/" + i;
            Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
            if (document == null) {
                return;
            }
            document.select("#ip_list > tbody > tr")
                    .stream()
                    .skip(1)
                    .forEach(element -> {
                        String ip = element.select("td:nth-child(2)").text();
                        int port = 0;
                        try {
                            port = Integer.parseInt(element.select("td:nth-child(3)").text());
                        } catch (Exception ignored) {
                        }
                        String anonymity = element.select("td:nth-child(5)").text();
                        String protocol = element.select("td:nth-child(6)").text().toLowerCase();
                        if (protocol.equals("socks4/5")) {
                            protocol = SOCKS4;
                        }
                        String country = element.select("td.country > img").attr("alt").toUpperCase();
                        String city = element.select("td:nth-child(4)").text();

                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY, PROXY.CITY)
                                .values(ip, port, false, anonymity, TIMEOUT, protocol, "http://www.xicidaili.com", country, city)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }, fixedThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map((Function<CompletableFuture<Void>, Object>) CompletableFuture::join)
                .forEach(aVoid -> {
                });
        log.info("crawl 西刺代理 proxy end {}", new Date());
    }

    // free-proxy-list
    @Scheduled(fixedDelay = HOURS)
    private void getFreeProxyList() {
        String url = "https://free-proxy-list.net/";
        Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
        if (document == null) {
            return;
        }
        document.select("#proxylisttable > tbody > tr")
                .forEach(element -> {
                    String ip = element.select("td:nth-child(1)").text();
                    int port = Integer.parseInt(element.select("td:nth-child(2)").text());
                    String country = element.select("td:nth-child(4)").text();
                    String anonymity = element.select("td:nth-child(5)").text();
                    String protocol = element.select("td:nth-child(7)").text().toLowerCase();
                    if (protocol.equals("yes")) {
                        protocol = HTTPS;
                    } else {
                        protocol = HTTP;
                    }

                    proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY)
                            .values(ip, port, false, anonymity, TIMEOUT, protocol, "https://free-proxy-list.net", country)
                            .onDuplicateKeyIgnore()
                            .execute();
                });
        log.info("crawl free-proxy-list proxy end {}", new Date());
    }

    // www.socks-proxy.net
    @Scheduled(fixedDelay = HOURS)
    private void getFreeProxyListSocks() {
        String url = "https://www.socks-proxy.net/";
        Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
        if (document == null) {
            return;
        }
        document.select("#proxylisttable > tbody > tr")
                .forEach(element -> {
                    String ip = element.select("td:nth-child(1)").text();
                    int port = Integer.parseInt(element.select("td:nth-child(2)").text());
                    String country = element.select("td:nth-child(4)").text();
                    String protocol = element.select("td:nth-child(5)").text().toLowerCase();
                    String anonymity = element.select("td:nth-child(6)").text();

                    proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY)
                            .values(ip, port, false, anonymity, TIMEOUT, protocol, "https://www.socks-proxy.net", country)
                            .onDuplicateKeyIgnore()
                            .execute();
                });
        log.info("crawl www.socks-proxy.net proxy end {}", new Date());
    }

    // proxydb.net
    @Scheduled(fixedDelay = HOURS)
    public void getProxyDB() {
        log.info("crawl proxydb.net start {}", new Date());

        for (int i = 0; i <= 500; i++) {
            int offset = i * 15;
            String url = "http://proxydb.net/?offset=" + offset;
            Document document = PhantomJSDownloader.getDocument(url, "atob");
            if (document == null) {
                continue;
            }

            document.select("body > div > div.table-responsive > table > tbody > tr")
                    .forEach(element -> {
                        String _proxy = element.select("td:nth-child(1) > a").text().trim();
                        String ip = _proxy.split(":")[0];
                        int port = Integer.parseInt(_proxy.split(":")[1]);
                        String country = element.select("td:nth-child(3) > img").attr("title");
                        String isp = element.select("td:nth-child(4) > div").text();
                        String protocol = element.select("td:nth-child(5)").text().trim().toLowerCase();
                        String anonymity = element.select("td:nth-child(6) > span").text().trim();

                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY, PROXY.ISP)
                                .values(ip, port, false, anonymity, TIMEOUT, protocol, "https://proxydb.net", country, isp)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }

        log.info("crawl proxydb.net end {}", new Date());
    }
}