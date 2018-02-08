package com.danyl.core.service.staticpage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Map;

public class StaticPageServiceImpl implements StaticPageService, ServletContextAware {
    private Configuration configuration;

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    //获取项目中的绝对路径
    public String getPath(String name) {
        return this.servletContext.getRealPath(name);
    }

    //静态化商品页方法
    public void index(Map<String, Object> root, Integer id) {
        String path  = getPath("/html/product/"+ id+".html");
        //product目录不存在时自动创建
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        FileWriter fileWriter = null;
        try {
            FileWriterWithEncoding writer = new FileWriterWithEncoding(path, "UTF-8");
            //读取模板名称
            Template template = configuration.getTemplate("productDetail.ftl");
            template.process( root,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != fileWriter) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}