package com.github.baseredis.cachebreakdown;

import com.github.baseredis.cachebreakdown.model.SysUser;
import com.github.baseredis.cachebreakdown.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class CacheBreakdown {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SysUserService sysUserService;

    //这种方式确实能够防止缓存失效时高并发到数据库,
    // 但是缓存没有失效的时候,在从缓存中拿数据时需要排队取锁,
    // 这必然会大大的降低了系统的吞吐量.
    public synchronized List<SysUser> getData01() {
        List<SysUser> result = new ArrayList<>();
        // 从缓存读取数据
        result = getDataFromCache();
        if (result.isEmpty()) {
            // 从数据库查询数据
            result = getDataFromDB();
            // 将查询到的数据写入缓存
            setDataToCache(result);
        }
        return result;
    }


    // 方法2:
    static Object lock = new Object();
    //这个方法在缓存命中的时候,系统的吞吐量不会受影响,但是当缓存失效时,
    // 请求还是会打到数据库,只不过不是高并发而是阻塞而已.
    // 但是,这样会造成用户体验不佳,并且还给数据库带来额外压力.
    public List<SysUser> getData02() {
        List<SysUser> result = new ArrayList<SysUser>();
        // 从缓存读取数据
        result = getDataFromCache();
        if (result.isEmpty()) {
            synchronized (lock) {
                // 从数据库查询数据
                result = getDataFromDB();
                // 将查询到的数据写入缓存
                setDataToCache(result);
            }
        }
        return result;
    }


    //方法3
    //双重判断虽然能够阻止高并发请求打到数据库,
    // 但是第二个以及之后的请求在命中缓存时,还是排队进行的.比如,当30个请求一起并发过来,在双重判断时,
    // 第一个请求去数据库查询并更新缓存数据,
    // 剩下的29个请求则是依次排队取缓存中取数据.请求排在后面的用户的体验会不爽
    public List<SysUser> getData03() {
        List<SysUser> result = new ArrayList<SysUser>();
        // 从缓存读取数据
        result = getDataFromCache();
        if (result.isEmpty()) {
            synchronized (lock) {
                //双重判断,第二个以及之后的请求不必去找数据库,直接命中缓存
                // 查询缓存
                result = getDataFromCache();
                if (result.isEmpty()) {
                    // 从数据库查询数据
                    result = getDataFromDB();
                    // 将查询到的数据写入缓存
                    setDataToCache(result);
                }
            }
        }
        return result;
    }




    static Lock reenLock = new ReentrantLock();
    //最后使用互斥锁的方式来实现，可以有效避免前面几种问题.
    public List<SysUser> getData04() throws InterruptedException {
        List<SysUser> result = new ArrayList<SysUser>();
        // 从缓存读取数据
        result = getDataFromCache();
        if (result.isEmpty()) {
            if (reenLock.tryLock()) {
                try {
                    log.info("我拿到锁了,从DB获取数据库后写入缓存");
                    // 从数据库查询数据
                    result = getDataFromDB();
                    // 将查询到的数据写入缓存
                    setDataToCache(result);
                } finally {
                    reenLock.unlock();// 释放锁
                }

            } else {
                result = getDataFromCache();// 先查一下缓存
                if (result.isEmpty()) {
                    log.info("我没拿到锁,缓存也没数据,先小憩一下");
                    Thread.sleep(100);// 小憩一会儿
                    return getData04();// 重试
                }
            }
        }
        return result;
    }
    private void setDataToCache(List<SysUser> result) {
        redisTemplate.opsForList().leftPushAll("test-list",result);
        redisTemplate.expire("test-list",60, TimeUnit.SECONDS);
    }

    private List<SysUser> getDataFromDB() {
        return sysUserService.getUser();
    }

    private List<SysUser> getDataFromCache() {
        return redisTemplate.opsForList().range("test-list", 0, -1);
    }


}
