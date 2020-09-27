package com.cloud.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

@Component
public class RedisBitMap {

    @Autowired
    private JedisPool jedisPool;
}
