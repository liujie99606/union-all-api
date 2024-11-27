package com.union.biz.manager;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.union.constant.RedisKeyConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

@Repository
public class CashNoRedisDAO {

    public static final String CASH_NO_PREFIX = "t";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String generate(String prefix) {
        // 递增序号
        String noPrefix = prefix + DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        String key = RedisKeyConstants.CASH_NO + noPrefix;
        Long no = stringRedisTemplate.opsForValue().increment(key);
        // 设置过期时间
        stringRedisTemplate.expire(key, Duration.ofMinutes(1L));
        return noPrefix + no;
    }

}
