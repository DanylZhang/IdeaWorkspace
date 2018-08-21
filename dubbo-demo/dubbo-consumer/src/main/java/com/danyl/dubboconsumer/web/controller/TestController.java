package com.danyl.dubboconsumer.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.danyl.dubboapi.entity.Proxy;
import com.danyl.dubboapi.service.ProxyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TestController {

    @Reference(check = false)
    private ProxyService proxyService;

    @GetMapping(path = {"/", "/index"})
    @ResponseBody
    public String index() {
        return "hello";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return proxyService.sayHello();
    }

    @GetMapping("/proxy")
    @ResponseBody
    public List<Proxy> proxy() {
        return proxyService.findAll();
    }

    @GetMapping("/findProxy")
    @ResponseBody
    public Object proxy(@RequestParam("ip") String ip,
                        @RequestParam(value = "callback", required = false) String callback) {
        List<Proxy> proxies = proxyService.findAllByIP(ip);

        // 支持jsonp callback
        if (StringUtils.isNotBlank(callback)) {
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(proxies);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return proxies;
    }
}
