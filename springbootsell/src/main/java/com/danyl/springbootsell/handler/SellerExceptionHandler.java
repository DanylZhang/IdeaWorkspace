package com.danyl.springbootsell.handler;

import com.danyl.springbootsell.config.ProjectUrlConfig;
import com.danyl.springbootsell.exception.SellerAuthorizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class SellerExceptionHandler {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    //拦截登录异常
    @ExceptionHandler(value = SellerAuthorizeException.class)
    public ModelAndView handlerAuthorizeException() {
        // 从RequestContextHolder中获取当前被拦截的Url
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        StringBuffer returnUrl = servletRequestAttributes.getRequest().getRequestURL();

        String redirect = "redirect:"
                .concat(projectUrlConfig.getWechatMpAuthorize())
                .concat("/sell/seller/fakeQRLogin")
                .concat("?returnUrl=")
                .concat(returnUrl.toString());
        log.warn(redirect);

        return new ModelAndView(redirect);
    }
}
