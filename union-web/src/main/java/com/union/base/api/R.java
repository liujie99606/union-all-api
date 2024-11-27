package com.union.base.api;

import cn.hutool.http.HttpStatus;
import com.union.constant.CommonConstants;
import com.union.base.util.MessageUtil;
import lombok.*;
import lombok.experimental.Accessors;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * 返回数据
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private T data;

    private long total;
    private String traceId;

    public static <T> R<T> ok() {
        return restResult(null, HttpStatus.HTTP_OK, MessageUtil.getText("success"));
    }

    public static <T> R<T> ok(String code, String... params) {
        return restResult(null, HttpStatus.HTTP_OK, MessageUtil.getText(code, params));
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, HttpStatus.HTTP_OK, MessageUtil.getText("success"));
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, HttpStatus.HTTP_OK, msg);
    }

    public static <T> R<T> error() {
        return restResult(null, HttpStatus.HTTP_INTERNAL_ERROR, MessageUtil.getText("fail"));
    }

    public static <T> R<T> error(String code, String... params) {
        return restResult(null, HttpStatus.HTTP_INTERNAL_ERROR, MessageUtil.getText(code, params));
    }

    public static <T> R<T> error(String msg) {
        return restResult(null, HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }

    public static <T> R<T> error(String msg, Integer code) {
        code = code != null ? code : HttpStatus.HTTP_INTERNAL_ERROR;
        return restResult(null, code, msg);
    }

    public static <T> R<T> error(T data, String msg, Integer code) {
        code = code != null ? code : HttpStatus.HTTP_INTERNAL_ERROR;
        return restResult(data, code, msg);
    }

    public static <T> R<T> error(T data) {
        return restResult(data, HttpStatus.HTTP_INTERNAL_ERROR, null);
    }

    public static <T> R<T> unAuth(String msg) {
        return restResult(null, HttpStatus.HTTP_UNAUTHORIZED, msg);
    }

    public static <T> R<T> bad(String msg) {
        return restResult(null, HttpStatus.HTTP_BAD_REQUEST, msg);
    }


    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setTraceId(MDC.get(CommonConstants.TRACE_ID));
        return apiResult;
    }

    public static <T> R<T> ok(T data, long total) {
        R<T> apiResult = new R<>();
        apiResult.setCode(HttpStatus.HTTP_OK);
        apiResult.setData(data);
        apiResult.setMsg(MessageUtil.getText("success"));
        apiResult.setTotal(total);
        apiResult.setTraceId(MDC.get(CommonConstants.TRACE_ID));
        return apiResult;
    }
}
