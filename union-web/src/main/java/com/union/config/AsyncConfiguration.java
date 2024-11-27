package com.union.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT300S")
public class AsyncConfiguration {
    //, mode = EnableSchedulerLock.InterceptMode.PROXY_METHOD
    private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);


    @Bean
    public LockProvider lockProvider(RedisTemplate redisTemplate) {
        return new RedisLockProvider(redisTemplate.getConnectionFactory());
    }

}
