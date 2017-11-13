package springmvc.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        System.out.println("Hello World");
        String user = httpServletRequest.getParameter("user");
        System.out.println(user);

        //httpServletRequest.setAttribute("key","今天天气转凉了");
        //httpServletRequest.getRequestDispatcher("index.jsp").forward(httpServletRequest,httpServletResponse);

        ModelAndView mv = new ModelAndView();
        mv.addObject("key","今天天气转凉了");
        mv.setViewName("index.jsp");
        return mv;
    }
}