package com.danyl.spiders;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static com.danyl.spiders.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpidersApplicationTests {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    @Test
    public void jsoupTest() {
        String url = "https://www.baidu.com/s?wd=ip&ie=UTF-8";
        List<Proxy> proxyList = proxy.selectFrom(PROXY)
                //.where(PROXY.IS_VALID.eq(true))
                .fetchInto(Proxy.class);
        proxyList.stream()
                .forEach(proxy1 -> {
                    try {
                        Document document = Jsoup.connect(url)
                                .proxy(proxy1.getIp(), proxy1.getPort())
                                .ignoreContentType(true)
                                .followRedirects(true)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                                .timeout(10 * 1000)
                                .get();
                        log.info("{} {} {}", proxy1.getIp(), proxy1.getPort(), document.title());
                    } catch (IOException ignored) {
                    }
                });
    }
}