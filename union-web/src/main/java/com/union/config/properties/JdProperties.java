package com.union.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jd")
public class JdProperties {
    
    /**
     * 服务器URL
     */
    private String serverUrl = "https://api.jd.com/routerjson";
    
    /**
     * 应用Key
     */
    private String appKey;
    
    /**
     * 应用密钥
     */
    private String appSecret;
} 