package com.danyl.core.controller;

import com.danyl.common.web.session.SessionProvider;
import com.danyl.core.bean.user.Buyer;
import com.danyl.core.service.buyer.BuyerService;
import com.danyl.core.web.Constants;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller
public class LoginController {
    @Autowired
    private SessionProvider sessionProvider;
    @Autowired
    private BuyerService buyerService;

    @GetMapping(value = "/shopping/login.html")
    public String login() {
        return "buyer/login";
    }

    public String encodePassword(String password) {
        String algorithm = "MD5";
        char[] chars = null;
        try {
            MessageDigest md5Digest = MessageDigest.getInstance(algorithm);
            byte[] digest = md5Digest.digest(password.getBytes());
            chars = Hex.encodeHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(chars);
    }

    @PostMapping(value = "/shopping/login.html")
    public String login(String username, String password, String captcha, String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {
        //1.验证码不能为空
        if (captcha != null) {
            String captcha1 = sessionProvider.getAttributeForCaptcha(request, response, Constants.CAPTCHA_NAME);
            //2.验证码必须正确
            if (captcha.equalsIgnoreCase(captcha1)) {
                //3.用户名不能为空
                if (username != null) {
                    //4.密码不能为空
                    if (password != null) {
                        //5.用户名必须正确
                        Buyer buyer = buyerService.selectBuyerByUserName(username);
                        if (buyer != null) {
                            //6.密码必须正确
                            if (encodePassword(password).equals(buyer.getPassword())) {
                                //7.把用户名放入session
                                sessionProvider.setAttributeForUserName(request, response, Constants.USER_NAME, buyer.getUsername());
                                //8.返回之前访问页面
                                return "redirect:" + returnUrl;
                            } else {
                                model.addAttribute("error", "密码必须正确");
                                return "buyer/login";
                            }
                        } else {
                            model.addAttribute("error", "用户名必须正确");
                            return "buyer/login";
                        }
                    } else {
                        model.addAttribute("error", "密码不能为空");
                        return "buyer/login";
                    }
                } else {
                    model.addAttribute("error", "用户名不能为空");
                    return "buyer/login";
                }
            } else {
                model.addAttribute("error", "验证码必须正确");
                return "buyer/login";
            }
        } else {
            model.addAttribute("error", "验证码不能为空");
            return "buyer/login";
        }
    }

    //验证码生成
    @RequestMapping(value = "/shopping/captcha.html")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("#######################生成数字和字母的验证码#######################");
        BufferedImage img = new BufferedImage(68, 22, BufferedImage.TYPE_INT_RGB);
        // 得到该图片的绘图对象
        Graphics g = img.getGraphics();

        Random r = new Random();

        Color c = new Color(200, 150, 255);
        g.setColor(c);

        // 填充整个图片的颜色
        g.fillRect(0, 0, 68, 22);

        // 向图片中输出数字和字母
        StringBuffer sb = new StringBuffer();

        char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

        int index, len = ch.length;
        for (int i = 0; i < 4; i++) {
            index = r.nextInt(len);
            g.setColor(new Color(r.nextInt(88), r.nextInt(188), r.nextInt(255)));
            g.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
            // 输出的  字体和大小
            g.drawString("" + ch[index], (i * 15) + 3, 18);
            //写什么数字，在图片 的什么位置画
            sb.append(ch[index]);
        }
        //把上面生成的验证码放到Session域中
        sessionProvider.setAttributeForCaptcha(request, response, Constants.CAPTCHA_NAME, sb.toString());
        try {
            ImageIO.write(img, "JPG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}