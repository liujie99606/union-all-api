package com.union.scheduled;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class UserInfoAuditScheduled {

    private static final String FOURTEEN_SENCOND = "PT50S";

    /**
     * 待审核信息通知
     * 2-7点不发
     */
    @Scheduled(cron = "0 0/30 0-1,8-23 * * ?")
    @SchedulerLock(name = "union:user:info:audit:lock",
            lockAtMostForString = FOURTEEN_SENCOND,
            lockAtLeastForString = FOURTEEN_SENCOND)
    public void userInfoAudit() {
        log.info("待审核信息通知 start");

        log.info("待审核信息通知 end");
    }




}
