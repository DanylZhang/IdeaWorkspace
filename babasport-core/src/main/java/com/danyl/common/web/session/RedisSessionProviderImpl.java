package com.danyl.common.web.session;

import com.danyl.core.web.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class RedisSessionProviderImpl implements SessionProvider {
    @Autowired
    private Jedis jedis;
    @Autowired
    private JedisPool jedisPool;

    //用户的SESSION超时时间
    private Integer expire = 30;

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    @Override
    public String getSessionId(HttpServletRequest request, HttpServletResponse response) {
        //1.从cookie中获取SESSION_ID
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (Constants.SESSION_ID.equals(cookie.getName())) {
                    //有 直接用
                    return cookie.getValue();
                }
            }
        }

        //2.没有 生成一个 asdfad-asdfa-asfdasf-asdfa-asdfasd  36 -> 32
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        //3.没有的话 保存SESSION_ID到cookie中
        Cookie cookie = new Cookie(Constants.SESSION_ID, sessionId);
        //存活时间 setMaxAge 小于0:浏览器关闭就失效，等于0：立即失效，一般用来删除浏览器端已保存的cookie 大于0:保存多少秒
        cookie.setMaxAge(60 * this.expire);
        //设置路径 http://localhost:8081/product/detail.html
        cookie.setPath("/");
        //写回浏览器
        response.addCookie(cookie);
        return null;
    }

    @Override
    public void setAttributeForUserName(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        //Constants.USER_NAME + 32位字符串(JSESSIONID)
        jedis.set(name + getSessionId(request, response), value);
        jedis.expire(name + getSessionId(request, response), 60 * this.expire);
    }

    @Override
    public void setAttributeForCaptcha(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        Jedis jedis = jedisPool.getResource();
        //Constants.CAPTCHA_NAME + 32位字符串(JSESSIONID)
        jedis.set(name + getSessionId(request, response), value);
        jedis.expire(name + getSessionId(request, response), 60);
        jedis.close();
    }

    @Override
    public String getAttributeForUserName(HttpServletRequest request, HttpServletResponse response, String name) {
        Jedis jedis = jedisPool.getResource();
        String userName = jedis.get(name + getSessionId(request, response));
        jedis.close();
        return userName;
    }

    @Override
    public String getAttributeForCaptcha(HttpServletRequest request, HttpServletResponse response, String name) {
        Jedis jedis = jedisPool.getResource();
        String captcha = jedis.get(name + getSessionId(request, response));
        jedis.close();
        return captcha;
    }
}