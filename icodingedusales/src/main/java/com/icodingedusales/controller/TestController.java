package com.icodingedusales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    //@Autowired
    //private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("")
    public String test(){
        return redisTemplate.opsForValue().get("name");
    }



}
