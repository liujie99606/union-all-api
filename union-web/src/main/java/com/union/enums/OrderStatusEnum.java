package com.union.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    WAIT_PAY(1, "待支付"),
    SUCCESS(2, "已完成"),
    //无效订单
    INVALID(5, "无效订单");;


    private final Integer code;
    private final String message;

}
