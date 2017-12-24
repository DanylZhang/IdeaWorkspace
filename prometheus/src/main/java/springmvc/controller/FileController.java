package springmvc.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Controller
public class FileController {
    @RequestMapping("/upload")
    public String upload(MultipartFile multipartFile) {
        System.out.println(multipartFile);
        System.out.println(multipartFile.getContentType());
        System.out.println(multipartFile.getName());
        System.out.println(multipartFile.getOriginalFilename());
        System.out.println(multipartFile.getSize());

        //将上传的文件保存在服务器中
        String path = "E:/";
        FileOutputStream fileOutputStream = null;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            String uuid = UUID.randomUUID().toString();
            String suffix = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            String fileName = uuid + "." + suffix;
            fileOutputStream = new FileOutputStream(new File(path, fileName));
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "redirect:upload.jsp";
    }

    @RequestMapping("/down")
    @ResponseBody//告诉springmvc所有的响应都由response操作
    public void download(HttpServletResponse httpServletResponse) {
        String dir = "D:/360极速浏览器下载/精通Perl.pdf";
        File file = new File(dir);
        //定义输入流
        FileInputStream fileInputStream = null;
        try {
            httpServletResponse.setHeader("Content-Disposition","attachment;filename="+file.getName());//设置下载头信息
            fileInputStream = new FileInputStream(file);
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            IOUtils.copy(fileInputStream, servletOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}