package com.danyl.spiders.web.controller;

import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

import static com.danyl.spiders.jooq.gen.proxy.Tables.PROXY;

@Slf4j
@Controller
public class HomeController {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index.html";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        Result<ProxyRecord> fetch = proxy.selectFrom(PROXY).fetch();
        fetch.parallelStream().forEach(proxyRecord -> {
            String url = "https://proxydb.net/anon";
            try {
                Connection.Response execute = Jsoup.connect(url)
                        .proxy(proxyRecord.getIp(), proxyRecord.getPort())
                        .timeout(5 * 1000)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                        .execute();
                Map<String, String> cookies = execute.cookies();
                Map<String, String> headers = execute.headers();
                Document document = execute.parse();
                String ipAddress = document.select("body > div > dl > dd:nth-child(2)").text();
                String anonymity = document.select("body > div > dl > dd:nth-child(4)").text();
                String country = document.select("body > div > dl > dd:nth-child(6)").text();
                log.info("proxy: {}, headers: {}, cookies: {}, ipAddress: {}, anonymity: {}, country: {}", proxyRecord, headers, cookies, ipAddress, anonymity, country);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        return "success";
    }
}