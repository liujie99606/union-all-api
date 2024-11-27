package com.union.biz.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.union.base.exception.BaseException;
import com.union.biz.mapper.UnionOrderSyncLogMapper;
import com.union.biz.model.UnionOrderSyncLogDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lj
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnionOrderSyncLogService extends ServiceImpl<UnionOrderSyncLogMapper, UnionOrderSyncLogDO> {

    private final UnionOrderSyncLogMapper unionOrderSyncLogMapper;

    /**
     * 保存同步日志
     *
     * @param type      同步类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(String type, LocalDateTime startTime, LocalDateTime endTime) {
        // 校验时间间隔是否为1分钟
        if (Duration.between(startTime, endTime).getSeconds() != 59) {
            throw new BaseException( "时间间隔必须为1分钟");
        }
        UnionOrderSyncLogDO logDO = new UnionOrderSyncLogDO();
        logDO.setOrderType(type);
        logDO.setStartTime(startTime);
        logDO.setEndTime(endTime);
        logDO.setCreator("system");

        // 查询最后一次同步记录
        UnionOrderSyncLogDO lastLog = unionOrderSyncLogMapper.selectOne(
                Wrappers.lambdaQuery(UnionOrderSyncLogDO.class)
                        .eq(UnionOrderSyncLogDO::getOrderType, type)
                        .le(UnionOrderSyncLogDO::getEndTime, startTime)
                        .orderByDesc(UnionOrderSyncLogDO::getStartTime)
                        .last("limit 1")
        );
        if (lastLog != null) {
            // 上次结束时间加1秒
            LocalDateTime lastEndTimePlusOneSecond = lastLog.getEndTime().plusSeconds(1);
            // 判断是否中断
            logDO.setIsInterrupted(lastEndTimePlusOneSecond.isBefore(startTime));
        } else {
            // 第一次同步,设置为未中断
            logDO.setIsInterrupted(false);
        }

        int i = unionOrderSyncLogMapper.insert(logDO);
        if (i < 1) {
            throw new BaseException( "保存失败");
        }
    }

    /**
     * 检查指定类型的同步记录是否存在中断
     *
     * @param type 同步类型
     */
    public List<Map<String, LocalDateTime>> checkSyncInterruptions(String type) {
        // 查询指定类型的所有同步记录，按开始时间升序排序
        List<UnionOrderSyncLogDO> logs = unionOrderSyncLogMapper.selectList(
                Wrappers.lambdaQuery(UnionOrderSyncLogDO.class)
                        .eq(UnionOrderSyncLogDO::getOrderType, type)
                        .eq(UnionOrderSyncLogDO::getDeleted, false)
                        .orderByAsc(UnionOrderSyncLogDO::getStartTime)
        );

        if (logs.size() < 2) {
            return null;
        }
        //中断时间集合
        List<Map<String, LocalDateTime>> interruptions = CollUtil.newArrayList();

        checkAndUpdateInterruptions(logs, interruptions, type);
        if (CollUtil.isNotEmpty(interruptions)) {
            log.info("[{}] 共发现 {} 个中断时间区间:", type, interruptions.size());
            for (Map<String, LocalDateTime> interruption : interruptions) {
                log.info("中断区间: {} - {}", interruption.get("startTime"), interruption.get("endTime"));
            }
            return interruptions;
        } else {
            if (logs.size() > 300) {
                // 获取300条之前的时间
                LocalDateTime thresholdTime = logs.get(logs.size() - 300).getStartTime();
                // 删除时间小于thresholdTime的记录
                unionOrderSyncLogMapper.delete(
                        Wrappers.lambdaQuery(UnionOrderSyncLogDO.class)
                                .eq(UnionOrderSyncLogDO::getOrderType, type)
                                .eq(UnionOrderSyncLogDO::getDeleted, false)
                                .lt(UnionOrderSyncLogDO::getStartTime, thresholdTime)
                );
            }
        }
        return null;
    }

    /**
     * 检查并更新中断状态
     */
    private void checkAndUpdateInterruptions(List<UnionOrderSyncLogDO> logs,
                                             List<Map<String, LocalDateTime>> interruptions,
                                             String type) {
        for (int i = 0; i < logs.size() - 1; i++) {
            UnionOrderSyncLogDO currentLog = logs.get(i);
            UnionOrderSyncLogDO nextLog = logs.get(i + 1);

            // 当前记录的结束时间加1秒
            LocalDateTime expectedNextStart = currentLog.getEndTime().plusSeconds(1);

            // 如果下一条记录的开始时间晚于期望的开始时间，说明存在中断
            if (nextLog.getStartTime().isAfter(expectedNextStart)) {
                handleInterruption(nextLog, expectedNextStart, interruptions, type);
            } else {
                updateNonInterruptedStatus(nextLog);
            }
        }
    }

    /**
     * 处理中断情况
     */
    private void handleInterruption(UnionOrderSyncLogDO nextLog, LocalDateTime expectedNextStart,
                                    List<Map<String, LocalDateTime>> interruptions, String type) {
        log.info("[{}] 同步存在中断,中断开始时间:{},中断结束时间:{}",
                type, expectedNextStart, nextLog.getStartTime());

        // 记录中断时间区间
        Map<String, LocalDateTime> interruption = new HashMap<>();
        interruption.put("startTime", expectedNextStart);
        interruption.put("endTime", nextLog.getStartTime().plusSeconds(-1));
        interruptions.add(interruption);

        // 只有当原来不是中断状态时才更新数据库
        if (!BooleanUtil.isTrue(nextLog.getIsInterrupted())) {
            nextLog.setIsInterrupted(true);
            UnionOrderSyncLogDO updateLog = new UnionOrderSyncLogDO();
            updateLog.setId(nextLog.getId());
            updateLog.setIsInterrupted(nextLog.getIsInterrupted());
            unionOrderSyncLogMapper.updateById(updateLog);
        }
    }

    /**
     * 更新非中断状态
     */
    private void updateNonInterruptedStatus(UnionOrderSyncLogDO nextLog) {
        // 如果原来是中断状态,现在不是中断,需要更新状态
        if (BooleanUtil.isTrue(nextLog.getIsInterrupted())) {
            UnionOrderSyncLogDO updateLog = new UnionOrderSyncLogDO();
            updateLog.setId(nextLog.getId());
            updateLog.setIsInterrupted(false);
            unionOrderSyncLogMapper.updateById(updateLog);
        }
    }

    /**
     * 检查指定时间范围是否已同步
     *
     * @param type      同步类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public boolean checkIfSynced(String type, LocalDateTime startTime, LocalDateTime endTime) {
        // 校验时间间隔是否为1分钟
        if (Duration.between(startTime, endTime).getSeconds() != 59) {
            throw new BaseException( "时间间隔必须为1分钟");
        }

        List<UnionOrderSyncLogDO> logs = unionOrderSyncLogMapper.selectList(
                Wrappers.lambdaQuery(UnionOrderSyncLogDO.class)
                        .eq(UnionOrderSyncLogDO::getOrderType, type)
                        .eq(UnionOrderSyncLogDO::getStartTime, startTime)
                        .eq(UnionOrderSyncLogDO::getEndTime, endTime)
                        .orderByAsc(UnionOrderSyncLogDO::getStartTime)
        );

        return CollUtil.isEmpty(logs);
    }
}