package com.cloud.provider.service;

import com.cloud.common.utils.RedisUtil;
import com.cloud.entity.dbo.OtaRateplanDBO;
import com.cloud.provider.mapper.OtaRateplanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
public class OtaRateplanService {

    @Autowired
    OtaRateplanMapper otaRateplanMapper;

    @Autowired
    RedisUtil redisUtil;


    public List<OtaRateplanDBO> getRateplanBySupplierCode(@PathVariable String supplierCode) {


        return otaRateplanMapper.getRateplanBySupplierCode(supplierCode);

    }
}
