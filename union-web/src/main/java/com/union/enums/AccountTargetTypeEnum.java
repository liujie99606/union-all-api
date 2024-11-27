package com.union.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@Getter
public enum AccountTargetTypeEnum {

    //返利拥金
    REBATE_AWARD("返利奖励"),
    //签到奖励
    SIGN_IN("签到奖励"),
    CASH("提现"),
    //部分退款
    REFUND("部分退款"),

    ;


    private final String message;


    public static AccountTargetTypeEnum getByCode(String code) {
        for (AccountTargetTypeEnum _enum : values()) {
            if (StrUtil.equals(_enum.name(), code)) {
                return _enum;
            }
        }
        return null;
    }


    public List<AccountTargetTypeEnum> getAllEnum() {
        return new ArrayList<>(Arrays.asList(values()));
    }


    public List<String> getAllEnumCode() {
        List<String> list = new ArrayList<>();
        for (AccountTargetTypeEnum _enum : values()) {
            list.add(_enum.name());
        }
        return list;
    }

}
