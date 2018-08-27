package com.danyl.spiders.downloader;

import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
public class OkHttpDownloader {

    private static ProxyService proxyService = ProxyService.getInstance();

    /**
     * @param url   通过此url测试连通性
     * @param regex 校验正则表达式
     */
    public static Pair<Boolean, Integer> getPage(String url, String regex) {
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(ProxyUtil.getProxy(proxyService.get(url)))
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();
        Request request = new Request.Builder().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .build();
        Call call = okHttpClient.newCall(request);
        long start = System.currentTimeMillis();
        try (Response response = call.execute()) {
            long end = System.currentTimeMillis();
            int costTime = (int) (end - start);
            // 超过半分钟就算超时
            if (costTime > TIMEOUT) {
                return Pair.of(false, costTime);
            }

            String res = response.body().string();
            if (Pattern.compile(regex).matcher(res).find()) {
                return Pair.of(true, costTime);
            }
        } catch (Exception ignored) {
        }
        return Pair.of(false, Integer.MAX_VALUE);
    }
}
