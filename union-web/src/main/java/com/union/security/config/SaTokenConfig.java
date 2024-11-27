package com.union.security.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.union.base.api.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@RequiredArgsConstructor
@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private final XssProperties xssProperties;

    /**
     * 注册 Sa-Token 的拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new SaInterceptor())
                .addPathPatterns("/**");
    }

    /**
     * 注册 Sa-Token全局过滤器，解决跨域问题
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 拦截与排除 path
                .addInclude("/**")
                .addExclude("/favicon.ico", "/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/**")
                // 全局认证函数
                .setAuth(obj -> {
                    // 登录校验
                    SaRouter.match("/**")
                            .notMatch(xssProperties.getNotLogin())
                            .check(r -> StpUtil.checkLogin());

                    // 根据路由划分模块，不同模块不同鉴权
                    //            SaRouter.match("/boss/**", r -> StpUtil.checkPermission("user"));
                })

                // 异常处理函数
                .setError(e -> {
                    log.info("---------- 进入Sa-Token异常处理 -----------,{}", e.getMessage());
                    return JSONUtil.toJsonStr(R.error("认证失败", HttpStatus.HTTP_UNAUTHORIZED));
                })

                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> log.info("--------OPTIONS预检请求，不做处理"))
                            .back();
                });
    }

}
