package com.icodingedusales.controller;

import com.alibaba.fastjson.JSON;
import com.icodingedusales.entity.Order;
import com.icodingedusales.entity.Product;
import com.icodingedusales.service.ProductService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * (Product)表控制层
 *
 * @author makejava
 * @since 2020-05-19 22:12:22
 */
@RestController
@RequestMapping("product")
public class ProductController {
    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;
    /**
     * 服务对象
     */
    @Resource
    private ProductService productService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Product selectOne(Integer id) {
        return this.productService.queryById(id);
    }

    /**
     * 并发优化：在Controller方法上加上synchronized，实现单例bean的线程安全
     * 性能差，分布式环境失效
     * 使用synchronized后吞吐量265.7,不使用synchronized吞吐量是2.8？？？
     */
    @GetMapping("seckill/{id}")
    public /*synchronized*/ String seckill(@PathVariable Integer id){
        //查询库存
        Product product = productService.queryById(id);
        if (product.getStock() > 0){
            productService.seckill(id);
            return "秒杀成功";
        }else {
            return "秒杀失败";
        }
    }
    /**
     * 并发优化：加数据库校验改SQL
     * update set stock = stock - 1 where id = ?? and stock > 0
     * 利用了innodb引擎的行级锁特性，性能较高
     * 严重依赖数据库
     */
    @PostConstruct
    public void init(){
        Product product = productService.queryById(2);
        String productKey = "seckill:stock:2";
        redisTemplate.opsForValue().set(productKey, product.getStock().toString());
        //清空这个set：seckill:uids:2
        Long size = redisTemplate.opsForSet().size("seckill:uids:2");
        log.info("redis集合大小 size = {}", size);
        redisTemplate.opsForSet().pop("seckill:uids:2",size);
    }
    @GetMapping("seckillWithRedis/{id}")
    public String seckillWithRedis(@PathVariable Integer id){
        String productKey = "seckill:stock:"+id;
        Integer stock = Integer.valueOf(redisTemplate.opsForValue().get(productKey));
        if (stock > 0){
            Long decrement = redisTemplate.opsForValue().decrement(productKey);
            if (decrement > 0){
                productService.seckill(id);
            }
            return decrement>0?"1":"-1";
        }else {
            return "-1";
        }
    }
    /**
     * 使用redis 借助lua实现原子性
     */
    static DefaultRedisScript<Long> redisScript = null;
    @PostConstruct
    public void init2(){
        redisScript = new DefaultRedisScript();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("seckill.lua")));
    }
    @GetMapping("seckillWithLua/{pid}/{uid}")
    public String seckillWithLua(@PathVariable long pid, @PathVariable long uid){
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(pid+""), uid+"");
        if (result == 1){
            Order order = new Order(uid, pid);
            kafkaTemplate.send("supersales", JSON.toJSONString(order));
            return uid+"秒杀成功";
        }else {
            return uid+"秒杀失败";
        }
    }

    @KafkaListener(topics = "supersales", id = "1")
    public void consume(ConsumerRecord<?,String> consumerRecord){
        String value = consumerRecord.value();
        Order order = JSON.parseObject(value, Order.class);
        log.info("收到MQ: pid = {}, uid = {}", order.getPid(), order.getUid());
    }


}