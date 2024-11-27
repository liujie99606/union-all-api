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
@TableName("union_qrcode")
public class UnionQrcodeDO extends BaseDO {

    //主键
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    //用户ID
    @TableField(value = "user_id")
    private Long userId;

    //过期时间(秒)
    @TableField(value = "expire_seconds")
    private Long expireSeconds;

    //二维码类型
    @TableField(value = "action_name")
    private String actionName;

    //租户编号
    @TableField(value = "tenant_id")
    private Long tenantId;

    //二维码url
    @TableField(value = "url")
    private String url;


}