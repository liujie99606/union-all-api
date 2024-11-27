package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_account_log")
public class UnionAccountLogDO extends BaseDO {

    //uid
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    //账单类型
    @TableField(value = "log_type")
    private String logType;

    //账户ID
    @TableField(value = "account_id")
    private String accountId;

    //用户id
    @TableField(value = "user_id")
    private Long userId;

    //总金额
    @TableField(value = "total")
    private BigDecimal total;


    @TableField(value = "total_old")
    private BigDecimal totalOld;

    //操作金额
    @TableField(value = "amount")
    private BigDecimal amount;

    //收入金额
    @TableField(value = "income")
    private BigDecimal income;


    @TableField(value = "income_new")
    private BigDecimal incomeNew;


    @TableField(value = "income_old")
    private BigDecimal incomeOld;

    //支出金额
    @TableField(value = "expend")
    private BigDecimal expend;


    @TableField(value = "expend_new")
    private BigDecimal expendNew;


    @TableField(value = "expend_old")
    private BigDecimal expendOld;

    //账户余额
    @TableField(value = "balance")
    private BigDecimal balance;


    @TableField(value = "balance_new")
    private BigDecimal balanceNew;


    @TableField(value = "balance_old")
    private BigDecimal balanceOld;

    //冻结金额
    @TableField(value = "frost")
    private BigDecimal frost;


    @TableField(value = "frost_new")
    private BigDecimal frostNew;


    @TableField(value = "frost_old")
    private BigDecimal frostOld;

    //目标ID
    @TableField(value = "target_id")
    private Long targetId;

    //对应单据类型
    @TableField(value = "target_type")
    private String targetType;


    @TableField(value = "target_code")
    private String targetCode;

    //收支项目
    @TableField(value = "target_name")
    private String targetName;

    //摘要
    @TableField(value = "summary")
    private String summary;


    @TableField(value = "sequence")
    private Integer sequence;

    @TableField(value = "tenant_id")
    private Long tenantId;


}
