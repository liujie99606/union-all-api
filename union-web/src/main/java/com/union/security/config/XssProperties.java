package com.union.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liujie
 */
@Component
@ConfigurationProperties(prefix = "xss")
@Data
public class XssProperties {

    private String enabled;

    private String excludes;

    private List<String> notLogin;

    private String urlPatterns;


}
