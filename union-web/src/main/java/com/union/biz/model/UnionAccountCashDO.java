package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_account_cash")
public class UnionAccountCashDO extends BaseDO {

    //主键ID
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    //用户ID
    @TableField(value = "user_id")
    private Long userId;

    //姓名
    @TableField(value = "name")
    private String name;

    //交易流水号
    @TableField(value = "trade_no")
    private String tradeNo;

    //提现状态(0：待审核；1:提现完成；2：提现失败；-1：发送中；-2自动审核通过；4资金异常；6自动审核不通过；7发送第三方失败；11第三方提现失败,5 第三方平台余额不足)
    @TableField(value = "cash_status")
    private Integer cashStatus;

    //提现金额
    @TableField(value = "total")
    private BigDecimal total;

    //到账总额
    @TableField(value = "credited")
    private BigDecimal credited;

    //银行账号
    @TableField(value = "account")
    private String account;

    //银行账号类型  1 微信
    @TableField(value = "account_type")
    private Integer accountType;

    //审核操作员ID
    @TableField(value = "verify_user_id")
    private Long verifyUserId;

    //审核时间
    @TableField(value = "verify_time")
    private Date verifyTime;

    //审核备注
    @TableField(value = "verify_remark")
    private String verifyRemark;

    @TableField(value = "payment_method")
    private String paymentMethod;

    @TableField(value = "version")
    private Integer version;

    @TableField(value = "tenant_id")
    private Long tenantId;


}
