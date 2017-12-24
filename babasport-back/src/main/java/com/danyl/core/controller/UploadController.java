package com.danyl.core.controller;

import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Controller
@RequestMapping(value = "/upload")
public class UploadController {

    @ResponseBody
    @RequestMapping(value = "uploadPic.html", method = RequestMethod.POST)
    public void uploadPic(@RequestParam(required = false) MultipartFile pic, HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println(pic.getOriginalFilename());

        //step 1: 图片名称生成策略
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmssSSS");
        String format = dateFormat.format(new Date());
        StringBuilder name = new StringBuilder(format);
        // 三位随机数
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            name.append(random.nextInt(10));
        }
        //step 2: 保存图片
        String extension = FilenameUtils.getExtension(pic.getOriginalFilename());
        String path = "/upload/../" + name + "." + extension;
        // 获取保存文件全路径
        String uri = request.getSession().getServletContext().getRealPath("") + path;
        System.out.println(uri);
        // 文件保存要用绝对路径
        pic.transferTo(new File(uri));
        response.setContentType("application/json;charset=UTF-8");
        JsonObject jsonObject = new JsonObject();
        // json返回保存的相对路径，正好可以回显在img上
        jsonObject.addProperty("path", path);
        response.getWriter().write(jsonObject.toString());
    }
}