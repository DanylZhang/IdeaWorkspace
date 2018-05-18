package com.danyl.springbootsell.utils;

import com.google.zxing.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QRCodeUtilTest {

    @Test
    public void zxingCodeCreate() {
        try {
            File file = ResourceUtils.getFile("classpath:static/logo.jpg");
            String logoPath = file.getAbsolutePath();
            QRCodeUtil.zxingCodeCreate("http://www.baidu.com?target=asdfa234", "D:/aaa.jpg", null, logoPath);

            Result result = QRCodeUtil.zxingCodeAnalyze("D:/aaa.jpg");
            System.out.println(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}