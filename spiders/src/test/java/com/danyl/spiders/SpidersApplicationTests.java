package com.danyl.spiders;

import com.danyl.spiders.downloader.JsoupDownloader;
import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    public void xiaomiAstartTest() {
        // 最终结论 astart=70后开始重复
        String product_id = "10000099";

        Integer astart = 0;
        Set<Integer> comment_ids = new LinkedHashSet<>();
        while (true) {
            String url = String.format("https://comment.huodong.mi.com/comment/entry/getSummary?goods_id=%s&v_pid=%s&sstart=0&slen=10&astart=%s&alen=10&_=%s", product_id, product_id, astart, System.currentTimeMillis());
            System.out.println(url);
            Connection connection = Jsoup.connect(url).referrer(String.format("https://item.mi.com/%s.html?cfrom=search", product_id));
            Connection.Response response = JsoupDownloader.jsoupExecute(connection, "msg");
            String json = response.body();
            System.out.println(json);
            DocumentContext parse = JsonPath.parse(json);
            List<Integer> tmp_comment_id_list = parse.read("$..addtime_comments..comment_id");
            Set<Integer> tmp_comment_ids = new LinkedHashSet<>(tmp_comment_id_list);
            System.out.println(tmp_comment_ids);
            System.out.println(tmp_comment_ids.size());
            Sets.SetView<Integer> intersection = Sets.intersection(comment_ids, tmp_comment_ids);
            ImmutableSet<Integer> integers = intersection.immutableCopy();

            System.out.println(integers);
            System.out.println(integers.size());
            System.out.println("astart:" + astart);
            comment_ids.addAll(tmp_comment_ids);
            astart += 10;
            if (integers.size() > 0) {
                System.out.println(integers);
                System.out.println(integers.size());
                return;
            }
        }
    }
}