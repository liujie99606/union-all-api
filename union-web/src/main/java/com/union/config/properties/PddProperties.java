package com.union.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pdd")
public class PddProperties {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * PID
     */
    private String pid;

    /**
     * 自定义参数模板
     */
    private String customParameters = "{\"uid\":\"%s\"}";

    /**
     * 返回URL模板
     */
    private String returnUrlTemplate = "查询失败！您未绑定拼多多\n" +
            "------\n" +
            "%s\n" +
            "------\n" +
            "①点击上方链接\n" +
            "②确认授权\n" +
            "③重新查询商品";
} 