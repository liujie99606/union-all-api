package com.union.biz.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.union.base.exception.BaseException;
import com.union.biz.manager.AccountManager;
import com.union.biz.mapper.UnionUserDailySignMapper;
import com.union.biz.model.UnionUserDailySignDO;
import com.union.enums.AccountTargetTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户每日签到服务
 *
 * @author lj
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnionUserDailySignService extends ServiceImpl<UnionUserDailySignMapper, UnionUserDailySignDO> {

    private final UnionUserDailySignMapper unionUserDailySignMapper;
    private final AccountManager accountManager;

    /**
     * 用户签到
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public UnionUserDailySignDO signIn(Long userId) {
        LocalDate today = LocalDate.now();
        UnionUserDailySignDO existingSign = unionUserDailySignMapper.selectByUserIdAndSignDate(userId, today);
        if (existingSign != null) {
            throw new BaseException( "今日已签到，无需重复签到~");
        }

        // 获取昨天的签到记录
        LocalDate yesterday = today.minusDays(1);
        UnionUserDailySignDO yesterdaySign = unionUserDailySignMapper.selectByUserIdAndSignDate(userId, yesterday);

        // 计算连续签到天数
        int continuousSignDays = 0;
        if (yesterdaySign != null) {
            continuousSignDays = yesterdaySign.getContinuousDays();
        }
        continuousSignDays++; // 加上今天的签到

        // 生成随机奖励金额
        BigDecimal amount;
        if (continuousSignDays >= 7) { // 连续7天及以上签到
            // 奖励金额 0.03-0.05
            amount = RandomUtil.randomBigDecimal(new BigDecimal("0.03"), new BigDecimal("0.06"))
                    .setScale(2, RoundingMode.DOWN);
        } else {
            // 普通签到奖励 0.01-0.03
            amount = RandomUtil.randomBigDecimal(new BigDecimal("0.01"), new BigDecimal("0.04"))
                    .setScale(2, RoundingMode.DOWN);
        }

        // 创建签到记录
        UnionUserDailySignDO signDO = new UnionUserDailySignDO();
        signDO.setUserId(userId);
        signDO.setSignDate(today);
        signDO.setAmount(amount);
        signDO.setContinuousDays(continuousSignDays);
        signDO.setCreator(userId + "");
        signDO.setCreateTime(LocalDateTime.now());

        int i = unionUserDailySignMapper.insert(signDO);
        if (i < 1) {
            throw new BaseException( "签到失败");
        }
        accountManager.income(userId, amount, signDO.getId(), AccountTargetTypeEnum.SIGN_IN, "签到奖励");
        return signDO;
    }

}