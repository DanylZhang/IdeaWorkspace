package com.danyl.spiders.web.controller;

import com.danyl.spiders.jooq.gen.proxy.tables.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;

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
        Result<ProxyRecord> fetch = proxy.selectFrom(Proxy.PROXY)
                .where(Proxy.PROXY.IS_VALID.eq(true).and(Proxy.PROXY.ANONYMITY.notLikeRegex("L1|L2|L3|L4")))
                .fetch();
        String[] strings = fetch.intoArray(Proxy.PROXY.ANONYMITY);
        System.out.println(Arrays.toString(strings));
        return "" + fetch.size();
    }
}