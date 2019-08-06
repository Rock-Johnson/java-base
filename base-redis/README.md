# _base redis_
### **application.properties**
```properties
spring.redisson.database=0
spring.redisson.password=
spring.redisson.timeout=3000
#sentinel/cluster/single
spring.redisson.mode=single
#连接池配置
spring.redisson.pool.max-idle=16
spring.redisson.pool.min-idle=8
spring.redisson.pool.max-active=8
spring.redisson.pool.max-wait=3000
spring.redisson.pool.conn-timeout=3000
spring.redisson.pool.so-timeout=3000
spring.redisson.pool.size=10
#单机配置
spring.redisson.single.address=192.168.60.23:6379
#集群配置
spring.redisson.cluster.scan-interval=1000
spring.redisson.cluster.nodes=
spring.redisson.cluster.read-mode=SLAVE
spring.redisson.cluster.retry-attempts=3
spring.redisson.cluster.failed-attempts=3
spring.redisson.cluster.slave-connection-pool-size=64
spring.redisson.cluster.master-connection-pool-size=64
spring.redisson.cluster.retry-interval=1500
#哨兵配置
spring.redisson.sentinel.master=business-master
spring.redisson.sentinel.nodes=
spring.redisson.sentinel.master-onlyWrite=true
spring.redisson.sentinel.fail-max=3
```
### **缓存淘汰**
Redis本身提供了6中缓存淘汰策略，以下属性表示允许使用的最大内存  

```
server.maxmemory
``` 

复制代码当使用的内存超过限制内存时，Redis会根据配置的以下6中淘汰策略选择数据淘汰  
* volatile-lru：从已设置过期时间的数据集中挑选最近最少使用的数据淘汰  
* volatile-ttl：从已设置过期时间的数据集挑选将要过期的数据淘汰  
* volatile-random：从已设置过期时间的数据集中任意选择数据淘汰  
* allkeys-lru：从数据集中挑选最近最少使用的数据淘汰  
* allkeys-random：从数据集中任意选择数据淘汰  
* no-enviction：内存不足时添加数据会报错  
其他相关配置：

```
#指定数据淘汰算法  
maxmemory-policy allkeys-lru
#LRU和最小TTL算法的样本个数
maxmemory-samples 5
```
### **缓存穿透**
大量的请求瞬时涌入系统，而这个数据在Redis中不存在，从而所有的请求都落到了数据库上从而把数据库打死。造成这种情况的原因如下：  
* 系统设计不合理，缓存数据更新不及时  
* 爬虫等恶意攻击  
##### **解决方案：**
* 如果key在数据库中也不存在，那么就写一个空值到Redis中，并设置一个过期时间，避免一直占用内存
* 查询缓存之前使用布隆过滤器拦截

### **缓存击穿**
缓存击穿，就是常说的热点key问题，当一个正有非常巨大的访问量访问的key 在失效的瞬间，大量的请求击穿了缓存，直接落到了数据库上，然后所有从数据获取到数据的线程又都并发的想要把数据缓存到redis中。  
##### 解决方案：
* 使用互斥锁，同一时刻只允许一个线程去构建缓存，其他线程等待构建完毕后去缓存取
* 定时更新，假如缓存过期时间为60分钟，则单独设置一个线程每59分钟去负责更新缓存

### **缓存雪崩**
由于Redis是基于内存的应用，可以很容易做到高性能、高并发从而起到保护数据库的作用。如果缓存意外挂了、所有的请求落到了数据上就形成了缓存雪崩。  
##### 解决方案：
*  事前：使用主从复制+哨兵或者Redis集群。Redis主从复制、Redis的哨兵机制、Redis集群环境搭建
*  事中：本地缓存结合限流和降级。基于注解的分布式限流组件
*  事后：开启持久化配置，实现快速缓存的快速恢复。 Redis 的持久化机制

### **数据库缓存双写一致性**
[分布式之数据库和缓存双写一致性方案解析](https://www.cnblogs.com/rjzheng/p/9041659.html)


