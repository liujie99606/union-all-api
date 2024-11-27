package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author lj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_order")
public class UnionOrderDO extends BaseDO {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;


    @TableField(value = "order_code")
    private String orderCode;

    //用户id
    @TableField(value = "user_id")
    private Long userId;

    //所属平台
    @TableField(value = "platform_id")
    private String platformId;

    //实际付款金额
    @TableField(value = "payment_amount")
    private BigDecimal paymentAmount;

    //预估付款金额
    @TableField(value = "estimate_payment_amount")
    private BigDecimal estimatePaymentAmount;

    //邀请人id
    @TableField(value = "inviter_user_id")
    private Long inviterUserId;

    //实际联盟返利金额
    @TableField(value = "union_rebate_amount")
    private BigDecimal unionRebateAmount;

    //预估联盟返利金额
    @TableField(value = "estimate_rebate_amount")
    private BigDecimal estimateRebateAmount;

    //实际用户获得金额
    @TableField(value = "user_rebate_amount")
    private BigDecimal userRebateAmount;

    //预估用户获得金额
    @TableField(value = "estimate_user_rebate_amount")
    private BigDecimal estimateUserRebateAmount;

    //实际推荐获得金额
    @TableField(value = "partner_rebate_amount")
    private BigDecimal partnerRebateAmount;

    //实际平台营收金额
    @TableField(value = "platform_rebate_amount")
    private BigDecimal platformRebateAmount;

    //订单结算状态
    @TableField(value = "settle_status")
    private Integer settleStatus;

    //订单状态,1：未完成；2：已完成；5：无效订单
    @TableField(value = "order_status")
    private Integer orderStatus;


    @TableField(value = "tenant_id")
    private Long tenantId;

    //存原始订单json
    @TableField(value = "data")
    private String data;

    //商品id
    @TableField(value = "product_id")
    private String productId;

    //商品名称
    @TableField(value = "product_name")
    private String productName;


}