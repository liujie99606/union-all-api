package com.union.scheduled;

import cn.hutool.core.date.DateUtil;
import com.union.biz.service.UnionOrderService;
import com.union.biz.service.UnionOrderSyncLogService;
import com.union.constant.CommonConstants;
import com.union.enums.TextMessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JdOrderSyncJob {

    @Resource
    private UnionOrderService unionOrderService;

    @Resource
    private UnionOrderSyncLogService unionOrderSyncLogService;

    @Scheduled(cron = "0 */1 * * * ?")
    @SchedulerLock(name = "union:job:jdOrderSync",
            lockAtMostForString = CommonConstants.FOURTEEN_SENCOND,
            lockAtLeastForString = CommonConstants.FOURTEEN_SENCOND)
    public void jdOrderSync() {
        log.info("执行京东订单同步 start");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfLastMinute = now.minusMinutes(1).withSecond(0).withNano(0);
        LocalDateTime endOfLastMinute = startOfLastMinute.withSecond(59);

        String startTime = DateUtil.format(startOfLastMinute, "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.format(endOfLastMinute, "yyyy-MM-dd HH:mm:ss");
        unionOrderService.jdOrderSync(startTime, endTime);
        unionOrderSyncLogService.saveLog(TextMessageTypeEnum.JD.name(), startOfLastMinute, endOfLastMinute);
        log.info("执行京东订单同步 end");
    }
}
