package com.danyl.spiders.tasks;

import com.danyl.spiders.downloader.JsoupDownloader;
import com.danyl.spiders.downloader.PhantomJSDownloader;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.ProtocolConstants.*;
import static com.danyl.spiders.constants.TimeConstants.HOURS;
import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Component
public class CrawlProxyTask {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    @Autowired
    private PhantomJSDownloader phantomJSDownloader;

    @Scheduled(fixedDelay = HOURS)
    public void crawlProxy() {
        log.info("crawl proxy start {}", new Date());

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(this::get66ip);
        executorService.execute(this::getip3366);
        executorService.execute(this::getkuaidaili);
        executorService.execute(this::getxicidaili);
        executorService.execute(this::getFreeProxyList);
        executorService.execute(this::getFreeProxyListSocks);

        // shutdown非阻塞，再使用awaitTermination进行阻塞等待
        try {
            executorService.awaitTermination(90, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 立即关闭线程池，不等待已开始执行的线程执行完毕
        executorService.shutdown();
        log.info("crawl proxy end {}", new Date());
    }

    // 66免费代理网
    private void get66ip() {
        for (int i = 0; i < 100; i++) {
            String url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=0&start=&ports=&export=&ipaddress=&area=0&proxytype=2&api=66ip";
            String html = JsoupDownloader.jsoupGet(url, "www\\.66daili\\.cn").html();
            Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(html);
            while (matcher.find()) {
                String ip = matcher.group(1);
                int port = Integer.parseInt(matcher.group(2));
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE)
                        .values(ip, port, false, TIMEOUT, HTTP, "http://www.66ip.cn")
                        .onDuplicateKeyIgnore()
                        .execute();
            }
        }
    }

    // 云代理
    private void getip3366() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 10; j++) {
                String url = "http://www.ip3366.net/?stype=" + i + "&page=" + j;
                Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
            }
        }
    }

    // 快代理
    private void getkuaidaili() {
        for (int i = 1; i < 25; i++) {
            String url = "https://www.kuaidaili.com/free/inha/" + i + "/";
            Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
        }
    }

    // 西刺代理
    private void getxicidaili() {
        for (int i = 1; i <= 300; i++) {
            String url = "http://www.xicidaili.com/nn/" + i;
            Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
        }
    }

    // free-proxy-list
    private void getFreeProxyList() {
        String url = "https://free-proxy-list.net/";
        Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
    }

    // www.socks-proxy.net
    private void getFreeProxyListSocks() {
        String url = "https://www.socks-proxy.net/";
        Document document = JsoupDownloader.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
    }

    // proxydb.net
    @Scheduled(fixedDelay = HOURS * 6)
    public void getProxyDB() {
        log.info("crawl proxy db use phantomjs start {}", new Date());

        for (int i = 0; i <= 500; i++) {
            int offset = i * 15;
            String url = "http://proxydb.net/?offset=" + offset;
            Document document = phantomJSDownloader.getDocument(url, "atob");
            if (document == null) {
                return;
            }

            document.select("body > div > div.table-responsive > table > tbody > tr")
                    .forEach(element -> {
                        String _proxy = element.select("td:nth-child(1) > a").text().trim();
                        String ip = _proxy.split(":")[0];
                        int port = Integer.parseInt(_proxy.split(":")[1]);
                        String host = element.select("td:nth-child(2) > div").text();
                        String country = element.select("td:nth-child(3) > img").attr("title");
                        String isp = element.select("td:nth-child(4) > div").text();
                        String protocol = element.select("td:nth-child(5)").text().trim().toLowerCase();
                        String anonymity = element.select("td:nth-child(6) > span").text().trim();
                        String via = element.select("td:nth-child(9) > div").text().trim();

                        proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.ANONYMITY, PROXY.SPEED, PROXY.PROTOCOL, PROXY.SOURCE, PROXY.COUNTRY, PROXY.ISP, PROXY.HOST, PROXY.VIA)
                                .values(ip, port, false, anonymity, TIMEOUT, protocol, "https://proxydb.net", country, isp, host, via)
                                .onDuplicateKeyIgnore()
                                .execute();
                    });
        }

        log.info("crawl proxy db use phantomjs end {}", new Date());
    }
}