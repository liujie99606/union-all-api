package com.union.interceptor;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.union.base.context.WebContext;
import com.union.base.dto.UserRequestLog;
import com.union.base.util.IPUtil;
import com.union.base.util.RequestUtil;
import com.union.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

/**
 * @author liujie
 */
@Slf4j
@Component
public class BaseInterceptor implements HandlerInterceptor {

    private ThreadLocal<UserRequestLog> requestParams = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) { // 请求进入这个拦截器
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        String appId = request.getHeader("X-App-Id");
        String agentId = request.getHeader("X-Agent-Id");
        WebContext.setAppId(appId);
        if (StringUtils.isNotBlank(agentId)) {
            WebContext.setAgentId(Integer.parseInt(agentId));
        }

        WebContext.setPage(
                Optional.ofNullable(page)
                        .filter(StrUtil::isNotBlank)
                        .map(Integer::parseInt)
                        .orElse(1)
        );

        WebContext.setSize(
                Optional.ofNullable(size)
                        .filter(StrUtil::isNotBlank)
                        .map(Integer::parseInt)
                        .orElse(10)
        );

        Map<String, Object> parameters = RequestUtil.getParameters(request);
        String url = request.getRequestURI();
        String ip = IPUtil.getRemoteAddr(request);
        String method = request.getMethod();
        UserRequestLog requestLog = new UserRequestLog();
        requestLog.setStartTime(System.currentTimeMillis());
        requestLog.setMethod(method);
        String userId = SecurityUtil.getUserIdOrNull();
        requestLog.setUserId(userId);
        requestLog.setIp(ip);
        requestLog.setParameters(parameters);
        requestLog.setUrl(url);
        requestParams.set(requestLog);

        return true; // 有的话就继续操作
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        WebContext.removePage();
        WebContext.removeSize();
        WebContext.removeAgentId();
        WebContext.removeAppId();

        UserRequestLog requestLog = requestParams.get();
        requestLog.setEndTime(System.currentTimeMillis());
        requestLog.setTimeConsuming(requestLog.getEndTime() - requestLog.getStartTime());
        log.info("基础拦截器中请求参数：{}", JSON.toJSONString(requestLog));

        requestParams.remove();
    }

}
