package com.danyl.spiders.tasks;

import com.danyl.spiders.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.HOURS;
import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@EnableScheduling
@Component
public class CrawlProxyTask {

    @Autowired
    @Qualifier("DSLContextProxy")
    private DSLContext proxy;

    @Scheduled(fixedDelay = HOURS * 3)
    public void crawlProxy() {
        log.info("crawl proxy start {}", new Date());

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(this::get66ip);
        executorService.execute(this::getip3366);
        executorService.execute(this::getkuaidaili);
        executorService.execute(this::getxicidaili);
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
                String type = "https";
                proxy.insertInto(PROXY, PROXY.IP, PROXY.PORT, PROXY.IS_VALID, PROXY.TYPE)
                        .values(ip, port, false, type)
                        .onDuplicateKeyIgnore()
                        .execute();
            }

            // 2. 一次100个 http代理
            url = "http://www.66ip.cn/nmtq.php?getnum=100&isp=0&anonymoustype=4&start=&ports=&export=&ipaddress=&area=0&proxytype=0&api=66ip";
            html = ProxyService.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").html();
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
                                    .execute();
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
                                .execute();
                    });
        }
    }

    // 西刺代理
    public void getxicidaili() {
        for (int i = 1; i <= 300; i++) {
            String url = "http://www.xicidaili.com/nn/" + i;
            Document document = ProxyService.jsoupGet(url, "(\\d+\\.\\d+\\.\\d+\\.\\d+)");
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
}