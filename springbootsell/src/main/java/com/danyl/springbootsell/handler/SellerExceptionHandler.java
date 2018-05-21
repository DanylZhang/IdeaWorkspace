package com.danyl.springbootsell.handler;

import com.danyl.springbootsell.VO.ResultVO;
import com.danyl.springbootsell.config.ProjectUrlConfig;
import com.danyl.springbootsell.exception.SellException;
import com.danyl.springbootsell.exception.SellerAuthorizeException;
import com.danyl.springbootsell.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class SellerExceptionHandler {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    //处理登录异常
    @ExceptionHandler(value = SellerAuthorizeException.class)
    public ModelAndView handlerAuthorizeException() {
        // 从RequestContextHolder中获取当前被拦截的Url
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        StringBuffer returnUrl = servletRequestAttributes.getRequest().getRequestURL();

        String redirect = "redirect:"
                .concat(projectUrlConfig.getWechatMpAuthorize())
                .concat("/sell/seller/fakeQRLogin")
                .concat("?returnUrl=")
                .concat(returnUrl.toString());
        log.warn(redirect);

        return new ModelAndView(redirect);
    }

    //其他异常统一处理
    @ExceptionHandler(value = SellException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultVO handlerSellerException(SellException e) {
        return ResultVOUtil.error(e.getCode(), e.getMessage());
    }
}
