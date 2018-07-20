package com.danyl.spiders;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpidersApplicationTests {

    @Test
    public void jsoupTest() {
        try {
            System.out.println(Jsoup.connect("https://www.vip.com")
                    .proxy("124.235.208.252", 443)
                    .get().html());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
