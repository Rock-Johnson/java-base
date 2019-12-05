package com.git.minio.util;

import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Component
public class OOSUtil {

    private MinioClient oos;

    @Value("${oos.accessKey}")
    private String accessKey;

    @Value("${oos.secretKey}")
    private String secretKey;

    @Value("${oos.endpoint}")
    private String endpoint;

    @Value("${oos.defaultBucket}")
    private String defaultBucket;


    @PostConstruct
    public void init() {
        try {
            log.info("OOS连接初始化--------------------------------------->");
            oos = new MinioClient(endpoint,accessKey,secretKey);
            if (!oos.bucketExists(defaultBucket)) {
                log.info("defaultBucket:" + this.defaultBucket + " 不存在,创建之------!");
                oos.makeBucket(defaultBucket);
                log.info("defaultBucket创建成功");
            } else {
                log.info("defaultBucket:" + this.defaultBucket + "已经存在!");
            }
            log.info("OOS连接成功--------!!!");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 创建bucket,注意是否有权限
     *
     * @param bucketName
     */
    public void createBucket(String bucketName) {

        try {
            log.info("defaultBucket 不存在,创建之......");
            oos.makeBucket(bucketName);
            log.info("defaultBucket创建成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 创建url
     *
     * @param key
     * @param expiration
     */
    public String genURL(String key, Date expiration) {
        try {

            long nowtime = System.currentTimeMillis();
            Integer expires = (int) ( expiration.getTime() - nowtime )/ 1000;
            //Expiry in seconds. Default expiry is set to 7 days.
            String url = oos.presignedGetObject(defaultBucket,key,expires);
            return url;
        } catch (Exception e) {
            log.info("url生成失败", e);
        }
        return "";
    }

    /**
     * 获取对象流（一般用于读取文件类型的对象）
     *
     * @param bucketName
     * @param key
     * @return
     */
    public InputStream getObjectStream(String bucketName, String key) {
        try {

            InputStream ins =  oos.getObject(bucketName,key);
            return ins;
        } catch (Exception e) {
            log.info("url生成失败", e);
            return null;
        }
    }


    /**
     * 获取对象流（一般用于读取文件类型的对象）
     *
     * @param key
     * @return
     */
    public InputStream getObjectStream(String key) {
        return getObjectStream(defaultBucket, key);
    }

    /**
     * 从指定获bucket取字符类型对象
     *
     * @param bucketName
     * @param key
     * @return
     */
    public String getStringObject(String bucketName, String key) {
        String context = new String();
        try {
            InputStream input = this.getObjectStream(bucketName, key);
            context = IOUtils.toString(input, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("对象不存在或值为空:bucketName:" + bucketName + ",key:" + key);
        }
        return context;
    }


    /**
     * 从缺省获bucket取字符类型对象
     *
     * @param key
     * @return
     */
    public String getStringObject(String key) {
        return this.getStringObject(defaultBucket, key);
    }

    public void putStringObject(String bucketName, String key, String value) {
        try {
            this.putObjectStream(bucketName, key, new ByteArrayInputStream(value.getBytes("UTF-8")));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    public void putStringObject(String key, String value) {
        try {
            this.putObjectStream(key, new ByteArrayInputStream(value.getBytes("UTF-8")));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    /**
     * 将对象流上传缺省oss的Bucket
     *
     * @param key
     * @param ins
     */
    public void putObjectStream(String key, InputStream ins) {
        try {
            oos.putObject(defaultBucket, key, ins,null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * 将对象流上传
     *
     * @param key
     * @param ins
     */
    public void putObjectStream(String bucketName, String key, InputStream ins) {
        try {
            oos.putObject(bucketName, key, ins, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 将文件上传缺省oss
     *
     * @param key
     */
    public void uploadFile(String bucketName, String key, File file) {
        try {
            oos.putObject(bucketName, key, file.getPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 将文件上传缺省oss的Bucket
     *
     * @param key
     */
    public void uploadFile(String key, File file) {
        this.uploadFile(defaultBucket, key, file);
    }


    /**
     * 将文件上传缺省oss的Bucket
     *
     * @param key
     */
    public void uploadFile(String key, String filepath) {
        this.uploadFile(defaultBucket, key, new File(filepath));
    }

    /**
     * 将文件下载缺省oss的Bucket
     *
     * @param key
     */
    public void downloadFile(String key, String path) {
        try {
            InputStream ins = this.getObjectStream(defaultBucket, key);
            FileTool.inputStreamToFile(ins, new File(path));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


}
