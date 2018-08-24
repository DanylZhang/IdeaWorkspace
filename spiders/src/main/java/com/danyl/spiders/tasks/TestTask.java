package com.danyl.spiders.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class TestTask {

    // 校验可用的代理
    @Scheduled(fixedDelay = 1000 * 3)
    public void test() {
        int count = 0;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            log.info(String.format("%s do do do ...", count++));
        }
    }
}