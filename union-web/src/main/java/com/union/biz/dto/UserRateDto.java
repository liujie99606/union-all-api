package com.union.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRateDto {
    //金额下限
    private Double minAmount;
    //金额上限
    private Double maxAmount;
    //佣金比例
    private Double rate;
}
