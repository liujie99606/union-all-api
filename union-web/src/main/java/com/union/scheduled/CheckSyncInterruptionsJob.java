package com.union.scheduled;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.union.biz.facade.OrderSyncFacade;
import com.union.biz.service.UnionOrderSyncLogService;
import com.union.constant.CommonConstants;
import com.union.enums.TextMessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class CheckSyncInterruptionsJob {

    @Resource
    private UnionOrderSyncLogService unionOrderSyncLogService;

    @Resource
    private OrderSyncFacade orderSyncFacade;

    @Scheduled(cron = "0 */30 * * * ?")
    @SchedulerLock(name = "union:job:checkSyncInterruptionsJob",
            lockAtMostForString = CommonConstants.FOURTEEN_SENCOND,
            lockAtLeastForString = CommonConstants.FOURTEEN_SENCOND)
    public void checkSyncInterruptionsJob() {
        log.info("检查同步记录是否存在中断 start");
        List<Map<String, LocalDateTime>> jdInterruptions = unionOrderSyncLogService.checkSyncInterruptions(TextMessageTypeEnum.JD.name());
        if (CollUtil.isNotEmpty(jdInterruptions)) {
            for (Map<String, LocalDateTime> jdInterruption : jdInterruptions) {
                orderSyncFacade.syncJdOrders(DateUtil.formatLocalDateTime(jdInterruption.get("startTime")),
                        DateUtil.formatLocalDateTime(jdInterruption.get("endTime")));
            }
        }

        List<Map<String, LocalDateTime>> tbInterruptions = unionOrderSyncLogService.checkSyncInterruptions(TextMessageTypeEnum.TAO_BAO.name());
        if (CollUtil.isNotEmpty(tbInterruptions)) {
            for (Map<String, LocalDateTime> tbInterruption : tbInterruptions) {
                orderSyncFacade.taobaoOrderSync(DateUtil.formatLocalDateTime(tbInterruption.get("startTime")),
                        DateUtil.formatLocalDateTime(tbInterruption.get("endTime")));
            }
        }

        List<Map<String, LocalDateTime>> pddInterruptions = unionOrderSyncLogService.checkSyncInterruptions(TextMessageTypeEnum.PDD.name());
        if (CollUtil.isNotEmpty(pddInterruptions)) {
            for (Map<String, LocalDateTime> pddInterruption : pddInterruptions) {
                orderSyncFacade.syncPddOrders(DateUtil.formatLocalDateTime(pddInterruption.get("startTime")),
                        DateUtil.formatLocalDateTime(pddInterruption.get("endTime")));
            }
        }
        log.info("检查同步记录是否存在中断 end");
    }
}
