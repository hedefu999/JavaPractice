package com.icodingedusales;

import com.icodingedusales.controller.ProductController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
//在springboottest中排除某些component：https://www.baeldung.com/spring-boot-exclude-auto-configuration-test

/**
 * 要在springbootTest中排除某些component的注入
 * 只能定义额外的springbootConfiguration类，可以是专门的类，也可以是当前的测试类
 * 因为@ComponentScan在Configuration类下可以起作用
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceTest.class)
@EnableAutoConfiguration
@MapperScan(basePackages = "com.icodingedusales.dao")
@ComponentScan(
        basePackages = "com.icodingedusales.*",
        excludeFilters = {
               @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {
                       //这个controller里有初始化操作，需要排除掉
                       ProductController.class
               })
        })
public class ServiceTest {
    private final Logger log = LoggerFactory.getLogger(ServiceTest.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    private DefaultRedisScript<Long> redisScript = null;
    @Before
    public void before(){
        redisScript = new DefaultRedisScript();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("seckill.lua")));
    }

    /**
     * lua脚本出错，为便于调试，才有此test
     */
    @Test
    public void test12(){
        String[] args = {"3"};
        List<String> keys = Arrays.asList("2");
        //String name = redisTemplate.opsForValue().get("name");
        Long result = redisTemplate.execute(redisScript, keys, args);
        log.info("-=-=-=-=- result = {}",result);
    }
}
