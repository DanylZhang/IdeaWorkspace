package com.danyl.spiders.tasks;

import com.danyl.spiders.jooq.gen.vip.tables.pojos.Ads;
import com.danyl.spiders.jooq.gen.vip.tables.pojos.AdsActivity;
import com.danyl.spiders.service.ProxyService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.danyl.spiders.constants.TimeConstants.HOURS;
import static com.danyl.spiders.jooq.gen.vip.Tables.ADS;
import static com.danyl.spiders.jooq.gen.vip.Tables.ADS_ACTIVITY;

@Slf4j
@Component
public class VipAdsTask {

    @Resource(name = "DSLContextNewVip")
    private DSLContext create;

    @Scheduled(fixedDelay = HOURS * 4)
    public void crawlVipAds() {
        Document document = ProxyService.jsoupGet("https://www.vip.com/", "ADS\\w{5}");
        String html = document.html();
        Matcher matcher = Pattern.compile("(ADS\\w{5})").matcher(html);
        List<String> adsList = new ArrayList<>();
        while (matcher.find()) {
            String ads = matcher.group(1);
            adsList.add(ads);
        }
        String url = "https://pcapi.vip.com/cmc/index.php?type=" + String.join("%2C", adsList) + "&warehouse=VIP_SH&areaid=103101&preview=0&date_from=&time_from=&user_class=&channelId=0";
        String adsJson = ProxyService.jsoupGet(url, "ADADS\\w+").text();
        DocumentContext parse = JsonPath.parse(adsJson);
        List<Map<String, String>> read = parse.read("$..items[?(@.link=~/.*mst.vip.com.*/i)]");
        read.stream()
                .map(map -> {
                    String link = map.get("link");
                    Connection connection = Jsoup.connect(link)
                            .followRedirects(true)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                            .header("Cookie", "cps=%3A5c5jp4tz%3Aed2c07a7%3A57139_168_0__1%3Af58df3bbf8714762a83259d697c1fd03; mars_pid=0; vip_wh=VIP_SH; vip_address=%257B%2522pid%2522%253A%2522103101%2522%252C%2522pname%2522%253A%2522%255Cu4e0a%255Cu6d77%255Cu5e02%2522%252C%2522cid%2522%253A%2522103101101%2522%252C%2522cname%2522%253A%2522%255Cu4e0a%255Cu6d77%255Cu5e02%2522%257D; vip_province=103101; vip_province_name=%E4%B8%8A%E6%B5%B7%E5%B8%82; vip_city_name=%E4%B8%8A%E6%B5%B7%E5%B8%82; vip_city_code=103101101; oversea_jump=cn; _smt_uid=5b4465ef.e448e84; VipUINFO=luc%3Aa%7Csuc%3Aa%7Cbct%3Ac_new%7Chct%3Ac_new%7Cbdts%3A0%7Cbcts%3A0%7Ckfts%3A0%7Cc10%3A0%7Crcabt%3A0%7Cp2%3A0%7Cp3%3A0%7Cp4%3A0%7Cp5%3A1; user_class=a; mst_csrf_key=0fde403d13dd1b1eb3fe29c60f860d6b; mars_sid=54d10491f4b8b9e397a1e2cc0172bb8f; visit_id=57286B1E75CE438934DBD3EA99468632; m_vip_province=103101; _jzqco=%7C%7C%7C%7C%7C1.62199142.1531209199750.1531818871732.1531818893240.1531818871732.1531818893240.0.0.0.21.21; mstRedirect_0=%7B%22guide%22%3A%7B%225535476%22%3A%5B1531818872%2C5535486%5D%2C%225603998%22%3A%5B1531818893%2C5604002%5D%7D%2C%22cdi%22%3A%7B%225535486%22%3A%5B1531818872%2C5535486%5D%2C%225604002%22%3A%5B1531818893%2C5604002%5D%7D%7D; mst_consumer=A; vipte_viewed_=575943922%2C563946147; mars_cid=1531201314264_96255128480bf8133042a3e3dcaafabb");
                    Response response = ProxyService.jsoupExecute(connection, "encrypt_id");
                    // 跳转后的url
                    link = response.url().toExternalForm();
                    log.info("ads link: {}", link);

                    Document document1 = null;
                    try {
                        document1 = response.parse();
                    } catch (IOException e) {
                        log.error("Jsoup parse document error: {}", e.getMessage());
                    }

                    AdsActivity adsActivity = new AdsActivity();
                    adsActivity.setId(Integer.parseInt(map.get("id")));
                    // 更新跳转后的 ads_id
                    Matcher matcher2 = Pattern.compile("=mst_(\\d+)").matcher(link);
                    if (matcher2.find()) {
                        adsActivity.setId(Integer.parseInt(matcher2.group(1)));
                    }
                    adsActivity.setUrl(link);
                    adsActivity.setImg(map.get("img").substring(2));
                    adsActivity.setType(Integer.parseInt(map.get("exptype")));
                    adsActivity.setModified(LocalDateTime.now());
                    String title = document1.title();
                    adsActivity.setName(title);

                    // 这一步将广告插入 ads_activity表
                    create.insertInto(ADS_ACTIVITY, ADS_ACTIVITY.ID, ADS_ACTIVITY.NAME, ADS_ACTIVITY.URL, ADS_ACTIVITY.IMG, ADS_ACTIVITY.TYPE, ADS_ACTIVITY.MODIFIED)
                            .values(adsActivity.getId(), adsActivity.getName(), adsActivity.getUrl(), adsActivity.getImg(), adsActivity.getType(), adsActivity.getModified())
                            .onDuplicateKeyUpdate()
                            .set(ADS_ACTIVITY.MODIFIED, adsActivity.getModified())
                            .execute();
                    return Pair.of(adsActivity, document1);
                })
                .collect(Collectors.toList())
                .forEach(adsActivityDocumentPair -> {
                    AdsActivity adsActivity = adsActivityDocumentPair.getLeft();
                    Document document1 = adsActivityDocumentPair.getRight();
                    // 继续查找广告页面内的广告商品
                    String html2 = document1.html();
                    Matcher matcher1 = Pattern.compile("data:\\s*(\\{.+?encrypt_id\":\"[^\"]+?\"}),").matcher(html2);
                    if (matcher1.find()) {
                        String group = matcher1.group(1);
                        DocumentContext parse1 = JsonPath.parse(group);
                        Integer page_id = Integer.valueOf(parse1.read("$.id").toString());
                        List<String> plugin_ids = parse1.read("$.moduleList[?(@.floor)].id");
                        for (String plugin_id : plugin_ids) {
                            String purchasesUrl = "https://mst.vip.com/Special/getPurchases?page_id=" + page_id + "&plugin_id=" + plugin_id + "&floor=1f&client=vipcms&warehouse=VIP_SH&_=" + System.currentTimeMillis();
                            log.info("getPurchases link: {}", purchasesUrl);

                            Connection connection = Jsoup.connect(purchasesUrl)
                                    .followRedirects(true)
                                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                                    .header("X-Requested-With", "XMLHttpRequest")
                                    .header("Referer", adsActivity.getUrl())
                                    .header("Cookie", "cps=%3A5c5jp4tz%3Aed2c07a7%3A57139_168_0__1%3Af58df3bbf8714762a83259d697c1fd03; mars_pid=0; vip_wh=VIP_SH; vip_address=%257B%2522pid%2522%253A%2522103101%2522%252C%2522pname%2522%253A%2522%255Cu4e0a%255Cu6d77%255Cu5e02%2522%252C%2522cid%2522%253A%2522103101101%2522%252C%2522cname%2522%253A%2522%255Cu4e0a%255Cu6d77%255Cu5e02%2522%257D; vip_province=103101; vip_province_name=%E4%B8%8A%E6%B5%B7%E5%B8%82; vip_city_name=%E4%B8%8A%E6%B5%B7%E5%B8%82; vip_city_code=103101101; oversea_jump=cn; _smt_uid=5b4465ef.e448e84; VipUINFO=luc%3Aa%7Csuc%3Aa%7Cbct%3Ac_new%7Chct%3Ac_new%7Cbdts%3A0%7Cbcts%3A0%7Ckfts%3A0%7Cc10%3A0%7Crcabt%3A0%7Cp2%3A0%7Cp3%3A0%7Cp4%3A0%7Cp5%3A1; user_class=a; mst_csrf_key=0fde403d13dd1b1eb3fe29c60f860d6b; mars_sid=54d10491f4b8b9e397a1e2cc0172bb8f; visit_id=57286B1E75CE438934DBD3EA99468632; m_vip_province=103101; _jzqco=%7C%7C%7C%7C%7C1.62199142.1531209199750.1531818871732.1531818893240.1531818871732.1531818893240.0.0.0.21.21; mstRedirect_0=%7B%22guide%22%3A%7B%225535476%22%3A%5B1531818872%2C5535486%5D%2C%225603998%22%3A%5B1531818893%2C5604002%5D%7D%2C%22cdi%22%3A%7B%225535486%22%3A%5B1531818872%2C5535486%5D%2C%225604002%22%3A%5B1531818893%2C5604002%5D%7D%7D; mst_consumer=A; vipte_viewed_=575943922%2C563946147; mars_cid=1531201314264_96255128480bf8133042a3e3dcaafabb");
                            String json = ProxyService.jsoupExecute(connection, "code").body();
                            log.info("getPurchases json: {}", json);
                            DocumentContext parse2 = JsonPath.parse(json);
                            List<String> spuIdList = parse2.read("$.data..v_spu_id");
                            if (spuIdList.size() > 0) {
                                List<Map<String, Object>> itemList = parse2.read("$.data");
                                for (Map<String, Object> stringObjectMap : itemList) {
                                    Ads ads = new Ads();
                                    ads.setItemId(Integer.parseInt(stringObjectMap.get("product_id").toString()));
                                    ads.setListId(Integer.parseInt(stringObjectMap.get("brand_id").toString()));
                                    ads.setSpuid(Long.parseLong(stringObjectMap.get("v_spu_id").toString()));
                                    ads.setActId(adsActivity.getId());
                                    ads.setDate(LocalDate.now());

                                    // 这一步将广告商品插入 ads表
                                    create.insertInto(ADS, ADS.ITEM_ID, ADS.LIST_ID, ADS.SPUID, ADS.ACT_ID, ADS.DATE)
                                            .values(ads.getItemId(), ads.getListId(), ads.getSpuid(), ads.getActId(), ads.getDate())
                                            .onDuplicateKeyIgnore()
                                            .execute();
                                }
                            }
                        }
                    }
                });
    }
}