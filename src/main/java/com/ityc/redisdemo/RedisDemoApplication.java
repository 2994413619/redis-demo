package com.ityc.redisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RedisDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisDemoApplication.class, args);
        TestRedis testRedis = context.getBean(TestRedis.class);
        //testRedis.testRedis();
        //testRedis.testStringRedisTemplate();
        //testRedis.testLowLevel();
        //testRedis.testHash();
        //testRedis.testSerializers();
        //testRedis.testMyTemplate();
        //testRedis.testPub();
        testRedis.testSub();
    }

}
