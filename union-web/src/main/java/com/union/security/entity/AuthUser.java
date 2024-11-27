package com.union.security.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AuthUser implements Serializable {
    /**
     * 主键ID
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 头像
     */
    private String email;
    /**
     * 机构编号
     */
    private Integer deptId;
    /**
     * 状态
     */
    private String status;

    /**
     * 角色标识集合
     */
    private Set<String> roles;

    /**
     * 权限标识集合
     */
    private Set<String> permissions;


    private String corpId;
}
