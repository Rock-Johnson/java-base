package com.github.baseredis.cachebreakdown;

import com.github.baseredis.BaseRedisApplication;
import com.github.baseredis.cachebreakdown.model.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes={BaseRedisApplication.class})
@Slf4j
public class CacheBreakdownTest {

    private static ExecutorService executor = new ThreadPoolExecutor(10, 10,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(10));

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch countDownLatch2 = new CountDownLatch(10);
    @Autowired
    private CacheBreakdown cacheBreakdown;


    @Test
    public void getData01() {
        List<SysUser> users = cacheBreakdown.getData01();
        log.info(users.get(0).toString());
    }

    @Test
    public void getData04() throws InterruptedException {
        for(int i=0;i<10;i++){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                        List<SysUser> users = cacheBreakdown.getData04();
                        log.info(users.get(0).toString());
                        countDownLatch2.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        countDownLatch.countDown();
        countDownLatch2.await();
        log.info("所有线程都结束了");

    }

}