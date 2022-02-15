package com.ityc.redisdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ityc.redisdemo.domain.Address;
import com.ityc.redisdemo.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Author yuchao
 * @Description 测试spring-data-redis基本api
 * @Date 2022/2/15 11:07
 **/
@Component
public class TestRedis {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("ooxx")
    StringRedisTemplate myTemplate;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * @Author yuchao
     * @Description hight level
     *              测试存放数据：执行完后进入redis查看，发现key是：\xac\xed\x00\x05t\x00\x05hello
     * @Date 2022/2/15 9:23
     * @return void
     **/
    public void testRedis() {
        redisTemplate.opsForValue().set("hello","spring-data-redis");
        System.out.println(redisTemplate.opsForValue().get("hello"));

    }

    /**
     * @Author yuchao
     * @Description hight level
     *              测试存放数据：执行完后进入redis查看，发现key、value没有乱码
     * @Date 2022/2/15 9:51
     * @return void
     **/
    public void testStringRedisTemplate() {
        stringRedisTemplate.opsForValue().set("hello","stringRedisTemplate");
        System.out.println(stringRedisTemplate.opsForValue().get("hello"));
    }




    /**
     * @Author yuchao
     * @Description 测试low level
     * @Date 2022/2/15 9:55
     * @return void
     **/
    public void testLowLevel() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.set("hello02".getBytes(), "low Level test".getBytes());
        System.out.println(new String(connection.get("hello02".getBytes())));
    }

    /**
     * @Author yuchao
     * @Description 测试hash
     * @Date 2022/2/15 10:04
     * @return void
     **/
    public void testHash() {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put("tom","age", "24");
        hash.put("tom","sex","男");
        System.out.println(hash.entries("tom"));
    }

    /**
     * @Author yuchao
     * @Description 测试 对象映射到hash
     * @Date 2022/2/15 10:27
     * @return void
     **/
    public void testSerializers() {
        Person person = new Person();
        person.setName("jack");
        person.setAge(18);
        Address address = new Address();
        address.setCountry("china");
        address.setCity("shenzhen");
        person.setAddress(address);

        //防止Integer不能转为String
        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        //第二个参数：是否扁平化，pom.xml中需要引入spring-boot-starter-json
        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);
        //存
        stringRedisTemplate.opsForHash().putAll("jack", jm.toHash(person));
        //取
        Map map = stringRedisTemplate.opsForHash().entries("jack");
        //转为对象
        Person per = objectMapper.convertValue(map, Person.class);

        System.out.println(per.getName());


    }

    /**
     * @Author yuchao
     * @Description 测试自定义template
     * @Date 2022/2/15 11:04
     * @return void
     **/
    public void testMyTemplate() {
        Person person = new Person();
        person.setName("jack");
        person.setAge(18);
        Address address = new Address();
        address.setCountry("china");
        address.setCity("shenzhen");
        person.setAddress(address);

        //第二个参数：是否扁平化，pom.xml中需要引入spring-boot-starter-json
        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);
        //存
        myTemplate.opsForHash().putAll("jack", jm.toHash(person));
        //取
        Map map = myTemplate.opsForHash().entries("jack");
        //转为对象
        Person per = objectMapper.convertValue(map, Person.class);

        System.out.println(per.getName());


    }

    /**
     * @Author yuchao
     * @Description 发布订阅测试
     *              发布 ：要有客户端在订阅着才能看到，redis不会保存消息
     *              可以使用redis-cli订阅：SUBSCRIBE ooxx
     * @Date 2022/2/15 11:08
     * @return void
     **/
    public void testPub() {
        //发布 ：要有客户端在订阅着才能看到，redis不会保存消息
        myTemplate.convertAndSend("ooxx", "hello");
    }

    /**
     * @Author yuchao
     * @Description 发布订阅测试
     *              可以使用redis-cli订阅：
     * @Date 2022/2/15 11:11
     * @return void
     **/
    public void testSub() {
        RedisConnection  cc = myTemplate.getConnectionFactory().getConnection();
        cc.subscribe(new MessageListener() {

            //相当于回调
            @Override
            public void onMessage(Message message, byte[] pattern) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());

        //防止程序结束
        while(true){}

    }


}
