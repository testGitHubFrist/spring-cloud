package com.cloud.client.service.restTemplate;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "cloud-provider", fallbackFactory = FeignClientFallbackFactory.class)
public interface UserFeignClient {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String findById(@PathVariable("id") Long id);
}
 
/**
 * UserFeignClient的fallbackFactory类，该类需实现FallbackFactory接口，并覆写create方法
 */
@Component
class FeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientFallbackFactory.class);
 
    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public String findById(Long id) {
                FeignClientFallbackFactory.LOGGER.info("fallback; reason was:", cause);

                return "";
            }
        };
    }
}
