package com.danyl.common.fastdfs;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

public class FastDFSUtils {
    public static String uploadPic(MultipartFile pic) throws Exception {
        ClientGlobal.initByProperties("fastdfs-client.properties");

        //初始化连接Tracker Server
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();

        //初始化连接Storage Server
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);

        //获取文件扩展名
        String extension = FilenameUtils.getExtension(pic.getOriginalFilename());
        //保存图片
        NameValuePair[] meta_list = new NameValuePair[3];
        meta_list[0] = new NameValuePair("filename",pic.getOriginalFilename());
        meta_list[1] = new NameValuePair("filesize", String.valueOf(pic.getSize()));
        meta_list[2] = new NameValuePair("fileext", extension);
        String path = storageClient1.upload_file1(pic.getBytes(), extension, meta_list);
        return path;
    }
}