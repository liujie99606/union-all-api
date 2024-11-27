package com.union.biz.service.message;

import com.union.base.exception.BaseException;
import com.union.biz.service.UnionOrderService;
import com.union.enums.TextMessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OrderQueryMessageStrategy implements MessageStrategy {

    @Resource
    private UnionOrderService unionOrderService;


    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.ORDER_QUERY;
    }

    @Override
    public Object execute(String text, String userId) {
        try {
            unionOrderService.orderQuery(text, userId);
        } catch (BaseException e) {
            log.warn("订单查询失败,{}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            log.error("订单查询失败", e);
            return "订单查询失败";
        }
        return "绑定成功";
    }
}