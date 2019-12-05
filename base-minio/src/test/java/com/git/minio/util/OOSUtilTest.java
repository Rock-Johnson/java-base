package com.git.minio.util;


import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
public class OOSUtilTest {

    private OOSUtil oosUtil;

    @Before
    public void before() {



        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        //MinioClient minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");

        //oos测试
        oosUtil = new OOSUtil();
        oosUtil.setAccessKey("minio");
        oosUtil.setSecretKey("minio123");
        oosUtil.setEndpoint("http://minio.k8s.io");
        oosUtil.setDefaultBucket("com.zking.curiosity");//macs.bucket com.zking.curiosity
        oosUtil.init();
    }


    @Test
    public void createBucket() {
        oosUtil.createBucket("macs.bucket");
    }

    @Test
    public void genURL() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        String url = oosUtil.genURL("testFile.jpg", calendar.getTime());
        System.out.println("url:" + url);
    }


    @Test
    public void getStringObject() {
        String vaule = oosUtil.getStringObject("testkey2");
        System.out.println("vaule:" + vaule);
    }


    @Test
    public void putStringObject() {
        oosUtil.putStringObject("testkey", "testvalue");
        oosUtil.putStringObject("testkey2", "testvalue2");
    }

    @Test
    public void testUploadFile() {
        oosUtil.uploadFile("testFile.jpg", "F:\\test\\1.jpg");
    }

    @Test
    public void downloadFile() {
        oosUtil.downloadFile("testFile.jpg", "F:\\test\\2.jpg");
    }
}