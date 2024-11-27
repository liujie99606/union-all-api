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
@TableName("union_order_log")
public class UnionOrderLogDO extends BaseDO {

    //主键ID
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    //订单ID
    @TableField(value = "order_id")
    private Long orderId;

    //日志内容
    @TableField(value = "log_content")
    private String logContent;

    //当前订单状态
    @TableField(value = "order_status")
    private Integer orderStatus;

}