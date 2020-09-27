package com.cloud.common.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Set;


/**
 * redis 简单限流
 */
@Component
public class RedisSimpleRateLimiter {
    @Autowired
    private JedisPool jedisPool;


    /**
     *
     * @param userId
     * @param actionKey
     * @param period
     * @param maxCount
     * @return
     */
    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) throws Exception {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            String key = String.format("hit:%s:%s", userId, actionKey);
            long nowTs = System.currentTimeMillis();
            Pipeline pipeline = jedis.pipelined();
            pipeline.multi();

            pipeline.zadd(key, nowTs, "" + nowTs);

            //移除滑动窗口外的数据
            pipeline.zremrangeByScore(key, 0, nowTs - period * 1000);
            //获取窗口内的所有数据
            Response<Long> count = pipeline.zcard(key);
            
            //设置key值保留的时间
            pipeline.expire(key, period + 1);
            pipeline.exec();
            pipeline.close();
            return count.get() <= maxCount;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 关闭jedis
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
