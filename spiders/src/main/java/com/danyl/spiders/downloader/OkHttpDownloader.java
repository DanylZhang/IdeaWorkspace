package com.danyl.spiders.downloader;

import com.danyl.spiders.service.ProxyService;
import com.danyl.spiders.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.TimeConstants.TIMEOUT;

@Slf4j
public class OkHttpDownloader {
    private static ProxyService proxyService = ProxyService.getInstance();

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);

    /**
     * @param url   通过此url测试连通性
     * @param regex 校验正则表达式
     */
    public static String getPage(String url, String regex) {
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .proxy(ProxyUtil.getProxy(proxyService.get(url)))
                .followSslRedirects(true)
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

        final AtomicReference<String> atomicReference = new AtomicReference<>("");
        CountDownLatch latch = new CountDownLatch(1);
        fixedThreadPool.submit(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .build();
            Call call = okHttpClient.newCall(request);
            try (Response response = call.execute()) {
                String res = response.body().string();
                if (Pattern.compile(regex).matcher(res).find()) {
                    atomicReference.set(res);
                }
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(TIMEOUT * 2, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("OkHttpDownloader error: {}", e.getMessage());
        }
        return atomicReference.get();
    }
}
