package com.itbaizhan.farm_common.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OrderNumberUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    String Prefis_key = "order:number";
    private static final DateTimeFormatter DATE_FORMATTER= DateTimeFormatter.ofPattern("yyyyMMdd");

    public String getOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String key = Prefis_key + ":" + DATE_FORMATTER.format(now);
        Long orderNumber = redisTemplate.opsForValue().increment(key);
        return now.format(DATE_FORMATTER) +"-"+ String.format("%05d", orderNumber);
    }
}
