package com.union.filter;//package com.lj.demo.interceptor;

import com.union.constant.CommonConstants;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Order(999)
@Component
public class TraceIdGeneratorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // 初始化配置
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            // 继续请求处理
            chain.doFilter(request, response);
        } finally {
            // 确保 TRACE_ID 被清除
            MDC.remove(CommonConstants.TRACE_ID);
        }
    }

    @Override
    public void destroy() {
        // 清理资源
    }
}