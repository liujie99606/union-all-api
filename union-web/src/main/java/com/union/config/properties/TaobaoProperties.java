package com.union.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "taobao")
public class TaobaoProperties {
    
    /**
     * 应用Key
     */
    private String appKey;
    
    /**
     * 应用密钥
     */
    private String secret;
    
    /**
     * API服务器地址
     */
    private String serverUrl = "https://eco.taobao.com/router/rest";
    
    /**
     * 推广位ID
     */
    private Long adzoneId = 115794850477L;
} 