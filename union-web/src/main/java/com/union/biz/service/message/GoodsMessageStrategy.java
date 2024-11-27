package com.union.biz.service.message;

import com.union.biz.dto.RebateGoodsDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GoodsMessageStrategy implements MessageStrategy {


    public abstract RebateGoodsDto getGoodsInfo(String request, String userId);



}