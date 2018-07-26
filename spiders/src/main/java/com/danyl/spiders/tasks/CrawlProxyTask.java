package com.danyl.spiders.tasks;

import com.danyl.spiders.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.HttpProtocolConstants.HTTP;
import static com.danyl.spiders.constants.HttpProtocolConstants.HTTPS;
import static com.danyl.spiders.constants.TimeConstants.MINUTES;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Component
public class CrawlProxyTask {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    @Scheduled(fixedDelay = MINUTES * 20)
    public void crawlProxy() {
        log.info("crawl proxy start {}", new Date());

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(this::get66ip);
        executorService.execute(this::getip3366);
        executorService.execute(this::getkuaidaili);
        executorService.execute(this::getxicidaili);

        // shutdown非阻塞，再使用awaitTermination进行阻塞等待
        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdownNow();
        log.info("crawl proxy end {}", new Date());
    }

    // 小六代理
    public void get66ip() {
        for (int i = 0; i < 100; i++) {
            // 1. 一次100个 https代理
            String url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=4&start=&ports=&export=&ipaddress=&area=0&proxytype=1&api=66ip";
            String html = ProxyService.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").html();
            Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(html);
            while (matcher.find()) {
                String ip = matcher.group(1);
                int port = Integer.parseInt(matcher.group(2));
                String type = HTTPS;
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE)
                        .values(ip, port, false, type)
                        .onDuplicateKeyIgnore()
                        .executeAsync();
            }

            // 2. 一次100个 http代理
            url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=4&start=&ports=&export=&ipaddress=&area=0&proxytype=0&api=66ip";
            html = ProxyService.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").html();
            matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(html);
            while (matcher.find()) {
                String ip = matcher.group(1);
                int port = Integer.parseInt(matcher.group(2));
                String type = HTTP;
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE)
                        .values(ip, port, false, type)
                        .onDuplicateKeyIgnore()
                        .executeAsync();
            }
        }
    }

    // 云代理
    public void getip3366() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 10; j++) {
                String url = "http://www.ip3366.net/?stype=" + i + "&page=" + j;
                Document document = ProxyService.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
                                    .executeAsync();
                        });
            }
        }
    }

    // 快代理
    public void getkuaidaili() {
        for (int i = 1; i < 25; i++) {
            String url = "https://www.kuaidaili.com/free/inha/" + i + "/";
            Document document = ProxyService.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
                                .executeAsync();
                    });
        }
    }

    // 西刺代理
    public void getxicidaili() {
        for (int i = 1; i <= 300; i++) {
            String url = "http://www.xicidaili.com/nn/" + i;
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            Document document = ProxyService.jsoupGet(connection, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
                                .executeAsync();
                    });
        }
    }
}