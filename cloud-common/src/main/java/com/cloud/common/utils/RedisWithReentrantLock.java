package com.cloud.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * redis 实现分布式锁
 * 1、死锁的问题
 *     setnx 加超时时间
 * 2、超时问题
 *      1)为了避免这个问题，Redis 分布式锁不要用于较长时间的任务。如果真的偶尔出现了，数据出现的小波错乱可能需要人工介入解决。
 *      2)有一个更加安全的方案是为 set 指令的 value 参数设置为一个随机数，释放锁时先匹配 随机数是否一致，然后再删除 key。
 *      但是匹配 value 和删除 key 不是一个原子操作，Redis 也 没有提供类似于 delifequals 这样的指令，这就需要使用 Lua 脚本来处理了，
 *      因为 Lua 脚本可 以保证连续多个指令的原子性执行。
 * 3、可重入性
 *  可重入性是指线程在持有锁的情况下再次请求加锁，如果一个锁支持同一个线程的多次加 锁，那么这个锁就是可重入的。
 *  关于什么是可重入锁，我们先来看一段维基百科的定义。通俗来说：当线程请求一个由其它线程持有的对象锁时，该线程会阻塞，
 *  而当线程请求由自己持有的对象锁时，如果该锁是重入锁，请求就会成功，否则阻塞。
 * 4、3 种策略来处理加锁失败:
 *      1、直接抛出异常，通知用户稍后重试;
 *      2、sleep 一会再重试;
 *      3、将请求转移至延时队列，过一会再试;
 */

@Component
public class RedisWithReentrantLock {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisUtil redisUtil;
    private ThreadLocal<Map> lockers = new ThreadLocal<>();

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;


    private boolean _lock(String lockKey, String requestId, int expireTime) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        } finally {
            returnToPool(jedis);
        }
    }

    private boolean _unlock(String lockKey, String requestId) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            //有一个更加安全的方案是为 set 指令的 value 参数设置为一个随机数，释放锁时先匹配 随机数是否一致，然后再删除 key。
            // 但是匹配 value 和删除 key 不是一个原子操作，Redis 也 没有提供类似于 delifequals 这样的指令，这就需要使用 Lua 脚本来处理了，
            // 因为 Lua 脚本可 以保证连续多个指令的原子性执行。

            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        } finally {
            returnToPool(jedis);
        }
    }


    private Map<String, Integer> currentLockers() {
        Map<String, Integer> refs = lockers.get();
        if (refs != null) {
            return refs;
        }
        lockers.set(new HashMap());
        return lockers.get();
    }

    public boolean lock(String lockKey, String requestId, int expireTime) {
        Map<String, Integer> refs = currentLockers();
        Integer refCnt = refs.get(lockKey);
        //重入
        if (refCnt != null) {
            refs.put(lockKey, refCnt + 1);
            return true;
        }

        //加锁
        boolean ok = this._lock(lockKey, requestId, expireTime);
        if (!ok) {
            return false;
        }

        refs.put(lockKey, 1);
        return true;

    }


    public boolean unlock(String lockKey, String requestId) {

        Map<String, Integer> refs = currentLockers();
        Integer refCnt = refs.get(lockKey);
        if (refCnt == null) {
            return false;
        }
        refCnt -= 1;
        if (refCnt > 0) {
            refs.put(lockKey, refCnt);
        } else {
            refs.remove(lockKey);
            boolean unlock = this._unlock(lockKey, requestId);
            if (!unlock) {
                if (redisUtil.get(lockKey) != null && requestId.equals(redisUtil.get(lockKey))) {
                    return redisUtil.delete(lockKey);
                }
            }
        }

        return true;

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
