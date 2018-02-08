package com.danyl.core.web.springmvc;

import com.danyl.common.web.session.SessionProvider;
import com.danyl.core.web.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 拦截器
 * 用户是否登陆
 */
public class SpringmvcInterceptor implements HandlerInterceptor {
    @Autowired
    private SessionProvider sessionProvider;

    //必须登陆的请求规则
    private static final String URL_INTERCEPTOR = "/buyer";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //判断用户是否登陆
        String userName = sessionProvider.getAttributeForUserName(httpServletRequest, httpServletResponse, Constants.USER_NAME);
        if (userName != null) {
            httpServletRequest.setAttribute("isLogin", true);
        } else {
            if (httpServletRequest.getRequestURI().startsWith(URL_INTERCEPTOR)) {
                httpServletResponse.sendRedirect("http://localhost:8081/shopping/login.html?returnUrl="+ URLEncoder.encode(httpServletRequest.getRequestURL().toString(),"UTF-8"));
                return false;
            }
            httpServletRequest.setAttribute("isLogin", false);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}