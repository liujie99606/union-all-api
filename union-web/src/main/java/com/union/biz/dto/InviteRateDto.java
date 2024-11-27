package com.union.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InviteRateDto {
    //人数下限
    private Integer min;
    //人数上限
    private Integer max;
    //佣金比例
    private Double rate;
}
