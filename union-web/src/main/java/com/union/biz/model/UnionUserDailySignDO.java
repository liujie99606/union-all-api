package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author lj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_user_daily_sign")
public class UnionUserDailySignDO extends BaseDO {

    //主键
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    //用户ID
    @TableField(value = "user_id")
    private Long userId;

    //获得金额
    @TableField(value = "amount")
    private BigDecimal amount;

    //签到日期
    @TableField(value = "sign_date")
    private LocalDate signDate;

    //租户编号
    @TableField(value = "tenant_id")
    private Long tenantId;

    //连续签到天数
    @TableField(value = "continuous_days")
    private Integer continuousDays;


}