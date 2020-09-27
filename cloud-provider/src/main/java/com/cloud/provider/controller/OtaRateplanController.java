package com.cloud.provider.controller;

import com.cloud.common.utils.RedisSimpleRateLimiter;
import com.cloud.common.utils.RedisWithReentrantLock;
import com.cloud.entity.dbo.OtaRateplanDBO;
import com.cloud.provider.service.OtaRateplanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OtaRateplanController {

    private static final Logger logger = LoggerFactory.getLogger(OtaRateplanController.class);
    @Autowired
    OtaRateplanService otaRateplanService;
    @Autowired
    RedisWithReentrantLock redisWithReentrantLock;

    @Autowired
    RedisSimpleRateLimiter redisSimpleRateLimiter;

    @GetMapping("/{supplierCode}")
    public List<OtaRateplanDBO> getRateplanBySupplierCode(@PathVariable String supplierCode) {
        boolean isActionAllowed = false;
        try {
            isActionAllowed = redisSimpleRateLimiter.isActionAllowed(this.getClass().getName(), "getRateplanBySupplierCode", 5, 1);
        } catch (Exception e) {

        }

        if (!isActionAllowed) {

            System.out.println("你被限流啦");
            return null;
        }


        return otaRateplanService.getRateplanBySupplierCode(supplierCode);
    }
}