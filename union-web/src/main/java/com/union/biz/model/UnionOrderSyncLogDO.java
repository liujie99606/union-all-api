package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author lj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_order_sync_log")
public class UnionOrderSyncLogDO extends BaseDO {

    //主键
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    //订单类型
    @TableField(value = "order_type")
    private String orderType;

    //拉取订单开始时间
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    //拉取订单结束时间
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    //与上一次时间是否中断
    @TableField(value = "is_interrupted")
    private Boolean isInterrupted;

    //租户编号
    @TableField(value = "tenant_id")
    private Long tenantId;


}