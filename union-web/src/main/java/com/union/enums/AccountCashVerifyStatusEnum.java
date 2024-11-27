package com.union.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现状态
 *
 * @author huangxiaoming
 */
@Getter
@AllArgsConstructor
public enum AccountCashVerifyStatusEnum {

    PASS(0), // 通过
    REFUSE(1); // 拒绝

    /**
     * 状态
     */
    private final Integer status;

}
