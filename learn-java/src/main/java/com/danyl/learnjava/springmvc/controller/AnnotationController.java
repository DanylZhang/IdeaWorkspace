package com.danyl.learnjava.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class AnnotationController {
    @RequestMapping("demo1")
    public ModelAndView method1(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        ModelAndView mv = new ModelAndView();
        mv.addObject("key","今天天气转凉了");
        mv.setViewName("forward:index.jsp");
        return mv;
    }
    @RequestMapping("demo2")
    public ModelAndView method2(HttpServletRequest httpServletRequest, HttpSession session){
        System.out.println(session);
        ModelAndView mv = new ModelAndView();
        mv.addObject("key","今天天气转凉了");
        mv.setViewName("forward:index.jsp");
        return mv;
    }
    //demo2 和 demo3 表明一个action必须有一个return ModelAndView或一个HttpServletResponse
    @RequestMapping("demo3")
    public ModelAndView method3(HttpServletResponse httpServletResponse, HttpSession session){
        System.out.println(session);
        ModelAndView mv = new ModelAndView();
        mv.addObject("key","今天天气转凉了");
        mv.setViewName("forward:index.jsp");
        return null;
    }
}