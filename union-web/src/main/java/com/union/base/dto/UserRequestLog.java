package com.union.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;


@Data
public class UserRequestLog implements Serializable {

    private String logId;

    private String userId;

    private String createdTime;

    private Long createdTimeTimestamp;

    private String ip;

    private String name;

    private String mobile;

    private Map<String, Object> parameters;

    private String url;

    private String method;

    private Long startTime;

    private Long endTime;

    private Long timeConsuming;

}
