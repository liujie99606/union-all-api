package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

/**
 * @author lj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_user")
public class UnionUserDO extends BaseDO {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    //外部平台ID
    @TableField(value = "open_id")
    private String openId;

    //头像
    @TableField(value = "avatar")
    private String avatar;

    //昵称
    @TableField(value = "nickname")
    private String nickname;

    //来源
    @TableField(value = "source_type")
    private Integer sourceType;

    //姓名
    @TableField(value = "name")
    private String name;

    //性别
    @TableField(value = "gender")
    private Integer gender;

    @TableField(value = "tenant_id")
    private Long tenantId;


    @TableField(value = "inviter_user_id")
    private Long inviterUserId;

    //是否绑定拼多多
    @TableField(value = "is_bind_pdd")
    private Boolean isBindPdd;


}