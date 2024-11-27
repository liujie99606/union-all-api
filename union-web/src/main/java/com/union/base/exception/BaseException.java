package com.union.base.exception;

import com.union.base.util.MessageUtil;

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code = 500;

    /**
     * 错误消息
     */
    private String msg;


    private String[] params;


    public BaseException(Integer code, String msg, String[] params) {
        this.code = code;
        this.msg = msg;
        this.params = params;
    }

    public BaseException(Integer code, String... params) {
        this(code, null, params);
    }

    public BaseException(String msg) {
        this(null, msg, null);
    }

    @Override
    public String getMessage() {
        String message = null;
        if (code != null) {
            message = MessageUtil.getText(String.valueOf(code), params);
        }
        if (message == null) {
            message = msg;
        }
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public Object[] getParams() {
        return params;
    }

    public String setMessage() {
        return msg;
    }
}
