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
@TableName("union_link")
public class UnionLinkDO extends BaseDO {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    //用户id
    @TableField(value = "user_id")
    private Long userId;

    //原始链接
    @TableField(value = "org_link")
    private String orgLink;

    //转换后内容
    @TableField(value = "content")
    private String content;

    //转换类型
    @TableField(value = "type")
    private String type;

    @TableField(value = "tenant_id")
    private Long tenantId;

    //商品id
    @TableField(value = "product_id")
    private String productId;


}