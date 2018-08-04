package com.danyl.spiders.tasks;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

import static com.danyl.spiders.constants.TimeConstants.HOURS;

@Slf4j
@Component
public class XiaoMiTask {

    @Resource(name = "DSLContextDangDang")
    private DSLContext create;

    @Scheduled(fixedDelay = HOURS * 8)
    public void crawlVipAds() {
        log.info("crawl vip ads start {}", new Date());

        log.info("crawl vip ads end {}", new Date());
    }
}