package springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import springmvc.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ValueController {
    //最原始的传参方式，从httpServletRequest对象中获取参数
    @RequestMapping("/value1")
    public ModelAndView value1(HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        httpServletRequest.setCharacterEncoding("utf-8");
        System.out.println("ValueController.value1()");
        String userName = httpServletRequest.getParameter("userName");
        String password = httpServletRequest.getParameter("password");
        User user = new User(userName, password);
        System.out.println(user);
        ModelAndView mv = new ModelAndView("input.jsp");
        return mv;
    }

    //前台参数的key和后台方法的形参名对应就能自动注入
    @RequestMapping("/value2")
    public ModelAndView value2(String userName, String password) {
        System.out.println("ValueController.value2()");
        User user = new User(userName, password);
        System.out.println(user);
        ModelAndView mv = new ModelAndView("input.jsp");
        return mv;
    }

    @RequestMapping("/value3")
    public ModelAndView value3(@RequestParam(value = "name", required = false) String userName, String password) {
        System.out.println("ValueController.value3()");
        User user = new User(userName, password);
        System.out.println(user);
        ModelAndView mv = new ModelAndView("input.jsp");
        return mv;
    }

    //对象传参方式，直接接收一个对象，该对象需要有无参构造函数和对应的形参构造函数，以及getter和setter
    @RequestMapping("/value4")
    public ModelAndView value4(User user) {
        System.out.println("ValueController.value4()");
        System.out.println(user);
        ModelAndView mv = new ModelAndView("input.jsp");
        return mv;
    }

    //地址栏传参
    @RequestMapping("/delete/{id}")
    public ModelAndView value5(@PathVariable("id") Long id) {
        System.out.println("delete");
        System.out.println(id);
        ModelAndView mv = new ModelAndView("input.jsp");
        return mv;
    }
}