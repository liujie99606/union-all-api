package com.union.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AccountCashStatusEnum {

    PENDING(0),//待审核
    SUCCESS(1), // 成功
    REFUSE(3); // 拒绝

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 通过status获取
     */
    public static AccountCashStatusEnum getEnum(Integer status) {
        for (AccountCashStatusEnum item : values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

}
