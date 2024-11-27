package com.union.security.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.http.HttpStatus;
import com.union.base.api.R;
import com.union.base.exception.BaseException;
import com.union.base.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限校验异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public R handleAccessDeniedException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return R.error("没有权限，请联系管理员授权", HttpStatus.HTTP_FORBIDDEN);
    }

    /**
     * 角色校验异常
     */
    @ExceptionHandler(NotRoleException.class)
    public R handleAccessDeniedException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色校验失败'{}'", requestURI, e.getMessage());
        return R.error("没有角色，请联系管理员授权", HttpStatus.HTTP_FORBIDDEN);
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException.class)
    public R handleAccessDeniedException(NotLoginException e,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        // 设置HTTP状态码为401
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);

        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',认证失败'{}',无法访问系统资源", requestURI, e.getMessage());
        return R.error(StrUtil.format("请求地址'{}',认证失败'{}',无法访问系统资源", requestURI),
                HttpStatus.HTTP_UNAUTHORIZED
        );
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                 HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return R.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException.class)
    public R<String> handleServiceException(BaseException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("请求地址'{}',发生自定义异常.,{}", requestURI, e.getMessage());
        return R.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return R.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public R handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return R.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();
        log.info("参数绑定异常信息 ex = {}", message);
        return R.error(message);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return R.error(e.getMessage());
    }

}
