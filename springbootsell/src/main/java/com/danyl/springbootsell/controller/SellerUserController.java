package com.danyl.springbootsell.controller;

import com.danyl.springbootsell.config.ProjectUrlConfig;
import com.danyl.springbootsell.constant.CookieConstant;
import com.danyl.springbootsell.constant.RedisConstant;
import com.danyl.springbootsell.entity.SellerInfo;
import com.danyl.springbootsell.enums.ResultEnum;
import com.danyl.springbootsell.service.SellerService;
import com.danyl.springbootsell.service.WebSocket;
import com.danyl.springbootsell.utils.CookieUtil;
import com.danyl.springbootsell.utils.KeyUtil;
import com.danyl.springbootsell.utils.QRCodeUtil;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sell/seller")
@Slf4j
public class SellerUserController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Autowired
    private WebSocket webSocket;

    @GetMapping("/fakeQRLogin")
    public ModelAndView fakeQRLogin(@RequestParam("returnUrl") String returnUrl,
                                    Map<String, Object> map) {
        String qrUUID = KeyUtil.genUUIDWithoutDelimiter();
        map.put("qrUUID", qrUUID);
        map.put("returnUrl", returnUrl);
        return new ModelAndView("login/fakeQRLogin", map);
    }

    @GetMapping("/fakeQRLogin/getQRCode/{qrUUID}")
    public void getQRCode(@PathVariable("qrUUID") String qrUUID,
                          HttpServletResponse response,
                          Map<String, Object> map) {

        String content = "".concat(projectUrlConfig.getWechatMpAuthorize())
                .concat("/sell/wechat/fakeQRAuthorize")
                .concat("?returnUrl=")
                .concat(projectUrlConfig.getWechatMpAuthorize())
                .concat("/sell/seller/fakeQRLogin/getAckPage")
                .concat("/" + qrUUID);
        try {
            File file = ResourceUtils.getFile("classpath:static/logo.jpg");
            String absolutePath = file.getAbsolutePath();
            BufferedImage bufferedImage = QRCodeUtil.getBufferedImage(content, 300, absolutePath);

            OutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpg");

            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("【获取登录二维码】失败，{}", e);
        }
    }

    @GetMapping("/fakeQRLogin/getAckPage/{qrUUID}")
    public ModelAndView getAckPage(@PathVariable("qrUUID") String qrUUID,
                                   @RequestParam("openid") String openid,
                                   Map<String, Object> map) {
        Map<String, String> webSocketResponseMap = new HashMap<>();
        webSocketResponseMap.put("code", "302");
        webSocketResponseMap.put("qrUUID", qrUUID);
        webSocketResponseMap.put("openid", openid);
        webSocket.sendMessageToUser(qrUUID, new GsonBuilder().create().toJson(webSocketResponseMap));

        map.put("qrUUID", qrUUID);
        map.put("openid", openid);
        return new ModelAndView("login/ackPage", map);
    }

    @GetMapping("/fakeQRLogin/ack/{qrUUID}")
    public ModelAndView ackLogin(@PathVariable("qrUUID") String qrUUID,
                                 @RequestParam("openid") String openid,
                                 Map<String, Object> map) {
        Map<String, String> webSocketResponseMap = new HashMap<>();
        webSocketResponseMap.put("code", "200");
        webSocketResponseMap.put("qrUUID", qrUUID);
        webSocketResponseMap.put("openid", openid);
        webSocket.sendMessageToUser(qrUUID, new GsonBuilder().create().toJson(webSocketResponseMap));
        map.put("msg", "200");
        return new ModelAndView("login/ackResult", map);
    }

    @GetMapping("/fakeQRLogin/cancel/{qrUUID}")
    public ModelAndView cancelLogin(@PathVariable("qrUUID") String qrUUID,
                                    @RequestParam("openid") String openid,
                                    Map<String, Object> map) {
        Map<String, String> webSocketResponseMap = new HashMap<>();
        webSocketResponseMap.put("code", "400");
        webSocketResponseMap.put("qrUUID", qrUUID);
        webSocketResponseMap.put("openid", openid);
        webSocket.sendMessageToUser(qrUUID, new GsonBuilder().create().toJson(webSocketResponseMap));
        map.put("msg", "400");
        return new ModelAndView("login/ackResult", map);
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam("openid") String openid,
                              @RequestParam(value = "returnUrl", required = false) String returnUrl,
                              HttpServletResponse response,
                              Map<String, Object> map) {

        //1. openid去和数据库里的数据匹配
        SellerInfo sellerInfo = sellerService.findSellerInfoByOpenid(openid);
        if (sellerInfo == null) {
            map.put("msg", ResultEnum.LOGIN_FAIL.getMessage());
            map.put("url", "/sell/seller/order/list");
            return new ModelAndView("common/error", map);
        }
        //2. 设置token至redis
        String token = KeyUtil.genUUIDWithoutDelimiter();
        Integer expire = RedisConstant.EXPIRE;

        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), openid, expire, TimeUnit.SECONDS);

        //3. 设置token至cookie
        CookieUtil.set(response, CookieConstant.TOKEN, token, expire);

        if (StringUtils.isEmpty(returnUrl)) {
            return new ModelAndView("redirect:" + projectUrlConfig.getSell() + "/sell/seller/order/list");
        } else {
            return new ModelAndView("redirect:" + returnUrl);
        }
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response,
                               Map<String, Object> map) {
        //1. 从cookie里查询
        Cookie token = CookieUtil.get(request, CookieConstant.TOKEN);
        if (token != null) {
            //2.清除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX, token.getValue()));

            //3.清除cookie
            CookieUtil.set(response, CookieConstant.TOKEN, null, 0);
        }
        map.put("msg", ResultEnum.LOGOUT_SUCCESS.getMessage());
        map.put("url", "/sell/seller/order/list");
        return new ModelAndView("common/success", map);
    }
}
