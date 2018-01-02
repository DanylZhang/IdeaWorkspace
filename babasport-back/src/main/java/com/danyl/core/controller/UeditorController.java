package com.danyl.core.controller;

import com.danyl.common.fastdfs.FastDFSUtils;
import com.danyl.common.util.Utils;
import com.danyl.core.web.Constants;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = "/res/ueditor")
public class UeditorController {
    //上传Ueditor
    @ResponseBody
    @RequestMapping(value = "controller.html")
    public void uploadFck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getMethod().equals("GET")) {
            response.setContentType("application/json;charset=UTF-8");
            JsonObject jsonObject = new JsonObject();
            // json返回保存的相对路径，正好可以回显在img上
            jsonObject.addProperty("responseText", "图片上传功能可用");
            response.getWriter().write(jsonObject.toString());
            return;
        }

        //强转request
        MultipartRequest multipartRequest = (MultipartRequest) request;
        //取图片，支持多张
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Set<Map.Entry<String, MultipartFile>> entries = fileMap.entrySet();
        for (Map.Entry<String, MultipartFile> fileEntry : entries) {
            //图片
            MultipartFile pic = fileEntry.getValue();
            //fastDFS
            try {
                String path = FastDFSUtils.uploadPic(pic);
                response.setContentType("application/json;charset=UTF-8");
                JsonObject jsonObject = new JsonObject();
                // json返回保存的相对路径，正好可以回显在img上
                jsonObject.addProperty("state", "SUCCESS");
                jsonObject.addProperty("url", Constants.IMG_WEB + path);
                jsonObject.addProperty("title", pic.getOriginalFilename());
                jsonObject.addProperty("original", pic.getOriginalFilename());
                response.getWriter().write(jsonObject.toString());
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}