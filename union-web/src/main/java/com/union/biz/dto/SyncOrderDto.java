package com.union.biz.dto;

import lombok.Data;

@Data
public class SyncOrderDto {

    //订单号
    private String orderNo;

    //订单来源人
    private Long userId;

    //实际付款金额
    private Double payPrice;

    //实际佣金金额
    private Double rebatePrice;

    //订单所有数据，存json
    private String data;

    //商品id
    private String productId;

    //订单状态,1：未完成；2：已完成；5：无效订单
    private Integer status;

    //预估付款金额
    private Double estimatePaymentAmount;

    //预估联盟返利金额
    private Double estimateRebateAmount;

    //商品名称
    private String productName;

    //订单平台
    private String platform;

}
