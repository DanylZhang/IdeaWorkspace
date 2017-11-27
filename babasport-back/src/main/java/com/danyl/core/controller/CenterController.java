package com.danyl.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/control")
public class CenterController {
    @RequestMapping(value = "index.html")
    public String index() {
        System.out.println(111);
        return "index";
    }
}