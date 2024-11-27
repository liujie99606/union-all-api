package com.union.interceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.union.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Component
public class TraceIdGeneratorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String traceId = request.getHeader(CommonConstants.TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = UUID.fastUUID().toString(true)
                    + "."
                    + DateUtil.format(new Date(), "yyMMdd.HHmmss");
        }
        MDC.put(CommonConstants.TRACE_ID, traceId);
        return true;
    }

}