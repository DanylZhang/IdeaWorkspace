package com.danyl.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/control")
public class CenterController {
    @RequestMapping(value = "index.html")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "top.html")
    public String top() {
        return "top";
    }

    @RequestMapping(value = "main.html")
    public String main() {
        return "main";
    }

    @RequestMapping(value = "left.html")
    public String left() {
        return "left";
    }

    @RequestMapping(value = "right.html")
    public String right() {
        return "right";
    }
}