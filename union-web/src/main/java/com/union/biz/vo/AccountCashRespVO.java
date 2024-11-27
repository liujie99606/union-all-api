package com.union.biz.vo;

import com.union.enums.AccountCashStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Schema(description = "管理后台 - 收藏 Response VO")
@Data
public class AccountCashRespVO {


    //姓名
    private String name;

    //交易流水号
    private String tradeNo;

    //提现状态(0：待审核；1:提现完成；2：提现失败；-1：发送中；-2自动审核通过；4资金异常；6自动审核不通过；7发送第三方失败；11第三方提现失败,5 第三方平台余额不足)
    private AccountCashStatusEnum cashStatus;

    //提现金额
    private BigDecimal total;

    //到账总额
    private BigDecimal credited;

    //银行账号
    private String account;

    //银行账号类型  1 微信
    private Integer accountType;

    //审核操作员ID
    private Long verifyUserId;

    //审核时间
    private Date verifyTime;

    //审核备注
    private String verifyRemark;

    private String paymentMethod;




}
