package com.union.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum SettleStatusEnum {

    //待结算
    WAIT_SETTLE(1, "待结算"),
    //已结算
    SETTLED(2, "已结算"),

    ;


    private final Integer code;
    private final String message;

}
