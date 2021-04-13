package com.microhumanresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MicrohumanresourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrohumanresourceApplication.class, args);
    }
    /**
     * 访问链接 login?username=admin&password=123
     * /employee/advance/hello 此链接会检查权限
     * /employee/basic/hello
     */
}
