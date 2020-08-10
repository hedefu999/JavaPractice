package com.icodingedusales;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
//缺少这个注解会报Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean.
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.icodingedusales")
@MapperScan(basePackages = "com.icodingedusales.dao")
public class IcodingedusalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(IcodingedusalesApplication.class, args);
    }

}
