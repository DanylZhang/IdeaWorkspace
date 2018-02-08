package com.danyl.common.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpSessionProviderImpl implements SessionProvider {

    @Override
    public String getSessionId(HttpServletRequest request, HttpServletResponse response) {
        return request.getSession().getId();
    }

    @Override
    public void setAttributeForUserName(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        HttpSession session = request.getSession();
        session.setAttribute(name,value);
    }

    @Override
    public void setAttributeForCaptcha(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        HttpSession session = request.getSession();
        session.setAttribute(name,value);
    }

    @Override
    public String getAttributeForUserName(HttpServletRequest request, HttpServletResponse response, String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(name);
        }
        return null;
    }

    @Override
    public String getAttributeForCaptcha(HttpServletRequest request, HttpServletResponse response, String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(name);
        }
        return null;
    }
}