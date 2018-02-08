package com.danyl.core.controller;

import com.danyl.common.web.session.SessionProvider;
import com.danyl.core.bean.BuyerCart;
import com.danyl.core.bean.BuyerItem;
import com.danyl.core.bean.order.Order;
import com.danyl.core.bean.product.Sku;
import com.danyl.core.service.order.OrderService;
import com.danyl.core.service.product.SkuService;
import com.danyl.core.web.Constants;
import com.google.common.collect.SetMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.el.parser.BooleanNode;
import org.ehcache.UserManagedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class BuyerCartController {
    @Autowired
    private SkuService skuService;
    @Autowired
    private SessionProvider sessionProvider;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/shopping/buyerCart.html")
    public String buyerCart(Integer skuId, Integer amount, Model model, HttpServletRequest request, HttpServletResponse response) {
        BuyerCart buyerCart = null;
        //1.获取cookie中的商品
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (Constants.BUYER_CART.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    buyerCart = new Gson().fromJson(value, BuyerCart.class);
                    break;
                }
            }
        }
        //2.已有，将商品追加到购物车
        //3.没有，创建购物车对象，并追加
        if (buyerCart == null) {
            buyerCart = new BuyerCart();
        }

        //拿到登录用户名
        String userName = sessionProvider.getAttributeForUserName(request, response, Constants.USER_NAME);
        //登录用户购物车存redis
        if (userName != null) {
            Jedis jedis = jedisPool.getResource();
            //1.将第一步从cookie中拿到的购物车信息存进redis
            List<BuyerItem> buyerItems = buyerCart.getBuyerItems();
            if (buyerItems.size() > 0) {
                for (BuyerItem buyerItem : buyerItems) {
                    //像List<buyerItem> 一样判断redis中是否已经存在同款商品
                    jedis.zadd(Constants.BUYER_CART + userName, 1f, buyerItem.getSku().getId().toString());
                    Boolean hexists = jedis.hexists(Constants.BUYER_ITEM + userName, buyerItem.getSku().getId().toString());
                    if (hexists) {
                        jedis.hincrBy(Constants.BUYER_ITEM + userName, buyerItem.getSku().getId().toString(), buyerItem.getAmount());
                    } else {
                        jedis.hset(Constants.BUYER_ITEM + userName, buyerItem.getSku().getId().toString(), buyerItem.getAmount().toString());
                    }
                }
            }
            //2.新商品存进redis
            if (skuId != null) {
                //像List<buyerItem> 一样判断redis中是否已经存在同款商品
                jedis.zadd(Constants.BUYER_CART + userName, 1f, skuId.toString());
                Boolean hexists = jedis.hexists(Constants.BUYER_ITEM + userName, skuId.toString());
                if (hexists) {
                    jedis.hincrBy(Constants.BUYER_ITEM + userName, skuId.toString(), amount);
                } else {
                    jedis.hset(Constants.BUYER_ITEM + userName, skuId.toString(), amount.toString());
                }
            }
            //3.用redis里的购物车信息填充buyerCart
            buyerCart.clear();
            Set<String> zrange = jedis.zrange(Constants.BUYER_CART + userName, 0, -1);
            if (zrange != null && zrange.size() > 0) {
                for (String key : zrange) {
                    Sku sku = new Sku();
                    sku.setId(Integer.parseInt(key));
                    BuyerItem buyerItem = new BuyerItem();
                    buyerItem.setSku(sku);
                    buyerItem.setAmount(Integer.parseInt(jedis.hget(Constants.BUYER_ITEM + userName, key)));
                    buyerCart.addItem(buyerItem);
                }
            }
            //4.最后清空用户浏览器端的cookie
            Cookie cookie = new Cookie(Constants.BUYER_CART, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            //关闭redis连接
            jedis.close();
        }
        //非登录用户继续存cookie
        else {
            //追加商品到购物车
            if (skuId != null) {
                Sku sku = new Sku();
                sku.setId(skuId);
                BuyerItem buyerItem = new BuyerItem();
                buyerItem.setSku(sku);
                buyerItem.setAmount(amount);
                //加进购物车
                buyerCart.addItem(buyerItem);
                //4.将购物车写回cookie
                Cookie cookie = new Cookie(Constants.BUYER_CART, new Gson().toJson(buyerCart));
                cookie.setMaxAge(-1);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        //倒排，使刚加入的显示在上面
        Collections.sort(buyerCart.getBuyerItems(), (o1, o2) -> {
            // -1 倒序 0 不动 1 正序
            return -1;
        });
        //5.通过skuId查询数据填充购物车对象各字段
        List<BuyerItem> buyerItems = buyerCart.getBuyerItems();
        if (buyerItems.size() > 0) {
            for (BuyerItem buyerItem : buyerItems) {
                Sku sku = skuService.selectSkuById(buyerItem.getSku().getId());
                buyerItem.setSku(sku);
            }
        }
        //6.Model购物车
        model.addAttribute("buyerCart", buyerCart);
        //7.跳转购物车页面
        return "product/cart";
    }

    @RequestMapping(value = "/shopping/delProduct.html")
    public String delProduct(Integer skuId, HttpServletRequest request, HttpServletResponse response) {
        BuyerCart buyerCart = null;
        //1.拿到登录用户名
        String userName = sessionProvider.getAttributeForUserName(request, response, Constants.USER_NAME);
        //登录用户购物车删redis
        if (userName != null) {
            Jedis jedis = jedisPool.getResource();
            jedis.zrem(Constants.BUYER_CART + userName, skuId.toString());
            jedis.hdel(Constants.BUYER_ITEM + userName, skuId.toString());
            jedis.close();
        } else {
            //1.从cookie中取购物车
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (Constants.BUYER_CART.equals(cookie.getName())) {
                        buyerCart = new Gson().fromJson(cookie.getValue(), BuyerCart.class);
                        break;
                    }
                }
            }
            //2.删除商品
            if (buyerCart != null) {
                buyerCart.removeItem(skuId);
            }
            //3.将cookie回填
            Cookie cookie = new Cookie(Constants.BUYER_CART, new Gson().toJson(buyerCart));
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return "redirect:/shopping/cart.html";
    }

    @RequestMapping(value = "/shopping/clearCart.html")
    public String clearCart(HttpServletRequest request, HttpServletResponse response) {
        //1.将cookie中的Constants.BUYER_CART项删掉
        Cookie cookie = new Cookie(Constants.BUYER_CART, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        //2.拿到登录用户名
        String userName = sessionProvider.getAttributeForUserName(request, response, Constants.USER_NAME);
        //登录用户购物车删redis
        if (userName != null) {
            Jedis jedis = jedisPool.getResource();
            jedis.del(Constants.BUYER_CART + userName);
            jedis.del(Constants.BUYER_ITEM + userName);
            jedis.close();
        }
        return "redirect:/shopping/cart.html";
    }

    @RequestMapping(value = "/shopping/updateCart.html")
    public void updateCart(Integer skuId, Integer amount, HttpServletRequest request, HttpServletResponse response) throws IOException {
        BuyerCart buyerCart = null;
        //1.获取cookie中的商品
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (Constants.BUYER_CART.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    buyerCart = new Gson().fromJson(value, BuyerCart.class);
                    break;
                }
            }
        }
        //2.已有，将商品追加到购物车
        //3.没有，创建购物车对象，并追加
        if (buyerCart == null) {
            buyerCart = new BuyerCart();
        }
        //拿到登录用户名
        String userName = sessionProvider.getAttributeForUserName(request, response, Constants.USER_NAME);
        //登录用户购物车存redis
        if (userName != null) {
            Jedis jedis = jedisPool.getResource();
            if (skuId != null) {
                jedis.hincrBy(Constants.BUYER_ITEM + userName, skuId.toString(), amount);
            }
            //用redis里的购物车信息填充buyerCart
            buyerCart.clear();
            Set<String> zrange = jedis.zrange(Constants.BUYER_CART + userName, 0, -1);
            if (zrange != null && zrange.size() > 0) {
                for (String key : zrange) {
                    Sku sku = new Sku();
                    sku.setId(Integer.parseInt(key));
                    BuyerItem buyerItem = new BuyerItem();
                    buyerItem.setSku(sku);
                    buyerItem.setAmount(Integer.parseInt(jedis.hget(Constants.BUYER_ITEM + userName, key)));
                    buyerCart.addItem(buyerItem);
                }
            }
            //4.最后清空用户浏览器端的cookie
            Cookie cookie = new Cookie(Constants.BUYER_CART, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            //关闭redis连接
            jedis.close();
        }
        //非登录用户继续存cookie
        else {
            //追加商品到购物车
            if (skuId != null) {
                Sku sku = new Sku();
                sku.setId(skuId);
                BuyerItem buyerItem = new BuyerItem();
                buyerItem.setSku(sku);
                buyerItem.setAmount(amount);
                //加进购物车，内部会进行合并
                buyerCart.addItem(buyerItem);
                //4.将购物车写回cookie
                Cookie cookie = new Cookie(Constants.BUYER_CART, new Gson().toJson(buyerCart));
                cookie.setMaxAge(-1);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        //5.通过skuId查询数据填充购物车对象各字段
        List<BuyerItem> buyerItems = buyerCart.getBuyerItems();
        if (buyerItems.size() > 0) {
            for (BuyerItem buyerItem : buyerItems) {
                Sku sku = skuService.selectSkuById(buyerItem.getSku().getId());
                buyerItem.setSku(sku);
            }
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productPrice", buyerCart.getProductPrice());
        jsonObject.addProperty("deliveFee", buyerCart.getDeliveFee());
        jsonObject.addProperty("totalPrice", buyerCart.getTotalPrice());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());
    }

    @RequestMapping(value = "/buyer/trueBuy.html")
    public String trueBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
        //1.必须登录，此功能在SpringmvcInterceptor中实现拦截
        String userName = sessionProvider.getAttributeForUserName(request, response, Constants.USER_NAME);
        //2.购物车必须有商品
        Jedis jedis = jedisPool.getResource();
        Set<String> zrange = jedis.zrange(Constants.BUYER_CART + userName, 0, -1);
        //购物车有商品
        if (zrange != null && zrange.size() > 0) {
            //3.购物车中商品必须有足够的库存
            //遍历购物车中每个商品，设置buyerItem.setHaveInventory
            Boolean flag = true;
            BuyerCart buyerCart = new BuyerCart();
            for (String skuId : zrange) {
                BuyerItem buyerItem = new BuyerItem();
                Integer amount = Integer.parseInt(jedis.hget(Constants.BUYER_ITEM + userName, skuId));
                buyerItem.setAmount(amount);
                Sku sku = skuService.selectSkuById(Integer.parseInt(skuId));
                buyerItem.setSku(sku);
                if (amount > buyerItem.getSku().getSkuUpperLimit()) {
                    buyerItem.setHaveInventory(false);
                    flag = false;
                }
                //填充购物车对象
                buyerCart.addItem(buyerItem);
            }
            jedis.close();
            if (flag) {
                return "product/productOrder";
            } else {
                model.addAttribute("buyerCart", buyerCart);
                return "product/cart";
            }
        }
        //购物车无商品
        else {
            return "redirect:/shopping/buyerCart.html";
        }
    }

    @RequestMapping(value = "/buyer/confirmOrder.html")
    public String confirmOrder(Order order, HttpServletRequest request, HttpServletResponse response,Model model) {
        BuyerCart buyerCart = new BuyerCart();
        Jedis jedis = jedisPool.getResource();

        //1.拿到登录用户名
        String userName = sessionProvider.getAttributeForUserName(request, response, Constants.USER_NAME);
        //订单设置用户名
        order.setBuyerId(userName);
        if (userName != null) {
            //2.用redis里的购物车信息填充buyerCart
            Set<String> zrange = jedis.zrange(Constants.BUYER_CART + userName, 0, -1);
            if (zrange != null && zrange.size() > 0) {
                for (String skuId : zrange) {
                    Sku sku = skuService.selectSkuById(Integer.parseInt(skuId));
                    BuyerItem buyerItem = new BuyerItem();
                    buyerItem.setSku(sku);
                    buyerItem.setAmount(Integer.parseInt(jedis.hget(Constants.BUYER_ITEM + userName, skuId)));
                    buyerCart.addItem(buyerItem);
                }
            }
            Long oid = orderService.insertOrder(order, buyerCart);
            model.addAttribute("oid",oid);
            model.addAttribute("totalPrice",buyerCart.getTotalPrice());
        }

        //清空购物车
        jedis.del(Constants.BUYER_CART + userName, Constants.BUYER_ITEM + userName);

        return "product/confirmOrder";
    }
}