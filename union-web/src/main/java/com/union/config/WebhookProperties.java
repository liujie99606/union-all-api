package com.union.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "webhook")
public class WebhookProperties {

    private String auditUrl;


}
