package com.union.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liujie
 */

@AllArgsConstructor
@Getter
public enum TextMessageTypeEnum {

    JD("京东"),
    //淘宝
    TAO_BAO("淘宝"),
    //拼多多
    PDD("拼多多"),
    //抖音
    DOU_YIN("抖音"),
    //未知
    UNKNOWN("未知"),
    //余额
    BALANCE("余额"),
    //提现
    WITHDRAW("提现"),
    //签到
    SIGN_IN("签到"),
    //邀请码
    INVITE_CODE("邀请码"),
    //邀请统计
    INVITE_STATISTICS("邀请统计"),
    //个人中心
    PERSONAL_CENTER("个人中心"),
    //订单查询
    ORDER_QUERY("订单查询"),
    //所有功能
    ALL("所有功能"),
    ;

    private final String message;


    public static Map<String, String> getMap() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(Enum::name, TextMessageTypeEnum::getMessage));
    }

}
