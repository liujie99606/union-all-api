package com.union.biz.dto;

import lombok.Data;

@Data
public class RebateGoodsDto {

    //下单url
    private String clickURL;

    //原价
    private Double originalPrice;

    //补贴金额
    private Double rebatePrice;

    //总拥金
    private Double totalRebatePrice;

    //商品名称
    private String goodsName;

    //商品id
    private String goodsId;
}
