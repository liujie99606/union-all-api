package com.union.scheduled;

import com.union.biz.service.UnionOrderService;
import com.union.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单结算定时
 */
@Slf4j
@Component
class UnionOrderSettleJob {

    @Resource
    private UnionOrderService unionOrderService;

    @Scheduled(cron = "0 */2 * * * ?")
    @SchedulerLock(name = "union:job:unionOrderSettle",
            lockAtMostForString = CommonConstants.FOURTEEN_SENCOND,
            lockAtLeastForString = CommonConstants.FOURTEEN_SENCOND)
    public void unionOrderSettle() {
        log.info("执行订单结算定时 start");
        unionOrderService.unionOrderSettle();
        log.info("执行订单结算定时 end");
    }
}