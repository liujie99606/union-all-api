package com.union.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liujie
 */

@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    ADMINISTRATOR("ADMINISTRATOR", "超级管理员"),
    MANAGER("MANAGER", "管理员"),
    OPERATOR("OPERATOR", "操作员"),
    DEALER("DEALER", "经销商"),
    MEMBER("MEMBER", "会员");

    private final String code;
    private final String message;


    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return ContractStatusEnum
     */
    public static UserTypeEnum getByCode(String code) {
        for (UserTypeEnum _enum : values()) {
            if (StringUtils.equals(_enum.getCode(), code)) {
                return _enum;
            }
        }
        return null;
    }

    /**
     * 获取全部枚举
     *
     * @return List<ContractStatusEnum>
     */
    public List<UserTypeEnum> getAllEnum() {
        List<UserTypeEnum> list = new ArrayList<UserTypeEnum>();
        for (UserTypeEnum _enum : values()) {
            list.add(_enum);
        }
        return list;
    }

    /**
     * 获取全部枚举值
     *
     * @return List<String>
     */
    public List<String> getAllEnumCode() {
        List<String> list = new ArrayList<String>();
        for (UserTypeEnum _enum : values()) {
            list.add(_enum.getCode());
        }
        return list;
    }

}
