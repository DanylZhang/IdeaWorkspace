package com.danyl.spiders.tasks;

import com.danyl.spiders.jooq.gen.vip.tables.pojos.Ads;
import com.danyl.spiders.jooq.gen.vip.tables.pojos.AdsActivity;
import com.danyl.spiders.service.ProxyService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.HOURS;
import static com.danyl.spiders.jooq.gen.vip.Tables.ADS;
import static com.danyl.spiders.jooq.gen.vip.Tables.ADS_ACTIVITY;

@Slf4j
@EnableScheduling
@Component
public class VipAdsTask {

    @Autowired
    @Qualifier("DSLContextNewVip")
    private DSLContext create;

    @Scheduled(fixedDelay = HOURS * 8)
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
        try {
            String html1 = ProxyService.jsoupGet(url, "ADADS\\w+").text();
            DocumentContext parse = JsonPath.parse(html1);
            List<Map<String, String>> read = parse.read("$..items[?(@.link=~/.*mst.vip.com.*/i)]");
            for (Map<String, String> map : read) {
                String link = map.get("link");
                Connection connection = Jsoup.connect(link)
                        .followRedirects(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                        .header("Cookie", "oversea_jump=cn; _smt_uid=5b0cee94.35dcab2c; WAP[from]=www; WAP[p_wh]=VIP_SH; warehouse=VIP_SH; m_vip_province=103101; WAP[p_area]=%25E4%25B8%258A%25E6%25B5%25B7; WAP[area_id]=103101101; wap_consumer=A1; vip_city_name=%E5%8C%97%E4%BA%AC%E5%B8%82; mar_ref=oper_special_3_4; mars_pid=303; cps=%3A5c5jp4tz%3Aed2c07a7%3A57139_168_0__1%3Af58df3bbf8714762a83259d697c1fd03; VipUINFO=luc%3Aa%7Csuc%3Aa%7Cbct%3Ac_new%7Chct%3Ac_new%7Cbdts%3A0%7Cbcts%3A0%7Ckfts%3A0%7Cc10%3A0%7Crcabt%3A0%7Cp2%3A0%7Cp3%3A1%7Cp4%3A0%7Cp5%3A0; mars_sid=0bcfed48549bd07b6aa7b4f3bed58cd9; PHPSESSID=j9i1mutv948qmffv6fmvpjb4h4; vip_address=%257B%2522pid%2522%253A%2522103101%2522%252C%2522pname%2522%253A%2522%255Cu4e0a%255Cu6d77%255Cu5e02%2522%252C%2522cid%2522%253A%2522103101101%2522%252C%2522cname%2522%253A%2522%255Cu5317%255Cu4eac%255Cu5e02%2522%252C%2522did%2522%253A101101101101%252C%2522dname%2522%253A%2522%255Cu4e1c%255Cu57ce%255Cu533a%2522%252C%2522sid%2522%253A911101101101%252C%2522sname%2522%253A%2522%255Cu4e1c%255Cu534e%255Cu95e8%255Cu8857%255Cu9053%2522%257D; vip_province=103101; vip_province_name=%E4%B8%8A%E6%B5%B7%E5%B8%82; vip_city_code=103101101; vip_wh=VIP_SH; vip_ipver=31; mst_csrf_key=56dfb498bca5e0e0ca63a75489348573; user_class=a; vipte_viewed_=567953774%2C570788523%2C570792728%2C562457047%2C563277169; mst_consumer=A1; visit_id=D9801FFC9679861344859BCA627A1F9F; _jzqco=%7C%7C%7C%7C%7C1.1001892331.1527574164472.1531190202351.1531193163987.1531190202351.1531193163987..0.0.161.161; mars_cid=1527574159932_457340df6cdd5de5a26b9715c01accd9");
                Response response = ProxyService.jsoupExecute(connection, "encrypt_id");
                link = response.url().toExternalForm();
                log.info("ads link: {}", link);
                Document document1 = response.parse();

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
                adsActivity.setLastUpdateTime(LocalDateTime.now());
                String title = document1.title();
                adsActivity.setName(title);

                // 这一步将广告插入 ads_activity表
                create.insertInto(ADS_ACTIVITY, ADS_ACTIVITY.ID, ADS_ACTIVITY.NAME, ADS_ACTIVITY.URL, ADS_ACTIVITY.IMG, ADS_ACTIVITY.TYPE, ADS_ACTIVITY.LAST_UPDATE_TIME)
                        .values(adsActivity.getId(), adsActivity.getName(), adsActivity.getUrl(), adsActivity.getImg(), adsActivity.getType(), adsActivity.getLastUpdateTime())
                        .onDuplicateKeyUpdate()
                        .set(ADS_ACTIVITY.LAST_UPDATE_TIME, adsActivity.getLastUpdateTime())
                        .execute();

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
                        Connection connection1 = connection.url(purchasesUrl)
                                .header("X-Requested-With", "XMLHttpRequest")
                                .header("Referer", link)
                                .header("Cookie", "oversea_jump=cn; _smt_uid=5b0cee94.35dcab2c; WAP[from]=www; WAP[p_wh]=VIP_SH; warehouse=VIP_SH; m_vip_province=103101; WAP[p_area]=%25E4%25B8%258A%25E6%25B5%25B7; WAP[area_id]=103101101; wap_consumer=A1; vip_city_name=%E5%8C%97%E4%BA%AC%E5%B8%82; mar_ref=oper_special_3_4; mars_pid=303; cps=%3A5c5jp4tz%3Aed2c07a7%3A57139_168_0__1%3Af58df3bbf8714762a83259d697c1fd03; VipUINFO=luc%3Aa%7Csuc%3Aa%7Cbct%3Ac_new%7Chct%3Ac_new%7Cbdts%3A0%7Cbcts%3A0%7Ckfts%3A0%7Cc10%3A0%7Crcabt%3A0%7Cp2%3A0%7Cp3%3A1%7Cp4%3A0%7Cp5%3A0; vip_address=%257B%2522pid%2522%253A%2522103101%2522%252C%2522pname%2522%253A%2522%255Cu4e0a%255Cu6d77%255Cu5e02%2522%252C%2522cid%2522%253A%2522103101101%2522%252C%2522cname%2522%253A%2522%255Cu5317%255Cu4eac%255Cu5e02%2522%252C%2522did%2522%253A101101101101%252C%2522dname%2522%253A%2522%255Cu4e1c%255Cu57ce%255Cu533a%2522%252C%2522sid%2522%253A911101101101%252C%2522sname%2522%253A%2522%255Cu4e1c%255Cu534e%255Cu95e8%255Cu8857%255Cu9053%2522%257D; vip_province=103101; vip_province_name=%E4%B8%8A%E6%B5%B7%E5%B8%82; vip_city_code=103101101; vip_wh=VIP_SH; vip_ipver=31; mst_csrf_key=56dfb498bca5e0e0ca63a75489348573; user_class=a; mst_consumer=A1; visit_id=D9801FFC9679861344859BCA627A1F9F; _jzqco=%7C%7C%7C%7C%7C1.1001892331.1527574164472.1531190202351.1531193163987.1531190202351.1531193163987..0.0.161.161; mstRedirect_0=%7B%22guide%22%3A%7B%225685729%22%3A%5B1531194110%2C5685733%5D%7D%2C%22cdi%22%3A%7B%225685733%22%3A%5B1531194110%2C5685733%5D%7D%7D; mars_sid=820e4ae920ce2d5cadc96d6ab7106901; mars_cid=1527574159932_457340df6cdd5de5a26b9715c01accd9");
                        String json = ProxyService.jsoupExecute(connection1, "code").body();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}