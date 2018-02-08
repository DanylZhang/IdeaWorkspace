package com.danyl.common.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SessionProvider {
    //获取SessionID
    public String getSessionId(HttpServletRequest request, HttpServletResponse response);

    //把用户名放到session中
    public void setAttributeForUserName(HttpServletRequest request, HttpServletResponse response, String name, String value);

    //把验证码放到session中
    public void setAttributeForCaptcha(HttpServletRequest request, HttpServletResponse response, String name, String value);

    //把用户名从session中取出
    public String getAttributeForUserName(HttpServletRequest request, HttpServletResponse response, String name);

    //把验证码从session中取出
    public String getAttributeForCaptcha(HttpServletRequest request, HttpServletResponse response, String name);

    //退出登陆
}