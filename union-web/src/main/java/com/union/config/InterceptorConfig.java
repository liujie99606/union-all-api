package com.union.config;

import com.union.interceptor.BaseInterceptor;
import com.union.interceptor.TraceIdGeneratorInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@AllArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final BaseInterceptor baseInterceptor;
    private final TraceIdGeneratorInterceptor traceIdGeneratorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceIdGeneratorInterceptor)
                .addPathPatterns("/**")
                .order(1);
        registry.addInterceptor(baseInterceptor)
                .addPathPatterns("/**")
                .order(2);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurePathMatch(configurer, "/app-api", "**.controller.app.**");
        configurePathMatch(configurer, "/admin-api", "**.controller.admin.**");
    }

    /**
     * 设置 API 前缀，仅仅匹配 controller 包下的
     *
     * @param configurer 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, String prefix, String controller) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        configurer.addPathPrefix(prefix, clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(controller, clazz.getPackage().getName())); // 仅仅匹配 controller 包
    }
}
