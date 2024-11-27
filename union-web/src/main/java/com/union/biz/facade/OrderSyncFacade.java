package com.union.biz.facade;

import cn.hutool.core.date.DateUtil;
import com.union.base.exception.BaseException;
import com.union.biz.service.UnionOrderService;
import com.union.biz.service.UnionOrderSyncLogService;
import com.union.enums.TextMessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class OrderSyncFacade {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Resource
    private UnionOrderService unionOrderService;

    @Resource
    private UnionOrderSyncLogService unionOrderSyncLogService;

    /**
     * 同步京东订单数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void syncJdOrders(String startTime, String endTime) {
        syncOrders(startTime, endTime, TextMessageTypeEnum.JD, (t1, t2) -> {
            unionOrderService.jdOrderSync(t1, t2);
        });
    }

    /**
     * 同步淘宝订单数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void taobaoOrderSync(String startTime, String endTime) {
        syncOrders(startTime, endTime, TextMessageTypeEnum.TAO_BAO, (t1, t2) -> {
            unionOrderService.taobaoOrderSync(t1, t2);
        });
    }

    /**
     * 同步拼多多订单数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void syncPddOrders(String startTime, String endTime) {
        syncOrders(startTime, endTime, TextMessageTypeEnum.PDD, (t1, t2) -> {
            unionOrderService.pddOrderSync(DateUtil.parseLocalDateTime(t1, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.parseLocalDateTime(t2, "yyyy-MM-dd HH:mm:ss"));
        });
    }

    /**
     * 同步订单数据的通用方法
     */
    private void syncOrders(String startTime, String endTime, TextMessageTypeEnum type, OrderSyncExecutor executor) {
        LocalDateTime[] times = validateAndProcessTime(startTime, endTime);
        LocalDateTime start = times[0];
        LocalDateTime end = times[1];

        long diff = Duration.between(start, end).getSeconds();

        if (diff > 60) {
            syncByMinute(start, end, type, executor);
        } else {
            syncSinglePeriod(start, end, type, executor);
        }
    }

    /**
     * 校验时间参数并处理时间
     */
    private LocalDateTime[] validateAndProcessTime(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            throw new BaseException("开始时间和结束时间不能为空");
        }

        LocalDateTime start = DateUtil.parseLocalDateTime(startTime);
        LocalDateTime end = DateUtil.parseLocalDateTime(endTime);

        if (end.isAfter(LocalDateTime.now())) {
            throw new BaseException("结束时间不能超过当前时间");
        }

        start = start.withSecond(0);
        end = end.withSecond(59);

        return new LocalDateTime[]{start, end};
    }

    /**
     * 按分钟同步数据
     * 将时间段按每分钟拆分执行同步
     */
    private void syncByMinute(LocalDateTime start, LocalDateTime end, TextMessageTypeEnum type, OrderSyncExecutor executor) {
        LocalDateTime tempStart = start;
        while (tempStart.isBefore(end)) {
            LocalDateTime tempEnd = tempStart.withSecond(59);
            if (tempEnd.isAfter(end)) {
                tempEnd = end;
            }
            syncSinglePeriod(tempStart, tempEnd, type, executor);
            tempStart = tempStart.plusMinutes(1);
            //接口有频率限制，休息500毫秒
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("线程中断", e);
            }
        }
    }

    /**
     * 同步单个时间段的数据
     */
    private void syncSinglePeriod(LocalDateTime start, LocalDateTime end, TextMessageTypeEnum type, OrderSyncExecutor executor) {
        String startString = DateUtil.format(start, DATE_FORMAT);
        String endString = DateUtil.format(end, DATE_FORMAT);

        if (unionOrderSyncLogService.checkIfSynced(type.name(), start, end)) {
            log.info("执行{}订单同步 startTime:{},endTime:{}", type.name(), startString, endString);
            executor.execute(startString, endString);
            unionOrderSyncLogService.saveLog(type.name(), start, end);
        }
    }


    /**
     * 订单同步执行器函数式接口
     */
    @FunctionalInterface
    private interface OrderSyncExecutor {
        void execute(String startTime, String endTime);
    }
}
